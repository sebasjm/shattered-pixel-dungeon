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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window

class WndChooseWay(tome: TomeOfMastery, way1: HeroSubClass, way2: HeroSubClass) : Window() {

    init {

        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(tome.image(), null))
        titlebar.label(tome.name()!!)
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        val hl = PixelScene.renderMultiline(6)
        hl.text(way1.desc() + "\n\n" + way2.desc() + "\n\n" + Messages.get(this.javaClass, "message"), WIDTH)
        hl.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(hl)

        val btnWay1 = object : RedButton(way1.title().toUpperCase()) {
            override fun onClick() {
                hide()
                tome.choose(way1)
            }
        }
        btnWay1.setRect(0f, hl.bottom() + GAP, (WIDTH - GAP) / 2, BTN_HEIGHT.toFloat())
        add(btnWay1)

        val btnWay2 = object : RedButton(way2.title().toUpperCase()) {
            override fun onClick() {
                hide()
                tome.choose(way2)
            }
        }
        btnWay2.setRect(btnWay1.right() + GAP, btnWay1.top(), btnWay1.width(), BTN_HEIGHT.toFloat())
        add(btnWay2)

        val btnCancel = object : RedButton(Messages.get(this@WndChooseWay.javaClass, "cancel")) {
            override fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0f, btnWay2.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnCancel)

        resize(WIDTH, btnCancel.bottom().toInt())
    }

    companion object {

        private val WIDTH = 120
        private val BTN_HEIGHT = 18
        private val GAP = 2f
    }
}
