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
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

class WndItem @JvmOverloads constructor(owner: WndBag?, item: Item, options: Boolean = owner != null) : Window() {

    init {

        var width = WIDTH_MIN

        val info = PixelScene.renderMultiline(item.info(), 6)
        info.maxWidth(width)

        //info box can go out of the screen on landscape, so widen it
        while (SPDSettings.landscape()
                && info.height() > 100
                && width < WIDTH_MAX) {
            width += 20
            info.maxWidth(width)
        }

        val titlebar = IconTitle(item)
        titlebar.setRect(0f, 0f, width.toFloat(), 0f)
        add(titlebar)

        if (item.levelKnown && item.level() > 0) {
            titlebar.color(ItemSlot.UPGRADED)
        } else if (item.levelKnown && item.level() < 0) {
            titlebar.color(ItemSlot.DEGRADED)
        }

        info.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(info)

        var y = info.top() + info.height() + GAP
        var x = 0f

        if (Dungeon.hero!!.isAlive && options) {
            var line = ArrayList<RedButton>()
            for (action in item.actions(Dungeon.hero!!)) {

                val btn = object : RedButton(Messages.get(item.javaClass, "ac_$action"), 8) {
                    override fun onClick() {
                        hide()
                        if (owner != null && owner.parent != null) owner.hide()
                        item.execute(Dungeon.hero!!, action)
                    }
                }
                btn.setSize(btn.reqWidth(), BUTTON_HEIGHT)
                if (x + btn.width() > width || line.size == 3) {
                    layoutButtons(line, width - x, y)
                    x = 0f
                    y += BUTTON_HEIGHT + 1
                    line = ArrayList()
                }
                x++
                add(btn)
                line.add(btn)

                if (action == item.defaultAction) {
                    btn.textColor(Window.TITLE_COLOR)
                }

                x += btn.width()
            }
            layoutButtons(line, width - x, y)
        }

        resize(width, (y + if (x > 0) BUTTON_HEIGHT else 0f).toInt())
    }

    companion object {

        private val BUTTON_HEIGHT = 16f

        private val GAP = 2f

        private val WIDTH_MIN = 120
        private val WIDTH_MAX = 220

        //this method assumes a max of 3 buttons per line
        //FIXME: this is really messy for just trying to make buttons fill the window. Gotta be a cleaner way.
        private fun layoutButtons(line: ArrayList<RedButton>?, extraWidth: Float, y: Float) {
            var extraWidth = extraWidth
            if (line == null || line.size == 0 || extraWidth == 0f) return
            if (line.size == 1) {
                line[0].setSize(line[0].width() + extraWidth, BUTTON_HEIGHT)
                line[0].setPos(0f, y)
                return
            }
            val lineByWidths = ArrayList(line)
            Collections.sort(lineByWidths, widthComparator)
            val smallest: RedButton
            val middle: RedButton
            var largest: RedButton?
            smallest = lineByWidths[0]
            middle = lineByWidths[1]
            largest = null
            if (lineByWidths.size == 3) {
                largest = lineByWidths[2]
            }

            var btnDiff = middle.width() - smallest.width()
            smallest.setSize(smallest.width() + Math.min(btnDiff, extraWidth), BUTTON_HEIGHT)
            extraWidth -= btnDiff
            if (extraWidth > 0) {
                if (largest == null) {
                    smallest.setSize(smallest.width() + extraWidth / 2, BUTTON_HEIGHT)
                    middle.setSize(middle.width() + extraWidth / 2, BUTTON_HEIGHT)
                } else {
                    btnDiff = largest.width() - smallest.width()
                    smallest.setSize(smallest.width() + Math.min(btnDiff, extraWidth / 2), BUTTON_HEIGHT)
                    middle.setSize(middle.width() + Math.min(btnDiff, extraWidth / 2), BUTTON_HEIGHT)
                    extraWidth -= btnDiff * 2
                    if (extraWidth > 0) {
                        smallest.setSize(smallest.width() + extraWidth / 3, BUTTON_HEIGHT)
                        middle.setSize(middle.width() + extraWidth / 3, BUTTON_HEIGHT)
                        largest.setSize(largest.width() + extraWidth / 3, BUTTON_HEIGHT)
                    }
                }
            }

            var x = 0f
            for (btn in line) {
                btn.setPos(x, y)
                x += btn.width() + 1
            }
        }

        private val widthComparator = Comparator<RedButton> { lhs, rhs ->
            if (lhs.width() < rhs.width()) {
                -1
            } else if (lhs.width() == rhs.width()) {
                0
            } else {
                1
            }
        }
    }
}
