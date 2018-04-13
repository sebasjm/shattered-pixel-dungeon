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

package com.watabou.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import java.io.IOException
import java.io.InputStream
import java.util.HashMap

object BitmapCache {

    private val DEFAULT = "__default"

    private val layers = HashMap<String, BitmapCache.Layer>()

    private val opts = BitmapFactory.Options()

    var context: Context? = null

    init {
        opts.inDither = false
    }

    operator fun get(assetName: String): Bitmap? {
        return get(DEFAULT, assetName)
    }

    operator fun get(layerName: String, assetName: String): Bitmap? {

        val layer: Layer
        if (!layers.containsKey(layerName)) {
            layer = Layer()
            layers[layerName] = layer
        } else {
            layer = layers[layerName]!!
        }

        return if (layer.containsKey(assetName)) {
            layer[assetName]
        } else {

            try {
                val stream = context!!.resources.assets.open(assetName)
                val bmp = BitmapFactory.decodeStream(stream, null, opts)
                layer[assetName] = bmp
                bmp
            } catch (e: IOException) {
                null
            }

        }
    }

    operator fun get(resID: Int): Bitmap {
        return get(DEFAULT, resID)
    }

    operator fun get(layerName: String, resID: Int): Bitmap {

        val layer: Layer
        if (!layers.containsKey(layerName)) {
            layer = Layer()
            layers[layerName] = layer
        } else {
            layer = layers[layerName]!!
        }

        if (layer.containsKey(resID)) {
            return layer[resID]!!
        } else {

            val bmp = BitmapFactory.decodeResource(context!!.resources, resID)
            layer[resID] = bmp
            return bmp

        }
    }

    fun clear(layerName: String) {
        if (layers.containsKey(layerName)) {
            layers[layerName]!!.clear()
            layers.remove(layerName)
        }
    }

    fun clear() {
        for (layer in layers.values) {
            layer.clear()
        }
        layers.clear()
    }

    private class Layer : HashMap<Any, Bitmap>() {

        override fun clear() {
            for (bmp in values) {
                bmp.recycle()
            }
            super.clear()
        }
    }
}
