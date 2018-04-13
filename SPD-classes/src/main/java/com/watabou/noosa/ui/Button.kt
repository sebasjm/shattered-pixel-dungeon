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

package com.watabou.noosa.ui

import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Game
import com.watabou.noosa.TouchArea

open class Button : Component() {

    protected var hotArea: TouchArea? = null

    protected var pressed: Boolean = false
    protected var pressTime: Float = 0.toFloat()

    protected var processed: Boolean = false

    override fun createChildren() {
        hotArea = object : TouchArea(0f, 0f, 0f, 0f) {
            override fun onTouchDown(touch: Touch) {
                pressed = true
                pressTime = 0f
                processed = false
                this@Button.onTouchDown()
            }

            override fun onTouchUp(touch: Touch) {
                pressed = false
                this@Button.onTouchUp()
            }

            override fun onClick(touch: Touch) {
                if (!processed) {
                    this@Button.onClick()
                }
            }
        }
        add(hotArea!!)
    }

    override fun update() {
        super.update()

        hotArea!!.active = visible

        if (pressed) {
            pressTime += Game.elapsed
            if (pressTime >= longClick) {
                pressed = false
                if (onLongClick()) {

                    hotArea!!.reset()
                    processed = true
                    onTouchUp()

                    Game.vibrate(50)
                }
            }
        }
    }

    protected open fun onTouchDown() {}
    protected open fun onTouchUp() {}
    protected open fun onClick() {}

    protected open fun onLongClick(): Boolean {
        return false
    }

    override fun layout() {
        hotArea!!.x = x
        hotArea!!.y = y
        hotArea!!.width = width
        hotArea!!.height = height
    }

    companion object {

        var longClick = 1f
    }
}
