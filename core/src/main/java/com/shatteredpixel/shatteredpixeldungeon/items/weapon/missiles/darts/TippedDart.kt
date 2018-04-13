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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass

import java.util.ArrayList
import java.util.HashMap

abstract class TippedDart : Dart() {

    init {
        bones = true
    }

    override fun STRReq(lvl: Int): Int {
        return 11
    }

    override fun rangedHit(enemy: Char, cell: Int) {
        if (enemy.isAlive)
            Buff.affect<PinCushion>(enemy, PinCushion::class.java)!!.stick(Dart())
        else
            Dungeon.level!!.drop(Dart(), enemy.pos).sprite!!.drop()
    }

    override fun price(): Int {
        return 6 * quantity
    }

    class TipDart : Recipe() {

        override//also sorts ingredients if it can
        fun testIngredients(ingredients: ArrayList<Item>): Boolean {
            if (ingredients.size != 2) return false

            if (ingredients[0].javaClass == Dart::class.java) {
                if (ingredients[1] !is Plant.Seed) {
                    return false
                }
            } else if (ingredients[0] is Plant.Seed) {
                if (ingredients[1].javaClass == Dart::class.java) {
                    val temp = ingredients[0]
                    ingredients[0] = ingredients[1]
                    ingredients[1] = temp
                } else {
                    return false
                }
            } else {
                return false
            }

            val seed = ingredients[1] as Plant.Seed

            return if (ingredients[0].quantity() >= 2
                    && seed.quantity() >= 1
                    && types.containsKey(seed.javaClass)) {
                true
            } else false

        }

        override fun cost(ingredients: ArrayList<Item>): Int {
            return 2
        }

        override fun brew(ingredients: ArrayList<Item>): Item? {
            if (!testIngredients(ingredients)) return null

            ingredients[0].quantity(ingredients[0].quantity() - 2)
            ingredients[1].quantity(ingredients[1].quantity() - 1)

            try {
                return types[ingredients[1].javaClass].newInstance().quantity(2)
            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
                return null
            }

        }

        override fun sampleOutput(ingredients: ArrayList<Item>): Item? {
            if (!testIngredients(ingredients)) return null

            try {
                return types[ingredients[1].javaClass].newInstance().quantity(2)
            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
                return null
            }

        }
    }

    companion object {

        private val types = HashMap<Class<out Plant.Seed>, Class<out TippedDart>>()

        init {
            types[Blindweed.Seed::class.java] = BlindingDart::class.java
            types[Dreamfoil.Seed::class.java] = SleepDart::class.java
            types[Earthroot.Seed::class.java] = ParalyticDart::class.java
            types[Fadeleaf.Seed::class.java] = DisplacingDart::class.java
            types[Firebloom.Seed::class.java] = IncendiaryDart::class.java
            types[Icecap.Seed::class.java] = ChillingDart::class.java
            types[Rotberry.Seed::class.java] = RotDart::class.java
            types[Sorrowmoss.Seed::class.java] = PoisonDart::class.java
            types[Starflower.Seed::class.java] = HolyDart::class.java
            types[Stormvine.Seed::class.java] = ShockingDart::class.java
            types[Sungrass.Seed::class.java] = HealingDart::class.java
        }

        fun randomTipped(): TippedDart? {
            var s: Plant.Seed
            do {
                s = Generator.random(Generator.Category.SEED) as Plant.Seed
            } while (!types.containsKey(s.javaClass))

            try {
                return types[s.javaClass].newInstance().quantity(2) as TippedDart
            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
                return null
            }

        }
    }
}
