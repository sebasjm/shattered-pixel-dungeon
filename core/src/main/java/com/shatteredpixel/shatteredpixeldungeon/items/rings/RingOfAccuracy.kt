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

class RingOfAccuracy : Ring() {

    override fun buff(): Ring.RingBuff? {
        return Accuracy()
    }

    inner class Accuracy : Ring.RingBuff()

    companion object {

        //The ring of accuracy reduces enemy evasion
        // this makes it more powerful against more evasive enemies
        fun enemyEvasionMultiplier(target: Char): Float {
            return Math.pow(0.75, Ring.getBonus(target, Accuracy::class.java).toDouble()).toFloat()
        }
    }
}
