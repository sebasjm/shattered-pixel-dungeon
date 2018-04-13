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

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower
import com.watabou.utils.Random

class SecretGardenRoom : SecretRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.GRASS)

        val grass = Patch.generate(width() - 2, height() - 2, 0.5f, 0, true)
        for (i in top + 1 until bottom) {
            for (j in left + 1 until right) {
                if (grass[xyToPatchCoords(j, i)]) {
                    level.map[i * level.width() + j] = Terrain.HIGH_GRASS
                }
            }
        }

        entrance().set(Room.Door.Type.HIDDEN)

        level.plant(Starflower.Seed(), plantPos(level))
        level.plant(WandOfRegrowth.Seedpod.Seed(), plantPos(level))
        level.plant(WandOfRegrowth.Dewcatcher.Seed(), plantPos(level))

        if (Random.Int(2) == 0) {
            level.plant(WandOfRegrowth.Seedpod.Seed(), plantPos(level))
        } else {
            level.plant(WandOfRegrowth.Dewcatcher.Seed(), plantPos(level))
        }

        var light: Foliage? = level.blobs[Foliage::class.java] as Foliage
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

    protected fun xyToPatchCoords(x: Int, y: Int): Int {
        return x - left - 1 + (y - top - 1) * (width() - 2)
    }
}
