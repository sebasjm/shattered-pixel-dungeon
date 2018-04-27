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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class Gauntlet : MeleeWeapon() {

    init {
        image = ItemSpriteSheet.GAUNTLETS

        tier = 5
        DLY = 0.5f //2x speed
    }

    override fun max(lvl: Int): Int {
        return Math.round(2.5f * (tier + 1)) +     //15 base, down from 30
                lvl * Math.round(0.5f * (tier + 1))  //+3 per level, down from +6
    }

    override fun defenseFactor(owner: Char): Int {
        return 5    //5 extra defence
    }
}
