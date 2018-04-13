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

import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Arrays

class VaultRoom : SpecialRoom() {

    private val prizeClasses = ArrayList<Category>(
            Arrays.asList<Category>(Generator.Category.WAND,
                    Generator.Category.RING,
                    Generator.Category.ARTIFACT))

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)
        Painter.fill(level, this, 2, Terrain.EMPTY)

        val cx = (left + right) / 2
        val cy = (top + bottom) / 2
        val c = cx + cy * level.width()

        Random.shuffle<Category>(prizeClasses)

        val i1: Item
        val i2: Item
        i1 = prize(level)
        i2 = prize(level)
        level.drop(i1, c).type = Heap.Type.CRYSTAL_CHEST
        level.drop(i2, c + PathFinder.NEIGHBOURS8[Random.Int(8)]).type = Heap.Type.CRYSTAL_CHEST
        level.addItemToSpawn(CrystalKey(Dungeon.depth))

        entrance().set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey(Dungeon.depth))
    }

    private fun prize(level: Level): Item {
        val cat = prizeClasses.removeAt(0)
        var prize: Item? = null
        do {
            prize = Generator.random(cat)
        } while (prize == null || Challenges.isItemBlocked(prize))
        return prize
    }
}
