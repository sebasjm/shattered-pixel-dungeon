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
import com.watabou.noosa.TextureFilm
import com.watabou.utils.Random

class MonkSprite : MobSprite() {

    private val kick: MovieClip.Animation

    init {

        texture(Assets.MONK)

        val frames = TextureFilm(texture, 15, 14)

        idle = MovieClip.Animation(6, true)
        idle!!.frames(frames, 1, 0, 1, 2)

        run = MovieClip.Animation(15, true)
        run!!.frames(frames, 11, 12, 13, 14, 15, 16)

        attack = MovieClip.Animation(12, false)
        attack!!.frames(frames, 3, 4, 3, 4)

        kick = MovieClip.Animation(10, false)
        kick.frames(frames, 5, 6, 5)

        die = MovieClip.Animation(15, false)
        die!!.frames(frames, 1, 7, 8, 8, 9, 10)

        play(idle)
    }

    override fun attack(cell: Int) {
        super.attack(cell)
        if (Random.Float() < 0.5f) {
            play(kick)
        }
    }

    override fun onComplete(anim: MovieClip.Animation) {
        super.onComplete(if (anim === kick) attack else anim)
    }
}
