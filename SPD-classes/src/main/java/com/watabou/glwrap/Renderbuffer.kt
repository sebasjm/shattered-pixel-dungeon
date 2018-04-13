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

class Renderbuffer {

    private val id: Int

    init {
        val buffers = IntArray(1)
        GLES20.glGenRenderbuffers(1, buffers, 0)
        id = buffers[0]
    }

    fun id(): Int {
        return id
    }

    fun bind() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, id)
    }

    fun delete() {
        val buffers = intArrayOf(id)
        GLES20.glDeleteRenderbuffers(1, buffers, 0)
    }

    fun storage(format: Int, width: Int, height: Int) {
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, format, width, height)
    }

    companion object {

        val RGBA8 = GLES20.GL_RGBA    // ?
        val DEPTH16 = GLES20.GL_DEPTH_COMPONENT16
        val STENCIL8 = GLES20.GL_STENCIL_INDEX8
    }
}
