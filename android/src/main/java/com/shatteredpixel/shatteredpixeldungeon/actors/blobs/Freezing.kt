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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Random

class Freezing : Blob() {

    override fun evolve() {

        val water = Dungeon.level!!.water
        var cell: Int

        val fire = Dungeon.level!!.blobs[Fire::class.java] as Fire?

        for (i in area.left - 1..area.right) {
            for (j in area.top - 1..area.bottom) {
                cell = i + j * Dungeon.level!!.width()
                if (cur!![cell] > 0) {

                    if (fire != null && fire.volume > 0 && fire.cur!![cell] > 0) {
                        fire.clear(cell)
                        cur!![cell] = 0
                        off!![cell] = cur!![cell]
                        continue
                    }

                    val ch = Actor.findChar(cell)
                    if (ch != null && !ch.isImmune(this.javaClass)) {
                        if (ch.buff<Frost>(Frost::class.java) != null) {
                            Buff.affect<Frost>(ch, Frost::class.java, 2f)
                        } else {
                            Buff.affect<Chill>(ch, Chill::class.java, if (water[cell]) 5f else 3f)
                            val chill = ch.buff<Chill>(Chill::class.java)
                            if (chill != null && chill.cooldown() >= 10f) {
                                Buff.affect<Frost>(ch, Frost::class.java, 5f)
                            }
                        }
                    }

                    val heap = Dungeon.level!!.heaps.get(cell)
                    heap?.freeze()

                    off!![cell] = cur!![cell] - 1
                    volume += off!![cell]
                } else {
                    off!![cell] = 0
                }
            }
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(SnowParticle.FACTORY, 0.05f, 0)
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }

    companion object {

        //legacy functionality from before this was a proper blob. Returns true if this cell is visible
        fun affect(cell: Int, fire: Fire?): Boolean {

            val ch = Actor.findChar(cell)
            if (ch != null) {
                if (Dungeon.level!!.water[ch.pos]) {
                    Buff.prolong<Frost>(ch, Frost::class.java, Frost.duration(ch) * Random.Float(5f, 7.5f))
                } else {
                    Buff.prolong<Frost>(ch, Frost::class.java, Frost.duration(ch) * Random.Float(1.0f, 1.5f))
                }
            }

            fire?.clear(cell)

            val heap = Dungeon.level!!.heaps.get(cell)
            heap?.freeze()

            if (Dungeon.level!!.heroFOV[cell]) {
                CellEmitter.get(cell).start(SnowParticle.FACTORY, 0.2f, 6)
                return true
            } else {
                return false
            }
        }
    }
}
