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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Quad
import com.watabou.glwrap.Vertexbuffer
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.Visual
import com.watabou.utils.RectF

import java.nio.FloatBuffer
import java.util.LinkedHashMap

class KeyDisplay : Visual(0, 0, 0, 0) {

    private val vertices = FloatArray(16)
    private var quads: FloatBuffer? = null
    private var buffer: Vertexbuffer? = null

    private val tx = TextureCache.get(Assets.MENU)

    private var dirty = true
    private var keys: IntArray? = null

    private var totalKeys = 0

    fun updateKeys() {
        keys = IntArray(keyMap.size + 1)

        for (rec in Notes.getRecords<Notes.KeyRecord>(Notes.KeyRecord::class.java)) {
            if (rec.depth() < Dungeon.depth) {
                //only ever 1 black key
                keys[0] = 1
            } else if (rec.depth() == Dungeon.depth) {
                keys[keyMap[rec.type()]] += rec.quantity()
            }
        }

        totalKeys = 0
        for (k in keys!!) {
            totalKeys += k
        }
        dirty = true
    }

    fun keyCount(): Int {
        return totalKeys
    }

    override fun draw() {
        super.draw()
        if (dirty) {

            updateVertices()

            quads!!.limit(quads!!.position())
            if (buffer == null)
                buffer = Vertexbuffer(quads!!)
            else
                buffer!!.updateVertices(quads)

        }

        val script = NoosaScript.get()

        tx.bind()

        script.camera(camera())

        script.uModel.valueM4(matrix)
        script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa)
        script.drawQuadSet(buffer, totalKeys, 0)
    }

    private fun updateVertices() {
        //assumes shorter key sprite
        val maxRows = (height + 1).toInt() / 5

        //1 pixel of padding between each key
        val maxPerRow = (width + 1).toInt() / 4

        val maxKeys = maxPerRow * maxRows


        while (totalKeys > maxKeys) {
            var mostType: Class<out Key>? = null
            var mostNum = 0
            for (k in keyMap.keys) {
                if (keys!![keyMap[k]] >= mostNum) {
                    mostType = k
                    mostNum = keys!![keyMap[k]]
                }
            }
            keys!![keyMap.get(mostType)]--
            totalKeys--
        }

        val rows = Math.ceil((totalKeys / maxPerRow.toFloat()).toDouble()).toInt()

        val shortKeys = rows * 8 > height
        var left: Float
        if (totalKeys > maxPerRow) {
            left = 0f
        } else {
            left = (width + 1 - totalKeys * 4) / 2
        }
        var top = (height + 1 - rows * if (shortKeys) 5 else 8) / 2
        quads = Quad.createSet(totalKeys)
        for (i in 0 until totalKeys) {
            var keyIdx = 0

            if (i == 0 && keys!![0] > 0) {
                //black key
                keyIdx = 0

            } else {
                for (j in 1 until keys!!.size) {
                    if (keys!![j] > 0) {
                        keys!![j]--
                        keyIdx = j
                        break
                    }
                }
            }

            //texture coordinates
            val r = tx.uvRect((43 + 3 * keyIdx).toFloat(), (if (shortKeys) 8 else 0).toFloat(),
                    (46 + 3 * keyIdx).toFloat(), (if (shortKeys) 12 else 7).toFloat())

            vertices[2] = r.left
            vertices[3] = r.top

            vertices[6] = r.right
            vertices[7] = r.top

            vertices[10] = r.right
            vertices[11] = r.bottom

            vertices[14] = r.left
            vertices[15] = r.bottom

            //screen coordinates
            vertices[0] = left
            vertices[1] = top

            vertices[4] = left + 3
            vertices[5] = top

            vertices[8] = left + 3
            vertices[9] = top + if (shortKeys) 4 else 7

            vertices[12] = left
            vertices[13] = top + if (shortKeys) 4 else 7

            quads!!.put(vertices)

            //move to the right for more keys, drop down if the row is done
            left += 4f
            if (left + 3 > width) {
                left = 0f
                top += (if (shortKeys) 5 else 8).toFloat()
            }
        }

        dirty = false

    }

    companion object {

        //mapping of key types to slots in the array, 0 is reserved for black (missed) keys
        //this also determines the order these keys will appear (lower first)
        //and the order they will be truncated if there is no space (higher first, larger counts first)
        private val keyMap = LinkedHashMap<Class<out Key>, Int>()

        init {
            keyMap[SkeletonKey::class.java] = 1
            keyMap[CrystalKey::class.java] = 2
            keyMap[GoldenKey::class.java] = 3
            keyMap[IronKey::class.java] = 4
        }
    }

}
