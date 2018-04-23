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

import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap
import com.watabou.utils.Point

class SecretSummoningRoom : SecretRoom() {

    //minimum of 3x3 traps, max of 6x6 traps

    override fun maxWidth(): Int {
        return 8
    }

    override fun maxHeight(): Int {
        return 8
    }

    override fun paint(level: Level) {
        super.paint(level)

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.SECRET_TRAP)

        val center = center()
        level.drop(Generator.random(), level.pointToCell(center)).type = Heap.Type.SKELETON

        for (p in points) {
            val cell = level.pointToCell(p)
            if (level.map!![cell] == Terrain.SECRET_TRAP) {
                level.setTrap(SummoningTrap().hide(), cell)
            }
        }

        entrance().set(Room.Door.Type.HIDDEN)
    }

}
