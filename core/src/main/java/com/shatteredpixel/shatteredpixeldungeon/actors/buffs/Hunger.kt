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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle

class Hunger : Buff(), Hero.Doom {

    private var level: Float = 0.toFloat()
    private var partialDamage: Float = 0.toFloat()

    val isStarving: Boolean
        get() = level >= STARVING

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
        bundle.put(PARTIALDAMAGE, partialDamage)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getFloat(LEVEL)
        partialDamage = bundle.getFloat(PARTIALDAMAGE)
    }

    override fun act(): Boolean {

        if (Dungeon.level!!.locked) {
            spend(STEP)
            return true
        }

        if (target.isAlive) {

            val hero = target as Hero

            if (isStarving) {

                partialDamage += STEP * target.HT / 1000f

                if (partialDamage > 1) {
                    target.damage(partialDamage.toInt(), this)
                    partialDamage -= partialDamage.toInt().toFloat()
                }

            } else {

                val newLevel = level + STEP
                var statusUpdated = false
                if (newLevel >= STARVING) {

                    GLog.n(Messages.get(this, "onstarving"))
                    hero.resting = false
                    hero.damage(1, this)
                    statusUpdated = true

                    hero.interrupt()

                } else if (newLevel >= HUNGRY && level < HUNGRY) {

                    GLog.w(Messages.get(this, "onhungry"))
                    statusUpdated = true

                }
                level = newLevel

                if (statusUpdated) {
                    BuffIndicator.refreshHero()
                }

            }

            spend(if (target.buff<Shadows>(Shadows::class.java) == null) STEP else STEP * 1.5f)

        } else {

            diactivate()

        }

        return true
    }

    fun satisfy(energy: Float) {
        var energy = energy

        val buff = target.buff<HornOfPlenty.hornRecharge>(HornOfPlenty.hornRecharge::class.java)
        if (buff != null && buff.isCursed) {
            energy *= 0.67f
            GLog.n(Messages.get(this, "cursedhorn"))
        }

        reduceHunger(energy)
    }

    //directly interacts with hunger, no checks.
    fun reduceHunger(energy: Float) {

        level -= energy
        if (level < 0) {
            level = 0f
        } else if (level > STARVING) {
            val excess = level - STARVING
            level = STARVING
            partialDamage += excess * (target.HT / 1000f)
        }

        BuffIndicator.refreshHero()
    }

    fun hunger(): Int {
        return Math.ceil(level.toDouble()).toInt()
    }

    override fun icon(): Int {
        return if (level < HUNGRY) {
            BuffIndicator.NONE
        } else if (level < STARVING) {
            BuffIndicator.HUNGER
        } else {
            BuffIndicator.STARVATION
        }
    }

    override fun toString(): String {
        return if (level < STARVING) {
            Messages.get(this, "hungry")
        } else {
            Messages.get(this, "starving")
        }
    }

    override fun desc(): String {
        var result: String
        if (level < STARVING) {
            result = Messages.get(this, "desc_intro_hungry")
        } else {
            result = Messages.get(this, "desc_intro_starving")
        }

        result += Messages.get(this, "desc")

        return result
    }

    override fun onDeath() {

        Badges.validateDeathFromHunger()

        Dungeon.fail(javaClass)
        GLog.n(Messages.get(this, "ondeath"))
    }

    companion object {

        private val STEP = 10f

        val HUNGRY = 300f
        val STARVING = 450f

        private val LEVEL = "level"
        private val PARTIALDAMAGE = "partialDamage"
    }
}
