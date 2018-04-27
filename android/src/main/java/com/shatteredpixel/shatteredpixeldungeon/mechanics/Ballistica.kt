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

package com.shatteredpixel.shatteredpixeldungeon.mechanics

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.watabou.noosa.Game

import java.util.ArrayList

class Ballistica(from: Int, to: Int, params: Int) {

    //note that the path is the FULL path of the projectile, including tiles after collision.
    //make sure to generate a subPath for the common case of going source to collision.
    var path = ArrayList<Int>()
    var sourcePos: Int? = null
    var collisionPos: Int? = null
    var dist: Int? = 0


    init {
        sourcePos = from
        build(from, to, params and STOP_TARGET > 0, params and STOP_CHARS > 0, params and STOP_TERRAIN > 0)
        if (collisionPos != null)
            dist = path.indexOf(collisionPos!!)
        else {
            dist = path.size - 1
            collisionPos = path.get(dist!!)
        }

    }

    private fun build(from: Int, to: Int, stopTarget: Boolean, stopChars: Boolean, stopTerrain: Boolean) {
        val w = Dungeon.level!!.width()

        val x0 = from % w
        val x1 = to % w
        val y0 = from / w
        val y1 = to / w

        var dx = x1 - x0
        var dy = y1 - y0

        val stepX = if (dx > 0) +1 else -1
        val stepY = if (dy > 0) +1 else -1

        dx = Math.abs(dx)
        dy = Math.abs(dy)

        val stepA: Int
        val stepB: Int
        val dA: Int
        val dB: Int

        if (dx > dy) {

            stepA = stepX
            stepB = stepY * w
            dA = dx
            dB = dy

        } else {

            stepA = stepY * w
            stepB = stepX
            dA = dy
            dB = dx

        }

        var cell = from

        var err = dA / 2
        while (Dungeon.level!!.insideMap(cell)) {

            //if we're in a wall, collide with the previous cell along the path.
            if (stopTerrain && cell != sourcePos && !Dungeon.level!!.passable[cell] && !Dungeon.level!!.avoid[cell]) {
                collide(path[path.size - 1])
            }

            path.add(cell)

            if (stopTerrain && cell != sourcePos && Dungeon.level!!.losBlocking[cell]
                    || cell != sourcePos && stopChars && Actor.findChar(cell) != null
                    || cell == to && stopTarget) {
                collide(cell)
            }

            cell += stepA

            err += dB
            if (err >= dA) {
                err = err - dA
                cell = cell + stepB
            }
        }
    }

    //we only want to record the first position collision occurs at.
    private fun collide(cell: Int) {
        if (collisionPos == null)
            collisionPos = cell
    }

    //returns a segment of the path from start to end, inclusive.
    //if there is an error, returns an empty arraylist instead.
    fun subPath(start: Int, end: Int): List<Int> {
        var end = end
        try {
            end = Math.min(end, path.size - 1)
            return path.subList(start, end + 1)
        } catch (e: Exception) {
            Game.reportException(e)
            return ArrayList()
        }

    }

    companion object {

        //parameters to specify the colliding cell
        val STOP_TARGET = 1 //ballistica will stop at the target cell
        val STOP_CHARS = 2 //ballistica will stop on first char hit
        val STOP_TERRAIN = 4 //ballistica will stop on terrain(LOS blocking, impassable, etc.)

        val PROJECTILE = STOP_TARGET or STOP_CHARS or STOP_TERRAIN

        val MAGIC_BOLT = STOP_CHARS or STOP_TERRAIN

        val WONT_STOP = 0
    }
}
