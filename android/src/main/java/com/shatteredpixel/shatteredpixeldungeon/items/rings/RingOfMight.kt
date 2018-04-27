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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item

class RingOfMight : Ring() {

    override fun doEquip(hero: Hero): Boolean {
        if (super.doEquip(hero)) {
            hero.updateHT(false)
            return true
        } else {
            return false
        }
    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {
            hero!!.updateHT(false)
            return true
        } else {
            return false
        }
    }

    override fun upgrade(): Item {
        super.upgrade()
        updateTargetHT()
        return this
    }

    override fun level(value: Int) {
        super.level(value)
        updateTargetHT()
    }

    private fun updateTargetHT() {
        if (buff != null && buff!!.target!! is Hero) {
            (buff!!.target!! as Hero).updateHT(false)
        }
    }

    override fun buff(): Ring.RingBuff? {
        return Might()
    }

    inner class Might : Ring.RingBuff()

    companion object {

        fun strengthBonus(target: Char): Int {
            return Ring.getBonus(target, Might::class.java)
        }

        fun HTMultiplier(target: Char): Float {
            return Math.pow(1.035, Ring.getBonus(target, Might::class.java).toDouble()).toFloat()
        }
    }
}

