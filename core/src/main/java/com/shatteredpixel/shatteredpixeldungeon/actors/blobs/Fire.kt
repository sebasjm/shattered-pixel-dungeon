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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene

class Fire : Blob() {

    override fun evolve() {

        val flamable = Dungeon.level!!.flamable
        var cell: Int
        var fire: Int

        val freeze = Dungeon.level!!.blobs[Freezing::class.java] as Freezing

        var observe = false

        for (i in area.left - 1..area.right) {
            for (j in area.top - 1..area.bottom) {
                cell = i + j * Dungeon.level!!.width()
                if (cur!![cell] > 0) {

                    if (freeze != null && freeze.volume > 0 && freeze.cur!![cell] > 0) {
                        freeze.clear(cell)
                        cur[cell] = 0
                        off[cell] = cur[cell]
                        continue
                    }

                    burn(cell)

                    fire = cur!![cell] - 1
                    if (fire <= 0 && flamable[cell]) {

                        Dungeon.level!!.destroy(cell)

                        observe = true
                        GameScene.updateMap(cell)

                    }

                } else if (freeze == null || freeze.volume <= 0 || freeze.cur!![cell] <= 0) {

                    if (flamable[cell] && (cur!![cell - 1] > 0
                                    || cur!![cell + 1] > 0
                                    || cur!![cell - Dungeon.level!!.width()] > 0
                                    || cur!![cell + Dungeon.level!!.width()] > 0)) {
                        fire = 4
                        burn(cell)
                        area.union(i, j)
                    } else {
                        fire = 0
                    }

                } else {
                    fire = 0
                }

                volume += (off[cell] = fire)
            }
        }

        if (observe) {
            Dungeon.observe()
        }
    }

    private fun burn(pos: Int) {
        val ch = Actor.findChar(pos)
        if (ch != null && !ch.isImmune(this.javaClass)) {
            Buff.affect<Burning>(ch, Burning::class.java)!!.reignite(ch)
        }

        val heap = Dungeon.level!!.heaps.get(pos)
        heap?.burn()

        val plant = Dungeon.level!!.plants.get(pos)
        plant?.wither()
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(FlameParticle.FACTORY, 0.03f, 0)
    }

    override fun tileDesc(): String? {
        return Messages.get(this, "desc")
    }
}
