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

package com.shatteredpixel.shatteredpixeldungeon.scenes

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.utils.Random

class AmuletScene : PixelScene() {

    private var amulet: Image? = null

    private var timer = 0f

    override fun create() {
        super.create()

        var text: RenderedTextMultiline? = null
        if (!noText) {
            text = PixelScene.renderMultiline(Messages.get(this, "text"), 8)
            text!!.maxWidth(WIDTH)
            add(text)
        }

        amulet = Image(Assets.AMULET)
        add(amulet)

        val btnExit = object : RedButton(Messages.get(this, "exit")) {
            override fun onClick() {
                Dungeon.win(Amulet::class.java)
                Dungeon.deleteGame(GamesInProgress.curSlot, true)
                Game.switchScene(RankingsScene::class.java)
            }
        }
        btnExit.setSize(WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnExit)

        val btnStay = object : RedButton(Messages.get(this, "stay")) {
            override fun onClick() {
                onBackPressed()
            }
        }
        btnStay.setSize(WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnStay)

        val height: Float
        if (noText) {
            height = amulet!!.height + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height()

            amulet!!.x = (Camera.main.width - amulet!!.width) / 2
            amulet!!.y = (Camera.main.height - height) / 2
            PixelScene.align(amulet!!)

            btnExit.setPos((Camera.main.width - btnExit.width()) / 2, amulet!!.y + amulet!!.height + LARGE_GAP)
            btnStay.setPos(btnExit.left(), btnExit.bottom() + SMALL_GAP)

        } else {
            height = amulet!!.height + LARGE_GAP + text!!.height() + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height()

            amulet!!.x = (Camera.main.width - amulet!!.width) / 2
            amulet!!.y = (Camera.main.height - height) / 2
            PixelScene.align(amulet!!)

            text.setPos((Camera.main.width - text.width()) / 2, amulet!!.y + amulet!!.height + LARGE_GAP)
            PixelScene.align(text)

            btnExit.setPos((Camera.main.width - btnExit.width()) / 2, text.top() + text.height() + LARGE_GAP)
            btnStay.setPos(btnExit.left(), btnExit.bottom() + SMALL_GAP)
        }

        Flare(8, 48f).color(0xFFDDBB, true).show(amulet, 0f).angularSpeed = +30f

        fadeIn()
    }

    override fun onBackPressed() {
        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE
        Game.switchScene(InterlevelScene::class.java)
    }

    override fun update() {
        super.update()

        if ((timer -= Game.elapsed) < 0) {
            timer = Random.Float(0.5f, 5f)

            val star = recycle(Speck::class.java) as Speck
            star.reset(0, amulet!!.x + 10.5f, amulet!!.y + 5.5f, Speck.DISCOVER)
            add(star)
        }
    }

    companion object {

        private val WIDTH = 120
        private val BTN_HEIGHT = 18
        private val SMALL_GAP = 2f
        private val LARGE_GAP = 8f

        var noText = false
    }
}
