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
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray

//based on: http://www.roguebasin.com/index.php?title=FOV_using_recursive_shadowcasting
object ShadowCaster {

    val MAX_DISTANCE = 8

    //max length of rows as FOV moves out, for each FOV distance
    //This is used to make the overall FOV circular, instead of square
    private var rounding: Array<IntArray>? = null

    init {
        rounding = arrayOfNulls(MAX_DISTANCE + 1)
        for (i in 1..MAX_DISTANCE) {
            rounding[i] = IntArray(i + 1)
            for (j in 1..i) {
                //testing the middle of a cell, so we use i + 0.5
                rounding!![i][j] = Math.min(
                        j.toLong(),
                        Math.round((i + 0.5) * Math.cos(Math.asin(j / (i + 0.5))))).toInt()
            }
        }
    }

    fun castShadow(x: Int, y: Int, fieldOfView: BooleanArray, distance: Int) {

        BArray.setFalse(fieldOfView)

        //set source cell to true
        fieldOfView[y * Dungeon.level!!.width() + x] = true

        val losBlocking = Dungeon.level!!.losBlocking

        //scans octants, clockwise
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, +1, -1, false)
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, -1, +1, true)
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, +1, +1, true)
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, +1, +1, false)
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, -1, +1, false)
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, +1, -1, true)
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, -1, -1, true)
        scanOctant(distance, fieldOfView, losBlocking, 1, x, y, 0.0, 1.0, -1, -1, false)

    }

    //TODO this is slightly less permissive that the previous algorithm, decide if that's okay

    //scans a single 45 degree octant of the FOV.
    //This can add up to a whole FOV by mirroring in X(mX), Y(mY), and X=Y(mXY)
    private fun scanOctant(distance: Int, fov: BooleanArray, blocking: BooleanArray, row: Int,
                           x: Int, y: Int, lSlope: Double, rSlope: Double,
                           mX: Int, mY: Int, mXY: Boolean) {
        var row = row
        var lSlope = lSlope

        var inBlocking = false
        var start: Int
        var end: Int
        var col: Int

        //calculations are offset by 0.5 because FOV is coming from the center of the source cell

        //for each row, starting with the current one
        while (row <= distance) {

            //we offset by slightly less than 0.5 to account for slopes just touching a cell
            if (lSlope == 0.0)
                start = 0
            else
                start = Math.floor((row - 0.5) * lSlope + 0.499).toInt()

            if (rSlope == 1.0)
                end = rounding!![distance][row]
            else
                end = Math.min(rounding!![distance][row],
                        Math.ceil((row + 0.5) * rSlope - 0.499).toInt())

            //coordinates of source
            var cell = x + y * Dungeon.level!!.width()

            //plus coordinates of current cell (including mirroring in x, y, and x=y)
            if (mXY)
                cell += mX * start * Dungeon.level!!.width() + mY * row
            else
                cell += mX * start + mY * row * Dungeon.level!!.width()

            //for each column in this row, which
            col = start
            while (col <= end) {

                fov[cell] = true

                if (blocking[cell]) {
                    if (!inBlocking) {
                        inBlocking = true

                        //start a new scan, 1 row deeper, ending at the left side of current cell
                        if (col != start) {
                            scanOctant(distance, fov, blocking, row + 1, x, y, lSlope,
                                    //change in x over change in y
                                    (col - 0.5) / (row + 0.5),
                                    mX, mY, mXY)
                        }
                    }

                } else {
                    if (inBlocking) {
                        inBlocking = false

                        //restrict current scan to the left side of current cell for future rows

                        //change in x over change in y
                        lSlope = (col - 0.5) / (row - 0.5)
                    }
                }

                if (!mXY)
                    cell += mX
                else
                    cell += mX * Dungeon.level!!.width()
                col++

            }

            //if the row ends in a blocking cell, this scan is finished.
            if (inBlocking) return
            row++
        }
    }
}
