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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class Dirk : MeleeWeapon() {

    init {
        image = ItemSpriteSheet.DIRK

        tier = 2
    }

    override fun max(lvl: Int): Int {
        return 4 * (tier + 1) +    //12 base, down from 15
                lvl * (tier + 1)   //scaling unchanged
    }

    override fun damageRoll(owner: Char): Int {
        if (owner is Hero) {
            val hero = owner
            val enemy = hero.enemy()
            if (enemy is Mob && enemy.surprisedBy(hero)) {
                //deals 67% toward max to max on surprise, instead of min to max.
                val diff = max() - min()
                var damage = imbue.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff * 0.67f),
                        max()))
                val exStr = hero.STR() - STRReq()
                if (exStr > 0) {
                    damage += Random.IntRange(0, exStr)
                }
                return damage
            }
        }
        return super.damageRoll(owner)
    }

}
