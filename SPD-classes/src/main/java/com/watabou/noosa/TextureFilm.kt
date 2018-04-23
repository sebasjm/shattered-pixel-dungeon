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
import com.watabou.utils.RectF

import java.util.HashMap

open class TextureFilm {

    private var texWidth: Int = 0
    private var texHeight: Int = 0

    private var texture: SmartTexture? = null

    protected var frames = HashMap<Any?, RectF>()

    constructor(tx: Any) {

        texture = TextureCache.get(tx)

        texWidth = texture!!.width
        texHeight = texture!!.height

        add(null, FULL)
    }

    constructor(texture: SmartTexture, width: Int) : this(texture, width, texture.height) {}

    constructor(tx: Any, width: Int, height: Int) {

        texture = TextureCache.get(tx)

        texWidth = texture!!.width
        texHeight = texture!!.height

        val uw = width.toFloat() / texWidth
        val vh = height.toFloat() / texHeight
        val cols = texWidth / width
        val rows = texHeight / height

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val rect = RectF(j * uw, i * vh, (j + 1) * uw, (i + 1) * vh)
                add(i * cols + j, rect)
            }
        }
    }

    constructor(atlas: TextureFilm, key: Any, width: Int, height: Int) {

        texture = atlas.texture

        texWidth = atlas.texWidth
        texHeight = atlas.texHeight

        val patch = atlas[key]!!

        val uw = width.toFloat() / texWidth
        val vh = height.toFloat() / texHeight
        val cols = (width(patch) / width).toInt()
        val rows = (height(patch) / height).toInt()

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val rect = RectF(j * uw, i * vh, (j + 1) * uw, (i + 1) * vh)
                rect.shift(patch.left, patch.top)
                add(i * cols + j, rect)
            }
        }
    }

    fun add(id: Any?, rect: RectF) {
        frames[id] = rect
    }

    fun add(id: Any, left: Int, top: Int, right: Int, bottom: Int) {
        frames[id] = texture!!.uvRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    operator fun get(id: Any): RectF? {
        return frames[id]
    }

    fun width(id: Any): Float {
        return width(get(id)!!)
    }

    fun width(frame: RectF): Float {
        return frame.width() * texWidth
    }

    fun height(id: Any): Float {
        return height(get(id)!!)
    }

    fun height(frame: RectF): Float {
        return frame.height() * texHeight
    }

    companion object {

        private val FULL = RectF(0f, 0f, 1f, 1f)
    }
}