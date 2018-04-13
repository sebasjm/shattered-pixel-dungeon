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

import android.opengl.GLES20

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

object Quad {

    // 0---1
    // | \ |
    // 3---2
    val VALUES = shortArrayOf(0, 1, 2, 0, 2, 3)

    val SIZE = VALUES.size

    private var indices: ShortBuffer? = null
    private var indexSize = 0
    private var bufferIndex = -1

    fun create(): FloatBuffer {
        return ByteBuffer.allocateDirect(16 * java.lang.Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer()
    }

    fun createSet(size: Int): FloatBuffer {
        return ByteBuffer.allocateDirect(size * 16 * java.lang.Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer()
    }

    //sets up for drawing up to 32k quads in one command, shouldn't ever need to exceed this
    fun setupIndices() {
        val indices = getIndices(java.lang.Short.MAX_VALUE.toInt())
        if (bufferIndex == -1) {
            val buf = IntArray(1)
            GLES20.glGenBuffers(1, buf, 0)
            bufferIndex = buf[0]
        }
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferIndex)
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * 2, indices, GLES20.GL_STATIC_DRAW)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun bindIndices() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferIndex)
    }

    fun releaseIndices() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun getIndices(size: Int): ShortBuffer {

        if (size > indexSize) {

            indexSize = size
            indices = ByteBuffer.allocateDirect(size * SIZE * java.lang.Short.SIZE / 8).order(ByteOrder.nativeOrder()).asShortBuffer()

            val values = ShortArray(size * 6)
            var pos = 0
            val limit = size * 4
            var ofs = 0
            while (ofs < limit) {
                values[pos++] = (ofs + 0).toShort()
                values[pos++] = (ofs + 1).toShort()
                values[pos++] = (ofs + 2).toShort()
                values[pos++] = (ofs + 0).toShort()
                values[pos++] = (ofs + 2).toShort()
                values[pos++] = (ofs + 3).toShort()
                ofs += 4
            }

            indices!!.put(values)
            indices!!.position(0)
        }

        return indices!!
    }

    fun fill(v: FloatArray,
             x1: Float, x2: Float, y1: Float, y2: Float,
             u1: Float, u2: Float, v1: Float, v2: Float) {

        v[0] = x1
        v[1] = y1
        v[2] = u1
        v[3] = v1

        v[4] = x2
        v[5] = y1
        v[6] = u2
        v[7] = v1

        v[8] = x2
        v[9] = y2
        v[10] = u2
        v[11] = v2

        v[12] = x1
        v[13] = y2
        v[14] = u1
        v[15] = v2
    }

    fun fillXY(v: FloatArray, x1: Float, x2: Float, y1: Float, y2: Float) {

        v[0] = x1
        v[1] = y1

        v[4] = x2
        v[5] = y1

        v[8] = x2
        v[9] = y2

        v[12] = x1
        v[13] = y2
    }

    fun fillUV(v: FloatArray, u1: Float, u2: Float, v1: Float, v2: Float) {

        v[2] = u1
        v[3] = v1

        v[6] = u2
        v[7] = v1

        v[10] = u2
        v[11] = v2

        v[14] = u1
        v[15] = v2
    }
}
