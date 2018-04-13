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

class SkullsRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(7, super.minWidth())
    }

    override fun minHeight(): Int {
        return Math.max(7, super.minHeight())
    }

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(9f, 3f, 1f)
    }

    override fun paint(level: Level) {

        val minDim = Math.min(width(), height())

        Painter.fill(level, this, Terrain.WALL)

        if (minDim >= 9) {
            Painter.fillEllipse(level, this, 2, Terrain.EMPTY)
        } else {
            Painter.fill(level, this, 2, Terrain.EMPTY)
        }

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
            if (door.x == left || door.x == right) {
                Painter.drawInside(level, this, door, (width() - 3) / 2, Terrain.EMPTY)
            } else {
                Painter.drawInside(level, this, door, (height() - 3) / 2, Terrain.EMPTY)
            }
        }

        val oddWidth = width() % 2 == 1
        val oddHeight = height() % 2 == 1

        if (minDim >= 12) {

            Painter.fillEllipse(level, this, 5, Terrain.STATUE)
            Painter.fillEllipse(level, this, 6, Terrain.WALL)

        } else {

            Painter.fill(level,
                    left + width() / 2 + if (oddWidth) 0 else -1,
                    top + height() / 2 + if (oddHeight) 0 else -1,
                    if (oddWidth) 1 else 2,
                    if (oddHeight) 1 else 2,
                    Terrain.STATUE)

        }

    }
}
