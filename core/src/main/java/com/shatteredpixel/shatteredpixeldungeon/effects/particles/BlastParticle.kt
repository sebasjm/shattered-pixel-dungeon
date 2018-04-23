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
import com.watabou.utils.Random

class BlastParticle : PixelParticle.Shrinking() {
    init {

        color(0xEE7722)
        acc.set(0f, +50f)
    }

    fun reset(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        lifespan = Random.Float()
        left = lifespan

        size = 8f
        speed.polar(-Random.Float(3.1415926f), Random.Float(32f, 64f))
    }

    override fun update() {
        super.update()
        am = if (left > 0.8f) (1 - left) * 5 else 1f
    }

    companion object {

        val FACTORY: Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(BlastParticle::class.java) as BlastParticle).reset(x, y)
            }

            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}