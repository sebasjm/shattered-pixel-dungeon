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
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap
import com.watabou.utils.Point
import com.watabou.utils.Random

class BurnedRoom : PatchRoom() {

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(4f, 1f, 0f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)
        for (door in connected.values.filterNotNull()) {
            door.set(Room.Door.Type.REGULAR)
        }

        //past 8x8 each point of width/height decreases fill by 3%
        // e.g. a 14x14 burned room has a fill of 54%
        val fill = Math.min(1f, 1.48f - (width() + height()) * 0.03f)
        setupPatch(level, fill, 2, false)

        for (i in top + 1 until bottom) {
            for (j in left + 1 until right) {
                if (!patch!![xyToPatchCoords(j, i)])
                    continue
                val cell = i * level.width() + j
                val t: Int
                when (Random.Int(5)) {
                    0 -> t = Terrain.EMPTY
                    1 -> t = Terrain.EMBERS
                    2 -> {
                        t = Terrain.TRAP
                        level.setTrap(BurningTrap().reveal(), cell)
                    }
                    3 -> {
                        t = Terrain.SECRET_TRAP
                        level.setTrap(BurningTrap().hide(), cell)
                    }
                    4 -> {
                        t = Terrain.INACTIVE_TRAP
                        val trap = BurningTrap()
                        trap.reveal().active = false
                        level.setTrap(trap, cell)
                    }
                    else -> t = Terrain.EMPTY
                }
                level.map[cell] = t
            }
        }
    }

    override fun canPlaceWater(p: Point): Boolean {
        return super.canPlaceWater(p) && !patch!![xyToPatchCoords(p.x, p.y)]
    }

    override fun canPlaceGrass(p: Point): Boolean {
        return super.canPlaceGrass(p) && !patch!![xyToPatchCoords(p.x, p.y)]
    }

    override fun canPlaceTrap(p: Point): Boolean {
        return super.canPlaceTrap(p) && !patch!![xyToPatchCoords(p.x, p.y)]
    }

}
