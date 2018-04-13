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

package com.shatteredpixel.shatteredpixeldungeon.levels.painters

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap
import com.watabou.utils.Graph
import com.watabou.utils.PathFinder
import com.watabou.utils.Point
import com.watabou.utils.Random
import com.watabou.utils.Rect

import java.util.ArrayList

abstract class RegularPainter : Painter() {

    private var waterFill = 0f
    private var waterSmoothness: Int = 0

    private var grassFill = 0f
    private var grassSmoothness: Int = 0

    private var nTraps = 0
    private var trapClasses: Array<Class<out Trap>>? = null
    private var trapChances: FloatArray? = null

    fun setWater(fill: Float, smoothness: Int): RegularPainter {
        waterFill = fill
        waterSmoothness = smoothness
        return this
    }

    fun setGrass(fill: Float, smoothness: Int): RegularPainter {
        grassFill = fill
        grassSmoothness = smoothness
        return this
    }

    fun setTraps(num: Int, classes: Array<Class<*>>, chances: FloatArray): RegularPainter {
        nTraps = num
        trapClasses = classes as Array<Class<out Trap>>
        trapChances = chances
        return this
    }

    override fun paint(level: Level, rooms: ArrayList<Room>?): Boolean {
        var rooms = rooms

        //painter can be used without rooms
        if (rooms != null) {

            val padding = if (level.feeling == Level.Feeling.CHASM) 2 else 1

            var leftMost = Integer.MAX_VALUE
            var topMost = Integer.MAX_VALUE

            for (r in rooms) {
                if (r.left < leftMost) leftMost = r.left
                if (r.top < topMost) topMost = r.top
            }

            leftMost -= padding
            topMost -= padding

            var rightMost = 0
            var bottomMost = 0

            for (r in rooms) {
                r.shift(-leftMost, -topMost)
                if (r.right > rightMost) rightMost = r.right
                if (r.bottom > bottomMost) bottomMost = r.bottom
            }

            rightMost += padding
            bottomMost += padding

            //add 1 to account for 0 values
            level.setSize(rightMost + 1, bottomMost + 1)
        } else {
            //check if the level's size was already initialized by something else
            if (level.length() == 0) return false

            //easier than checking for null everywhere
            rooms = ArrayList()
        }

        Random.shuffle(rooms)

        for (r in rooms) {
            placeDoors(r)
            r.paint(level)
        }

        paintDoors(level, rooms)

        if (waterFill > 0f) {
            paintWater(level, rooms)
        }

        if (grassFill > 0f) {
            paintGrass(level, rooms)
        }

        if (nTraps > 0) {
            paintTraps(level, rooms)
        }

        decorate(level, rooms)

        return true
    }

    protected abstract fun decorate(level: Level, rooms: ArrayList<Room>)

    private fun placeDoors(r: Room) {
        for (n in r.connected.keys) {
            var door: Room.Door? = r.connected[n]
            if (door == null) {

                val i = r.intersect(n)
                val doorSpots = ArrayList<Point>()
                for (p in i.points) {
                    if (r.canConnect(p) && n.canConnect(p))
                        doorSpots.add(p)
                }
                if (doorSpots.isEmpty()) {
                    ShatteredPixelDungeon.reportException(
                            RuntimeException("Could not place a door! " +
                                    "r=" + r.javaClass.getSimpleName() +
                                    " n=" + n.javaClass.getSimpleName()))
                    continue
                }
                door = Room.Door(Random.element(doorSpots))

                r.connected[n] = door
                n.connected[r] = door
            }
        }
    }

    protected fun paintDoors(l: Level, rooms: ArrayList<Room>) {
        for (r in rooms) {
            for (n in r.connected.keys) {

                if (joinRooms(l, r, n)) {
                    continue
                }

                val d = r.connected[n]
                val door = d.x + d.y * l.width()

                if (d.type == Room.Door.Type.REGULAR) {
                    //chance for a hidden door scales from 3/21 on floor 2 to 3/3 on floor 20
                    if (Dungeon.depth > 1 && (Dungeon.depth >= 20 || Random.Int(23 - Dungeon.depth) < Dungeon.depth)) {
                        d.type = Room.Door.Type.HIDDEN
                        Graph.buildDistanceMap(rooms, r)
                        //don't hide if it would make this room only accessible by hidden doors
                        if (n.distance == Integer.MAX_VALUE) {
                            d.type = Room.Door.Type.UNLOCKED
                        }
                    } else {
                        d.type = Room.Door.Type.UNLOCKED
                    }
                }

                when (d.type) {
                    Room.Door.Type.EMPTY -> l.map[door] = Terrain.EMPTY
                    Room.Door.Type.TUNNEL -> l.map[door] = l.tunnelTile()
                    Room.Door.Type.UNLOCKED -> l.map[door] = Terrain.DOOR
                    Room.Door.Type.HIDDEN -> l.map[door] = Terrain.SECRET_DOOR
                    Room.Door.Type.BARRICADE -> l.map[door] = Terrain.BARRICADE
                    Room.Door.Type.LOCKED -> l.map[door] = Terrain.LOCKED_DOOR
                }
            }
        }
    }

