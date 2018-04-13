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

package com.shatteredpixel.shatteredpixeldungeon.scenes

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.watabou.glwrap.Blending
import com.watabou.input.Touchscreen
import com.watabou.noosa.BitmapText
import com.watabou.noosa.BitmapText.Font
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Camera
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText
import com.watabou.noosa.Scene
import com.watabou.noosa.Visual
import com.watabou.noosa.ui.Component
import com.watabou.utils.BitmapCache

open class PixelScene : Scene() {

    override fun create() {

        super.create()

        GameScene.scene = null

        val minWidth: Float
        val minHeight: Float
        if (SPDSettings.landscape()) {
            minWidth = MIN_WIDTH_L
            minHeight = MIN_HEIGHT_L
        } else {
            minWidth = MIN_WIDTH_P
            minHeight = MIN_HEIGHT_P
        }

        maxDefaultZoom = Math.min(Game.width / minWidth, Game.height / minHeight).toInt()
        maxScreenZoom = Math.min(Game.dispWidth / minWidth, Game.dispHeight / minHeight).toInt()
        defaultZoom = SPDSettings.scale()

        if (defaultZoom < Math.ceil((Game.density * 2).toDouble()) || defaultZoom > maxDefaultZoom) {
            defaultZoom = Math.ceil(Game.density * 2.5).toInt()
            while ((Game.width / defaultZoom < minWidth || Game.height / defaultZoom < minHeight) && defaultZoom > 1) {
                defaultZoom--
            }
        }

        minZoom = 1f
        maxZoom = (defaultZoom * 2).toFloat()

        Camera.reset(PixelCamera(defaultZoom.toFloat()))

        val uiZoom = defaultZoom.toFloat()
        uiCamera = Camera.createFullscreen(uiZoom)
        Camera.add(uiCamera)

        if (pixelFont == null) {

            // 3x5 (6)
            pixelFont = Font.colorMarked(
                    BitmapCache.get(Assets.PIXELFONT), 0x00000000, BitmapText.Font.LATIN_FULL)
            pixelFont!!.baseLine = 6f
            pixelFont!!.tracking = -1f

            //Fonts disabled to save memory (~1mb of texture data just sitting there unused)
            //uncomment if you wish to enable these again.

            // 9x15 (18)
            /*font1x = Font.colorMarked(
					BitmapCache.get( Assets.FONT1X), 22, 0x00000000, BitmapText.Font.LATIN_FULL );
			font1x.baseLine = 17;
			font1x.tracking = -2;
			font1x.texture.filter(Texture.LINEAR, Texture.LINEAR);

			//font1x double scaled
			font2x = Font.colorMarked(
					BitmapCache.get( Assets.FONT2X), 44, 0x00000000, BitmapText.Font.LATIN_FULL );
			font2x.baseLine = 38;
			font2x.tracking = -4;
			font2x.texture.filter(Texture.LINEAR, Texture.NEAREST);*/
        }
    }

    override fun destroy() {
        super.destroy()
        Touchscreen.event.removeAll()
    }

    protected fun fadeIn() {
        if (noFade) {
            noFade = false
        } else {
            fadeIn(-0x1000000, false)
        }
    }

    protected fun fadeIn(color: Int, light: Boolean) {
        add(Fader(color, light))
    }

    protected class Fader(color: Int, private val light: Boolean) : ColorBlock(uiCamera.width, uiCamera.height, color) {

        private var time: Float = 0.toFloat()

        init {

            camera = uiCamera

            alpha(1f)
            time = FADE_TIME
        }

        override fun update() {

            super.update()

            if ((time -= Game.elapsed) <= 0) {
                alpha(0f)
                parent!!.remove(this)
            } else {
                alpha(time / FADE_TIME)
            }
        }

        override fun draw() {
            if (light) {
                Blending.setLightMode()
                super.draw()
                Blending.setNormalMode()
            } else {
                super.draw()
            }
        }

        companion object {

            private val FADE_TIME = 1f
        }
    }

