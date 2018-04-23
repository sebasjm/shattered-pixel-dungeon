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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point

import java.util.ArrayList

//tunnels along the room's perimeter
open class PerimeterRoom : ConnectionRoom() {

    private var corners: Array<Point?>? = null

    override fun paint(level: Level) {

        val floor = level.tunnelTile()

        val pointsToFill = ArrayList<Point>()
        for (door in connected.values.filterNotNull()) {
            val p = Point(door)
            if (p.y == top) {
                p.y++
            } else if (p.y == bottom) {
                p.y--
            } else if (p.x == left) {
                p.x++
            } else {
                p.x--
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
            fillBetweenPoints(level, from!!, to!!, floor)
            pointsFilled.add(to)
            pointsToFill.remove(to)
        }

        for (door in connected.values.filterNotNull()) {
            door.set(Room.Door.Type.TUNNEL)
        }
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
            corners!![0] = Point(left + 1, top + 1)
            corners!![1] = Point(right - 1, top + 1)
            corners!![2] = Point(right - 1, bottom - 1)
            corners!![3] = Point(left + 1, bottom - 1)
        }

        //doors on adjacent sides
        for (c in corners!!) {
            if ((c!!.x == from.x || c!!.y == from.y) && (c!!.x == to.x || c!!.y == to.y)) {
                Painter.drawLine(level, from, c, floor)
                Painter.drawLine(level, c, to, floor)
                return
            }
        }

        //doors on opposite sides
        val side: Point
        if (from.y == top + 1 || from.y == bottom - 1) {
            //connect along the left, or right side
            if (spaceBetween(left, from.x) + spaceBetween(left, to.x) <= spaceBetween(right, from.x) + spaceBetween(right, to.x)) {
                side = Point(left + 1, top + height() / 2)
            } else {
                side = Point(right - 1, top + height() / 2)
            }

        } else {
            //connect along the top, or bottom side
            if (spaceBetween(top, from.y) + spaceBetween(top, to.y) <= spaceBetween(bottom, from.y) + spaceBetween(bottom, to.y)) {
                side = Point(left + width() / 2, top + 1)
            } else {
                side = Point(left + width() / 2, bottom - 1)
            }
        }
        //treat this as two connections with adjacent sides
        fillBetweenPoints(level, from, side, floor)
        fillBetweenPoints(level, side, to, floor)
    }
}
