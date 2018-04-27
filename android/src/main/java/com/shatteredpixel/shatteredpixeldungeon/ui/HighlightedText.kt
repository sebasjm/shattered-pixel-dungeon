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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.ui.Component
import com.watabou.utils.Highlighter

class HighlightedText(size: Float) : Component() {

    protected var normal: BitmapTextMultiline
    protected var highlighted: BitmapTextMultiline

    protected var nColor = 0xFFFFFF
    protected var hColor = 0xFFFF44

    init {
        normal = PixelScene.createMultiline(size)
        add(normal)

        highlighted = PixelScene.createMultiline(size)
        add(highlighted)

        setColor(0xFFFFFF, 0xFFFF44)
    }

    override fun layout() {
        highlighted.x = x
        normal.x = highlighted.x
        highlighted.y = y
        normal.y = highlighted.y
    }

    fun text(value: String, maxWidth: Int) {
        val hl = Highlighter(value)

        normal.text(hl.text)
        normal.maxWidth = maxWidth
        normal.measure()

        if (hl.isHighlighted) {
            normal.mask = hl.inverted()

            highlighted.text(hl.text)
            highlighted.maxWidth = maxWidth
            highlighted.measure()

            highlighted.mask = hl.mask
            highlighted.visible = true
        } else {
            highlighted.visible = false
        }

        width = normal.width()
        height = normal.height()
    }

    fun setColor(n: Int, h: Int) {
        normal.hardlight(n)
        highlighted.hardlight(h)
    }
}