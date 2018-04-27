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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing
import com.watabou.utils.Random

class Chilling : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Int {
        // lvl 0 - 20%
        // lvl 1 - 33%
        // lvl 2 - 43%
        val level = Math.max(0, weapon.level())

        if (Random.Int(level + 5) >= 4) {

            Buff.prolong<Chill>(defender, Chill::class.java, Random.Float(2f, 3f))
            Splash.at(defender.sprite!!.center(), -0x4d2901, 5)

        }

        return damage
    }

    override fun glowing(): Glowing {
        return TEAL
    }

    companion object {

        private val TEAL = ItemSprite.Glowing(0x00FFFF)
    }

}
