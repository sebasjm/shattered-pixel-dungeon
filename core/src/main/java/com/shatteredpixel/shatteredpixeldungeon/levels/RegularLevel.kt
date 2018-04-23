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

import com.shatteredpixel.shatteredpixeldungeon.Bones
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey
import com.shatteredpixel.shatteredpixeldungeon.journal.Document
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap
import com.watabou.noosa.Game
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

abstract class RegularLevel : Level() {

    protected var rooms: ArrayList<Room>? = null

    protected var builder: Builder? = null

    protected var roomEntrance: Room? = null
    protected var roomExit: Room? = null

    var secretDoors: Int = 0

    private var mobsToSpawn: ArrayList<Class<out Mob>>? = ArrayList()

    override fun build(): Boolean {

        builder = builder()

        val initRooms = initRooms()
        Random.shuffle(initRooms)

        do {
            for (r in initRooms) {
                r.neigbours.clear()
                r.connected.clear()
            }
            rooms = builder!!.build(initRooms.clone() as ArrayList<Room>)
        } while (rooms == null)

        return painter().paint(this, rooms!!)

    }

    protected open fun initRooms(): ArrayList<Room> {
        val initRooms = ArrayList<Room>()
        roomEntrance = EntranceRoom()
        initRooms.add(roomEntrance!!)
        roomExit = ExitRoom()
        initRooms.add(roomExit!!)

        val standards = standardRooms()
        run {
            var i = 0
            while (i < standards) {
                var s: StandardRoom?
                do {
                    s = StandardRoom.createRoom()
                } while (!s!!.setSizeCat(standards - i))
                i += s.sizeCat!!.roomValue - 1
                initRooms.add(s)
                i++
            }
        }

        if (Dungeon.shopOnLevel())
            initRooms.add(ShopRoom())

        val specials = specialRooms()
        SpecialRoom.initForFloor()
        for (i in 0 until specials)
            initRooms.add(SpecialRoom.createRoom())

        val secrets = SecretRoom.secretsForFloor(Dungeon.depth)
        for (i in 0 until secrets)
            initRooms.add(SecretRoom.createRoom())

        return initRooms
    }

    protected open fun standardRooms(): Int {
        return 0
    }

    protected open fun specialRooms(): Int {
        return 0
    }

    protected open fun builder(): Builder {
        return LoopBuilder()
                .setLoopShape(2,
                        Random.Float(0.4f, 0.7f),
                        Random.Float(0f, 0.5f))
    }

    protected abstract fun painter(): Painter

    protected open fun waterFill(): Float {
        return 0f
    }

    protected open fun waterSmoothing(): Int {
        return 0
    }

    protected open fun grassFill(): Float {
        return 0f
    }

    protected open fun grassSmoothing(): Int {
        return 0
    }

    protected open fun nTraps(): Int {
        return Random.NormalIntRange(1, 3 + Dungeon.depth / 3)
    }

    protected open fun trapClasses(): Array<Class<*>> {
        return arrayOf(WornDartTrap::class.java)
    }

    protected open fun trapChances(): FloatArray {
        return floatArrayOf(1f)
    }

    override fun nMobs(): Int {
        when (Dungeon.depth) {
            1 ->
                //mobs are not randomly spawned on floor 1.
                return 0
            else -> return 2 + Dungeon.depth % 5 + Random.Int(5)
        }
    }

    override fun createMob(): Mob? {
        if (mobsToSpawn == null || mobsToSpawn!!.isEmpty())
            mobsToSpawn = Bestiary.getMobRotation(Dungeon.depth)

        try {
            return mobsToSpawn!!.removeAt(0).newInstance()
        } catch (e: Exception) {
            Game.reportException(e)
            return null
        }

    }

    override fun createMobs() {
        //on floor 1, 10 rats are created so the player can get level 2.
        var mobsToSpawn = if (Dungeon.depth == 1) 10 else nMobs()

        val stdRooms = ArrayList<Room>()
        for (room in rooms!!) {
            if (room is StandardRoom && room !== roomEntrance) {
                for (i in 0 until room.sizeCat!!.roomValue) {
                    stdRooms.add(room)
                }
                //pre-0.6.0 save compatibility
            } else if (room.legacyType == "STANDARD") {
                stdRooms.add(room)
            }
        }
        Random.shuffle(stdRooms)
        var stdRoomIter = stdRooms.iterator()

        while (mobsToSpawn > 0) {
            if (!stdRoomIter.hasNext())
                stdRoomIter = stdRooms.iterator()
            val roomToSpawn = stdRoomIter.next()

            var mob = createMob()
            mob!!.pos = pointToCell(roomToSpawn.random())

            if (findMob(mob.pos) == null && passable[mob.pos] && mob.pos != exit) {
                mobsToSpawn--
                mobs.add(mob)

                //TODO: perhaps externalize this logic into a method. Do I want to make mobs more likely to clump deeper down?
                if (mobsToSpawn > 0 && Random.Int(4) == 0) {
                    mob = createMob()
                    mob!!.pos = pointToCell(roomToSpawn.random())

                    if (findMob(mob.pos) == null && passable[mob.pos] && mob.pos != exit) {
                        mobsToSpawn--
                        mobs.add(mob)
                    }
                }
            }
        }

        for (m in mobs) {
            if (map!![m.pos] == Terrain.HIGH_GRASS) {
                map!![m.pos] = Terrain.GRASS
                losBlocking[m.pos] = false
            }

        }

    }

