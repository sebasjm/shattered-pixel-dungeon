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

package com.shatteredpixel.shatteredpixeldungeon.items.rings

import com.shatteredpixel.shatteredpixeldungeon.actors.Char

class RingOfSharpshooting : Ring() {

    override fun buff(): Ring.RingBuff? {
        return Aim()
    }

    inner class Aim : Ring.RingBuff()

    companion object {

        //roughly in line with the boost a weapon gets from an upgrade
        fun damageMultiplier(target: Char): Float {
            return 1f + 0.2f * Ring.getBonus(target, Aim::class.java)
        }

        fun durabilityMultiplier(target: Char): Float {
            return Math.pow(1.2, Ring.getBonus(target, Aim::class.java).toDouble()).toFloat()
        }
    }
}
