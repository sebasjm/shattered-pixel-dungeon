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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.LastShopLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed
import com.shatteredpixel.shatteredpixeldungeon.windows.WndAlchemy
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect
import com.watabou.noosa.Game
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.FileUtils
import com.watabou.utils.PathFinder
import com.watabou.utils.Random
import com.watabou.utils.SparseArray

import java.io.IOException
import java.util.ArrayList
import java.util.HashSet

object Dungeon {

    var challenges: Int = 0

    var hero: Hero? = null
    var level: Level? = null

    var quickslot = QuickSlot()

    var depth: Int = 0
    var gold: Int = 0

    var chapters: HashSet<Int> = HashSet()

    var droppedItems: SparseArray<ArrayList<Item>> = SparseArray()

    var version: Int = 0

    var seed: Long = 0

    private val VERSION = "version"
    private val SEED = "seed"
    private val CHALLENGES = "challenges"
    private val HERO = "hero"
    private val GOLD = "gold"
    private val DEPTH = "depth"
    private val DROPPED = "dropped%d"
    private val LEVEL = "level"
    private val LIMDROPS = "limited_drops"
    private val CHAPTERS = "chapters"
    private val QUESTS = "quests"
    private val BADGES = "badges"

    //we store this to avoid having to re-allocate the array with each pathfind
    private var passable: BooleanArray? = null

    //enum of items which have limited spawns, records how many have spawned
    //could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
    enum class LimitedDrops {
        //limited world drops
        STRENGTH_POTIONS,
        UPGRADE_SCROLLS,
        ARCANE_STYLI,

        //Health potion sources
        //enemies
        SWARM_HP,
        GUARD_HP,
        BAT_HP,
        WARLOCK_HP,
        SCORPIO_HP,
        //alchemy
        COOKING_HP,
        BLANDFRUIT_SEED,

        //doesn't use Generator, so we have to enforce one armband drop here
        THIEVES_ARMBAND,

        //containers
        DEW_VIAL,
        VELVET_POUCH,
        SCROLL_HOLDER,
        POTION_BANDOLIER,
        MAGICAL_HOLSTER;

        var count = 0

        //for items which can only be dropped once, should directly access count otherwise.
        fun dropped(): Boolean {
            return count != 0
        }

        fun drop() {
            count = 1
        }

        companion object {

            fun reset() {
                for (lim in values()) {
                    lim.count = 0
                }
            }

            fun store(bundle: Bundle) {
                for (lim in values()) {
                    bundle.put(lim.name, lim.count)
                }
            }

            fun restore(bundle: Bundle) {
                for (lim in values()) {
                    if (bundle.contains(lim.name)) {
                        lim.count = bundle.getInt(lim.name)
                    } else {
                        lim.count = 0
                    }

                }
                //saves prior to 0.6.4
                if (bundle.contains("SEED_POUCH")) {
                    LimitedDrops.VELVET_POUCH.count = bundle.getInt("SEED_POUCH")
                }
                if (bundle.contains("WAND_HOLSTER")) {
                    LimitedDrops.MAGICAL_HOLSTER.count = bundle.getInt("WAND_HOLSTER")
                }
            }

            //for saves prior to 0.6.1
            fun legacyRestore(counts: IntArray) {
                STRENGTH_POTIONS.count = counts[0]
                UPGRADE_SCROLLS.count = counts[1]
                ARCANE_STYLI.count = counts[2]
                SWARM_HP.count = counts[3]
                BAT_HP.count = counts[4]
                WARLOCK_HP.count = counts[5]
                SCORPIO_HP.count = counts[6]
                COOKING_HP.count = counts[7]
                BLANDFRUIT_SEED.count = counts[8]
                THIEVES_ARMBAND.count = counts[9]
                DEW_VIAL.count = counts[10]
                VELVET_POUCH.count = counts[11]
                SCROLL_HOLDER.count = counts[12]
                POTION_BANDOLIER.count = counts[13]
                MAGICAL_HOLSTER.count = counts[14]
                GUARD_HP.count = counts[15]
            }
        }

    }

