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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.noosa.Game
import com.watabou.utils.Point
import com.watabou.utils.Random

import java.util.ArrayList

abstract class ConnectionRoom : Room() {

    override fun minWidth(): Int {
        return 3
    }

    override fun maxWidth(): Int {
        return 10
    }

    override fun minHeight(): Int {
        return 3
    }

    override fun maxHeight(): Int {
        return 10
    }

    override fun minConnections(direction: Int): Int {
        return if (direction == Room.ALL)
            2
        else
            0
    }

    override fun maxConnections(direction: Int): Int {
        return if (direction == Room.ALL)
            16
        else
            4
    }

    override fun canPlaceTrap(p: Point): Boolean {
        //traps cannot appear in connection rooms on floor 1
        return super.canPlaceTrap(p) && Dungeon.depth > 1
    }

    companion object {

        //FIXME this is a very messy way of handing variable connection rooms
        private val rooms = ArrayList<Class<out ConnectionRoom>>()

        init {
            rooms.add(TunnelRoom::class.java)
            rooms.add(BridgeRoom::class.java)

            rooms.add(PerimeterRoom::class.java)
            rooms.add(WalkwayRoom::class.java)

            rooms.add(RingTunnelRoom::class.java)
            rooms.add(RingBridgeRoom::class.java)
        }

        private val chances = arrayOfNulls<FloatArray>(27)

        init {
            chances[1] = floatArrayOf(20f, 1f, 0f, 2f, 2f, 1f)
            chances[2] = chances[1]
            chances[3] = chances[2]
            chances[4] = chances[3]
            chances[5] = floatArrayOf(18f, 0f, 0f, 0f, 7f, 0f)

            chances[6] = floatArrayOf(0f, 0f, 22f, 3f, 0f, 0f)
            chances[7] = chances[6]
            chances[8] = chances[7]
            chances[9] = chances[8]
            chances[10] = chances[9]

            chances[11] = floatArrayOf(12f, 0f, 0f, 5f, 5f, 3f)
            chances[12] = chances[11]
            chances[13] = chances[12]
            chances[14] = chances[13]
            chances[15] = chances[14]

            chances[16] = floatArrayOf(0f, 0f, 18f, 3f, 3f, 1f)
            chances[17] = chances[16]
            chances[18] = chances[17]
            chances[19] = chances[18]
            chances[20] = chances[19]

            chances[21] = chances[5]

            chances[22] = floatArrayOf(15f, 4f, 0f, 2f, 3f, 2f)
            chances[23] = chances[22]
            chances[24] = chances[23]
            chances[25] = chances[24]
            chances[26] = chances[25]
        }

        fun createRoom(): ConnectionRoom? {
            try {
                return rooms[Random.chances(chances[Dungeon.depth]!!)].newInstance()
            } catch (e: Exception) {
                Game.reportException(e)
                return null
            }

        }
    }
}
