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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm

class SuccubusSprite : MobSprite() {
    init {

        texture(Assets.SUCCUBUS)

        val frames = TextureFilm(texture!!, 12, 15)

        idle = MovieClip.Animation(8, true)
        idle!!.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 1)

        run = MovieClip.Animation(15, true)
        run!!.frames(frames, 3, 4, 5, 6, 7, 8)

        attack = MovieClip.Animation(12, false)
        attack!!.frames(frames, 9, 10, 11)

        die = MovieClip.Animation(10, false)
        die!!.frames(frames, 12)

        play(idle)
    }

    override fun die() {
        super.die()
        emitter().burst(Speck.factory(Speck.HEART), 6)
        emitter().burst(ShadowParticle.UP, 8)
    }
}
