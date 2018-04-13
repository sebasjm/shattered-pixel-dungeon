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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood

class Regeneration : Buff() {

    init {
        //unlike other buffs, this one acts after the hero and takes priority against other effects
        //healing is much more useful if you get some of it off before taking damage
        actPriority = HERO_PRIO - 1
    }

    override fun act(): Boolean {
        if (target.isAlive) {

            if (target.HP < regencap() && !(target as Hero).isStarving) {
                val lock = target.buff<LockedFloor>(LockedFloor::class.java)
                if (target.HP > 0 && (lock == null || lock.regenOn())) {
                    target.HP += 1
                    if (target.HP == regencap()) {
                        (target as Hero).resting = false
                    }
                }
            }

            val regenBuff = Dungeon.hero!!.buff<ChaliceOfBlood.chaliceRegen>(ChaliceOfBlood.chaliceRegen::class.java)

            if (regenBuff != null)
                if (regenBuff.isCursed)
                    spend(REGENERATION_DELAY * 1.5f)
                else
                    spend(REGENERATION_DELAY - regenBuff.itemLevel() * 0.9f)
            else
                spend(REGENERATION_DELAY)

        } else {

            diactivate()

        }

        return true
    }

    fun regencap(): Int {
        return if (target.buff<Berserk>(Berserk::class.java) == null) target.HT else target.buff<Berserk>(Berserk::class.java)!!.targetHPMax()
    }

    companion object {

        private val REGENERATION_DELAY = 10f
    }
}
