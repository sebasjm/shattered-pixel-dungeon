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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import com.watabou.glwrap.Texture
import com.watabou.noosa.Game

import java.util.HashMap

object TextureCache {

    var context: Context? = null

    private val all = HashMap<Any, SmartTexture>()

    // No dithering, no scaling, 32 bits per pixel
    private val bitmapOptions = BitmapFactory.Options()

    init {
        bitmapOptions.inScaled = false
        bitmapOptions.inDither = false
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    @Synchronized
    fun createSolid(color: Int): SmartTexture {
        val key = "1x1:$color"

        if (all.containsKey(key)) {

            return all[key]!!

        } else {

            val bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            bmp.eraseColor(color)

            val tx = SmartTexture(bmp)
            all[key] = tx

            return tx
        }
    }

    @Synchronized
    fun createGradient(vararg colors: Int): SmartTexture {

        val key = "" + colors

        if (all.containsKey(key)) {

            return all[key]!!

        } else {

            val bmp = Bitmap.createBitmap(colors.size, 1, Bitmap.Config.ARGB_8888)
            for (i in colors.indices) {
                bmp.setPixel(i, 0, colors[i])
            }
            val tx = SmartTexture(bmp)

            tx.filter(Texture.LINEAR, Texture.LINEAR)
            tx.wrap(Texture.CLAMP, Texture.CLAMP)

            all[key] = tx
            return tx
        }

    }

    @Synchronized
    fun add(key: Any, tx: SmartTexture) {
        all[key] = tx
    }

    @Synchronized
    fun remove(key: Any) {
        val tx = all[key]
        if (tx != null) {
            all.remove(key)
            tx.delete()
        }
    }

    @Synchronized
    operator fun get(src: Any): SmartTexture {

        if (all.containsKey(src)) {

            return all[src]!!

        } else if (src is SmartTexture) {

            return src

        } else {

            val tx = SmartTexture(getBitmap(src)!!)
            all[src] = tx
            return tx
        }

    }

    @Synchronized
    fun clear() {

        for (txt in all.values) {
            txt.delete()
        }
        all.clear()

    }

    @Synchronized
    fun reload() {
        for (tx in all.values) {
            tx.reload()
        }
    }

    fun getBitmap(src: Any): Bitmap? {

        try {
            return if (src is Int) {

                BitmapFactory.decodeResource(
                        context!!.resources, src, bitmapOptions)

            } else if (src is String) {

                BitmapFactory.decodeStream(
                        context!!.assets.open(src), null, bitmapOptions)

            } else src as? Bitmap
        } catch (e: Exception) {

            Game.reportException(e)
            return null

        }

    }

    @Synchronized
    operator fun contains(key: Any): Boolean {
        return all.containsKey(key)
    }

}
