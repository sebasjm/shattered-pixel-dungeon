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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Bundle
import com.watabou.utils.FileUtils

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashMap

object GamesInProgress {

    val MAX_SLOTS = 4

    //null means we have loaded info and it is empty, no entry means unknown.
    private val slotStates = HashMap<Int, Info?>()
    var curSlot: Int = 0

    var selectedClass: HeroClass? = null

    private val GAME_FOLDER = "game%d"
    private val GAME_FILE = "game.dat"
    private val DEPTH_FILE = "depth%d.dat"

    val scoreComparator: Comparator<GamesInProgress.Info> = Comparator { lhs, rhs ->
        val lScore = lhs.level * lhs.maxDepth * 100 + lhs.goldCollected
        val rScore = rhs.level * rhs.maxDepth * 100 + rhs.goldCollected
        Math.signum((rScore - lScore).toFloat()).toInt()
    }

    fun gameExists(slot: Int): Boolean {
        return FileUtils.dirExists(Messages.format(GAME_FOLDER, slot))
    }

    fun gameFolder(slot: Int): File? {
        return FileUtils.getDir(Messages.format(GAME_FOLDER, slot))
    }

    fun gameFile(slot: Int): File? {
        return FileUtils.getFile(gameFolder(slot)!!, GAME_FILE)
    }

    fun depthFile(slot: Int, depth: Int): File? {
        return FileUtils.getFile(gameFolder(slot)!!, Messages.format(DEPTH_FILE, depth))
    }

    fun firstEmpty(): Int {
        for (i in 1..MAX_SLOTS) {
            if (check(i) == null) return i
        }
        return -1
    }

    fun checkAll(): ArrayList<Info> {
        val result = ArrayList<Info>()
        for (i in 0..MAX_SLOTS) {
            val curr = check(i)
            if (curr != null) result.add(curr)
        }
        Collections.sort(result, scoreComparator)
        return result
    }

    fun check(slot: Int): Info? {

        if (slotStates.containsKey(slot)) {

            return slotStates[slot]

        } else if (!gameExists(slot)) {

            slotStates[slot] = null
            return null

        } else {

            var info: Info?
            try {

                val bundle = FileUtils.bundleFromFile(gameFile(slot)!!)
                info = Info()
                info.slot = slot
                Dungeon.preview(info, bundle)

                //saves from before 0.5.0b are not supported
                if (info.version < ShatteredPixelDungeon.v0_5_0b) {
                    info = null
                }

            } catch (e: IOException) {
                info = null
            }

            slotStates[slot] = info
            return info

        }
    }

    operator fun set(slot: Int, depth: Int, challenges: Int,
                     hero: Hero) {
        val info = Info()
        info.slot = slot

        info.depth = depth
        info.challenges = challenges

        info.level = hero.lvl
        info.str = hero.STR
        info.exp = hero.exp
        info.hp = hero.HP
        info.ht = hero.HT
        info.shld = hero.SHLD
        info.heroClass = hero.heroClass
        info.subClass = hero.subClass
        info.armorTier = hero.tier()

        info.goldCollected = Statistics.goldCollected
        info.maxDepth = Statistics.deepestFloor

        slotStates[slot] = info
    }

    fun setUnknown(slot: Int) {
        slotStates.remove(slot)
    }

    fun delete(slot: Int) {
        slotStates[slot] = null
    }

    class Info {
        var slot: Int = 0

        var depth: Int = 0
        var version: Int = 0
        var challenges: Int = 0

        var level: Int = 0
        var str: Int = 0
        var exp: Int = 0
        var hp: Int = 0
        var ht: Int = 0
        var shld: Int = 0
        var heroClass: HeroClass? = null
        var subClass: HeroSubClass? = null
        var armorTier: Int = 0

        var goldCollected: Int = 0
        var maxDepth: Int = 0
    }
}
