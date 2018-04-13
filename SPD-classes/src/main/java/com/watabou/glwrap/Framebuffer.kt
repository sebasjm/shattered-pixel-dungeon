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

class Framebuffer {

    private val id: Int

    constructor() {
        val buffers = IntArray(1)
        GLES20.glGenBuffers(1, buffers, 0)
        id = buffers[0]
    }

    private constructor(n: Int): this() {

    }

    fun bind() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id)
    }

    fun delete() {
        val buffers = intArrayOf(id)
        GLES20.glDeleteFramebuffers(1, buffers, 0)
    }

    fun attach(point: Int, tex: Texture) {
        bind()
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, point, GLES20.GL_TEXTURE_2D, tex.id, 0)
    }

    fun attach(point: Int, buffer: Renderbuffer) {
        bind()
        GLES20.glFramebufferRenderbuffer(GLES20.GL_RENDERBUFFER, point, GLES20.GL_TEXTURE_2D, buffer.id())
    }

    fun status(): Boolean {
        bind()
        return GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) == GLES20.GL_FRAMEBUFFER_COMPLETE
    }

    companion object {

        val COLOR = GLES20.GL_COLOR_ATTACHMENT0
        val DEPTH = GLES20.GL_DEPTH_ATTACHMENT
        val STENCIL = GLES20.GL_STENCIL_ATTACHMENT

        val system = Framebuffer(0)
    }
}
