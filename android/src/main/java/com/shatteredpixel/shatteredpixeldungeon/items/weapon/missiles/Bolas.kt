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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class Bolas : MissileWeapon() {

    init {
        image = ItemSpriteSheet.BOLAS

    }

    override fun min(lvl: Int): Int {
        return 4
    }

    override fun max(lvl: Int): Int {
        return 6
    }

    override fun STRReq(lvl: Int): Int {
        return 15
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {
        Buff.prolong<Cripple>(defender, Cripple::class.java, Cripple.DURATION)
        return super.proc(attacker, defender, damage)
    }

    override fun durabilityPerUse(): Float {
        return super.durabilityPerUse() * 2f
    }

    override fun price(): Int {
        return 18 * quantity
    }
}
