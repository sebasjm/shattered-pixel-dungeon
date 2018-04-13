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

class Uniform(private val location: Int) {

    fun location(): Int {
        return location
    }

    fun enable() {
        GLES20.glEnableVertexAttribArray(location)
    }

    fun disable() {
        GLES20.glDisableVertexAttribArray(location)
    }

    fun value1f(value: Float) {
        GLES20.glUniform1f(location, value)
    }

    fun value2f(v1: Float, v2: Float) {
        GLES20.glUniform2f(location, v1, v2)
    }

    fun value4f(v1: Float, v2: Float, v3: Float, v4: Float) {
        GLES20.glUniform4f(location, v1, v2, v3, v4)
    }

    fun valueM3(value: FloatArray) {
        GLES20.glUniformMatrix3fv(location, 1, false, value, 0)
    }

    fun valueM4(value: FloatArray) {
        GLES20.glUniformMatrix4fv(location, 1, false, value, 0)
    }
}