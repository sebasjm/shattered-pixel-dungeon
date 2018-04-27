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

package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite

class Swiftness : Armor.Glyph() {

    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        //no proc effect, see hero.defenseskill and hero.speed for effect.
        return damage
    }

    override fun tierDRAdjust(): Int {
        return -2
    }

    override fun tierSTRAdjust(): Float {
        return -1f
    }

    override fun glowing(): ItemSprite.Glowing {
        return YELLOW
    }

    companion object {

        private val YELLOW = ItemSprite.Glowing(0xFFFF00)
    }

}
