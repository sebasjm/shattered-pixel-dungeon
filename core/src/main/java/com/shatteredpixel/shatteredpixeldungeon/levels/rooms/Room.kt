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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Graph
import com.watabou.utils.Point
import com.watabou.utils.Random
import com.watabou.utils.Rect

import java.util.ArrayList
import java.util.LinkedHashMap

//Note that this class should be treated as if it were abstract
// it is currently not abstract to maintain compatibility with pre-0.6.0 saves
// TODO make this class abstract after dropping support for pre-0.6.0 saves
open class Room : Rect, Graph.Node, Bundlable {

    var neigbours = ArrayList<Room>()
    var connected = LinkedHashMap<Room, Door>()

    var distance: Int = 0
    var price = 1

    var legacyType = "NULL"

    constructor() : super() {}

    constructor(other: Rect) : super(other) {}

    fun set(other: Room): Room {
        super.set(other)
        for (r in other.neigbours) {
            neigbours.add(r)
            r.neigbours.remove(other)
            r.neigbours.add(this)
        }
        for (r in other.connected.keys) {
            val d = other.connected[r]
            r.connected.remove(other)
            r.connected[this] = d
            connected[r] = d
        }
        return this
    }

    // **** Spatial logic ****

    //Note: when overriding these YOU MUST store any randomly decided values.
    //With the same room and the same parameters these should always return
    //the same value over multiple calls, even if there's some randomness initially.
    open fun minWidth(): Int {
        return -1
    }

    open fun maxWidth(): Int {
        return -1
    }

    open fun minHeight(): Int {
        return -1
    }

    open fun maxHeight(): Int {
        return -1
    }

    fun setSize(): Boolean {
        return setSize(minWidth(), maxWidth(), minHeight(), maxHeight())
    }

    fun forceSize(w: Int, h: Int): Boolean {
        return setSize(w, w, h, h)
    }

    fun setSizeWithLimit(w: Int, h: Int): Boolean {
        if (w < minWidth() || h < minHeight()) {
            return false
        } else {
            setSize()

            if (width() > w || height() > h) {
                resize(Math.min(width(), w) - 1, Math.min(height(), h) - 1)
            }

            return true
        }
    }

    protected fun setSize(minW: Int, maxW: Int, minH: Int, maxH: Int): Boolean {
        if (minW < minWidth()
                || maxW > maxWidth()
                || minH < minHeight()
                || maxH > maxHeight()
                || minW > maxW
                || minH > maxH) {
            return false
        } else {
            //subtract one because rooms are inclusive to their right and bottom sides
            resize(Random.NormalIntRange(minW, maxW) - 1,
                    Random.NormalIntRange(minH, maxH) - 1)
            return true
        }
    }

    //Width and height are increased by 1 because rooms are inclusive to their right and bottom sides
    override fun width(): Int {
        return super.width() + 1
    }

    override fun height(): Int {
        return super.height() + 1
    }

    @JvmOverloads
    fun random(m: Int = 1): Point {
        return Point(Random.IntRange(left + m, right - m),
                Random.IntRange(top + m, bottom - m))
    }

    //a point is only considered to be inside if it is within the 1 tile perimeter
    override fun inside(p: Point): Boolean {
        return p.x > left && p.y > top && p.x < right && p.y < bottom
    }

    fun center(): Point {
        return Point(
                (left + right) / 2 + if ((right - left) % 2 == 1) Random.Int(2) else 0,
                (top + bottom) / 2 + if ((bottom - top) % 2 == 1) Random.Int(2) else 0)
    }

    //TODO make abstract
    open fun minConnections(direction: Int): Int {
        return -1
    }

    fun curConnections(direction: Int): Int {
        if (direction == ALL) {
            return connected.size

        } else {
            var total = 0
            for (r in connected.keys) {
                val i = intersect(r)
                if (direction == LEFT && i.width() == 0 && i.left == left)
                    total++
                else if (direction == TOP && i.height() == 0 && i.top == top)
                    total++
                else if (direction == RIGHT && i.width() == 0 && i.right == right)
                    total++
                else if (direction == BOTTOM && i.height() == 0 && i.bottom == bottom) total++
            }
            return total
        }
    }

    fun remConnections(direction: Int): Int {
        return if (curConnections(ALL) >= maxConnections(ALL))
            0
        else
            maxConnections(direction) - curConnections(direction)
    }

    //TODO make abstract
    open fun maxConnections(direction: Int): Int {
        return -1
    }

    //only considers point-specific limits, not direction limits
    open fun canConnect(p: Point): Boolean {
        //point must be along exactly one edge, no corners.
        return (p.x == left || p.x == right) != (p.y == top || p.y == bottom)
    }

    //only considers direction limits, not point-specific limits
    fun canConnect(direction: Int): Boolean {
        return remConnections(direction) > 0
    }

