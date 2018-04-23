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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.ui.Component

open class HealthBar : Component() {

    private var Bg: ColorBlock? = null
    private var Shld: ColorBlock? = null
    private var Hp: ColorBlock? = null

    private var health: Float = 0.toFloat()
    private var shield: Float = 0.toFloat()

    override fun createChildren() {
        Bg = ColorBlock(1f, 1f, COLOR_BG)
        add(Bg!!)

        Shld = ColorBlock(1f, 1f, COLOR_SHLD)
        add(Shld!!)

        Hp = ColorBlock(1f, 1f, COLOR_HP)
        add(Hp!!)

        height = HEIGHT.toFloat()
    }

    override fun layout() {

        Hp!!.x = x
        Shld!!.x = Hp!!.x
        Bg!!.x = Shld!!.x
        Hp!!.y = y
        Shld!!.y = Hp!!.y
        Bg!!.y = Shld!!.y

        Bg!!.size(width, height)

        //logic here rounds up to the nearest pixel
        var pixelWidth = width
        if (camera() != null) pixelWidth *= camera()!!.zoom
        Shld!!.size(width * Math.ceil((shield * pixelWidth).toDouble()).toFloat() / pixelWidth, height)
        Hp!!.size(width * Math.ceil((health * pixelWidth).toDouble()).toFloat() / pixelWidth, height)
    }

    fun level(value: Float) {
        level(value, 0f)
    }

    fun level(health: Float, shield: Float) {
        this.health = health
        this.shield = shield
        layout()
    }

    fun level(c: Char) {
        val health = c.HP.toFloat()
        val shield = c.SHLD.toFloat()
        val max = Math.max(health + shield, c.HT.toFloat())

        level(health / max, (health + shield) / max)
    }

    companion object {

        private val COLOR_BG = -0x340000
        private val COLOR_HP = -0xff1200
        private val COLOR_SHLD = -0x441145

        private val HEIGHT = 2
    }
}
