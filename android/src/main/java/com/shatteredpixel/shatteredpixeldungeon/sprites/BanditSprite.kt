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

class BanditSprite : MobSprite() {
    init {

        texture(Assets.THIEF)
        val film = TextureFilm(texture!!, 12, 13)

        idle = MovieClip.Animation(1, true)
        idle!!.frames(film, 21, 21, 21, 22, 21, 21, 21, 21, 22)

        run = MovieClip.Animation(15, true)
        run!!.frames(film, 21, 21, 23, 24, 24, 25)

        die = MovieClip.Animation(10, false)
        die!!.frames(film, 25, 27, 28, 29, 30)

        attack = MovieClip.Animation(12, false)
        attack!!.frames(film, 31, 32, 33)

        idle()
    }
}
