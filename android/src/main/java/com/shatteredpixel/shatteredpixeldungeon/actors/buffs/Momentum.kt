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
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image
import com.watabou.utils.Bundle

class Momentum : Buff() {

    private var stacks = 0
    private var turnsSinceMove = 0

    override fun act(): Boolean {
        turnsSinceMove++
        if (turnsSinceMove > 0) {
            stacks = Math.max(0, stacks - turnsSinceMove)
            BuffIndicator.refreshHero()
            if (stacks == 0) detach()
        }
        spend(Actor.TICK)
        return true
    }

    fun gainStack() {
        stacks = Math.min(stacks + 1, 10)
        turnsSinceMove = -1
        BuffIndicator.refreshHero()
    }

    fun stacks(): Int {
        return stacks
    }

    fun speedMultiplier(): Float {
        //1.33x speed at max stacks
        return 1f + stacks / 30f
    }

    fun evasionBonus(excessArmorStr: Int): Int {
        //10 evasion, +2.5 evasion per excess str, at max stacks
        return Math.round((1f + 0.25f * excessArmorStr) * stacks)
    }

    override fun icon(): Int {
        return BuffIndicator.MOMENTUM
    }

    override fun tintIcon(icon: Image) {
        if (stacks <= 5) {
            icon.hardlight(0.2f * (stacks - 1), 1f, 0f)
        } else {
            icon.hardlight(1f, 1f - 0.2f * (stacks - 6), 0f)
        }
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", stacks * 10)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STACKS, stacks)
        bundle.put(TURNS_SINCE, turnsSinceMove)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stacks = bundle.getInt(STACKS)
        turnsSinceMove = bundle.getInt(TURNS_SINCE)
    }

    companion object {

        private val STACKS = "stacks"
        private val TURNS_SINCE = "turnsSinceMove"
    }
}