    fun init() {

        version = Game.versionCode
        challenges = SPDSettings.challenges()

        seed = DungeonSeed.randomSeed()

        Actor.clear()
        Actor.resetNextID()

        Random.seed(seed)

        Scroll.initLabels()
        Potion.initColors()
        Ring.initGems()

        SpecialRoom.initForRun()
        SecretRoom.initForRun()

        Random.seed()

        Statistics.reset()
        Notes.reset()

        quickslot.reset()
        QuickSlotButton.reset()

        depth = 0
        gold = 0

        droppedItems = SparseArray()

        for (a in LimitedDrops.values())
            a.count = 0

        chapters = HashSet()

        Ghost.Quest.reset()
        Wandmaker.Quest.reset()
        Blacksmith.Quest.reset()
        Imp.Quest.reset()

        Generator.reset()
        Generator.initArtifacts()
        hero = Hero()
        hero!!.live()

        Badges.reset()

        GamesInProgress.selectedClass!!.initHero(hero!!)
    }

    fun isChallenged(mask: Int): Boolean {
        return challenges and mask != 0
    }

    fun newLevel(): Level {

        Dungeon.level = null
        Actor.clear()

        depth++
        if (depth > Statistics.deepestFloor) {
            Statistics.deepestFloor = depth

            Statistics.completedWithNoKilling = Statistics.qualifiedForNoKilling
        }

        val level: Level
        when (depth) {
            1, 2, 3, 4 -> level = SewerLevel()
            5 -> level = SewerBossLevel()
            6, 7, 8, 9 -> level = PrisonLevel()
            10 -> level = PrisonBossLevel()
            11, 12, 13, 14 -> level = CavesLevel()
            15 -> level = CavesBossLevel()
            16, 17, 18, 19 -> level = CityLevel()
            20 -> level = CityBossLevel()
            21 -> level = LastShopLevel()
            22, 23, 24 -> level = HallsLevel()
            25 -> level = HallsBossLevel()
            26 -> level = LastLevel()
            else -> {
                level = DeadEndLevel()
                Statistics.deepestFloor--
            }
        }

        level.create()

        Statistics.qualifiedForNoKilling = !bossLevel()

        return level
    }

    fun resetLevel() {

        Actor.clear()

        level!!.reset()
        switchLevel(level!!, level!!.entrance)
    }

    fun seedCurDepth(): Long {
        return seedForDepth(depth)
    }

    fun seedForDepth(depth: Int): Long {
        Random.seed(seed)
        for (i in 0 until depth)
            Random.Long() //we don't care about these values, just need to go through them
        val result = Random.Long()
        Random.seed()
        return result
    }

    fun shopOnLevel(): Boolean {
        return depth == 6 || depth == 11 || depth == 16
    }

    @JvmOverloads
    fun bossLevel(depth: Int = this.depth): Boolean {
        return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25
    }

