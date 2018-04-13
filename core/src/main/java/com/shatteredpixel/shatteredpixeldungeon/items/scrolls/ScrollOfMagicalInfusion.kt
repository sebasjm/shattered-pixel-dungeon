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
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag

class ScrollOfMagicalInfusion : InventoryScroll() {

    init {
        initials = 2
        mode = WndBag.Mode.ENCHANTABLE
    }

    override fun onItemSelected(item: Item?) {

        if (item is Weapon)
            item.upgrade(true)
        else
            (item as Armor).upgrade(true)

        GLog.p(Messages.get(this, "infuse", item.name()))

        Badges.validateItemLevelAquired(item)

        Item.curUser.sprite!!.emitter().start(Speck.factory(Speck.UP), 0.2f, 3)
        Enchanting.show(Item.curUser, item)
    }

    override fun empoweredRead() {
        //does nothing for now, this should never happen.
    }

    override fun price(): Int {
        return if (isKnown) 100 * quantity else super.price()
    }
}
