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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class ThrowingKnife : MissileWeapon() {

    private var enemy: Char? = null

    init {
        image = ItemSpriteSheet.THROWING_KNIFE

        bones = false

    }

    override fun min(lvl: Int): Int {
        return 2
    }

    override fun max(lvl: Int): Int {
        return 6
    }

    override fun STRReq(lvl: Int): Int {
        return 9
    }

    override fun onThrow(cell: Int) {
        enemy = Actor.findChar(cell)
        super.onThrow(cell)
    }

    override fun damageRoll(owner: Char): Int {
        if (owner is Hero) {
            val hero = owner
            if (enemy is Mob && (enemy as Mob).surprisedBy(hero)) {
                //deals 75% toward max to max on surprise, instead of min to max.
                val diff = max() - min()
                var damage = imbue.damageFactor(Random.NormalIntRange(
                        min() + Math.round(diff * 0.75f),
                        max()))
                damage = Math.round(damage * RingOfSharpshooting.damageMultiplier(hero))
                val exStr = hero.STR() - STRReq()
                if (exStr > 0 && hero.heroClass == HeroClass.HUNTRESS) {
                    damage += Random.IntRange(0, exStr)
                }
                return damage
            }
        }
        return super.damageRoll(owner)
    }

    override fun durabilityPerUse(): Float {
        return super.durabilityPerUse() * 2f
    }

    override fun price(): Int {
        return 6 * quantity
    }
}
