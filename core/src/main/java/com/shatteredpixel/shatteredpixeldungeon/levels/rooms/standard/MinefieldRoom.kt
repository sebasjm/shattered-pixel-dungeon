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
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class MinefieldRoom : StandardRoom() {

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(4f, 1f, 0f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)
        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }

        var mines = Math.round(Math.sqrt(square().toDouble())).toInt()

        when (sizeCat) {
            StandardRoom.SizeCategory.NORMAL -> mines -= 3
            StandardRoom.SizeCategory.LARGE -> mines += 3
            StandardRoom.SizeCategory.GIANT -> mines += 9
        }

        for (i in 0 until mines) {
            var pos: Int
            do {
                pos = level.pointToCell(random(1))
            } while (level.traps.get(pos) != null)

            //randomly places some embers around the mines
            for (j in 0..7) {
                val c = PathFinder.NEIGHBOURS8[Random.Int(8)]
                if (level.traps.get(pos + c) == null && level.map!![pos + c] == Terrain.EMPTY) {
                    Painter.set(level, pos + c, Terrain.EMBERS)
                }
            }

            Painter.set(level, pos, Terrain.SECRET_TRAP)
            level.setTrap(ExplosiveTrap().hide(), pos)

        }

    }
}
