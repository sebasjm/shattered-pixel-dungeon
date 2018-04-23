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

package com.watabou.noosa

import android.graphics.Bitmap

import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Matrix
import com.watabou.glwrap.Quad
import com.watabou.glwrap.Vertexbuffer
import com.watabou.utils.RectF

import java.nio.FloatBuffer

open class BitmapText @JvmOverloads constructor(protected var text: String? = "", protected var font: Font? = null) : Visual(0f, 0f, 0f, 0f) {

    protected var vertices = FloatArray(16)
    protected var quads: FloatBuffer? = null
    protected var buffer: Vertexbuffer? = null

    var realLength: Int = 0

    protected var dirty = true

    constructor(font: Font) : this("", font) {}

    override fun updateMatrix() {
        // "origin" field is ignored
        Matrix.setIdentity(matrix)
        Matrix.translate(matrix, x, y)
        Matrix.scale(matrix, scale.x, scale.y)
        Matrix.rotate(matrix, angle)
    }

    override fun draw() {

        super.draw()

        if (dirty) {
            updateVertices()
            quads!!.limit(quads!!.position())
            if (buffer == null)
                buffer = Vertexbuffer(quads!!)
            else
                buffer!!.updateVertices(quads)
        }

        val script = NoosaScript.get()

        font!!.texture.bind()

        script.camera(camera())

        script.uModel.valueM4(matrix)
        script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa)
        script.drawQuadSet(buffer!!, realLength, 0)

    }

    override fun destroy() {
        super.destroy()
        if (buffer != null)
            buffer!!.delete()
    }

    @Synchronized
    protected open fun updateVertices() {

        width = 0f
        height = 0f

        if (text == null) {
            text = ""
        }

        quads = Quad.createSet(text!!.length)
        realLength = 0

        val length = text!!.length
        for (i in 0 until length) {
            var rect: RectF? = font!![text!![i]]

            if (rect == null) {
                rect = null
            }
            val w = font!!.width(rect!!)
            val h = font!!.height(rect)

            vertices[0] = width
            vertices[1] = 0f

            vertices[2] = rect.left
            vertices[3] = rect.top

            vertices[4] = width + w
            vertices[5] = 0f

            vertices[6] = rect.right
            vertices[7] = rect.top

            vertices[8] = width + w
            vertices[9] = h

            vertices[10] = rect.right
            vertices[11] = rect.bottom

            vertices[12] = width
            vertices[13] = h

            vertices[14] = rect.left
            vertices[15] = rect.bottom

            quads!!.put(vertices)
            realLength++

            width += w + font!!.tracking
            if (h > height) {
                height = h
            }
        }

        if (length > 0) {
            width -= font!!.tracking
        }

        dirty = false

    }

    @Synchronized
    open fun measure() {

        width = 0f
        height = 0f

        if (text == null) {
            text = ""
        }

        val length = text!!.length
        for (i in 0 until length) {
            val rect = font!![text!![i]]!!

            val w = font!!.width(rect)
            val h = font!!.height(rect)

            width += w + font!!.tracking
            if (h > height) {
                height = h
            }
        }

        if (length > 0) {
            width -= font!!.tracking
        }
    }

    open fun baseLine(): Float {
        return font!!.baseLine * scale.y
    }

    fun font(): Font? {
        return font
    }

    @Synchronized
    fun font(value: Font) {
        font = value
    }

    fun text(): String? {
        return text
    }

    @Synchronized
    fun text(str: String?) {
        text = str
        dirty = true
    }

    class Font : TextureFilm {

        var texture: SmartTexture

        var tracking = 0f
        var baseLine: Float = 0.toFloat()

        var lineHeight: Float = 0.toFloat()

        protected constructor(tx: SmartTexture) : super(tx) {

            texture = tx
        }

        constructor(tx: SmartTexture, width: Int, chars: String) : this(tx, width, tx.height, chars) {}

        constructor(tx: SmartTexture, width: Int, height: Int, chars: String) : super(tx) {

            texture = tx

            val length = chars.length

            val uw = width.toFloat() / tx.width
            val vh = height.toFloat() / tx.height

            var left = 0f
            var top = 0f
            var bottom = vh

            for (i in 0 until length) {
                left += uw
                val rect = RectF(left, top, left, bottom)
                add(chars[i], rect)
                if (left >= 1) {
                    left = 0f
                    top = bottom
                    bottom += vh
                }
            }

            baseLine = height.toFloat()
            lineHeight = baseLine
        }

        protected fun splitBy(bitmap: Bitmap, height: Int, color: Int, chars: String) {

            val length = chars.length

            val width = bitmap.width
            val vHeight = height.toFloat() / bitmap.height

            var pos: Int
            var line = 0

            pos = 0
            spaceMeasuring@ while (pos < width) {
                for (j in 0 until height) {
                    if (bitmap.getPixel(pos, j) != color) {
                        break@spaceMeasuring
                    }
                }
                pos++
            }
            add(' ', RectF(0f, 0f, pos.toFloat() / width, vHeight - 0.01f))

            var separator = pos

            for (i in 0 until length) {

                val ch = chars[i]
                if (ch == ' ') {
                    continue
                } else {

                    var found: Boolean

                    do {
                        if (separator >= width) {
                            line += height
                            separator = 0
                        }
                        found = false
                        for (j in line until line + height) {
                            if (bitmap.getPixel(separator, j) != color) {
                                found = true
                                break
                            }
                        }
                        if (!found) separator++
                    } while (!found)
                    var start = separator

                    do {
                        if (++separator >= width) {
                            line += height
                            start = 0
                            separator = start
                            if (line + height >= bitmap.height)
                                break
                        }
                        found = true
                        for (j in line until line + height) {
                            if (bitmap.getPixel(separator, j) != color) {
                                found = false
                                break
                            }
                        }
                    } while (!found)

                    add(ch, RectF(start.toFloat() / width, line.toFloat() / bitmap.height, separator.toFloat() / width, line.toFloat() / bitmap.height + vHeight))
                    separator++
                }
            }

            baseLine = height(frames[chars[0]]!!)
            lineHeight = baseLine
        }

        operator fun get(ch: Char): RectF? {
            return if (frames.containsKey(ch)) {
                super.get(ch)
            } else {
                super.get('?')
            }
        }

        companion object {

            val LATIN_FULL = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007F"

            fun colorMarked(bmp: Bitmap, color: Int, chars: String): Font {
                val font = Font(TextureCache.get(bmp))
                font.splitBy(bmp, bmp.height, color, chars)
                return font
            }

            fun colorMarked(bmp: Bitmap, height: Int, color: Int, chars: String): Font {
                val font = Font(TextureCache.get(bmp))
                font.splitBy(bmp, height, color, chars)
                return font
            }
        }
    }
}
