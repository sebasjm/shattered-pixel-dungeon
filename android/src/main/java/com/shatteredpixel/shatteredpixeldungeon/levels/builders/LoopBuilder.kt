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
import com.watabou.utils.PointF
import com.watabou.utils.Random

import java.util.ArrayList

//A builder with one core loop as its primary element
class LoopBuilder : RegularBuilder() {

    //These methods allow for the adjusting of the shape of the loop
    //by default the loop is a perfect circle, but it can be adjusted

    //increasing the exponent will increase the the curvature, making the loop more oval shaped.
    private var curveExponent = 0

    //This is a percentage (range 0-1) of the intensity of the curve function
    // 0 makes for a perfect linear curve (circle)
    // 1 means the curve is completely determined by the curve exponent
    private var curveIntensity = 1f

    //Adjusts the starting point along the loop.
    // a common example, setting to 0.25 will make for a short fat oval instead of a long one.
    private var curveOffset = 0f

    private var loopCenter: PointF? = null

    fun setLoopShape(exponent: Int, intensity: Float, offset: Float): LoopBuilder {
        this.curveExponent = Math.abs(exponent)
        curveIntensity = intensity % 1f
        curveOffset = offset % 0.5f
        return this
    }

    private fun targetAngle(percentAlong: Float): Float {
        var percentAlong = percentAlong
        percentAlong += curveOffset
        return 360f * (curveIntensity * curveEquation(percentAlong.toDouble()) + (1 - curveIntensity) * percentAlong - curveOffset).toFloat()
    }

    private fun curveEquation(x: Double): Double {
        return (Math.pow(4.0, (2 * curveExponent).toDouble()) * Math.pow(x % 0.5f - 0.25, (2 * curveExponent + 1).toDouble())
                + 0.25 + 0.5 * Math.floor(2 * x))
    }

    override fun build(rooms: ArrayList<Room>): ArrayList<Room>? {

        setupRooms(rooms)

        if (entrance == null) {
            return null
        }

        entrance!!.setSize()
        entrance!!.setPos(0, 0)

        val startAngle = Random.Float(0f, 360f)

        val loop = ArrayList<Room>()
        var roomsOnLoop = (multiConnections.size * pathLength).toInt() + Random.chances(pathLenJitterChances)
        roomsOnLoop = Math.min(roomsOnLoop, multiConnections.size)
        roomsOnLoop++

        var pathTunnels = pathTunnelChances.clone()
        for (i in 0 until roomsOnLoop) {
            if (i == 0)
                loop.add(entrance!!)
            else
                loop.add(multiConnections.removeAt(0))

            var tunnels = Random.chances(pathTunnels)
            if (tunnels == -1) {
                pathTunnels = pathTunnelChances.clone()
                tunnels = Random.chances(pathTunnels)
            }
            pathTunnels[tunnels]--

            for (j in 0 until tunnels) {
                loop.add(ConnectionRoom.createRoom()!!)
            }
        }

        if (exit != null) loop.add((loop.size + 1) / 2, exit!!)

        var prev = entrance
        var targetAngle: Float
        for (i in 1 until loop.size) {
            val r = loop[i]
            targetAngle = startAngle + targetAngle(i / loop.size.toFloat())
            if (Builder.placeRoom(rooms, prev!!, r, targetAngle) != -1f) {
                prev = r
                if (!rooms.contains(prev))
                    rooms.add(prev)
            } else {
                //FIXME this is lazy, there are ways to do this without relying on chance
                return null
            }
        }

        //FIXME this is still fairly chance reliant
        // should just write a general function for stitching two rooms together in builder
        while (!prev!!.connect(entrance!!)) {

            val c = ConnectionRoom.createRoom()
            if (Builder.placeRoom(loop, prev, c!!, Builder.angleBetweenRooms(prev, entrance!!)) == -1f) {
                return null
            }
            loop.add(c)
            rooms.add(c)
            prev = c
        }

        loopCenter = PointF()
        for (r in loop) {
            loopCenter!!.x += (r.left + r.right) / 2f
            loopCenter!!.y += (r.top + r.bottom) / 2f
        }
        loopCenter!!.x /= loop.size.toFloat()
        loopCenter!!.y /= loop.size.toFloat()

        if (shop != null) {
            var angle: Float
            var tries = 10
            do {
                angle = Builder.placeRoom(loop, entrance!!, shop!!, Random.Float(360f))
                tries--
            } while (angle == -1f && tries >= 0)
            if (angle == -1f) return null
        }

        val branchable = ArrayList(loop)

        val roomsToBranch = ArrayList<Room>()
        roomsToBranch.addAll(multiConnections)
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

    override fun randomBranchAngle(r: Room): Float {
        if (loopCenter == null)
            return super.randomBranchAngle(r)
        else {
            //generate four angles randomly and return the one which points closer to the center
            var toCenter = Builder.angleBetweenPoints(PointF((r.left + r.right) / 2f, (r.top + r.bottom) / 2f), loopCenter!!)
            if (toCenter < 0) toCenter += 360f

            var currAngle = Random.Float(360f)
            for (i in 0..3) {
                val newAngle = Random.Float(360f)
                if (Math.abs(toCenter - newAngle) < Math.abs(toCenter - currAngle)) {
                    currAngle = newAngle
                }
            }
            return currAngle
        }
    }
}
