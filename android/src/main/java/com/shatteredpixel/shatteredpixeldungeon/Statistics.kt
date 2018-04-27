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

package com.shatteredpixel.shatteredpixeldungeon

import com.watabou.utils.Bundle

object Statistics {

    var goldCollected: Int = 0
    var deepestFloor: Int = 0
    var enemiesSlain: Int = 0
    var foodEaten: Int = 0
    var potionsCooked: Int = 0
    var piranhasKilled: Int = 0
    var ankhsUsed: Int = 0

    var duration: Float = 0.toFloat()

    var qualifiedForNoKilling = false
    var completedWithNoKilling = false

    var amuletObtained = false

    private val GOLD = "score"
    private val DEEPEST = "maxDepth"
    private val SLAIN = "enemiesSlain"
    private val FOOD = "foodEaten"
    private val ALCHEMY = "potionsCooked"
    private val PIRANHAS = "priranhas"
    private val ANKHS = "ankhsUsed"
    private val DURATION = "duration"
    private val AMULET = "amuletObtained"

    fun reset() {

        goldCollected = 0
        deepestFloor = 0
        enemiesSlain = 0
        foodEaten = 0
        potionsCooked = 0
        piranhasKilled = 0
        ankhsUsed = 0

        duration = 0f

        qualifiedForNoKilling = false

        amuletObtained = false

    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(GOLD, goldCollected)
        bundle.put(DEEPEST, deepestFloor)
        bundle.put(SLAIN, enemiesSlain)
        bundle.put(FOOD, foodEaten)
        bundle.put(ALCHEMY, potionsCooked)
        bundle.put(PIRANHAS, piranhasKilled)
        bundle.put(ANKHS, ankhsUsed)
        bundle.put(DURATION, duration)
        bundle.put(AMULET, amuletObtained)
    }

    fun restoreFromBundle(bundle: Bundle) {
        goldCollected = bundle.getInt(GOLD)
        deepestFloor = bundle.getInt(DEEPEST)
        enemiesSlain = bundle.getInt(SLAIN)
        foodEaten = bundle.getInt(FOOD)
        potionsCooked = bundle.getInt(ALCHEMY)
        piranhasKilled = bundle.getInt(PIRANHAS)
        ankhsUsed = bundle.getInt(ANKHS)
        duration = bundle.getFloat(DURATION)
        amuletObtained = bundle.getBoolean(AMULET)
    }

    fun preview(info: GamesInProgress.Info, bundle: Bundle) {
        info.goldCollected = bundle.getInt(GOLD)
        info.maxDepth = bundle.getInt(DEEPEST)
    }

}
