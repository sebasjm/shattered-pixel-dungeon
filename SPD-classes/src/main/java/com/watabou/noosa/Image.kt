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

import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Quad
import com.watabou.glwrap.Vertexbuffer
import com.watabou.utils.RectF

import java.nio.FloatBuffer

open class Image() : Visual(0, 0, 0, 0) {

    var texture: SmartTexture? = null
    protected var frame: RectF

    var flipHorizontal: Boolean = false
    var flipVertical: Boolean = false

    protected var vertices: FloatArray
    protected var verticesBuffer: FloatBuffer
    protected var buffer: Vertexbuffer? = null

    protected var dirty: Boolean = false

    init {

        vertices = FloatArray(16)
        verticesBuffer = Quad.create()
    }

    constructor(src: Image) : this() {
        copy(src)
    }

    constructor(tx: Any) : this() {
        texture(tx)
    }

    constructor(tx: Any, left: Int, top: Int, width: Int, height: Int) : this(tx) {
        frame(texture!!.uvRect(left.toFloat(), top.toFloat(), (left + width).toFloat(), (top + height).toFloat()))
    }

    fun texture(tx: Any) {
        texture = tx as? SmartTexture ?: TextureCache.get(tx)
        frame(RectF(0f, 0f, 1f, 1f))
    }

    open fun frame(frame: RectF) {
        this.frame = frame

        width = frame.width() * texture!!.width
        height = frame.height() * texture!!.height

        updateFrame()
        updateVertices()
    }

    fun frame(left: Int, top: Int, width: Int, height: Int) {
        frame(texture!!.uvRect(left.toFloat(), top.toFloat(), (left + width).toFloat(), (top + height).toFloat()))
    }

    fun frame(): RectF {
        return RectF(frame)
    }

    fun copy(other: Image) {
        texture = other.texture
        frame = RectF(other.frame)

        width = other.width
        height = other.height

        updateFrame()
        updateVertices()
    }

    protected open fun updateFrame() {

        if (flipHorizontal) {
            vertices[2] = frame.right
            vertices[6] = frame.left
            vertices[10] = frame.left
            vertices[14] = frame.right
        } else {
            vertices[2] = frame.left
            vertices[6] = frame.right
            vertices[10] = frame.right
            vertices[14] = frame.left
        }

        if (flipVertical) {
            vertices[3] = frame.bottom
            vertices[7] = frame.bottom
            vertices[11] = frame.top
            vertices[15] = frame.top
        } else {
            vertices[3] = frame.top
            vertices[7] = frame.top
            vertices[11] = frame.bottom
            vertices[15] = frame.bottom
        }

        dirty = true
    }

    protected fun updateVertices() {

        vertices[0] = 0f
        vertices[1] = 0f

        vertices[4] = width
        vertices[5] = 0f

        vertices[8] = width
        vertices[9] = height

        vertices[12] = 0f
        vertices[13] = height

        dirty = true
    }

    override fun draw() {

        if (texture == null || !dirty && buffer == null)
            return

        super.draw()

        if (dirty) {
            verticesBuffer.position(0)
            verticesBuffer.put(vertices)
            if (buffer == null)
                buffer = Vertexbuffer(verticesBuffer)
            else
                buffer!!.updateVertices(verticesBuffer)
            dirty = false
        }

        val script = script()

        texture!!.bind()

        script.camera(camera())

        script.uModel.valueM4(matrix)
        script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa)

        script.drawQuad(buffer)

    }

    protected open fun script(): NoosaScript {
        return NoosaScript.get()
    }

    override fun destroy() {
        super.destroy()
        if (buffer != null)
            buffer!!.delete()
    }
}
