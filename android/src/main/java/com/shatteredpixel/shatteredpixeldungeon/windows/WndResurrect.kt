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

import com.shatteredpixel.shatteredpixeldungeon.Rankings
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Game

class WndResurrect(ankh: Ankh, causeOfDeath: Any) : Window() {

    init {

        instance = this
        WndResurrect.causeOfDeath = causeOfDeath

        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(ankh.image(), null))
        titlebar.label(ankh.name()!!)
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        val message = PixelScene.renderMultiline(Messages.get(this.javaClass, "message"), 6)
        message.maxWidth(WIDTH)
        message.setPos(0f, titlebar.bottom() + GAP)
        add(message)

        val btnYes = object : RedButton(Messages.get(this@WndResurrect.javaClass, "yes")) {
            override fun onClick() {
                hide()

                Statistics.ankhsUsed++

                InterlevelScene.mode = InterlevelScene.Mode.RESURRECT
                Game.switchScene(InterlevelScene::class.java)
            }
        }
        btnYes.setRect(0f, message.top() + message.height() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnYes)

        val btnNo = object : RedButton(Messages.get(this@WndResurrect.javaClass, "no")) {
            override fun onClick() {
                hide()

                Rankings.INSTANCE.submit(false, WndResurrect.causeOfDeath!!.javaClass)
                Hero.reallyDie(WndResurrect.causeOfDeath!!)
            }
        }
        btnNo.setRect(0f, btnYes.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnNo)

        resize(WIDTH, btnNo.bottom().toInt())
    }

    override fun destroy() {
        super.destroy()
        instance = null
    }

    override fun onBackPressed() {}

    companion object {

        private val WIDTH = 120
        private val BTN_HEIGHT = 20
        private val GAP = 2f

        var instance: WndResurrect? = null
        var causeOfDeath: Any? = null
    }
}
