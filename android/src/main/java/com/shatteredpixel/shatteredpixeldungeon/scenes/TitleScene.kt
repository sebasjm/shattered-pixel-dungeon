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
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs
import com.shatteredpixel.shatteredpixeldungeon.ui.ChangesButton
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton
import com.shatteredpixel.shatteredpixeldungeon.ui.LanguageButton
import com.shatteredpixel.shatteredpixeldungeon.ui.PrefsButton
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStartGame
import com.watabou.glwrap.Blending
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button

class TitleScene : PixelScene() {

    override fun create() {

        super.create()

        Music.INSTANCE.play(Assets.THEME, true)

        PixelScene.uiCamera!!.visible = false

        val w = Camera.main!!.width
        val h = Camera.main!!.height

        val archs = Archs()
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)

        val title = BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON)
        add(title)

        val topRegion = Math.max(95f, h * 0.45f)

        title.x = (w - title.width()) / 2f
        if (SPDSettings.landscape())
            title.y = (topRegion - title.height()) / 2f
        else
            title.y = 16 + (topRegion - title.height() - 16f) / 2f

        PixelScene.align(title)

        placeTorch(title.x + 22, title.y + 46)
        placeTorch(title.x + title.width - 22, title.y + 46)

        val signs = object : Image(BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON_SIGNS)) {
            private var time = 0f
            override fun update() {
                super.update()
                time += Game.elapsed
                am = Math.max(0f, Math.sin(time.toDouble()).toFloat())
                if (time >= 1.5f * Math.PI) time = 0f
            }

            override fun draw() {
                Blending.setLightMode()
                super.draw()
                Blending.setNormalMode()
            }
        }
        signs.x = title.x + (title.width() - signs.width()) / 2f
        signs.y = title.y
        add(signs)

        val btnBadges = object : DashboardItem(Messages.get(this@TitleScene.javaClass, "badges"), 3) {
            override fun onClick() {
                ShatteredPixelDungeon.switchNoFade(BadgesScene::class.java)
            }
        }
        add(btnBadges)

        val btnAbout = object : DashboardItem(Messages.get(this@TitleScene.javaClass, "about"), 1) {
            override fun onClick() {
                ShatteredPixelDungeon.switchNoFade(AboutScene::class.java)
            }
        }
        add(btnAbout)

        val btnPlay = object : DashboardItem(Messages.get(this@TitleScene.javaClass, "play"), 0) {
            override fun onClick() {
                if (GamesInProgress.checkAll().size == 0) {
                    this@TitleScene.add(WndStartGame(1))
                } else {
                    ShatteredPixelDungeon.switchNoFade(StartScene::class.java)
                }
            }
        }
        add(btnPlay)

        val btnRankings = object : DashboardItem(Messages.get(this@TitleScene.javaClass, "rankings"), 2) {
            override fun onClick() {
                ShatteredPixelDungeon.switchNoFade(RankingsScene::class.java)
            }
        }
        add(btnRankings)

        if (SPDSettings.landscape()) {
            btnRankings.setPos(w / 2 - btnRankings.width(), topRegion)
            btnBadges.setPos((w / 2).toFloat(), topRegion)
            btnPlay.setPos(btnRankings.left() - btnPlay.width(), topRegion)
            btnAbout.setPos(btnBadges.right(), topRegion)
        } else {
            btnPlay.setPos(w / 2 - btnPlay.width(), topRegion)
            btnRankings.setPos((w / 2).toFloat(), btnPlay.top())
            btnBadges.setPos(w / 2 - btnBadges.width(), btnPlay.top() + DashboardItem.SIZE)
            btnAbout.setPos((w / 2).toFloat(), btnBadges.top())
        }

        val version = BitmapText("v " + Game.version + "", PixelScene.pixelFont!!)
        version.measure()
        version.hardlight(0xCCCCCC)
        version.x = w - version.width()
        version.y = h - version.height()
        add(version)

        val changes = ChangesButton()
        changes.setPos(w - changes.width(), h.toFloat() - version.height() - changes.height())
        add(changes)

        var pos = 0

        val btnPrefs = PrefsButton()
        btnPrefs.setRect(pos.toFloat(), 0f, 16f, 16f)
        add(btnPrefs)

        pos += btnPrefs.width().toInt()

        val btnLang = LanguageButton()
        btnLang.setRect(pos.toFloat(), 0f, 14f, 16f)
        add(btnLang)

        val btnExit = ExitButton()
        btnExit.setPos(w - btnExit.width(), 0f)
        add(btnExit)

        fadeIn()
    }

    private fun placeTorch(x: Float, y: Float) {
        val fb = Fireball()
        fb.setPos(x, y)
        add(fb)
    }

    private open class DashboardItem(text: String, index: Int) : Button() {

        private var image: Image? = null
        private var label: RenderedText? = null

        init {

            image!!.frame(image!!.texture!!.uvRect((index * IMAGE_SIZE).toFloat(), 0f, ((index + 1) * IMAGE_SIZE).toFloat(), IMAGE_SIZE.toFloat()))
            this.label!!.text(text)

            setSize(SIZE, SIZE)
        }

        override fun createChildren() {
            super.createChildren()

            image = Image(Assets.DASHBOARD)
            add(image)

            label = PixelScene.renderText(9)
            add(label)
        }

        override fun layout() {
            super.layout()

            image!!.x = x + (width - image!!.width()) / 2
            image!!.y = y
            PixelScene.align(image!!)

            label!!.x = x + (width - label!!.width()) / 2
            label!!.y = image!!.y + image!!.height() + 2f
            PixelScene.align(label!!)
        }

        override fun onTouchDown() {
            image!!.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK, 1f, 1f, 0.8f)
        }

        override fun onTouchUp() {
            image!!.resetColor()
        }

        companion object {

            val SIZE = 48f

            private val IMAGE_SIZE = 32
        }
    }
}
