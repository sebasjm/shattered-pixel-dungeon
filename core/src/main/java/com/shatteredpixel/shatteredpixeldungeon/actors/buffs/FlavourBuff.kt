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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs

import com.watabou.noosa.Image

//buff whose only internal logic is to wait and detach after a time.
open class FlavourBuff : Buff() {

    override fun act(): Boolean {
        detach()
        return true
    }

    //flavour buffs can all just rely on cooldown()
    protected fun dispTurns(): String {
        //add one turn as buffs act last, we want them to end at 1 visually, even if they end at 0 internally.
        return dispTurns(cooldown() + 1f)
    }

    companion object {

        fun greyIcon(icon: Image, startGrey: Float, remaining: Float) {
            if (remaining >= startGrey) {
                icon.resetColor()
            } else {
                icon.tint(0xb3b3b3, 0.6f + 0.3f * (startGrey - remaining) / startGrey)
            }
        }
    }
}
