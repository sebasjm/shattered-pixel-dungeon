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
import com.watabou.utils.Random

import java.util.ArrayList

//A builder that creates only branches, very simple and very random
class BranchesBuilder : RegularBuilder() {

    override fun build(rooms: ArrayList<Room>): ArrayList<Room>? {

        setupRooms(rooms)

        if (entrance == null) {
            return null
        }

        val branchable = ArrayList<Room>()

        entrance!!.setSize()
        entrance!!.setPos(0, 0)
        branchable.add(entrance)

        if (shop != null) {
            Builder.placeRoom(branchable, entrance, shop, Random.Float(360f))
        }

        val roomsToBranch = ArrayList<Room>()
        roomsToBranch.addAll(multiConnections)
        if (exit != null) roomsToBranch.add(exit)
        roomsToBranch.addAll(singleConnections)
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
