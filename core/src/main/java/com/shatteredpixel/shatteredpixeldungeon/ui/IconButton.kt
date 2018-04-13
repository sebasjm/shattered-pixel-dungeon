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
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button

open class IconButton : Button {

    protected var icon: Image? = null

    constructor() : super() {}

    constructor(icon: Image) : super() {
        icon(icon)
    }

    override fun layout() {
        super.layout()

        if (icon != null) {
            icon!!.x = x + (width - icon!!.width()) / 2f
            icon!!.y = y + (height - icon!!.height()) / 2f
            PixelScene.align(icon!!)
        }
    }

    override fun onTouchDown() {
        if (icon != null) icon!!.brightness(1.5f)
        Sample.INSTANCE.play(Assets.SND_CLICK)
    }

    override fun onTouchUp() {
        if (icon != null) icon!!.resetColor()
    }

    fun enable(value: Boolean) {
        active = value
        if (icon != null) icon!!.alpha(if (value) 1.0f else 0.3f)
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
}
