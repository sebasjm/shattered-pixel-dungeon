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

import java.nio.FloatBuffer
import java.util.ArrayList

class Vertexbuffer(vertices: FloatBuffer) {

    private var id: Int = 0
    private var vertices: FloatBuffer? = null
    private var updateStart: Int = 0
    private var updateEnd: Int = 0

    init {
        synchronized(buffers) {
            val ptr = IntArray(1)
            GLES20.glGenBuffers(1, ptr, 0)
            id = ptr[0]

            this.vertices = vertices
            buffers.add(this)

            updateStart = 0
            updateEnd = vertices.limit()
        }
    }

    //For flagging an update with a subset of data changed
    @JvmOverloads
    fun updateVertices(vertices: FloatBuffer? = vertices, start: Int = 0, end: Int = vertices!!.limit()) {
        this.vertices = vertices

        if (updateStart == -1)
            updateStart = start
        else
            updateStart = Math.min(start, updateStart)

        if (updateEnd == -1)
            updateEnd = end
        else
            updateEnd = Math.max(end, updateEnd)
    }

    fun updateGLData() {
        if (updateStart == -1) return

        vertices!!.position(updateStart)
        bind()

        if (updateStart == 0 && updateEnd == vertices!!.limit()) {
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices!!.limit() * 4, vertices, GLES20.GL_DYNAMIC_DRAW)
        } else {
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, updateStart * 4, (updateEnd - updateStart) * 4, vertices)
        }

        release()
        updateEnd = -1
        updateStart = updateEnd
    }

    fun bind() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id)
    }

    fun release() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    fun delete() {
        synchronized(buffers) {
            GLES20.glDeleteBuffers(1, intArrayOf(id), 0)
            buffers.remove(this)
        }
    }

    companion object {

        private val buffers = ArrayList<Vertexbuffer>()

        fun refreshAllBuffers() {
            synchronized(buffers) {
                for (buf in buffers) {
                    buf.updateVertices()
                    buf.updateGLData()
                }
            }
        }
    }

}//For flagging the buffer for a full update without changing anything
//For flagging an update with a full set of new data
