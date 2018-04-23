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

package com.shatteredpixel.shatteredpixeldungeon.levels.builders

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.MazeConnectionRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.LinkedHashSet

//Introduces the concept of a major path, and branches
// with tunnels padding rooms placed in them
abstract class RegularBuilder : Builder() {

    // *** Parameter values for level building logic ***
    // note that implementations do not have to use al of these variables

    protected var pathVariance = 45f

    //path length is the percentage of pathable rooms that are on
    protected var pathLength = 0.5f
    //The chance weights for extra rooms to be added to the path
    protected var pathLenJitterChances = floatArrayOf(0f, 1f, 0f)

    protected var pathTunnelChances = floatArrayOf(1f, 3f, 1f)
    protected var branchTunnelChances = floatArrayOf(2f, 2f, 1f)

    protected var extraConnectionChance = 0.2f

    // *** Room Setup ***

    protected var entrance: Room? = null
    protected var exit: Room? = null
    protected var shop: Room? = null

    protected var multiConnections = ArrayList<Room>()
    protected var singleConnections = ArrayList<Room>()

    fun setPathVariance(`var`: Float): RegularBuilder {
        pathVariance = `var`
        return this
    }

    fun setPathLength(len: Float, jitter: FloatArray): RegularBuilder {
        pathLength = len
        pathLenJitterChances = jitter
        return this
    }

    fun setTunnelLength(path: FloatArray, branch: FloatArray): RegularBuilder {
        pathTunnelChances = path
        branchTunnelChances = branch
        return this
    }

    fun setExtraConnectionChance(chance: Float): RegularBuilder {
        extraConnectionChance = chance
        return this
    }

    protected fun setupRooms(rooms: ArrayList<Room>) {
        for (r in rooms) {
            r.setEmpty()
        }

        shop = null
        exit = shop
        entrance = exit
        singleConnections.clear()
        multiConnections.clear()
        for (r in rooms) {
            if (r is EntranceRoom) {
                entrance = r
            } else if (r is ExitRoom) {
                exit = r
            } else if (r is ShopRoom && r.maxConnections(Room.ALL) == 1) {
                shop = r
            } else if (r.maxConnections(Room.ALL) > 1) {
                multiConnections.add(r)
            } else if (r.maxConnections(Room.ALL) == 1) {
                singleConnections.add(r)
            }
        }

        //this weights larger rooms to be much more likely to appear in the main loop, by placing them earlier in the multiconnections list
        weightRooms(multiConnections)
        Random.shuffle(multiConnections)
        multiConnections = ArrayList(LinkedHashSet(multiConnections))
    }

    // *** Branch Placement ***

    protected fun weightRooms(rooms: ArrayList<Room>) {
        for (r in rooms.toTypedArray<Room>()) {
            if (r is StandardRoom) {
                for (i in 1 until (r as StandardRoom).sizeCat!!.connectionWeight())
                    rooms.add(r)
            }
        }
    }

    //places the rooms in roomsToBranch into branches from rooms in branchable.
    //note that the three arrays should be separate, they may contain the same rooms however
    protected fun createBranches(rooms: ArrayList<Room>, branchable: ArrayList<Room>,
                                 roomsToBranch: ArrayList<Room>, connChances: FloatArray) {

        var i = 0
        var angle: Float
        var tries: Int
        var curr: Room?
        val connectingRoomsThisBranch = ArrayList<Room>()

        var connectionChances = connChances.clone()
        while (i < roomsToBranch.size) {

            val r = roomsToBranch[i]

            connectingRoomsThisBranch.clear()

            do {
                curr = Random.element(branchable)
            } while (r is SecretRoom && curr is ConnectionRoom)

            var connectingRooms = Random.chances(connectionChances)
            if (connectingRooms == -1) {
                connectionChances = connChances.clone()
                connectingRooms = Random.chances(connectionChances)
            }
            connectionChances[connectingRooms]--

            for (j in 0 until connectingRooms) {
                val t = (if (r is SecretRoom) MazeConnectionRoom() else ConnectionRoom.createRoom())!!
                tries = 3

                do {
                    angle = Builder.placeRoom(rooms, curr!!, t, randomBranchAngle(curr))
                    tries--
                } while (angle == -1f && tries > 0)

                if (angle == -1f) {
                    for (c in connectingRoomsThisBranch) {
                        c.clearConnections()
                        rooms.remove(c)
                    }
                    connectingRoomsThisBranch.clear()
                    break
                } else {
                    connectingRoomsThisBranch.add(t)
                    rooms.add(t)
                }

                curr = t
            }

            if (connectingRoomsThisBranch.size != connectingRooms) {
                continue
            }

            tries = 10

            do {
                angle = Builder.placeRoom(rooms, curr!!, r, randomBranchAngle(curr!!))
                tries--
            } while (angle == -1f && tries > 0)

            if (angle == -1f) {
                for (t in connectingRoomsThisBranch) {
                    t.clearConnections()
                    rooms.remove(t)
                }
                connectingRoomsThisBranch.clear()
                continue
            }

            for (j in connectingRoomsThisBranch.indices) {
                if (Random.Int(3) <= 1) branchable.add(connectingRoomsThisBranch[j])
            }
            if (r.maxConnections(Room.ALL) > 1 && Random.Int(3) == 0) {
                if (r is StandardRoom) {
                    for (j in 0 until r.sizeCat!!.connectionWeight()) {
                        branchable.add(r)
                    }
                } else {
                    branchable.add(r)
                }
            }

            i++
        }
    }

    protected open fun randomBranchAngle(r: Room): Float {
        return Random.Float(360f)
    }

}
