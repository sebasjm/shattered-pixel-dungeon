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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

class ArmorKit : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    private val itemSelector = object: WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                this@ArmorKit.upgrade(item as Armor)
            }
        }

    }

    init {
        image = ItemSpriteSheet.KIT

        unique = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_APPLY)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action === AC_APPLY) {

            Item.curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.ARMOR, Messages.get(this.javaClass, "prompt"))

        }
    }

    private fun upgrade(armor: Armor) {

        detach(Item.curUser!!.belongings.backpack)

        Item.curUser!!.sprite!!.centerEmitter().start(Speck.factory(Speck.KIT), 0.05f, 10)
        Item.curUser!!.spend(TIME_TO_UPGRADE)
        Item.curUser!!.busy()

        GLog.w(Messages.get(this.javaClass, "upgraded", armor.name()))

        val classArmor = ClassArmor.upgrade(Item.curUser!!, armor)
        if (Item.curUser!!.belongings.armor === armor) {

            Item.curUser!!.belongings.armor = classArmor
            (Item.curUser!!.sprite as HeroSprite).updateArmor()
            classArmor.activate(Item.curUser!!)

        } else {

            armor.detach(Item.curUser!!.belongings.backpack)
            classArmor.collect(Item.curUser!!.belongings.backpack)

        }

        Item.curUser!!.sprite!!.operate(Item.curUser!!.pos)
        Sample.INSTANCE.play(Assets.SND_EVOKE)
    }

    companion object {

        private val TXT_UPGRADED = "you applied the armor kit to upgrade your %s"

        private val TIME_TO_UPGRADE = 2f

        private val AC_APPLY = "APPLY"
    }
}
