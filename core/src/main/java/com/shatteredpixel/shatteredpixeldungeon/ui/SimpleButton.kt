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

import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Image
import com.watabou.noosa.TouchArea
import com.watabou.noosa.ui.Component

open class SimpleButton(image: Image) : Component() {

    private var image: Image? = null

    init {

        this.image!!.copy(image)
        width = image.width
        height = image.height
    }

    override fun createChildren() {
        image = Image()
        add(image)

        add(object : TouchArea(image) {
            override fun onTouchDown(touch: Touch) {
                image!!.brightness(1.2f)
            }

            override fun onTouchUp(touch: Touch) {
                image!!.brightness(1.0f)
            }

            override fun onClick(touch: Touch) {
                this@SimpleButton.onClick()
            }
        })
    }

    override fun layout() {
        image!!.x = x
        image!!.y = y
    }

    protected open fun onClick() {}
}
