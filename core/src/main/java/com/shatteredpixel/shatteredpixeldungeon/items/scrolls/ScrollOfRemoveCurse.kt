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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample

class ScrollOfRemoveCurse : InventoryScroll() {

    init {
        initials = 8
        mode = WndBag.Mode.UNIDED_OR_CURSED
    }

    override fun empoweredRead() {
        for (item in Item.curUser.belongings) {
            if (item.cursed) {
                item.cursedKnown = true
            }
        }
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()
        doRead()
    }

    override fun onItemSelected(item: Item?) {
        Flare(6, 32f).show(Item.curUser.sprite, 2f)

        val procced = uncurse(Item.curUser, item!!)

        Weakness.detach(Item.curUser, Weakness::class.java)

        if (procced) {
            GLog.p(Messages.get(this, "cleansed"))
        } else {
            GLog.i(Messages.get(this, "not_cleansed"))
        }
    }

    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }

    companion object {

        fun uncurse(hero: Hero, vararg items: Item): Boolean {

            var procced = false
            for (item in items) {
                if (item != null && item.cursed) {
                    item.cursed = false
                    procced = true
                }
                if (item is Weapon) {
                    val w = item
                    if (w.hasCurseEnchant()) {
                        w.enchant(null)
                        w.cursed = false
                        procced = true
                    }
                }
                if (item is Armor) {
                    val a = item
                    if (a.hasCurseGlyph()) {
                        a.inscribe(null)
                        a.cursed = false
                        procced = true
                    }
                }
                if (item is Bag) {
                    for (bagItem in item.items) {
                        if (bagItem != null && bagItem.cursed) {
                            bagItem.cursed = false
                            procced = true
                        }
                    }
                }
            }

            if (procced) {
                hero.sprite!!.emitter().start(ShadowParticle.UP, 0.05f, 10)
                hero.updateHT(false) //for ring of might
            }

            return procced
        }
    }
}
