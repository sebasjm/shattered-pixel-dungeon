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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Random

import java.util.ArrayList

class Frost : FlavourBuff() {

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {

            target.paralysed++
            Buff.detach(target, Burning::class.java)
            Buff.detach(target, Chill::class.java)

            if (target is Hero) {

                val hero = target
                val freezable = ArrayList<Item>()
                //does not reach inside of containers
                for (i in hero.belongings.backpack.items) {
                    if (i is Potion && !(i is PotionOfStrength || i is PotionOfMight) || i is MysteryMeat) {
                        freezable.add(i)
                    }
                }

                if (!freezable.isEmpty()) {
                    val toFreeze = Random.element(freezable)!!.detach(hero.belongings.backpack)
                    if (toFreeze is Potion) {
                        toFreeze.shatter(hero.pos)
                    } else if (toFreeze is MysteryMeat) {
                        val carpaccio = FrozenCarpaccio()
                        if (!carpaccio.collect(hero.belongings.backpack)) {
                            Dungeon.level!!.drop(carpaccio, target.pos).sprite!!.drop()
                        }
                    }
                    GLog.w(Messages.get(this, "freezes", toFreeze!!.toString()))
                }

            } else if (target is Thief) {

                val item = target.item

                if (item is Potion && !(item is PotionOfStrength || item is PotionOfMight)) {
                    (target.item as Potion).shatter(target.pos)
                    target.item = null
                } else if (item is MysteryMeat) {
                    target.item = FrozenCarpaccio()
                }

            }

            return true
        } else {
            return false
        }
    }

    override fun detach() {
        super.detach()
        if (target.paralysed > 0)
            target.paralysed--
        if (Dungeon.level!!.water[target.pos])
            Buff.prolong<Chill>(target, Chill::class.java, 4f)
    }

    override fun icon(): Int {
        return BuffIndicator.FROST
    }

    override fun fx(on: Boolean) {
        if (on)
            target.sprite!!.add(CharSprite.State.FROZEN)
        else
            target.sprite!!.remove(CharSprite.State.FROZEN)
    }

    override fun toString(): String {
        return Messages.get(this, "name")
    }

    override fun desc(): String {
        return Messages.get(this, "desc", dispTurns())
    }

    companion object {

        private val DURATION = 5f

        fun duration(ch: Char): Float {
            return DURATION
        }
    }
}
