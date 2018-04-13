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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class HealingDart : TippedDart() {

    init {
        image = ItemSpriteSheet.HEALING_DART
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {

        //heals 30 hp at base, scaling with enemy HT
        Buff.affect<Healing>(defender, Healing::class.java)!!.setHeal((0.5f * defender.HT + 30).toInt(), 0.333f, 0)
        PotionOfHealing.cure(defender)

        return if (attacker.alignment == defender.alignment) {
            0
        } else super.proc(attacker, defender, damage)

    }

}
