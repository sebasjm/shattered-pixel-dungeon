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
import com.watabou.utils.Random

class PoisonParticle : PixelParticle() {
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
        am = left / lifespan
        size(4 - am * 3)
        // color: 0x8844FF -> 0x00FF00
        color(ColorMath.interpolate(0x00FF00, 0x8844FF, am))
    }

    companion object {

        val MISSILE: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PoisonParticle::class.java) as PoisonParticle).resetMissile(x, y)
            }

            override fun lightMode(): Boolean {
                return true
            }
        }

        val SPLASH: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PoisonParticle::class.java) as PoisonParticle).resetSplash(x, y)
            }

            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}