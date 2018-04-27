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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class ScrollOfMirrorImage : Scroll() {

    init {
        initials = 4
    }

    override fun doRead() {
        val spawnedImages = spawnImages(Item.curUser!!, NIMAGES)

        if (spawnedImages > 0) {
            setKnown()
        }

        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()

        readAnimation()
    }

    override fun empoweredRead() {
        //spawns 2 images right away, delays 4 of them, 6 total.
        DelayedImageSpawner(6 - spawnImages(Item.curUser!!, 2), 2, 3f).attachTo(Item.curUser!!)

        setKnown()

        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()

        readAnimation()
    }

    class DelayedImageSpawner @JvmOverloads constructor(private var totImages: Int = NIMAGES, private var imPerRound: Int = NIMAGES, private var delay: Float = 1f) : Buff() {

        override fun attachTo(target: Char): Boolean {
            if (super.attachTo(target)) {
                spend(delay)
                return true
            } else {
                return false
            }
        }

        override fun act(): Boolean {

            val spawned = spawnImages(target!! as Hero, Math.min(totImages, imPerRound))

            totImages -= spawned

            if (totImages < 0) {
                detach()
            } else {
                spend(delay)
            }

            return true
        }

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(TOTAL, totImages)
            bundle.put(PER_ROUND, imPerRound)
            bundle.put(DELAY, delay)
        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            totImages = bundle.getInt(TOTAL)
            imPerRound = bundle.getInt(PER_ROUND)
            delay = bundle.getFloat(DELAY)
        }

        companion object {

            private val TOTAL = "images"
            private val PER_ROUND = "per_round"
            private val DELAY = "delay"
        }
    }

    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }

    companion object {

        private val NIMAGES = 3

        //returns the number of images spawned
        fun spawnImages(hero: Hero?, nImages: Int): Int {
            var nImages = nImages

            val respawnPoints = ArrayList<Int>()

            for (i in PathFinder.NEIGHBOURS8!!.indices) {
                val p = hero!!.pos + PathFinder.NEIGHBOURS8!![i]
                if (Actor.findChar(p) == null && (Dungeon.level!!.passable[p] || Dungeon.level!!.avoid[p])) {
                    respawnPoints.add(p)
                }
            }

            var spawned = 0
            while (nImages > 0 && respawnPoints.size > 0) {
                val index = Random.index(respawnPoints)

                val mob = MirrorImage()
                mob.duplicate(hero!!)
                GameScene.add(mob)
                ScrollOfTeleportation.appear(mob, respawnPoints[index])

                respawnPoints.removeAt(index)
                nImages--
                spawned++
            }

            return spawned
        }
    }
}
