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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.Random

class TreasuryRoom : SpecialRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        Painter.set(level, center(), Terrain.STATUE)

        val heapType = if (Random.Int(2) == 0) Heap.Type.CHEST else Heap.Type.HEAP

        val n = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != Terrain.EMPTY || level.heaps.get(pos) != null)
            level.drop(Gold().random(), pos).type = if (Random.Int(20) == 0 && heapType == Heap.Type.CHEST) Heap.Type.MIMIC else heapType
        }

        if (heapType == Heap.Type.HEAP) {
            for (i in 0..5) {
                var pos: Int
                do {
                    pos = level.pointToCell(random())
                } while (level.map!![pos] != Terrain.EMPTY)
                level.drop(Gold(Random.IntRange(5, 12)), pos)
            }
        }

        entrance().set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey(Dungeon.depth))
    }
}
