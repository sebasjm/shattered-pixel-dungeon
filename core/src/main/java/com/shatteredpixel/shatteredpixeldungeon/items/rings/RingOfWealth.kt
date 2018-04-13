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

package com.shatteredpixel.shatteredpixeldungeon.items.rings

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.Bomb
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashSet

class RingOfWealth : Ring() {

    private var triesToDrop = 0f

    override fun buff(): Ring.RingBuff? {
        return Wealth()
    }

    inner class Wealth : Ring.RingBuff() {

        private fun triesToDrop(`val`: Float) {
            triesToDrop = `val`
        }

        private fun triesToDrop(): Float {
            return triesToDrop
        }


    }

    companion object {

        fun dropChanceMultiplier(target: Char): Float {
            return Math.pow(1.15, Ring.getBonus(target, Wealth::class.java).toDouble()).toFloat()
        }

        fun tryRareDrop(target: Char, tries: Int): ArrayList<Item>? {
            if (Ring.getBonus(target, Wealth::class.java) <= 0) return null

            val buffs = target.buffs<Wealth>(Wealth::class.java)
            var triesToDrop = -1f

            //find the largest count (if they aren't synced yet)
            for (w in buffs) {
                if (w.triesToDrop() > triesToDrop) {
                    triesToDrop = w.triesToDrop()
                }
            }

            //reset (if needed), decrement, and store counts
            if (triesToDrop <= 0) triesToDrop += Random.NormalIntRange(15, 60).toFloat()
            triesToDrop -= dropProgression(target, tries)
            for (w in buffs) {
                w.triesToDrop(triesToDrop)
            }

            //now handle reward logic
            return if (triesToDrop <= 0) {
                generateRareDrop()
            } else {
                null
            }

        }

        //TODO this is a start, but i'm sure this could be made more interesting...
        private fun generateRareDrop(): ArrayList<Item> {
            val roll = Random.Float()
            val items = ArrayList<Item>()
            if (roll < 0.6f) {
                when (Random.Int(3)) {
                    0 -> items.add(Gold().random())
                    1 -> items.add(Generator.random(Generator.Category.POTION))
                    2 -> items.add(Generator.random(Generator.Category.SCROLL))
                }
            } else if (roll < 0.9f) {
                when (Random.Int(3)) {
                    0 -> {
                        items.add(Generator.random(Generator.Category.SEED))
                        items.add(Generator.random(Generator.Category.SEED))
                        items.add(Generator.random(Generator.Category.SEED))
                        items.add(Generator.random(Generator.Category.SEED))
                        items.add(Generator.random(Generator.Category.SEED))
                    }
                    1 -> {
                        items.add(Generator.random(if (Random.Int(2) == 0) Generator.Category.POTION else Generator.Category.SCROLL))
                        items.add(Generator.random(if (Random.Int(2) == 0) Generator.Category.POTION else Generator.Category.SCROLL))
                        items.add(Generator.random(if (Random.Int(2) == 0) Generator.Category.POTION else Generator.Category.SCROLL))
                    }
                    2 -> {
                        items.add(Bomb().random())
                        items.add(Honeypot())
                    }
                }
            } else {
                val g = Gold()
                g.random()
                g.quantity(g.quantity() * 5)
                items.add(g)
            }
            return items
        }

        //caps at a 50% bonus
        private fun dropProgression(target: Char, tries: Int): Float {
            return tries * Math.pow(1.2, (Ring.getBonus(target, Wealth::class.java) - 1).toDouble()).toFloat()
        }
    }
}
