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

import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component

class IconTitle : Component {

    protected var imIcon: Image
    protected var tfLabel: RenderedTextMultiline
    protected var health: HealthBar

    private var healthLvl = java.lang.Float.NaN

    constructor() : super() {}

    constructor(item: Item) {
        val icon = ItemSprite()
        icon(icon)
        label(Messages.titleCase(item.toString()))
        icon.view(item)
    }

    constructor(icon: Image, label: String) : super() {

        icon(icon)
        label(label)
    }

    override fun createChildren() {
        imIcon = Image()
        add(imIcon)

        tfLabel = PixelScene.renderMultiline(FONT_SIZE.toInt())
        tfLabel.hardlight(Window.TITLE_COLOR)
        add(tfLabel)

        health = HealthBar()
        add(health)
    }

    override fun layout() {

        health.visible = !java.lang.Float.isNaN(healthLvl)

        imIcon.x = x + Math.max(0f, 8 - imIcon.width() / 2)
        imIcon.y = y + Math.max(0f, 8 - imIcon.height() / 2)
        PixelScene.align(imIcon)

        val imWidth = Math.max(imIcon.width(), 16f).toInt()
        val imHeight = Math.max(imIcon.height(), 16f).toInt()

        tfLabel.maxWidth((width - (imWidth + GAP)).toInt())
        tfLabel.setPos(x + imWidth.toFloat() + GAP, if (imHeight > tfLabel.height())
            y + (imHeight - tfLabel.height()) / 2
        else
            y)
        PixelScene.align(tfLabel)

        if (health.visible) {
            health.setRect(tfLabel.left(), tfLabel.bottom(), tfLabel.maxWidth().toFloat(), 0f)
            height = Math.max(imHeight.toFloat(), health.bottom())
        } else {
            height = Math.max(imHeight.toFloat(), tfLabel.height())
        }
    }

    fun icon(icon: Image) {
        remove(imIcon)
        add(imIcon = icon)
    }

    fun label(label: String) {
        tfLabel.text(label)
    }

    fun label(label: String, color: Int) {
        tfLabel.text(label)
        tfLabel.hardlight(color)
    }

    fun color(color: Int) {
        tfLabel.hardlight(color)
    }

    fun health(value: Float) {
        health.level(healthLvl = value)
        layout()
    }

    companion object {

        private val FONT_SIZE = 9f

        private val GAP = 2f
    }
}
