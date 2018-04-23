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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual
import com.watabou.utils.Point
import com.watabou.utils.Random

class WeakFloorRoom : SpecialRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.CHASM)

        val door = entrance()
        door.set(Room.Door.Type.REGULAR)

        var well: Point? = null

        if (door.x == left) {
            for (i in top + 1 until bottom) {
                Painter.drawInside(level, this, Point(left, i), Random.IntRange(1, width() - 4), Terrain.EMPTY_SP)
            }
            well = Point(right - 1, if (Random.Int(2) == 0) top + 2 else bottom - 1)
        } else if (door.x == right) {
            for (i in top + 1 until bottom) {
                Painter.drawInside(level, this, Point(right, i), Random.IntRange(1, width() - 4), Terrain.EMPTY_SP)
            }
            well = Point(left + 1, if (Random.Int(2) == 0) top + 2 else bottom - 1)
        } else if (door.y == top) {
            for (i in left + 1 until right) {
                Painter.drawInside(level, this, Point(i, top), Random.IntRange(1, height() - 4), Terrain.EMPTY_SP)
            }
            well = Point(if (Random.Int(2) == 0) left + 1 else right - 1, bottom - 1)
        } else if (door.y == bottom) {
            for (i in left + 1 until right) {
                Painter.drawInside(level, this, Point(i, bottom), Random.IntRange(1, height() - 4), Terrain.EMPTY_SP)
            }
            well = Point(if (Random.Int(2) == 0) left + 1 else right - 1, top + 2)
        }

        Painter.set(level, well!!, Terrain.CHASM)
        val vis = HiddenWell()
        vis.pos(well.x, well.y)
        level.customTiles.add(vis)
    }

    class HiddenWell : CustomTiledVisual(Assets.WEAK_FLOOR) {

        override fun create(): CustomTiledVisual {
            tileH = 1
            tileW = tileH
            map(intArrayOf(Dungeon.depth / 5), 1)
            return super.create()
        }

        override fun name(tileX: Int, tileY: Int): String? {
            return Messages.get(this.javaClass, "name")
        }

        override fun desc(tileX: Int, tileY: Int): String? {
            return Messages.get(this.javaClass, "desc")
        }

    }
}
