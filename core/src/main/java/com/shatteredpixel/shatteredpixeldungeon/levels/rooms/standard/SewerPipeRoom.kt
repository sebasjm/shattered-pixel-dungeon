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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.GameMath
import com.watabou.utils.PathFinder
import com.watabou.utils.Point
import com.watabou.utils.PointF
import com.watabou.utils.Random
import com.watabou.utils.Rect

import java.util.ArrayList

class SewerPipeRoom : StandardRoom() {

    //returns the space which all doors must connect to (usually 1 cell, but can be more)
    //Note that, like rooms, this space is inclusive to its right and bottom sides
    protected val connectionSpace: Rect
        get() {
            val c = if (connected.size <= 1) center() else doorCenter

            return Rect(c.x, c.y, c.x, c.y)
        }

    //returns a point equidistant from all doors this room has
    protected val doorCenter: Point
        get() {
            val doorCenter = PointF(0f, 0f)

            for (door in connected.values) {
                doorCenter.x += door.x.toFloat()
                doorCenter.y += door.y.toFloat()
            }

            val c = Point(doorCenter.x.toInt() / connected.size, doorCenter.y.toInt() / connected.size)
            if (Random.Float() < doorCenter.x % 1) c.x++
            if (Random.Float() < doorCenter.y % 1) c.y++
            c.x = GameMath.gate((left + 2).toFloat(), c.x.toFloat(), (right - 2).toFloat()).toInt()
            c.y = GameMath.gate((top + 2).toFloat(), c.y.toFloat(), (bottom - 2).toFloat()).toInt()

            return c
        }

    private var corners: Array<Point>? = null

    override fun minWidth(): Int {
        return Math.max(7, super.minWidth())
    }

    override fun minHeight(): Int {
        return Math.max(7, super.minHeight())
    }

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(4f, 2f, 1f)
    }

    override fun canConnect(p: Point): Boolean {
        //refuses connections next to corners
        return super.canConnect(p) && (p.x > left + 1 && p.x < right - 1 || p.y > top + 1 && p.y < bottom - 1)
    }

    //FIXME this class is a total mess, lots of copy-pasta from tunnel and perimeter rooms here
    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)

        val c = connectionSpace

        if (connected.size <= 2) {
            for (door in connected.values) {

                val start: Point
                val mid: Point
                val end: Point

                start = Point(door)
                if (start.x == left)
                    start.x += 2
                else if (start.y == top)
                    start.y += 2
                else if (start.x == right)
                    start.x -= 2
                else if (start.y == bottom) start.y -= 2

                val rightShift: Int
                val downShift: Int

                if (start.x < c.left)
                    rightShift = c.left - start.x
                else if (start.x > c.right)
                    rightShift = c.right - start.x
                else
                    rightShift = 0

                if (start.y < c.top)
                    downShift = c.top - start.y
                else if (start.y > c.bottom)
                    downShift = c.bottom - start.y
                else
                    downShift = 0

                //always goes inward first
                if (door.x == left || door.x == right) {
                    mid = Point(start.x + rightShift, start.y)
                    end = Point(mid.x, mid.y + downShift)

                } else {
                    mid = Point(start.x, start.y + downShift)
                    end = Point(mid.x + rightShift, mid.y)

                }

                Painter.drawLine(level, start, mid, Terrain.WATER)
                Painter.drawLine(level, mid, end, Terrain.WATER)

            }
        } else {
            val pointsToFill = ArrayList<Point>()
            for (door in connected.values) {
                val p = Point(door)
                if (p.y == top) {
                    p.y += 2
                } else if (p.y == bottom) {
                    p.y -= 2
                } else if (p.x == left) {
                    p.x += 2
                } else {
                    p.x -= 2
                }
                pointsToFill.add(p)
            }

            val pointsFilled = ArrayList<Point>()
            pointsFilled.add(pointsToFill.removeAt(0))

            var from: Point? = null
            var to: Point? = null
            var shortestDistance: Int
            while (!pointsToFill.isEmpty()) {
                shortestDistance = Integer.MAX_VALUE
                for (f in pointsFilled) {
                    for (t in pointsToFill) {
                        val dist = distanceBetweenPoints(f, t)
                        if (dist < shortestDistance) {
                            from = f
                            to = t
                            shortestDistance = dist
                        }
                    }
                }
                fillBetweenPoints(level, from!!, to!!, Terrain.WATER)
                pointsFilled.add(to)
                pointsToFill.remove(to)
            }
        }

        for (p in points) {
            val cell = level.pointToCell(p)
            if (level.map!![cell] == Terrain.WATER) {
                for (i in PathFinder.NEIGHBOURS8) {
                    if (level.map!![cell + i] == Terrain.WALL) {
                        Painter.set(level, cell + i, Terrain.EMPTY)
                    }
                }
            }
        }

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }
    }

    override fun canPlaceWater(p: Point): Boolean {
        return false
    }

    private fun spaceBetween(a: Int, b: Int): Int {
        return Math.abs(a - b) - 1
    }

    //gets the path distance between two points
    private fun distanceBetweenPoints(a: Point, b: Point): Int {
        //on the same side
        return if (a.y == b.y || a.x == b.x) {
            Math.max(spaceBetween(a.x, b.x), spaceBetween(a.y, b.y))
        } else Math.min(spaceBetween(left, a.x) + spaceBetween(left, b.x),
                spaceBetween(right, a.x) + spaceBetween(right, b.x)) + Math.min(spaceBetween(top, a.y) + spaceBetween(top, b.y),
                spaceBetween(bottom, a.y) + spaceBetween(bottom, b.y)) - 1

        //otherwise...
        //subtract 1 at the end to account for overlap
    }

    //picks the smallest path to fill between two points
    private fun fillBetweenPoints(level: Level, from: Point, to: Point, floor: Int) {

        //doors are along the same side
        if (from.y == to.y || from.x == to.x) {
            Painter.fill(level,
                    Math.min(from.x, to.x),
                    Math.min(from.y, to.y),
                    spaceBetween(from.x, to.x) + 2,
                    spaceBetween(from.y, to.y) + 2,
                    floor)
            return
        }

        //set up corners
        if (corners == null) {
            corners = arrayOfNulls(4)
            corners[0] = Point(left + 2, top + 2)
            corners[1] = Point(right - 2, top + 2)
            corners[2] = Point(right - 2, bottom - 2)
            corners[3] = Point(left + 2, bottom - 2)
        }

        //doors on adjacent sides
        for (c in corners!!) {
            if ((c.x == from.x || c.y == from.y) && (c.x == to.x || c.y == to.y)) {
                Painter.drawLine(level, from, c, floor)
                Painter.drawLine(level, c, to, floor)
                return
            }
        }

        //doors on opposite sides
        val side: Point
        if (from.y == top + 2 || from.y == bottom - 2) {
            //connect along the left, or right side
            if (spaceBetween(left, from.x) + spaceBetween(left, to.x) <= spaceBetween(right, from.x) + spaceBetween(right, to.x)) {
                side = Point(left + 2, top + height() / 2)
            } else {
                side = Point(right - 2, top + height() / 2)
            }

        } else {
            //connect along the top, or bottom side
            if (spaceBetween(top, from.y) + spaceBetween(top, to.y) <= spaceBetween(bottom, from.y) + spaceBetween(bottom, to.y)) {
                side = Point(left + width() / 2, top + 2)
            } else {
                side = Point(left + width() / 2, bottom - 2)
            }
        }
        //treat this as two connections with adjacent sides
        fillBetweenPoints(level, from, side, floor)
        fillBetweenPoints(level, side, to, floor)
    }

}