    override fun randomRespawnCell(): Int {
        var count = 0
        var cell = -1

        while (true) {

            if (++count > 30) {
                return -1
            }

            val room = randomRoom(StandardRoom::class.java)
            if (room == null || room === roomEntrance) {
                continue
            }

            cell = pointToCell(room.random(1))
            if (!heroFOV[cell]
                    && Actor.findChar(cell) == null
                    && passable[cell]
                    && cell != exit) {
                return cell
            }

        }
    }

    override fun randomDestination(): Int {

        var cell = -1

        while (true) {

            val room = Random.element(rooms!!) ?: continue

            cell = pointToCell(room.random())
            if (passable[cell]) {
                return cell
            }

        }
    }

    override fun createItems() {

        // drops 3/4/5 items 60%/30%/10% of the time
        val nItems = 3 + Random.chances(floatArrayOf(6f, 3f, 1f))

        for (i in 0 until nItems) {
            var type: Heap.Type? = null
            when (Random.Int(20)) {
                0 -> type = Heap.Type.SKELETON
                1, 2, 3, 4 -> type = Heap.Type.CHEST
                5 -> type = if (Dungeon.depth > 1) Heap.Type.MIMIC else Heap.Type.CHEST
                else -> type = Heap.Type.HEAP
            }
            val cell = randomDropCell()
            if (map!![cell] == Terrain.HIGH_GRASS) {
                map!![cell] = Terrain.GRASS
                losBlocking[cell] = false
            }

            val toDrop = Generator.random() ?: continue

            if (toDrop is Artifact && Random.Int(2) == 0 || toDrop.isUpgradable && Random.Int(4 - toDrop.level()) == 0) {
                val dropped = drop(toDrop, cell)
                if (heaps.get(cell) === dropped) {
                    dropped.type = Heap.Type.LOCKED_CHEST
                    addItemToSpawn(GoldenKey(Dungeon.depth))
                }
            } else {
                drop(toDrop, cell).type = type
            }

        }

        for (item in itemsToSpawn) {
            val cell = randomDropCell()
            drop(item, cell).type = Heap.Type.HEAP
            if (map!![cell] == Terrain.HIGH_GRASS) {
                map!![cell] = Terrain.GRASS
                losBlocking[cell] = false
            }
        }

        val item = Bones.get()
        if (item != null) {
            val cell = randomDropCell()
            if (map!![cell] == Terrain.HIGH_GRASS) {
                map!![cell] = Terrain.GRASS
                losBlocking[cell] = false
            }
            drop(item, cell).type = Heap.Type.REMAINS
        }

        //guide pages
        val allPages = Document.ADVENTURERS_GUIDE.pages()
        val missingPages = ArrayList<String>()
        for (page in allPages) {
            if (!Document.ADVENTURERS_GUIDE.hasPage(page)) {
                missingPages.add(page)
            }
        }

        //these are dropped specially
        missingPages.remove(Document.GUIDE_INTRO_PAGE)
        missingPages.remove(Document.GUIDE_SEARCH_PAGE)

        val foundPages = allPages.size - (missingPages.size + 2)

        //chance to find a page scales with pages missing and depth
        if (missingPages.size > 0 && Random.Float() < Dungeon.depth / (foundPages + 1).toFloat()) {
            val p = GuidePage()
            p.page(missingPages[0])
            val cell = randomDropCell()
            if (map!![cell] == Terrain.HIGH_GRASS) {
                map!![cell] = Terrain.GRASS
                losBlocking[cell] = false
            }
            drop(p, cell)
        }

    }

    protected fun randomRoom(type: Class<out Room>): Room? {
        Random.shuffle(rooms!!)
        for (r in rooms!!) {
            if (type.isInstance(r)
                    //compatibility with pre-0.6.0 saves
                    || type == StandardRoom::class.java && r.legacyType == "STANDARD") {
                return r
            }
        }
        return null
    }

    fun room(pos: Int): Room? {
        for (room in rooms!!) {
            if (room.inside(cellToPoint(pos))) {
                return room
            }
        }

        return null
    }

    protected fun randomDropCell(): Int {
        while (true) {
            val room = randomRoom(StandardRoom::class.java)
            if (room != null && room !== roomEntrance) {
                val pos = pointToCell(room.random())
                if (passable[pos]
                        && pos != exit
                        && heaps.get(pos) == null) {

                    val t = traps.get(pos)

                    //items cannot spawn on traps which destroy items
                    if (t == null || !(t is BurningTrap || t is BlazingTrap
                                    || t is ChillingTrap || t is FrostTrap
                                    || t is ExplosiveTrap || t is DisintegrationTrap)) {

                        return pos
                    }
                }
            }
        }
    }

    override fun fallCell(fallIntoPit: Boolean): Int {
        if (fallIntoPit) {
            for (room in rooms!!) {
                if (room is PitRoom || room.legacyType == "PIT") {
                    var result: Int
                    do {
                        result = pointToCell(room.random())
                    } while (traps.get(result) != null
                            || findMob(result) != null
                            || heaps.get(result) != null)
                    return result
                }
            }
        }

        return super.fallCell(false)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put("rooms", rooms!!)
        bundle.put("mobs_to_spawn", mobsToSpawn!!.toTypedArray<Class<*>>())
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)

        rooms = ArrayList(bundle.getCollection("rooms") as Collection<Room>)
        for (r in rooms!!) {
            r.onLevelLoad(this)
            if (r is EntranceRoom || r.legacyType == "ENTRANCE") {
                roomEntrance = r
            } else if (r is ExitRoom || r.legacyType == "EXIT") {
                roomExit = r
            }
        }

        if (bundle.contains("mobs_to_spawn")) {
            for (mob in bundle.getClassArray("mobs_to_spawn")!!) {
                if (mob != null) mobsToSpawn!!.add(mob as Class<out Mob>)
            }
        }
    }

}
