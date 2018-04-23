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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog

class WndWandmaker(wandmaker: Wandmaker, item: Item) : Window() {

    init {

        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(item.image(), null))
        titlebar.label(Messages.titleCase(item.name()))
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        var msg = ""
        if (item is CorpseDust) {
            msg = Messages.get(this.javaClass, "dust")
        } else if (item is Embers) {
            msg = Messages.get(this.javaClass, "ember")
        } else if (item is Rotberry.Seed) {
            msg = Messages.get(this.javaClass, "berry")
        }

        val message = PixelScene.renderMultiline(msg, 6)
        message.maxWidth(WIDTH)
        message.setPos(0f, titlebar.bottom() + GAP)
        add(message)

        val btnWand1 = object : RedButton(Wandmaker.Quest.wand1!!.name()) {
            override fun onClick() {
                selectReward(wandmaker, item, Wandmaker.Quest.wand1!!)
            }
        }
        btnWand1.setRect(0f, message.top() + message.height() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnWand1)

        val btnWand2 = object : RedButton(Wandmaker.Quest.wand2!!.name()) {
            override fun onClick() {
                selectReward(wandmaker, item, Wandmaker.Quest.wand2!!)
            }
        }
        btnWand2.setRect(0f, btnWand1.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnWand2)

        resize(WIDTH, btnWand2.bottom().toInt())
    }

    private fun selectReward(wandmaker: Wandmaker, item: Item, reward: Wand) {

        hide()

        item.detach(Dungeon.hero!!.belongings.backpack)

        reward.identify()
        if (reward.doPickUp(Dungeon.hero!!)) {
            GLog.i(Messages.get(Dungeon.hero!!.javaClass, "you_now_have", reward.name()))
        } else {
            Dungeon.level!!.drop(reward, wandmaker.pos).sprite!!.drop()
        }

        wandmaker.yell(Messages.get(this.javaClass, "farewell", Dungeon.hero!!.givenName()))
        wandmaker.destroy()

        wandmaker.sprite!!.die()

        Wandmaker.Quest.complete()
    }

    companion object {

        private val WIDTH = 120
        private val BTN_HEIGHT = 20
        private val GAP = 2f
    }
}
