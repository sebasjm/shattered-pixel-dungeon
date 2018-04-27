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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class ScrollOfTerror : Scroll() {

    init {
        initials = 10
    }

    override fun doRead() {

        Flare(5, 32f).color(0xFF0000, true).show(Item.curUser!!.sprite!!, 2f)
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()

        var count = 0
        var affected: Mob? = null
        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                Buff.affect<Terror>(mob, Terror::class.java, Terror.DURATION).`object` = Item.curUser!!.id()

                if (mob.buff(Terror::class.java) != null) {
                    count++
                    affected = mob
                }
            }
        }

        when (count) {
            0 -> GLog.i(Messages.get(this.javaClass, "none"))
            1 -> GLog.i(Messages.get(this.javaClass, "one", affected!!.name))
            else -> GLog.i(Messages.get(this.javaClass, "many"))
        }
        setKnown()

        readAnimation()
    }

    override fun empoweredRead() {
        doRead()
        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                val t = mob.buff(Terror::class.java)
                if (t != null) {
                    Buff.prolong<Terror>(mob, Terror::class.java, Terror.DURATION * 1.5f)
                    Buff.affect<Paralysis>(mob, Paralysis::class.java, Terror.DURATION * .5f)
                }
            }
        }
    }

    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }
}