    //considers both direction and point limits
    open fun canConnect(r: Room): Boolean {
        val i = intersect(r)

        var foundPoint = false
        for (p in i.points) {
            if (canConnect(p) && r.canConnect(p)) {
                foundPoint = true
                break
            }
        }
        if (!foundPoint) return false

        return if (i.width() == 0 && i.left == left)
            canConnect(LEFT) && r.canConnect(LEFT)
        else if (i.height() == 0 && i.top == top)
            canConnect(TOP) && r.canConnect(TOP)
        else if (i.width() == 0 && i.right == right)
            canConnect(RIGHT) && r.canConnect(RIGHT)
        else if (i.height() == 0 && i.bottom == bottom)
            canConnect(BOTTOM) && r.canConnect(BOTTOM)
        else
            false
    }

    fun addNeigbour(other: Room): Boolean {
        if (neigbours.contains(other))
            return true

        val i = intersect(other)
        if (i.width() == 0 && i.height() >= 2 || i.height() == 0 && i.width() >= 2) {
            neigbours.add(other)
            other.neigbours.add(this)
            return true
        }
        return false
    }

    fun connect(room: Room): Boolean {
        if ((neigbours.contains(room) || addNeigbour(room))
                && !connected.containsKey(room) && canConnect(room)) {
            connected[room] = null
            room.connected[this] = null
            return true
        }
        return false
    }

    fun clearConnections() {
        for (r in neigbours) {
            r.neigbours.remove(this)
        }
        neigbours.clear()
        for (r in connected.keys) {
            r.connected.remove(this)
        }
        connected.clear()
    }

    // **** Painter Logic ****

    //TODO make abstract
    open fun paint(level: Level) {}

    //whether or not a painter can make its own modifications to a specific point
    open fun canPlaceWater(p: Point): Boolean {
        return inside(p)
    }

    fun waterPlaceablePoints(): ArrayList<Point> {
        val points = ArrayList<Point>()
        for (i in left..right) {
            for (j in top..bottom) {
                val p = Point(i, j)
                if (canPlaceWater(p)) points.add(p)
            }
        }
        return points
    }

    //whether or not a painter can make place grass at a specific point
    open fun canPlaceGrass(p: Point): Boolean {
        return inside(p)
    }

    fun grassPlaceablePoints(): ArrayList<Point> {
        val points = ArrayList<Point>()
        for (i in left..right) {
            for (j in top..bottom) {
                val p = Point(i, j)
                if (canPlaceGrass(p)) points.add(p)
            }
        }
        return points
    }

    //whether or not a painter can place a trap at a specific point
    open fun canPlaceTrap(p: Point): Boolean {
        return inside(p)
    }

    fun trapPlaceablePoints(): ArrayList<Point> {
        val points = ArrayList<Point>()
        for (i in left..right) {
            for (j in top..bottom) {
                val p = Point(i, j)
                if (canPlaceTrap(p)) points.add(p)
            }
        }
        return points
    }


    // **** Graph.Node interface ****

    override fun distance(): Int {
        return distance
    }

    override fun distance(value: Int) {
        distance = value
    }

    override fun price(): Int {
        return price
    }

    override fun price(value: Int) {
        price = value
    }

    override fun edges(): Collection<Room> {
        val edges = ArrayList<Room>()
        for (r in connected.keys) {
            val d = connected[r]
            //for the purposes of path building, ignore all doors that are locked, blocked, or hidden
            if (d.type == Door.Type.EMPTY || d.type == Door.Type.TUNNEL
                    || d.type == Door.Type.UNLOCKED || d.type == Door.Type.REGULAR) {
                edges.add(r)
            }
        }
        return edges
    }

    override fun storeInBundle(bundle: Bundle) {
        bundle.put("left", left)
        bundle.put("top", top)
        bundle.put("right", right)
        bundle.put("bottom", bottom)
        if (legacyType != "NULL")
            bundle.put("type", legacyType)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        left = bundle.getInt("left")
        top = bundle.getInt("top")
        right = bundle.getInt("right")
        bottom = bundle.getInt("bottom")
        if (bundle.contains("type"))
            legacyType = bundle.getString("type")
    }

    //Note that currently connections and neighbours are not preserved on load
    open fun onLevelLoad(level: Level) {
        //does nothing by default
    }

    class Door : Point {
        var type = Type.EMPTY

        enum class Type {
            EMPTY, TUNNEL, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED
        }

        constructor(p: Point) : super(p) {}

        constructor(x: Int, y: Int) : super(x, y) {}

        fun set(type: Type) {
            if (type.compareTo(this.type) > 0) {
                this.type = type
            }
        }
    }

    companion object {


        // **** Connection logic ****

        val ALL = 0
        val LEFT = 1
        val TOP = 2
        val RIGHT = 3
        val BOTTOM = 4
    }
}