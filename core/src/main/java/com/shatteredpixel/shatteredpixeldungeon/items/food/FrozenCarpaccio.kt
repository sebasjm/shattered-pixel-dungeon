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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Random

class FrozenCarpaccio : Food() {

    init {
        image = ItemSpriteSheet.CARPACCIO
        energy = Hunger.HUNGRY / 2f
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == Food.AC_EAT) {
            effect(hero)
        }
    }

    override fun price(): Int {
        return 10 * quantity
    }

    companion object {

        fun effect(hero: Hero) {
            when (Random.Int(5)) {
                0 -> {
                    GLog.i(Messages.get(FrozenCarpaccio::class.java, "invis"))
                    Buff.affect<Invisibility>(hero, Invisibility::class.java, Invisibility.DURATION)
                }
                1 -> {
                    GLog.i(Messages.get(FrozenCarpaccio::class.java, "hard"))
                    Buff.affect<Barkskin>(hero, Barkskin::class.java)!!.level(hero.HT / 4)
                }
                2 -> {
                    GLog.i(Messages.get(FrozenCarpaccio::class.java, "refresh"))
                    Buff.detach(hero, Poison::class.java)
                    Buff.detach(hero, Cripple::class.java)
                    Buff.detach(hero, Weakness::class.java)
                    Buff.detach(hero, Bleeding::class.java)
                    Buff.detach(hero, Drowsy::class.java)
                    Buff.detach(hero, Slow::class.java)
                    Buff.detach(hero, Vertigo::class.java)
                }
                3 -> {
                    GLog.i(Messages.get(FrozenCarpaccio::class.java, "better"))
                    if (hero.HP < hero.HT) {
                        hero.HP = Math.min(hero.HP + hero.HT / 4, hero.HT)
                        hero.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)
                    }
                }
            }
        }

        fun cook(ingredient: MysteryMeat): Food {
            val result = FrozenCarpaccio()
            result.quantity = ingredient.quantity()
            return result
        }
    }
}
