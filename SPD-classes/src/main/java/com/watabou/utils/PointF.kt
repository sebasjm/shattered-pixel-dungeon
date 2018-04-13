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

import android.annotation.SuppressLint

@SuppressLint("FloatMath")
class PointF {

    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()

    constructor() {}

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    constructor(p: PointF) {
        this.x = p.x
        this.y = p.y
    }

    constructor(p: Point) {
        this.x = p.x.toFloat()
        this.y = p.y.toFloat()
    }

    fun clone(): PointF {
        return PointF(this)
    }

    fun scale(f: Float): PointF {
        this.x *= f
        this.y *= f
        return this
    }

    fun invScale(f: Float): PointF {
        this.x /= f
        this.y /= f
        return this
    }

    operator fun set(x: Float, y: Float): PointF {
        this.x = x
        this.y = y
        return this
    }

    fun set(p: PointF): PointF {
        this.x = p.x
        this.y = p.y
        return this
    }

    fun set(v: Float): PointF {
        this.x = v
        this.y = v
        return this
    }

    fun polar(a: Float, l: Float): PointF {
        this.x = l * Math.cos(a.toDouble()).toFloat()
        this.y = l * Math.sin(a.toDouble()).toFloat()
        return this
    }

    fun offset(dx: Float, dy: Float): PointF {
        x += dx
        y += dy
        return this
    }

    fun offset(p: PointF): PointF {
        x += p.x
        y += p.y
        return this
    }

    fun negate(): PointF {
        x = -x
        y = -y
        return this
    }

    fun normalize(): PointF {
        val l = length()
        x /= l
        y /= l
        return this
    }

    fun floor(): Point {
        return Point(x.toInt(), y.toInt())
    }

    fun length(): Float {
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    override fun toString(): String {
        return "$x, $y"
    }

    override fun equals(o: Any?): Boolean {
        return if (super.equals(o)) true else o is PointF && o.x == x && o.y == y
    }

    companion object {

        val PI = 3.1415926f
        val PI2 = PI * 2
        val G2R = PI / 180

        fun sum(a: PointF, b: PointF): PointF {
            return PointF(a.x + b.x, a.y + b.y)
        }

        fun diff(a: PointF, b: PointF): PointF {
            return PointF(a.x - b.x, a.y - b.y)
        }

        fun inter(a: PointF, b: PointF, d: Float): PointF {
            return PointF(a.x + (b.x - a.x) * d, a.y + (b.y - a.y) * d)
        }

        fun distance(a: PointF, b: PointF): Float {
            val dx = a.x - b.x
            val dy = a.y - b.y
            return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        }

        fun angle(start: PointF, end: PointF): Float {
            return Math.atan2((end.y - start.y).toDouble(), (end.x - start.x).toDouble()).toFloat()
        }
    }
}
