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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.noosa.Image
import com.watabou.noosa.NinePatch
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button

open class RedButton @JvmOverloads constructor(label: String, size: Int = 9) : Button() {

    protected var bg: NinePatch? = null
    protected var text: RenderedText? = null
    protected var icon: Image? = null

    init {

        text = PixelScene.renderText(size)
        text!!.text(label)
        add(text)
    }

    override fun createChildren() {
        super.createChildren()

        bg = Chrome.get(Chrome.Type.BUTTON)
        add(bg)
    }

    override fun layout() {

        super.layout()

        bg!!.x = x
        bg!!.y = y
        bg!!.size(width, height)

        var componentWidth = 0f

        if (icon != null) componentWidth += icon!!.width() + 2

        if (text != null && text!!.text() != "") {
            componentWidth += text!!.width() + 2

            text!!.x = x + (width() - componentWidth) / 2f + 1f
            text!!.y = y + (height() - text!!.baseLine()) / 2f
            PixelScene.align(text!!)

        }

        if (icon != null) {

            icon!!.x = x + (width() + componentWidth) / 2f - icon!!.width() - 1f
            icon!!.y = y + (height() - icon!!.height()) / 2f
            PixelScene.align(icon!!)
        }

    }

    override fun onTouchDown() {
        bg!!.brightness(1.2f)
        Sample.INSTANCE.play(Assets.SND_CLICK)
    }

    override fun onTouchUp() {
        bg!!.resetColor()
    }

    open fun enable(value: Boolean) {
        active = value
        text!!.alpha(if (value) 1.0f else 0.3f)
    }

    fun text(value: String) {
        text!!.text(value)
        layout()
    }

    fun textColor(value: Int) {
        text!!.hardlight(value)
    }

    fun icon(icon: Image) {
        if (this.icon != null) {
            remove(this.icon)
        }
        this.icon = icon
        if (this.icon != null) {
            add(this.icon)
            layout()
        }
    }

    fun icon(): Image? {
        return icon
    }

    fun reqWidth(): Float {
        var reqWidth = 0f
        if (icon != null) {
            reqWidth += icon!!.width() + 2
        }
        if (text != null && text!!.text() != "") {
            reqWidth += text!!.width() + 2
        }
        return reqWidth
    }

    fun reqHeight(): Float {
        var reqHeight = 0f
        if (icon != null) {
            reqHeight = Math.max(icon!!.height() + 4, reqHeight)
        }
        if (text != null && text!!.text() != "") {
            reqHeight = Math.max(text!!.baseLine() + 4, reqHeight)
        }
        return reqHeight
    }
}
