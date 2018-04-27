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

package com.shatteredpixel.shatteredpixeldungeon.items.potions

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PathFinder

import java.util.ArrayList

class PotionOfPurity : Potion() {

    init {
        initials = 9

        affectedBlobs = ArrayList<Class<*>>(BlobImmunity().immunities())
    }

    override fun shatter(cell: Int) {

        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level!!.solid, null), DISTANCE)

        val blobs = ArrayList<Blob>()
        for (c in affectedBlobs!!) {
            val b = Dungeon.level!!.blobs.get(c)
            if (b != null && b.volume > 0) {
                blobs.add(b)
            }
        }

        for (i in 0 until Dungeon.level!!.length()) {
            if (PathFinder.distance!![i] < Integer.MAX_VALUE) {

                for (blob in blobs) {

                    val value = blob.cur!![i]
                    if (value > 0) {

                        blob.clear(i)
                        blob.cur!![i] = 0
                        blob.volume -= value

                    }

                }

                if (Dungeon.level!!.heroFOV[i]) {
                    CellEmitter.get(i).burst(Speck.factory(Speck.DISCOVER), 2)
                }

            }
        }


        if (Dungeon.level!!.heroFOV[cell]) {
            splash(cell)
            Sample.INSTANCE.play(Assets.SND_SHATTER)

            setKnown()
            GLog.i(Messages.get(this.javaClass, "freshness"))
        }

    }

    override fun apply(hero: Hero) {
        GLog.w(Messages.get(this.javaClass, "protected"))
        Buff.prolong<BlobImmunity>(hero, BlobImmunity::class.java, BlobImmunity.DURATION)
        setKnown()
    }

    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }

    companion object {

        private val DISTANCE = 3

        private var affectedBlobs: ArrayList<Class<*>>? = null
    }
}
