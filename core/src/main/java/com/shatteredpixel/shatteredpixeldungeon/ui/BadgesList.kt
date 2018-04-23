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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBadge
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Component

import java.util.ArrayList

class BadgesList(global: Boolean) : ScrollPane(Component()) {

    private val items = ArrayList<ListItem>()

    init {

        for (badge in Badges.filtered(global)) {

            if (badge.image == -1) {
                continue
            }

            val item = ListItem(badge)
            content.add(item)
            items.add(item)
        }
    }

    override fun layout() {

        var pos = 0f

        val size = items.size
        for (i in 0 until size) {
            items[i].setRect(0f, pos, width, HEIGHT)
            pos += HEIGHT
        }

        content.setSize(width, pos)

        super.layout()
    }

    override fun onClick(x: Float, y: Float) {
        val size = items.size
        for (i in 0 until size) {
            if (items[i].onClick(x, y)) {
                break
            }
        }
    }

    private inner class ListItem(private val badge: Badges.Badge) : Component() {

        private var icon: Image? = null
        private var label: RenderedText? = null

        init {
            icon!!.copy(BadgeBanner.image(badge.image))
            label!!.text(badge.desc())
        }

        override fun createChildren() {
            icon = Image()
            add(icon!!)

            label = PixelScene.renderText(6)
            add(label!!)
        }

        override fun layout() {
            icon!!.x = x
            icon!!.y = y + (height - icon!!.height) / 2
            PixelScene.align(icon!!)

            label!!.x = icon!!.x + icon!!.width + 2f
            label!!.y = y + (height - label!!.baseLine()) / 2
            PixelScene.align(label!!)
        }

        fun onClick(x: Float, y: Float): Boolean {
            if (inside(x, y)) {
                Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
                Game.scene()!!.add(WndBadge(badge))
                return true
            } else {
                return false
            }
        }

    }
        companion object {

            private val HEIGHT = 20f
        }
}