    fun switchLevel(level: Level, pos: Int) {
        var pos = pos

        if (pos < 0 || pos >= level.length()) {
            pos = level.exit
        }

        PathFinder.setMapSize(level.width(), level.height())

        Dungeon.level = level
        DriedRose.restoreGhostHero(level, pos)
        Actor.init()

        val respawner = level.respawner()
        if (respawner != null) {
            Actor.add(level.respawner())
        }

        hero!!.pos = pos

        val light = hero!!.buff<Light>(Light::class.java)
        hero!!.viewDistance = if (light == null) level.viewDistance else Math.max(Light.DISTANCE, level.viewDistance)

        hero!!.lastAction = null
        hero!!.curAction = hero!!.lastAction

        observe()
        try {
            saveAll()
        } catch (e: IOException) {
            Game.reportException(e)
            /*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
			But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
        }

    }

    fun dropToChasm(item: Item) {
        val depth = Dungeon.depth + 1
        var dropped: ArrayList<Item>? = Dungeon.droppedItems.get(depth) as ArrayList<Item>?
        if (dropped == null) {
            dropped = ArrayList<Item>()
            Dungeon.droppedItems.put(depth, dropped)
        }
        dropped!!.add(item)
    }

    fun posNeeded(): Boolean {
        //2 POS each floor set
        val posLeftThisSet = 2 - (LimitedDrops.STRENGTH_POTIONS.count - depth / 5 * 2)
        if (posLeftThisSet <= 0) return false

        val floorThisSet = depth % 5

        //pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
        var targetPOSLeft = 2 - floorThisSet / 2
        if (floorThisSet % 2 == 1 && Random.Int(2) == 0) targetPOSLeft--

        return (targetPOSLeft < posLeftThisSet)

    }

    fun souNeeded(): Boolean {
        val souLeftThisSet: Int
        //3 SOU each floor set, 1.5 (rounded) on forbidden runes challenge
        if (isChallenged(Challenges.NO_SCROLLS)) {
            souLeftThisSet = Math.round(1.5f - (LimitedDrops.UPGRADE_SCROLLS.count - depth / 5 * 1.5f))
        } else {
            souLeftThisSet = 3 - (LimitedDrops.UPGRADE_SCROLLS.count - depth / 5 * 3)
        }
        if (souLeftThisSet <= 0) return false

        val floorThisSet = depth % 5
        //chance is floors left / scrolls left
        return Random.Int(5 - floorThisSet) < souLeftThisSet
    }

    fun asNeeded(): Boolean {
        //1 AS each floor set
        val asLeftThisSet = 1 - (LimitedDrops.ARCANE_STYLI.count - depth / 5)
        if (asLeftThisSet <= 0) return false

        val floorThisSet = depth % 5
        //chance is floors left / scrolls left
        return Random.Int(5 - floorThisSet) < asLeftThisSet
    }

    @Throws(IOException::class)
    fun saveGame(save: Int) {
        try {
            val bundle = Bundle()

            version = Game.versionCode
            bundle.put(VERSION, version)
            bundle.put(SEED, seed)
            bundle.put(CHALLENGES, challenges)
            bundle.put(HERO, hero)
            bundle.put(GOLD, gold)
            bundle.put(DEPTH, depth)

            for (d in droppedItems.keyArray()) {
                bundle.put(Messages.format(DROPPED, d), droppedItems.get(d))
            }

            quickslot.storePlaceholders(bundle)

            val limDrops = Bundle()
            LimitedDrops.store(limDrops)
            bundle.put(LIMDROPS, limDrops)

            var count = 0
            val ids = IntArray(chapters.size)
            for (id in chapters) {
                ids[count++] = id
            }
            bundle.put(CHAPTERS, ids)

            val quests = Bundle()
            Ghost.Quest.storeInBundle(quests)
            Wandmaker.Quest.storeInBundle(quests)
            Blacksmith.Quest.storeInBundle(quests)
            Imp.Quest.storeInBundle(quests)
            bundle.put(QUESTS, quests)

            SpecialRoom.storeRoomsInBundle(bundle)
            SecretRoom.storeRoomsInBundle(bundle)

            WndAlchemy.storeInBundle(bundle)

            Statistics.storeInBundle(bundle)
            Notes.storeInBundle(bundle)
            Generator.storeInBundle(bundle)

            Scroll.save(bundle)
            Potion.save(bundle)
            Ring.save(bundle)

            Actor.storeNextID(bundle)

            val badges = Bundle()
            Badges.saveLocal(badges)
            bundle.put(BADGES, badges)

            FileUtils.bundleToFile(GamesInProgress.gameFile(save)!!, bundle)

        } catch (e: IOException) {
            GamesInProgress.setUnknown(save)
            Game.reportException(e)
        }

    }

    @Throws(IOException::class)
    fun saveLevel(save: Int) {
        val bundle = Bundle()
        bundle.put(LEVEL, level)

        FileUtils.bundleToFile(GamesInProgress.depthFile(save, depth)!!, bundle)
    }

    @Throws(IOException::class)
    fun saveAll() {
        if (hero != null && hero!!.isAlive) {

            Actor.fixTime()
            saveGame(GamesInProgress.curSlot)
            saveLevel(GamesInProgress.curSlot)

            GamesInProgress.set(GamesInProgress.curSlot, depth, challenges, hero!!)

        } else if (WndResurrect.instance != null) {

            WndResurrect.instance!!.hide()
            Hero.reallyDie(WndResurrect.causeOfDeath!!)

        }
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun loadGame(save: Int, fullLoad: Boolean = true) {

        val bundle = FileUtils.bundleFromFile(GamesInProgress.gameFile(save)!!)

        version = bundle.getInt(VERSION)

        seed = if (bundle.contains(SEED)) bundle.getLong(SEED) else DungeonSeed.randomSeed()

        Actor.restoreNextID(bundle)

        quickslot.reset()
        QuickSlotButton.reset()

        Dungeon.challenges = bundle.getInt(CHALLENGES)

        Dungeon.level = null
        Dungeon.depth = -1

        Scroll.restore(bundle)
        Potion.restore(bundle)
        Ring.restore(bundle)

        quickslot.restorePlaceholders(bundle)

        if (fullLoad) {

            //pre-0.6.1
            if (bundle.contains("limiteddrops")) {
                LimitedDrops.legacyRestore(bundle.getIntArray("limiteddrops")!!)
            } else {
                LimitedDrops.restore(bundle.getBundle(LIMDROPS))
            }

            chapters = HashSet()
            val ids = bundle.getIntArray(CHAPTERS)
            if (ids != null) {
                for (id in ids) {
                    chapters.add(id)
                }
            }

            val quests = bundle.getBundle(QUESTS)
            if (!quests.isNull) {
                Ghost.Quest.restoreFromBundle(quests)
                Wandmaker.Quest.restoreFromBundle(quests)
                Blacksmith.Quest.restoreFromBundle(quests)
                Imp.Quest.restoreFromBundle(quests)
            } else {
                Ghost.Quest.reset()
                Wandmaker.Quest.reset()
                Blacksmith.Quest.reset()
                Imp.Quest.reset()
            }

            SpecialRoom.restoreRoomsFromBundle(bundle)
            SecretRoom.restoreRoomsFromBundle(bundle)
        }

        val badges = bundle.getBundle(BADGES)
        if (!badges.isNull) {
            Badges.loadLocal(badges)
        } else {
            Badges.reset()
        }

        Notes.restoreFromBundle(bundle)

        hero = null
        hero = bundle.get(HERO) as Hero?

        WndAlchemy.restoreFromBundle(bundle, hero!!)

        gold = bundle.getInt(GOLD)
        depth = bundle.getInt(DEPTH)

        Statistics.restoreFromBundle(bundle)
        Generator.restoreFromBundle(bundle)

        droppedItems = SparseArray()
        for (i in 2..Statistics.deepestFloor + 1) {
            val dropped = ArrayList<Item>()
            if (bundle.contains(Messages.format(DROPPED, i)))
                for (b in bundle.getCollection(Messages.format(DROPPED, i))) {
                    dropped.add(b as Item)
                }
            if (!dropped.isEmpty()) {
                droppedItems.put(i, dropped)
            }
        }
    }

    @Throws(IOException::class)
    fun loadLevel(save: Int): Level {

        Dungeon.level = null
        Actor.clear()

        val bundle = FileUtils.bundleFromFile(GamesInProgress.depthFile(save, depth)!!)

        val level = bundle.get(LEVEL) as Level?

        return level ?: throw IOException()
    }

    fun deleteGame(save: Int, deleteLevels: Boolean) {

        FileUtils.deleteFile(GamesInProgress.gameFile(save)!!)

        if (deleteLevels) {
            FileUtils.deleteDir(GamesInProgress.gameFolder(save))
        }

        GamesInProgress.delete(save)
    }

    fun preview(info: GamesInProgress.Info, bundle: Bundle) {
        info.depth = bundle.getInt(DEPTH)
        info.version = bundle.getInt(VERSION)
        info.challenges = bundle.getInt(CHALLENGES)
        Hero.preview(info, bundle.getBundle(HERO))
        Statistics.preview(info, bundle)
    }

    fun fail(cause: Class<*>) {
        if (hero!!.belongings.getItem<Ankh>(Ankh::class.java) == null) {
            Rankings.INSTANCE.submit(false, cause)
        }
    }

    fun win(cause: Class<*>) {

        hero!!.belongings.identify()

        if (challenges != 0) {
            Badges.validateChampion()
        }

        Rankings.INSTANCE.submit(true, cause)
    }

    @JvmOverloads
    fun observe(dist: Int = hero!!.viewDistance + 1) {

        if (level == null) {
            return
        }

        level!!.updateFieldOfView(hero!!, level!!.heroFOV)

        val x = hero!!.pos % level!!.width()
        val y = hero!!.pos / level!!.width()

        //left, right, top, bottom
        val l = Math.max(0, x - dist)
        val r = Math.min(x + dist, level!!.width() - 1)
        val t = Math.max(0, y - dist)
        val b = Math.min(y + dist, level!!.height() - 1)

        val width = r - l + 1
        val height = b - t + 1

        var pos = l + t * level!!.width()

        for (i in t..b) {
            BArray.or(level!!.visited!!, level!!.heroFOV!!, pos, width, level!!.visited)
            pos += level!!.width()
        }

        GameScene.updateFog(l, t, width, height)

        if (hero!!.buff<MindVision>(MindVision::class.java) != null) {
            for (m in level!!.mobs.toTypedArray<Mob>()) {
                BArray.or(level!!.visited!!, level!!.heroFOV, m.pos - 1 - level!!.width(), 3, level!!.visited)
                BArray.or(level!!.visited!!, level!!.heroFOV, m.pos, 3, level!!.visited)
                BArray.or(level!!.visited!!, level!!.heroFOV, m.pos - 1 + level!!.width(), 3, level!!.visited)
                //updates adjacent cells too
                GameScene.updateFog(m.pos, 2)
            }
        }

        if (hero!!.buff<Awareness>(Awareness::class.java) != null) {
            for (h in level!!.heaps.values().filterNotNull()) {
                BArray.or(level!!.visited!!, level!!.heroFOV, h.pos - 1 - level!!.width(), 3, level!!.visited)
                BArray.or(level!!.visited!!, level!!.heroFOV, h.pos - 1, 3, level!!.visited)
                BArray.or(level!!.visited!!, level!!.heroFOV, h.pos - 1 + level!!.width(), 3, level!!.visited)
                GameScene.updateFog(h.pos, 2)
            }
        }

        GameScene.afterObserve()
    }

    private fun setupPassable() {
        if (passable == null || passable!!.size != Dungeon.level!!.length())
            passable = BooleanArray(Dungeon.level!!.length())
        else
            BArray.setFalse(passable!!)
    }

    fun findPath(ch: Char, from: Int, to: Int, pass: BooleanArray, visible: BooleanArray): PathFinder.Path? {

        setupPassable()
        if (ch.flying || ch.buff<Amok>(Amok::class.java) != null) {
            BArray.or(pass, Dungeon.level!!.avoid, passable!!)
        } else {
            System.arraycopy(pass, 0, passable!!, 0, Dungeon.level!!.length())
        }

        for (c in Actor.chars()) {
            if (visible[c.pos]) {
                passable!![c.pos] = false
            }
        }

        return PathFinder.find(from, to, passable!!)

    }

    fun findStep(ch: Char, from: Int, to: Int, pass: BooleanArray, visible: BooleanArray): Int {

        if (Dungeon.level!!.adjacent(from, to)) {
            return if (Actor.findChar(to) == null && (pass[to] || Dungeon.level!!.avoid[to])) to else -1
        }

        setupPassable()
        if (ch.flying || ch.buff<Amok>(Amok::class.java) != null) {
            BArray.or(pass, Dungeon.level!!.avoid, passable!!)
        } else {
            System.arraycopy(pass, 0, passable!!, 0, Dungeon.level!!.length())
        }

        for (c in Actor.chars()) {
            if (visible[c.pos]) {
                passable!![c.pos] = false
            }
        }

        return PathFinder.getStep(from, to, passable!!)

    }

    fun flee(ch: Char, cur: Int, from: Int, pass: BooleanArray, visible: BooleanArray): Int {

        setupPassable()
        if (ch.flying) {
            BArray.or(pass, Dungeon.level!!.avoid, passable!!)
        } else {
            System.arraycopy(pass, 0, passable!!, 0, Dungeon.level!!.length())
        }

        for (c in Actor.chars()) {
            if (visible[c.pos]) {
                passable!![c.pos] = false
            }
        }
        passable!![cur] = true

        return PathFinder.getStepBack(cur, from, passable!!)

    }

}
