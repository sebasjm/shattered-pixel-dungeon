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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.Emitter.Factory
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.Random

class LeafParticle : PixelParticle.Shrinking() {
    init {

        lifespan = 1.2f
        acc.set(0f, 25f)
    }

    fun reset(x: Float, y: Float) {
        revive()

        this.x = x
        this.y = y

        speed.set(Random.Float(-8f, +8f), -20f)

        left = lifespan
        size = Random.Float(2f, 3f)
    }

    companion object {

        var color1: Int = 0
        var color2: Int = 0


        val GENERAL: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = emitter.recycle(LeafParticle::class.java) as LeafParticle
                p.color(ColorMath.random(0x004400, 0x88CC44))
                p.reset(x, y)
            }
        }

        val LEVEL_SPECIFIC: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = emitter.recycle(LeafParticle::class.java) as LeafParticle
                p.color(ColorMath.random(Dungeon.level!!.color1, Dungeon.level!!.color2))
                p.reset(x, y)
            }
        }
    }
}