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

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point
import com.watabou.utils.Random

class SecretWellRoom : SecretRoom() {

    override fun canConnect(p: Point): Boolean {
        //refuses connections next to corners
        return super.canConnect(p) && (p.x > left + 1 && p.x < right - 1 || p.y > top + 1 && p.y < bottom - 1)
    }

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        val door = entrance()
        val well: Point
        if (door.x == left) {
            well = Point(right - 2, door.y)
        } else if (door.x == right) {
            well = Point(left + 2, door.y)
        } else if (door.y == top) {
            well = Point(door.x, bottom - 2)
        } else {
            well = Point(door.x, top + 2)
        }

        Painter.fill(level, well.x - 1, well.y - 1, 3, 3, Terrain.CHASM)
        Painter.drawLine(level, door, well, Terrain.EMPTY)

        Painter.set(level, well, Terrain.WELL)

        val waterClass = Random.element(WATERS) as Class<out WellWater>

        Blob.seed(well.x + level.width() * well.y, 1, waterClass, level)

        entrance().set(Room.Door.Type.HIDDEN)
    }

    companion object {

        private val WATERS = arrayOf<Class<*>>(WaterOfAwareness::class.java, WaterOfHealth::class.java, WaterOfTransmutation::class.java)
    }
}
