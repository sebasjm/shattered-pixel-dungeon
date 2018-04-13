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

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag

class ScrollOfUpgrade : InventoryScroll() {

    init {
        initials = 11
        mode = WndBag.Mode.UPGRADEABLE
    }

    override fun onItemSelected(item: Item?) {

        upgrade(Item.curUser)

        //logic for telling the user when item properties change from upgrades
        //...yes this is rather messy
        if (item is Weapon) {
            val w = item as Weapon?
            val wasCursed = w!!.cursed
            val hadCursedEnchant = w.hasCurseEnchant()
            val hadGoodEnchant = w.hasGoodEnchant()

            w.upgrade()

            if (hadCursedEnchant && !w.hasCurseEnchant()) {
                removeCurse(Dungeon.hero!!)
            } else if (wasCursed && !w.cursed) {
                weakenCurse(Dungeon.hero!!)
            }
            if (hadGoodEnchant && !w.hasGoodEnchant()) {
                GLog.w(Messages.get(Weapon::class.java, "incompatible"))
            }

        } else if (item is Armor) {
            val a = item as Armor?
            val wasCursed = a!!.cursed
            val hadCursedGlyph = a.hasCurseGlyph()
            val hadGoodGlyph = a.hasGoodGlyph()

            a.upgrade()

            if (hadCursedGlyph && !a.hasCurseGlyph()) {
                removeCurse(Dungeon.hero!!)
            } else if (wasCursed && !a.cursed) {
                weakenCurse(Dungeon.hero!!)
            }
            if (hadGoodGlyph && !a.hasGoodGlyph()) {
                GLog.w(Messages.get(Armor::class.java, "incompatible"))
            }

        } else if (item is Wand || item is Ring) {
            val wasCursed = item.cursed

            item.upgrade()

            if (wasCursed && !item.cursed) {
                removeCurse(Dungeon.hero!!)
            }

        } else {
            item!!.upgrade()
        }

        Badges.validateItemLevelAquired(item)
    }

    override fun empoweredRead() {
        //does nothing for now, this should never happen.
    }

    override fun price(): Int {
        return if (isKnown) 50 * quantity else super.price()
    }

    companion object {

        fun upgrade(hero: Hero) {
            hero.sprite!!.emitter().start(Speck.factory(Speck.UP), 0.2f, 3)
        }

        fun weakenCurse(hero: Hero) {
            GLog.p(Messages.get(ScrollOfUpgrade::class.java, "weaken_curse"))
            hero.sprite!!.emitter().start(ShadowParticle.UP, 0.05f, 5)
        }

        fun removeCurse(hero: Hero) {
            GLog.p(Messages.get(ScrollOfUpgrade::class.java, "remove_curse"))
            hero.sprite!!.emitter().start(ShadowParticle.UP, 0.05f, 10)
        }
    }
}
