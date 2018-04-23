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

package com.shatteredpixel.shatteredpixeldungeon.levels.painters

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point
import com.watabou.utils.Rect

import java.util.ArrayList
import java.util.Arrays

abstract class Painter {

    //If painters require additional parameters, they should
    // request them in their constructor or other methods

    //Painters take a level and its collection of rooms, and paint all the specific tile values
    abstract fun paint(level: Level, rooms: ArrayList<Room>?): Boolean

    companion object {

        // Static methods

        operator fun set(level: Level, cell: Int, value: Int) {
            level.map[cell] = value
        }

        operator fun set(level: Level, x: Int, y: Int, value: Int) {
            set(level, x + y * level.width(), value)
        }

        operator fun set(level: Level, p: Point, value: Int) {
            set(level, p.x, p.y, value)
        }

        fun fill(level: Level, x: Int, y: Int, w: Int, h: Int, value: Int) {

            val width = level.width()

            var pos = y * width + x
            var i = y
            while (i < y + h) {
                Arrays.fill(level.map!!, pos, pos + w, value)
                i++
                pos += width
            }
        }

        fun fill(level: Level, rect: Rect, value: Int) {
            fill(level, rect.left, rect.top, rect.width(), rect.height(), value)
        }

        fun fill(level: Level, rect: Rect, m: Int, value: Int) {
            fill(level, rect.left + m, rect.top + m, rect.width() - m * 2, rect.height() - m * 2, value)
        }

        fun fill(level: Level, rect: Rect, l: Int, t: Int, r: Int, b: Int, value: Int) {
            fill(level, rect.left + l, rect.top + t, rect.width() - (l + r), rect.height() - (t + b), value)
        }

        fun drawLine(level: Level, from: Point, to: Point, value: Int) {
            var x = from.x.toFloat()
            var y = from.y.toFloat()
            var dx = (to.x - from.x).toFloat()
            var dy = (to.y - from.y).toFloat()

            val movingbyX = Math.abs(dx) >= Math.abs(dy)
            //normalize
            if (movingbyX) {
                dy /= Math.abs(dx)
                dx /= Math.abs(dx)
            } else {
                dx /= Math.abs(dy)
                dy /= Math.abs(dy)
            }

            set(level, Math.round(x), Math.round(y), value)
            while (movingbyX && to.x.toFloat() != x || !movingbyX && to.y.toFloat() != y) {
                x += dx
                y += dy
                set(level, Math.round(x), Math.round(y), value)
            }
        }

        fun fillEllipse(level: Level, rect: Rect, value: Int) {
            fillEllipse(level, rect.left, rect.top, rect.width(), rect.height(), value)
        }

        fun fillEllipse(level: Level, rect: Rect, m: Int, value: Int) {
            fillEllipse(level, rect.left + m, rect.top + m, rect.width() - m * 2, rect.height() - m * 2, value)
        }

        fun fillEllipse(level: Level, x: Int, y: Int, w: Int, h: Int, value: Int) {

            //radii
            val radH = (h / 2f).toDouble()
            val radW = (w / 2f).toDouble()

            //fills each row of the ellipse from top to bottom
            for (i in 0 until h) {

                //y coordinate of the row for determining ellipsis width
                //always want to test the middle of a tile, hence the 0.5 shift
                val rowY = -radH + 0.5 + i.toDouble()

                //equation is derived from ellipsis formula: y^2/radH^2 + x^2/radW^2 = 1
                //solves for x and then doubles to get the width
                var rowW = 2.0 * Math.sqrt(radW * radW * (1.0 - rowY * rowY / (radH * radH)))

                //need to round to nearest even or odd number, depending on width
                if (w % 2 == 0) {
                    rowW = Math.round(rowW / 2.0) * 2.0

                } else {
                    rowW = Math.floor(rowW / 2.0) * 2.0
                    rowW++
                }

                val cell = x + (w - rowW.toInt()) / 2 + (y + i) * level.width()
                Arrays.fill(level.map!!, cell, cell + rowW.toInt(), value)

            }

        }

        fun drawInside(level: Level, room: Room, from: Point, n: Int, value: Int): Point {

            val step = Point()
            if (from.x == room.left) {
                step.set(+1, 0)
            } else if (from.x == room.right) {
                step.set(-1, 0)
            } else if (from.y == room.top) {
                step.set(0, +1)
            } else if (from.y == room.bottom) {
                step.set(0, -1)
            }

            val p = Point(from).offset(step)
            for (i in 0 until n) {
                if (value != -1) {
                    set(level, p, value)
                }
                p.offset(step)
            }

            return p
        }
    }
}
