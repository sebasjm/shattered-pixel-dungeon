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

class CaveRoom : PatchRoom() {

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(9f, 3f, 1f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)
        for (door in connected.values.filterNotNull()) {
            door.set(Room.Door.Type.REGULAR)
        }

        //fill scales from ~25% at 4x4, to ~55% at 18x18
        // normal   ~25% to ~35%
        // large    ~35% to ~45%
        // giant    ~45% to ~55%
        val fill = 0.25f + width() * height() / 1024f

        setupPatch(level, fill, 4, true)
        cleanDiagonalEdges()

        for (i in top + 1 until bottom) {
            for (j in left + 1 until right) {
                if (patch!![xyToPatchCoords(j, i)]) {
                    val cell = i * level.width() + j
                    level.map[cell] = Terrain.WALL
                }
            }
        }
    }

}
