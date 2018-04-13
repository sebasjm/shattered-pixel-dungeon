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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfTransmutation
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

open class SpecialRoom : Room() {

    override fun minWidth(): Int {
        return 5
    }

    override fun maxWidth(): Int {
        return 10
    }

    override fun minHeight(): Int {
        return 5
    }

    override fun maxHeight(): Int {
        return 10
    }

    override fun minConnections(direction: Int): Int {
        return if (direction == Room.ALL)
            1
        else
            0
    }

    override fun maxConnections(direction: Int): Int {
        return 1
    }

    open fun entrance(): Room.Door {
        return connected.values.iterator().next()
    }

    companion object {

        private val ALL_SPEC = ArrayList(Arrays.asList<Class<out SpecialRoom>>(
                WeakFloorRoom::class.java, MagicWellRoom::class.java, CryptRoom::class.java, PoolRoom::class.java, GardenRoom::class.java, LibraryRoom::class.java, ArmoryRoom::class.java,
                TreasuryRoom::class.java, TrapsRoom::class.java, StorageRoom::class.java, StatueRoom::class.java, LaboratoryRoom::class.java, VaultRoom::class.java
        ))

        var runSpecials = ArrayList<Class<out Room>>()
        var floorSpecials = ArrayList<Class<out Room>>()

        private var pitNeededDepth = -1
        private var guaranteedWellDepth = Integer.MAX_VALUE

        fun initForRun() {
            runSpecials = ALL_SPEC.clone() as ArrayList<Class<out Room>>

            pitNeededDepth = -1
            guaranteedWellDepth = Random.IntRange(6, 14)
            Random.shuffle(runSpecials)
        }

        fun initForFloor() {
            //laboratory rooms are more common
            val labIdx = runSpecials.indexOf(LaboratoryRoom::class.java)
            if (labIdx > 0) {
                Collections.swap(runSpecials, labIdx - 1, labIdx)
            }

            floorSpecials = runSpecials.clone() as ArrayList<Class<out Room>>
        }

        private fun useType(type: Class<out Room>) {
            if (runSpecials.remove(type)) {
                floorSpecials.remove(type)
                runSpecials.add(type)
            }
        }

        fun resetPitRoom(depth: Int) {
            if (pitNeededDepth == depth) pitNeededDepth = -1
        }

        fun disableGuaranteedWell() {
            guaranteedWellDepth = Integer.MAX_VALUE
        }

        fun createRoom(): SpecialRoom {
            if (Dungeon.depth == pitNeededDepth) {
                pitNeededDepth = -1

                floorSpecials.remove(ArmoryRoom::class.java)
                floorSpecials.remove(CryptRoom::class.java)
                floorSpecials.remove(LaboratoryRoom::class.java)
                floorSpecials.remove(LibraryRoom::class.java)
                floorSpecials.remove(StatueRoom::class.java)
                floorSpecials.remove(TreasuryRoom::class.java)
                floorSpecials.remove(VaultRoom::class.java)
                floorSpecials.remove(WeakFloorRoom::class.java)

                return PitRoom()

            } else if (Dungeon.depth >= guaranteedWellDepth) {
                useType(MagicWellRoom::class.java)

                val r = MagicWellRoom()
                r.overrideWater = WaterOfTransmutation::class.java
                guaranteedWellDepth = Integer.MAX_VALUE
                return r

            } else {

                if (Dungeon.bossLevel(Dungeon.depth + 1)) {
                    floorSpecials.remove(WeakFloorRoom::class.java)
                }

                var r: Room? = null
                var index = floorSpecials.size
                for (i in 0..3) {
                    val newidx = Random.Int(floorSpecials.size)
                    if (newidx < index) index = newidx
                }
                try {
                    r = floorSpecials[index].newInstance()
                } catch (e: Exception) {
                    ShatteredPixelDungeon.reportException(e)
                }

                if (r is WeakFloorRoom) {
                    pitNeededDepth = Dungeon.depth + 1
                }

                useType(r!!.javaClass)
                return r as SpecialRoom?

            }
        }

        private val ROOMS = "special_rooms"
        private val PIT = "pit_needed"
        private val WELL = "guaranteed_well"

        fun restoreRoomsFromBundle(bundle: Bundle) {
            runSpecials.clear()
            if (bundle.contains(ROOMS)) {
                for (type in bundle.getClassArray(ROOMS)!!) {
                    if (type != null) runSpecials.add(type)
                }
            } else {
                initForRun()
                ShatteredPixelDungeon.reportException(Exception("specials array didn't exist!"))
            }
            pitNeededDepth = bundle.getInt(PIT)
            guaranteedWellDepth = bundle.getInt(WELL)
        }

        fun storeRoomsInBundle(bundle: Bundle) {
            bundle.put(ROOMS, runSpecials.toTypedArray<Class<*>>())
            bundle.put(PIT, pitNeededDepth)
            bundle.put(WELL, guaranteedWellDepth)
        }
    }
}
