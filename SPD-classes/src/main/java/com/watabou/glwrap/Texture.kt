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

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

open class Texture {

    var id = -1

    var premultiplied = false

    protected open fun generate() {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        id = ids[0]
    }

    fun bind() {
        if (id == -1) {
            generate()
        }
        if (id != bound_id) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
            bound_id = id
        }
    }

    open fun filter(minMode: Int, maxMode: Int) {
        bind()
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minMode.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, maxMode.toFloat())
    }

    open fun wrap(s: Int, t: Int) {
        bind()
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, s.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, t.toFloat())
    }

    open fun delete() {
        if (bound_id == id) bound_id = 0
        val ids = intArrayOf(id)
        GLES20.glDeleteTextures(1, ids, 0)
    }

    open fun bitmap(bitmap: Bitmap) {
        bind()
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        premultiplied = true
    }

    fun pixels(w: Int, h: Int, pixels: IntArray) {

        bind()

        val imageBuffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
        imageBuffer.put(pixels)
        imageBuffer.position(0)

        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                w,
                h,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                imageBuffer)
    }

    fun pixels(w: Int, h: Int, pixels: ByteArray) {

        bind()

        val imageBuffer = ByteBuffer.allocateDirect(w * h).order(ByteOrder.nativeOrder())
        imageBuffer.put(pixels)
        imageBuffer.position(0)

        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1)

        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_ALPHA,
                w,
                h,
                0,
                GLES20.GL_ALPHA,
                GLES20.GL_UNSIGNED_BYTE,
                imageBuffer)
    }

    // If getConfig returns null (unsupported format?), GLUtils.texImage2D works
    // incorrectly. In this case we need to load pixels manually
    fun handMade(bitmap: Bitmap, recode: Boolean) {

        val w = bitmap.width
        val h = bitmap.height

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        // recode - components reordering is needed
        if (recode) {
            for (i in pixels.indices) {
                val color = pixels[i]
                val ag = color and -0xff0100
                val r = color shr 16 and 0xFF
                val b = color and 0xFF
                pixels[i] = ag or (b shl 16) or r
            }
        }

        pixels(w, h, pixels)

        premultiplied = false
    }

    companion object {

        val NEAREST = GLES20.GL_NEAREST
        val LINEAR = GLES20.GL_LINEAR

        val REPEAT = GLES20.GL_REPEAT
        val MIRROR = GLES20.GL_MIRRORED_REPEAT
        val CLAMP = GLES20.GL_CLAMP_TO_EDGE
        private var bound_id = 0 //id of the currently bound texture

        fun activate(index: Int) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index)
        }

        fun create(bmp: Bitmap): Texture {
            val tex = Texture()
            tex.bitmap(bmp)

            return tex
        }

        fun create(width: Int, height: Int, pixels: IntArray): Texture {
            val tex = Texture()
            tex.pixels(width, height, pixels)

            return tex
        }

        fun create(width: Int, height: Int, pixels: ByteArray): Texture {
            val tex = Texture()
            tex.pixels(width, height, pixels)

            return tex
        }
    }
}
