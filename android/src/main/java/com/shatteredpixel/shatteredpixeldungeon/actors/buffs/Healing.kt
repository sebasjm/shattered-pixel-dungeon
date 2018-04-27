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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.watabou.utils.Bundle
import com.watabou.utils.GameMath

class Healing : Buff() {

    private var healingLeft: Int = 0

    private var percentHealPerTick: Float = 0.toFloat()
    private var flatHealPerTick: Int = 0

    init {
        //unlike other buffs, this one acts after the hero and takes priority against other effects
        //healing is much more useful if you get some of it off before taking damage
        actPriority = HERO_PRIO - 1
    }

    override fun act(): Boolean {

        var healingThisTick = Math.round(healingLeft * percentHealPerTick) + flatHealPerTick

        healingThisTick = GameMath.gate(1f, healingThisTick.toFloat(), healingLeft.toFloat()).toInt()

        target!!.HP = Math.min(target!!.HT, target!!.HP + healingThisTick)

        target!!.sprite!!.showStatus(CharSprite.POSITIVE, Messages.get(this.javaClass, "value", healingThisTick))

        healingLeft -= healingThisTick

        if (healingLeft <= 0) {
            detach()
        }

        spend(Actor.TICK)

        return true
    }

    fun setHeal(amount: Int, percentPerTick: Float, flatPerTick: Int) {
        healingLeft = amount
        percentHealPerTick = percentPerTick
        flatHealPerTick = flatPerTick
    }

    fun increaseHeal(amount: Int) {
        healingLeft += amount
    }

    override fun fx(on: Boolean) {
        if (on)
            target!!.sprite!!.add(CharSprite.State.HEALING)
        else
            target!!.sprite!!.remove(CharSprite.State.HEALING)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, healingLeft)
        bundle.put(PERCENT, percentHealPerTick)
        bundle.put(FLAT, flatHealPerTick)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        healingLeft = bundle.getInt(LEFT)
        percentHealPerTick = bundle.getFloat(PERCENT)
        flatHealPerTick = bundle.getInt(FLAT)
    }

    companion object {

        private val LEFT = "left"
        private val PERCENT = "percent"
        private val FLAT = "flat"
    }
}
