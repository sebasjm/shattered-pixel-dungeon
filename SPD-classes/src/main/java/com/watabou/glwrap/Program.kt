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

open class Program {

    private val handle: Int

    init {
        handle = GLES20.glCreateProgram()
    }

    fun handle(): Int {
        return handle
    }

    fun attach(shader: Shader) {
        GLES20.glAttachShader(handle, shader.handle())
    }

    fun link() {
        GLES20.glLinkProgram(handle)

        val status = IntArray(1)
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] == GLES20.GL_FALSE) {
            throw Error(GLES20.glGetProgramInfoLog(handle))
        }
    }

    fun attribute(name: String): Attribute {
        return Attribute(GLES20.glGetAttribLocation(handle, name))
    }

    fun uniform(name: String): Uniform {
        return Uniform(GLES20.glGetUniformLocation(handle, name))
    }

    open fun use() {
        GLES20.glUseProgram(handle)
    }

    fun delete() {
        GLES20.glDeleteProgram(handle)
    }

    companion object {

        fun create(vararg shaders: Shader): Program {
            val program = Program()
            for (i in shaders.indices) {
                program.attach(shaders[i])
            }
            program.link()
            return program
        }
    }
}
