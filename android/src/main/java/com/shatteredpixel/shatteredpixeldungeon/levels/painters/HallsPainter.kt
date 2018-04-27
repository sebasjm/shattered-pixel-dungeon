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

package com.shatteredpixel.shatteredpixeldungeon.levels.painters

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class HallsPainter : RegularPainter() {

    override fun decorate(level: Level, rooms: ArrayList<Room>) {

        val map = level.map
        val w = level.width()
        val l = level.length()

        for (i in w + 1 until l - w - 1) {
            if (map!![i] == Terrain.EMPTY) {

                var count = 0
                for (j in PathFinder.NEIGHBOURS8!!.indices) {
                    if (Terrain.flags[map[i + PathFinder.NEIGHBOURS8!![j]]] and Terrain.PASSABLE > 0) {
                        count++
                    }
                }

                if (Random.Int(80) < count) {
                    map[i] = Terrain.EMPTY_DECO
                }

            } else if (map[i] == Terrain.WALL &&
                    map[i - 1] != Terrain.WALL_DECO && map[i - w] != Terrain.WALL_DECO &&
                    Random.Int(20) == 0) {

                map[i] = Terrain.WALL_DECO

            }
        }
    }
}
