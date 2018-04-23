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

package com.shatteredpixel.shatteredpixeldungeon.windows

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window

class WndTradeItem : Window {

    private var owner: WndBag? = null

    constructor(item: Item, owner: WndBag) : super() {

        this.owner = owner

        var pos = createDescription(item, false)

        if (item.quantity() == 1) {

            val btnSell = object : RedButton(Messages.get(this.javaClass, "sell", item.price())) {
                override fun onClick() {
                    sell(item)
                    hide()
                }
            }
            btnSell.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnSell)

            pos = btnSell.bottom()

        } else {

            val priceAll = item.price()
            val btnSell1 = object : RedButton(Messages.get(this.javaClass, "sell_1", priceAll / item.quantity())) {
                override fun onClick() {
                    sellOne(item)
                    hide()
                }
            }
            btnSell1.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnSell1)
            val btnSellAll = object : RedButton(Messages.get(this.javaClass, "sell_all", priceAll)) {
                override fun onClick() {
                    sell(item)
                    hide()
                }
            }
            btnSellAll.setRect(0f, btnSell1.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnSellAll)

            pos = btnSellAll.bottom()

        }

        val btnCancel = object : RedButton(Messages.get(this.javaClass, "cancel")) {
            override fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnCancel)

        resize(WIDTH, btnCancel.bottom().toInt())
    }

    constructor(heap: Heap, canBuy: Boolean) : super() {

        val item = heap.peek()

        val pos = createDescription(item, true)

        val price = price(item)

        if (canBuy) {

            val btnBuy = object : RedButton(Messages.get(this.javaClass, "buy", price)) {
                override fun onClick() {
                    hide()
                    buy(heap)
                }
            }
            btnBuy.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            btnBuy.enable(price <= Dungeon.gold)
            add(btnBuy)

            val btnCancel = object : RedButton(Messages.get(this.javaClass, "cancel")) {
                override fun onClick() {
                    hide()
                }
            }

            val thievery = Dungeon.hero!!.buff<MasterThievesArmband.Thievery>(MasterThievesArmband.Thievery::class.java)
            if (thievery != null) {
                val chance = thievery.stealChance(price)
                val btnSteal = object : RedButton(Messages.get(this.javaClass, "steal", Math.min(100, (chance * 100).toInt()))) {
                    override fun onClick() {
                        if (thievery.steal(price)) {
                            val hero = Dungeon.hero!!
                            val item = heap.pickUp()
                            hide()

                            if (!item.doPickUp(hero)) {
                                Dungeon.level!!.drop(item, heap.pos).sprite!!.drop()
                            }
                        } else {
                            for (mob in Dungeon.level!!.mobs) {
                                if (mob is Shopkeeper) {
                                    mob.yell(Messages.get(mob.javaClass, "thief"))
                                    mob.flee()
                                    break
                                }
                            }
                            hide()
                        }
                    }
                }
                btnSteal.setRect(0f, btnBuy.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
                add(btnSteal)

                btnCancel.setRect(0f, btnSteal.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            } else
                btnCancel.setRect(0f, btnBuy.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())

            add(btnCancel)

            resize(WIDTH, btnCancel.bottom().toInt())

        } else {

            resize(WIDTH, pos.toInt())

        }
    }

    override fun hide() {

        super.hide()

        if (owner != null) {
            owner!!.hide()
            Shopkeeper.sell()
        }
    }

    private fun createDescription(item: Item, forSale: Boolean): Float {

        // Title
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(item))
        titlebar.label(if (forSale)
            Messages.get(this.javaClass, "sale", item.toString(), price(item))
        else
            Messages.titleCase(item.toString()))
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        // Upgraded / degraded
        if (item.levelKnown) {
            if (item.level() < 0) {
                titlebar.color(ItemSlot.DEGRADED)
            } else if (item.level() > 0) {
                titlebar.color(ItemSlot.UPGRADED)
            }
        }

        // Description
        val info = PixelScene.renderMultiline(item.info(), 6)
        info.maxWidth(WIDTH)
        info.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(info)

        return info.bottom()
    }

    private fun sell(item: Item) {

        val hero = Dungeon.hero!!

        if (item.isEquipped(hero) && !(item as EquipableItem).doUnequip(hero, false)) {
            return
        }
        item.detachAll(hero!!.belongings.backpack)

        Gold(item.price()).doPickUp(hero)

        //selling items in the sell interface doesn't spend time
        hero.spend(-hero.cooldown())
    }

    private fun sellOne(item: Item) {
        var item = item

        if (item.quantity() <= 1) {
            sell(item)
        } else {

            val hero = Dungeon.hero!!

            item = item.detach(hero!!.belongings.backpack)!!

            Gold(item.price()).doPickUp(hero)

            //selling items in the sell interface doesn't spend time
            hero.spend(-hero.cooldown())
        }
    }

    private fun price(item: Item): Int {
        val price = item.price() * 5 * (Dungeon.depth / 5 + 1)
        return price
    }

    private fun buy(heap: Heap) {

        val hero = Dungeon.hero!!
        val item = heap.pickUp()

        val price = price(item)
        Dungeon.gold -= price

        if (!item.doPickUp(hero)) {
            Dungeon.level!!.drop(item, heap.pos).sprite!!.drop()
        }
    }

    companion object {

        private val GAP = 2f
        private val WIDTH = 120
        private val BTN_HEIGHT = 16
    }
}
