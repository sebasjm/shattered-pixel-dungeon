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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class ScrollOfRecharging : Scroll() {

    init {
        initials = 7
    }

    override fun doRead() {

        Buff.affect<Recharging>(Item.curUser, Recharging::class.java, BUFF_DURATION)
        charge(Item.curUser)

        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()

        GLog.i(Messages.get(this, "surge"))
        SpellSprite.show(Item.curUser, SpellSprite.CHARGE)
        setKnown()

        readAnimation()
    }

    override fun empoweredRead() {
        doRead()
        Buff.append<Recharging>(Item.curUser, Recharging::class.java, BUFF_DURATION / 3f)
    }

    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }

    companion object {

        val BUFF_DURATION = 30f

        fun charge(hero: Hero) {
            hero.sprite!!.centerEmitter().burst(EnergyParticle.FACTORY, 15)
        }
    }
}
