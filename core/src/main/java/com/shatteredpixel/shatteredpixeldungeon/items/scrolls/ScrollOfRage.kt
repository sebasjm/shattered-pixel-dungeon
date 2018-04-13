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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class ScrollOfRage : Scroll() {

    init {
        initials = 6
    }

    override fun doRead() {

        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            mob.beckon(Item.curUser.pos)
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                Buff.prolong<Amok>(mob, Amok::class.java, 5f)
            }
        }

        for (heap in Dungeon.level!!.heaps.values()) {
            if (heap.type == Heap.Type.MIMIC) {
                val m = Mimic.spawnAt(heap.pos, heap.items)
                if (m != null) {
                    m.beckon(Item.curUser.pos)
                    heap.destroy()
                }
            }
        }

        GLog.w(Messages.get(this, "roar"))
        setKnown()

        Item.curUser.sprite!!.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3)
        Sample.INSTANCE.play(Assets.SND_CHALLENGE)
        Invisibility.dispel()

        readAnimation()
    }

    override fun empoweredRead() {
        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                Buff.prolong<Amok>(mob, Amok::class.java, 5f)
            }
        }

        setKnown()

        Item.curUser.sprite!!.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3)
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()

        readAnimation()
    }

    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }
}
