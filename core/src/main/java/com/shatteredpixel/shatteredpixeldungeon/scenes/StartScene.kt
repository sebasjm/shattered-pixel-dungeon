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

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStartGame
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.noosa.NinePatch
import com.watabou.noosa.RenderedText
import com.watabou.noosa.ui.Button

import java.util.ArrayList

class StartScene : PixelScene() {

    override fun create() {
        super.create()

        Badges.loadGlobal()
        Journal.loadGlobal()

        PixelScene.uiCamera.visible = false

        val w = Camera.main.width
        val h = Camera.main.height

        val archs = Archs()
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)

        val btnExit = ExitButton()
        btnExit.setPos(w - btnExit.width(), 0f)
        add(btnExit)

        val title = PixelScene.renderText(Messages.get(this, "title"), 9)
        title.hardlight(Window.TITLE_COLOR)
        title.x = (w - title.width()) / 2f
        title.y = (16 - title.baseLine()) / 2f
        PixelScene.align(title)
        add(title)

        val games = GamesInProgress.checkAll()

        val slotGap = if (SPDSettings.landscape()) 5 else 10
        val slotCount = Math.min(GamesInProgress.MAX_SLOTS, games.size + 1)
        val slotsHeight = slotCount * SLOT_HEIGHT + (slotCount - 1) * slotGap

        var yPos = (h - slotsHeight) / 2f
        if (SPDSettings.landscape()) yPos += 8f

        for (game in games) {
            val existingGame = SaveSlotButton()
            existingGame.set(game.slot)
            existingGame.setRect((w - SLOT_WIDTH) / 2f, yPos, SLOT_WIDTH.toFloat(), SLOT_HEIGHT.toFloat())
            yPos += (SLOT_HEIGHT + slotGap).toFloat()
            PixelScene.align(existingGame)
            add(existingGame)

        }

        if (games.size < GamesInProgress.MAX_SLOTS) {
            val newGame = SaveSlotButton()
            newGame.set(GamesInProgress.firstEmpty())
            newGame.setRect((w - SLOT_WIDTH) / 2f, yPos, SLOT_WIDTH.toFloat(), SLOT_HEIGHT.toFloat())
            yPos += (SLOT_HEIGHT + slotGap).toFloat()
            PixelScene.align(newGame)
            add(newGame)
        }

        GamesInProgress.curSlot = 0
        ActionIndicator.action = null

        fadeIn()

    }

    override fun onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene::class.java)
    }

    private class SaveSlotButton : Button() {

        private var bg: NinePatch? = null

        private var hero: Image? = null
        private var name: RenderedText? = null

        private var steps: Image? = null
        private var depth: BitmapText? = null
        private var classIcon: Image? = null
        private var level: BitmapText? = null

        private var slot: Int = 0
        private var newGame: Boolean = false

        override fun createChildren() {
            super.createChildren()

            bg = Chrome.get(Chrome.Type.GEM)
            add(bg)

            name = PixelScene.renderText(9)
            add(name)
        }

        fun set(slot: Int) {
            this.slot = slot
            val info = GamesInProgress.check(slot)
            newGame = info == null
            if (newGame) {
                name!!.text(Messages.get(StartScene::class.java, "new"))

                if (hero != null) {
                    remove(hero)
                    hero = null
                    remove(steps)
                    steps = null
                    remove(depth)
                    depth = null
                    remove(classIcon)
                    classIcon = null
                    remove(level)
                    level = null
                }
            } else {

                if (info!!.subClass != HeroSubClass.NONE) {
                    name!!.text(Messages.titleCase(info.subClass!!.title()))
                } else {
                    name!!.text(Messages.titleCase(info.heroClass!!.title()))
                }

                if (hero == null) {
                    hero = Image(info.heroClass!!.spritesheet(), 0, 15 * info.armorTier, 12, 15)
                    add(hero)

                    steps = Image(Icons.get(Icons.DEPTH))
                    add(steps)
                    depth = BitmapText(PixelScene.pixelFont)
                    add(depth)

                    classIcon = Image(Icons.get(info.heroClass!!))
                    add(classIcon)
                    level = BitmapText(PixelScene.pixelFont)
                    add(level)
                } else {
                    hero!!.copy(Image(info.heroClass!!.spritesheet(), 0, 15 * info.armorTier, 12, 15))

                    classIcon!!.copy(Icons.get(info.heroClass!!))
                }

                depth!!.text(Integer.toString(info.depth))
                depth!!.measure()

                level!!.text(Integer.toString(info.level))
                level!!.measure()

                if (info.challenges > 0) {
                    name!!.hardlight(Window.TITLE_COLOR)
                    depth!!.hardlight(Window.TITLE_COLOR)
                    level!!.hardlight(Window.TITLE_COLOR)
                } else {
                    name!!.resetColor()
                    depth!!.resetColor()
                    level!!.resetColor()
                }

            }

            layout()
        }

        override fun layout() {
            super.layout()

            bg!!.x = x
            bg!!.y = y
            bg!!.size(width, height)

            if (hero != null) {
                hero!!.x = x + 8
                hero!!.y = y + (height - hero!!.height()) / 2f
                PixelScene.align(hero!!)

                name!!.x = hero!!.x + hero!!.width() + 6f
                name!!.y = y + (height - name!!.baseLine()) / 2f
                PixelScene.align(name!!)

                classIcon!!.x = x + width - classIcon!!.width() - 8f
                classIcon!!.y = y + (height - classIcon!!.height()) / 2f

                level!!.x = classIcon!!.x + (classIcon!!.width() - level!!.width()) / 2f
                level!!.y = classIcon!!.y + (classIcon!!.height() - level!!.height()) / 2f + 1f
                PixelScene.align(level!!)

                steps!!.x = classIcon!!.x - steps!!.width()
                steps!!.y = y + (height - steps!!.height()) / 2f

                depth!!.x = steps!!.x + (steps!!.width() - depth!!.width()) / 2f
                depth!!.y = steps!!.y + (steps!!.height() - depth!!.height()) / 2f + 1f
                PixelScene.align(depth!!)

            } else {
                name!!.x = x + (width - name!!.width()) / 2f
                name!!.y = y + (height - name!!.baseLine()) / 2f
                PixelScene.align(name!!)
            }


        }

        override fun onClick() {
            if (newGame) {
                ShatteredPixelDungeon.scene()!!.add(WndStartGame(slot))
            } else {
                ShatteredPixelDungeon.scene()!!.add(WndGameInProgress(slot))
            }
        }
    }

    companion object {

        private val SLOT_WIDTH = 120
        private val SLOT_HEIGHT = 30
    }
}
