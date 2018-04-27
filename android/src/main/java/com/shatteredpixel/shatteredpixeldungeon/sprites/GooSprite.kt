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

package com.shatteredpixel.shatteredpixeldungeon.sprites

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.Emitter.Factory
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random

class GooSprite : MobSprite() {

    private val pump: MovieClip.Animation
    private val pumpAttack: MovieClip.Animation

    private val spray: Emitter

    init {

        texture(Assets.GOO)

        val frames = TextureFilm(texture!!, 20, 14)

        idle = MovieClip.Animation(10, true)
        idle!!.frames(frames, 2, 1, 0, 0, 1)

        run = MovieClip.Animation(15, true)
        run!!.frames(frames, 3, 2, 1, 2)

        pump = MovieClip.Animation(20, true)
        pump.frames(frames, 4, 3, 2, 1, 0)

        pumpAttack = MovieClip.Animation(20, false)
        pumpAttack.frames(frames, 4, 3, 2, 1, 0, 7)

        attack = MovieClip.Animation(10, false)
        attack!!.frames(frames, 8, 9, 10)

        die = MovieClip.Animation(10, false)
        die!!.frames(frames, 5, 6, 7)

        play(idle!!)

        spray = centerEmitter()
        spray.autoKill = false
        spray.pour(GooParticle.FACTORY, 0.04f)
        spray.on = false
    }

    override fun link(ch: Char) {
        super.link(ch)
        if (ch.HP * 2 <= ch.HT)
            spray(true)
    }

    fun pumpUp() {
        play(pump)
    }

    fun pumpAttack() {
        play(pumpAttack)
    }

    override fun blood(): Int {
        return -0x1000000
    }

    fun spray(on: Boolean) {
        spray.on = on
    }

    override fun update() {
        super.update()
        spray.pos(center())
        spray.visible = visible
    }

    class GooParticle : PixelParticle.Shrinking() {
        init {

            color(0x000000)
            lifespan = 0.3f

            acc.set(0f, +50f)
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            left = lifespan

            size = 4f
            speed.polar(-Random.Float(PointF.PI), Random.Float(32f, 48f))
        }

        override fun update() {
            super.update()
            val p = left / lifespan
            am = if (p > 0.5f) (1 - p) * 2f else 1f
        }

        companion object {

            val FACTORY: Emitter.Factory = object : Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(GooParticle::class.java) as GooParticle).reset(x, y)
                }
            }
        }
    }

    override fun onComplete(anim: MovieClip.Animation) {
        super.onComplete(anim)

        if (anim === pumpAttack) {

            idle()
            ch!!.onAttackComplete()
        } else if (anim === die) {
            spray.killAndErase()
        }
    }
}
