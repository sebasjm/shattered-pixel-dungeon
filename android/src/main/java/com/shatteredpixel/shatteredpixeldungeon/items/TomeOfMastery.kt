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

package com.shatteredpixel.shatteredpixeldungeon.items

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseWay
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

class TomeOfMastery : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        stackable = false
        image = ItemSpriteSheet.MASTERY

        unique = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_READ)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_READ) {

            Item.curUser = hero

            var way1: HeroSubClass? = null
            var way2: HeroSubClass? = null
            when (hero.heroClass) {
                HeroClass.WARRIOR -> {
                    way1 = HeroSubClass.GLADIATOR
                    way2 = HeroSubClass.BERSERKER
                }
                HeroClass.MAGE -> {
                    way1 = HeroSubClass.BATTLEMAGE
                    way2 = HeroSubClass.WARLOCK
                }
                HeroClass.ROGUE -> {
                    way1 = HeroSubClass.FREERUNNER
                    way2 = HeroSubClass.ASSASSIN
                }
                HeroClass.HUNTRESS -> {
                    way1 = HeroSubClass.SNIPER
                    way2 = HeroSubClass.WARDEN
                }
            }
            GameScene.show(WndChooseWay(this, way1!!, way2!!))

        }
    }

    override fun doPickUp(hero: Hero): Boolean {
        Badges.validateMastery()
        return super.doPickUp(hero)
    }

    fun choose(way: HeroSubClass) {

        detach(Item.curUser!!.belongings.backpack)

        Item.curUser!!.spend(TomeOfMastery.TIME_TO_READ)
        Item.curUser!!.busy()

        Item.curUser!!.subClass = way

        Item.curUser!!.sprite!!.operate(Item.curUser!!.pos)
        Sample.INSTANCE.play(Assets.SND_MASTERY)

        SpellSprite.show(Item.curUser!!, SpellSprite.MASTERY)
        Item.curUser!!.sprite!!.emitter().burst(Speck.factory(Speck.MASTERY), 12)
        GLog.w(Messages.get(this.javaClass, "way", way.title()))

        if (way == HeroSubClass.BERSERKER) {
            Buff.affect<Berserk>(Item.curUser!!, Berserk::class.java)
        }
    }

    companion object {

        val TIME_TO_READ = 10f

        val AC_READ = "READ"
    }
}