    protected fun joinRooms(l: Level, r: Room, n: Room): Boolean {

        if (!(r is EmptyRoom && n is EmptyRoom)) {
            return false
        }

        //TODO decide on good probabilities and dimension restrictions
        val w = r.intersect(n)
        if (w.left == w.right) {

            if (w.bottom - w.top < 3) {
                return false
            }

            if (w.height() + 1 == Math.max(r.height(), n.height())) {
                return false
            }

            if (r.width() + n.width() > 10) {
                return false
            }

            w.top += 1
            w.bottom -= 0

            w.right++

            Painter.fill(l, w.left, w.top, 1, w.height(), Terrain.EMPTY)

        } else {

            if (w.right - w.left < 3) {
                return false
            }

            if (w.width() + 1 == Math.max(r.width(), n.width())) {
                return false
            }

            if (r.height() + n.height() > 10) {
                return false
            }

            w.left += 1
            w.right -= 0

            w.bottom++

            Painter.fill(l, w.left, w.top, w.width(), 1, Terrain.EMPTY)
        }

        return true
    }

    protected fun paintWater(l: Level, rooms: ArrayList<Room>) {
        val lake = Patch.generate(l.width(), l.height(), waterFill, waterSmoothness, true)

        if (!rooms.isEmpty()) {
            for (r in rooms) {
                for (p in r.waterPlaceablePoints()) {
                    val i = l.pointToCell(p)
                    if (lake[i] && l.map!![i] == Terrain.EMPTY) {
                        l.map[i] = Terrain.WATER
                    }
                }
            }
        } else {
            for (i in 0 until l.length()) {
                if (lake[i] && l.map!![i] == Terrain.EMPTY) {
                    l.map[i] = Terrain.WATER
                }
            }
        }

    }

    protected fun paintGrass(l: Level, rooms: ArrayList<Room>) {
        val grass = Patch.generate(l.width(), l.height(), grassFill, grassSmoothness, true)

        val grassCells = ArrayList<Int>()

        if (!rooms.isEmpty()) {
            for (r in rooms) {
                for (p in r.grassPlaceablePoints()) {
                    val i = l.pointToCell(p)
                    if (grass[i] && l.map!![i] == Terrain.EMPTY) {
                        grassCells.add(i)
                    }
                }
            }
        } else {
            for (i in 0 until l.length()) {
                if (grass[i] && l.map!![i] == Terrain.EMPTY) {
                    grassCells.add(i)
                }
            }
        }

        //Adds chaos to grass height distribution. Ratio of high grass depends on fill and smoothing
        //Full range is 8.3% to 75%, but most commonly (20% fill with 3 smoothing) is around 60%
        //low smoothing, or very low fill, will begin to push the ratio down, normally to 50-30%
        for (i in grassCells) {
            if (l.heaps.get(i) != null || l.findMob(i) != null) {
                l.map[i] = Terrain.GRASS
                continue
            }

            var count = 1
            for (n in PathFinder.NEIGHBOURS8) {
                if (grass[i + n]) {
                    count++
                }
            }
            l.map[i] = if (Random.Float() < count / 12f) Terrain.HIGH_GRASS else Terrain.GRASS
        }
    }

    protected fun paintTraps(l: Level, rooms: ArrayList<Room>) {
        val validCells = ArrayList<Int>()

        if (!rooms.isEmpty()) {
            for (r in rooms) {
                for (p in r.trapPlaceablePoints()) {
                    val i = l.pointToCell(p)
                    if (l.map!![i] == Terrain.EMPTY) {
                        validCells.add(i)
                    }
                }
            }
        } else {
            for (i in 0 until l.length()) {
                if (l.map!![i] == Terrain.EMPTY) {
                    validCells.add(i)
                }
            }
        }

        //no more than one trap every 5 valid tiles.
        nTraps = Math.min(nTraps, validCells.size / 5)

        for (i in 0 until nTraps) {

            val trapPos = Random.element(validCells)
            validCells.remove(trapPos) //removes the integer object, not at the index

            try {
                val trap = trapClasses!![Random.chances(trapChances!!)].newInstance().hide()
                l.setTrap(trap, trapPos!!)
                //some traps will not be hidden
                l.map[trapPos] = if (trap.visible) Terrain.TRAP else Terrain.SECRET_TRAP
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
    }

}
