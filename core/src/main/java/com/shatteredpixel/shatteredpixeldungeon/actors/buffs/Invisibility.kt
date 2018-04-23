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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image

open class Invisibility : FlavourBuff() {

    init {
        type = Buff.buffType.POSITIVE
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            target.invisible++
            if (target is Hero && target.subClass == HeroSubClass.ASSASSIN) {
                Buff.affect<Preparation>(target, Preparation::class.java)
            }
            return true
        } else {
            return false
        }
    }

    override fun detach() {
        if (target!!.invisible > 0)
            target!!.invisible--
        super.detach()
    }

    override fun icon(): Int {
        return BuffIndicator.INVISIBLE
    }

    override fun tintIcon(icon: Image) {
        FlavourBuff.greyIcon(icon, 5f, cooldown())
    }

    override fun fx(on: Boolean) {
        if (on)
            target!!.sprite!!.add(CharSprite.State.INVISIBLE)
        else if (target!!.invisible == 0) target!!.sprite!!.remove(CharSprite.State.INVISIBLE)
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns())
    }

    companion object {

        val DURATION = 20f

        fun dispel() {
            val buff = Dungeon.hero!!.buff<Invisibility>(Invisibility::class.java)
            buff?.detach()
            val cloakBuff = Dungeon.hero!!.buff<CloakOfShadows.cloakStealth>(CloakOfShadows.cloakStealth::class.java)
            cloakBuff?.dispel()
            //this isn't a form of invisibilty, but it is meant to dispel at the same time as it.
            val timeFreeze = Dungeon.hero!!.buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)
            timeFreeze?.detach()
        }
    }
}
