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
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.watabou.noosa.audio.Sample

abstract class InventoryScroll : Scroll() {

    protected var inventoryTitle = Messages.get(this, "inv_title")
    protected var mode: WndBag.Mode = WndBag.Mode.ALL

    override fun doRead() {

        if (!isKnown) {
            setKnown()
            identifiedByUse = true
        } else {
            identifiedByUse = false
        }

        GameScene.selectItem(itemSelector, mode, inventoryTitle)
    }

    private fun confirmCancelation() {
        GameScene.show(object : WndOptions(Messages.titleCase(name()), Messages.get(this, "warning"),
                Messages.get(this, "yes"), Messages.get(this, "no")) {
            override fun onSelect(index: Int) {
                when (index) {
                    0 -> {
                        Item.curUser.spendAndNext(Scroll.TIME_TO_READ)
                        identifiedByUse = false
                    }
                    1 -> GameScene.selectItem(itemSelector, mode, inventoryTitle)
                }
            }

            override fun onBackPressed() {}
        })
    }

    protected abstract fun onItemSelected(item: Item?)

    companion object {

        protected var identifiedByUse = false
        protected var itemSelector: WndBag.Listener = WndBag.Listener { item ->
            //FIXME this safety check shouldn't be necessary
            //it would be better to eliminate the curItem static variable.
            if (Item.curItem !is InventoryScroll) {
                return@Listener
            }

            if (item != null) {

                (Item.curItem as InventoryScroll).onItemSelected(item)
                (Item.curItem as InventoryScroll).readAnimation()

                Sample.INSTANCE.play(Assets.SND_READ)
                Invisibility.dispel()

            } else if (identifiedByUse && !(Item.curItem as Scroll).ownedByBook) {

                (Item.curItem as InventoryScroll).confirmCancelation()

            } else if (!(Item.curItem as Scroll).ownedByBook) {

                Item.curItem.collect(Item.curUser.belongings.backpack)

            }
        }
    }
}
