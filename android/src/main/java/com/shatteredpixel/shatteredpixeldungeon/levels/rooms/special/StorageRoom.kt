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

import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Random

class StorageRoom : SpecialRoom() {

    override fun paint(level: Level) {

        val floor = Terrain.EMPTY_SP

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, floor)

        var honeyPot = Random.Int(2) == 0

        val n = Random.IntRange(3, 4)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != floor)
            if (honeyPot) {
                level.drop(Honeypot(), pos)
                honeyPot = false
            } else
                level.drop(prize(level), pos)
        }

        entrance().set(Room.Door.Type.BARRICADE)
        level.addItemToSpawn(PotionOfLiquidFlame())
    }

    private fun prize(level: Level): Item? {

        if (Random.Int(2) != 0) {
            val prize = level.findPrizeItem()
            if (prize != null)
                return prize
        }

        return Generator.random(Random.oneOf<Generator.Category>(
                Generator.Category.POTION,
                Generator.Category.SCROLL,
                Generator.Category.FOOD,
                Generator.Category.GOLD
        ))
    }
}
