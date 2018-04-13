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

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText

import java.util.ArrayList
import java.util.Arrays
import java.util.Locale

class WndLangs : Window() {

    private val WIDTH_P = 120
    private val WIDTH_L = 171

    private val MIN_HEIGHT = 110

    private val BTN_WIDTH = 50
    private val BTN_HEIGHT = 12

    init {

        val langs = ArrayList(Arrays.asList(*Languages.values()))

        val nativeLang = Languages.matchLocale(Locale.getDefault())
        langs.remove(nativeLang)
        //move the native language to the top.
        langs.add(0, nativeLang)

        val currLang = Messages.lang()

        //language buttons layout
        var y = 0
        for (i in langs.indices) {
            val langIndex = i
            val btn = object : RedButton(Messages.titleCase(langs[i].nativeName())) {
                override fun onClick() {
                    super.onClick()
                    Messages.setup(langs[langIndex])
                    ShatteredPixelDungeon.switchNoFade(TitleScene::class.java, object : Game.SceneChangeCallback {
                        override fun beforeCreate() {
                            SPDSettings.language(langs[langIndex])
                            RenderedText.clearCache()
                        }

                        override fun afterCreate() {
                            Game.scene()!!.add(WndLangs())
                        }
                    })
                }
            }
            if (currLang == langs[i]) {
                btn.textColor(Window.TITLE_COLOR)
            } else {
                when (langs[i].status()) {
                    Languages.Status.INCOMPLETE -> btn.textColor(0x999999)
                    Languages.Status.UNREVIEWED -> btn.textColor(0xCCCCCC)
                }
            }
            btn.setSize(BTN_WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            if (SPDSettings.landscape() && i % 2 == 1) {
                btn.setPos((BTN_WIDTH + 1).toFloat(), (y - (BTN_HEIGHT + 1)).toFloat())
            } else {
                btn.setPos(0f, y.toFloat())
                y += BTN_HEIGHT + 1
            }

            add(btn)
        }
        y = Math.max(MIN_HEIGHT, y)
        resize(if (SPDSettings.landscape()) WIDTH_L else WIDTH_P, y)

        val textLeft = width - 65
        val textWidth = width - textLeft

        val separator = ColorBlock(1f, y.toFloat(), -0x1000000)
        separator.x = textLeft - 2.5f
        add(separator)

        //language info layout.
        val title = PixelScene.renderText(Messages.titleCase(currLang!!.nativeName()), 9)
        title.x = textLeft + (textWidth - title.width()) / 2f
        title.y = 0f
        title.hardlight(Window.TITLE_COLOR)
        PixelScene.align(title)
        add(title)

        if (currLang == Languages.ENGLISH) {

            val info = PixelScene.renderMultiline(6)
            info.text("This is the source language, written by the developer.", width - textLeft)
            info.setPos(textLeft.toFloat(), title.height() + 2)
            add(info)

        } else {

            val info = PixelScene.renderMultiline(6)
            when (currLang.status()) {
                Languages.Status.REVIEWED -> info.text(Messages.get(this, "completed"), width - textLeft)
                Languages.Status.UNREVIEWED -> info.text(Messages.get(this, "unreviewed"), width - textLeft)
                Languages.Status.INCOMPLETE -> info.text(Messages.get(this, "unfinished"), width - textLeft)
            }
            info.setPos(textLeft.toFloat(), title.height() + 2)
            add(info)

            val creditsBtn = object : RedButton(Messages.titleCase(Messages.get(this, "credits"))) {
                override fun onClick() {
                    super.onClick()
                    var creds = ""
                    val reviewers = currLang.reviewers()
                    val translators = currLang.translators()
                    if (reviewers.size > 0) {
                        creds += "_" + Messages.titleCase(Messages.get(WndLangs::class.java, "reviewers")) + "_\n"
                        for (reviewer in reviewers) {
                            creds += "-$reviewer\n"
                        }
                        creds += "\n"
                    }

                    if (reviewers.size > 0 || translators.size > 0) {
                        creds += "_" + Messages.titleCase(Messages.get(WndLangs::class.java, "translators")) + "_"
                        //reviewers are also translators
                        for (reviewer in reviewers) {
                            creds += "\n-$reviewer"
                        }
                        for (translator in translators) {
                            creds += "\n-$translator"
                        }
                    }

                    val credits = Window()

                    val title = PixelScene.renderMultiline(9)
                    title.text(Messages.titleCase(Messages.get(WndLangs::class.java, "credits")), 65)
                    title.hardlight(Window.SHPX_COLOR)
                    title.setPos((65 - title.width()) / 2, 0f)
                    credits.add(title)

                    val text = PixelScene.renderMultiline(6)
                    text.text(creds, 65)
                    text.setPos(0f, title.bottom() + 2)
                    credits.add(text)

                    credits.resize(65, text.bottom().toInt())
                    parent!!.add(credits)
                }
            }
            creditsBtn.setSize(creditsBtn.reqWidth() + 2, 16f)
            creditsBtn.setPos(textLeft + (textWidth - creditsBtn.width()) / 2f, (y - 18).toFloat())
            add(creditsBtn)

            val transifex_text = PixelScene.renderMultiline(6)
            transifex_text.text(Messages.get(this, "transifex"), width - textLeft)
            transifex_text.setPos(textLeft.toFloat(), creditsBtn.top() - 2f - transifex_text.height())
            add(transifex_text)

        }

    }

}
