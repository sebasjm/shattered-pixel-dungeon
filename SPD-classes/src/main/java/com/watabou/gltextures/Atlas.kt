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

package com.watabou.gltextures

import com.watabou.utils.RectF

import java.util.HashMap

class Atlas(var tx: SmartTexture) {

    protected var namedFrames: HashMap<Any, RectF>

    protected var uvLeft: Float = 0.toFloat()
    protected var uvTop: Float = 0.toFloat()
    protected var uvWidth: Float = 0.toFloat()
    protected var uvHeight: Float = 0.toFloat()
    protected var cols: Int = 0

    init {
        tx.atlas = this

        namedFrames = HashMap()
    }

    fun add(key: Any, left: Int, top: Int, right: Int, bottom: Int) {
        add(key, uvRect(tx, left, top, right, bottom))
    }

    fun add(key: Any, rect: RectF) {
        namedFrames[key] = rect
    }

    @JvmOverloads
    fun grid(width: Int, height: Int = tx.height) {
        grid(0, 0, width, height, tx.width / width)
    }

    fun grid(left: Int, top: Int, width: Int, height: Int, cols: Int) {
        uvLeft = left.toFloat() / tx.width
        uvTop = top.toFloat() / tx.height
        uvWidth = width.toFloat() / tx.width
        uvHeight = height.toFloat() / tx.height
        this.cols = cols
    }

    operator fun get(index: Int): RectF {
        val x = (index % cols).toFloat()
        val y = (index / cols).toFloat()
        val l = uvLeft + x * uvWidth
        val t = uvTop + y * uvHeight
        return RectF(l, t, l + uvWidth, t + uvHeight)
    }

    operator fun get(key: Any): RectF {
        return namedFrames[key]
    }

    fun width(rect: RectF): Float {
        return rect.width() * tx.width
    }

    fun height(rect: RectF): Float {
        return rect.height() * tx.height
    }

    companion object {

        fun uvRect(tx: SmartTexture, left: Int, top: Int, right: Int, bottom: Int): RectF {
            return RectF(
                    left.toFloat() / tx.width,
                    top.toFloat() / tx.height,
                    right.toFloat() / tx.width,
                    bottom.toFloat() / tx.height)
        }
    }
}
