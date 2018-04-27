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

class StatuesRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(7, super.minWidth())
    }

    override fun minHeight(): Int {
        return Math.max(7, super.minHeight())
    }

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(9f, 3f, 1f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        for (door in connected.values.filterNotNull()) {
            door.set(Room.Door.Type.REGULAR)
        }


        val rows = (width() + 1) / 6
        val cols = (height() + 1) / 6

        val w = (width() - 4 - (rows - 1)) / rows
        val h = (height() - 4 - (cols - 1)) / cols

        val Wspacing = if (rows % 2 == width() % 2) 2 else 1
        val Hspacing = if (cols % 2 == height() % 2) 2 else 1

        for (x in 0 until rows) {
            for (y in 0 until cols) {
                val left = this.left + 2 + x * (w + Wspacing)
                val top = this.top + 2 + y * (h + Hspacing)

                Painter.fill(level, left, top, w, h, Terrain.EMPTY_SP)

                Painter.set(level, left, top, Terrain.STATUE_SP)
                Painter.set(level, left + w - 1, top, Terrain.STATUE_SP)
                Painter.set(level, left, top + h - 1, Terrain.STATUE_SP)
                Painter.set(level, left + w - 1, top + h - 1, Terrain.STATUE_SP)
            }
        }

    }
}
