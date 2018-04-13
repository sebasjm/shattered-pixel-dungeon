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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotLasher
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class RotGardenRoom : SpecialRoom() {

    override fun minWidth(): Int {
        return 7
    }

    override fun minHeight(): Int {
        return 7
    }

    override fun paint(level: Level) {

        val entrance = entrance()
        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey(Dungeon.depth))

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.GRASS)


        var heartX = Random.IntRange(left + 1, right - 1)
        var heartY = Random.IntRange(top + 1, bottom - 1)

        if (entrance.x == left) {
            heartX = right - 1
        } else if (entrance.x == right) {
            heartX = left + 1
        } else if (entrance.y == top) {
            heartY = bottom - 1
        } else if (entrance.y == bottom) {
            heartY = top + 1
        }

        placePlant(level, heartX + heartY * level.width(), RotHeart())

        val lashers = (width() - 2) * (height() - 2) / 8

        for (i in 1..lashers) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (!validPlantPos(level, pos))
            placePlant(level, pos, RotLasher())
        }
    }

    private fun validPlantPos(level: Level, pos: Int): Boolean {
        if (level.map!![pos] != Terrain.GRASS) {
            return false
        }

        for (i in PathFinder.NEIGHBOURS9) {
            if (level.findMob(pos + i) != null) {
                return false
            }
        }

        return true
    }

    private fun placePlant(level: Level, pos: Int, plant: Mob) {
        plant.pos = pos
        level.mobs.add(plant)

        for (i in PathFinder.NEIGHBOURS8) {
            if (level.map!![pos + i] == Terrain.GRASS) {
                Painter.set(level, pos + i, Terrain.HIGH_GRASS)
            }
        }
    }
}
