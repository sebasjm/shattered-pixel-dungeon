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
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText
import com.watabou.utils.FileUtils

import java.util.Locale

class WndGameInProgress(slot: Int) : Window() {

    private var GAP = 5

    private var pos: Float = 0.toFloat()

    init {

        val info = GamesInProgress.check(slot)

        var className: String? = null
        if (info!!.subClass != HeroSubClass.NONE) {
            className = info.subClass!!.title()
        } else {
            className = info.heroClass!!.title()
        }

        val title = IconTitle()
        title.icon(HeroSprite.avatar(info.heroClass!!, info.armorTier))
        title.label(Messages.get(this.javaClass, "title", info.level, className).toUpperCase(Locale.ENGLISH))
        title.color(Window.SHPX_COLOR)
        title.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(title)

        if (info.challenges > 0) GAP -= 2

        pos = title.bottom() + GAP

        if (info.challenges > 0) {
            val btnChallenges = object : RedButton(Messages.get(this@WndGameInProgress.javaClass, "challenges")) {
                override fun onClick() {
                    Game.scene()!!.add(WndChallenges(info.challenges, false))
                }
            }
            val btnW = btnChallenges.reqWidth() + 2
            btnChallenges.setRect((WIDTH - btnW) / 2, pos, btnW, btnChallenges.reqHeight() + 2)
            add(btnChallenges)

            pos = btnChallenges.bottom() + GAP
        }

        pos += GAP.toFloat()

        statSlot(Messages.get(this.javaClass, "str"), info.str)
        if (info.shld > 0)
            statSlot(Messages.get(this.javaClass, "health"), info.hp.toString() + "+" + info.shld + "/" + info.ht)
        else
            statSlot(Messages.get(this.javaClass, "health"), info.hp.toString() + "/" + info.ht)
        statSlot(Messages.get(this.javaClass, "exp"), info.exp.toString() + "/" + Hero.maxExp(info.level))

        pos += GAP.toFloat()
        statSlot(Messages.get(this.javaClass, "gold"), info.goldCollected)
        statSlot(Messages.get(this.javaClass, "depth"), info.maxDepth)

        pos += GAP.toFloat()

        val cont = object : RedButton(Messages.get(this@WndGameInProgress.javaClass, "continue")) {
            override fun onClick() {
                super.onClick()

                GamesInProgress.curSlot = slot

                Dungeon.hero = null
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE
                Game.switchScene(InterlevelScene::class.java)
            }
        }

        val erase = object : RedButton(Messages.get(this@WndGameInProgress.javaClass, "erase")) {
            override fun onClick() {
                super.onClick()

                Game.scene()!!.add(object : WndOptions(
                        Messages.get(WndGameInProgress::class.java, "erase_warn_title"),
                        Messages.get(WndGameInProgress::class.java, "erase_warn_body"),
                        Messages.get(WndGameInProgress::class.java, "erase_warn_yes"),
                        Messages.get(WndGameInProgress::class.java, "erase_warn_no")) {
                    override fun onSelect(index: Int) {
                        if (index == 0) {
                            FileUtils.deleteDir(GamesInProgress.gameFolder(slot))
                            GamesInProgress.setUnknown(slot)
                            ShatteredPixelDungeon.switchNoFade(StartScene::class.java)
                        }
                    }
                })
            }
        }

        cont.setRect(0f, (HEIGHT - 20).toFloat(), (WIDTH / 2 - 1).toFloat(), 20f)
        add(cont)

        erase.setRect((WIDTH / 2 + 1).toFloat(), (HEIGHT - 20).toFloat(), (WIDTH / 2 - 1).toFloat(), 20f)
        add(erase)

        resize(WIDTH, HEIGHT)
    }

    private fun statSlot(label: String, value: String) {

        var txt = PixelScene.renderText(label, 8)
        txt.y = pos
        add(txt)

        txt = PixelScene.renderText(value, 8)
        txt.x = WIDTH * 0.6f
        txt.y = pos
        PixelScene.align(txt)
        add(txt)

        pos += GAP + txt.baseLine()
    }

    private fun statSlot(label: String, value: Int) {
        statSlot(label, Integer.toString(value))
    }

    companion object {

        private val WIDTH = 120
        private val HEIGHT = 120
    }
}
