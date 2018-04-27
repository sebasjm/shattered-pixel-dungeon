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
import com.shatteredpixel.shatteredpixeldungeon.Bones
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.MazeRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class PrisonBossLevel : Level() {

    private var state: State? = null
    private var tengu: Tengu? = null

    //keep track of that need to be removed as the level is changed. We dump 'em back into the level at the end.
    private val storedItems = ArrayList<Item>()

    init {
        color1 = 0x6a723d
        color2 = 0x88924c
    }

    private enum class State {
        START,
        FIGHT_START,
        MAZE,
        FIGHT_ARENA,
        WON
    }

    override fun tilesTex(): String? {
        return Assets.TILES_PRISON
    }

    override fun waterTex(): String? {
        return Assets.WATER_PRISON
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STATE, state)
        bundle.put(TENGU, tengu)
        bundle.put(STORED_ITEMS, storedItems)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        state = bundle.getEnum<State>(STATE, State::class.java)

        //in some states tengu won't be in the world, in others he will be.
        if (state == State.START || state == State.MAZE) {
            tengu = bundle.get(TENGU) as Tengu?
        } else {
            for (mob in mobs) {
                if (mob is Tengu) {
                    tengu = mob
                    break
                }
            }
        }

        for (item in bundle.getCollection(STORED_ITEMS)) {
            storedItems.add(item as Item)
        }
    }

    override fun build(): Boolean {

        setSize(32, 32)

        map = MAP_START.clone()

        buildFlagMaps()
        cleanWalls()

        state = State.START
        entrance = 5 + 2 * 32
        exit = 0

        resetTraps()

        return true
    }

    override fun createMob(): Mob? {
        return null
    }

    override fun createMobs() {
        tengu = Tengu() //We want to keep track of tengu independently of other mobs, he's not always in the level.
    }

    override fun respawner(): Actor? {
        return null
    }

    override fun createItems() {
        val item = Bones.get()
        if (item != null) {
            drop(item, randomRespawnCell()).type = Heap.Type.REMAINS
        }
        drop(IronKey(10), randomPrisonCell())
    }

    private fun randomPrisonCell(): Int {
        var pos = 1 + 8 * 32 //initial position at top-left room

        //randomly assign a room.
        pos += Random.Int(4) * (4 * 32) //one of the 4 rows
        pos += Random.Int(2) * 6 // one of the 2 columns

        //and then a certain tile in that room.
        pos += Random.Int(3) + Random.Int(3) * 32

        return pos
    }

    override fun press(cell: Int, ch: Char) {

        super.press(cell, ch)

        if (ch === Dungeon.hero!!) {
            //hero enters tengu's chamber
            if (state == State.START && (Room().set(2, 25, 8, 32) as Room).inside(cellToPoint(cell))) {
                progress()
            } else if (state == State.MAZE && (Room().set(4, 0, 7, 4) as Room).inside(cellToPoint(cell))) {
                progress()
            }//hero finishes the maze
        }
    }

    override fun randomRespawnCell(): Int {
        return 5 + 2 * 32 + PathFinder.NEIGHBOURS8!![Random.Int(8)] //random cell adjacent to the entrance.
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(PrisonLevel::class.java, "water_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.EMPTY_DECO -> return Messages.get(PrisonLevel::class.java, "empty_deco_desc")
            Terrain.BOOKSHELF -> return Messages.get(PrisonLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    private fun resetTraps() {
        traps.clear()

        for (i in 0 until length()) {
            if (map!![i] == Terrain.INACTIVE_TRAP) {
                val t = GrippingTrap().reveal()
                t.active = false
                setTrap(t, i)
                map!![i] = Terrain.INACTIVE_TRAP
            }
        }
    }

    private fun changeMap(map: IntArray) {
        this.map = map.clone()
        buildFlagMaps()
        cleanWalls()

        entrance = 0
        exit = entrance
        for (i in 0 until length())
            if (map[i] == Terrain.ENTRANCE)
                entrance = i
            else if (map[i] == Terrain.EXIT)
                exit = i

        BArray.setFalse(visited!!)
        BArray.setFalse(mapped!!)

        for (blob in blobs.values) {
            blob.fullyClear()
        }
        addVisuals() //this also resets existing visuals
        resetTraps()


        GameScene.resetMap()
        Dungeon.observe()
    }

    private fun clearEntities(safeArea: Room?) {
        for (heap in heaps.values().filterNotNull()) {
            if (safeArea == null || !safeArea.inside(cellToPoint(heap.pos))) {
                for (item in heap.items!!)
                    storedItems.add(item)
                heap.destroy()
            }
        }
        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (mob !== tengu && (safeArea == null || !safeArea.inside(cellToPoint(mob.pos)))) {
                mob.destroy()
                if (mob.sprite != null)
                    mob.sprite!!.killAndErase()
            }
        }
        for (plant in plants.values()) {
            if (safeArea == null || !safeArea.inside(cellToPoint(plant.pos))) {
                plants.remove(plant.pos)
            }
        }
    }

    fun progress() {
        when (state) {
        //moving to the beginning of the fight
            PrisonBossLevel.State.START -> {
                seal()
                Level.set(5 + 25 * 32, Terrain.LOCKED_DOOR)
                GameScene.updateMap(5 + 25 * 32)

                for (m in mobs) {
                    //bring the first ally with you
                    if (m.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ALLY) {
                        m.pos = 5 + 25 * 32 //they should immediately walk out of the door
                        m.sprite!!.place(m.pos)
                        break
                    }
                }

                tengu!!.state = tengu!!.HUNTING
                tengu!!.pos = 5 + 28 * 32 //in the middle of the fight room
                GameScene.add(tengu!!)
                tengu!!.notice()

                state = State.FIGHT_START
            }

        //halfway through, move to the maze
            PrisonBossLevel.State.FIGHT_START -> {

                changeMap(MAP_MAZE)
                clearEntities(Room().set(0, 5, 8, 32) as Room) //clear the entrance

                Actor.remove(tengu)
                mobs.remove(tengu!!)
                TargetHealthIndicator.instance!!.target(null)
                tengu!!.sprite!!.kill()

                val maze = MazeRoom()
                maze.set(10, 1, 31, 29)
                maze.connected[null] = Room.Door(10, 2)
                maze.connected[maze] = Room.Door(20, 29)
                maze.paint(this)
                buildFlagMaps()
                cleanWalls()
                GameScene.resetMap()

                GameScene.flash(0xFFFFFF)
                Sample.INSTANCE.play(Assets.SND_BLAST)

                state = State.MAZE
            }

        //maze beaten, moving to the arena
            PrisonBossLevel.State.MAZE -> {
                Dungeon.hero!!.interrupt()
                Dungeon.hero!!.pos += 9 + 3 * 32
                Dungeon.hero!!.sprite!!.interruptMotion()
                Dungeon.hero!!.sprite!!.place(Dungeon.hero!!.pos)

                changeMap(MAP_ARENA)
                clearEntities(Room().set(0, 0, 10, 4) as Room) //clear all but the area right around the teleport spot

                //if any allies are left over, move them along the same way as the hero
                for (m in mobs) {
                    if (m.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ALLY) {
                        m.pos += 9 + 3 * 32
                        m.sprite()!!.place(m.pos)
                    }
                }

                tengu!!.state = tengu!!.HUNTING
                do {
                    tengu!!.pos = Random.Int(length())
                } while (solid[tengu!!.pos] || distance(tengu!!.pos, Dungeon.hero!!.pos) < 8)
                GameScene.add(tengu!!)
                tengu!!.notice()

                GameScene.flash(0xFFFFFF)
                Sample.INSTANCE.play(Assets.SND_BLAST)

                state = State.FIGHT_ARENA
            }

        //arena ended, fight over.
            PrisonBossLevel.State.FIGHT_ARENA -> {
                unseal()

                var vis: CustomTiledVisual = exitVisual()
                vis.pos(11, 8)
                customTiles.add(vis)
                (Game.scene() as GameScene).addCustomTile(vis)

                vis = exitVisualWalls()
                vis.pos(11, 8)
                customWalls.add(vis)
                (Game.scene() as GameScene).addCustomWall(vis)

                Dungeon.hero!!.interrupt()
                Dungeon.hero!!.pos = 5 + 27 * 32
                Dungeon.hero!!.sprite!!.interruptMotion()
                Dungeon.hero!!.sprite!!.place(Dungeon.hero!!.pos)

                tengu!!.pos = 5 + 28 * 32
                tengu!!.sprite!!.place(5 + 28 * 32)

                //remove all mobs, but preserve allies
                val allies = ArrayList<Mob>()
                for (m in mobs.toTypedArray<Mob>()) {
                    if (m.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ALLY) {
                        allies.add(m)
                        mobs.remove(m)
                    }
                }
                clearEntities(null)

                changeMap(MAP_END)

                for (m in allies) {
                    do {
                        m.pos = Random.IntRange(3, 7) + Random.IntRange(26, 30) * 32
                    } while (findMob(m.pos) != null)
                    m.sprite()!!.place(m.pos)
                    mobs.add(m)
                }

                tengu!!.die(Dungeon.hero!!)

                for (item in storedItems)
                    drop(item, randomPrisonCell())

                GameScene.flash(0xFFFFFF)
                Sample.INSTANCE.play(Assets.SND_BLAST)

                state = State.WON
            }
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        PrisonLevel.addPrisonVisuals(this, visuals)
        return visuals
    }


    class exitVisual : CustomTiledVisual(Assets.PRISON_EXIT) {

        override fun create(): CustomTiledVisual {
            tileW = 12
            tileH = 14
            mapSimpleImage(0, 0)
            return super.create()
        }

        override fun needsRender(pos: Int): Boolean {
            return render[pos].toInt() != 0
        }

        companion object {

            private val render = shortArrayOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        }
    }

    class exitVisualWalls : CustomTiledVisual(Assets.PRISON_EXIT) {

        override fun create(): CustomTiledVisual {
            tileW = 12
            tileH = 14
            mapSimpleImage(4, 0)
            return super.create()
        }

        override fun needsRender(pos: Int): Boolean {
            return render[pos].toInt() != 0
        }

        companion object {
            private val render = shortArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        }
    }

    companion object {

        private val STATE = "state"
        private val TENGU = "tengu"
        private val STORED_ITEMS = "storeditems"

        private val W = Terrain.WALL
        private val D = Terrain.DOOR
        private val L = Terrain.LOCKED_DOOR
        private val ee = Terrain.EMPTY

        private val T = Terrain.INACTIVE_TRAP

        private val E = Terrain.ENTRANCE
        private val X = Terrain.EXIT

        private val M = Terrain.WALL_DECO
        private val P = Terrain.PEDESTAL

        //TODO if I ever need to store more static maps I should externalize them instead of hard-coding
        //Especially as I means I won't be limited to legal identifiers
        private val MAP_START = intArrayOf(W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, E, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, L, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W)

        private val MAP_MAZE = intArrayOf(W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, D, ee, ee, ee, D, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, ee, ee, ee, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, D, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, M, W, W, ee, W, W, M, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, M, W, W, ee, W, W, M, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, M, W, W, ee, W, W, M, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, M, W, W, ee, W, W, M, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, ee, W, W, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, ee, W, W, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, M, W, D, W, M, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, T, T, T, T, T, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, T, T, T, T, T, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, T, T, T, T, T, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, T, T, T, T, T, W, ee, W, W, W, W, W, W, W, W, W, W, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W)

        private val MAP_ARENA = intArrayOf(W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, W, W, M, W, W, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, W, W, ee, ee, ee, W, W, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, W, W, ee, ee, ee, ee, ee, D, ee, ee, ee, D, ee, ee, ee, ee, ee, W, W, ee, ee, ee, ee, W, W, W, W, W, ee, ee, W, W, W, M, ee, ee, ee, ee, W, W, ee, ee, ee, W, W, ee, ee, ee, ee, M, W, W, W, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, W, W, ee, ee, ee, ee, ee, ee, ee, W, W, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, ee, ee, W, W, D, W, W, ee, ee, ee, ee, W, ee, ee, ee, W, ee, ee, ee, ee, W, W, D, W, W, ee, ee, W, W, W, W, W, ee, W, W, ee, ee, ee, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, ee, ee, ee, W, W, ee, W, W, W, W, W, ee, W, W, ee, ee, ee, W, W, ee, ee, ee, ee, ee, M, ee, ee, ee, ee, ee, W, W, ee, ee, ee, W, W, ee, W, W, W, W, W, ee, W, W, ee, ee, ee, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, ee, ee, ee, W, W, ee, W, W, W, W, W, ee, ee, W, W, D, W, W, ee, ee, ee, ee, W, ee, ee, ee, W, ee, ee, ee, ee, W, W, D, W, W, ee, ee, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, ee, W, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, W, W, ee, ee, ee, ee, ee, ee, ee, W, W, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, M, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, W, W, W, W, ee, ee, ee, ee, W, W, ee, ee, ee, W, W, ee, ee, ee, ee, W, W, W, W, ee, ee, W, W, W, W, W, ee, ee, ee, ee, M, W, ee, ee, ee, ee, ee, D, ee, ee, ee, D, ee, ee, ee, ee, ee, W, M, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, W, W, ee, ee, ee, W, W, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, W, W, W, W, W, ee, ee, ee, ee, ee, W, ee, ee, ee, ee, ee, W, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W)

        private val MAP_END = intArrayOf(W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, E, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, M, ee, W, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, ee, X, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, W, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, W, ee, W, W, ee, W, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, W, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, D, ee, D, ee, ee, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, ee, ee, W, ee, W, ee, ee, ee, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, ee, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, M, W, D, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, P, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W)
    }
}
