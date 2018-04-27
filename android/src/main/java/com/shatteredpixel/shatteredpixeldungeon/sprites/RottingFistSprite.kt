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
import com.watabou.noosa.Camera
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm

class RottingFistSprite : MobSprite() {
    init {

        texture(Assets.ROTTING)

        val frames = TextureFilm(texture!!, 24, 17)

        idle = MovieClip.Animation(2, true)
        idle!!.frames(frames, 0, 0, 1)

        run = MovieClip.Animation(3, true)
        run!!.frames(frames, 0, 1)

        attack = MovieClip.Animation(2, false)
        attack!!.frames(frames, 0)

        die = MovieClip.Animation(10, false)
        die!!.frames(frames, 0, 2, 3, 4)

        play(idle!!)
    }

    override fun attack(cell: Int) {
        super.attack(cell)

        speed.set(0f, -FALL_SPEED)
        acc.set(0f, FALL_SPEED * 4)
    }

    override fun onComplete(anim: MovieClip.Animation) {
        super.onComplete(anim)
        if (anim === attack) {
            speed.set(0f)
            acc.set(0f)
            place(ch!!.pos)

            Camera.main!!.shake(4f, 0.2f)
        }
    }

    companion object {

        private val FALL_SPEED = 64f
    }
}
