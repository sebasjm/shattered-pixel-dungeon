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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator

import java.text.DecimalFormat

class Chill : FlavourBuff() {

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun attachTo(target: Char): Boolean {
        //can't chill what's frozen!
        if (target.buff<Frost>(Frost::class.java) != null) return false

        if (super.attachTo(target)) {
            Buff.detach(target, Burning::class.java)
            return true
        } else {
            return false
        }
    }

    //reduces speed by 10% for every turn remaining, capping at 50%
    fun speedFactor(): Float {
        return Math.max(0.5f, 1 - cooldown() * 0.1f)
    }

    override fun icon(): Int {
        return BuffIndicator.FROST
    }

    override fun fx(on: Boolean) {
        if (on)
            target!!.sprite!!.add(CharSprite.State.CHILLED)
        else
            target!!.sprite!!.remove(CharSprite.State.CHILLED)
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns(), DecimalFormat("#.##").format(((1f - speedFactor()) * 100f).toDouble()))
    }
}
