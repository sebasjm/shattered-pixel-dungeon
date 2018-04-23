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
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component

open class WndTitledMessage(titlebar: Component, message: String) : Window() {

    constructor(icon: Image, title: String, message: String) : this(IconTitle(icon, title), message) {

    }

    init {

        val width = if (SPDSettings.landscape()) WIDTH_L else WIDTH_P

        titlebar.setRect(0f, 0f, width.toFloat(), 0f)
        add(titlebar)

        val text = PixelScene.renderMultiline(6)
        text.text(message, width)
        text.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(text)

        resize(width, text.bottom().toInt())
    }

    companion object {

        @JvmStatic protected val WIDTH_P = 120
        @JvmStatic protected val WIDTH_L = 160
        @JvmStatic protected val GAP = 2
    }
}
