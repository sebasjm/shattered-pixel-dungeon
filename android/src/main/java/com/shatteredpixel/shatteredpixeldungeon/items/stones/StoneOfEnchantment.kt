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

package com.shatteredpixel.shatteredpixeldungeon.items.stones

import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag

class StoneOfEnchantment : InventoryStone() {

    init {
        mode = WndBag.Mode.ENCHANTABLE
        image = ItemSpriteSheet.STONE_TIWAZ
    }

    override fun onItemSelected(item: Item?) {

        if (item is Weapon) {

            item.enchant()

        } else {

            (item as Armor).inscribe()

        }

        Item.curUser!!.sprite!!.emitter().start(Speck.factory(Speck.LIGHT), 0.1f, 5)
        Enchanting.show(Item.curUser!!, item)

        //FIXME add this to translations
        if (item is Weapon) {
            GLog.p(Messages.get(this.javaClass, "weapon"))
        } else {
            GLog.p(Messages.get(this.javaClass, "armor"))
        }

    }
}