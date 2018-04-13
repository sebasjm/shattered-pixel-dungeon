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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog

class WndImp(imp: Imp, tokens: DwarfToken) : Window() {

    init {

        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(tokens.image(), null))
        titlebar.label(Messages.titleCase(tokens.name()))
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        val message = PixelScene.renderMultiline(Messages.get(this, "message"), 6)
        message.maxWidth(WIDTH)
        message.setPos(0f, titlebar.bottom() + GAP)
        add(message)

        val btnReward = object : RedButton(Messages.get(this, "reward")) {
            override fun onClick() {
                takeReward(imp, tokens, Imp.Quest.reward)
            }
        }
        btnReward.setRect(0f, message.top() + message.height() + GAP.toFloat(), WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnReward)

        resize(WIDTH, btnReward.bottom().toInt())
    }

    private fun takeReward(imp: Imp, tokens: DwarfToken, reward: Item?) {

        hide()

        tokens.detachAll(Dungeon.hero!!.belongings.backpack)
        if (reward == null) return

        reward.identify()
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Messages.get(Dungeon.hero!!, "you_now_have", reward.name()))
        } else {
            Dungeon.level!!.drop(reward, imp.pos).sprite!!.drop()
        }

        imp.flee()

        Imp.Quest.complete()
    }

    companion object {

        private val WIDTH = 120
        private val BTN_HEIGHT = 20
        private val GAP = 2
    }
}
