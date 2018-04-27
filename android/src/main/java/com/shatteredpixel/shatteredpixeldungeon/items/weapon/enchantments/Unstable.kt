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

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.watabou.noosa.Game
import com.watabou.utils.Random

class Unstable : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Int {
        try {
            return Random.oneOf<Class<out Weapon.Enchantment>>(*randomEnchants as Array<out Class<out Weapon.Enchantment>>).newInstance().proc(weapon, attacker, defender, damage)
        } catch (e: Exception) {
            Game.reportException(e)
            return damage
        }

    }

    override fun glowing(): ItemSprite.Glowing {
        return WHITE
    }

    companion object {

        private val WHITE = ItemSprite.Glowing(0xFFFFFF)

        private val randomEnchants = arrayOf<Class<*>>(Blazing::class.java, Chilling::class.java, Dazzling::class.java, Eldritch::class.java, Grim::class.java, Lucky::class.java,
                //projecting not included, no on-hit effect
                Shocking::class.java, Stunning::class.java, Vampiric::class.java, Venomous::class.java, Vorpal::class.java)
    }
}
