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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.watabou.noosa.Game
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.FileUtils

import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.UUID

enum class Rankings {

    INSTANCE;

    var records: ArrayList<Record>? = null
    var lastRecord: Int = 0
    var totalNumber: Int = 0
    var wonNumber: Int = 0

    fun submit(win: Boolean, cause: Class<*>) {

        load()

        val rec = Record()

        rec.cause = cause
        rec.win = win
        rec.heroClass = Dungeon.hero!!.heroClass
        rec.armorTier = Dungeon.hero!!.tier()
        rec.herolevel = Dungeon.hero!!.lvl
        rec.depth = Dungeon.depth
        rec.score = score(win)

        INSTANCE.saveGameData(rec)

        rec.gameID = UUID.randomUUID().toString()

        records!!.add(rec)

        Collections.sort(records!!, scoreComparator)

        lastRecord = records!!.indexOf(rec)
        var size = records!!.size
        while (size > TABLE_SIZE) {

            if (lastRecord == size - 1) {
                records!!.removeAt(size - 2)
                lastRecord--
            } else {
                records!!.removeAt(size - 1)
            }

            size = records!!.size
        }

        totalNumber++
        if (win) {
            wonNumber++
        }

        Badges.validateGamesPlayed()

        save()
    }

    private fun score(win: Boolean): Int {
        return (Statistics.goldCollected + Dungeon.hero!!.lvl * (if (win) 26 else Dungeon.depth) * 100) * if (win) 2 else 1
    }

    fun saveGameData(rec: Record) {
        rec.gameData = Bundle()

        val belongings = Dungeon.hero!!.belongings

        //save the hero and belongings
        val allItems = belongings.backpack.items.clone() as ArrayList<Item>
        //remove items that won't show up in the rankings screen
        for (item in belongings.backpack.items.toTypedArray<Item>()) {
            if (item is Bag) {
                for (bagItem in (item as Bag).items.toTypedArray<Item>()) {
                    if (Dungeon.quickslot.contains(bagItem)) belongings.backpack.items.add(bagItem)
                }
                belongings.backpack.items.remove(item)
            } else if (!Dungeon.quickslot.contains(item))
                belongings.backpack.items.remove(item)
        }
        rec.gameData!!.put(HERO, Dungeon.hero!!)

        //save stats
        val stats = Bundle()
        Statistics.storeInBundle(stats)
        rec.gameData!!.put(STATS, stats)

        //save badges
        val badges = Bundle()
        Badges.saveLocal(badges)
        rec.gameData!!.put(BADGES, badges)

        //save handler information
        val handler = Bundle()
        Scroll.saveSelectively(handler, belongings.backpack.items)
        Potion.saveSelectively(handler, belongings.backpack.items)
        //include worn rings
        if (belongings.misc1 != null) belongings.backpack.items.add(belongings!!.misc1!!)
        if (belongings.misc2 != null) belongings.backpack.items.add(belongings!!.misc2!!)
        Ring.saveSelectively(handler, belongings.backpack.items)
        rec.gameData!!.put(HANDLERS, handler)

        //restore items now that we're done saving
        belongings.backpack.items = allItems

        //save challenges
        rec.gameData!!.put(CHALLENGES, Dungeon.challenges)
    }

    fun loadGameData(rec: Record) {
        val data = rec.gameData

        Dungeon.hero = null
        Dungeon.level = null
        Generator.reset()
        Notes.reset()
        Dungeon.quickslot.reset()
        QuickSlotButton.reset()

        val handler = data!!.getBundle(HANDLERS)
        Scroll.restore(handler)
        Potion.restore(handler)
        Ring.restore(handler)

        Badges.loadLocal(data.getBundle(BADGES))

        Dungeon.hero = data.get(HERO) as Hero

        Statistics.restoreFromBundle(data.getBundle(STATS))

        Dungeon.challenges = data.getInt(CHALLENGES)

    }

    fun save() {
        val bundle = Bundle()
        bundle.put(RECORDS, records!!)
        bundle.put(LATEST, lastRecord)
        bundle.put(TOTAL, totalNumber)
        bundle.put(WON, wonNumber)

        try {
            FileUtils.bundleToFile(RANKINGS_FILE, bundle)
        } catch (e: IOException) {
            Game.reportException(e)
        }

    }

    fun load() {

        if (records != null) {
            return
        }

        records = ArrayList()

        try {
            val bundle = FileUtils.bundleFromFile(RANKINGS_FILE)

            for (record in bundle.getCollection(RECORDS)) {
                records!!.add(record as Record)
            }
            lastRecord = bundle.getInt(LATEST)

            totalNumber = bundle.getInt(TOTAL)
            if (totalNumber == 0) {
                totalNumber = records!!.size
            }

            wonNumber = bundle.getInt(WON)
            if (wonNumber == 0) {
                for (rec in records!!) {
                    if (rec.win) {
                        wonNumber++
                    }
                }
            }

        } catch (e: IOException) {
        }

    }

    class Record : Bundlable {

        var cause: Class<*>? = null
        var win: Boolean = false

        var heroClass: HeroClass? = null
        var armorTier: Int = 0
        var herolevel: Int = 0
        var depth: Int = 0

        var gameData: Bundle? = null
        var gameID: String? = null

        var score: Int = 0

        fun desc(): String {
            if (cause == null) {
                return Messages.get(this.javaClass, "something")
            } else {
                val result = Messages.get(cause, "rankings_desc", Messages.get(cause, "name"))
                return if (result.contains("!!!NO TEXT FOUND!!!")) {
                    Messages.get(this.javaClass, "something")
                } else {
                    result
                }
            }
        }

        override fun restoreFromBundle(bundle: Bundle) {

            if (bundle.contains(CAUSE)) {
                cause = bundle.getClass(CAUSE)
            } else {
                cause = null
            }

            win = bundle.getBoolean(WIN)
            score = bundle.getInt(SCORE)

            heroClass = HeroClass.restoreInBundle(bundle)
            armorTier = bundle.getInt(TIER)

            if (bundle.contains(DATA)) gameData = bundle.getBundle(DATA)
            if (bundle.contains(ID)) gameID = bundle.getString(ID)

            if (gameID == null) gameID = UUID.randomUUID().toString()

            depth = bundle.getInt(DEPTH)
            herolevel = bundle.getInt(LEVEL)

        }

        override fun storeInBundle(bundle: Bundle) {

            if (cause != null) bundle.put(CAUSE, cause!!)

            bundle.put(WIN, win)
            bundle.put(SCORE, score)

            heroClass!!.storeInBundle(bundle)
            bundle.put(TIER, armorTier)
            bundle.put(LEVEL, herolevel)
            bundle.put(DEPTH, depth)

            if (gameData != null) bundle.put(DATA, gameData!!)
            bundle.put(ID, gameID!!)
        }

        companion object {

            private val CAUSE = "cause"
            private val WIN = "win"
            private val SCORE = "score"
            private val TIER = "tier"
            private val LEVEL = "level"
            private val DEPTH = "depth"
            private val DATA = "gameData"
            private val ID = "gameID"
        }
    }

    companion object {

        val TABLE_SIZE = 11

        val RANKINGS_FILE = "rankings.dat"

        val HERO = "hero"
        val STATS = "stats"
        val BADGES = "badges"
        val HANDLERS = "handlers"
        val CHALLENGES = "challenges"

        private val RECORDS = "records"
        private val LATEST = "latest"
        private val TOTAL = "total"
        private val WON = "won"

        private val scoreComparator = Comparator<Record> { lhs, rhs -> Math.signum((rhs.score - lhs.score).toFloat()).toInt() }
    }
}
