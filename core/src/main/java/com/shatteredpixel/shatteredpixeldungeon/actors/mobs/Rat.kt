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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite
import com.watabou.utils.Random

open class Rat : Mob() {

    init {
        spriteClass = RatSprite::class.java

        HT = 8
        HP = HT
        defenseSkill = 2

        maxLvl = 5
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(1, 4)
    }

    override fun attackSkill(target: Char): Int {
        return 8
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 1)
    }
}
