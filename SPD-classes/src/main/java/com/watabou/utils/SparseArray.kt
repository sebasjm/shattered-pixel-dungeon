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

class SparseArray<T> : android.util.SparseArray<T>() {

    fun keyArray(): IntArray {
        val size = size()
        val array = IntArray(size)
        for (i in 0 until size) {
            array[i] = keyAt(i)
        }
        return array
    }

    fun values(): List<T> {
        val size = size()
        val list = ArrayList<T>(size)
        for (i in 0 until size) {
            list.add(i, valueAt(i))
        }
        return list
    }
}
