/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.media.AudioManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View

import com.watabou.glscripts.Script
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Blending
import com.watabou.glwrap.ScreenConfigChooser
import com.watabou.glwrap.Vertexbuffer
import com.watabou.input.Keys
import com.watabou.input.Touchscreen
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.utils.BitmapCache
import com.watabou.utils.DeviceCompat
import com.watabou.utils.SystemTime

import java.util.ArrayList

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class Game(// New scene class
        protected var sceneClass: Class<out Scene>) : Activity(), GLSurfaceView.Renderer, View.OnTouchListener {

    // Current scene
    protected var scene: Scene? = null
    // New scene we are going to switch to
    protected var requestedScene: Scene? = null
    // true if scene switch is requested
    protected var requestedReset = true
    // callback to perform logic during scene change
    protected var onChange: SceneChangeCallback? = null

    // Current time in milliseconds
    protected var now: Long = 0
    // Milliseconds passed since previous update
    protected var step: Long = 0

    protected var view: GLSurfaceView? = null
    protected var holder: SurfaceHolder? = null

    // Accumulated touch events
    protected var motionEvents = ArrayList<MotionEvent>()

    // Accumulated key events
    protected var keysEvents = ArrayList<KeyEvent>()

    var isPaused: Boolean = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance = this
        TextureCache.context = instance
        BitmapCache.context = TextureCache.context

        val m = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            windowManager.defaultDisplay.getRealMetrics(m)
        else
            windowManager.defaultDisplay.getMetrics(m)
        density = m.density
        dispHeight = m.heightPixels
        dispWidth = m.widthPixels

        try {
            version = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: NameNotFoundException) {
            version = "???"
        }

        try {
            versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: NameNotFoundException) {
            versionCode = 0
        }

        volumeControlStream = AudioManager.STREAM_MUSIC

        view = GLSurfaceView(this)
        view!!.setEGLContextClientVersion(2)

        //Older devices are forced to RGB 565 for performance reasons.
        //Otherwise try to use RGB888 for best quality, but use RGB565 if it is what's available.
        view!!.setEGLConfigChooser(ScreenConfigChooser(
                DeviceCompat.legacyDevice(),
                false))

        view!!.setRenderer(this)
        view!!.setOnTouchListener(this)
        setContentView(view)

        //so first call to onstart/onresume calls correct logic.
        isPaused = true
    }

    //Starting with honeycomb, android's lifecycle management changes slightly

    public override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            resumeGame()
        }
    }

    override fun onResume() {
        super.onResume()

        if (scene != null) {
            scene!!.onResume()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            resumeGame()
        }
    }

    override fun onPause() {
        super.onPause()

        if (scene != null) {
            scene!!.onPause()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            pauseGame()
        }
    }

    public override fun onStop() {
        super.onStop()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            pauseGame()
        }
    }

    fun pauseGame() {
        if (isPaused) return

        isPaused = true
        view!!.onPause()
        Script.reset()

        Music.INSTANCE.pause()
        Sample.INSTANCE.pause()
    }

    fun resumeGame() {
        if (!isPaused) return

        now = 0
        isPaused = false
        view!!.onResume()

        Music.INSTANCE.resume()
        Sample.INSTANCE.resume()
    }

    public override fun onDestroy() {
        super.onDestroy()
        destroyGame()

        Music.INSTANCE.mute()
        Sample.INSTANCE.reset()
    }

    @SuppressLint("Recycle", "ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        synchronized(motionEvents) {
            motionEvents.add(MotionEvent.obtain(event))
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode != Keys.BACK && keyCode != Keys.MENU) {
            return false
        }

        synchronized(motionEvents) {
            keysEvents.add(event)
        }
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode != Keys.BACK && keyCode != Keys.MENU) {
            return false
        }

        synchronized(motionEvents) {
            keysEvents.add(event)
        }
        return true
    }

    override fun onDrawFrame(gl: GL10) {

        if (width == 0 || height == 0) {
            return
        }

        NoosaScript.get().resetCamera()
        NoosaScriptNoLighting.get().resetCamera()
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        draw()

        GLES20.glFlush()

        SystemTime.tick()
        val rightNow = SystemClock.elapsedRealtime()
        step = if (now == 0L) 0 else rightNow - now
        now = rightNow

        step()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

        GLES20.glViewport(0, 0, width, height)

        if (height != Game.height || width != Game.width) {

            Game.width = width
            Game.height = height

            resetScene()
        }
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Blending.useDefault()

        //refreshes texture and vertex data stored on the gpu
        TextureCache.reload()
        RenderedText.reloadCache()
        Vertexbuffer.refreshAllBuffers()
    }

    protected fun destroyGame() {
        if (scene != null) {
            scene!!.destroy()
            scene = null
        }

        //instance = null;
    }

    protected fun step() {

        if (requestedReset) {
            requestedReset = false

            try {
                requestedScene = sceneClass.newInstance()
                switchScene()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }

        update()
    }

    protected fun draw() {
        if (scene != null) scene!!.draw()
    }

    protected fun switchScene() {

        Camera.reset()

        if (scene != null) {
            scene!!.destroy()
        }
        scene = requestedScene
        if (onChange != null) onChange!!.beforeCreate()
        scene!!.create()
        if (onChange != null) onChange!!.afterCreate()
        onChange = null

        Game.elapsed = 0f
        Game.timeScale = 1f
        Game.timeTotal = 0f
    }

    protected fun update() {
        Game.elapsed = Game.timeScale * step.toFloat() * 0.001f
        Game.timeTotal += Game.elapsed

        synchronized(motionEvents) {
            Touchscreen.processTouchEvents(motionEvents)
            motionEvents.clear()
        }
        synchronized(keysEvents) {
            Keys.processTouchEvents(keysEvents)
            keysEvents.clear()
        }

        scene!!.update()
        Camera.updateAll()
    }

    protected fun logException(tr: Throwable) {
        Log.e("GAME", Log.getStackTraceString(tr))
    }

    interface SceneChangeCallback {
        fun beforeCreate()
        fun afterCreate()
    }

    companion object {

        var instance: Game? = null

        //actual size of the display
        var dispWidth: Int = 0
        var dispHeight: Int = 0

        // Size of the EGL surface view
        var width: Int = 0
        var height: Int = 0

        // Density: mdpi=1, hdpi=1.5, xhdpi=2...
        var density = 1f

        var version: String? = null
        var versionCode: Int = 0

        var timeScale = 1f
        var elapsed = 0f
        var timeTotal = 0f

        fun resetScene() {
            switchScene(instance!!.sceneClass)
        }

        @JvmOverloads
        fun switchScene(c: Class<out Scene>, callback: SceneChangeCallback? = null) {
            instance!!.sceneClass = c
            instance!!.requestedReset = true
            instance!!.onChange = callback
        }

        fun scene(): Scene? {
            return instance!!.scene
        }

        fun reportException(tr: Throwable) {
            if (instance != null) instance!!.logException(tr)
        }

        fun vibrate(milliseconds: Int) {
            (instance!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(milliseconds.toLong())
        }
    }
}
