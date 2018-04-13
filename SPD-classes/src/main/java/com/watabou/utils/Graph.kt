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

import java.util.ArrayList
import java.util.LinkedList

object Graph {

    fun <T : Graph.Node> setPrice(nodes: List<T>, value: Int) {
        for (node in nodes) {
            node.price(value)
        }
    }

    fun <T : Graph.Node> buildDistanceMap(nodes: Collection<T>, focus: Node) {

        for (node in nodes) {
            node.distance(Integer.MAX_VALUE)
        }

        val queue = LinkedList<Node>()

        focus.distance(0)
        queue.add(focus)

        while (!queue.isEmpty()) {

            val node = queue.poll()
            val distance = node.distance()
            val price = node.price()

            for (edge in node.edges()) {
                if (edge.distance() > distance + price) {
                    queue.add(edge)
                    edge.distance(distance + price)
                }
            }
        }
    }

    fun <T : Graph.Node> buildPath(nodes: Collection<T>, from: T, to: T): List<T>? {

        val path = ArrayList<T>()

        var room = from
        while (room !== to) {

            var min = room.distance()
            var next: T? = null

            val edges = room.edges()

            for (edge in edges) {

                val distance = edge.distance()
                if (distance < min) {
                    min = distance
                    next = edge as T
                }
            }

            if (next == null) {
                return null
            }

            path.add(next)
            room = next
        }

        return path
    }

    interface Node {

        fun distance(): Int
        fun distance(value: Int)

        fun price(): Int
        fun price(value: Int)

        fun edges(): Collection<Node>

    }
}
