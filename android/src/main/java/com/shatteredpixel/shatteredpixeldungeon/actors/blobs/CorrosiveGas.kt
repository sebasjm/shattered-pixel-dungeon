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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Bundle

class CorrosiveGas : Blob() {

    private var strength = 0

    override fun evolve() {
        super.evolve()

        if (volume == 0) {
            strength = 0
        } else {
            var ch: Char?
            var cell: Int

            for (i in area.left until area.right) {
                for (j in area.top until area.bottom) {
                    cell = i + j * Dungeon.level!!.width()
                    if (cur!![cell] > 0) {
                        ch = Actor.findChar(cell)
                        if (ch != null) if (!ch!!.isImmune(this.javaClass))
                            Buff.affect<Corrosion>(ch, Corrosion::class.java)!!.set(2f, strength)
                    }
                }
            }
        }
    }

    fun setStrength(str: Int) {
        if (str > strength)
            strength = str
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        strength = bundle.getInt(STRENGTH)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STRENGTH, strength)
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)

        emitter.pour(Speck.factory(Speck.CORROSION), 0.4f)
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }

    companion object {

        private val STRENGTH = "strength"
    }
}
