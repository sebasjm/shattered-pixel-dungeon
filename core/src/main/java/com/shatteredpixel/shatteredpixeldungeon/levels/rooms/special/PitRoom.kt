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
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.Point
import com.watabou.utils.Random

class PitRoom : SpecialRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        val entrance = entrance()
        entrance.set(Room.Door.Type.LOCKED)

        var well: Point? = null
        if (entrance.x == left) {
            well = Point(right - 1, if (Random.Int(2) == 0) top + 1 else bottom - 1)
        } else if (entrance.x == right) {
            well = Point(left + 1, if (Random.Int(2) == 0) top + 1 else bottom - 1)
        } else if (entrance.y == top) {
            well = Point(if (Random.Int(2) == 0) left + 1 else right - 1, bottom - 1)
        } else if (entrance.y == bottom) {
            well = Point(if (Random.Int(2) == 0) left + 1 else right - 1, top + 1)
        }
        Painter.set(level, well!!, Terrain.EMPTY_WELL)

        var remains = level.pointToCell(random())
        while (level.map!![remains] == Terrain.EMPTY_WELL) {
            remains = level.pointToCell(random())
        }

        level.drop(IronKey(Dungeon.depth), remains).type = Heap.Type.SKELETON
        var mainLoot: Item? = null
        do {
            when (Random.Int(3)) {
                0 -> mainLoot = Generator.random(Generator.Category.RING)
                1 -> mainLoot = Generator.random(Generator.Category.ARTIFACT)
                2 -> mainLoot = Generator.random(Random.oneOf<Category>(
                        Generator.Category.WEAPON,
                        Generator.Category.ARMOR))
            }
        } while (mainLoot == null || Challenges.isItemBlocked(mainLoot))
        level.drop(mainLoot, remains)

        val n = Random.IntRange(1, 2)
        for (i in 0 until n) {
            level.drop(prize(level), remains)
        }
    }

    private fun prize(level: Level): Item? {

        if (Random.Int(2) != 0) {
            val prize = level.findPrizeItem()
            if (prize != null)
                return prize
        }

        return Generator.random(Random.oneOf<Category>(
                Generator.Category.POTION,
                Generator.Category.SCROLL,
                Generator.Category.FOOD,
                Generator.Category.GOLD
        ))
    }

    override fun canPlaceTrap(p: Point): Boolean {
        //the player is already weak after landing, and will likely need to kite the ghost.
        //having traps here just seems unfair
        return false
    }
}
