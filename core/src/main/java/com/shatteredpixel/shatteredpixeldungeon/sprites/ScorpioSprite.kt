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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm
import com.watabou.utils.Callback
import com.watabou.utils.asCallback

open class ScorpioSprite : MobSprite() {

    private var cellToAttack: Int = 0

    init {

        texture(Assets.SCORPIO)

        val frames = TextureFilm(texture!!, 18, 17)

        idle = MovieClip.Animation(12, true)
        idle!!.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 2)

        run = MovieClip.Animation(8, true)
        run!!.frames(frames, 5, 5, 6, 6)

        attack = MovieClip.Animation(15, false)
        attack!!.frames(frames, 0, 3, 4)

        zap = attack!!.clone()

        die = MovieClip.Animation(12, false)
        die!!.frames(frames, 0, 7, 8, 9, 10)

        play(idle!!)
    }

    override fun blood(): Int {
        return -0xbb00de
    }

    override fun attack(cell: Int) {
        if (!Dungeon.level!!.adjacent(cell, ch!!.pos)) {

            cellToAttack = cell
            turnTo(ch!!.pos, cell)
            play(zap)

        } else {

            super.attack(cell)

        }
    }

    override fun onComplete(anim: MovieClip.Animation) {
        if (anim === zap) {
            idle()

            (parent!!.recycle(MissileSprite::class.java) as MissileSprite).reset(ch!!.pos, cellToAttack, Dart(), { ch!!.onAttackComplete() } .asCallback())
        } else {
            super.onComplete(anim)
        }
    }
}
