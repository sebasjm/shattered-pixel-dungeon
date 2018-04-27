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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class ScrollOfLullaby : Scroll() {

    init {
        initials = 1
    }

    override fun doRead() {

        Item.curUser!!.sprite!!.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5)
        Sample.INSTANCE.play(Assets.SND_LULLABY)
        Invisibility.dispel()

        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                Buff.affect<Drowsy>(mob, Drowsy::class.java)
                mob.sprite!!.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5)
            }
        }

        Buff.affect<Drowsy>(Item.curUser!!, Drowsy::class.java)

        GLog.i(Messages.get(this.javaClass, "sooth"))

        setKnown()

        readAnimation()
    }

    override fun empoweredRead() {
        doRead()
        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                val drowsy = mob.buff(Drowsy::class.java)
                if (drowsy != null) drowsy!!.act()
            }
        }
    }

    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
}
