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
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

import com.watabou.gltextures.SmartTexture
import com.watabou.glwrap.Matrix
import com.watabou.glwrap.Texture
import com.watabou.utils.RectF

import java.util.HashSet
import java.util.LinkedHashMap

open class RenderedText : Image {

    private var size: Int = 0
    private var text: String? = null
    private var cache: CachedText? = null

    private var needsRender = false

    constructor() {
        text = null
    }

    constructor(size: Int) {
        text = null
        this.size = size
    }

    constructor(text: String, size: Int) {
        this.text = text
        this.size = size

        needsRender = true
        measure(this)
    }

    fun text(text: String) {
        this.text = text

        needsRender = true
        measure(this)
    }

    fun text(): String? {
        return text
    }

    fun size(size: Int) {
        this.size = size
        needsRender = true
        measure(this)
    }

    fun baseLine(): Float {
        return size * scale.y
    }

    override fun updateMatrix() {
        super.updateMatrix()
        //the y value is set at the top of the character, not at the top of accents.
        Matrix.translate(matrix, 0f, (-Math.round(baseLine() * 0.15f / scale.y)).toFloat())
    }

    override fun draw() {
        if (needsRender)
            render(this)
        if (texture != null)
            super.draw()
    }

    override fun destroy() {
        if (cache != null)
            cache!!.activeTexts!!.remove(this)
        super.destroy()
    }

    private class CachedText {
        var texture: SmartTexture? = null
        var rect: RectF? = null
        var length: Int = 0
        var activeTexts: HashSet<RenderedText>? = null
    }

    companion object {

        private val canvas = Canvas()
        private val painter = Paint()

        var font: Typeface? = null
            private set

        //this is basically a LRU cache. capacity is determined by character count, not entry count.
        //FIXME: Caching based on words is very inefficient for every language but chinese.
        private val textCache = LinkedHashMap<String, CachedText>(700, 0.75f, true)

        private var cachedChars = 0

        private val GC_TRIGGER = 1250
        private val GC_TARGET = 1000

        private fun runGC() {
            val it = textCache.entries.iterator()
            while (cachedChars > GC_TARGET && it.hasNext()) {
                val cached = it.next().value
                if (cached.activeTexts!!.isEmpty()) {
                    cachedChars -= cached.length
                    cached.texture!!.delete()
                    it.remove()
                }
            }
        }

        @Synchronized
        private fun measure(r: RenderedText) {

            if (r.text == null || r.text == "") {
                r.text = ""
                r.height = 0f
                r.width = r.height
                r.visible = false
                return
            } else {
                r.visible = true
            }

            painter.textSize = r.size.toFloat()
            painter.isAntiAlias = true

            if (font != null) {
                painter.typeface = font
            } else {
                painter.typeface = Typeface.DEFAULT
            }

            //paint outer strokes
            painter.setARGB(0xff, 0, 0, 0)
            painter.style = Paint.Style.STROKE
            painter.strokeWidth = r.size / 5f

            r.width = painter.measureText(r.text) + r.size / 5f
            r.height = -painter.ascent() + painter.descent() + r.size / 5f
        }

        @Synchronized
        private fun render(r: RenderedText) {
            r.needsRender = false

            if (r.cache != null)
                r.cache!!.activeTexts!!.remove(r)

            val key = "text:" + r.size + " " + r.text
            if (textCache.containsKey(key)) {
                r.cache = textCache[key]
                r.texture = r.cache!!.texture
                r.frame(r.cache!!.rect!!)
                r.cache!!.activeTexts!!.add(r)
            } else {

                measure(r)

                if (r.width == 0f || r.height == 0f)
                    return

                //bitmap has to be in a power of 2 for some devices (as we're using openGL methods to render to texture)
                val bitmap = Bitmap.createBitmap(Integer.highestOneBit(r.width.toInt()) * 2, Integer.highestOneBit(r.height.toInt()) * 2, Bitmap.Config.ARGB_4444)
                bitmap.eraseColor(0x00000000)

                canvas.setBitmap(bitmap)
                canvas.drawText(r.text!!, r.size / 10f, r.size.toFloat(), painter)

                //paint inner text
                painter.setARGB(0xff, 0xff, 0xff, 0xff)
                painter.style = Paint.Style.FILL

                canvas.drawText(r.text!!, r.size / 10f, r.size.toFloat(), painter)

                r.texture = SmartTexture(bitmap, Texture.NEAREST, Texture.CLAMP, true)

                val rect = r.texture!!.uvRect(0f, 0f, r.width, r.height)
                r.frame(rect)

                r.cache = CachedText()
                r.cache!!.rect = rect
                r.cache!!.texture = r.texture
                r.cache!!.length = r.text!!.length
                r.cache!!.activeTexts = HashSet()
                r.cache!!.activeTexts!!.add(r)

                cachedChars += r.cache!!.length
                textCache["text:" + r.size + " " + r.text] = r.cache!!

                if (cachedChars >= GC_TRIGGER) {
                    runGC()
                }
            }
        }

        fun clearCache() {
            for (cached in textCache.values) {
                cached.texture!!.delete()
            }
            cachedChars = 0
            textCache.clear()
        }

        fun reloadCache() {
            for (txt in textCache.values) {
                txt.texture!!.reload()
            }
        }

        fun setFont(asset: String?) {
            if (asset == null)
                font = null
            else
                font = Typeface.createFromAsset(Game.instance!!.assets, asset)
            clearCache()
        }
    }
}
