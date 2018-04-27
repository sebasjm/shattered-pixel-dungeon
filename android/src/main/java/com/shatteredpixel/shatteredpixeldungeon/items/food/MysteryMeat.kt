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

package com.shatteredpixel.shatteredpixeldungeon.items.food

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Random

class MysteryMeat : Food() {

    init {
        image = ItemSpriteSheet.MEAT
        energy = Hunger.HUNGRY / 2f
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == Food.AC_EAT) {
            effect(hero)
        }
    }

    override fun price(): Int {
        return 5 * quantity
    }

    companion object {

        fun effect(hero: Hero) {
            when (Random.Int(5)) {
                0 -> {
                    GLog.w(Messages.get(MysteryMeat::class.java, "hot"))
                    Buff.affect<Burning>(hero, Burning::class.java)!!.reignite(hero)
                }
                1 -> {
                    GLog.w(Messages.get(MysteryMeat::class.java, "legs"))
                    Buff.prolong<Roots>(hero, Roots::class.java, Paralysis.DURATION)
                }
                2 -> {
                    GLog.w(Messages.get(MysteryMeat::class.java, "not_well"))
                    Buff.affect<Poison>(hero, Poison::class.java)!!.set((hero.HT / 5).toFloat())
                }
                3 -> {
                    GLog.w(Messages.get(MysteryMeat::class.java, "stuffed"))
                    Buff.prolong<Slow>(hero, Slow::class.java, Slow.DURATION)
                }
            }
        }
    }
}
