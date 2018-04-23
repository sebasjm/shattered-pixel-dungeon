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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

abstract class WellWater : Blob() {

    protected var pos: Int = 0

    override fun evolve() {
        var cell: Int
        var seen = false
        for (i in area.top - 1..area.bottom) {
            for (j in area.left - 1..area.right) {
                cell = j + i * Dungeon.level!!.width()
                if (Dungeon.level!!.insideMap(cell)) {
                    off!![cell] = cur!![cell]
                    volume += off!![cell]
                    if (off!![cell] > 0 && Dungeon.level!!.visited!![cell]) {
                        seen = true
                    }
                }
            }
        }
        if (seen) {
            Notes.add(record())
        } else {
            Notes.remove(record())
        }
    }

    protected fun affect(pos: Int): Boolean {

        val heap: Heap?

        if (pos == Dungeon.hero!!.pos && affectHero(Dungeon.hero!!)) {

            cur!![pos] = 0
            return true

        } else {
            heap = Dungeon.level!!.heaps.get(pos)

            if (heap != null) {

                val oldItem = heap.peek()
                val newItem = affectItem(oldItem)

                if (newItem != null) {

                    if (newItem === oldItem) {

                    } else if (oldItem.quantity() > 1) {

                        oldItem.quantity(oldItem.quantity() - 1)
                        heap.drop(newItem)

                    } else {
                        heap.replace(oldItem, newItem)
                    }

                    heap.sprite!!.link()
                    cur!![pos] = 0

                    return true

                } else {

                    var newPlace: Int
                    do {
                        newPlace = pos + PathFinder.NEIGHBOURS8!![Random.Int(8)]
                    } while (!Dungeon.level!!.passable[newPlace] && !Dungeon.level!!.avoid[newPlace])
                    Dungeon.level!!.drop(heap.pickUp(), newPlace).sprite!!.drop(pos)

                    return false

                }

            } else {

                return false

            }
        }
    }

    protected abstract fun affectHero(hero: Hero?): Boolean

    protected abstract fun affectItem(item: Item): Item?

    protected abstract fun record(): Notes.Landmark

    companion object {

        fun affectCell(cell: Int) {

            val waters = arrayOf<Class<*>>(WaterOfHealth::class.java, WaterOfAwareness::class.java, WaterOfTransmutation::class.java)

            for (waterClass in waters) {
                val water = Dungeon.level!!.blobs.get(waterClass) as WellWater
                if (water != null &&
                        water.volume > 0 &&
                        water.cur!![cell] > 0 &&
                        water.affect(cell)) {

                    Level.set(cell, Terrain.EMPTY_WELL)
                    GameScene.updateMap(cell)

                    return
                }
            }
        }
    }
}
