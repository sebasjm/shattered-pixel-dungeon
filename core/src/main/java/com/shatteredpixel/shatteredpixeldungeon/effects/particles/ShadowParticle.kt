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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles

import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.Emitter.Factory
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.PointF
import com.watabou.utils.Random

class ShadowParticle : PixelParticle.Shrinking() {

    fun reset(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        speed.set(Random.Float(-5f, +5f), Random.Float(-5f, +5f))

        size = 6f
        lifespan = 0.5f
        left = lifespan
    }

    fun resetCurse(x: Float, y: Float) {
        revive()

        size = 8f
        lifespan = 0.5f
        left = lifespan

        speed.polar(Random.Float(PointF.PI2), Random.Float(16f, 32f))
        this.x = x - speed.x * lifespan
        this.y = y - speed.y * lifespan
    }

    fun resetUp(x: Float, y: Float) {
        revive()

        speed.set(Random.Float(-8f, +8f), Random.Float(-32f, -48f))
        this.x = x
        this.y = y

        size = 6f
        lifespan = 1f
        left = lifespan
    }

    override fun update() {
        super.update()

        val p = left / lifespan
        // alpha: 0 -> 1 -> 0; size: 6 -> 0; color: 0x660044 -> 0x000000
        color(ColorMath.interpolate(0x000000, 0x440044, p))
        am = if (p < 0.5f) p * p * 4f else (1 - p) * 2
    }

    companion object {

        val MISSILE: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).reset(x, y)
            }
        }

        val CURSE: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).resetCurse(x, y)
            }
        }

        val UP: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).resetUp(x, y)
            }
        }
    }
}