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

import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Game
import com.watabou.noosa.TouchArea
import com.watabou.utils.SparseArray

open class WndStory(text: String) : Window(0, 0, Chrome.get(Chrome.Type.SCROLL)!!) {

    private val tf: RenderedTextMultiline

    private var delay: Float = 0.toFloat()

    init {

        tf = PixelScene.renderMultiline(text, 6)
        tf.maxWidth(if (SPDSettings.landscape())
            WIDTH_L - MARGIN * 2
        else
            WIDTH_P - MARGIN * 2)
        tf.invert()
        tf.setPos(MARGIN.toFloat(), 0f)
        add(tf)

        add(object : TouchArea(chrome) {
            override fun onClick(touch: Touch) {
                hide()
            }
        })

        resize((tf.width() + MARGIN * 2).toInt(), Math.min(tf.height(), 180f).toInt())
    }

    override fun update() {
        super.update()

        if (delay > 0) {
            delay -= Game.elapsed
            if (delay <= 0) {
                tf.visible = true
                chrome.visible = tf.visible
                shadow.visible = chrome.visible
            }
        }
    }

    companion object {

        private val WIDTH_P = 125
        private val WIDTH_L = 160
        private val MARGIN = 2

        private val bgR = 0.77f
        private val bgG = 0.73f
        private val bgB = 0.62f

        val ID_SEWERS = 0
        val ID_PRISON = 1
        val ID_CAVES = 2
        val ID_CITY = 3
        val ID_HALLS = 4

        private val CHAPTERS = SparseArray<String>()

        init {
            CHAPTERS.put(ID_SEWERS, "sewers")
            CHAPTERS.put(ID_PRISON, "prison")
            CHAPTERS.put(ID_CAVES, "caves")
            CHAPTERS.put(ID_CITY, "city")
            CHAPTERS.put(ID_HALLS, "halls")
        }

        fun showChapter(id: Int) {

            if (Dungeon.chapters.contains(id)) {
                return
            }

            val text = Messages.get(WndStory::class.java, CHAPTERS.get(id))
            if (text != null) {
                val wnd = WndStory(text)
                wnd.delay = 0.6f
                if (wnd.delay > 0) {
                    wnd.tf.visible = false
                    wnd.chrome.visible = wnd.tf.visible
                    wnd.shadow.visible = wnd.chrome.visible
                }

                Game.scene()!!.add(wnd)

                Dungeon.chapters.add(id)
            }
        }
    }
}
