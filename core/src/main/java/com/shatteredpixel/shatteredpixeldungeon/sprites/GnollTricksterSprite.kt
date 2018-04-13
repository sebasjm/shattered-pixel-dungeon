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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm
import com.watabou.utils.Callback

class GnollTricksterSprite : MobSprite() {

    private val cast: MovieClip.Animation

    init {

        texture(Assets.GNOLL)

        val frames = TextureFilm(texture, 12, 15)

        idle = MovieClip.Animation(2, true)
        idle!!.frames(frames, 21, 21, 21, 22, 21, 21, 22, 22)

        run = MovieClip.Animation(12, true)
        run!!.frames(frames, 25, 26, 27, 28)

        attack = MovieClip.Animation(12, false)
        attack!!.frames(frames, 23, 24, 21)

        cast = attack!!.clone()

        die = MovieClip.Animation(12, false)
        die!!.frames(frames, 29, 30, 31)

        play(idle)
    }

    override fun attack(cell: Int) {
        if (!Dungeon.level!!.adjacent(cell, ch!!.pos)) {

            (parent!!.recycle(MissileSprite::class.java) as MissileSprite).reset(ch!!.pos, cell, ParalyticDart()) { ch!!.onAttackComplete() }

            play(cast)
            turnTo(ch!!.pos, cell)

        } else {

            super.attack(cell)

        }
    }
}
