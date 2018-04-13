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

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfTransmutation
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.Point
import com.watabou.utils.Random

class MagicWellRoom : SpecialRoom() {

    var overrideWater: Class<out WellWater>? = null

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        val c = center()
        Painter.set(level, c.x, c.y, Terrain.WELL)

        val waterClass = if (overrideWater != null)
            overrideWater
        else
            Random.element(WATERS) as Class<out WellWater>

        if (waterClass == WaterOfTransmutation::class.java) {
            SpecialRoom.disableGuaranteedWell()
        }

        WellWater.seed<out WellWater>(c.x + level.width() * c.y, 1, waterClass, level)

        entrance().set(Room.Door.Type.REGULAR)
    }

    companion object {

        private val WATERS = arrayOf<Class<*>>(WaterOfAwareness::class.java, WaterOfHealth::class.java, WaterOfTransmutation::class.java)
    }
}
