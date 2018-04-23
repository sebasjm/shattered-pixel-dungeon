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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

abstract class EquipableItem : Item() {

    init {
        bones = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(if (isEquipped(hero)) AC_UNEQUIP else AC_EQUIP)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_EQUIP) {
            //In addition to equipping itself, item reassigns itself to the quickslot
            //This is a special case as the item is being removed from inventory, but is staying with the hero.
            val slot = Dungeon.quickslot.getSlot(this)
            doEquip(hero)
            if (slot != -1) {
                Dungeon.quickslot.setSlot(slot, this)
                updateQuickslot()
            }
        } else if (action == AC_UNEQUIP) {
            doUnequip(hero, true)
        }
    }

    override fun doDrop(hero: Hero) {
        if (!isEquipped(hero) || doUnequip(hero, false, false)) {
            super.doDrop(hero)
        }
    }

    override fun cast(user: Hero?, dst: Int) {

        if (isEquipped(user!!)) {
            if (quantity == 1 && !this.doUnequip(user, false, false)) {
                return
            }
        }

        super.cast(user, dst)
    }

    protected open fun time2equip(hero: Hero?): Float {
        return 1f
    }

    abstract fun doEquip(hero: Hero): Boolean

    open fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {

        if (cursed) {
            GLog.w(Messages.get(EquipableItem::class.java, "unequip_cursed"))
            return false
        }

        if (single) {
            hero!!.spendAndNext(time2equip(hero))
        } else {
            hero!!.spend(time2equip(hero))
        }

        if (!collect || !collect(hero.belongings.backpack)) {
            onDetach()
            Dungeon.quickslot.clearItem(this)
            updateQuickslot()
            if (collect) Dungeon.level!!.drop(this, hero.pos)
        }

        return true
    }

    fun doUnequip(hero: Hero, collect: Boolean): Boolean {
        return doUnequip(hero, collect, true)
    }

    open fun activate(ch: Char) {}

    companion object {

        val AC_EQUIP = "EQUIP"
        val AC_UNEQUIP = "UNEQUIP"

        fun equipCursed(hero: Hero) {
            hero.sprite!!.emitter().burst(ShadowParticle.CURSE, 6)
            Sample.INSTANCE.play(Assets.SND_CURSED)
        }
    }
}
