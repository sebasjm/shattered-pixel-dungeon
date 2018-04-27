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
import com.watabou.noosa.Game
import com.watabou.noosa.NinePatch
import com.watabou.noosa.ui.Button

open class Tag(color: Int) : Button() {

    private val r: Float
    private val g: Float
    private val b: Float
    protected var bg: NinePatch? = null

    protected var lightness = 0f

    init {

        this.r = (color shr 16) / 255f
        this.g = (color shr 8 and 0xFF) / 255f
        this.b = (color and 0xFF) / 255f
    }

    override fun createChildren() {

        super.createChildren()

        bg = Chrome.get(Chrome.Type.TAG)
        bg!!.hardlight(r, g, b)
        add(bg!!)
    }

    override fun layout() {

        super.layout()

        bg!!.x = x
        bg!!.y = y
        bg!!.size(width, height)
    }

    fun flash() {
        lightness = 1f
    }

    fun flip(value: Boolean) {
        bg!!.flipHorizontal(value)
    }

    override fun update() {
        super.update()

        if (visible && lightness > 0.5) {
            lightness -= Game.elapsed
            if (lightness > 0.5) {
                bg!!.ba = 2 * lightness - 1
                bg!!.ga = bg!!.ba
                bg!!.ra = bg!!.ga
                bg!!.rm = 2f * r * (1 - lightness)
                bg!!.gm = 2f * g * (1 - lightness)
                bg!!.bm = 2f * b * (1 - lightness)
            } else {
                bg!!.hardlight(r, g, b)
            }
        }
    }
}
