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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class MasterThievesArmband : Artifact() {

   var exp2 = 0

    init {
        image = ItemSpriteSheet.ARTIFACT_ARMBAND

        levelCap = 10

        charge = 0
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return Thievery()
    }

    override fun desc(): String {
        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!))
            desc += "\n\n" + Messages.get(this.javaClass, "desc_worn")

        return desc
    }

    inner class Thievery : Artifact.ArtifactBuff() {
        fun collect(gold: Int) {
            charge += gold / 2
        }

        override fun detach() {
            charge = (charge * 0.95f).toInt()
            super.detach()
        }

        fun steal(value: Int): Boolean {
            if (value <= charge) {
                charge -= value
                exp2 += value
            } else {
                val chance = stealChance(value)
                if (Random.Float() > chance)
                    return false
                else {
                    if (chance <= 1)
                        charge = 0
                    else
                    //removes the charge it took you to reach 100%
                        charge -= (charge / chance).toInt()
                    exp2 += value
                }
            }
            while (exp2 >= 250 + 50 * level() && level() < levelCap) {
                exp2 -= 250 + 50 * level()
                upgrade()
            }
            return true
        }

        fun stealChance(value: Int): Float {
            //get lvl*50 gold or lvl*3.33% item value of free charge, whichever is less.
            val chargeBonus = Math.min(level() * 50, value * level() / 30)
            return (charge.toFloat() + chargeBonus) / value
        }
    }
}
