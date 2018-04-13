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

package com.shatteredpixel.shatteredpixeldungeon.items.armor

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.audio.Sample

class MageArmor : ClassArmor() {

    init {
        image = ItemSpriteSheet.ARMOR_MAGE
    }

    override fun doSpecial() {

        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {
                Buff.affect<Burning>(mob, Burning::class.java)!!.reignite(mob)
                Buff.prolong<Roots>(mob, Roots::class.java, 3f)
            }
        }

        Item.curUser.HP -= Item.curUser.HP / 3

        Item.curUser.spend(Actor.TICK)
        Item.curUser.sprite!!.operate(Item.curUser.pos)
        Item.curUser.busy()

        Item.curUser.sprite!!.centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4)
        Sample.INSTANCE.play(Assets.SND_READ)
    }

}