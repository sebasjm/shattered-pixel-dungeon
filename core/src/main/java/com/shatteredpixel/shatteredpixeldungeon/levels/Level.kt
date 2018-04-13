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

package com.shatteredpixel.shatteredpixeldungeon.levels

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlowParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus
import com.shatteredpixel.shatteredpixeldungeon.items.Torch
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Point
import com.watabou.utils.Random
import com.watabou.utils.SparseArray

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet

abstract class Level : Bundlable {

    protected var width: Int = 0
    protected var height: Int = 0
    protected var length: Int = 0

    var version: Int = 0

    var map: IntArray? = null
    var visited: BooleanArray? = null
    var mapped: BooleanArray? = null
    var discoverable: BooleanArray

    var viewDistance = if (Dungeon.isChallenged(Challenges.DARKNESS)) 2 else 8

    var heroFOV: BooleanArray

    var passable: BooleanArray
    var losBlocking: BooleanArray
    var flamable: BooleanArray
    var secret: BooleanArray
    var solid: BooleanArray
    var avoid: BooleanArray
    var water: BooleanArray
    var pit: BooleanArray

    var feeling = Feeling.NONE

    var entrance: Int = 0
    var exit: Int = 0

    //when a boss level has become locked.
    var locked = false

    var mobs: HashSet<Mob>
    var heaps: SparseArray<Heap>
    var blobs: HashMap<Class<out Blob>, Blob>
    var plants: SparseArray<Plant>
    var traps: SparseArray<Trap>
    var customTiles: HashSet<CustomTiledVisual>
    var customWalls: HashSet<CustomTiledVisual>

    protected var itemsToSpawn = ArrayList<Item>()

    protected var visuals: Group? = null

    var color1 = 0x004400
    var color2 = 0x88CC44

    enum class Feeling {
        NONE,
        CHASM,
        WATER,
        GRASS,
        DARK
    }

