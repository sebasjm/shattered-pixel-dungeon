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

package com.shatteredpixel.shatteredpixeldungeon

import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class QuickSlot {
    private var slots = arrayOfNulls<Item>(SIZE)

    private val PLACEHOLDERS = "placeholders"
    private val PLACEMENTS = "placements"


    //direct array interaction methods, everything should build from these methods.
    fun setSlot(slot: Int, item: Item) {
        clearItem(item) //we don't want to allow the same item in multiple slots.
        slots[slot] = item
    }

    fun clearSlot(slot: Int) {
        slots[slot] = null
    }

    fun reset() {
        slots = arrayOfNulls(SIZE)
    }

    fun getItem(slot: Int): Item? {
        return slots[slot]
    }


    //utility methods, for easier use of the internal array.
    fun getSlot(item: Item): Int {
        for (i in 0 until SIZE)
            if (getItem(i) === item)
                return i
        return -1
    }

    fun isPlaceholder(slot: Int): Boolean? {
        return getItem(slot) != null && getItem(slot)!!.quantity() == 0
    }

    fun isNonePlaceholder(slot: Int): Boolean? {
        return getItem(slot) != null && getItem(slot)!!.quantity() > 0
    }

    fun clearItem(item: Item) {
        if (contains(item))
            clearSlot(getSlot(item))
    }

    operator fun contains(item: Item): Boolean {
        return getSlot(item) != -1
    }

    fun replacePlaceholder(item: Item) {
        for (i in 0 until SIZE)
            if (isPlaceholder(i)!! && item.isSimilar(getItem(i)))
                setSlot(i, item)
    }

    fun convertToPlaceholder(item: Item) {
        val placeholder = Item.virtual(item.javaClass)

        if (placeholder != null && contains(item))
            for (i in 0 until SIZE)
                if (getItem(i) === item)
                    setSlot(i, placeholder)
    }

    fun randomNonePlaceholder(): Item? {

        val result = ArrayList<Item>()
        for (i in 0 until SIZE)
            if (getItem(i) != null && (!isPlaceholder(i))!!)
                result.add(getItem(i))

        return Random.element(result)
    }

    /**
     * Placements array is used as order is preserved while bundling, but exact index is not, so if we
     * bundle both the placeholders (which preserves their order) and an array telling us where the placeholders are,
     * we can reconstruct them perfectly.
     */

    fun storePlaceholders(bundle: Bundle) {
        val placeholders = ArrayList<Item>(SIZE)
        val placements = BooleanArray(SIZE)

        for (i in 0 until SIZE)
            if (isPlaceholder(i)!!) {
                placeholders.add(getItem(i))
                placements[i] = true
            }
        bundle.put(PLACEHOLDERS, placeholders)
        bundle.put(PLACEMENTS, placements)
    }

    fun restorePlaceholders(bundle: Bundle) {
        val placeholders = bundle.getCollection(PLACEHOLDERS)
        val placements = bundle.getBooleanArray(PLACEMENTS)

        var i = 0
        for (item in placeholders) {
            while (!placements!![i]) i++
            setSlot(i, item as Item)
            i++
        }

    }

    companion object {

        /**
         * Slots contain objects which are also in a player's inventory. The one exception to this is when quantity is 0,
         * which can happen for a stackable item that has been 'used up', these are refered to a placeholders.
         */

        //note that the current max size is coded at 4, due to UI constraints, but it could be much much bigger with no issue.
        var SIZE = 4
    }

}
