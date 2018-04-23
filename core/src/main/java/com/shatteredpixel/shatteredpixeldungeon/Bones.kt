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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact
import com.watabou.noosa.Game
import com.watabou.utils.Bundle
import com.watabou.utils.FileUtils
import com.watabou.utils.Random

import java.io.IOException
import java.util.ArrayList

object Bones {

    private val BONES_FILE = "bones.dat"

    private val LEVEL = "level"
    private val ITEM = "item"

    private var depth = -1
    private var item: Item? = null

    fun leave() {

        depth = Dungeon.depth

        //heroes which have won the game, who die far above their farthest depth, or who are challenged drop no bones.
        if (Statistics.amuletObtained || Statistics.deepestFloor - 5 >= depth || Dungeon.challenges > 0) {
            depth = -1
            return
        }

        item = pickItem(Dungeon.hero!!)

        val bundle = Bundle()
        bundle.put(LEVEL, depth)
        bundle.put(ITEM, item)

        try {
            FileUtils.bundleToFile(BONES_FILE, bundle)
        } catch (e: IOException) {
            Game.reportException(e)
        }

    }

    private fun pickItem(hero: Hero?): Item {
        var item: Item? = null
        if (Random.Int(3) != 0) {
            when (Random.Int(6)) {
                0 -> item = hero!!.belongings.weapon
                1 -> item = hero!!.belongings.armor
                2 -> item = hero!!.belongings.misc1
                3 -> item = hero!!.belongings.misc2
                4, 5 -> item = Dungeon.quickslot.randomNonePlaceholder()
            }
            if (item == null || !item.bones) {
                return pickItem(hero)
            }
        } else {

            val iterator = hero!!.belongings.backpack.iterator()
            var curItem: Item
            val items = ArrayList<Item>()
            while (iterator.hasNext()) {
                curItem = iterator.next()
                if (curItem.bones)
                    items.add(curItem)
            }

            if (Random.Int(3) < items.size) {
                item = Random.element(items)
                if (item!!.stackable) {
                    item.quantity(Random.NormalIntRange(1, (item.quantity() + 1) / 2))
                }
            } else {
                if (Dungeon.gold > 100) {
                    item = Gold(Random.NormalIntRange(50, Dungeon.gold / 2))
                } else {
                    item = Gold(50)
                }
            }
        }

        return item
    }

    fun get(): Item? {
        if (depth == -1) {

            try {
                val bundle = FileUtils.bundleFromFile(BONES_FILE)

                depth = bundle.getInt(LEVEL)
                item = bundle.get(ITEM) as Item

                return get()

            } catch (e: IOException) {
                return null
            }

        } else {
            //heroes who are challenged cannot find bones
            if (depth == Dungeon.depth && Dungeon.challenges == 0) {
                FileUtils.deleteFile(BONES_FILE)
                depth = 0

                //Enforces artifact uniqueness
                if (item is Artifact) {
                    return if (Generator.removeArtifact((item as Artifact).javaClass)) {
                        try {
                            //generates a new artifact of the same type, always +0
                            val artifact = item!!.javaClass.newInstance() as Artifact

                            artifact.cursed = true
                            artifact.cursedKnown = true

                            artifact
                        } catch (e: Exception) {
                            Game.reportException(e)
                            Gold(item!!.price())
                        }

                    } else {
                        Gold(item!!.price())
                    }
                }

                if (item!!.isUpgradable) {
                    item!!.cursed = true
                    item!!.cursedKnown = true
                    if (item!!.isUpgradable) {
                        //caps at +3
                        if (item!!.level() > 3) {
                            item!!.degrade(item!!.level() - 3)
                        }
                        item!!.levelKnown = false
                    }
                }

                item!!.reset()

                return item
            } else {
                return null
            }
        }
    }
}
