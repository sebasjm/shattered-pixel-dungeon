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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Yog
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap

import java.util.HashSet

class RingOfElements : Ring() {

    override fun buff(): Ring.RingBuff? {
        return Resistance()
    }

    inner class Resistance : Ring.RingBuff()

    companion object {

        val RESISTS = HashSet<Class<*>>()

        init {
            RESISTS.add(Burning::class.java)
            RESISTS.add(Charm::class.java)
            RESISTS.add(Chill::class.java)
            RESISTS.add(Frost::class.java)
            RESISTS.add(Ooze::class.java)
            RESISTS.add(Paralysis::class.java)
            RESISTS.add(Poison::class.java)
            RESISTS.add(Corrosion::class.java)
            RESISTS.add(Weakness::class.java)

            RESISTS.add(DisintegrationTrap::class.java)
            RESISTS.add(GrimTrap::class.java)

            RESISTS.add(ToxicGas::class.java)
            RESISTS.add(Electricity::class.java)

            RESISTS.add(Shaman::class.java)
            RESISTS.add(Warlock::class.java)
            RESISTS.add(Eye::class.java)
            RESISTS.add(Yog.BurningFist::class.java)
        }

        fun resist(target: Char, effect: Class<*>): Float {
            if (Ring.getBonus(target, Resistance::class.java) == 0) return 1f

            for (c in RESISTS) {
                if (c.isAssignableFrom(effect)) {
                    return Math.pow(0.875, Ring.getBonus(target, Resistance::class.java).toDouble()).toFloat()
                }
            }

            return 1f
        }
    }
}
