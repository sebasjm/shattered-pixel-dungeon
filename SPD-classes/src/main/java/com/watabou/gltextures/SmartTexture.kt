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

import android.graphics.Bitmap

import com.watabou.glwrap.Texture
import com.watabou.utils.RectF

open class SmartTexture : Texture {

    var width: Int = 0
    var height: Int = 0

    var fModeMin: Int = 0
    var fModeMax: Int = 0

    var wModeH: Int = 0
    var wModeV: Int = 0

    var bitmap: Bitmap? = null

    var atlas: Atlas? = null

    protected constructor() {
        //useful for subclasses which want to manage their own texture data
        // in cases where android.graphics.bitmap isn't fast enough.

        //subclasses which use this MUST also override some mix of reload/generate/bind
    }

    @JvmOverloads constructor(bitmap: Bitmap, filtering: Int = Texture.NEAREST, wrapping: Int = Texture.CLAMP, premultiplied: Boolean = false) {

        this.bitmap = bitmap
        width = bitmap.width
        height = bitmap.height
        this.fModeMax = filtering
        this.fModeMin = this.fModeMax
        this.wModeV = wrapping
        this.wModeH = this.wModeV
        this.premultiplied = premultiplied

    }

    override fun generate() {
        super.generate()
        bitmap(bitmap!!, premultiplied)
        filter(fModeMin, fModeMax)
        wrap(wModeH, wModeV)
    }

    override fun filter(minMode: Int, maxMode: Int) {
        fModeMin = minMode
        fModeMax = maxMode
        if (id != -1)
            super.filter(fModeMin, fModeMax)
    }

    override fun wrap(s: Int, t: Int) {
        wModeH = s
        wModeV = t
        if (id != -1)
            super.wrap(wModeH, wModeV)
    }

    override fun bitmap(bitmap: Bitmap) {
        bitmap(bitmap, false)
    }

    fun bitmap(bitmap: Bitmap, premultiplied: Boolean) {
        if (premultiplied) {
            super.bitmap(bitmap)
        } else {
            handMade(bitmap, true)
        }

        this.bitmap = bitmap
        width = bitmap.width
        height = bitmap.height
    }

    open fun reload() {
        id = -1
        generate()
    }

    override fun delete() {

        super.delete()

        if (bitmap != null)
            bitmap!!.recycle()
        bitmap = null
    }

    fun uvRect(left: Float, top: Float, right: Float, bottom: Float): RectF {
        return RectF(
                left / width,
                top / height,
                right / width,
                bottom / height)
    }
}
