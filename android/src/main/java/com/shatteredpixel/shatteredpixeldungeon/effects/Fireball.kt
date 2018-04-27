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

package com.shatteredpixel.shatteredpixeldungeon.effects

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.watabou.glwrap.Blending
import com.watabou.glwrap.Texture
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.noosa.ui.Component
import com.watabou.utils.ColorMath
import com.watabou.utils.Random
import com.watabou.utils.RectF

class Fireball : Component() {

    private var bLight: Image? = null
    private var fLight: Image? = null
    private var emitter: Emitter? = null
    private var sparks: Group? = null

    override fun createChildren() {

        sparks = Group()
        add(sparks!!)

        bLight = Image(Assets.FIREBALL)
        bLight!!.frame(BLIGHT)
        bLight!!.origin.set(bLight!!.width / 2)
        bLight!!.angularSpeed = -90f
        add(bLight!!)

        emitter = Emitter()
        emitter!!.pour(object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = emitter.recycle(Flame::class.java) as Flame
                p.reset()
                p.heightLimit(this@Fireball.y - 30)
                p.x = x - p.width / 2
                p.y = y - p.height / 2
            }
        }, 0.1f)
        add(emitter!!)

        fLight = Image(Assets.FIREBALL)
        fLight!!.frame(FLIGHT)
        fLight!!.origin.set(fLight!!.width / 2)
        fLight!!.angularSpeed = 360f
        add(fLight!!)

        bLight!!.texture!!.filter(Texture.LINEAR, Texture.LINEAR)
    }

    override fun layout() {

        bLight!!.x = x - bLight!!.width / 2
        bLight!!.y = y - bLight!!.height / 2

        emitter!!.pos(
                x - bLight!!.width / 4,
                y - bLight!!.height / 4,
                bLight!!.width / 2,
                bLight!!.height / 2)

        fLight!!.x = x - fLight!!.width / 2
        fLight!!.y = y - fLight!!.height / 2
    }

    override fun update() {

        super.update()

        if (Random.Float() < Game.elapsed) {
            val spark = sparks!!.recycle(PixelParticle.Shrinking::class.java) as PixelParticle
            spark.reset(x, y, ColorMath.random(COLOR, 0x66FF66), 2f, Random.Float(0.5f, 1.0f))
            spark.speed.set(
                    Random.Float(-40f, +40f),
                    Random.Float(-60f, +20f))
            spark.acc.set(0f, +80f)
            sparks!!.add(spark)
        }
    }

    override fun draw() {
        Blending.setLightMode()
        super.draw()
        Blending.setNormalMode()
    }

    class Flame : Image(Assets.FIREBALL) {

        private var timeLeft: Float = 0.toFloat()
        private var heightLimit: Float = 0.toFloat()

        init {

            frame(if (Random.Int(2) == 0) FLAME1 else FLAME2)
            origin.set(width / 2, height / 2)
            acc.set(0f, ACC)
        }

        fun reset() {
            revive()
            timeLeft = LIFESPAN
            speed.set(0f, SPEED)
        }

        fun heightLimit(limit: Float) {
            heightLimit = limit
        }

        override fun update() {

            super.update()

            if (y < heightLimit) {
                y = heightLimit
                speed.set(Random.Float(-20f, 20f), 0f)
                acc.set(0f, 0f)
            }

            timeLeft -= Game.elapsed
            if (timeLeft <= 0) {

                kill()

            } else {

                val p = timeLeft / LIFESPAN
                scale.set(p)
                alpha(if (p > 0.8f) (1 - p) * 5f else p * 1.25f)

            }
        }

        companion object {

            private val LIFESPAN = 1f

            private val SPEED = -40f
            private val ACC = -20f
        }
    }

    companion object {

        private val BLIGHT = RectF(0f, 0f, 0.25f, 1f)
        private val FLIGHT = RectF(0.25f, 0f, 0.5f, 1f)
        private val FLAME1 = RectF(0.50f, 0f, 0.75f, 1f)
        private val FLAME2 = RectF(0.75f, 0f, 1.00f, 1f)

        private val COLOR = 0xFF66FF
    }
}
