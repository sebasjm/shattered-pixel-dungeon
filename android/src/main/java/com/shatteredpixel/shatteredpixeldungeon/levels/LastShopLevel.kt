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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LineBuilder
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.noosa.Group

import java.util.ArrayList

class LastShopLevel : RegularLevel() {

    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }

    override fun tilesTex(): String? {
        return Assets.TILES_CITY
    }

    override fun waterTex(): String? {
        return Assets.WATER_CITY
    }

    override fun build(): Boolean {
        feeling = Level.Feeling.CHASM
        if (super.build()) {

            for (i in 0 until length()) {
                if (map!![i] == Terrain.SECRET_DOOR) {
                    map!![i] = Terrain.DOOR
                }
            }

            return true
        } else {
            return false
        }
    }

    override fun initRooms(): ArrayList<Room> {
        val rooms = ArrayList<Room>()

        roomEntrance = EntranceRoom()
        rooms.add(roomEntrance!!)
        rooms.add(ImpShopRoom())
        roomExit = ExitRoom()
        rooms.add(roomExit!!)

        return rooms
    }

    override fun builder(): Builder {
        return LineBuilder()
                .setPathVariance(0f)
                .setPathLength(1f, floatArrayOf(1f))
                .setTunnelLength(floatArrayOf(0f, 0f, 1f), floatArrayOf(1f))
    }

    override fun painter(): Painter {
        return CityPainter()
                .setWater(0.10f, 4)
                .setGrass(0.10f, 3)
    }

    override fun createMob(): Mob? {
        return null
    }

    override fun createMobs() {}

    override fun respawner(): Actor? {
        return null
    }

    override fun createItems() {
        val item = Bones.get()
        if (item != null) {
            var pos: Int
            do {
                pos = pointToCell(roomEntrance!!.random())
            } while (pos == entrance)
            drop(item, pos).type = Heap.Type.REMAINS
        }
    }

    override fun randomRespawnCell(): Int {
        return pointToCell(roomEntrance!!.random())
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(CityLevel::class.java, "water_name")
            Terrain.HIGH_GRASS -> return Messages.get(CityLevel::class.java, "high_grass_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.ENTRANCE -> return Messages.get(CityLevel::class.java, "entrance_desc")
            Terrain.EXIT -> return Messages.get(CityLevel::class.java, "exit_desc")
            Terrain.WALL_DECO, Terrain.EMPTY_DECO -> return Messages.get(CityLevel::class.java, "deco_desc")
            Terrain.EMPTY_SP -> return Messages.get(CityLevel::class.java, "sp_desc")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(CityLevel::class.java, "statue_desc")
            Terrain.BOOKSHELF -> return Messages.get(CityLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        CityLevel.addCityVisuals(this, visuals)
        return visuals
    }
}
