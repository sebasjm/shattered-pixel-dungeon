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
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm

class PiranhaSprite : MobSprite() {
    init {

        renderShadow = false
        perspectiveRaise = 0.2f

        texture(Assets.PIRANHA)

        val frames = TextureFilm(texture!!, 12, 16)

        idle = MovieClip.Animation(8, true)
        idle!!.frames(frames, 0, 1, 2, 1)

        run = MovieClip.Animation(20, true)
        run!!.frames(frames, 0, 1, 2, 1)

        attack = MovieClip.Animation(20, false)
        attack!!.frames(frames, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        die = MovieClip.Animation(4, false)
        die!!.frames(frames, 12, 13, 14)

        play(idle!!)
    }

    override fun link(ch: Char) {
        super.link(ch)
        renderShadow = false
    }

    override fun onComplete(anim: MovieClip.Animation) {
        super.onComplete(anim)

        if (anim === attack) {
            GameScene.ripple(ch!!.pos)
        }
    }
}