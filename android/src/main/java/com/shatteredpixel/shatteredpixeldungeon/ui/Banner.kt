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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.watabou.noosa.Game
import com.watabou.noosa.Image

class Banner : Image {
    private var state: State? = null

    private var time: Float = 0.toFloat()

    private var color: Int = 0
    private var fadeTime: Float = 0.toFloat()
    private var showTime: Float = 0.toFloat()

    private enum class State {
        FADE_IN, STATIC, FADE_OUT
    }

    constructor(sample: Image) : super() {
        copy(sample)
        alpha(0f)
    }

    constructor(tx: Any) : super(tx) {
        alpha(0f)
    }

    @JvmOverloads
    fun show(color: Int, fadeTime: Float, showTime: Float = java.lang.Float.MAX_VALUE) {

        this.color = color
        this.fadeTime = fadeTime
        this.showTime = showTime

        state = State.FADE_IN

        time = fadeTime
    }

    override fun update() {
        super.update()

        time -= Game.elapsed
        if (time >= 0) {

            val p = time / fadeTime

            when (state) {
                Banner.State.FADE_IN -> {
                    tint(color, p)
                    alpha(1 - p)
                }
                Banner.State.STATIC -> {
                }
                Banner.State.FADE_OUT -> alpha(p)
            }

        } else {

            when (state) {
                Banner.State.FADE_IN -> {
                    time = showTime
                    state = State.STATIC
                }
                Banner.State.STATIC -> {
                    time = fadeTime
                    state = State.FADE_OUT
                }
                Banner.State.FADE_OUT -> killAndErase()
            }

        }
    }
}
