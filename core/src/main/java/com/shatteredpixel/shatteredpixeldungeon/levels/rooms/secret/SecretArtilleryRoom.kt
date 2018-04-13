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

import com.shatteredpixel.shatteredpixeldungeon.items.Bomb
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter

class SecretArtilleryRoom : SecretRoom() {

    override fun paint(level: Level) {
        super.paint(level)

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)

        Painter.set(level, center(), Terrain.STATUE_SP)

        for (i in 0..2) {
            var itemPos: Int
            do {
                itemPos = level.pointToCell(random())
            } while (level.map!![itemPos] != Terrain.EMPTY_SP || level.heaps.get(itemPos) != null)

            if (i == 0) {
                level.drop(Bomb.DoubleBomb(), itemPos)
            } else {
                level.drop(Generator.randomMissile(), itemPos)
            }
        }

        entrance().set(Room.Door.Type.HIDDEN)
    }
}
