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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Random

class ScrollOfPsionicBlast : Scroll() {

    init {
        initials = 5

        bones = true
    }

    override fun doRead() {

        GameScene.flash(0xFFFFFF)

        Sample.INSTANCE.play(Assets.SND_BLAST)
        Invisibility.dispel()

        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                mob.damage(mob.HP, this)
            }
        }

        Item.curUser.damage(Math.max(Item.curUser.HT / 5, Item.curUser.HP / 2), this)
        if (Item.curUser.isAlive) {
            Buff.prolong<Paralysis>(Item.curUser, Paralysis::class.java, Random.Int(4, 6).toFloat())
            Buff.prolong<Blindness>(Item.curUser, Blindness::class.java, Random.Int(6, 9).toFloat())
            Dungeon.observe()
        }

        setKnown()

        readAnimation()

        if (!Item.curUser.isAlive) {
            Dungeon.fail(javaClass)
            GLog.n(Messages.get(this, "ondeath"))
        }
    }

    override fun empoweredRead() {
        GameScene.flash(0xFFFFFF)

        Sample.INSTANCE.play(Assets.SND_BLAST)
        Invisibility.dispel()

        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                mob.damage(mob.HT, this)
            }
        }

        setKnown()

        readAnimation()
    }

    override fun price(): Int {
        return if (isKnown) 50 * quantity else super.price()
    }
}
