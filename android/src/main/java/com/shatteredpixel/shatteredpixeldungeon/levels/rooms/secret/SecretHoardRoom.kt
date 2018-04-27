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
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap
import com.watabou.noosa.Game
import com.watabou.utils.Point
import com.watabou.utils.Random

class SecretHoardRoom : SecretRoom() {

    override fun paint(level: Level) {
        super.paint(level)

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        val trapClass: Class<out Trap>
        if (Random.Int(2) == 0) {
            trapClass = RockfallTrap::class.java
        } else if (Dungeon.depth >= 10) {
            trapClass = DisintegrationTrap::class.java
        } else {
            trapClass = PoisonDartTrap::class.java
        }

        var goldPos: Int
        //half of the internal space of the room
        val totalGold = (width() - 2) * (height() - 2) / 2

        //no matter how much gold it drops, roughly equals 8 gold stacks.
        val goldRatio = 8 / totalGold.toFloat()
        for (i in 0 until totalGold) {
            do {
                goldPos = level.pointToCell(random())
            } while (level.heaps.get(goldPos) != null)
            val gold = Gold().random()
            gold.quantity(Math.round(gold.quantity() * goldRatio))
            level.drop(gold, goldPos)
        }

        for (p in points) {
            if (Random.Int(2) == 0 && level.map!![level.pointToCell(p)] == Terrain.EMPTY) {
                try {
                    level.setTrap(trapClass.newInstance().reveal(), level.pointToCell(p))
                    Painter.set(level, p, Terrain.TRAP)
                } catch (e: Exception) {
                    Game.reportException(e)
                }

            }
        }

        entrance().set(Room.Door.Type.HIDDEN)
    }

    override fun canPlaceTrap(p: Point): Boolean {
        return false
    }
}
