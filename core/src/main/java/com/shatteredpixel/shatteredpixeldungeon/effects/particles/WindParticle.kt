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

class WindParticle : PixelParticle() {
    init {

        lifespan = Random.Float(1f, 2f)
        size = Random.Float(3f)
        scale.set(size)
    }

    fun reset(x: Float, y: Float) {
        revive()

        left = lifespan

        super.speed.set(WindParticle.speed)
        super.speed.scale(size)

        this.x = x - super.speed.x * lifespan / 2
        this.y = y - super.speed.y * lifespan / 2

        angle += Random.Float(-0.1f, +0.1f)
        speed = PointF().polar(angle, 5f)

        am = 0f
    }

    override fun update() {
        super.update()

        val p = left / lifespan
        am = (if (p < 0.5f) p else 1 - p) * size * 0.2f
    }

    class Wind(private val pos: Int) : Group() {

        private val x: Float
        private val y: Float

        private var delay: Float = 0.toFloat()

        init {
            val p = DungeonTilemap.tileToWorld(pos)
            x = p.x
            y = p.y

            delay = Random.Float(5f)
        }

        override fun update() {

            visible = pos < Dungeon.level!!.heroFOV.size && Dungeon.level!!.heroFOV[pos]
            if (visible) {

                super.update()

                delay -= Game.elapsed
                if (delay <= 0) {

                    delay = Random.Float(5f)

                    (recycle(WindParticle::class.java) as WindParticle).reset(
                            x + Random.Float(DungeonTilemap.SIZE.toFloat()),
                            y + Random.Float(DungeonTilemap.SIZE.toFloat()))
                }
            }
        }
    }

    companion object {

        val FACTORY: Emitter.Factory = object : Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(WindParticle::class.java) as WindParticle).reset(x, y)
            }
        }

        private var angle = Random.Float(PointF.PI2)
        private var speed = PointF().polar(angle, 5f)
    }
}