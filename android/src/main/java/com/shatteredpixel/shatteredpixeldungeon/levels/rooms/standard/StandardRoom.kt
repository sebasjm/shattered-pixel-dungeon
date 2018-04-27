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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.noosa.Game
import com.watabou.utils.Random

import java.util.ArrayList

abstract class StandardRoom : Room() {

    var sizeCat: SizeCategory? = null

    enum class SizeCategory private constructor(val minDim: Int, val maxDim: Int, val roomValue: Int) {

        NORMAL(4, 10, 1),
        LARGE(10, 14, 2),
        GIANT(14, 18, 3);

        fun connectionWeight(): Int {
            return roomValue * roomValue
        }

    }

    init {
        setSizeCat2()
    }

    //Note that if a room wishes to allow itself to be forced to a certain size category,
    //but would (effectively) never roll that size category, consider using Float.MIN_VALUE
    open fun sizeCatProbs(): FloatArray {
        //always normal by default
        return floatArrayOf(1f, 0f, 0f)
    }

    //assumes room value is always ordinal+1
    fun setSizeCat(maxRoomValue: Int): Boolean {
        return setSizeCat2(0, maxRoomValue - 1)
    }

    //returns false if size cannot be set
    @JvmOverloads
    fun setSizeCat2(minOrdinal: Int = 0, maxOrdinal: Int = SizeCategory.values().size - 1): Boolean {
        val probs = sizeCatProbs()
        val categories = SizeCategory.values()

        if (probs.size != categories.size) return false

        for (i in 0 until minOrdinal) probs[i] = 0f
        for (i in maxOrdinal + 1 until categories.size) probs[i] = 0f

        val ordinal = Random.chances(probs)

        if (ordinal != -1) {
            sizeCat = categories[ordinal]
            return true
        } else {
            return false
        }
    }

    override fun minWidth(): Int {
        return sizeCat!!.minDim
    }

    override fun maxWidth(): Int {
        return sizeCat!!.maxDim
    }

    override fun minHeight(): Int {
        return sizeCat!!.minDim
    }

    override fun maxHeight(): Int {
        return sizeCat!!.maxDim
    }

    override fun minConnections(direction: Int): Int {
        return if (direction == Room.ALL)
            1
        else
            0
    }

    override fun maxConnections(direction: Int): Int {
        return if (direction == Room.ALL)
            16
        else
            4
    }

    companion object {

        //FIXME this is a very messy way of handing variable standard rooms
        private val rooms = ArrayList<Class<out StandardRoom>>()

        init {
            rooms.add(EmptyRoom::class.java)


            rooms.add(SewerPipeRoom::class.java)
            rooms.add(RingRoom::class.java)

            rooms.add(SegmentedRoom::class.java)
            rooms.add(StatuesRoom::class.java)

            rooms.add(CaveRoom::class.java)
            rooms.add(CirclePitRoom::class.java)

            rooms.add(HallwayRoom::class.java)
            rooms.add(PillarsRoom::class.java)

            rooms.add(RuinsRoom::class.java)
            rooms.add(SkullsRoom::class.java)


            rooms.add(PlantsRoom::class.java)
            rooms.add(AquariumRoom::class.java)
            rooms.add(PlatformRoom::class.java)
            rooms.add(BurnedRoom::class.java)
            rooms.add(FissureRoom::class.java)
            rooms.add(GrassyGraveRoom::class.java)
            rooms.add(StripedRoom::class.java)
            rooms.add(StudyRoom::class.java)
            rooms.add(SuspiciousChestRoom::class.java)
            rooms.add(MinefieldRoom::class.java)
        }

        private val chances = arrayOfNulls<FloatArray>(27)

        init {
            chances[1] = floatArrayOf(20f, 15f, 5f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 0f, 1f, 0f, 1f, 1f, 0f, 0f)
            chances[2] = floatArrayOf(20f, 15f, 5f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
            chances[3] = chances[2]
            chances[4] = chances[3]
            chances[5] = floatArrayOf(50f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

            chances[6] = floatArrayOf(20f, 0f, 0f, 15f, 5f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
            chances[7] = chances[6]
            chances[8] = chances[7]
            chances[9] = chances[8]
            chances[10] = chances[9]

            chances[11] = floatArrayOf(20f, 0f, 0f, 0f, 0f, 15f, 5f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
            chances[12] = chances[11]
            chances[13] = chances[12]
            chances[14] = chances[13]
            chances[15] = chances[14]

            chances[16] = floatArrayOf(20f, 0f, 0f, 0f, 0f, 0f, 0f, 15f, 5f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
            chances[17] = chances[16]
            chances[18] = chances[17]
            chances[19] = chances[18]
            chances[20] = chances[19]

            chances[21] = chances[5]

            chances[22] = floatArrayOf(20f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 15f, 5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
            chances[23] = chances[22]
            chances[24] = chances[23]
            chances[25] = chances[24]
            chances[26] = chances[25]
        }


        fun createRoom(): StandardRoom? {
            try {
                return rooms[Random.chances(chances[Dungeon.depth]!!)].newInstance()
            } catch (e: Exception) {
                Game.reportException(e)
                return null
            }

        }
    }

}
