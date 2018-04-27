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
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm

class BruteSprite : MobSprite() {
    init {

        texture(Assets.BRUTE)

        val frames = TextureFilm(texture!!, 12, 16)

        idle = MovieClip.Animation(2, true)
        idle!!.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1)

        run = MovieClip.Animation(12, true)
        run!!.frames(frames, 4, 5, 6, 7)

        attack = MovieClip.Animation(12, false)
        attack!!.frames(frames, 2, 3, 0)

        die = MovieClip.Animation(12, false)
        die!!.frames(frames, 8, 9, 10)

        play(idle!!)
    }
}
