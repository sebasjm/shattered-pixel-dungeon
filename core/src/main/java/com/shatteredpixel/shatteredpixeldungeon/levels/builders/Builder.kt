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
import com.watabou.utils.GameMath
import com.watabou.utils.Point
import com.watabou.utils.PointF
import com.watabou.utils.Random
import com.watabou.utils.Rect

import java.util.ArrayList

abstract class Builder {

    //If builders require additional parameters, they should
    // request them in their constructor or other methods

    //builders take a list of rooms and returns them as a connected map
    //returns null on failure
    abstract fun build(rooms: ArrayList<Room>): ArrayList<Room>

    companion object {

        protected fun findNeighbours(rooms: ArrayList<Room>) {
            val ra = rooms.toTypedArray<Room>()
            for (i in 0 until ra.size - 1) {
                for (j in i + 1 until ra.size) {
                    ra[i].addNeigbour(ra[j])
                }
            }
        }

        //returns a rectangle representing the maximum amount of free space from a specific start point
        protected fun findFreeSpace(start: Point, collision: ArrayList<Room>, maxSize: Int): Rect {
            val space = Rect(start.x - maxSize, start.y - maxSize, start.x + maxSize, start.y + maxSize)

            //shallow copy
            val colliding = ArrayList(collision)
            do {

                //remove empty rooms and any rooms we aren't currently overlapping
                val it = colliding.iterator()
                while (it.hasNext()) {
                    val room = it.next()
                    //if not colliding
                    if (room.isEmpty
                            || Math.max(space.left, room.left) >= Math.min(space.right, room.right)
                            || Math.max(space.top, room.top) >= Math.min(space.bottom, room.bottom)) {
                        it.remove()
                    }
                }

                //iterate through all rooms we are overlapping, and find the closest one
                var closestRoom: Room? = null
                var closestDiff = Integer.MAX_VALUE
                var inside = true
                var curDiff = 0
                for (curRoom in colliding) {

                    if (start.x <= curRoom.left) {
                        inside = false
                        curDiff += curRoom.left - start.x
                    } else if (start.x >= curRoom.right) {
                        inside = false
                        curDiff += start.x - curRoom.right
                    }

                    if (start.y <= curRoom.top) {
                        inside = false
                        curDiff += curRoom.top - start.y
                    } else if (start.y >= curRoom.bottom) {
                        inside = false
                        curDiff += start.y - curRoom.bottom
                    }

                    if (inside) {
                        space.set(start.x, start.y, start.x, start.y)
                        return space
                    }

                    if (curDiff < closestDiff) {
                        closestDiff = curDiff
                        closestRoom = curRoom
                    }

                }

                var wDiff: Int
                var hDiff: Int
                if (closestRoom != null) {

                    wDiff = Integer.MAX_VALUE
                    if (closestRoom.left >= start.x) {
                        wDiff = (space.right - closestRoom.left) * (space.height() + 1)
                    } else if (closestRoom.right <= start.x) {
                        wDiff = (closestRoom.right - space.left) * (space.height() + 1)
                    }

                    hDiff = Integer.MAX_VALUE
                    if (closestRoom.top >= start.y) {
                        hDiff = (space.bottom - closestRoom.top) * (space.width() + 1)
                    } else if (closestRoom.bottom <= start.y) {
                        hDiff = (closestRoom.bottom - space.top) * (space.width() + 1)
                    }

                    //reduce by as little as possible to resolve the collision
                    if (wDiff < hDiff || wDiff == hDiff && Random.Int(2) == 0) {
                        if (closestRoom.left >= start.x && closestRoom.left < space.right) space.right = closestRoom.left
                        if (closestRoom.right <= start.x && closestRoom.right > space.left) space.left = closestRoom.right
                    } else {
                        if (closestRoom.top >= start.y && closestRoom.top < space.bottom) space.bottom = closestRoom.top
                        if (closestRoom.bottom <= start.y && closestRoom.bottom > space.top) space.top = closestRoom.bottom
                    }
                    colliding.remove(closestRoom)
                } else {
                    colliding.clear()
                }

                //loop until we are no longer colliding with any rooms
            } while (!colliding.isEmpty())

            return space
        }

        private val A = 180 / Math.PI

        //returns the angle in degrees made by the centerpoints of 2 rooms, with 0 being straight up.
        protected fun angleBetweenRooms(from: Room, to: Room): Float {
            val fromCenter = PointF((from.left + from.right) / 2f, (from.top + from.bottom) / 2f)
            val toCenter = PointF((to.left + to.right) / 2f, (to.top + to.bottom) / 2f)
            return angleBetweenPoints(fromCenter, toCenter)
        }

        protected fun angleBetweenPoints(from: PointF, to: PointF): Float {
            val m = ((to.y - from.y) / (to.x - from.x)).toDouble()

            var angle = (A * (Math.atan(m) + Math.PI / 2.0)).toFloat()
            if (from.x > to.x) angle -= 180f
            return angle
        }

        //Attempts to place a room such that the angle between the center of the previous room
        // and it matches the given angle ([0-360), where 0 is straight up) as closely as possible.
        //Note that getting an exactly correct angle is harder the closer that angle is to diagonal.
        //Returns the exact angle between the centerpoints of the two rooms, or -1 if placement fails.
        protected fun placeRoom(collision: ArrayList<Room>, prev: Room, next: Room, angle: Float): Float {
            var angle = angle

            //wrap angle around to always be [0-360)
            angle %= 360f
            if (angle < 0) {
                angle += 360f
            }

            val prevCenter = PointF((prev.left + prev.right) / 2f, (prev.top + prev.bottom) / 2f)

            // calculating using y = mx+b, straight line formula
            val m = Math.tan(angle / A + Math.PI / 2.0)
            val b = prevCenter.y - m * prevCenter.x

            //using the line equation, we find the point along the prev room where the line exists
            val start: Point
            val direction: Int
            if (Math.abs(m) >= 1) {
                if (angle < 90 || angle > 270) {
                    direction = Room.TOP
                    start = Point(Math.round((prev.top - b) / m).toInt(), prev.top)
                } else {
                    direction = Room.BOTTOM
                    start = Point(Math.round((prev.bottom - b) / m).toInt(), prev.bottom)
                }
            } else {
                if (angle < 180) {
                    direction = Room.RIGHT
                    start = Point(prev.right, Math.round(m * prev.right + b).toInt())
                } else {
                    direction = Room.LEFT
                    start = Point(prev.left, Math.round(m * prev.left + b).toInt())
                }
            }

            //cap it to a valid connection point for most rooms
            if (direction == Room.TOP || direction == Room.BOTTOM) {
                start.x = GameMath.gate((prev.left + 1).toFloat(), start.x.toFloat(), (prev.right - 1).toFloat()).toInt()
            } else {
                start.y = GameMath.gate((prev.top + 1).toFloat(), start.y.toFloat(), (prev.bottom - 1).toFloat()).toInt()
            }

            //space checking
            val space = findFreeSpace(start, collision, Math.max(next.maxWidth(), next.maxHeight()))
            if (!next.setSizeWithLimit(space.width() + 1, space.height() + 1)) {
                return -1f
            }

            //find the ideal center for this new room using the line equation and known dimensions
            val targetCenter = PointF()
            if (direction == Room.TOP) {
                targetCenter.y = prev.top - (next.height() - 1) / 2f
                targetCenter.x = ((targetCenter.y - b) / m).toFloat()
                next.setPos(Math.round(targetCenter.x - (next.width() - 1) / 2f), prev.top - (next.height() - 1))

            } else if (direction == Room.BOTTOM) {
                targetCenter.y = prev.bottom + (next.height() - 1) / 2f
                targetCenter.x = ((targetCenter.y - b) / m).toFloat()
                next.setPos(Math.round(targetCenter.x - (next.width() - 1) / 2f), prev.bottom)

            } else if (direction == Room.RIGHT) {
                targetCenter.x = prev.right + (next.width() - 1) / 2f
                targetCenter.y = (m * targetCenter.x + b).toFloat()
                next.setPos(prev.right, Math.round(targetCenter.y - (next.height() - 1) / 2f))

            } else if (direction == Room.LEFT) {
                targetCenter.x = prev.left - (next.width() - 1) / 2f
                targetCenter.y = (m * targetCenter.x + b).toFloat()
                next.setPos(prev.left - (next.width() - 1), Math.round(targetCenter.y - (next.height() - 1) / 2f))

            }

            //perform connection bounds and target checking, move the room if necessary
            if (direction == Room.TOP || direction == Room.BOTTOM) {
                if (next.right < prev.left + 2)
                    next.shift(prev.left + 2 - next.right, 0)
                else if (next.left > prev.right - 2) next.shift(prev.right - 2 - next.left, 0)

                if (next.right > space.right)
                    next.shift(space.right - next.right, 0)
                else if (next.left < space.left) next.shift(space.left - next.left, 0)
            } else {
                if (next.bottom < prev.top + 2)
                    next.shift(0, prev.top + 2 - next.bottom)
                else if (next.top > prev.bottom - 2) next.shift(0, prev.bottom - 2 - next.top)

                if (next.bottom > space.bottom)
                    next.shift(0, space.bottom - next.bottom)
                else if (next.top < space.top) next.shift(0, space.top - next.top)
            }

            //attempt to connect, return the result angle if successful.
            return if (next.connect(prev)) {
                angleBetweenRooms(prev, next)
            } else {
                -1f
            }
        }
    }
}
