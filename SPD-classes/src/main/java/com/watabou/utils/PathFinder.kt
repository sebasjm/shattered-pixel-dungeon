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

package com.watabou.utils

import java.util.Arrays
import java.util.LinkedList

object PathFinder {

    var distance: IntArray = IntArray(0)
    private var maxVal: IntArray = IntArray(0)

    private var goals: BooleanArray = BooleanArray(0)
    private var queue: IntArray = IntArray(0)

    private var size = 0
    private var width = 0

    private var dir: IntArray = IntArray(0)
    private var dirLR: IntArray = IntArray(0)

    //performance-light shortcuts for some common pathfinder cases
    //they are in array-access order for increased memory performance
    var NEIGHBOURS4: IntArray = IntArray(0)
    var NEIGHBOURS8: IntArray = IntArray(0)
    var NEIGHBOURS9: IntArray = IntArray(0)

    //similar to their equivalent neighbour arrays, but the order is clockwise.
    //Useful for some logic functions, but is slower due to lack of array-access order.
    var CIRCLE4: IntArray? = null
    var CIRCLE8: IntArray? = null

    fun setMapSize(width: Int, height: Int) {

        PathFinder.width = width
        PathFinder.size = width * height

        distance = IntArray(size)
        goals = BooleanArray(size)
        queue = IntArray(size)

        maxVal = IntArray(size)
        Arrays.fill(maxVal!!, Integer.MAX_VALUE)

        dir = intArrayOf(-1, +1, -width, +width, -width - 1, -width + 1, +width - 1, +width + 1)
        dirLR = intArrayOf(-1 - width, -1, -1 + width, -width, +width, +1 - width, +1, +1 + width)

        NEIGHBOURS4 = intArrayOf(-width, -1, +1, +width)
        NEIGHBOURS8 = intArrayOf(-width - 1, -width, -width + 1, -1, +1, +width - 1, +width, +width + 1)
        NEIGHBOURS9 = intArrayOf(-width - 1, -width, -width + 1, -1, 0, +1, +width - 1, +width, +width + 1)

        CIRCLE4 = intArrayOf(-width, +1, +width, -1)
        CIRCLE8 = intArrayOf(-width - 1, -width, -width + 1, +1, +width + 1, +width, +width - 1, -1)
    }

    fun find(from: Int, to: Int, passable: BooleanArray): Path? {

        if (!buildDistanceMap(from, to, passable)) {
            return null
        }

        val result = Path()
        var s = from

        // From the starting position we are moving downwards,
        // until we reach the ending point
        do {
            var minD = distance!![s]
            var mins = s

            for (i in dir!!.indices) {

                val n = s + dir!![i]

                val thisD = distance!![n]
                if (thisD < minD) {
                    minD = thisD
                    mins = n
                }
            }
            s = mins
            result.add(s)
        } while (s != to)

        return result
    }

    fun getStep(from: Int, to: Int, passable: BooleanArray): Int {

        if (!buildDistanceMap(from, to, passable)) {
            return -1
        }

        // From the starting position we are making one step downwards
        var minD = distance!![from]
        var best = from

        var step: Int
        var stepD: Int

        for (i in dir!!.indices) {

            step = from + dir!![i]
            stepD = distance!![step]
            if (stepD < minD) {
                minD = stepD
                best = step
            }
        }

        return best
    }

    fun getStepBack(cur: Int, from: Int, passable: BooleanArray): Int {

        val d = buildEscapeDistanceMap(cur, from, 2f, passable)
        for (i in 0 until size) {
            goals!![i] = distance!![i] == d
        }
        if (!buildDistanceMap(cur, goals!!, passable)) {
            return -1
        }

        val s = cur

        // From the starting position we are making one step downwards
        var minD = distance!![s]
        var mins = s

        for (i in dir!!.indices) {

            val n = s + dir!![i]
            val thisD = distance!![n]

            if (thisD < minD) {
                minD = thisD
                mins = n
            }
        }

        return mins
    }

