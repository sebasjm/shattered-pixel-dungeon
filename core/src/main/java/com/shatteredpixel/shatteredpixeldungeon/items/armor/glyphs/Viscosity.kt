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

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor.Glyph
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class Viscosity : Glyph() {

    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {

        if (damage == 0) {
            return 0
        }

        val level = Math.max(0, armor.level())

        if (Random.Int(level + 6) >= 5) {

            var debuff = defender.buff<DeferedDamage>(DeferedDamage::class.java)
            if (debuff == null) {
                debuff = DeferedDamage()
                debuff.attachTo(defender)
            }
            debuff.prolong(damage)

            defender.sprite!!.showStatus(CharSprite.WARNING, Messages.get(this.javaClass, "deferred", damage))

            return 0

        } else {
            return damage
        }
    }

    override fun glowing(): Glowing {
        return PURPLE
    }

    class DeferedDamage : Buff() {

        protected var damage = 0

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(DAMAGE, damage)

        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            damage = bundle.getInt(DAMAGE)
        }

        override fun attachTo(target: Char): Boolean {
            if (super.attachTo(target)) {
                postpone(Actor.TICK)
                return true
            } else {
                return false
            }
        }

        fun prolong(damage: Int) {
            this.damage += damage
        }

        override fun icon(): Int {
            return BuffIndicator.DEFERRED
        }

        override fun toString(): String {
            return Messages.get(this.javaClass, "name")
        }

        override fun act(): Boolean {
            if (target!!.isAlive) {

                val damageThisTick = Math.max(1, (damage * 0.1f).toInt())
                target!!.damage(damageThisTick, this)
                if (target!! === Dungeon.hero!! && !target!!.isAlive) {

                    Dungeon.fail(javaClass)
                    GLog.n(Messages.get(this.javaClass, "ondeath"))

                    Badges.validateDeathFromGlyph()
                }
                spend(Actor.TICK)

                damage -= damageThisTick
                if (damage <= 0) {
                    detach()
                }

            } else {

                detach()

            }

            return true
        }

        override fun desc(): String {
            return Messages.get(this.javaClass, "desc", damage)
        }

        companion object {

            private val DAMAGE = "damage"
        }
    }

    companion object {

        private val PURPLE = ItemSprite.Glowing(0x8844CC)
    }
}
