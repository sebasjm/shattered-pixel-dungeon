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
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window

open class WndOptions(title: String, message: String, vararg options: String) : Window() {

    init {

        val width = if (SPDSettings.landscape()) WIDTH_L else WIDTH_P

        val tfTitle = PixelScene.renderMultiline(title, 9)
        tfTitle.hardlight(Window.TITLE_COLOR)
        tfTitle.setPos(MARGIN.toFloat(), MARGIN.toFloat())
        tfTitle.maxWidth(width - MARGIN * 2)
        add(tfTitle)

        val tfMesage = PixelScene.renderMultiline(6)
        tfMesage.text(message, width - MARGIN * 2)
        tfMesage.setPos(MARGIN.toFloat(), tfTitle.top() + tfTitle.height() + MARGIN.toFloat())
        add(tfMesage)

        var pos = tfMesage.bottom() + MARGIN

        for (i in options.indices) {
            val index = i
            val btn = object : RedButton(options[i]) {
                override fun onClick() {
                    hide()
                    onSelect(index)
                }
            }
            btn.setRect(MARGIN.toFloat(), pos, (width - MARGIN * 2).toFloat(), BUTTON_HEIGHT.toFloat())
            add(btn)

            pos += (BUTTON_HEIGHT + MARGIN).toFloat()
        }

        resize(width, pos.toInt())
    }

    protected open fun onSelect(index: Int) {}

    companion object {

        private val WIDTH_P = 120
        private val WIDTH_L = 144

        private val MARGIN = 2
        private val BUTTON_HEIGHT = 20
    }
}
