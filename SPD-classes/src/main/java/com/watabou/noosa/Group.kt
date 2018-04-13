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

package com.watabou.noosa

import com.watabou.noosa.particles.Emitter
import com.watabou.utils.Random

import java.util.ArrayList

open class Group : Gizmo() {

    protected var members: ArrayList<Gizmo?>? = null

    // Accessing it is a little faster,
    // than calling members.getSize()
    var length: Int = 0

    init {
        members = ArrayList()
        length = 0
    }

    @Synchronized
    override fun destroy() {
        super.destroy()
        for (i in 0 until length) {
            val g = members!![i]
            g?.destroy()
        }

        if (members != null) {
            members!!.clear()
            members = null
        }
        length = 0
    }

    @Synchronized
    override fun update() {
        for (i in 0 until length) {
            val g = members!![i]
            if (g != null && g.exists && g.active
                    //functionality for the freezing of emitters(particle effects), effects are given a second
                    //from load to get started so they aren't frozen before anything is generated.
                    && !(freezeEmitters && Game.timeTotal > 1f && g is Emitter)) {
                g.update()
            }
        }
    }

    @Synchronized
    override fun draw() {
        for (i in 0 until length) {
            val g = members!![i]
            if (g != null && g.exists && g.isVisible) {
                g.draw()
            }
        }
    }

    @Synchronized
    override fun kill() {
        // A killed group keeps all its members,
        // but they get killed too
        for (i in 0 until length) {
            val g = members!![i]
            if (g != null && g.exists) {
                g.kill()
            }
        }

        super.kill()
    }

    @Synchronized
    fun indexOf(g: Gizmo): Int {
        return members!!.indexOf(g)
    }

    @Synchronized
    fun add(g: Gizmo): Gizmo {

        if (g.parent === this) {
            return g
        }

        if (g.parent != null) {
            g.parent!!.remove(g)
        }

        // Trying to find an empty space for a new member
        for (i in 0 until length) {
            if (members!![i] == null) {
                members!![i] = g
                g.parent = this
                return g
            }
        }

        members!!.add(g)
        g.parent = this
        length++
        return g
    }

    @Synchronized
    fun addToFront(g: Gizmo): Gizmo {

        if (g.parent === this) {
            return g
        }

        if (g.parent != null) {
            g.parent!!.remove(g)
        }

        // Trying to find an empty space for a new member
        // starts from the front and never goes over a none-null element
        for (i in length - 1 downTo 0) {
            if (members!![i] == null) {
                if (i == 0 || members!![i - 1] != null) {
                    members!![i] = g
                    g.parent = this
                    return g
                }
            } else {
                break
            }
        }

        members!!.add(g)
        g.parent = this
        length++
        return g
    }

    @Synchronized
    fun addToBack(g: Gizmo): Gizmo {

        if (g.parent === this) {
            sendToBack(g)
            return g
        }

        if (g.parent != null) {
            g.parent!!.remove(g)
        }

        if (members!![0] == null) {
            members!![0] = g
            g.parent = this
            return g
        }

        members!!.add(0, g)
        g.parent = this
        length++
        return g
    }

    @Synchronized
    fun recycle(c: Class<out Gizmo>?): Gizmo? {

        val g = getFirstAvailable(c)
        if (g != null) {

            return g

        } else if (c == null) {

            return null

        } else {

            try {
                return add(c.newInstance())
            } catch (e: Exception) {
                Game.reportException(e)
            }

        }

        return null
    }

    // Fast removal - replacing with null
    @Synchronized
    fun erase(g: Gizmo): Gizmo? {
        val index = members!!.indexOf(g)

        if (index != -1) {
            members!!.set(index, null)
            g.parent = null
            return g
        } else {
            return null
        }
    }

    // Real removal
    @Synchronized
    fun remove(g: Gizmo): Gizmo? {
        if (members!!.remove(g)) {
            length--
            g.parent = null
            return g
        } else {
            return null
        }
    }

    @Synchronized
    fun replace(oldOne: Gizmo, newOne: Gizmo): Gizmo? {
        val index = members!!.indexOf(oldOne)
        if (index != -1) {
            members!![index] = newOne
            newOne.parent = this
            oldOne.parent = null
            return newOne
        } else {
            return null
        }
    }

    @Synchronized
    fun getFirstAvailable(c: Class<out Gizmo>?): Gizmo? {

        for (i in 0 until length) {
            val g = members!![i]
            if (g != null && !g.exists && (c == null || g.javaClass == c)) {
                return g
            }
        }

        return null
    }

    @Synchronized
    fun countLiving(): Int {

        var count = 0

        for (i in 0 until length) {
            val g = members!![i]
            if (g != null && g.exists && g.alive) {
                count++
            }
        }

        return count
    }

    @Synchronized
    fun countDead(): Int {

        var count = 0

        for (i in 0 until length) {
            val g = members!![i]
            if (g != null && !g.alive) {
                count++
            }
        }

        return count
    }

    @Synchronized
    fun random(): Gizmo? {
        return if (length > 0) {
            members!![Random.Int(length)]
        } else {
            null
        }
    }

    @Synchronized
    fun clear() {
        for (i in 0 until length) {
            val g = members!![i]
            if (g != null) {
                g.parent = null
            }
        }
        members!!.clear()
        length = 0
    }

    @Synchronized
    fun bringToFront(g: Gizmo): Gizmo? {
        if (members!!.contains(g)) {
            members!!.remove(g)
            members!!.add(g)
            return g
        } else {
            return null
        }
    }

    @Synchronized
    fun sendToBack(g: Gizmo): Gizmo? {
        if (members!!.contains(g)) {
            members!!.remove(g)
            members!!.add(0, g)
            return g
        } else {
            return null
        }
    }

    companion object {

        var freezeEmitters = false
    }
}
