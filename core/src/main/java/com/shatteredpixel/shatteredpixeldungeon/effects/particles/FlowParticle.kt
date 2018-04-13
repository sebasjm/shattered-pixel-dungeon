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
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.Emitter.Factory
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random

class FlowParticle : PixelParticle() {
    init {

        lifespan = 0.6f
        acc.set(0f, 32f)
        angularSpeed = Random.Float(-360f, +360f)
    }

    fun reset(x: Float, y: Float) {
        revive()

        left = lifespan

        this.x = x
        this.y = y

        am = 0f
        size(0f)
        speed.set(0f)
    }

    override fun update() {
        super.update()

        val p = left / lifespan
        am = (if (p < 0.5f) p else 1 - p) * 0.6f
        size((1 - p) * 4)
    }

    class Flow(private val pos: Int) : Group() {

        private val x: Float
        private val y: Float

        private var delay: Float = 0.toFloat()

        init {

            val p = DungeonTilemap.tileToWorld(pos)
            x = p.x
            y = p.y + DungeonTilemap.SIZE - 1

            delay = Random.Float(DELAY)
        }

        override fun update() {

            if (visible = pos < Dungeon.level!!.heroFOV.size && Dungeon.level!!.heroFOV[pos]) {

                super.update()

                if ((delay -= Game.elapsed) <= 0) {

                    delay = Random.Float(DELAY)

                    (recycle(FlowParticle::class.java) as FlowParticle).reset(
                            x + Random.Float(DungeonTilemap.SIZE.toFloat()), y)
                }
            }
        }

        companion object {

            private val DELAY = 0.1f
        }
    }

    companion object {

        val FACTORY: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(FlowParticle::class.java) as FlowParticle).reset(x, y)
            }
        }
    }
}