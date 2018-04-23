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

class PurpleParticle : PixelParticle() {
    init {

        lifespan = 0.5f
    }

    fun reset(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        speed.set(Random.Float(-5f, +5f), Random.Float(-5f, +5f))

        left = lifespan
    }

    fun resetBurst(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        speed.polar(Random.Float(PointF.PI2), Random.Float(16f, 32f))

        left = lifespan
    }

    override fun update() {
        super.update()
        // alpha: 1 -> 0; size: 1 -> 5
        am = left / lifespan
        size(5 - am * 4)
        // color: 0xFF0044 -> 0x220066
        color(ColorMath.interpolate(0x220066, 0xFF0044, am))
    }

    companion object {

        val MISSILE: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PurpleParticle::class.java) as PurpleParticle).reset(x, y)
            }
        }

        val BURST: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PurpleParticle::class.java) as PurpleParticle).resetBurst(x, y)
            }

            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}