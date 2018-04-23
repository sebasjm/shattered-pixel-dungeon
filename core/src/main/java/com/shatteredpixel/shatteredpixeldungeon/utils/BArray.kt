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

package com.shatteredpixel.shatteredpixeldungeon.utils

object BArray {

    private var falseArray: BooleanArray? = null

    //This is MUCH faster than making a new boolean[] or using Arrays.fill;
    fun setFalse(toBeFalse: BooleanArray) {
        if (falseArray == null || falseArray!!.size < toBeFalse.size)
            falseArray = BooleanArray(toBeFalse.size)

        System.arraycopy(falseArray!!, 0, toBeFalse, 0, toBeFalse.size)
    }

    fun and(a: BooleanArray, b: BooleanArray, result: BooleanArray?): BooleanArray {
        var result = result

        val length = a.size

        if (result == null) {
            result = BooleanArray(length)
        }

        for (i in 0 until length) {
            result[i] = a[i] && b[i]
        }

        return result
    }

    fun or(a: BooleanArray, b: BooleanArray, result: BooleanArray?): BooleanArray {
        return or(a, b, 0, a.size, result)
    }

    fun or(a: BooleanArray, b: BooleanArray, offset: Int, length: Int, result: BooleanArray?): BooleanArray {
        var result = result

        if (result == null) {
            result = BooleanArray(length)
        }

        for (i in offset until offset + length) {
            result[i] = a[i] || b[i]
        }

        return result
    }

    fun not(a: BooleanArray, result: BooleanArray?): BooleanArray {
        var result = result

        val length = a.size

        if (result == null) {
            result = BooleanArray(length)
        }

        for (i in 0 until length) {
            result[i] = !a[i]
        }

        return result
    }

    fun `is`(a: IntArray, result: BooleanArray?, v1: Int): BooleanArray {
        var result = result

        val length = a.size

        if (result == null) {
            result = BooleanArray(length)
        }

        for (i in 0 until length) {
            result[i] = a[i] == v1
        }

        return result
    }

    fun isOneOf(a: IntArray, result: BooleanArray?, vararg v: Int): BooleanArray {
        var result = result

        val length = a.size
        val nv = v.size

        if (result == null) {
            result = BooleanArray(length)
        }

        for (i in 0 until length) {
            result[i] = false
            for (j in 0 until nv) {
                if (a[i] == v[j]) {
                    result[i] = true
                    break
                }
            }
        }

        return result
    }

    fun isNot(a: IntArray, result: BooleanArray?, v1: Int): BooleanArray {
        var result = result

        val length = a.size

        if (result == null) {
            result = BooleanArray(length)
        }

        for (i in 0 until length) {
            result[i] = a[i] != v1
        }

        return result
    }

    fun isNotOneOf(a: IntArray, result: BooleanArray?, vararg v: Int): BooleanArray {
        var result = result

        val length = a.size
        val nv = v.size

        if (result == null) {
            result = BooleanArray(length)
        }

        for (i in 0 until length) {
            result[i] = true
            for (j in 0 until nv) {
                if (a[i] == v[j]) {
                    result[i] = false
                    break
                }
            }
        }

        return result
    }
}
