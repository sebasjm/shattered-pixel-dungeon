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

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.RenderedText

class WndList(items: Array<String>) : Window() {

    init {

        var pos = MARGIN.toFloat()
        var dotWidth = 0f
        var maxWidth = 0f

        for (i in items.indices) {

            if (i > 0) {
                pos += GAP.toFloat()
            }

            val dot = PixelScene.renderText("-", 6)
            dot.x = MARGIN.toFloat()
            dot.y = pos
            if (dotWidth == 0f) {
                dotWidth = dot.width()
            }
            add(dot)

            val item = PixelScene.createMultiline(items[i], 6f)
            item.x = dot.x + dotWidth
            item.y = pos
            item.maxWidth = (WIDTH.toFloat() - (MARGIN * 2).toFloat() - dotWidth).toInt()
            item.measure()
            add(item)

            pos += item.height()
            val w = item.width()
            if (w > maxWidth) {
                maxWidth = w
            }
        }

        resize((maxWidth + dotWidth + (MARGIN * 2).toFloat()).toInt(), (pos + MARGIN).toInt())
    }

    companion object {

        private val WIDTH = 120
        private val MARGIN = 4
        private val GAP = 4
    }
}
