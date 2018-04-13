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

open class NinePatch(tx: Any, x: Int, y: Int, w: Int, h: Int, protected var marginLeft: Int, protected var marginTop: Int, protected var marginRight: Int, protected var marginBottom: Int) : Visual(0f, 0f, 0f, 0f) {

    var texture: SmartTexture

    protected var vertices: FloatArray
    protected var quads: FloatBuffer
    protected var buffer: Vertexbuffer? = null

    protected var outterF: RectF
    protected var innerF: RectF

    protected var nWidth: Float = 0.toFloat()
    protected var nHeight: Float = 0.toFloat()

    protected var flipHorizontal: Boolean = false
    protected var flipVertical: Boolean = false

    protected var dirty: Boolean = false

    constructor(tx: Any, margin: Int) : this(tx, margin, margin, margin, margin) {}

    constructor(tx: Any, left: Int, top: Int, right: Int, bottom: Int) : this(tx, 0, 0, 0, 0, left, top, right, bottom) {}

    constructor(tx: Any, x: Int, y: Int, w: Int, h: Int, margin: Int) : this(tx, x, y, w, h, margin, margin, margin, margin) {}

    init {
        var w = w
        var h = h

        texture = TextureCache.get(tx)
        w = if (w == 0) texture.width else w
        h = if (h == 0) texture.height else h

        width = w.toFloat()
        nWidth = width
        height = h.toFloat()
        nHeight = height

        vertices = FloatArray(16)
        quads = Quad.createSet(9)

        outterF = texture.uvRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat())
        innerF = texture.uvRect((x + marginLeft).toFloat(), (y + marginTop).toFloat(), (x + w - marginRight).toFloat(), (y + h - marginBottom).toFloat())

        updateVertices()
    }

    protected fun updateVertices() {

        quads.position(0)

        val right = width - marginRight
        val bottom = height - marginBottom

        val outleft = if (flipHorizontal) outterF.right else outterF.left
        val outright = if (flipHorizontal) outterF.left else outterF.right
        val outtop = if (flipVertical) outterF.bottom else outterF.top
        val outbottom = if (flipVertical) outterF.top else outterF.bottom

        val inleft = if (flipHorizontal) innerF.right else innerF.left
        val inright = if (flipHorizontal) innerF.left else innerF.right
        val intop = if (flipVertical) innerF.bottom else innerF.top
        val inbottom = if (flipVertical) innerF.top else innerF.bottom

        Quad.fill(vertices,
                0f, marginLeft.toFloat(), 0f, marginTop.toFloat(), outleft, inleft, outtop, intop)
        quads.put(vertices)
        Quad.fill(vertices,
                marginLeft.toFloat(), right, 0f, marginTop.toFloat(), inleft, inright, outtop, intop)
        quads.put(vertices)
        Quad.fill(vertices,
                right, width, 0f, marginTop.toFloat(), inright, outright, outtop, intop)
        quads.put(vertices)

        Quad.fill(vertices,
                0f, marginLeft.toFloat(), marginTop.toFloat(), bottom, outleft, inleft, intop, inbottom)
        quads.put(vertices)
        Quad.fill(vertices,
                marginLeft.toFloat(), right, marginTop.toFloat(), bottom, inleft, inright, intop, inbottom)
        quads.put(vertices)
        Quad.fill(vertices,
                right, width, marginTop.toFloat(), bottom, inright, outright, intop, inbottom)
        quads.put(vertices)

        Quad.fill(vertices,
                0f, marginLeft.toFloat(), bottom, height, outleft, inleft, inbottom, outbottom)
        quads.put(vertices)
        Quad.fill(vertices,
                marginLeft.toFloat(), right, bottom, height, inleft, inright, inbottom, outbottom)
        quads.put(vertices)
        Quad.fill(vertices,
                right, width, bottom, height, inright, outright, inbottom, outbottom)
        quads.put(vertices)

        dirty = true
    }

    fun marginLeft(): Int {
        return marginLeft
    }

    fun marginRight(): Int {
        return marginRight
    }

    fun marginTop(): Int {
        return marginTop
    }

    fun marginBottom(): Int {
        return marginBottom
    }

    fun marginHor(): Int {
        return marginLeft + marginRight
    }

    fun marginVer(): Int {
        return marginTop + marginBottom
    }

    fun innerWidth(): Float {
        return width - marginLeft.toFloat() - marginRight.toFloat()
    }

    fun innerHeight(): Float {
        return height - marginTop.toFloat() - marginBottom.toFloat()
    }

    fun innerRight(): Float {
        return width - marginRight
    }

    fun innerBottom(): Float {
        return height - marginBottom
    }

    fun flipHorizontal(value: Boolean) {
        flipHorizontal = value
        updateVertices()
    }

    fun flipVertical(value: Boolean) {
        flipVertical = value
        updateVertices()
    }

    open fun size(width: Float, height: Float) {
        this.width = width
        this.height = height
        updateVertices()
    }

    override fun draw() {

        super.draw()

        if (dirty) {
            if (buffer == null)
                buffer = Vertexbuffer(quads)
            else
                buffer!!.updateVertices(quads)
            dirty = false
        }

        val script = NoosaScript.get()

        texture.bind()

        script.camera(camera())

        script.uModel.valueM4(matrix)
        script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa)

        script.drawQuadSet(buffer!!, 9, 0)

    }

    override fun destroy() {
        super.destroy()
        if (buffer != null)
            buffer!!.delete()
    }
}
