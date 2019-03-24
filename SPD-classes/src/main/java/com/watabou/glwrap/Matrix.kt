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

package com.watabou.glwrap

object Matrix {

    val G2RAD = 0.01745329251994329576923690768489f

    private val identity = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)

    fun clone(m: FloatArray): FloatArray {

        var n = m.size
        val res = FloatArray(n)
        do {
            res[--n] = m[n]
        } while (n > 0)

        return res
    }

    fun copy(src: FloatArray, dst: FloatArray) {

        var n = src.size
        do {
            dst[--n] = src[n]
        } while (n > 0)

    }

    fun setIdentity(m: FloatArray) {
        System.arraycopy(identity, 0, m, 0, identity.size)
    }

    fun rotate(m: FloatArray, a: Float) {
        var a = a
        a *= G2RAD
        val sin = Math.sin(a.toDouble()).toFloat()
        val cos = Math.cos(a.toDouble()).toFloat()
        val m0 = m[0]
        val m1 = m[1]
        val m4 = m[4]
        val m5 = m[5]
        m[0] = m0 * cos + m4 * sin
        m[1] = m1 * cos + m5 * sin
        m[4] = -m0 * sin + m4 * cos
        m[5] = -m1 * sin + m5 * cos
    }

    fun skewX(m: FloatArray, a: Float) {
        val t = Math.tan((a * G2RAD).toDouble())
        m[4] += (-m[0] * t).toFloat()
        m[5] += (-m[1] * t).toFloat()
    }

    fun skewY(m: FloatArray, a: Float) {
        val t = Math.tan((a * G2RAD).toDouble())
        m[0] += (m[4] * t).toFloat()
        m[1] += (m[5] * t).toFloat()
    }

    fun scale(m: FloatArray, x: Float, y: Float) {
        m[0] *= x
        m[1] *= x
        m[2] *= x
        m[3] *= x
        m[4] *= y
        m[5] *= y
        m[6] *= y
        m[7] *= y
        //	android.opengl.Matrix.scaleM( m, 0, x, y, 1 );
    }

    fun translate(m: FloatArray, x: Float, y: Float) {
        m[12] += m[0] * x + m[4] * y
        m[13] += m[1] * x + m[5] * y
    }

//    fun multiply(left: FloatArray, right: FloatArray, result: FloatArray) {
//        android.opengl.Matrix.multiplyMM(result, 0, left, 0, right, 0)
//    }
}