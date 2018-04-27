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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

open class Food : Item() {

    var energy = Hunger.HUNGRY
    var message = Messages.get(this.javaClass, "eat_msg")

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        stackable = true
        image = ItemSpriteSheet.RATION

        bones = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_EAT)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_EAT) {

            detach(hero.belongings.backpack)

            hero.buff<Hunger>(Hunger::class.java)?.satisfy(energy)
            GLog.i(message)

            when (hero.heroClass) {
                HeroClass.WARRIOR -> if (hero.HP < hero.HT) {
                    hero.HP = Math.min(hero.HP + 5, hero.HT)
                    hero.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)
                }
                HeroClass.MAGE -> {
                    //1 charge
                    Buff.affect<Recharging>(hero, Recharging::class.java, 4f)
                    ScrollOfRecharging.charge(hero)
                }
                HeroClass.ROGUE, HeroClass.HUNTRESS -> {
                }
            }

            hero.sprite!!.operate(hero.pos)
            hero.busy()
            SpellSprite.show(hero, SpellSprite.FOOD)
            Sample.INSTANCE.play(Assets.SND_EAT)

            hero.spend(TIME_TO_EAT)

            Statistics.foodEaten++
            Badges.validateFoodEaten()

        }
    }

    override fun price(): Int {
        return 10 * quantity
    }

    companion object {

        val TIME_TO_EAT = 3f

        val AC_EAT = "EAT"
    }
}
