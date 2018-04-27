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
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window

class WndInfoItem : Window {

    constructor(heap: Heap) : super() {

        if (heap.type == Heap.Type.HEAP || heap.type == Heap.Type.FOR_SALE) {

            val item = heap.peek()

            var color = Window.TITLE_COLOR
            if (item.levelKnown && item.level() > 0) {
                color = ItemSlot.UPGRADED
            } else if (item.levelKnown && item.level() < 0) {
                color = ItemSlot.DEGRADED
            }
            fillFields(item.image(), item.glowing(), color, item.toString(), item.info())

        } else {

            fillFields(heap.image(), heap.glowing(), Window.TITLE_COLOR, heap.toString(), heap.info())

        }
    }

    constructor(item: Item) : super() {

        var color = Window.TITLE_COLOR
        if (item.levelKnown && item.level() > 0) {
            color = ItemSlot.UPGRADED
        } else if (item.levelKnown && item.level() < 0) {
            color = ItemSlot.DEGRADED
        }

        fillFields(item.image(), item.glowing(), color, item.toString(), item.info())
    }

    private fun fillFields(image: Int, glowing: ItemSprite.Glowing?, titleColor: Int, title: String, info: String) {

        val width = if (SPDSettings.landscape()) WIDTH_L else WIDTH_P

        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(image, glowing))
        titlebar.label(Messages.titleCase(title), titleColor)
        titlebar.setRect(0f, 0f, width.toFloat(), 0f)
        add(titlebar)

        val txtInfo = PixelScene.renderMultiline(info, 6)
        txtInfo.maxWidth(width)
        txtInfo.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(txtInfo)

        resize(width, (txtInfo.top() + txtInfo.height()).toInt())
    }

    companion object {

        private val GAP = 2f

        private val WIDTH_P = 120
        private val WIDTH_L = 144
    }
}
