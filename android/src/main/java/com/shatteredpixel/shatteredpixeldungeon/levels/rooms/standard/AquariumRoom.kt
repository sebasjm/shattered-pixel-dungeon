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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room

class AquariumRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(super.minWidth(), 7)
    }

    override fun minHeight(): Int {
        return Math.max(super.minHeight(), 7)
    }

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(3f, 1f, 0f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)
        Painter.fill(level, this, 2, Terrain.EMPTY_SP)
        Painter.fill(level, this, 3, Terrain.WATER)

        val minDim = Math.min(width(), height())
        val numFish = (minDim - 4) / 3 //1-3 fish, depending on room size

        for (i in 0 until numFish) {
            val piranha = Piranha()
            do {
                piranha.pos = level.pointToCell(random(3))
            } while (level.map!![piranha.pos] != Terrain.WATER || level.findMob(piranha.pos) != null)
            level.mobs.add(piranha)
        }

        for (door in connected.values.filterNotNull()) {
            door.set(Room.Door.Type.REGULAR)
        }

        super.paint(level)
    }

}
