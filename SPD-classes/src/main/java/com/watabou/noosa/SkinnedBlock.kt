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

import com.watabou.glwrap.Texture
import com.watabou.utils.RectF

open class SkinnedBlock(width: Float, height: Float, tx: Any) : Image(tx) {

    protected var scaleX: Float = 0.toFloat()
    protected var scaleY: Float = 0.toFloat()

    protected var offsetX: Float = 0.toFloat()
    protected var offsetY: Float = 0.toFloat()

    var autoAdjust = false

    init {

        texture!!.wrap(Texture.REPEAT, Texture.REPEAT)

        size(width, height)
    }

    override fun frame(frame: RectF) {
        scaleX = 1f
        scaleY = 1f

        offsetX = 0f
        offsetY = 0f

        super.frame(RectF(0f, 0f, 1f, 1f))
    }

    override fun updateFrame() {

        if (autoAdjust) {
            while (offsetX > texture!!.width) {
                offsetX -= texture!!.width.toFloat()
            }
            while (offsetX < -texture!!.width) {
                offsetX += texture!!.width.toFloat()
            }
            while (offsetY > texture!!.height) {
                offsetY -= texture!!.height.toFloat()
            }
            while (offsetY < -texture!!.height) {
                offsetY += texture!!.height.toFloat()
            }
        }

        val tw = 1f / texture!!.width
        val th = 1f / texture!!.height

        val u0 = offsetX * tw
        val v0 = offsetY * th
        val u1 = u0 + width * tw / scaleX
        val v1 = v0 + height * th / scaleY

        vertices[2] = u0
        vertices[3] = v0

        vertices[6] = u1
        vertices[7] = v0

        vertices[10] = u1
        vertices[11] = v1

        vertices[14] = u0
        vertices[15] = v1

        dirty = true
    }

    fun offsetTo(x: Float, y: Float) {
        offsetX = x
        offsetY = y
        updateFrame()
    }

    fun offset(x: Float, y: Float) {
        offsetX += x
        offsetY += y
        updateFrame()
    }

    fun offsetX(): Float {
        return offsetX
    }

    fun offsetY(): Float {
        return offsetY
    }

    fun scale(x: Float, y: Float) {
        scaleX = x
        scaleY = y
        updateFrame()
    }

    fun size(w: Float, h: Float) {
        this.width = w
        this.height = h
        updateFrame()
        updateVertices()
    }
}
