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

import com.watabou.glwrap.Matrix
import com.watabou.utils.Point
import com.watabou.utils.PointF
import com.watabou.utils.Random

import java.util.ArrayList

open class Camera(var x: Int, var y: Int, var width: Int, var height: Int, var zoom: Float) : Gizmo() {

    var fullScreen: Boolean = false

    internal var screenWidth: Int = 0
    internal var screenHeight: Int = 0

    var matrix: FloatArray

    var scroll: PointF
    var target: Visual? = null

    private var shakeMagX = 10f
    private var shakeMagY = 10f
    private var shakeTime = 0f
    private var shakeDuration = 1f

    protected var shakeX: Float = 0.toFloat()
    protected var shakeY: Float = 0.toFloat()

    init {

        screenWidth = (width * zoom).toInt()
        screenHeight = (height * zoom).toInt()

        scroll = PointF()

        matrix = FloatArray(16)
        Matrix.setIdentity(matrix)
    }

    override fun destroy() {
        target = null
    }

    @JvmOverloads
    fun zoom(value: Float, fx: Float = scroll.x + width / 2, fy: Float = scroll.y + height / 2) {

        zoom = value
        width = (screenWidth / zoom).toInt()
        height = (screenHeight / zoom).toInt()

        focusOn(fx, fy)
    }

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        screenWidth = (width * zoom).toInt()
        screenHeight = (height * zoom).toInt()
    }

    override fun update() {
        super.update()

        if (target != null) {
            focusOn(target!!.x + target!!.width / 2, target!!.y + target!!.height / 2)
        }

        if ((shakeTime -= Game.elapsed) > 0) {
            val damping = shakeTime / shakeDuration
            shakeX = Random.Float(-shakeMagX, +shakeMagX) * damping
            shakeY = Random.Float(-shakeMagY, +shakeMagY) * damping
        } else {
            shakeX = 0f
            shakeY = 0f
        }

        updateMatrix()
    }

    fun center(): PointF {
        return PointF((width / 2).toFloat(), (height / 2).toFloat())
    }

    fun hitTest(x: Float, y: Float): Boolean {
        return x >= this.x && y >= this.y && x < this.x + screenWidth && y < this.y + screenHeight
    }

    fun focusOn(x: Float, y: Float) {
        scroll.set(x - width / 2, y - height / 2)
    }

    fun focusOn(point: PointF) {
        focusOn(point.x, point.y)
    }

    fun focusOn(visual: Visual) {
        focusOn(visual.center())
    }

    fun screenToCamera(x: Int, y: Int): PointF {
        return PointF(
                (x - this.x) / zoom + scroll.x,
                (y - this.y) / zoom + scroll.y)
    }

    fun cameraToScreen(x: Float, y: Float): Point {
        return Point(
                ((x - scroll.x) * zoom + this.x).toInt(),
                ((y - scroll.y) * zoom + this.y).toInt())
    }

    fun screenWidth(): Float {
        return width * zoom
    }

    fun screenHeight(): Float {
        return height * zoom
    }

    protected open fun updateMatrix() {

        /*	Matrix.setIdentity( matrix );
		Matrix.translate( matrix, -1, +1 );
		Matrix.scale( matrix, 2f / G.width, -2f / G.height );
		Matrix.translate( matrix, x, y );
		Matrix.scale( matrix, zoom, zoom );
		Matrix.translate( matrix, scroll.x, scroll.y );*/

        matrix[0] = +zoom * invW2
        matrix[5] = -zoom * invH2

        matrix[12] = -1 + x * invW2 - (scroll.x + shakeX) * matrix[0]
        matrix[13] = +1f - y * invH2 - (scroll.y + shakeY) * matrix[5]

    }

    fun shake(magnitude: Float, duration: Float) {
        shakeMagY = magnitude
        shakeMagX = shakeMagY
        shakeDuration = duration
        shakeTime = shakeDuration
    }

    companion object {

        private val all = ArrayList<Camera>()

        protected var invW2: Float = 0.toFloat()
        protected var invH2: Float = 0.toFloat()

        var main: Camera

        fun reset(): Camera {
            return reset(createFullscreen(1f))
        }

        @Synchronized
        fun reset(newCamera: Camera): Camera {

            invW2 = 2f / Game.width
            invH2 = 2f / Game.height

            val length = all.size
            for (i in 0 until length) {
                all[i].destroy()
            }
            all.clear()

            return main = add(newCamera)
        }

        @Synchronized
        fun add(camera: Camera): Camera {
            all.add(camera)
            return camera
        }

        @Synchronized
        fun remove(camera: Camera): Camera {
            all.remove(camera)
            return camera
        }

        @Synchronized
        fun updateAll() {
            val length = all.size
            for (i in 0 until length) {
                val c = all[i]
                if (c.exists && c.active) {
                    c.update()
                }
            }
        }

        fun createFullscreen(zoom: Float): Camera {
            val w = Math.ceil((Game.width / zoom).toDouble()).toInt()
            val h = Math.ceil((Game.height / zoom).toDouble()).toInt()
            val c = Camera(
                    (Game.width - w * zoom).toInt() / 2,
                    (Game.height - h * zoom).toInt() / 2,
                    w, h, zoom)
            c.fullScreen = true
            return c
        }
    }
}
