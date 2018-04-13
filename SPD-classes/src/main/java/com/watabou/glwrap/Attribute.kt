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
import android.os.Build

import java.nio.FloatBuffer

class Attribute(private val location: Int) {

    fun location(): Int {
        return location
    }

    fun enable() {
        GLES20.glEnableVertexAttribArray(location)
    }

    fun disable() {
        GLES20.glDisableVertexAttribArray(location)
    }

    fun vertexPointer(size: Int, stride: Int, ptr: FloatBuffer) {
        GLES20.glVertexAttribPointer(location, size, GLES20.GL_FLOAT, false, stride * 4, ptr)
    }

    fun vertexBuffer(size: Int, stride: Int, offset: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            GLES20.glVertexAttribPointer(location, size, GLES20.GL_FLOAT, false, stride * 4, offset * 4)
        } else {
            FroyoGLES20Fix.glVertexAttribPointer(location, size, GLES20.GL_FLOAT, false, stride * 4, offset * 4)
        }
    }
}
