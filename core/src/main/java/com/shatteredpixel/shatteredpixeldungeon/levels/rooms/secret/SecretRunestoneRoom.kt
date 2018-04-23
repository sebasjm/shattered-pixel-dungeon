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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret

import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point

class SecretRunestoneRoom : SecretRoom() {

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        val entrance = entrance()
        val center = center()

        if (entrance.x == left || entrance.x == right) {
            Painter.drawLine(level,
                    Point(center.x, top + 1),
                    Point(center.x, bottom - 1),
                    Terrain.BOOKSHELF)
            if (entrance.x == left) {
                Painter.fill(level, center.x + 1, top + 1, right - center.x - 1, height() - 2, Terrain.EMPTY_SP)
            } else {
                Painter.fill(level, left + 1, top + 1, center.x - left - 1, height() - 2, Terrain.EMPTY_SP)
            }
        } else {
            Painter.drawLine(level,
                    Point(left + 1, center.y),
                    Point(right - 1, center.y),
                    Terrain.BOOKSHELF)
            if (entrance.y == top) {
                Painter.fill(level, left + 1, center.y + 1, width() - 2, bottom - center.y - 1, Terrain.EMPTY_SP)
            } else {
                Painter.fill(level, left + 1, top + 1, width() - 2, center.y - top - 1, Terrain.EMPTY_SP)
            }
        }

        level.addItemToSpawn(PotionOfLiquidFlame())

        var dropPos: Int
        do {
            dropPos = level.pointToCell(random())
        } while (level.map!![dropPos] != Terrain.EMPTY_SP)
        level.drop(StoneOfEnchantment(), dropPos)

        entrance.set(Room.Door.Type.HIDDEN)
    }

    override fun canPlaceWater(p: Point): Boolean {
        return false
    }

    override fun canPlaceGrass(p: Point): Boolean {
        return false
    }
}