    private fun buildDistanceMap(from: Int, to: Int, passable: BooleanArray): Boolean {

        if (from == to) {
            return false
        }

        System.arraycopy(maxVal!!, 0, distance, 0, maxVal!!.size)

        var pathFound = false

        var head = 0
        var tail = 0

        // Add to queue
        queue!![tail++] = to
        distance!![to] = 0

        while (head < tail) {

            // Remove from queue
            val step = queue!![head++]
            if (step == from) {
                pathFound = true
                break
            }
            val nextDistance = distance!![step] + 1

            val start = if (step % width == 0) 3 else 0
            val end = if ((step + 1) % width == 0) 3 else 0
            for (i in start until dirLR!!.size - end) {

                val n = step + dirLR!![i]
                if (n == from || n >= 0 && n < size && passable[n] && distance!![n] > nextDistance) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }

            }
        }

        return pathFound
    }

    fun buildDistanceMap(to: Int, passable: BooleanArray, limit: Int) {

        System.arraycopy(maxVal!!, 0, distance, 0, maxVal!!.size)

        var head = 0
        var tail = 0

        // Add to queue
        queue!![tail++] = to
        distance!![to] = 0

        while (head < tail) {

            // Remove from queue
            val step = queue!![head++]

            val nextDistance = distance!![step] + 1
            if (nextDistance > limit) {
                return
            }

            val start = if (step % width == 0) 3 else 0
            val end = if ((step + 1) % width == 0) 3 else 0
            for (i in start until dirLR!!.size - end) {

                val n = step + dirLR!![i]
                if (n >= 0 && n < size && passable[n] && distance!![n] > nextDistance) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }

            }
        }
    }

    private fun buildDistanceMap(from: Int, to: BooleanArray, passable: BooleanArray): Boolean {

        if (to[from]) {
            return false
        }

        System.arraycopy(maxVal!!, 0, distance, 0, maxVal!!.size)

        var pathFound = false

        var head = 0
        var tail = 0

        // Add to queue
        for (i in 0 until size) {
            if (to[i]) {
                queue!![tail++] = i
                distance!![i] = 0
            }
        }

        while (head < tail) {

            // Remove from queue
            val step = queue!![head++]
            if (step == from) {
                pathFound = true
                break
            }
            val nextDistance = distance!![step] + 1

            val start = if (step % width == 0) 3 else 0
            val end = if ((step + 1) % width == 0) 3 else 0
            for (i in start until dirLR!!.size - end) {

                val n = step + dirLR!![i]
                if (n == from || n >= 0 && n < size && passable[n] && distance!![n] > nextDistance) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }

            }
        }

        return pathFound
    }

    private fun buildEscapeDistanceMap(cur: Int, from: Int, factor: Float, passable: BooleanArray): Int {

        System.arraycopy(maxVal!!, 0, distance, 0, maxVal!!.size)

        var destDist = Integer.MAX_VALUE

        var head = 0
        var tail = 0

        // Add to queue
        queue!![tail++] = from
        distance!![from] = 0

        var dist = 0

        while (head < tail) {

            // Remove from queue
            val step = queue!![head++]
            dist = distance!![step]

            if (dist > destDist) {
                return destDist
            }

            if (step == cur) {
                destDist = (dist * factor).toInt() + 1
            }

            val nextDistance = dist + 1

            val start = if (step % width == 0) 3 else 0
            val end = if ((step + 1) % width == 0) 3 else 0
            for (i in start until dirLR!!.size - end) {

                val n = step + dirLR!![i]
                if (n >= 0 && n < size && passable[n] && distance!![n] > nextDistance) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }

            }
        }

        return dist
    }

    fun buildDistanceMap(to: Int, passable: BooleanArray) {

        System.arraycopy(maxVal!!, 0, distance, 0, maxVal!!.size)

        var head = 0
        var tail = 0

        // Add to queue
        queue!![tail++] = to
        distance!![to] = 0

        while (head < tail) {

            // Remove from queue
            val step = queue!![head++]
            val nextDistance = distance!![step] + 1

            val start = if (step % width == 0) 3 else 0
            val end = if ((step + 1) % width == 0) 3 else 0
            for (i in start until dirLR!!.size - end) {

                val n = step + dirLR!![i]
                if (n >= 0 && n < size && passable[n] && distance!![n] > nextDistance) {
                    // Add to queue
                    queue!![tail++] = n
                    distance!![n] = nextDistance
                }

            }
        }
    }

    class Path : LinkedList<Int>()
}
