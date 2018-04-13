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
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.Random

class CorrosionParticle : PixelParticle() {
    init {

        lifespan = 0.6f

        acc.set(0f, +30f)
    }

    fun resetMissile(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        left = lifespan

        speed.polar(-Random.Float(3.1415926f), Random.Float(6f))
    }

    fun resetSplash(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        left = lifespan

        speed.polar(Random.Float(3.1415926f), Random.Float(10f, 20f))
    }

    override fun update() {
        super.update()
        // alpha: 1 -> 0; size: 1 -> 4
        size(4 - (am = left / lifespan) * 3)
        // color: 0xAAAAAA -> 0xFF8800
        color(ColorMath.interpolate(0xFF8800, 0xAAAAAA, am))
    }

    companion object {

        val MISSILE: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(CorrosionParticle::class.java) as CorrosionParticle).resetMissile(x, y)
            }

            override fun lightMode(): Boolean {
                return false
            }
        }

        val SPLASH: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(CorrosionParticle::class.java) as CorrosionParticle).resetSplash(x, y)
            }

            override fun lightMode(): Boolean {
                return false
            }
        }
    }

}
