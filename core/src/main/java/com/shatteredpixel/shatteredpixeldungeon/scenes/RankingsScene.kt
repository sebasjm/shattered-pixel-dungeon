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
import com.shatteredpixel.shatteredpixeldungeon.Rankings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRanking
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Music
import com.watabou.noosa.ui.Button
import com.watabou.utils.GameMath

class RankingsScene : PixelScene() {

    private var archs: Archs? = null

    override fun create() {

        super.create()

        Music.INSTANCE.play(Assets.THEME, true)

        PixelScene.uiCamera.visible = false

        val w = Camera.main.width
        val h = Camera.main.height

        archs = Archs()
        archs!!.setSize(w.toFloat(), h.toFloat())
        add(archs)

        Rankings.INSTANCE.load()

        val title = PixelScene.renderText(Messages.get(this, "title"), 9)
        title.hardlight(Window.TITLE_COLOR)
        title.x = (w - title.width()) / 2f
        title.y = (16 - title.baseLine()) / 2f
        PixelScene.align(title)
        add(title)

        if (Rankings.INSTANCE.records!!.size > 0) {

            //attempts to give each record as much space as possible, ideally as much space as portrait mode
            val rowHeight = GameMath.gate(ROW_HEIGHT_MIN, ((PixelScene.uiCamera.height - 26) / Rankings.INSTANCE.records!!.size).toFloat(), ROW_HEIGHT_MAX)

            val left = (w - Math.min(MAX_ROW_WIDTH, w.toFloat())) / 2 + GAP
            val top = (h - rowHeight * Rankings.INSTANCE.records!!.size) / 2

            var pos = 0

            for (rec in Rankings.INSTANCE.records!!) {
                val row = Record(pos, pos == Rankings.INSTANCE.lastRecord, rec)
                val offset = (if (rowHeight <= 14)
                    if (pos % 2 == 1)
                        5
                    else
                        -5
                else
                    0).toFloat()
                row.setRect(left + offset, top + pos * rowHeight, w - left * 2, rowHeight)
                add(row)

                pos++
            }

            if (Rankings.INSTANCE.totalNumber >= Rankings.TABLE_SIZE) {
                val label = PixelScene.renderText(Messages.get(this, "total") + " ", 8)
                label.hardlight(0xCCCCCC)
                add(label)

                val won = PixelScene.renderText(Integer.toString(Rankings.INSTANCE.wonNumber), 8)
                won.hardlight(Window.SHPX_COLOR)
                add(won)

                val total = PixelScene.renderText("/" + Rankings.INSTANCE.totalNumber, 8)
                total.hardlight(0xCCCCCC)
                total.x = (w - total.width()) / 2
                total.y = top + pos * rowHeight + GAP
                add(total)

                val tw = label.width() + won.width() + total.width()
                label.x = (w - tw) / 2
                won.x = label.x + label.width()
                total.x = won.x + won.width()
                total.y = h.toFloat() - label.height() - GAP
                won.y = total.y
                label.y = won.y

                PixelScene.align(label)
                PixelScene.align(total)
                PixelScene.align(won)

            }

        } else {

            val noRec = PixelScene.renderText(Messages.get(this, "no_games"), 8)
            noRec.hardlight(0xCCCCCC)
            noRec.x = (w - noRec.width()) / 2
            noRec.y = (h - noRec.height()) / 2
            PixelScene.align(noRec)
            add(noRec)

        }

        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0f)
        add(btnExit)

