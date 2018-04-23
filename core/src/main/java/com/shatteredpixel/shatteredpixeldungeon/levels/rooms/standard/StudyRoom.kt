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
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point
import com.watabou.utils.Random

class StudyRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(super.minWidth(), 7)
    }

    override fun minHeight(): Int {
        return Math.max(super.minHeight(), 7)
    }

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(2f, 1f, 0f)
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.BOOKSHELF)
        Painter.fill(level, this, 2, Terrain.EMPTY_SP)

        for (door in connected.values.filterNotNull()) {
            Painter.drawInside(level, this, door, 2, Terrain.EMPTY_SP)
            door.set(Room.Door.Type.REGULAR)
        }

        //TODO add support for giant size as well
        if (sizeCat == StandardRoom.SizeCategory.LARGE) {
            val pillarW = (width() - 7) / 2
            val pillarH = (height() - 7) / 2

            Painter.fill(level, left + 3, top + 3, pillarW, 1, Terrain.BOOKSHELF)
            Painter.fill(level, left + 3, top + 3, 1, pillarH, Terrain.BOOKSHELF)

            Painter.fill(level, left + 3, bottom - 2 - 1, pillarW, 1, Terrain.BOOKSHELF)
            Painter.fill(level, left + 3, bottom - 2 - pillarH, 1, pillarH, Terrain.BOOKSHELF)

            Painter.fill(level, right - 2 - pillarW, top + 3, pillarW, 1, Terrain.BOOKSHELF)
            Painter.fill(level, right - 2 - 1, top + 3, 1, pillarH, Terrain.BOOKSHELF)

            Painter.fill(level, right - 2 - pillarW, bottom - 2 - 1, pillarW, 1, Terrain.BOOKSHELF)
            Painter.fill(level, right - 2 - 1, bottom - 2 - pillarH, 1, pillarH, Terrain.BOOKSHELF)
        }

        val center = center()
        Painter.set(level, center, Terrain.PEDESTAL)

        val prize = if (Random.Int(2) == 0) level.findPrizeItem() else null

        if (prize != null) {
            level.drop(prize, center.x + center.y * level.width())
        } else {
            level.drop(Generator.random(Random.oneOf<Generator.Category>(
                    Generator.Category.POTION,
                    Generator.Category.SCROLL)), center.x + center.y * level.width())
        }
    }
}
