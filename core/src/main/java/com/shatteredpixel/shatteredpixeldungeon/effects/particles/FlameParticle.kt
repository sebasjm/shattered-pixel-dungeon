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

class FlameParticle : PixelParticle.Shrinking() {
    init {

        color(0xEE7722)
        lifespan = 0.6f

        acc.set(0f, -80f)
    }

    fun reset(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        left = lifespan

        size = 4f
        speed.set(0f)
    }

    override fun update() {
        super.update()
        val p = left / lifespan
        am = if (p > 0.8f) (1 - p) * 5 else 1
    }

    companion object {

        val FACTORY: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(FlameParticle::class.java) as FlameParticle).reset(x, y)
            }

            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}