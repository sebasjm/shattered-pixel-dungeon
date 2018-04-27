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
import com.watabou.noosa.particles.PixelParticle

class ShopkeeperSprite : MobSprite() {

    private var coin: PixelParticle? = null

    init {

        texture(Assets.KEEPER)
        val film = TextureFilm(texture!!, 14, 14)

        idle = MovieClip.Animation(10, true)
        idle!!.frames(film, 1, 1, 1, 1, 1, 0, 0, 0, 0)

        die = MovieClip.Animation(20, false)
        die!!.frames(film, 0)

        run = idle!!.clone()

        attack = idle!!.clone()

        idle()
    }

    override fun onComplete(anim: MovieClip.Animation) {
        super.onComplete(anim)

        if (visible && anim === idle) {
            if (coin == null) {
                coin = PixelParticle()
                parent!!.add(coin!!)
            }
            coin!!.reset(x + if (flipHorizontal) 0 else 13, y + 7, 0xFFFF00, 1f, 0.5f)
            coin!!.speed.y = -40f
            coin!!.acc.y = +160f
        }
    }
}
