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

import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Maze
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.PathFinder
import com.watabou.utils.Point
import com.watabou.utils.Random

class SecretMazeRoom : SecretRoom() {

    override fun minWidth(): Int {
        return 14
    }

    override fun minHeight(): Int {
        return 14
    }

    override fun maxWidth(): Int {
        return 18
    }

    override fun maxHeight(): Int {
        return 18
    }

    override fun paint(level: Level) {
        super.paint(level)

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        //true = space, false = wall
        val maze = Maze.generate(this)
        val passable = BooleanArray(width() * height())

        Painter.fill(level, this, 1, Terrain.EMPTY)
        for (x in maze.indices) {
            for (y in 0 until maze[0].size) {
                if (maze[x][y] == Maze.FILLED) {
                    Painter.fill(level, x + left, y + top, 1, 1, Terrain.WALL)
                }
                passable[x + width() * y] = maze[x][y] == Maze.EMPTY
            }
        }

        PathFinder.setMapSize(width(), height())
        val entrance = entrance()
        val entrancePos = entrance.x - left + width() * (entrance.y - top)

        PathFinder.buildDistanceMap(entrancePos, passable)

        var bestDist = 0
        val bestDistP = Point()
        for (i in PathFinder.distance.indices) {
            if (PathFinder.distance[i] != Integer.MAX_VALUE && PathFinder.distance[i] > bestDist) {
                bestDist = PathFinder.distance[i]
                bestDistP.x = i % width() + left
                bestDistP.y = i / width() + top
            }
        }

        var prize: Item?
        //1 floor set higher in probability, never cursed
        do {
            if (Random.Int(2) == 0) {
                prize = Generator.randomWeapon(Dungeon.depth / 5 + 1)
            } else {
                prize = Generator.randomArmor(Dungeon.depth / 5 + 1)
            }
        } while (prize!!.cursed || Challenges.isItemBlocked(prize))

        //33% chance for an extra update.
        if (Random.Int(3) == 0) {
            prize.upgrade()
        }

        level.drop(prize, level.pointToCell(bestDistP)).type = Heap.Type.CHEST

        PathFinder.setMapSize(level.width(), level.height())

        entrance().set(Room.Door.Type.HIDDEN)
    }
}
