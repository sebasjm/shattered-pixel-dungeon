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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PathFinder
import com.watabou.utils.Point
import com.watabou.utils.Random

import java.util.ArrayList

class RockfallTrap : Trap() {

    init {
        color = Trap.GREY
        shape = Trap.DIAMOND
    }

    override fun hide(): Trap {
        //this one can't be hidden
        return reveal()
    }

    override fun activate() {

        val rockCells = ArrayList<Int>()

        if (Dungeon.level is RegularLevel) {
            val r = (Dungeon.level as RegularLevel).room(pos)
            var cell: Int
            for (p in r!!.points) {
                cell = Dungeon.level!!.pointToCell(p)
                if (!Dungeon.level!!.solid[cell]) {
                    rockCells.add(cell)
                }
            }

            //if we don't have rooms, then just do 5x5
        } else {
            PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level!!.solid, null), 2)
            for (i in PathFinder.distance!!.indices) {
                if (PathFinder.distance!![i] < Integer.MAX_VALUE) {
                    rockCells.add(i)
                }
            }
        }

        var seen = false
        for (cell in rockCells) {

            if (Dungeon.level!!.heroFOV[cell]) {
                CellEmitter.get(cell - Dungeon.level!!.width()).start(Speck.factory(Speck.ROCK), 0.07f, 10)
                seen = true
            }

            val ch = Actor.findChar(cell)

            if (ch != null) {
                var damage = Random.NormalIntRange(5 + Dungeon.depth, 10 + Dungeon.depth * 2)
                damage -= ch.drRoll()
                ch.damage(Math.max(damage, 0), this)

                Buff.prolong<Paralysis>(ch, Paralysis::class.java, Paralysis.DURATION)

                if (!ch.isAlive && ch === Dungeon.hero!!) {
                    Dungeon.fail(javaClass)
                    GLog.n(Messages.get(this.javaClass, "ondeath"))
                }
            }
        }

        if (seen) {
            Camera.main!!.shake(3f, 0.7f)
            Sample.INSTANCE.play(Assets.SND_ROCKS)
        }

    }
}
