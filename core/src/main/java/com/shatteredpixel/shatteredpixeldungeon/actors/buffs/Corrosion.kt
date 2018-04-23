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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Image
import com.watabou.utils.Bundle

class Corrosion : Buff(), Hero.Doom {

    private var damage = 1f
    protected var left: Float = 0.toFloat()

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DAMAGE, damage)
        bundle.put(LEFT, left)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        damage = bundle.getFloat(DAMAGE)
        left = bundle.getFloat(LEFT)
    }

    operator fun set(duration: Float, damage: Int) {
        this.left = Math.max(duration, left)
        if (this.damage < damage) this.damage = damage.toFloat()
    }

    override fun icon(): Int {
        return BuffIndicator.POISON
    }

    override fun tintIcon(icon: Image) {
        icon.hardlight(1f, 0.5f, 0f)
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun heroMessage(): String? {
        return Messages.get(this.javaClass, "heromsg")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns(left), damage.toInt())
    }

    override fun act(): Boolean {
        if (target!!.isAlive) {
            target!!.damage(damage.toInt(), this)
            if (damage < Dungeon.depth / 2 + 2) {
                damage++
            } else {
                damage += 0.5f
            }

            spend(Actor.TICK)
            left -= Actor.TICK
            if (left <= 0) {
                detach()
            }
        } else {
            detach()
        }

        return true
    }

    override fun onDeath() {
        Dungeon.fail(javaClass)
        GLog.n(Messages.get(this.javaClass, "ondeath"))
    }

    companion object {

        private val DAMAGE = "damage"
        private val LEFT = "left"
    }

}
