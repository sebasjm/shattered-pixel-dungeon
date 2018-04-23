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

import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass
import com.watabou.utils.Random

class GardenRoom : SpecialRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.HIGH_GRASS)
        Painter.fill(level, this, 2, Terrain.GRASS)

        entrance().set(Room.Door.Type.REGULAR)

        if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
            if (Random.Int(2) == 0) {
                level.plant(Sungrass.Seed(), plantPos(level))
            }
        } else {
            val bushes = Random.Int(3)
            if (bushes == 0) {
                level.plant(Sungrass.Seed(), plantPos(level))
            } else if (bushes == 1) {
                level.plant(BlandfruitBush.Seed(), plantPos(level))
            } else if (Random.Int(5) == 0) {
                level.plant(Sungrass.Seed(), plantPos(level))
                level.plant(BlandfruitBush.Seed(), plantPos(level))
            }
        }

        var light: Foliage? = level.blobs[Foliage::class.java] as Foliage?
        if (light == null) {
            light = Foliage()
        }
        for (i in top + 1 until bottom) {
            for (j in left + 1 until right) {
                light.seed(level, j + level.width() * i, 1)
            }
        }
        level.blobs[Foliage::class.java] = light
    }

    private fun plantPos(level: Level): Int {
        var pos: Int
        do {
            pos = level.pointToCell(random())
        } while (level.plants.get(pos) != null)
        return pos
    }
}
