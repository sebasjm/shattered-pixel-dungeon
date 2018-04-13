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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

open class Dart : MissileWeapon() {

    init {
        image = ItemSpriteSheet.DART
    }

    override fun min(lvl: Int): Int {
        return if (bow != null) 4 + bow!!.level() else 1
    }

    override fun max(lvl: Int): Int {
        return if (bow != null) 12 + 3 * bow!!.level() else 2
    }

    override fun STRReq(lvl: Int): Int {
        return 9
    }

    override fun durabilityPerUse(): Float {
        return 0f
    }

    private fun updateCrossbow() {
        if (Dungeon.hero!!.belongings.weapon is Crossbow) {
            bow = Dungeon.hero!!.belongings.weapon as Crossbow
        } else {
            bow = null
        }
    }

    override fun throwPos(user: Hero?, dst: Int): Int {
        return if (bow != null && bow!!.hasEnchant(Projecting::class.java)
                && !Dungeon.level!!.solid[dst] && Dungeon.level!!.distance(user!!.pos, dst) <= 4) {
            dst
        } else {
            super.throwPos(user, dst)
        }
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {
        var damage = damage
        if (bow != null && bow!!.enchantment != null) {
            damage = bow!!.enchantment!!.proc(bow, attacker, defender, damage)
        }
        return super.proc(attacker, defender, damage)
    }

    override fun onThrow(cell: Int) {
        updateCrossbow()
        super.onThrow(cell)
    }

    override fun info(): String {
        updateCrossbow()
        return super.info()
    }

    override fun price(): Int {
        return 4 * quantity
    }

    companion object {

        private var bow: Crossbow? = null
    }
}
