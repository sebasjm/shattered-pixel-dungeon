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
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window

class WndMessage(text: String) : Window() {

    init {

        val info = PixelScene.renderMultiline(text, 6)
        info.maxWidth((if (SPDSettings.landscape()) WIDTH_L else WIDTH_P) - MARGIN * 2)
        info.setPos(MARGIN.toFloat(), MARGIN.toFloat())
        add(info)

        resize(
                info.width().toInt() + MARGIN * 2,
                info.height().toInt() + MARGIN * 2)
    }

    companion object {

        private val WIDTH_P = 120
        private val WIDTH_L = 144
        private val MARGIN = 4
    }
}
