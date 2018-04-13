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

import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.watabou.utils.Point
import com.watabou.utils.Random

class PlantsRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(super.minWidth(), 5)
    }

    override fun minHeight(): Int {
        return Math.max(super.minHeight(), 5)
    }

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(3f, 1f, 0f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.GRASS)
        Painter.fill(level, this, 2, Terrain.HIGH_GRASS)

        if (Math.min(width(), height()) >= 7) {
            Painter.fill(level, this, 3, Terrain.GRASS)
        }

        val center = center()

        //place at least 2 plants for rooms with at least 9 in one dimensions
        if (Math.max(width(), height()) >= 9) {

            //place 4 plants for very large rooms
            if (Math.min(width(), height()) >= 11) {
                Painter.drawLine(level, Point(left + 2, center.y), Point(right - 2, center.y), Terrain.HIGH_GRASS)
                Painter.drawLine(level, Point(center.x, top + 2), Point(center.x, bottom - 2), Terrain.HIGH_GRASS)
                level.plant(randomSeed(), level.pointToCell(Point(center.x - 1, center.y - 1)))
                level.plant(randomSeed(), level.pointToCell(Point(center.x + 1, center.y - 1)))
                level.plant(randomSeed(), level.pointToCell(Point(center.x - 1, center.y + 1)))
                level.plant(randomSeed(), level.pointToCell(Point(center.x + 1, center.y + 1)))

                //place 2 plants otherwise
                //left/right
            } else if (width() > height() || width() == height() && Random.Int(2) == 0) {
                Painter.drawLine(level, Point(center.x, top + 2), Point(center.x, bottom - 2), Terrain.HIGH_GRASS)
                level.plant(randomSeed(), level.pointToCell(Point(center.x - 1, center.y)))
                level.plant(randomSeed(), level.pointToCell(Point(center.x + 1, center.y)))

                //top/bottom
            } else {
                Painter.drawLine(level, Point(left + 2, center.y), Point(right - 2, center.y), Terrain.HIGH_GRASS)
                level.plant(randomSeed(), level.pointToCell(Point(center.x, center.y - 1)))
                level.plant(randomSeed(), level.pointToCell(Point(center.x, center.y + 1)))

            }

            //place just one plant for smaller sized rooms
        } else {
            level.plant(randomSeed(), level.pointToCell(center))
        }

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }

        super.paint(level)
    }

    private fun randomSeed(): Plant.Seed {
        var result: Plant.Seed
        do {
            result = Generator.random(Generator.Category.SEED) as Plant.Seed
        } while (result is Firebloom.Seed)
        return result
    }
}
