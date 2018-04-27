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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.watabou.noosa.audio.Sample

class GrippingTrap : Trap() {

    init {
        color = Trap.GREY
        shape = Trap.DOTS
    }

    override fun trigger() {
        if (Dungeon.level!!.heroFOV[pos]) {
            Sample.INSTANCE.play(Assets.SND_TRAP)
        }
        //this trap is not disarmed by being triggered
        reveal()
        Level.set(pos, Terrain.TRAP)
        activate()
    }

    override fun activate() {

        val c = Actor.findChar(pos)

        if (c != null) {
            val damage = Math.max(0, 2 + Dungeon.depth - c.drRoll())
            Buff.affect<Bleeding>(c, Bleeding::class.java)!!.set(damage)
            Buff.prolong<Cripple>(c, Cripple::class.java, Cripple.DURATION)
            Wound.hit(c)
        } else {
            Wound.hit(pos)
        }

    }
}
