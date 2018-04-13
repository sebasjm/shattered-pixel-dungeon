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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Game
import com.watabou.noosa.NinePatch
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button

import java.util.ArrayList

open class WndTabbed : Window(0, 0, Chrome.get(Chrome.Type.TAB_SET)) {

    protected var tabs: ArrayList<Tab> = ArrayList()
    protected var selected: Tab

    protected fun add(tab: Tab): Tab {

        tab.setPos(if (tabs.size == 0)
            -chrome.marginLeft() + 1
        else
            tabs[tabs.size - 1].right(), height.toFloat())
        tab.select(false)
        super.add(tab)

        tabs.add(tab)

        return tab
    }

    fun select(index: Int) {
        select(tabs[index])
    }

    fun select(tab: Tab) {
        if (tab !== selected) {
            for (t in tabs) {
                if (t === selected) {
                    t.select(false)
                } else if (t === tab) {
                    t.select(true)
                }
            }

            selected = tab
        }
    }

    override fun resize(w: Int, h: Int) {
        // -> super.resize(...)
        this.width = w
        this.height = h

        chrome.size(
                (width + chrome.marginHor()).toFloat(),
                (height + chrome.marginVer()).toFloat())

        camera!!.resize(chrome.width.toInt(), chrome.marginTop() + height + tabHeight())
        camera!!.x = (Game.width - camera!!.screenWidth()).toInt() / 2
        camera!!.y = (Game.height - camera!!.screenHeight()).toInt() / 2
        camera!!.y += (yOffset * camera!!.zoom).toInt()

        shadow.boxRect(
                camera!!.x / camera!!.zoom,
                camera!!.y / camera!!.zoom,
                chrome.width(), chrome.height)
        // <- super.resize(...)

        for (tab in tabs) {
            remove(tab)
        }

        val tabs = ArrayList(this.tabs)
        this.tabs.clear()

        for (tab in tabs) {
            add(tab)
        }
    }

    fun layoutTabs() {
        //subract two as there's extra horizontal space for those nobs on the top.
        var fullWidth = width + chrome.marginHor() - 2
        val numTabs = tabs.size

        if (numTabs == 0)
            return
        if (numTabs == 1) {
            tabs[0].setSize(fullWidth.toFloat(), tabHeight().toFloat())
            return
        }

        val spaces = numTabs - 1
        var spacing = -1

        while (spacing == -1) {
            for (i in 0..3) {
                if ((fullWidth - i * spaces) % numTabs == 0) {
                    spacing = i
                    break
                }
            }
            if (spacing == -1) fullWidth--
        }

        val tabWidth = (fullWidth - spacing * (numTabs - 1)) / numTabs

        for (i in tabs.indices) {
            tabs[i].setSize(tabWidth.toFloat(), tabHeight().toFloat())
            tabs[i].setPos(if (i == 0)
                -chrome.marginLeft() + 1
            else
                tabs[i - 1].right() + spacing, height.toFloat())
        }

    }

    protected open fun tabHeight(): Int {
        return 25
    }

    protected open fun onClick(tab: Tab) {
        select(tab)
    }

    protected open inner class Tab : Button() {

        protected val CUT = 5

        protected var selected: Boolean = false

        protected var bg: NinePatch? = null

        override fun layout() {
            super.layout()

            if (bg != null) {
                bg!!.x = x
                bg!!.y = y
                bg!!.size(width, height)
            }
        }

        open fun select(value: Boolean) {

            active = !(selected = value)

            if (bg != null) {
                remove(bg)
            }

            bg = Chrome.get(if (selected)
                Chrome.Type.TAB_SELECTED
            else
                Chrome.Type.TAB_UNSELECTED)
            addToBack(bg)

            layout()
        }

        override fun onClick() {
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
            this@WndTabbed.onClick(this)
        }
    }

    protected open inner class LabeledTab(label: String) : Tab() {

        private var btLabel: RenderedText? = null

        init {

            btLabel!!.text(label)
        }

        override fun createChildren() {
            super.createChildren()

            btLabel = PixelScene.renderText(9)
            add(btLabel)
        }

        override fun layout() {
            super.layout()

            btLabel!!.x = x + (width - btLabel!!.width()) / 2
            btLabel!!.y = y + (height - btLabel!!.baseLine()) / 2 - 1
            if (!selected) {
                btLabel!!.y -= 2f
            }
            PixelScene.align(btLabel!!)
        }

        protected override fun select(value: Boolean) {
            super.select(value)
            btLabel!!.am = if (selected) 1.0f else 0.6f
        }
    }

}
