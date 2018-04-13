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

class RuinsRoom : PatchRoom() {

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(9f, 3f, 1f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)
        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }

        //fill scales from ~10% at 4x4, to ~25% at 18x18
        // normal   ~20% to ~25%
        // large    ~25% to ~30%
        // giant    ~30% to ~35%
        val fill = .2f + width() * height() / 2048f

        setupPatch(level, fill, 0, true)
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
