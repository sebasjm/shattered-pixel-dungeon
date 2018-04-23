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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback

class WarlockSprite : MobSprite() {
    init {

        texture(Assets.WARLOCK)

        val frames = TextureFilm(texture!!, 12, 15)

        idle = MovieClip.Animation(2, true)
        idle!!.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1)

        run = MovieClip.Animation(15, true)
        run!!.frames(frames, 0, 2, 3, 4)

        attack = MovieClip.Animation(12, false)
        attack!!.frames(frames, 0, 5, 6)

        zap = attack!!.clone()

        die = MovieClip.Animation(15, false)
        die!!.frames(frames, 0, 7, 8, 8, 9, 10)

        play(idle)
    }

    override fun zap(cell: Int) {

        turnTo(ch!!.pos, cell)
        play(zap)

        MagicMissile.boltFromChar(parent!!,
                MagicMissile.SHADOW,
                this,
                cell,
                { (ch as Warlock).onZapComplete() } as Callback
        )
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    override fun onComplete(anim: MovieClip.Animation) {
        if (anim === zap) {
            idle()
        }
        super.onComplete(anim)
    }
}
