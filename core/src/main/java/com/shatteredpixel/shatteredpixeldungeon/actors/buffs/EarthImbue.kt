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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image

class EarthImbue : FlavourBuff() {

    fun proc(enemy: Char) {
        Buff.affect<Roots>(enemy, Roots::class.java, 2f)
        CellEmitter.bottom(enemy.pos).start(EarthParticle.FACTORY, 0.05f, 8)
    }

    override fun icon(): Int {
        return BuffIndicator.ROOTS
    }

    override fun tintIcon(icon: Image) {
        FlavourBuff.greyIcon(icon, 5f, cooldown())
    }

    override fun toString(): String {
        return Messages.get(this, "name")
    }

    override fun desc(): String {
        return Messages.get(this, "desc", dispTurns())
    }

    init {
        immunities.add(Paralysis::class.java)
        immunities.add(Roots::class.java)
        immunities.add(Slow::class.java)
    }

    companion object {

        val DURATION = 30f
    }
}