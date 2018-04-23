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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class Belongings(private val owner: Hero) : Iterable<Item> {

    var backpack: Bag

    var weapon: KindOfWeapon? = null
    var armor: Armor? = null
    var misc1: KindofMisc? = null
    var misc2: KindofMisc? = null

    init {

        backpack = object : Bag() {
            init {
                name = Messages.get(Bag::class.java, "name")
                size = BACKPACK_SIZE
            }
        }
        backpack.owner = owner
    }

    fun storeInBundle(bundle: Bundle) {

        backpack.storeInBundle(bundle)

        bundle.put(WEAPON, weapon)
        bundle.put(ARMOR, armor)
        bundle.put(MISC1, misc1)
        bundle.put(MISC2, misc2)
    }

    fun restoreFromBundle(bundle: Bundle) {

        //moving keys to Notes, for pre-0.6.1 saves
        if (bundle.contains("ironKeys")) {
            val ironKeys = bundle.getIntArray("ironKeys")
            for (i in ironKeys!!.indices) {
                if (ironKeys[i] > 0) {
                    Notes.add(IronKey(i).quantity(ironKeys[i]) as Key)
                }
            }
        }

        if (bundle.contains("specialKeys")) {
            val specialKeys = bundle.getIntArray("specialKeys")
            for (i in specialKeys!!.indices) {
                if (specialKeys[i] > 0) {
                    if (i % 5 == 0) {
                        Notes.add(SkeletonKey(i).quantity(specialKeys[i]) as Key)
                    } else {
                        Notes.add(GoldenKey(i).quantity(specialKeys[i]) as Key)
                    }
                }
            }
        }

        backpack.clear()
        backpack.restoreFromBundle(bundle)

        weapon = bundle.get(WEAPON) as KindOfWeapon
        if (weapon != null) {
            weapon!!.activate(owner)
        }

        armor = bundle.get(ARMOR) as Armor
        if (armor != null) {
            armor!!.activate(owner)
        }

        misc1 = bundle.get(MISC1) as KindofMisc
        if (misc1 != null) {
            misc1!!.activate(owner)
        }

        misc2 = bundle.get(MISC2) as KindofMisc
        if (misc2 != null) {
            misc2!!.activate(owner)
        }
    }

    fun <T : Item> getItem(itemClass: Class<T>): T? {

        for (item in this) {
            if (itemClass.isInstance(item)) {
                return item as T
            }
        }

        return null
    }

    fun getSimilar(similar: Item): Item? {

        for (item in this) {
            if (item.isSimilar(similar)) {
                return item
            }
        }

        return null
    }

    fun identify() {
        for (item in this) {
            item.identify()
        }
    }

    fun observe() {
        if (weapon != null) {
            weapon!!.identify()
            Badges.validateItemLevelAquired(weapon!!)
        }
        if (armor != null) {
            armor!!.identify()
            Badges.validateItemLevelAquired(armor!!)
        }
        if (misc1 != null) {
            misc1!!.identify()
            Badges.validateItemLevelAquired(misc1!!)
        }
        if (misc2 != null) {
            misc2!!.identify()
            Badges.validateItemLevelAquired(misc2!!)
        }
        for (item in backpack) {
            item.cursedKnown = true
        }
    }

    fun uncurseEquipped() {
        ScrollOfRemoveCurse.uncurse(owner, armor, weapon, misc1, misc2)
    }

    fun randomUnequipped(): Item? {
        return Random.element(backpack.items)
    }

    fun resurrect(depth: Int) {

        for (item in backpack.items.toTypedArray<Item>()) {
            if (item is Key) {
                if ((item as Key).depth == depth) {
                    item.detachAll(backpack)
                }
            } else if (item.unique) {
                item.detachAll(backpack)
                //you keep the bag itself, not its contents.
                if (item is Bag) {
                    (item as Bag).resurrect()
                }
                item.collect()
            } else if (!item.isEquipped(owner)) {
                item.detachAll(backpack)
            }
        }

        if (weapon != null) {
            weapon!!.cursed = false
            weapon!!.activate(owner)
        }

        if (armor != null) {
            armor!!.cursed = false
            armor!!.activate(owner)
        }

        if (misc1 != null) {
            misc1!!.cursed = false
            misc1!!.activate(owner)
        }
        if (misc2 != null) {
            misc2!!.cursed = false
            misc2!!.activate(owner)
        }
    }

    fun charge(charge: Float): Int {

        val count = 0

        for (charger in owner.buffs<Wand.Charger>(Wand.Charger::class.java)) {
            charger.gainCharge(charge)
        }

        return count
    }

    override fun iterator(): Iterator<Item> {
        return ItemIterator()
    }

    private inner class ItemIterator : Iterator<Item> {

        private var index = 0

        private val backpackIterator = backpack.iterator()

        private val equipped = arrayOf<Item?>(weapon, armor, misc1, misc2)
        private val backpackIndex = equipped.size

        override fun hasNext(): Boolean {

            for (i in index until backpackIndex) {
                if (equipped[i] != null) {
                    return true
                }
            }

            return backpackIterator.hasNext()
        }

        override fun next(): Item {

            while (index < backpackIndex) {
                val item = equipped[index++]
                if (item != null) {
                    return item
                }
            }

            return backpackIterator.next()
        }

        fun remove() {
            when (index) {
                0 -> {
                    weapon = null
                    equipped[0] = weapon
                }
                1 -> {
                    armor = null
                    equipped[1] = armor
                }
                2 -> {
                    misc1 = null
                    equipped[2] = misc1
                }
                3 -> {
                    misc2 = null
                    equipped[3] = misc2
                }
                else -> backpackIterator.remove()
            }
        }
    }

    companion object {

        val BACKPACK_SIZE = 20

        private val WEAPON = "weapon"
        private val ARMOR = "armor"
        private val MISC1 = "misc1"
        private val MISC2 = "misc2"

        fun preview(info: GamesInProgress.Info, bundle: Bundle) {
            if (bundle.contains(ARMOR)) {
                info.armorTier = (bundle.get(ARMOR) as Armor).tier
            } else {
                info.armorTier = 0
            }
        }
    }
}
