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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog

class MagicalSleep : Buff() {

    override fun attachTo(target: Char): Boolean {
        if (!target.isImmune(Sleep::class.java) && super.attachTo(target)) {

            if (target is Hero)
                if (target.HP == target.buff<Regeneration>(Regeneration::class.java)!!.regencap()) {
                    GLog.i(Messages.get(this, "toohealthy"))
                    detach()
                    return true
                } else {
                    GLog.i(Messages.get(this, "fallasleep"))
                }
            else if (target is Mob)
                target.state = target.SLEEPING

            target.paralysed++

            return true
        } else {
            return false
        }
    }

    override fun act(): Boolean {
        if (target is Mob && (target as Mob).state !== (target as Mob).SLEEPING) {
            detach()
            return true
        }
        if (target is Hero) {
            target.HP = Math.min(target.HP + 1, target.HT)
            (target as Hero).resting = true
            if (target.HP == target.buff<Regeneration>(Regeneration::class.java)!!.regencap()) {
                GLog.p(Messages.get(this, "wakeup"))
                detach()
            }
        }
        spend(STEP)
        return true
    }

    override fun detach() {
        if (target.paralysed > 0)
            target.paralysed--
        if (target is Hero)
            (target as Hero).resting = false
        super.detach()
    }

    override fun icon(): Int {
        return BuffIndicator.MAGIC_SLEEP
    }

    override fun toString(): String {
        return Messages.get(this, "name")
    }

    override fun desc(): String {
        return Messages.get(this, "desc")
    }

    companion object {

        private val STEP = 1f
    }
}