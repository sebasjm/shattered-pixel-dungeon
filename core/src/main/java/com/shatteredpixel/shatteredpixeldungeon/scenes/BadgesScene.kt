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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBadge
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.utils.Callback
import com.watabou.utils.Random

class BadgesScene : PixelScene() {

    override fun create() {

        super.create()

        Music.INSTANCE.play(Assets.THEME, true)

        PixelScene.uiCamera.visible = false

        val w = Camera.main.width
        val h = Camera.main.height

        val archs = Archs()
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)

        val left = 5f
        val top = 16f

        val title = PixelScene.renderText(Messages.get(this, "title"), 9)
        title.hardlight(Window.TITLE_COLOR)
        title.x = (w - title.width()) / 2f
        title.y = (top - title.baseLine()) / 2f
        PixelScene.align(title)
        add(title)

        Badges.loadGlobal()

        val badges = Badges.filtered(true)

        var blankBadges = 34
        blankBadges -= badges.size
        if (badges.contains(Badges.Badge.ALL_ITEMS_IDENTIFIED)) blankBadges -= 6
        if (badges.contains(Badges.Badge.YASD)) blankBadges -= 5
        blankBadges = Math.max(0, blankBadges)

        //guarantees a max of 5 rows in landscape, and 8 in portrait, assuming a max of 40 buttons
        var nCols = if (SPDSettings.landscape()) 7 else 4
        if (badges.size + blankBadges > 32 && !SPDSettings.landscape()) nCols++

        val nRows = 1 + (blankBadges + badges.size) / nCols

        val badgeWidth = (w - 2 * left) / nCols
        val badgeHeight = (h - 2 * top) / nRows

        for (i in 0 until badges.size + blankBadges) {
            val row = i / nCols
            val col = i % nCols
            val b = if (i < badges.size) badges[i] else null
            val button = BadgeButton(b)
            button.setPos(
                    left + col * badgeWidth + (badgeWidth - button.width()) / 2,
                    top + row * badgeHeight + (badgeHeight - button.height()) / 2)
            PixelScene.align(button)
            add(button)
        }

        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0f)
        add(btnExit)

        fadeIn()

        Badges.loadingListener = Callback {
            if (Game.scene() === this@BadgesScene) {
                ShatteredPixelDungeon.switchNoFade(BadgesScene::class.java)
            }
        }
    }

    override fun destroy() {

        Badges.saveGlobal()
        Badges.loadingListener = null

        super.destroy()
    }

    override fun onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene::class.java)
    }

    private class BadgeButton(private val badge: Badges.Badge?) : Button() {

        private val icon: Image

        init {
            active = badge != null

            icon = if (active) BadgeBanner.image(badge!!.image) else Image(Assets.LOCKED)
            add(icon)

            setSize(icon.width(), icon.height())
        }

        override fun layout() {
            super.layout()

            icon.x = x + (width - icon.width()) / 2
            icon.y = y + (height - icon.height()) / 2
        }

        override fun update() {
            super.update()

            if (Random.Float() < Game.elapsed * 0.1) {
                BadgeBanner.highlight(icon, badge!!.image)
            }
        }

        override fun onClick() {
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
            Game.scene()!!.add(WndBadge(badge!!))
        }
    }
}
