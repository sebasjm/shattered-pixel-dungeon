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

package com.watabou.utils

open class Point {

    var x: Int = 0
    var y: Int = 0

    constructor() {}

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    constructor(p: Point) {
        this.x = p.x
        this.y = p.y
    }

    operator fun set(x: Int, y: Int): Point {
        this.x = x
        this.y = y
        return this
    }

    fun set(p: Point): Point {
        x = p.x
        y = p.y
        return this
    }

    fun clone(): Point {
        return Point(this)
    }

    fun scale(f: Float): Point {
        this.x *= f.toInt()
        this.y *= f.toInt()
        return this
    }

    fun offset(dx: Int, dy: Int): Point {
        x += dx
        y += dy
        return this
    }

    fun offset(d: Point): Point {
        x += d.x
        y += d.y
        return this
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is Point) {
            val p = obj as Point?
            return p!!.x == x && p.y == y
        } else {
            return false
        }
    }
}
