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
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.GameMath
import com.watabou.utils.Point
import com.watabou.utils.PointF
import com.watabou.utils.Random
import com.watabou.utils.Rect

class HallwayRoom : EmptyRoom() {

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

            for (door in connected.values.filterNotNull()) {
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

    //FIXME lots of copy-pasta from tunnel rooms here
    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        if (connected.size < 2) {
            //don't want to make a hallway between doors that don't exist
            return
        }


        val c = connectionSpace

        for (door in connected.values.filterNotNull()) {

            val start: Point
            val mid: Point
            val end: Point

            start = Point(door)
            if (start.x == left)
                start.x++
            else if (start.y == top)
                start.y++
            else if (start.x == right)
                start.x--
            else if (start.y == bottom) start.y--

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

            Painter.drawLine(level, start, mid, Terrain.EMPTY_SP)
            Painter.drawLine(level, mid, end, Terrain.EMPTY_SP)

        }

        for (door in connected.values.filterNotNull()) {
            door.set(Room.Door.Type.REGULAR)
        }
    }

}
