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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator

class Corruption : Buff() {

    private var buildToDamage = 0f

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            target.alignment = com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ALLY
            return true
        } else {
            return false
        }
    }

    override fun act(): Boolean {
        buildToDamage += target!!.HT / 200f

        val damage = buildToDamage.toInt()
        buildToDamage -= damage.toFloat()

        if (damage > 0)
            target!!.damage(damage, this)

        spend(Actor.TICK)

        return true
    }

    override fun fx(on: Boolean) {
        if (on)
            target!!.sprite!!.add(CharSprite.State.DARKENED)
        else if (target!!.invisible == 0) target!!.sprite!!.remove(CharSprite.State.DARKENED)
    }

    override fun icon(): Int {
        return BuffIndicator.CORRUPT
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc")
    }
}
