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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Random

import java.util.ArrayList

class ScrollOfIdentify : InventoryScroll() {

    init {
        initials = 0
        mode = WndBag.Mode.UNIDENTIFED

        bones = true
    }

    override fun empoweredRead() {
        val unIDed = ArrayList<Item>()

        for (i in Item.curUser.belongings) {
            if (!i.isIdentified) {
                unIDed.add(i)
            }
        }

        if (unIDed.size > 1) {
            Random.element(unIDed)!!.identify()
            Sample.INSTANCE.play(Assets.SND_TELEPORT)
        }

        doRead()
    }

    override fun onItemSelected(item: Item?) {

        Item.curUser.sprite!!.parent!!.add(Identification(Item.curUser.sprite!!.center().offset(0f, -16f)))

        item!!.identify()
        GLog.i(Messages.get(this, "it_is", item))

        Badges.validateItemLevelAquired(item)
    }

    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }
}
