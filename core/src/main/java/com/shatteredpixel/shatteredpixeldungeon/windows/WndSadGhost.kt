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

import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog

class WndSadGhost(ghost: Ghost, type: Int) : Window() {

    init {

        val titlebar = IconTitle()
        val message: RenderedTextMultiline
        when (type) {
            1 -> {
                titlebar.icon(FetidRatSprite())
                titlebar.label(Messages.get(this, "rat_title"))
                message = PixelScene.renderMultiline(Messages.get(this, "rat") + Messages.get(this, "give_item"), 6)
            }
            2 -> {
                titlebar.icon(GnollTricksterSprite())
                titlebar.label(Messages.get(this, "gnoll_title"))
                message = PixelScene.renderMultiline(Messages.get(this, "gnoll") + Messages.get(this, "give_item"), 6)
            }
            3 -> {
                titlebar.icon(GreatCrabSprite())
                titlebar.label(Messages.get(this, "crab_title"))
                message = PixelScene.renderMultiline(Messages.get(this, "crab") + Messages.get(this, "give_item"), 6)
            }
            else -> {
                titlebar.icon(FetidRatSprite())
                titlebar.label(Messages.get(this, "rat_title"))
                message = PixelScene.renderMultiline(Messages.get(this, "rat") + Messages.get(this, "give_item"), 6)
            }
        }

        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        message.maxWidth(WIDTH)
        message.setPos(0f, titlebar.bottom() + GAP)
        add(message)

        val btnWeapon = object : RedButton(Messages.get(this, "weapon")) {
            override fun onClick() {
                selectReward(ghost, Ghost.Quest.weapon)
            }
        }
        btnWeapon.setRect(0f, message.top() + message.height() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnWeapon)

        if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
            val btnArmor = object : RedButton(Messages.get(this, "armor")) {
                override fun onClick() {
                    selectReward(ghost, Ghost.Quest.armor)
                }
            }
            btnArmor.setRect(0f, btnWeapon.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnArmor)

            resize(WIDTH, btnArmor.bottom().toInt())
        } else {
            resize(WIDTH, btnWeapon.bottom().toInt())
        }
    }

    private fun selectReward(ghost: Ghost, reward: Item?) {

        hide()

        if (reward == null) return

        reward.identify()
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Messages.get(Dungeon.hero!!, "you_now_have", reward.name()))
        } else {
            Dungeon.level!!.drop(reward, ghost.pos).sprite!!.drop()
        }

        ghost.yell(Messages.get(this, "farewell"))
        ghost.die(null)

        Ghost.Quest.complete()
    }

    companion object {

        private val WIDTH = 120
        private val BTN_HEIGHT = 20
        private val GAP = 2f
    }
}
