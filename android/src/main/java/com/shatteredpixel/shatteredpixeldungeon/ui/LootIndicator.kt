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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item

class LootIndicator : Tag(0x1F75CC) {

    private var slot: ItemSlot? = null

    private var lastItem: Item? = null
    private var lastQuantity = 0

    init {

        setSize(24f, 24f)

        visible = false
    }

    override fun createChildren() {
        super.createChildren()

        slot = object : ItemSlot() {
            override fun onClick() {
                if (Dungeon.hero!!.handle(Dungeon.hero!!.pos)) {
                    Dungeon.hero!!.next()
                }

            }
        }
        slot!!.showParams(true, false, false)
        add(slot!!)
    }

    override fun layout() {
        super.layout()

        slot!!.setRect(x + 2, y + 3, width - 2, height - 6)
    }

    override fun update() {

        if (Dungeon.hero!!.ready) {
            val heap = Dungeon.level!!.heaps.get(Dungeon.hero!!.pos)
            if (heap != null) {

                val item = if (heap.type == Heap.Type.CHEST || heap.type == Heap.Type.MIMIC)
                    ItemSlot.CHEST
                else if (heap.type == Heap.Type.LOCKED_CHEST)
                    ItemSlot.LOCKED_CHEST
                else if (heap.type == Heap.Type.CRYSTAL_CHEST)
                    ItemSlot.CRYSTAL_CHEST
                else if (heap.type == Heap.Type.TOMB)
                    ItemSlot.TOMB
                else if (heap.type == Heap.Type.SKELETON)
                    ItemSlot.SKELETON
                else if (heap.type == Heap.Type.REMAINS)
                    ItemSlot.REMAINS
                else
                    heap.peek()
                if (item !== lastItem || item.quantity() != lastQuantity) {
                    lastItem = item
                    lastQuantity = item.quantity()

                    slot!!.item(item)
                    flash()
                }
                visible = true

            } else {

                lastItem = null
                visible = false

            }
        }

        slot!!.enable(visible && Dungeon.hero!!.ready)

        super.update()
    }
}
