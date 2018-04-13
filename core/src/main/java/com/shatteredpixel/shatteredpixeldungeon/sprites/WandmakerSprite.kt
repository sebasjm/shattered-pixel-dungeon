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
import com.shatteredpixel.shatteredpixeldungeon.effects.Halo
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.watabou.glwrap.Blending
import com.watabou.noosa.Game
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PointF

class WandmakerSprite : MobSprite() {

    private var shield: Shield? = null

    init {

        texture(Assets.MAKER)

        val frames = TextureFilm(texture, 12, 14)

        idle = MovieClip.Animation(10, true)
        idle!!.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1)

        run = MovieClip.Animation(20, true)
        run!!.frames(frames, 0)

        die = MovieClip.Animation(20, false)
        die!!.frames(frames, 0)

        play(idle)
    }

    override fun link(ch: Char) {
        super.link(ch)

        if (shield == null) {
            parent!!.add(shield = Shield())
        }
    }

    override fun die() {
        super.die()

        if (shield != null) {
            shield!!.putOut()
        }
        emitter().start(ElmoParticle.FACTORY, 0.03f, 60)

        if (visible) {
            Sample.INSTANCE.play(Assets.SND_BURNING)
        }
    }

    inner class Shield : Halo(9, 0xBBAACC, 1f) {

        private var phase: Float = 0.toFloat()

        init {

            am = -0.33f
            aa = +0.33f

            phase = 1f
        }

        override fun update() {
            super.update()

            if (phase < 1) {
                if ((phase -= Game.elapsed) <= 0) {
                    killAndErase()
                } else {
                    scale.set((2 - phase) * radius / Halo.RADIUS)
                    am = phase * -1
                    aa = phase * +1
                }
            }

            if (visible = this@WandmakerSprite.visible) {
                val p = this@WandmakerSprite.center()
                point(p.x, p.y)
            }
        }

        override fun draw() {
            Blending.setLightMode()
            super.draw()
            Blending.setNormalMode()
        }

        fun putOut() {
            phase = 0.999f
        }
    }

}
