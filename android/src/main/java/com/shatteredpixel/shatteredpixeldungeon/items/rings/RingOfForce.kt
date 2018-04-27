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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Random

class RingOfForce : Ring() {

    override fun buff(): Ring.RingBuff? {
        return Force()
    }

    override fun desc(): String {
        var desc = super.desc()
        val tier = tier(Dungeon.hero!!.STR())
        if (levelKnown) {
            desc += "\n\n" + Messages.get(this.javaClass, "avg_dmg", min(level(), tier), max(level(), tier))
        } else {
            desc += "\n\n" + Messages.get(this.javaClass, "typical_avg_dmg", min(1, tier), max(1, tier))
        }

        return desc
    }

    inner class Force : Ring.RingBuff()

    companion object {

        fun armedDamageBonus(ch: Char): Int {
            return Ring.getBonus(ch, Force::class.java)
        }


        // *** Weapon-like properties ***

        private fun tier(str: Int): Float {
            var tier = Math.max(1f, (str - 8) / 2f)
            //each str point after 18 is half as effective
            if (tier > 5) {
                tier = 5 + (tier - 5) / 2f
            }
            return tier
        }

        fun damageRoll(hero: Hero): Int {
            if (hero.buff<Force>(Force::class.java) != null) {
                val level = Ring.getBonus(hero, Force::class.java)
                val tier = tier(hero.STR())
                return Random.NormalIntRange(min(level, tier), max(level, tier))
            } else {
                //attack without any ring of force influence
                return Random.NormalIntRange(1, Math.max(hero.STR() - 8, 1))
            }
        }

        //same as equivalent tier weapon
        private fun min(lvl: Int, tier: Float): Int {
            return Math.round(
                    tier +  //base
                            lvl     //level scaling
            )
        }

        //same as equivalent tier weapon
        private fun max(lvl: Int, tier: Float): Int {
            return Math.round(
                    5 * (tier + 1) +    //base
                            lvl * (tier + 1)    //level scaling
            )
        }
    }
}

