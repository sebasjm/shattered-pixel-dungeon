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

import java.util.Collections
import java.util.HashMap

object Random {

    private var rand = java.util.Random()

    fun seed() {
        rand = java.util.Random()
    }

    fun seed(seed: Long) {
        rand.setSeed(seed)
    }

    //returns a uniformly distributed float in the range [0, 1)
    fun Float(): Float {
        return rand.nextFloat()
    }

    //returns a uniformly distributed float in the range [0, max)
    fun Float(max: Float): Float {
        return Float() * max
    }

    //returns a uniformly distributed float in the range [min, max)
    fun Float(min: Float, max: Float): Float {
        return min + Float(max - min)
    }

    //returns a uniformly distributed int in the range [0, max)
    fun Int(max: Int): Int {
        return if (max > 0) rand.nextInt(max) else 0
    }

    //returns a uniformly distributed int in the range [min, max)
    fun Int(min: Int, max: Int): Int {
        return min + Int(max - min)
    }

    //returns a uniformly distributed int in the range [min, max]
    fun IntRange(min: Int, max: Int): Int {
        return min + Int(max - min + 1)
    }

    //returns a triangularly distributed int in the range [min, max]
    fun NormalIntRange(min: Int, max: Int): Int {
        return min + ((Float() + Float()) * (max - min + 1) / 2f).toInt()
    }

    //returns a uniformly distributed long in the range [-2^63, 2^63)
    fun Long(): Long {
        return rand.nextLong()
    }

    //returns a uniformly distributed long in the range [0, max)
    fun Long(max: Long): Long {
        var result = Long()
        if (result < 0) result += java.lang.Long.MAX_VALUE
        return result % max
    }

    //returns an index from chances, the probability of each index is the weight values in changes
    fun chances(chances: FloatArray): Int {

        val length = chances.size

        var sum = 0f
        for (i in 0 until length) {
            sum += chances[i]
        }

        val value = Float(sum)
        sum = 0f
        for (i in 0 until length) {
            sum += chances[i]
            if (value < sum) {
                return i
            }
        }

        return -1
    }

    //returns a key element from chances, the probability of each key is the weight value it maps to
    fun <K> chances(chances: HashMap<K, Float>): K? {

        val size = chances.size

        val values = chances.keys.toTypedArray()
        val probs = FloatArray(size)
        var sum = 0f
        for (i in 0 until size) {
            probs[i] = chances[values[i]]
            sum += probs[i]
        }

        if (sum <= 0) {
            return null
        }

        val value = Float(sum)

        sum = probs[0]
        for (i in 0 until size) {
            if (value < sum) {
                return values[i]
            }
            sum += probs[i + 1]
        }

        return null
    }

    fun index(collection: Collection<*>): Int {
        return Int(collection.size)
    }

    @SafeVarargs
    fun <T> oneOf(vararg array: T): T {
        return array[Int(array.size)]
    }

    fun <T> element(array: Array<T>): T {
        return element(array, array.size)
    }

    fun <T> element(array: Array<T>, max: Int): T {
        return array[Int(max)]
    }

    fun <T> element(collection: Collection<T>): T? {
        val size = collection.size
        return if (size > 0)
            collection.toTypedArray()[Int(size)]
        else
            null
    }

    fun <T> shuffle(list: List<T>) {
        Collections.shuffle(list, rand)
    }

    fun <T> shuffle(array: Array<T>) {
        for (i in 0 until array.size - 1) {
            val j = Int(i, array.size)
            if (j != i) {
                val t = array[i]
                array[i] = array[j]
                array[j] = t
            }
        }
    }

    fun <U, V> shuffle(u: Array<U>, v: Array<V>) {
        for (i in 0 until u.size - 1) {
            val j = Int(i, u.size)
            if (j != i) {
                val ut = u[i]
                u[i] = u[j]
                u[j] = ut

                val vt = v[i]
                v[i] = v[j]
                v[j] = vt
            }
        }
    }
}