    private class PixelCamera(zoom: Float) : Camera((Game.width - Math.ceil((Game.width / zoom).toDouble()) * zoom).toInt() / 2, (Game.height - Math.ceil((Game.height / zoom).toDouble()) * zoom).toInt() / 2, Math.ceil((Game.width / zoom).toDouble()).toInt(), Math.ceil((Game.height / zoom).toDouble()).toInt(), zoom) {

        init {
            fullScreen = true
        }

        override fun updateMatrix() {
            val sx = align(this, scroll.x + shakeX)
            val sy = align(this, scroll.y + shakeY)

            matrix[0] = +zoom * Camera.invW2
            matrix[5] = -zoom * Camera.invH2

            matrix[12] = -1 + x * Camera.invW2 - sx * matrix[0]
            matrix[13] = +1f - y * Camera.invH2 - sy * matrix[5]

        }
    }

    companion object {

        // Minimum virtual display size for portrait orientation
        val MIN_WIDTH_P = 135f
        val MIN_HEIGHT_P = 225f

        // Minimum virtual display size for landscape orientation
        val MIN_WIDTH_L = 240f
        val MIN_HEIGHT_L = 160f

        var defaultZoom = 0
        var maxDefaultZoom = 0
        var maxScreenZoom = 0
        var minZoom: Float = 0.toFloat()
        var maxZoom: Float = 0.toFloat()

        var uiCamera: Camera

        //stylized pixel font
        var pixelFont: BitmapText.Font? = null
        //These represent various mipmaps of the same font
        var font1x: BitmapText.Font? = null
        var font2x: BitmapText.Font? = null

        var font: BitmapText.Font? = null
        var scale: Float = 0.toFloat()

        @JvmOverloads
        fun chooseFont(size: Float, zoom: Float = defaultZoom.toFloat()) {

            val pt = size * zoom

            if (pt >= 25) {

                font = font2x
                scale = pt / 38f

            } else if (pt >= 12) {

                font = font1x
                scale = pt / 19f

            } else {
                font = pixelFont
                scale = 1f
            }

            scale /= zoom
        }

        fun createText(size: Float): BitmapText {
            return createText(null, size)
        }

        fun createText(text: String?, size: Float): BitmapText {

            chooseFont(size)

            val result = BitmapText(text, font)
            result.scale.set(scale)

            return result
        }

        fun createMultiline(size: Float): BitmapTextMultiline {
            return createMultiline(null, size)
        }

        fun createMultiline(text: String?, size: Float): BitmapTextMultiline {

            chooseFont(size)

            val result = BitmapTextMultiline(text, font)
            result.scale.set(scale)

            return result
        }

        fun renderText(size: Int): RenderedText {
            return renderText("", size)
        }

        fun renderText(text: String, size: Int): RenderedText {
            val result = RenderedText(text, size * defaultZoom)
            result.scale.set(1 / defaultZoom.toFloat())
            return result
        }

        fun renderMultiline(size: Int): RenderedTextMultiline {
            return renderMultiline("", size)
        }

        fun renderMultiline(text: String, size: Int): RenderedTextMultiline {
            val result = RenderedTextMultiline(text, size * defaultZoom)
            result.zoom(1 / defaultZoom.toFloat())
            return result
        }

        /**
         * These methods align UI elements to device pixels.
         * e.g. if we have a scale of 3x then valid positions are #.0, #.33, #.67
         */

        fun align(pos: Float): Float {
            return Math.round(pos * defaultZoom) / defaultZoom.toFloat()
        }

        fun align(camera: Camera, pos: Float): Float {
            return Math.round(pos * camera.zoom) / camera.zoom
        }

        fun align(v: Visual) {
            v.x = align(v.x)
            v.y = align(v.y)
        }

        fun align(c: Component) {
            c.setPos(align(c.left()), align(c.top()))
        }

        var noFade = false

        fun showBadge(badge: Badges.Badge) {
            val banner = BadgeBanner.show(badge.image)
            banner.camera = uiCamera
            banner.x = align(banner.camera!!, (banner.camera!!.width - banner.width) / 2)
            banner.y = align(banner.camera!!, (banner.camera!!.height - banner.height) / 3)
            Game.scene()!!.add(banner)
        }
    }
}
