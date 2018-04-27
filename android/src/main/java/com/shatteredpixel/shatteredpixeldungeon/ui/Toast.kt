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

import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.noosa.NinePatch
import com.watabou.noosa.ui.Component

open class Toast(text: String) : Component() {

    protected var bg: NinePatch? = null
    protected var close: SimpleButton? = null
    protected var text: RenderedTextMultiline? = null

    init {
        text(text)

        width = this.text!!.width() + close!!.width() + bg!!.marginHor().toFloat() + MARGIN_HOR * 3
        height = Math.max(this.text!!.height(), close!!.height()) + bg!!.marginVer().toFloat() + MARGIN_VER * 2
    }

    override fun createChildren() {
        super.createChildren()

        bg = Chrome.get(Chrome.Type.TOAST_TR)
        add(bg!!)

        close = object : SimpleButton(Icons.get(Icons.CLOSE)) {
            override fun onClick() {
                onClose()
            }
        }
        add(close!!)

        text = PixelScene.renderMultiline(8)
        add(text!!)
    }

    override fun layout() {
        super.layout()

        bg!!.x = x
        bg!!.y = y
        bg!!.size(width, height)

        close!!.setPos(
                bg!!.x + bg!!.width() - bg!!.marginHor() / 2f - MARGIN_HOR - close!!.width(),
                y + (height - close!!.height()) / 2f)
        PixelScene.align(close!!)

        text!!.setPos(close!!.left() - MARGIN_HOR - text!!.width(), y + (height - text!!.height()) / 2)
        PixelScene.align(text!!)
    }

    fun text(txt: String) {
        text!!.text(txt)
    }

    protected open fun onClose() {}

    companion object {

        private val MARGIN_HOR = 2f
        private val MARGIN_VER = 2f
    }
}
