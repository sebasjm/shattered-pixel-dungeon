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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions


abstract class KindofMisc : EquipableItem() {

    override fun doEquip(hero: Hero): Boolean {

        if (hero.belongings.misc1 != null && hero.belongings.misc2 != null) {

            val m1 = hero.belongings.misc1
            val m2 = hero.belongings.misc2

            GameScene.show(
                    object : WndOptions(Messages.get(KindofMisc::class.java, "unequip_title"),
                            Messages.get(KindofMisc::class.java, "unequip_message"),
                            Messages.titleCase(m1!!.toString()),
                            Messages.titleCase(m2!!.toString())) {

                        override fun onSelect(index: Int) {

                            val equipped = if (index == 0) m1 else m2
                            //temporarily give 1 extra backpack spot to support swapping with a full inventory
                            hero.belongings.backpack.size++
                            if (equipped!!.doUnequip(hero, true, false)) {
                                //fully re-execute rather than just call doEquip as we want to preserve quickslot
                                execute(hero, EquipableItem.AC_EQUIP)
                            }
                            hero.belongings.backpack.size--
                        }
                    })

            return false

        } else {

            if (hero.belongings.misc1 == null) {
                hero.belongings.misc1 = this
            } else {
                hero.belongings.misc2 = this
            }

            detach(hero.belongings.backpack)

            activate(hero)

            cursedKnown = true
            if (cursed) {
                EquipableItem.equipCursed(hero)
                GLog.n(Messages.get(this.javaClass, "equip_cursed", this))
            }

            hero.spendAndNext(TIME_TO_EQUIP)
            return true

        }

    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {

            if (hero!!.belongings.misc1 === this) {
                hero!!.belongings.misc1 = null
            } else {
                hero!!.belongings.misc2 = null
            }

            return true

        } else {

            return false

        }
    }

    override fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.misc1 === this || hero.belongings.misc2 === this
    }

    companion object {

        private val TIME_TO_EQUIP = 1f
    }

}
