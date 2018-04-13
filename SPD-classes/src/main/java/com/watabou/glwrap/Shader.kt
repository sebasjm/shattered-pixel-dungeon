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

class Shader(type: Int) {

    private val handle: Int

    init {
        handle = GLES20.glCreateShader(type)
    }

    fun handle(): Int {
        return handle
    }

    fun source(src: String) {
        GLES20.glShaderSource(handle, src)
    }

    fun compile() {
        GLES20.glCompileShader(handle)

        val status = IntArray(1)
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == GLES20.GL_FALSE) {
            throw Error(GLES20.glGetShaderInfoLog(handle))
        }
    }

    fun delete() {
        GLES20.glDeleteShader(handle)
    }

    companion object {

        val VERTEX = GLES20.GL_VERTEX_SHADER
        val FRAGMENT = GLES20.GL_FRAGMENT_SHADER

        fun createCompiled(type: Int, src: String): Shader {
            val shader = Shader(type)
            shader.source(src)
            shader.compile()
            return shader
        }
    }
}
