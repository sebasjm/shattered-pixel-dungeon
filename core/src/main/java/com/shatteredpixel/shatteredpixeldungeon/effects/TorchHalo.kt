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

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.watabou.glwrap.Blending
import com.watabou.noosa.Game

class TorchHalo(private val target: CharSprite) : Halo(20f, 0xFFDDCC, 0.2f) {

    private var phase = 0f

    init {
        am = 0f
    }

    override fun update() {
        super.update()

        if (phase < 0) {
            phase += Game.elapsed
            if (phase >= 0) {
                killAndErase()
            } else {
                scale.set((2 + phase) * radius / Halo.RADIUS)
                am = -phase * brightness
            }
        } else if (phase < 1) {
            phase += Game.elapsed
            if (phase >= 1) {
                phase = 1f
            }
            scale.set(phase * radius / Halo.RADIUS)
            am = phase * brightness
        }

        point(target.x + target.width / 2, target.y + target.height / 2)
    }

    override fun draw() {
        Blending.setLightMode()
        super.draw()
        Blending.setNormalMode()
    }

    fun putOut() {
        phase = -1f
    }
}
