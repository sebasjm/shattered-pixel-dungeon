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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene

class PitfallTrap : Trap() {

    init {
        color = Trap.RED
        shape = Trap.DIAMOND
    }

    override fun activate() {
        val heap = Dungeon.level!!.heaps.get(pos)

        if (heap != null) {
            for (item in heap.items!!) {
                Dungeon.dropToChasm(item)
            }
            heap.sprite!!.kill()
            GameScene.discard(heap)
            Dungeon.level!!.heaps.remove(pos)
        }

        val ch = Actor.findChar(pos)

        if (ch === Dungeon.hero!!) {
            Chasm.heroFall(pos)
        } else if (ch != null) {
            Chasm.mobFall(ch as Mob)
        }
    }

    //TODO these used to become chasms when disarmed, but the functionality was problematic
    //because it could block routes, perhaps some way to make this work elegantly?
}
