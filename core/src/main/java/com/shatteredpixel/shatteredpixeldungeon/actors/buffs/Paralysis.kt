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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class Paralysis : FlavourBuff() {

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            target.paralysed++
            return true
        } else {
            return false
        }
    }

    fun processDamage(damage: Int) {
        if (target == null) return
        var resist = target!!.buff<ParalysisResist>(ParalysisResist::class.java)
        if (resist == null) {
            resist = Buff.affect(target!!, ParalysisResist::class.java)
        }
        resist!!.damage += damage
        if (Random.NormalIntRange(0, resist!!.damage) >= Random.NormalIntRange(0, target!!.HP)) {
            detach()
            if (Dungeon.level!!.heroFOV[target!!.pos]) {
                GLog.i(Messages.get(this.javaClass, "out", target!!.name))
            }
        }
    }

    override fun detach() {
        super.detach()
        if (target!!.paralysed > 0)
            target!!.paralysed--
    }

    override fun icon(): Int {
        return BuffIndicator.PARALYSIS
    }

    override fun fx(on: Boolean) {
        if (on)
            target!!.sprite!!.add(CharSprite.State.PARALYSED)
        else
            target!!.sprite!!.remove(CharSprite.State.PARALYSED)
    }

    override fun heroMessage(): String? {
        return Messages.get(this.javaClass, "heromsg")
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns())
    }

    class ParalysisResist : Buff() {

        var damage: Int = 0

        override fun act(): Boolean {
            if (target!!.buff<Paralysis>(Paralysis::class.java) == null) {
                damage -= Math.ceil((damage / 10f).toDouble()).toInt()
                if (damage >= 0) detach()
            }
            spend(Actor.TICK)
            return true
        }

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            damage = bundle.getInt(DAMAGE)
        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            bundle.put(DAMAGE, damage)
        }

        companion object {

            private val DAMAGE = "damage"
        }
    }

    companion object {

        val DURATION = 10f

        fun duration(ch: Char): Float {
            return DURATION
        }
    }
}