        fadeIn()
    }

    override fun onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene::class.java)
    }

    class Record(pos: Int, latest: Boolean, private val rec: Rankings.Record) : Button() {

        protected var shield: ItemSprite
        private var flare: Flare? = null
        private var position: BitmapText? = null
        private var desc: RenderedTextMultiline? = null
        private var steps: Image? = null
        private var depth: BitmapText? = null
        private var classIcon: Image? = null
        private var level: BitmapText? = null

        init {

            if (latest) {
                flare = Flare(6, 24f)
                flare!!.angularSpeed = 90f
                flare!!.color(if (rec.win) FLARE_WIN else FLARE_LOSE)
                addToBack(flare)
            }

            if (pos != Rankings.TABLE_SIZE - 1) {
                position!!.text(Integer.toString(pos + 1))
            } else
                position!!.text(" ")
            position!!.measure()

            desc!!.text(Messages.titleCase(rec.desc()))

            //desc.measure();

            val odd = pos % 2

            if (rec.win) {
                shield.view(ItemSpriteSheet.AMULET, null)
                position!!.hardlight(TEXT_WIN[odd])
                desc!!.hardlight(TEXT_WIN[odd])
                depth!!.hardlight(TEXT_WIN[odd])
                level!!.hardlight(TEXT_WIN[odd])
            } else {
                position!!.hardlight(TEXT_LOSE[odd])
                desc!!.hardlight(TEXT_LOSE[odd])
                depth!!.hardlight(TEXT_LOSE[odd])
                level!!.hardlight(TEXT_LOSE[odd])

                if (rec.depth != 0) {
                    depth!!.text(Integer.toString(rec.depth))
                    depth!!.measure()
                    steps!!.copy(Icons.DEPTH.get())

                    add(steps)
                    add(depth)
                }

            }

            if (rec.herolevel != 0) {
                level!!.text(Integer.toString(rec.herolevel))
                level!!.measure()
                add(level)
            }

            classIcon!!.copy(Icons.get(rec.heroClass))
        }

        override fun createChildren() {

            super.createChildren()

            shield = ItemSprite(ItemSpriteSheet.TOMB, null)
            add(shield)

            position = BitmapText(PixelScene.pixelFont)
            add(position)

            desc = PixelScene.renderMultiline(7)
            add(desc)

            depth = BitmapText(PixelScene.pixelFont)

            steps = Image()

            classIcon = Image()
            add(classIcon)

            level = BitmapText(PixelScene.pixelFont)
        }

        override fun layout() {

            super.layout()

            shield.x = x
            shield.y = y + (height - shield.height) / 2f
            PixelScene.align(shield)

            position!!.x = shield.x + (shield.width - position!!.width()) / 2f
            position!!.y = shield.y + (shield.height - position!!.height()) / 2f + 1f
            PixelScene.align(position!!)

            if (flare != null) {
                flare!!.point(shield.center())
            }

            classIcon!!.x = x + width - classIcon!!.width
            classIcon!!.y = shield.y

            level!!.x = classIcon!!.x + (classIcon!!.width - level!!.width()) / 2f
            level!!.y = classIcon!!.y + (classIcon!!.height - level!!.height()) / 2f + 1f
            PixelScene.align(level!!)

            steps!!.x = x + width - steps!!.width - classIcon!!.width
            steps!!.y = shield.y

            depth!!.x = steps!!.x + (steps!!.width - depth!!.width()) / 2f
            depth!!.y = steps!!.y + (steps!!.height - depth!!.height()) / 2f + 1f
            PixelScene.align(depth!!)

            desc!!.maxWidth((steps!!.x - (shield.x + shield.width + GAP)).toInt())
            desc!!.setPos(shield.x + shield.width + GAP, shield.y + (shield.height - desc!!.height()) / 2f + 1f)
            PixelScene.align(desc!!)
        }

        override fun onClick() {
            if (rec.gameData != null) {
                parent!!.add(WndRanking(rec))
            } else {
                parent!!.add(WndError(Messages.get(RankingsScene::class.java, "no_info")))
            }
        }

        companion object {

            private val GAP = 4f

            private val TEXT_WIN = intArrayOf(0xFFFF88, 0xB2B25F)
            private val TEXT_LOSE = intArrayOf(0xDDDDDD, 0x888888)
            private val FLARE_WIN = 0x888866
            private val FLARE_LOSE = 0x666666
        }
    }

    companion object {

        private val ROW_HEIGHT_MAX = 20f
        private val ROW_HEIGHT_MIN = 12f

        private val MAX_ROW_WIDTH = 160f

        private val GAP = 4f
    }
}
