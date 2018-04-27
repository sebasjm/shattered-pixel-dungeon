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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class Electricity : Blob() {

    private var water: BooleanArray? = null

    init {
        //acts after mobs, to give them a chance to resist paralysis
        actPriority = MOB_PRIO - 1
    }

    override fun evolve() {

        water = Dungeon.level!!.water
        var cell: Int

        //spread first..
        for (i in area.left - 1..area.right) {
            for (j in area.top - 1..area.bottom) {
                cell = i + j * Dungeon.level!!.width()

                if (cur!![cell] > 0) {
                    spreadFromCell(cell, cur!![cell])
                }
            }
        }

        //..then decrement/shock
        for (i in area.left - 1..area.right) {
            for (j in area.top - 1..area.bottom) {
                cell = i + j * Dungeon.level!!.width()
                if (cur!![cell] > 0) {
                    val ch = Actor.findChar(cell)
                    if (ch != null && !ch.isImmune(this.javaClass)) {
                        Buff.prolong<Paralysis>(ch, Paralysis::class.java, 1f)
                        if (cur!![cell] % 2 == 1) {
                            ch.damage(Math.round(Random.Float(2 + Dungeon.depth / 5f)), this)
                        }
                    }

                    val h = Dungeon.level!!.heaps.get(cell)
                    if (h != null) {
                        val toShock = h.peek()
                        if (toShock is Wand) {
                            toShock.gainCharge(0.333f)
                        } else if (toShock is MagesStaff) {
                            toShock.gainCharge(0.333f)
                        }
                    }

                    off!![cell] = cur!![cell] - 1
                    volume += off!![cell]
                } else {
                    off!![cell] = 0
                }
            }
        }

    }

    private fun spreadFromCell(cell: Int, power: Int) {
        if (cur!![cell] == 0) {
            area.union(cell % Dungeon.level!!.width(), cell / Dungeon.level!!.width())
        }
        cur!![cell] = Math.max(cur!![cell], power)

        for (c in PathFinder.NEIGHBOURS4!!) {
            if (water!![cell + c] && cur!![cell + c] < power) {
                spreadFromCell(cell + c, power)
            }
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(SparkParticle.FACTORY, 0.05f, 0)
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }

}
