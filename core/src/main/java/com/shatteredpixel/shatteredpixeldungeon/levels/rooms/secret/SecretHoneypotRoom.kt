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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee
import com.shatteredpixel.shatteredpixeldungeon.items.Bomb
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point
import com.watabou.utils.Random

class SecretHoneypotRoom : SecretRoom() {

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        val brokenPotPos = center()

        brokenPotPos.x = (brokenPotPos.x + entrance().x) / 2
        brokenPotPos.y = (brokenPotPos.y + entrance().y) / 2

        val pot = Honeypot.ShatteredPot()
        level.drop(pot, level.pointToCell(brokenPotPos))

        val bee = Bee()
        bee.spawn(Dungeon.depth)
        bee.HP = bee.HT
        bee.pos = level.pointToCell(brokenPotPos)
        level.mobs.add(bee)

        pot.setBee(bee)
        bee.setPotInfo(level.pointToCell(brokenPotPos), null)

        placeItem(Honeypot(), level)

        placeItem(if (Random.Int(3) == 0) Bomb.DoubleBomb() else Bomb(), level)

        if (Random.Int(2) == 0) {
            placeItem(Bomb(), level)
        }

        entrance().set(Room.Door.Type.HIDDEN)
    }

    private fun placeItem(item: Item, level: Level) {
        var itemPos: Int
        do {
            itemPos = level.pointToCell(random())
        } while (level.heaps.get(itemPos) != null)

        level.drop(item, itemPos)
    }
}
