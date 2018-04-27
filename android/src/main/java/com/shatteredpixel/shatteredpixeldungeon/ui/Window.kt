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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.effects.ShadowBox
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.input.Keys
import com.watabou.input.Keys.Key
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.NinePatch
import com.watabou.noosa.TouchArea
import com.watabou.utils.Signal

open class Window
    @JvmOverloads constructor(
            protected var width: Int = 0,
            protected var height: Int = 0,
            protected var yOffset: Int = 0,
            protected var chrome: NinePatch = Chrome.get(Chrome.Type.WINDOW)!!
    ) : Group(), Signal.Listener<Key> {

    protected var blocker: TouchArea
    protected var shadow: ShadowBox

    constructor(width: Int, height: Int, chrome: NinePatch) : this(width, height, 0, chrome) {}

    init {

        blocker = object : TouchArea(0f, 0f, PixelScene.uiCamera!!.width.toFloat(), PixelScene.uiCamera!!.height.toFloat()) {
            override fun onClick(touch: Touch) {
                if (this@Window.parent != null && !this@Window.chrome.overlapsScreenPoint(
                                touch.current.x.toInt(),
                                touch.current.y.toInt())) {

                    onBackPressed()
                }
            }
        }
        blocker.camera = PixelScene.uiCamera!!
        add(blocker)

        shadow = ShadowBox()
        shadow.am = 0.5f
        shadow.camera = if (PixelScene.uiCamera!!.visible)
            PixelScene.uiCamera!!
        else
            Camera.main!!
        add(shadow)

        chrome.x = (-chrome.marginLeft()).toFloat()
        chrome.y = (-chrome.marginTop()).toFloat()
        chrome.size(
                width - chrome.x + chrome.marginRight(),
                height - chrome.y + chrome.marginBottom())
        add(chrome)

        camera = Camera(0, 0,
                chrome.width.toInt(),
                chrome.height.toInt(),
                PixelScene.defaultZoom.toFloat())
        camera!!.x = (Game.width - camera!!.width * camera!!.zoom).toInt() / 2
        camera!!.y = (Game.height - camera!!.height * camera!!.zoom).toInt() / 2
        camera!!.y -= (yOffset * camera!!.zoom).toInt()
        camera!!.scroll.set(chrome.x, chrome.y)
        Camera.add(camera!!)

        shadow.boxRect(
                camera!!.x / camera!!.zoom,
                camera!!.y / camera!!.zoom,
                chrome.width(), chrome.height)

        Keys.event.add(this as Signal.Listener<Key?>)
    }

    open fun resize(w: Int, h: Int) {
        this.width = w
        this.height = h

        chrome.size(
                (width + chrome.marginHor()).toFloat(),
                (height + chrome.marginVer()).toFloat())

        camera!!.resize(chrome.width.toInt(), chrome.height.toInt())
        camera!!.x = (Game.width - camera!!.screenWidth()).toInt() / 2
        camera!!.y = (Game.height - camera!!.screenHeight()).toInt() / 2
        camera!!.y += (yOffset * camera!!.zoom).toInt()

        shadow.boxRect(camera!!.x / camera!!.zoom, camera!!.y / camera!!.zoom, chrome.width(), chrome.height)
    }

    fun offset(yOffset: Int) {
        camera!!.y -= (this.yOffset * camera!!.zoom).toInt()
        this.yOffset = yOffset
        camera!!.y += (yOffset * camera!!.zoom).toInt()

        shadow.boxRect(camera!!.x / camera!!.zoom, camera!!.y / camera!!.zoom, chrome.width(), chrome.height)
    }

    open fun hide() {
        if (parent != null) {
            parent!!.erase(this)
        }
        destroy()
    }

    override fun destroy() {
        super.destroy()

        Camera.remove(camera!!)
        Keys.event.remove(this)
    }

    override fun onSignal(key: Key) {
        if (key.pressed) {
            when (key.code) {
                Keys.BACK -> onBackPressed()
                Keys.MENU -> onMenuPressed()
            }
        }

        Keys.event.cancel()
    }

    open fun onBackPressed() {
        hide()
    }

    open fun onMenuPressed() {}

    companion object {

        val TITLE_COLOR = 0xFFFF44
        val SHPX_COLOR = 0x33BB33
    }
}
