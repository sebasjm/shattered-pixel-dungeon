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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WebParticle
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages

class Web : Blob() {

    override fun evolve() {

        var cell: Int

        for (i in area.left until area.right) {
            for (j in area.top until area.bottom) {
                cell = i + j * Dungeon.level!!.width()
                off[cell] = if (cur!![cell] > 0) cur!![cell] - 1 else 0

                if (off!![cell] > 0) {

                    volume += off!![cell]

                    val ch = Actor.findChar(cell)
                    if (ch != null && !ch.isImmune(this.javaClass)) {
                        Buff.prolong<Roots>(ch, Roots::class.java, Actor.TICK)
                    }
                }
            }
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)

        emitter.pour(WebParticle.FACTORY, 0.4f)
    }

    override fun tileDesc(): String? {
        return Messages.get(this, "desc")
    }
}
