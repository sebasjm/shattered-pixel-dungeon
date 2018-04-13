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

package com.shatteredpixel.shatteredpixeldungeon.items.quest

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class CorpseDust : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        image = ItemSpriteSheet.DUST

        cursed = true
        cursedKnown = true

        unique = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        return ArrayList() //yup, no dropping this one
    }

    override fun doPickUp(hero: Hero): Boolean {
        if (super.doPickUp(hero)) {
            GLog.n(Messages.get(this, "chill"))
            Buff.affect<DustGhostSpawner>(hero, DustGhostSpawner::class.java)
            return true
        }
        return false
    }

    override fun onDetach() {
        val spawner = Dungeon.hero!!.buff<DustGhostSpawner>(DustGhostSpawner::class.java)
        spawner?.dispel()
    }

    class DustGhostSpawner : Buff() {

        internal var spawnPower = 0

        override fun act(): Boolean {
            spawnPower++
            var wraiths = 1 //we include the wraith we're trying to spawn
            for (mob in Dungeon.level!!.mobs) {
                if (mob is Wraith) {
                    wraiths++
                }
            }

            val powerNeeded = Math.min(25, wraiths * wraiths)

            if (powerNeeded <= spawnPower) {
                spawnPower -= powerNeeded
                var pos = 0
                do {
                    pos = Random.Int(Dungeon.level!!.length())
                } while (!Dungeon.level!!.heroFOV[pos] || !Dungeon.level!!.passable[pos] || Actor.findChar(pos) != null)
                Wraith.spawnAt(pos)
                Sample.INSTANCE.play(Assets.SND_CURSED)
            }

            spend(Actor.TICK)
            return true
        }

        fun dispel() {
            detach()
            for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
                if (mob is Wraith) {
                    mob.die(null)
                }
            }
        }

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(SPAWNPOWER, spawnPower)
        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            spawnPower = bundle.getInt(SPAWNPOWER)
        }

        companion object {

            private val SPAWNPOWER = "spawnpower"
        }
    }

}
