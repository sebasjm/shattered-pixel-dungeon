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

import java.util.ArrayList

open class Rect @JvmOverloads constructor(var left: Int = 0, var top: Int = 0, var right: Int = 0, var bottom: Int = 0) {

    val isEmpty: Boolean
        get() = right <= left || bottom <= top

    val points: ArrayList<Point>
        get() {
            val points = ArrayList<Point>()
            for (i in left..right)
                for (j in top..bottom)
                    points.add(Point(i, j))
            return points
        }

    constructor(rect: Rect) : this(rect.left, rect.top, rect.right, rect.bottom) {}

    open fun width(): Int {
        return right - left
    }

    open fun height(): Int {
        return bottom - top
    }

    fun square(): Int {
        return width() * height()
    }

    operator fun set(left: Int, top: Int, right: Int, bottom: Int): Rect {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        return this
    }

    fun set(rect: Rect): Rect {
        return set(rect.left, rect.top, rect.right, rect.bottom)
    }

    fun setPos(x: Int, y: Int): Rect {
        return set(x, y, x + (right - left), y + (bottom - top))
    }

    fun shift(x: Int, y: Int): Rect {
        return set(left + x, top + y, right + x, bottom + y)
    }

    fun resize(w: Int, h: Int): Rect {
        return set(left, top, left + w, top + h)
    }

    fun setEmpty(): Rect {
        bottom = 0
        top = bottom
        right = top
        left = right
        return this
    }

    fun intersect(other: Rect): Rect {
        val result = Rect()
        result.left = Math.max(left, other.left)
        result.right = Math.min(right, other.right)
        result.top = Math.max(top, other.top)
        result.bottom = Math.min(bottom, other.bottom)
        return result
    }

    fun union(other: Rect): Rect {
        val result = Rect()
        result.left = Math.min(left, other.left)
        result.right = Math.max(right, other.right)
        result.top = Math.min(top, other.top)
        result.bottom = Math.max(bottom, other.bottom)
        return result
    }

    fun union(x: Int, y: Int): Rect {
        if (isEmpty) {
            return set(x, y, x + 1, y + 1)
        } else {
            if (x < left) {
                left = x
            } else if (x >= right) {
                right = x + 1
            }
            if (y < top) {
                top = y
            } else if (y >= bottom) {
                bottom = y + 1
            }
            return this
        }
    }

    fun union(p: Point): Rect {
        return union(p.x, p.y)
    }

    open fun inside(p: Point): Boolean {
        return p.x >= left && p.x < right && p.y >= top && p.y < bottom
    }

    @JvmOverloads
    fun shrink(d: Int = 1): Rect {
        return Rect(left + d, top + d, right - d, bottom - d)
    }

}
