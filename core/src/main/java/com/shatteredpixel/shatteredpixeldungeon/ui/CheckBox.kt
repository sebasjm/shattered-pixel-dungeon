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

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene

open class CheckBox(label: String) : RedButton(label) {

    private var checked = false

    init {

        icon(Icons.get(Icons.UNCHECKED))
    }

    override fun layout() {
        super.layout()

        var margin = (height - text!!.baseLine()) / 2

        text!!.x = x + margin
        text!!.y = y + margin
        PixelScene.align(text!!)

        margin = (height - icon!!.height) / 2

        icon!!.x = x + width - margin - icon!!.width
        icon!!.y = y + margin
        PixelScene.align(icon!!)
    }

    fun checked(): Boolean {
        return checked
    }

    fun checked(value: Boolean) {
        if (checked != value) {
            checked = value
            icon!!.copy(Icons.get(if (checked) Icons.CHECKED else Icons.UNCHECKED))
        }
    }

    override fun onClick() {
        super.onClick()
        checked(!checked)
    }
}
