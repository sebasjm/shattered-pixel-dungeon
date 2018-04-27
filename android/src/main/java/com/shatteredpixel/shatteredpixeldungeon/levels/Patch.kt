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

package com.shatteredpixel.shatteredpixeldungeon.levels

import com.watabou.utils.Random

object Patch {

    /*
	 * fill is the initial seeded fill rate when creating a random boolean array.
	 *
	 * clustering is the number of clustering passes done on then array, to create patches.
	 * each clustering pass is basically a 3x3 mask filter but with rounding to true or false
	 * high clustering values will produce more concentrated patches,
	 * but any amount of clustering will rapidly push fill rates towards 1.0f or 0.0f
	 * The closer the fill rate is to 0.5f the weaker this pushing will be.
	 *
	 * forceFillRate adjusts the algorithm to force fill rate to be consistent despite clustering.
	 * this is achieved by firstly pulling the initial fill value towards 0.5f
	 * and then by manually filling in or emptying cells after clustering, until the fill rate is
	 * achieved. This is tracked with the fillDiff variable.
	*/
    fun generate(w: Int, h: Int, fill: Float, clustering: Int, forceFillRate: Boolean): BooleanArray {
        var fill = fill

        val length = w * h

        var cur = BooleanArray(length)
        var off = BooleanArray(length)

        var fillDiff = -Math.round(length * fill)

        if (forceFillRate && clustering > 0) {
            fill += (0.5f - fill) * 0.5f
        }

        for (i in 0 until length) {
            off[i] = Random.Float() < fill
            if (off[i]) fillDiff++
        }

        for (i in 0 until clustering) {

            for (y in 0 until h) {
                for (x in 0 until w) {

                    val pos = x + y * w
                    var count = 0
                    var neighbours = 0

                    if (y > 0) {
                        if (x > 0) {
                            if (off[pos - w - 1]) count++
                            neighbours++
                        }
                        if (off[pos - w]) count++
                        neighbours++
                        if (x < w - 1) {
                            if (off[pos - w + 1]) count++
                            neighbours++
                        }
                    }

                    if (x > 0) {
                        if (off[pos - 1]) count++
                        neighbours++
                    }
                    if (off[pos]) count++
                    neighbours++
                    if (x < w - 1) {
                        if (off[pos + 1]) count++
                        neighbours++
                    }

                    if (y < h - 1) {
                        if (x > 0) {
                            if (off[pos + w - 1]) count++
                            neighbours++
                        }
                        if (off[pos + w]) count++
                        neighbours++
                        if (x < w - 1) {
                            if (off[pos + w + 1]) count++
                            neighbours++
                        }
                    }

                    cur[pos] = 2 * count >= neighbours
                    if (cur[pos] != off[pos]) fillDiff += if (cur[pos]) +1 else -1

                }
            }

            val tmp = cur
            cur = off
            off = tmp
        }

        //even if force fill rate is on, only do this if we have some kind of border
        if (forceFillRate && Math.min(w, h) > 2) {
            val neighbours = intArrayOf(-w - 1, -w, -w + 1, -1, 0, +1, +w - 1, +w, +w + 1)
            val growing = fillDiff < 0

            while (fillDiff != 0) {
                var cell: Int
                var tries = 0

                //random cell, not in the map's borders
                // try length/10 times to find a cell we can grow from, and not start a new patch/hole
                do {
                    cell = Random.Int(1, w - 1) + Random.Int(1, h - 1) * w
                    tries++
                } while (off[cell] != growing && tries * 10 < length)

                for (i in neighbours) {
                    if (fillDiff != 0 && off[cell + i] != growing) {
                        off[cell + i] = growing
                        fillDiff += if (growing) +1 else -1
                    }
                }
            }
        }

        return off
    }
}
