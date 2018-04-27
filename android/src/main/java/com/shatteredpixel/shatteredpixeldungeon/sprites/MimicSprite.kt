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

class MimicSprite : MobSprite() {
    init {

        texture(Assets.MIMIC)

        val frames = TextureFilm(texture!!, 16, 16)

        idle = MovieClip.Animation(5, true)
        idle!!.frames(frames, 0, 0, 0, 1, 1)

        run = MovieClip.Animation(10, true)
        run!!.frames(frames, 0, 1, 2, 3, 3, 2, 1)

        attack = MovieClip.Animation(10, false)
        attack!!.frames(frames, 0, 4, 5, 6)

        die = MovieClip.Animation(5, false)
        die!!.frames(frames, 7, 8, 9)

        play(idle!!)
    }

    override fun blood(): Int {
        return -0x346900
    }
}
