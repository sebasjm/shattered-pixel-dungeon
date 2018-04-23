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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

abstract class InventoryStone : Runestone() {

    protected var inventoryTitle = Messages.get(this.javaClass, "inv_title")
    protected var mode: WndBag.Mode = WndBag.Mode.ALL

    init {
        defaultAction = AC_USE
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_USE)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {
        super.execute(hero, action)
        if (action == AC_USE) {
            Item.curItem = detach(hero.belongings.backpack)
            activate(Item.curUser!!.pos)
        }
    }

    override fun activate(cell: Int) {
        GameScene.selectItem(itemSelector, mode, inventoryTitle)
    }

    private fun useAnimation() {
        Item.curUser!!.spend(1f)
        Item.curUser!!.busy()
        Item.curUser!!.sprite!!.operate(Item.curUser!!.pos)
    }

    protected abstract fun onItemSelected(item: Item?)

    companion object {

        val AC_USE = "USE"

        protected var itemSelector: WndBag.Listener = lambda@ { item: Item? ->
            //FIXME this safety check shouldn't be necessary
            //it would be better to eliminate the curItem static variable.
            if (Item.curItem!! !is InventoryStone) {
                return@lambda
            }

            if (item != null) {

                (Item.curItem!! as InventoryStone).onItemSelected(item)
                (Item.curItem!! as InventoryStone).useAnimation()

                Sample.INSTANCE.play(Assets.SND_READ)
                Invisibility.dispel()

            } else {
                Item.curItem!!.collect(Item.curUser!!.belongings.backpack)
            }
        } as WndBag.Listener
    }

}
