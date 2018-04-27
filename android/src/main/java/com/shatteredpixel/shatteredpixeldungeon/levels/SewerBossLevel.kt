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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.RatKingRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SewerBossEntranceRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class SewerBossLevel : SewerLevel() {

    private var stairs = 0

    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }

    override fun initRooms(): ArrayList<Room> {
        val initRooms = ArrayList<Room>()
        roomExit = SewerBossEntranceRoom()
        roomEntrance = roomExit
        initRooms.add(roomEntrance!!)

        val standards = standardRooms()
        for (i in 0 until standards) {
            initRooms.add(EmptyRoom())
        }

        initRooms.add(RatKingRoom())
        return initRooms
    }

    override fun standardRooms(): Int {
        //2 to 4, average 3
        return 2 + Random.chances(floatArrayOf(1f, 1f, 1f))
    }

    override fun builder(): Builder {
        return LoopBuilder()
                .setPathLength(1f, floatArrayOf(1f))
                .setTunnelLength(floatArrayOf(0f, 3f, 1f), floatArrayOf(1f))
    }

    override fun waterFill(): Float {
        return 0.50f
    }

    override fun waterSmoothing(): Int {
        return 5
    }

    override fun grassFill(): Float {
        return 0.20f
    }

    override fun grassSmoothing(): Int {
        return 4
    }

    override fun nTraps(): Int {
        return 0
    }

    override fun createMobs() {
        val boss = Goo()
        var room: Room?
        do {
            room = randomRoom(StandardRoom::class.java)
        } while (room === roomEntrance)
        boss.pos = pointToCell(room!!.random())
        mobs.add(boss)
    }

    override fun respawner(): Actor? {
        return null
    }

    override fun createItems() {
        val item = Bones.get()
        if (item != null) {
            var pos: Int
            do {
                pos = pointToCell(roomEntrance!!.random())
            } while (pos == entrance || solid[pos])
            drop(item, pos).type = Heap.Type.REMAINS
        }
    }

    override fun randomRespawnCell(): Int {
        var pos: Int
        do {
            pos = pointToCell(roomEntrance!!.random())
        } while (pos == entrance || solid[pos])
        return pos
    }


    override fun seal() {
        if (entrance != 0) {

            super.seal()

            Level.set(entrance, Terrain.WATER)
            GameScene.updateMap(entrance)
            GameScene.ripple(entrance)

            stairs = entrance
            entrance = 0
        }
    }

    override fun unseal() {
        if (stairs != 0) {

            super.unseal()

            entrance = stairs
            stairs = 0

            Level.set(entrance, Terrain.ENTRANCE)
            GameScene.updateMap(entrance)

        }
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STAIRS, stairs)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stairs = bundle.getInt(STAIRS)
        roomExit = roomEntrance
    }

    companion object {

        private val STAIRS = "stairs"
    }
}
