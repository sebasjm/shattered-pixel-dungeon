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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndItem
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

class BrokenSeal : Item() {

    override//scroll of upgrade can be used directly once, same as upgrading armor the seal is affixed to then removing it.
    val isUpgradable: Boolean
        get() = level() == 0

    init {
        image = ItemSpriteSheet.SEAL

        levelKnown = true
        cursedKnown = levelKnown
        unique = true
        bones = false

        defaultAction = AC_INFO
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_AFFIX)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_AFFIX) {
            Item.curItem = this
            GameScene.selectItem(armorSelector, WndBag.Mode.ARMOR, Messages.get(this.javaClass, "prompt"))
        } else if (action == AC_INFO) {
            GameScene.show(WndItem(null, this, true))
        }
    }

    class WarriorShield : Buff() {

        private var armor: Armor? = null
        private var partialShield: Float = 0.toFloat()

        @Synchronized
        override fun act(): Boolean {
            if (armor == null)
                detach()
            else if (armor!!.isEquipped(target!! as Hero)) {
                if (target!!.SHLD < maxShield()) {
                    partialShield += (1 / (35 * Math.pow(0.885, (maxShield() - target!!.SHLD - 1).toDouble()))).toFloat()
                }
            }
            while (partialShield >= 1) {
                target!!.SHLD++
                partialShield--
            }
            spend(Actor.TICK)
            return true
        }

        @Synchronized
        fun setArmor(arm: Armor?) {
            armor = arm
        }

        @Synchronized
        fun maxShield(): Int {
            return 1 + armor!!.tier + armor!!.level()
        }
    }

    companion object {

        val AC_AFFIX = "AFFIX"

        //only to be used from the quickslot, for tutorial purposes mostly.
        val AC_INFO = "INFO_WINDOW"

        protected var armorSelector : WndBag.Listener = object: WndBag.Listener {
            override fun onSelect(item: Item?) {
                if (item != null && item is Armor) {
                    val armor = item
                    if (!armor.levelKnown) {
                        GLog.w(Messages.get(BrokenSeal::class.java, "unknown_armor"))
                    } else if (armor.cursed || armor.level() < 0) {
                        GLog.w(Messages.get(BrokenSeal::class.java, "degraded_armor"))
                    } else {
                        GLog.p(Messages.get(BrokenSeal::class.java, "affix"))
                        Dungeon.hero!!.sprite!!.operate(Dungeon.hero!!.pos)
                        Sample.INSTANCE.play(Assets.SND_UNLOCK)
                        armor.affixSeal(Item.curItem!! as BrokenSeal)
                        Item.curItem!!.detach(Dungeon.hero!!.belongings.backpack)
                        Badges.validateTutorial()
                    }
                }
            }

        }
    }
}
