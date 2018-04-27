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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point
import com.watabou.utils.Random

class LaboratoryRoom : SpecialRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)

        val entrance = entrance()

        var pot: Point? = null
        if (entrance.x == left) {
            pot = Point(right - 1, if (Random.Int(2) == 0) top + 1 else bottom - 1)
        } else if (entrance.x == right) {
            pot = Point(left + 1, if (Random.Int(2) == 0) top + 1 else bottom - 1)
        } else if (entrance.y == top) {
            pot = Point(if (Random.Int(2) == 0) left + 1 else right - 1, bottom - 1)
        } else if (entrance.y == bottom) {
            pot = Point(if (Random.Int(2) == 0) left + 1 else right - 1, top + 1)
        }
        Painter.set(level, pot!!, Terrain.ALCHEMY)

        val alchemy = Alchemy()
        alchemy.seed(level, pot.x + level.width() * pot.y, Random.IntRange(25, 50))
        level.blobs[Alchemy::class.java] = alchemy

        val n = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != Terrain.EMPTY_SP || level.heaps.get(pos) != null)
            level.drop(prize(level), pos)
        }

        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey(Dungeon.depth))
    }

    private fun prize(level: Level): Item {

        var prize = level.findPrizeItem(Potion::class.java)
        if (prize == null)
            prize = Generator.random(Generator.Category.POTION)

        return prize!!
    }
}
