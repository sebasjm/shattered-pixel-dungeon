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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush
import com.watabou.utils.Point

class SecretLarderRoom : SecretRoom() {

    override fun minHeight(): Int {
        return 6
    }

    override fun minWidth(): Int {
        return 6
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)

        val c = center()

        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.WATER)
        Painter.set(level, c, Terrain.GRASS)

        level.plant(BlandfruitBush.Seed(), level.pointToCell(c))

        var extraFood = (Hunger.STARVING - Hunger.HUNGRY).toInt() * (1 + Dungeon.depth / 5)

        while (extraFood > 0) {
            val food: Food
            if (extraFood >= Hunger.STARVING) {
                food = Pasty()
                extraFood -= Hunger.STARVING.toInt()
            } else {
                food = ChargrilledMeat()
                extraFood -= (Hunger.STARVING - Hunger.HUNGRY).toInt()
            }
            var foodPos: Int
            do {
                foodPos = level.pointToCell(random())
            } while (level.map!![foodPos] != Terrain.EMPTY_SP || level.heaps.get(foodPos) != null)
            level.drop(food, foodPos)
        }

        entrance().set(Room.Door.Type.HIDDEN)
    }


}
