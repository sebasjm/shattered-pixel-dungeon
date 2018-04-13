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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Random

abstract class KindOfWeapon : EquipableItem() {

    override fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.weapon === this
    }

    override fun doEquip(hero: Hero): Boolean {

        detachAll(hero.belongings.backpack)

        if (hero.belongings.weapon == null || hero.belongings.weapon!!.doUnequip(hero, true)) {

            hero.belongings.weapon = this
            activate(hero)

            updateQuickslot()

            cursedKnown = true
            if (cursed) {
                EquipableItem.equipCursed(hero)
                GLog.n(Messages.get(KindOfWeapon::class.java, "equip_cursed"))
            }

            hero.spendAndNext(TIME_TO_EQUIP)
            return true

        } else {

            collect(hero.belongings.backpack)
            return false
        }
    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {

            hero!!.belongings.weapon = null
            return true

        } else {

            return false

        }
    }

    fun min(): Int {
        return min(level())
    }

    fun max(): Int {
        return max(level())
    }

    abstract fun min(lvl: Int): Int
    abstract fun max(lvl: Int): Int

    open fun damageRoll(owner: Char): Int {
        return Random.NormalIntRange(min(), max())
    }

    open fun accuracyFactor(owner: Char): Float {
        return 1f
    }

    open fun speedFactor(owner: Char): Float {
        return 1f
    }

    open fun reachFactor(owner: Char): Int {
        return 1
    }

    open fun defenseFactor(owner: Char): Int {
        return 0
    }

    open fun proc(attacker: Char, defender: Char, damage: Int): Int {
        return damage
    }

    companion object {

        protected val TIME_TO_EQUIP = 1f
    }

}
