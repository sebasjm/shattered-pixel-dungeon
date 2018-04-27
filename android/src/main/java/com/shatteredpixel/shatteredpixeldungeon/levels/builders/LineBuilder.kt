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
import com.watabou.utils.Random

import java.util.ArrayList

//A simple builder which utilizes a line as its core feature.
class LineBuilder : RegularBuilder() {

    override fun build(rooms: ArrayList<Room>): ArrayList<Room>? {

        setupRooms(rooms)

        if (entrance == null) {
            return null
        }

        val direction = Random.Float(0f, 360f)
        val branchable = ArrayList<Room>()

        entrance!!.setSize()
        entrance!!.setPos(0, 0)
        branchable.add(entrance!!)

        if (shop != null) {
            Builder.placeRoom(rooms, entrance!!, shop!!, direction + 180f)
        }

        var roomsOnPath = (multiConnections.size * pathLength).toInt() + Random.chances(pathLenJitterChances)
        roomsOnPath = Math.min(roomsOnPath, multiConnections.size)

        var curr = entrance!!

        var pathTunnels = pathTunnelChances.clone()
        for (i in 0..roomsOnPath) {
            if (i == roomsOnPath && exit == null)
                continue

            var tunnels = Random.chances(pathTunnels)
            if (tunnels == -1) {
                pathTunnels = pathTunnelChances.clone()
                tunnels = Random.chances(pathTunnels)
            }
            pathTunnels[tunnels]--

            for (j in 0 until tunnels) {
                val t = ConnectionRoom.createRoom()!!
                Builder.placeRoom(rooms, curr, t, direction + Random.Float(-pathVariance, pathVariance))
                branchable.add(t)
                rooms.add(t)
                curr = t
            }

            val r = if (i == roomsOnPath) exit!! else multiConnections[i]
            Builder.placeRoom(rooms, curr, r, direction + Random.Float(-pathVariance, pathVariance))
            branchable.add(r)
            curr = r
        }

        val roomsToBranch = ArrayList<Room>()
        for (i in roomsOnPath until multiConnections.size) {
            roomsToBranch.add(multiConnections[i])
        }
        roomsToBranch.addAll(singleConnections)
        weightRooms(branchable)
        createBranches(rooms, branchable, roomsToBranch, branchTunnelChances)

        Builder.findNeighbours(rooms)

        for (r in rooms) {
            for (n in r.neigbours) {
                if (!n.connected.containsKey(r) && Random.Float() < extraConnectionChance) {
                    r.connect(n)
                }
            }
        }

        return rooms

    }

}
