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
import com.watabou.utils.PointF
import com.watabou.utils.Random

class EnergyParticle : PixelParticle() {
    init {

        lifespan = 1f
        color(0xFFFFAA)

        speed.polar(Random.Float(PointF.PI2), Random.Float(24f, 32f))
    }

    fun reset(x: Float, y: Float) {
        revive()

        left = lifespan

        this.x = x - speed.x * lifespan
        this.y = y - speed.y * lifespan
    }

    override fun update() {
        super.update()

        val p = left / lifespan
        am = if (p < 0.5f) p * p * 4f else (1 - p) * 2
        size(Random.Float(5 * left / lifespan))
    }

    companion object {

        val FACTORY: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(EnergyParticle::class.java) as EnergyParticle).reset(x, y)
            }

            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}