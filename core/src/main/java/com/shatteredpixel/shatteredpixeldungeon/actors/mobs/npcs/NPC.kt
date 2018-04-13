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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

abstract class NPC : Mob() {

    init {
        HT = 1
        HP = HT
        EXP = 0

        alignment = Char.Alignment.NEUTRAL
        state = PASSIVE
    }

    protected fun throwItem() {
        val heap = Dungeon.level!!.heaps.get(pos)
        if (heap != null) {
            var n: Int
            do {
                n = pos + PathFinder.NEIGHBOURS8[Random.Int(8)]
            } while (!Dungeon.level!!.passable[n] && !Dungeon.level!!.avoid[n])
            Dungeon.level!!.drop(heap.pickUp(), n).sprite!!.drop(pos)
        }
    }

    override fun beckon(cell: Int) {}

    abstract fun interact(): Boolean
}