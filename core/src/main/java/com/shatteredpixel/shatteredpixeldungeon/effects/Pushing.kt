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

package com.shatteredpixel.shatteredpixeldungeon.effects

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.watabou.noosa.Game
import com.watabou.noosa.Visual
import com.watabou.utils.Callback
import com.watabou.utils.PointF

class Pushing(ch: Char, private val from: Int, private val to: Int) : Actor() {

    private val sprite: CharSprite?

    private val effect: Effect? = null

    private var callback: Callback? = null

    init {
        actPriority = VFX_PRIO
    }

    init {
        sprite = ch.sprite
        this.callback = null
    }

    constructor(ch: Char, from: Int, to: Int, callback: Callback) : this(ch, from, to) {
        this.callback = callback
    }

    override fun act(): Boolean {
        if (sprite != null) {

            if (effect == null) {
                Effect()
            }
        }

        Actor.remove(this@Pushing)

        //so that all pushing effects at the same time go simultaneously
        for (actor in Actor.all()) {
            if (actor is Pushing && actor.cooldown() == 0f)
                return true
        }
        return false

    }

    inner class Effect : Visual(0, 0, 0, 0) {

        private val end: PointF

        private var delay: Float = 0.toFloat()

        init {

            point(sprite!!.worldToCamera(from))
            end = sprite.worldToCamera(to)

            speed.set(2 * (end.x - x) / DELAY, 2 * (end.y - y) / DELAY)
            acc.set(-speed.x / DELAY, -speed.y / DELAY)

            delay = 0f

            if (sprite.parent != null)
                sprite.parent!!.add(this)
        }

        override fun update() {
            super.update()

            if ((delay += Game.elapsed) < DELAY) {

                sprite!!.x = x
                sprite.y = y

            } else {

                sprite!!.point(end)

                killAndErase()
                Actor.remove(this@Pushing)
                if (callback != null) callback!!.call()

                next()
            }
        }

        companion object {

            private val DELAY = 0.15f
        }
    }

}
