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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh
import com.shatteredpixel.shatteredpixeldungeon.items.Bomb
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.MerchantsBeacon
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus
import com.shatteredpixel.shatteredpixeldungeon.items.Torch
import com.shatteredpixel.shatteredpixeldungeon.items.Weightstone
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.watabou.utils.Point
import com.watabou.utils.Random

import java.util.ArrayList

open class ShopRoom : SpecialRoom() {

    private var itemsToSpawn: ArrayList<Item>? = null

    override fun minWidth(): Int {
        if (itemsToSpawn == null) itemsToSpawn = generateItems()
        return Math.max(7, (Math.sqrt(itemsToSpawn!!.size.toDouble()) + 3).toInt())
    }

    override fun minHeight(): Int {
        if (itemsToSpawn == null) itemsToSpawn = generateItems()
        return Math.max(7, (Math.sqrt(itemsToSpawn!!.size.toDouble()) + 3).toInt())
    }

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)

        placeShopkeeper(level)

        placeItems(level)

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }

    }

    protected open fun placeShopkeeper(level: Level) {

        val pos = level.pointToCell(center())

        val shopkeeper = Shopkeeper()
        shopkeeper.pos = pos
        level.mobs.add(shopkeeper)

    }

    protected fun placeItems(level: Level) {

        if (itemsToSpawn == null)
            itemsToSpawn = generateItems()

        val itemPlacement = Point(entrance())
        if (itemPlacement.y == top) {
            itemPlacement.y++
        } else if (itemPlacement.y == bottom) {
            itemPlacement.y--
        } else if (itemPlacement.x == left) {
            itemPlacement.x++
        } else {
            itemPlacement.x--
        }

        for (item in itemsToSpawn!!) {

            if (itemPlacement.x == left + 1 && itemPlacement.y != top + 1) {
                itemPlacement.y--
            } else if (itemPlacement.y == top + 1 && itemPlacement.x != right - 1) {
                itemPlacement.x++
            } else if (itemPlacement.x == right - 1 && itemPlacement.y != bottom - 1) {
                itemPlacement.y++
            } else {
                itemPlacement.x--
            }

            var cell = level.pointToCell(itemPlacement)

            if (level.heaps.get(cell) != null) {
                do {
                    cell = level.pointToCell(random())
                } while (level.heaps.get(cell) != null || level.findMob(cell) != null)
            }

            level.drop(item, cell).type = Heap.Type.FOR_SALE
        }

    }

    companion object {

        protected fun generateItems(): ArrayList<Item> {

            val itemsToSpawn = ArrayList<Item>()

            when (Dungeon.depth) {
                6 -> {
                    itemsToSpawn.add((if (Random.Int(2) == 0) Shortsword().identify() else HandAxe()).identify())
                    itemsToSpawn.add(if (Random.Int(2) == 0)
                        FishingSpear().quantity(2)
                    else
                        Shuriken().quantity(2))
                    itemsToSpawn.add(LeatherArmor().identify())
                }

                11 -> {
                    itemsToSpawn.add((if (Random.Int(2) == 0) Sword().identify() else Mace()).identify())
                    itemsToSpawn.add(if (Random.Int(2) == 0)
                        ThrowingSpear().quantity(2)
                    else
                        Bolas().quantity(2))
                    itemsToSpawn.add(MailArmor().identify())
                }

                16 -> {
                    itemsToSpawn.add((if (Random.Int(2) == 0) Longsword().identify() else BattleAxe()).identify())
                    itemsToSpawn.add(if (Random.Int(2) == 0)
                        Javelin().quantity(2)
                    else
                        Tomahawk().quantity(2))
                    itemsToSpawn.add(ScaleArmor().identify())
                }

                21 -> {
                    itemsToSpawn.add(if (Random.Int(2) == 0) Greatsword().identify() else WarHammer().identify())
                    itemsToSpawn.add(if (Random.Int(2) == 0)
                        Trident().quantity(2)
                    else
                        ThrowingHammer().quantity(2))
                    itemsToSpawn.add(PlateArmor().identify())
                    itemsToSpawn.add(Torch())
                    itemsToSpawn.add(Torch())
                    itemsToSpawn.add(Torch())
                }
            }

            itemsToSpawn.add(TippedDart.randomTipped())

            itemsToSpawn.add(MerchantsBeacon())


            itemsToSpawn.add(ChooseBag(Dungeon.hero!!.belongings))


            itemsToSpawn.add(PotionOfHealing())
            for (i in 0..2)
                itemsToSpawn.add(Generator.random(Generator.Category.POTION))

            itemsToSpawn.add(ScrollOfIdentify())
            itemsToSpawn.add(ScrollOfRemoveCurse())
            itemsToSpawn.add(ScrollOfMagicMapping())
            itemsToSpawn.add(Generator.random(Generator.Category.SCROLL))

            for (i in 0..1)
                itemsToSpawn.add(if (Random.Int(2) == 0)
                    Generator.random(Generator.Category.POTION)
                else
                    Generator.random(Generator.Category.SCROLL))


            itemsToSpawn.add(SmallRation())
            itemsToSpawn.add(SmallRation())

            itemsToSpawn.add(Bomb().random())
            when (Random.Int(5)) {
                1 -> itemsToSpawn.add(Bomb())
                2 -> itemsToSpawn.add(Bomb().random())
                3, 4 -> itemsToSpawn.add(Honeypot())
            }


            if (Dungeon.depth == 6) {
                itemsToSpawn.add(Ankh())
                itemsToSpawn.add(Weightstone())
            } else {
                itemsToSpawn.add(if (Random.Int(2) == 0) Ankh() else Weightstone())
            }


            val hourglass = Dungeon.hero!!.belongings.getItem<TimekeepersHourglass>(TimekeepersHourglass::class.java)
            if (hourglass != null) {
                var bags = 0
                //creates the given float percent of the remaining bags to be dropped.
                //this way players who get the hourglass late can still max it, usually.
                when (Dungeon.depth) {
                    6 -> bags = Math.ceil(((5 - hourglass.sandBags) * 0.20f).toDouble()).toInt()
                    11 -> bags = Math.ceil(((5 - hourglass.sandBags) * 0.25f).toDouble()).toInt()
                    16 -> bags = Math.ceil(((5 - hourglass.sandBags) * 0.50f).toDouble()).toInt()
                    21 -> bags = Math.ceil(((5 - hourglass.sandBags) * 0.80f).toDouble()).toInt()
                }

                for (i in 1..bags) {
                    itemsToSpawn.add(TimekeepersHourglass.sandBag())
                    hourglass.sandBags++
                }
            }

            val rare: Item?
            when (Random.Int(10)) {
                0 -> {
                    rare = Generator.random(Generator.Category.WAND)
                    rare!!.level(0)
                }
                1 -> {
                    rare = Generator.random(Generator.Category.RING)
                    rare!!.level(0)
                }
                2 -> rare = Generator.random(Generator.Category.ARTIFACT)
                else -> rare = Stylus()
            }
            rare!!.cursedKnown = false
            rare.cursed = rare.cursedKnown
            itemsToSpawn.add(rare)

            //hard limit is 63 items + 1 shopkeeper, as shops can't be bigger than 8x8=64 internally
            if (itemsToSpawn.size > 63)
                throw RuntimeException("Shop attempted to carry more than 63 items!")

            Random.shuffle(itemsToSpawn)
            return itemsToSpawn
        }

        protected fun ChooseBag(pack: Belongings): Bag? {

            //0=pouch, 1=holder, 2=bandolier, 3=holster
            val bagItems = IntArray(4)

            //count up items in the main bag
            for (item in pack.backpack.items) {
                if (item is Plant.Seed || item is Runestone) bagItems[0]++
                if (item is Scroll) bagItems[1]++
                if (item is Potion) bagItems[2]++
                if (item is Wand || item is MissileWeapon) bagItems[3]++
            }

            //disqualify bags that have already been dropped
            if (Dungeon.LimitedDrops.VELVET_POUCH.dropped()) bagItems[0] = -1
            if (Dungeon.LimitedDrops.SCROLL_HOLDER.dropped()) bagItems[1] = -1
            if (Dungeon.LimitedDrops.POTION_BANDOLIER.dropped()) bagItems[2] = -1
            if (Dungeon.LimitedDrops.MAGICAL_HOLSTER.dropped()) bagItems[3] = -1

            //find the best bag to drop. This does give a preference to later bags, if counts are equal
            var bestBagIdx = 0
            for (i in 1..3) {
                if (bagItems[bestBagIdx] <= bagItems[i]) {
                    bestBagIdx = i
                }
            }

            //drop it, or return nothing if no bag works
            if (bagItems[bestBagIdx] == -1) return null
            when (bestBagIdx) {
                0 -> {
                    Dungeon.LimitedDrops.VELVET_POUCH.drop()
                    return VelvetPouch()
                }
                1 -> {
                    Dungeon.LimitedDrops.SCROLL_HOLDER.drop()
                    return ScrollHolder()
                }
                2 -> {
                    Dungeon.LimitedDrops.POTION_BANDOLIER.drop()
                    return PotionBandolier()
                }
                3 -> {
                    Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop()
                    return MagicalHolster()
                }
                else -> {
                    Dungeon.LimitedDrops.VELVET_POUCH.drop()
                    return VelvetPouch()
                }
            }

        }
    }

}
