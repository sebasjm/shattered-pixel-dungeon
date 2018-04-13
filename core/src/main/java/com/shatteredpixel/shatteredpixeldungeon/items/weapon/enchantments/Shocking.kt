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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class Shocking : Weapon.Enchantment() {

    private val affected = ArrayList<Char>()

    private val arcs = ArrayList<Lightning.Arc>()

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Int {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        val level = Math.max(0, weapon.level())

        if (Random.Int(level + 3) >= 2) {

            affected.clear()
            affected.add(attacker)

            arcs.clear()
            arcs.add(Lightning.Arc(attacker.sprite!!.center(), defender.sprite!!.center()))
            hit(defender, Random.Int(1, damage / 3))

            attacker.sprite!!.parent!!.addToFront(Lightning(arcs, null))

        }

        return damage

    }

    override fun glowing(): ItemSprite.Glowing {
        return WHITE
    }

    private fun hit(ch: Char, damage: Int) {

        if (damage < 1) {
            return
        }

        affected.add(ch)
        ch.damage(if (Dungeon.level!!.water[ch.pos] && !ch.flying) 2 * damage else damage, this)

        ch.sprite!!.centerEmitter().burst(SparkParticle.FACTORY, 3)
        ch.sprite!!.flash()

        for (i in PathFinder.NEIGHBOURS8.indices) {
            val n = Actor.findChar(ch.pos + PathFinder.NEIGHBOURS8[i])
            if (n != null && !affected.contains(n)) {
                arcs.add(Lightning.Arc(ch.sprite!!.center(), n.sprite!!.center()))
                hit(n, Random.Int(damage / 2, damage))
            }
        }
    }

    companion object {

        private val WHITE = ItemSprite.Glowing(0xFFFFFF, 0.6f)
    }
}
