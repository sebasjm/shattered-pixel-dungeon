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

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.Random

class StripedRoom : StandardRoom() {

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(2f, 1f, 0f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }

        if (sizeCat == StandardRoom.SizeCategory.NORMAL) {
            Painter.fill(level, this, 1, Terrain.EMPTY_SP)
            if (width() > height() || width() == height() && Random.Int(2) == 0) {
                var i = left + 2
                while (i < right) {
                    Painter.fill(level, i, top + 1, 1, height() - 2, Terrain.HIGH_GRASS)
                    i += 2
                }
            } else {
                var i = top + 2
                while (i < bottom) {
                    Painter.fill(level, left + 1, i, width() - 2, 1, Terrain.HIGH_GRASS)
                    i += 2
                }
            }

        } else if (sizeCat == StandardRoom.SizeCategory.LARGE) {
            val layers = (Math.min(width(), height()) - 1) / 2
            for (i in 1..layers) {
                Painter.fill(level, this, i, if (i % 2 == 1) Terrain.EMPTY_SP else Terrain.HIGH_GRASS)
            }
        }
    }
}
