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

import android.annotation.SuppressLint

import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Blending
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.Visual
import com.watabou.utils.PointF

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Flare @SuppressLint("FloatMath")
constructor(private val nRays: Int, radius: Float) : Visual(0f, 0f, 0f, 0f) {

    private var duration = 0f
    private var lifespan: Float = 0.toFloat()

    private var lightMode = true

    private val texture: SmartTexture

    private val vertices: FloatBuffer
    private val indices: ShortBuffer

    init {

        val gradient = intArrayOf(-0x1, 0x00FFFFFF)
        texture = TextureCache.createGradient(*gradient)

        angle = 45f
        angularSpeed = 180f

        vertices = ByteBuffer.allocateDirect((nRays * 2 + 1) * 4 * (java.lang.Float.SIZE / 8)).order(ByteOrder.nativeOrder()).asFloatBuffer()

        indices = ByteBuffer.allocateDirect(nRays * 3 * java.lang.Short.SIZE / 8).order(ByteOrder.nativeOrder()).asShortBuffer()

        val v = FloatArray(4)

        v[0] = 0f
        v[1] = 0f
        v[2] = 0.25f
        v[3] = 0f
        vertices.put(v)

        v[2] = 0.75f
        v[3] = 0f

        for (i in 0 until nRays) {

            var a = i.toFloat() * 3.1415926f * 2f / nRays
            v[0] = Math.cos(a.toDouble()).toFloat() * radius
            v[1] = Math.sin(a.toDouble()).toFloat() * radius
            vertices.put(v)

            a += 3.1415926f * 2 / nRays.toFloat() / 2f
            v[0] = Math.cos(a.toDouble()).toFloat() * radius
            v[1] = Math.sin(a.toDouble()).toFloat() * radius
            vertices.put(v)

            indices.put(0.toShort())
            indices.put((1 + i * 2).toShort())
            indices.put((2 + i * 2).toShort())
        }

        indices.position(0)
    }

    fun color(color: Int, lightMode: Boolean): Flare {
        this.lightMode = lightMode
        hardlight(color)

        return this
    }

    fun show(visual: Visual, duration: Float): Flare {
        point(visual.center())
        visual.parent!!.addToBack(this)

        this.duration = duration
        lifespan = this.duration
        if (lifespan > 0) scale.set(0f)

        return this
    }

    fun show(parent: Group, pos: PointF, duration: Float): Flare {
        point(pos)
        parent.add(this)

        this.duration = duration
        lifespan = this.duration
        if (lifespan > 0) scale.set(0f)

        return this
    }

    override fun update() {
        super.update()

        if (duration > 0) {
            lifespan -= Game.elapsed
            if (lifespan > 0) {

                var p = 1 - lifespan / duration    // 0 -> 1
                p = if (p < 0.25f) p * 4 else (1 - p) * 1.333f
                scale.set(p)
                alpha(p)

            } else {
                killAndErase()
            }
        }
    }

    override fun draw() {

        super.draw()

        if (lightMode) {
            Blending.setLightMode()
            drawRays()
            Blending.setNormalMode()
        } else {
            drawRays()
        }
    }

    private fun drawRays() {

        val script = NoosaScript.get()

        texture.bind()

        script.uModel.valueM4(matrix)
        script.lighting(
                rm, gm, bm, am,
                ra, ga, ba, aa)

        script.camera(camera)
        script.drawElements(vertices, indices, nRays * 3)
    }
}
