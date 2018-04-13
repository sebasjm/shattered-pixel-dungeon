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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Image
import com.watabou.utils.Bundle

class Poison : Buff(), Hero.Doom {

    protected var left: Float = 0.toFloat()

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)

    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
    }

    fun set(duration: Float) {
        this.left = Math.max(duration, left)
    }

    fun extend(duration: Float) {
        this.left += duration
    }

    override fun icon(): Int {
        return BuffIndicator.POISON
    }

    override fun tintIcon(icon: Image) {
        icon.hardlight(0.6f, 0.2f, 0.6f)
    }

    override fun toString(): String {
        return Messages.get(this, "name")
    }

    override fun heroMessage(): String? {
        return Messages.get(this, "heromsg")
    }

    override fun desc(): String {
        return Messages.get(this, "desc", dispTurns(left))
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target) && target.sprite != null) {
            CellEmitter.center(target.pos).burst(PoisonParticle.SPLASH, 5)
            return true
        } else
            return false
    }

    override fun act(): Boolean {
        if (target.isAlive) {

            target.damage((left / 3).toInt() + 1, this)
            spend(Actor.TICK)

            if ((left -= Actor.TICK) <= 0) {
                detach()
            }

        } else {

            detach()

        }

        return true
    }

    override fun onDeath() {
        Badges.validateDeathFromPoison()

        Dungeon.fail(javaClass)
        GLog.n(Messages.get(this, "ondeath"))
    }

    companion object {

        private val LEFT = "left"
    }
}
