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

class WoolParticle : PixelParticle.Shrinking() {
    init {

        color(ColorMath.random(0x999999, 0xEEEEE0))

        acc.set(0f, -40f)
    }

    fun reset(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        lifespan = Random.Float(0.6f, 1f)
        left = lifespan
        size = 5f

        speed.set(Random.Float(-10f, +10f), Random.Float(-10f, +10f))
    }

    companion object {

        val FACTORY: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(WoolParticle::class.java) as WoolParticle).reset(x, y)
            }
        }
    }
}