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

package com.shatteredpixel.shatteredpixeldungeon.effects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Image

open class Halo() : Image() {

    protected var radius = RADIUS.toFloat()
    protected var brightness = 1f

    init {

        if (!TextureCache.contains(CACHE_KEY)) {
            val bmp = Bitmap.createBitmap(RADIUS * 2, RADIUS * 2, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.color = 0x0AFFFFFF
            for (i in 0..49) {
                canvas.drawCircle(RADIUS.toFloat(), RADIUS.toFloat(), RADIUS * (i + 1) / 50f, paint)
            }
            TextureCache.add(CACHE_KEY, SmartTexture(bmp))
        }

        texture(CACHE_KEY)
    }

    constructor(radius: Float, color: Int, brightness: Float) : this() {

        hardlight(color)
        this.brightness = brightness
        alpha(this.brightness)
        radius(radius)
    }

    fun point(x: Float, y: Float): Halo {
        this.x = x - width() / 2f
        this.y = y - height() / 2f
        PixelScene.align(this)
        return this
    }

    fun radius(value: Float) {
        this.radius = value
        scale.set(this.radius / RADIUS)
    }

    companion object {

        private val CACHE_KEY = Halo::class.java

        @JvmStatic val RADIUS = 64
    }
}
