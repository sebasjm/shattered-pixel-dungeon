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

import com.watabou.noosa.Group

open class Component : Group() {

    protected var x: Float = 0.toFloat()
    protected var y: Float = 0.toFloat()
    protected var width: Float = 0.toFloat()
    protected var height: Float = 0.toFloat()

    init {
        createChildren()
    }

    fun setPos(x: Float, y: Float): Component {
        this.x = x
        this.y = y
        layout()

        return this
    }

    fun setSize(width: Float, height: Float): Component {
        this.width = width
        this.height = height
        layout()

        return this
    }

    fun setRect(x: Float, y: Float, width: Float, height: Float): Component {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        layout()

        return this
    }

    fun inside(x: Float, y: Float): Boolean {
        return x >= this.x && y >= this.y && x < this.x + width && y < this.y + height
    }

    fun fill(c: Component) {
        setRect(c.x, c.y, c.width, c.height)
    }

    fun left(): Float {
        return x
    }

    fun right(): Float {
        return x + width
    }

    fun centerX(): Float {
        return x + width / 2
    }

    fun top(): Float {
        return y
    }

    fun bottom(): Float {
        return y + height
    }

    fun centerY(): Float {
        return y + height / 2
    }

    fun width(): Float {
        return width
    }

    fun height(): Float {
        return height
    }

    protected open fun createChildren() {}

    protected open fun layout() {}
}
