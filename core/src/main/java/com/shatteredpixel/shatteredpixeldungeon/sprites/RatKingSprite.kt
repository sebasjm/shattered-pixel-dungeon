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

import java.util.Calendar

class RatKingSprite : MobSprite() {

    var festive: Boolean = false

    init {

        val calendar = Calendar.getInstance()
        //once a year the rat king feels a bit festive!
        festive = calendar.get(Calendar.MONTH) == 11 && calendar.get(Calendar.WEEK_OF_MONTH) > 2

        val c = if (festive) 8 else 0

        texture(Assets.RATKING)

        val frames = TextureFilm(texture!!, 16, 17)

        idle = MovieClip.Animation(2, true)
        idle!!.frames(frames, c + 0, c + 0, c + 0, c + 1)

        run = MovieClip.Animation(10, true)
        run!!.frames(frames, c + 2, c + 3, c + 4, c + 5, c + 6)

        attack = MovieClip.Animation(15, false)
        attack!!.frames(frames, c + 0)

        die = MovieClip.Animation(10, false)
        die!!.frames(frames, c + 0)

        play(idle!!)
    }
}
