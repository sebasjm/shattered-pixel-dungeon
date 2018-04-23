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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class Exhausting : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Int {

        if (attacker === Dungeon.hero!! && Random.Int(15) == 0) {
            Buff.affect<Weakness>(attacker, Weakness::class.java, Random.NormalIntRange(5, 20).toFloat())
        }

        return damage
    }

    override fun curse(): Boolean {
        return true
    }

    override fun glowing(): ItemSprite.Glowing {
        return BLACK
    }

    companion object {

        private val BLACK = ItemSprite.Glowing(0x000000)
    }
}
