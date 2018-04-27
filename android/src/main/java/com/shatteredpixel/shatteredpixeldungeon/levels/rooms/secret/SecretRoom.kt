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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom
import com.watabou.noosa.Game
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Arrays


abstract class SecretRoom : SpecialRoom() {
    companion object {


        private val ALL_SECRETS = ArrayList(Arrays.asList<Class<out SecretRoom>>(
                SecretGardenRoom::class.java, SecretLaboratoryRoom::class.java, SecretLibraryRoom::class.java,
                SecretLarderRoom::class.java, SecretWellRoom::class.java, SecretRunestoneRoom::class.java,
                SecretArtilleryRoom::class.java, SecretChestChasmRoom::class.java, SecretHoneypotRoom::class.java,
                SecretHoardRoom::class.java, SecretMazeRoom::class.java, SecretSummoningRoom::class.java))

        var runSecrets = ArrayList<Class<out SecretRoom>>()

        //this is the number of secret rooms per region (whole value),
        // plus the chance for an extra secret room (fractional value)
        private val baseRegionSecrets = floatArrayOf(1.4f, 1.8f, 2.2f, 2.6f, 3.0f)
        private var regionSecretsThisRun: IntArray? = IntArray(5)

        fun initForRun() {

            val regionChances = baseRegionSecrets.clone()

            if (GamesInProgress.selectedClass == HeroClass.ROGUE) {
                for (i in regionChances.indices) {
                    regionChances[i] += 0.6f
                }
            }

            for (i in regionSecretsThisRun!!.indices) {
                regionSecretsThisRun!![i] = regionChances[i].toInt()
                if (Random.Float() < regionChances[i] % 1f) {
                    regionSecretsThisRun!![i]++
                }
            }

            runSecrets = ArrayList(ALL_SECRETS)
            Random.shuffle(runSecrets)

        }

        fun secretsForFloor(depth: Int): Int {
            if (depth == 1) return 0

            val region = depth / 5
            val floor = depth % 5

            val floorsLeft = 5 - floor

            var secrets: Float
            if (floorsLeft == 0) {
                secrets = regionSecretsThisRun!![region].toFloat()
            } else {
                secrets = (regionSecretsThisRun!![region] / floorsLeft).toFloat()
                if (Random.Float() < secrets % 1f) {
                    secrets = Math.ceil(secrets.toDouble()).toFloat()
                } else {
                    secrets = Math.floor(secrets.toDouble()).toFloat()
                }
            }

            regionSecretsThisRun!![region] -= secrets.toInt()
            return secrets.toInt()
        }

        fun createRoom(): SecretRoom {

            var r: SecretRoom? = null
            var index = runSecrets.size
            for (i in 0..3) {
                val newidx = Random.Int(runSecrets.size)
                if (newidx < index) index = newidx
            }
            try {
                r = runSecrets[index].newInstance()
            } catch (e: Exception) {
                Game.reportException(e)
            }

            runSecrets.add(runSecrets.removeAt(index))

            return r!!
        }

        private val ROOMS = "secret_rooms"
        private val REGIONS = "region_secrets"

        fun restoreRoomsFromBundle(bundle: Bundle) {
            runSecrets.clear()
            if (bundle.contains(ROOMS)) {
                for (type in bundle.getClassArray(ROOMS)!!) {
                    if (type != null) runSecrets.add(type as Class<out SecretRoom>)
                }
                regionSecretsThisRun = bundle.getIntArray(REGIONS)
            } else {
                initForRun()
                Game.reportException(Exception("secrets array didn't exist!"))
            }
        }

        fun storeRoomsInBundle(bundle: Bundle) {
            bundle.put(ROOMS, runSecrets.toTypedArray<Class<*>>())
            bundle.put(REGIONS, regionSecretsThisRun!!)
        }
    }

}