    open fun create() {

        Random.seed(Dungeon.seedCurDepth())

        if (!(Dungeon.bossLevel() || Dungeon.depth == 21) /*final shop floor*/) {

            if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
                addItemToSpawn(SmallRation())
            } else {
                addItemToSpawn(Generator.random(Generator.Category.FOOD))
            }

            if (Dungeon.isChallenged(Challenges.DARKNESS)) {
                addItemToSpawn(Torch())
            }

            if (Dungeon.posNeeded()) {
                addItemToSpawn(PotionOfStrength())
                Dungeon.LimitedDrops.STRENGTH_POTIONS.count++
            }
            if (Dungeon.souNeeded()) {
                addItemToSpawn(ScrollOfUpgrade())
                Dungeon.LimitedDrops.UPGRADE_SCROLLS.count++
            }
            if (Dungeon.asNeeded()) {
                addItemToSpawn(Stylus())
                Dungeon.LimitedDrops.ARCANE_STYLI.count++
            }

            val rose = Dungeon.hero!!.belongings.getItem<DriedRose>(DriedRose::class.java)
            if (rose != null && rose.isIdentified && !rose.cursed) {
                //aim to drop 1 petal every 2 floors
                val petalsNeeded = Math.ceil(((Dungeon.depth / 2 - rose.droppedPetals).toFloat() / 3).toDouble()).toInt()

                for (i in 1..petalsNeeded) {
                    //the player may miss a single petal and still max their rose.
                    if (rose.droppedPetals < 11) {
                        addItemToSpawn(DriedRose.Petal())
                        rose.droppedPetals++
                    }
                }
            }

            if (Dungeon.depth > 1) {
                when (Random.Int(10)) {
                    0 -> if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
                        feeling = Feeling.CHASM
                    }
                    1 -> feeling = Feeling.WATER
                    2 -> feeling = Feeling.GRASS
                    3 -> {
                        feeling = Feeling.DARK
                        addItemToSpawn(Torch())
                        viewDistance = Math.round(viewDistance / 2f)
                    }
                }
            }
        }

        do {
            length = 0
            height = length
            width = height

            mobs = HashSet()
            heaps = SparseArray()
            blobs = HashMap()
            plants = SparseArray()
            traps = SparseArray()
            customTiles = HashSet()
            customWalls = HashSet()

        } while (!build())

        buildFlagMaps()
        cleanWalls()

        createMobs()
        createItems()

        Random.seed()
    }

    fun setSize(w: Int, h: Int) {

        width = w
        height = h
        length = w * h

        map = IntArray(length)
        Arrays.fill(map!!, if (feeling == Level.Feeling.CHASM) Terrain.CHASM else Terrain.WALL)

        visited = BooleanArray(length)
        mapped = BooleanArray(length)

        heroFOV = BooleanArray(length)

        passable = BooleanArray(length)
        losBlocking = BooleanArray(length)
        flamable = BooleanArray(length)
        secret = BooleanArray(length)
        solid = BooleanArray(length)
        avoid = BooleanArray(length)
        water = BooleanArray(length)
        pit = BooleanArray(length)

        PathFinder.setMapSize(w, h)
    }

    fun reset() {

        for (mob in mobs.toTypedArray<Mob>()) {
            if (!mob.reset()) {
                mobs.remove(mob)
            }
        }
        createMobs()
    }

    override fun restoreFromBundle(bundle: Bundle) {

        version = bundle.getInt(VERSION)

        //saves from before 0.5.0b are not supported
        if (version < ShatteredPixelDungeon.v0_5_0b) {
            throw RuntimeException("old save")
        }

        setSize(bundle.getInt(WIDTH), bundle.getInt(HEIGHT))

        mobs = HashSet()
        heaps = SparseArray()
        blobs = HashMap()
        plants = SparseArray()
        traps = SparseArray()
        customTiles = HashSet()
        customWalls = HashSet()

        map = bundle.getIntArray(MAP)

        visited = bundle.getBooleanArray(VISITED)
        mapped = bundle.getBooleanArray(MAPPED)

        entrance = bundle.getInt(ENTRANCE)
        exit = bundle.getInt(EXIT)

        locked = bundle.getBoolean(LOCKED)

        // pre-0.6.1 saves
        if (version <= ShatteredPixelDungeon.v0_6_0b) {
            map = Terrain.convertTilesFrom0_6_0b(map!!)
        }

        var collection = bundle.getCollection(HEAPS)
        for (h in collection) {
            val heap = h as Heap
            if (!heap.isEmpty)
                heaps.put(heap.pos, heap)
        }

        collection = bundle.getCollection(PLANTS)
        for (p in collection) {
            val plant = p as Plant
            plants.put(plant.pos, plant)
        }

        collection = bundle.getCollection(TRAPS)
        for (p in collection) {
            val trap = p as Trap
            traps.put(trap.pos, trap)
        }

        collection = bundle.getCollection(CUSTOM_TILES)
        for (p in collection) {
            val vis = p as CustomTiledVisual
            customTiles.add(vis)
        }

        collection = bundle.getCollection(CUSTOM_WALLS)
        for (p in collection) {
            val vis = p as CustomTiledVisual
            customWalls.add(vis)
        }

        collection = bundle.getCollection(MOBS)
        for (m in collection) {
            val mob = m as Mob
            if (mob != null) {
                mobs.add(mob)
            }
        }

        collection = bundle.getCollection(BLOBS)
        for (b in collection) {
            val blob = b as Blob
            blobs[blob.javaClass] = blob
        }

        feeling = bundle.getEnum(FEELING, Feeling::class.java)
        if (feeling == Feeling.DARK)
            viewDistance = Math.round(viewDistance / 2f)

        buildFlagMaps()
        cleanWalls()
    }

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(VERSION, Game.versionCode)
        bundle.put(WIDTH, width)
        bundle.put(HEIGHT, height)
        bundle.put(MAP, map)
        bundle.put(VISITED, visited)
        bundle.put(MAPPED, mapped)
        bundle.put(ENTRANCE, entrance)
        bundle.put(EXIT, exit)
        bundle.put(LOCKED, locked)
        bundle.put(HEAPS, heaps.values())
        bundle.put(PLANTS, plants.values())
        bundle.put(TRAPS, traps.values())
        bundle.put(CUSTOM_TILES, customTiles)
        bundle.put(CUSTOM_WALLS, customWalls)
        bundle.put(MOBS, mobs)
        bundle.put(BLOBS, blobs.values)
        bundle.put(FEELING, feeling)
    }

    fun tunnelTile(): Int {
        return if (feeling == Feeling.CHASM) Terrain.EMPTY_SP else Terrain.EMPTY
    }

    fun width(): Int {
        return width
    }

    fun height(): Int {
        return height
    }

    fun length(): Int {
        return length
    }

    open fun tilesTex(): String? {
        return null
    }

    open fun waterTex(): String? {
        return null
    }

    protected abstract fun build(): Boolean

    abstract fun createMob(): Mob

    protected abstract fun createMobs()

    protected abstract fun createItems()

    open fun seal() {
        if (!locked) {
            locked = true
            Buff.affect<LockedFloor>(Dungeon.hero!!, LockedFloor::class.java)
        }
    }

    open fun unseal() {
        if (locked) {
            locked = false
        }
    }

    open fun addVisuals(): Group {
        if (visuals == null || visuals!!.parent == null) {
            visuals = Group()
        } else {
            visuals!!.clear()
        }
        for (i in 0 until length()) {
            if (pit[i]) {
                visuals!!.add(WindParticle.Wind(i))
                if (i >= width() && water[i - width()]) {
                    visuals!!.add(FlowParticle.Flow(i - width()))
                }
            }
        }
        return visuals
    }

    open fun nMobs(): Int {
        return 0
    }

    fun findMob(pos: Int): Mob? {
        for (mob in mobs) {
            if (mob.pos == pos) {
                return mob
            }
        }
        return null
    }

    open fun respawner(): Actor {
        return object : Actor() {

            init {
                actPriority = BUFF_PRIO //as if it were a buff.
            }

            override fun act(): Boolean {
                var count = 0
                for (mob in mobs.toTypedArray<Mob>()) {
                    if (mob.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ENEMY) count++
                }

                if (count < nMobs()) {

                    val mob = createMob()
                    mob.state = mob.WANDERING
                    mob.pos = randomRespawnCell()
                    if (Dungeon.hero!!.isAlive && mob.pos != -1 && distance(Dungeon.hero!!.pos, mob.pos) >= 4) {
                        GameScene.add(mob)
                        if (Statistics.amuletObtained) {
                            mob.beckon(Dungeon.hero!!.pos)
                        }
                    }
                }
                if (Statistics.amuletObtained) {
                    spend(TIME_TO_RESPAWN / 2f)
                } else if (Dungeon.level!!.feeling == Feeling.DARK) {
                    spend(2 * TIME_TO_RESPAWN / 3f)
                } else {
                    spend(TIME_TO_RESPAWN)
                }
                return true
            }
        }
    }

    open fun randomRespawnCell(): Int {
        var cell: Int
        do {
            cell = Random.Int(length())
        } while (Dungeon.level === this && heroFOV[cell]
                || !passable[cell]
                || Actor.findChar(cell) != null)
        return cell
    }

    open fun randomDestination(): Int {
        var cell: Int
        do {
            cell = Random.Int(length())
        } while (!passable[cell])
        return cell
    }

    fun addItemToSpawn(item: Item?) {
        if (item != null) {
            itemsToSpawn.add(item)
        }
    }

    @JvmOverloads
    fun findPrizeItem(match: Class<out Item>? = null): Item? {
        if (itemsToSpawn.size == 0)
            return null

        if (match == null) {
            val item = Random.element(itemsToSpawn)
            itemsToSpawn.remove(item)
            return item
        }

        for (item in itemsToSpawn) {
            if (match.isInstance(item)) {
                itemsToSpawn.remove(item)
                return item
            }
        }

        return null
    }

    protected fun buildFlagMaps() {

        for (i in 0 until length()) {
            val flags = Terrain.flags[map!![i]]
            passable[i] = flags and Terrain.PASSABLE != 0
            losBlocking[i] = flags and Terrain.LOS_BLOCKING != 0
            flamable[i] = flags and Terrain.FLAMABLE != 0
            secret[i] = flags and Terrain.SECRET != 0
            solid[i] = flags and Terrain.SOLID != 0
            avoid[i] = flags and Terrain.AVOID != 0
            water[i] = flags and Terrain.LIQUID != 0
            pit[i] = flags and Terrain.PIT != 0
        }

        val lastRow = length() - width()
        for (i in 0 until width()) {
            avoid[i] = false
            passable[i] = avoid[i]
            losBlocking[i] = true
            avoid[lastRow + i] = false
            passable[lastRow + i] = avoid[lastRow + i]
            losBlocking[lastRow + i] = true
        }
        var i = width()
        while (i < lastRow) {
            avoid[i] = false
            passable[i] = avoid[i]
            losBlocking[i] = true
            avoid[i + width() - 1] = false
            passable[i + width() - 1] = avoid[i + width() - 1]
            losBlocking[i + width() - 1] = true
            i += width()
        }
    }

    fun destroy(pos: Int) {
        set(pos, Terrain.EMBERS)
    }

    protected fun cleanWalls() {
        discoverable = BooleanArray(length())

        for (i in 0 until length()) {

            var d = false

            for (j in PathFinder.NEIGHBOURS9.indices) {
                val n = i + PathFinder.NEIGHBOURS9[j]
                if (n >= 0 && n < length() && map!![n] != Terrain.WALL && map!![n] != Terrain.WALL_DECO) {
                    d = true
                    break
                }
            }

            discoverable[i] = d
        }
    }

    open fun drop(item: Item?, cell: Int): Heap {

        if (item == null || Challenges.isItemBlocked(item)) {

            //create a dummy heap, give it a dummy sprite, don't add it to the game, and return it.
            //effectively nullifies whatever the logic calling this wants to do, including dropping items.
            val heap = Heap()
            heap.sprite = ItemSprite()
            val sprite = heap.sprite
            sprite.link(heap)
            return heap

        }

        var heap: Heap? = heaps.get(cell)
        if (heap == null) {

            heap = Heap()
            heap.seen = Dungeon.level === this && heroFOV[cell]
            heap.pos = cell
            heap.drop(item)
            if (map!![cell] == Terrain.CHASM || Dungeon.level != null && pit[cell]) {
                Dungeon.dropToChasm(item)
                GameScene.discard(heap)
            } else {
                heaps.put(cell, heap)
                GameScene.add(heap)
            }

        } else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {

            var n: Int
            do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)]
            } while (!passable[n] && !avoid[n])
            return drop(item, n)

        } else {
            heap.drop(item)
        }

        if (Dungeon.level != null) {
            press(cell, null, true)
        }

        return heap
    }

    fun plant(seed: Plant.Seed, pos: Int): Plant? {

        if (Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
            return null
        }

        var plant: Plant? = plants.get(pos)
        if (plant != null) {
            plant.wither()
        }

        if (map!![pos] == Terrain.HIGH_GRASS ||
                map!![pos] == Terrain.EMPTY ||
                map!![pos] == Terrain.EMBERS ||
                map!![pos] == Terrain.EMPTY_DECO) {
            set(pos, Terrain.GRASS, this)
            GameScene.updateMap(pos)
        }

        plant = seed.couch(pos, this)
        plants.put(pos, plant)

        GameScene.plantSeed(pos)

        return plant
    }

    fun uproot(pos: Int) {
        plants.remove(pos)
        GameScene.updateMap(pos)
    }

    fun setTrap(trap: Trap, pos: Int): Trap {
        val existingTrap = traps.get(pos)
        if (existingTrap != null) {
            traps.remove(pos)
        }
        trap.set(pos)
        traps.put(pos, trap)
        GameScene.updateMap(pos)
        return trap
    }

    fun disarmTrap(pos: Int) {
        set(pos, Terrain.INACTIVE_TRAP)
        GameScene.updateMap(pos)
    }

    fun discover(cell: Int) {
        set(cell, Terrain.discover(map!![cell]))
        val trap = traps.get(cell)
        trap?.reveal()
        GameScene.updateMap(cell)
    }

    open fun fallCell(fallIntoPit: Boolean): Int {
        var result: Int
        do {
            result = randomRespawnCell()
        } while (traps.get(result) != null
                || findMob(result) != null
                || heaps.get(result) != null)
        return result
    }

    //characters which are not the hero 'soft' press cells by default
    open fun press(cell: Int, ch: Char) {
        press(cell, ch, ch === Dungeon.hero)
    }

    //a 'soft' press ignores hidden traps
    //a 'hard' press triggers all things
    //generally a 'hard' press should be forced is something is moving forcefully (e.g. thrown)
    fun press(cell: Int, ch: Char?, hard: Boolean) {

        if (ch != null && pit[cell] && !ch.flying) {
            if (ch === Dungeon.hero) {
                Chasm.heroFall(cell)
            } else if (ch is Mob) {
                Chasm.mobFall((ch as Mob?)!!)
            }
            return
        }

        var trap: Trap? = null

        when (map!![cell]) {

            Terrain.SECRET_TRAP -> if (hard) {
                trap = traps.get(cell)
                GLog.i(Messages.get(Level::class.java, "hidden_trap", trap!!.name))
            }

            Terrain.TRAP -> trap = traps.get(cell)

            Terrain.HIGH_GRASS -> HighGrass.trample(this, cell, ch)

            Terrain.WELL -> WellWater.affectCell(cell)

            Terrain.DOOR -> Door.enter(cell)
        }

        if (trap != null) {

            val timeFreeze = ch?.buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)

            if (timeFreeze == null) {

                if (ch === Dungeon.hero) {
                    Dungeon.hero!!.interrupt()
                }

                trap.trigger()

            } else {

                Sample.INSTANCE.play(Assets.SND_TRAP)

                discover(cell)

                timeFreeze.setDelayedPress(cell)

            }
        }

        val plant = plants.get(cell)
        plant?.trigger()
    }

    fun updateFieldOfView(c: Char, fieldOfView: BooleanArray) {

        val cx = c.pos % width()
        val cy = c.pos / width()

        val sighted = (c.buff<Blindness>(Blindness::class.java) == null && c.buff<Shadows>(Shadows::class.java) == null
                && c.buff<TimekeepersHourglass.timeStasis>(TimekeepersHourglass.timeStasis::class.java) == null && c.isAlive)
        if (sighted) {
            ShadowCaster.castShadow(cx, cy, fieldOfView, c.viewDistance)
        } else {
            BArray.setFalse(fieldOfView)
        }

        var sense = 1
        //Currently only the hero can get mind vision
        if (c.isAlive && c === Dungeon.hero) {
            for (b in c.buffs<MindVision>(MindVision::class.java)) {
                sense = Math.max((b as MindVision).distance, sense)
            }
        }

        if (sighted && sense > 1 || !sighted) {

            val ax = Math.max(0, cx - sense)
            val bx = Math.min(cx + sense, width() - 1)
            val ay = Math.max(0, cy - sense)
            val by = Math.min(cy + sense, height() - 1)

            val len = bx - ax + 1
            var pos = ax + ay * width()
            var y = ay
            while (y <= by) {
                System.arraycopy(discoverable, pos, fieldOfView, pos, len)
                y++
                pos += width()
            }
        }

        //Currently only the hero can get mind vision or awareness
        if (c.isAlive && c === Dungeon.hero) {
            Dungeon.hero!!.mindVisionEnemies.clear()
            if (c.buff<MindVision>(MindVision::class.java) != null) {
                for (mob in mobs) {
                    val p = mob.pos

                    if (!fieldOfView[p]) {
                        Dungeon.hero!!.mindVisionEnemies.add(mob)
                    }

                }
            } else if (c.heroClass == HeroClass.HUNTRESS) {
                for (mob in mobs) {
                    val p = mob.pos
                    if (distance(c.pos, p) == 2) {

                        if (!fieldOfView[p]) {
                            Dungeon.hero!!.mindVisionEnemies.add(mob)
                        }
                    }
                }
            }

            for (m in Dungeon.hero!!.mindVisionEnemies) {
                for (i in PathFinder.NEIGHBOURS9) {
                    fieldOfView[m.pos + i] = true
                }
            }

            if (c.buff<Awareness>(Awareness::class.java) != null) {
                for (heap in heaps.values()) {
                    val p = heap.pos
                    for (i in PathFinder.NEIGHBOURS9)
                        fieldOfView[p + i] = true
                }
            }
        }

        if (c === Dungeon.hero) {
            for (heap in heaps.values())
                if (!heap.seen && fieldOfView[heap.pos])
                    heap.seen = true
        }

    }

    fun distance(a: Int, b: Int): Int {
        val ax = a % width()
        val ay = a / width()
        val bx = b % width()
        val by = b / width()
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by))
    }

    fun adjacent(a: Int, b: Int): Boolean {
        return distance(a, b) == 1
    }

    //uses pythagorean theorum for true distance, as if there was no movement grid
    fun trueDistance(a: Int, b: Int): Float {
        val ax = a % width()
        val ay = a / width()
        val bx = b % width()
        val by = b / width()
        return Math.sqrt(Math.pow(Math.abs(ax - bx).toDouble(), 2.0) + Math.pow(Math.abs(ay - by).toDouble(), 2.0)).toFloat()
    }

    //returns true if the input is a valid tile within the level
    fun insideMap(tile: Int): Boolean {
        //top and bottom row and beyond
        return !//left and right column
        (tile < width || tile >= length - width || tile % width == 0 || tile % width == width - 1)
    }

    fun cellToPoint(cell: Int): Point {
        return Point(cell % width(), cell / width())
    }

    fun pointToCell(p: Point): Int {
        return p.x + p.y * width()
    }

    open fun tileName(tile: Int): String {

        when (tile) {
            Terrain.CHASM -> return Messages.get(Level::class.java, "chasm_name")
            Terrain.EMPTY, Terrain.EMPTY_SP, Terrain.EMPTY_DECO, Terrain.SECRET_TRAP -> return Messages.get(Level::class.java, "floor_name")
            Terrain.GRASS -> return Messages.get(Level::class.java, "grass_name")
            Terrain.WATER -> return Messages.get(Level::class.java, "water_name")
            Terrain.WALL, Terrain.WALL_DECO, Terrain.SECRET_DOOR -> return Messages.get(Level::class.java, "wall_name")
            Terrain.DOOR -> return Messages.get(Level::class.java, "closed_door_name")
            Terrain.OPEN_DOOR -> return Messages.get(Level::class.java, "open_door_name")
            Terrain.ENTRANCE -> return Messages.get(Level::class.java, "entrace_name")
            Terrain.EXIT -> return Messages.get(Level::class.java, "exit_name")
            Terrain.EMBERS -> return Messages.get(Level::class.java, "embers_name")
            Terrain.LOCKED_DOOR -> return Messages.get(Level::class.java, "locked_door_name")
            Terrain.PEDESTAL -> return Messages.get(Level::class.java, "pedestal_name")
            Terrain.BARRICADE -> return Messages.get(Level::class.java, "barricade_name")
            Terrain.HIGH_GRASS -> return Messages.get(Level::class.java, "high_grass_name")
            Terrain.LOCKED_EXIT -> return Messages.get(Level::class.java, "locked_exit_name")
            Terrain.UNLOCKED_EXIT -> return Messages.get(Level::class.java, "unlocked_exit_name")
            Terrain.SIGN -> return Messages.get(Level::class.java, "sign_name")
            Terrain.WELL -> return Messages.get(Level::class.java, "well_name")
            Terrain.EMPTY_WELL -> return Messages.get(Level::class.java, "empty_well_name")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(Level::class.java, "statue_name")
            Terrain.INACTIVE_TRAP -> return Messages.get(Level::class.java, "inactive_trap_name")
            Terrain.BOOKSHELF -> return Messages.get(Level::class.java, "bookshelf_name")
            Terrain.ALCHEMY -> return Messages.get(Level::class.java, "alchemy_name")
            else -> return Messages.get(Level::class.java, "default_name")
        }
    }

    open fun tileDesc(tile: Int): String {

        when (tile) {
            Terrain.CHASM -> return Messages.get(Level::class.java, "chasm_desc")
            Terrain.WATER -> return Messages.get(Level::class.java, "water_desc")
            Terrain.ENTRANCE -> return Messages.get(Level::class.java, "entrance_desc")
            Terrain.EXIT, Terrain.UNLOCKED_EXIT -> return Messages.get(Level::class.java, "exit_desc")
            Terrain.EMBERS -> return Messages.get(Level::class.java, "embers_desc")
            Terrain.HIGH_GRASS -> return Messages.get(Level::class.java, "high_grass_desc")
            Terrain.LOCKED_DOOR -> return Messages.get(Level::class.java, "locked_door_desc")
            Terrain.LOCKED_EXIT -> return Messages.get(Level::class.java, "locked_exit_desc")
            Terrain.BARRICADE -> return Messages.get(Level::class.java, "barricade_desc")
            Terrain.SIGN -> return Messages.get(Level::class.java, "sign_desc")
            Terrain.INACTIVE_TRAP -> return Messages.get(Level::class.java, "inactive_trap_desc")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(Level::class.java, "statue_desc")
            Terrain.ALCHEMY -> return Messages.get(Level::class.java, "alchemy_desc")
            Terrain.EMPTY_WELL -> return Messages.get(Level::class.java, "empty_well_desc")
            else -> return ""
        }
    }

    companion object {

        protected val TIME_TO_RESPAWN = 50f

        private val VERSION = "version"
        private val WIDTH = "width"
        private val HEIGHT = "height"
        private val MAP = "map"
        private val VISITED = "visited"
        private val MAPPED = "mapped"
        private val ENTRANCE = "entrance"
        private val EXIT = "exit"
        private val LOCKED = "locked"
        private val HEAPS = "heaps"
        private val PLANTS = "plants"
        private val TRAPS = "traps"
        private val CUSTOM_TILES = "customTiles"
        private val CUSTOM_WALLS = "customWalls"
        private val MOBS = "mobs"
        private val BLOBS = "blobs"
        private val FEELING = "feeling"

        @JvmOverloads
        fun set(cell: Int, terrain: Int, level: Level? = Dungeon.level) {
            Painter.set(level!!, cell, terrain)

            if (terrain != Terrain.TRAP && terrain != Terrain.SECRET_TRAP && terrain != Terrain.INACTIVE_TRAP) {
                level.traps.remove(cell)
            }

            val flags = Terrain.flags[terrain]
            level.passable[cell] = flags and Terrain.PASSABLE != 0
            level.losBlocking[cell] = flags and Terrain.LOS_BLOCKING != 0
            level.flamable[cell] = flags and Terrain.FLAMABLE != 0
            level.secret[cell] = flags and Terrain.SECRET != 0
            level.solid[cell] = flags and Terrain.SOLID != 0
            level.avoid[cell] = flags and Terrain.AVOID != 0
            level.pit[cell] = flags and Terrain.PIT != 0
            level.water[cell] = terrain == Terrain.WATER
        }
    }
}
