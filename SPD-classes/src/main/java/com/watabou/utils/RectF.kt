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

class RectF @JvmOverloads constructor(var left: Float = 0f, var top: Float = 0f, var right: Float = 0f, var bottom: Float = 0f) {

    val isEmpty: Boolean
        get() = right <= left || bottom <= top

    constructor(rect: RectF) : this(rect.left, rect.top, rect.right, rect.bottom) {}

    constructor(rect: Rect) : this(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat()) {}

    fun width(): Float {
        return right - left
    }

    fun height(): Float {
        return bottom - top
    }

    fun square(): Float {
        return width() * height()
    }

    operator fun set(left: Float, top: Float, right: Float, bottom: Float): RectF {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        return this
    }

    fun set(rect: Rect): RectF {
        return set(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat())
    }

    fun setPos(x: Float, y: Float): RectF {
        return set(x, y, x + (right - left), y + (bottom - top))
    }

    fun shift(x: Float, y: Float): RectF {
        return set(left + x, top + y, right + x, bottom + y)
    }

    fun resize(w: Float, h: Float): RectF {
        return set(left, top, left + w, top + h)
    }

    fun setEmpty(): RectF {
        bottom = 0f
        top = bottom
        right = top
        left = right
        return this
    }

    fun intersect(other: RectF): RectF {
        val result = RectF()
        result.left = Math.max(left, other.left)
        result.right = Math.min(right, other.right)
        result.top = Math.max(top, other.top)
        result.bottom = Math.min(bottom, other.bottom)
        return result
    }

    fun union(other: RectF): RectF {
        val result = RectF()
        result.left = Math.min(left, other.left)
        result.right = Math.max(right, other.right)
        result.top = Math.min(top, other.top)
        result.bottom = Math.max(bottom, other.bottom)
        return result
    }

    fun union(x: Float, y: Float): RectF {
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

    fun union(p: Point): RectF {
        return union(p.x.toFloat(), p.y.toFloat())
    }

    fun inside(p: Point): Boolean {
        return p.x >= left && p.x < right && p.y >= top && p.y < bottom
    }

    @JvmOverloads
    fun shrink(d: Float = 1f): RectF {
        return RectF(left + d, top + d, right - d, bottom - d)
    }

}
