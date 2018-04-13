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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap
import com.watabou.utils.Point
import com.watabou.utils.Random

class BlacksmithRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(super.minWidth(), 6)
    }

    override fun minHeight(): Int {
        return Math.max(super.minHeight(), 6)
    }

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.TRAP)
        Painter.fill(level, this, 2, Terrain.EMPTY_SP)

        for (i in 0..1) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != Terrain.EMPTY_SP)
            level.drop(
                    Generator.random(Random.oneOf<Category>(
                            Generator.Category.ARMOR,
                            Generator.Category.WEAPON,
                            Generator.Category.MISSILE
                    )), pos)
        }

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
            Painter.drawInside(level, this, door, 1, Terrain.EMPTY)
        }

        val npc = Blacksmith()
        do {
            npc.pos = level.pointToCell(random(2))
        } while (level.heaps.get(npc.pos) != null)
        level.mobs.add(npc)

        for (p in points) {
            val cell = level.pointToCell(p)
            if (level.map!![cell] == Terrain.TRAP) {
                level.setTrap(BurningTrap().reveal(), cell)
            }
        }
    }
}
