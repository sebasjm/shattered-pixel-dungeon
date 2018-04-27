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
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite

class GooWarn : Blob() {

    protected var pos: Int = 0

    //cosmetic blob, used to warn noobs that goo's pump up should, infact, be avoided.

    init {
        //this one needs to act after the Goo
        actPriority = MOB_PRIO - 1
    }

    override fun evolve() {

        var cell: Int

        for (i in area.left until area.right) {
            for (j in area.top until area.bottom) {
                cell = i + j * Dungeon.level!!.width()
                off!![cell] = if (cur!![cell] > 0) cur!![cell] - 1 else 0

                if (off!![cell] > 0) {
                    volume += off!![cell]
                }
            }
        }

    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(GooSprite.GooParticle.FACTORY, 0.03f)
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }
}

