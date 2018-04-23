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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.King
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet
import com.watabou.noosa.Group
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class CityBossLevel : Level() {

    private var arenaDoor: Int = 0
    private var enteredArena = false
    private var keyDropped = false

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

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DOOR, arenaDoor)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        arenaDoor = bundle.getInt(DOOR)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }

    override fun build(): Boolean {

        setSize(32, 32)

        Painter.fill(this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY)
        Painter.fill(this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY_SP)

        var y = TOP + 1
        while (y < TOP + HALL_HEIGHT) {
            map!![y * width() + CENTER - 2] = Terrain.STATUE_SP
            map!![y * width() + CENTER + 2] = Terrain.STATUE_SP
            y += 2
        }

        val left = pedestal(true)
        val right = pedestal(false)
        map!![right] = Terrain.PEDESTAL
        map!![left] = map!![right]
        for (i in left + 1 until right) {
            map!![i] = Terrain.EMPTY_SP
        }

        exit = (TOP - 1) * width() + CENTER
        map!![exit] = Terrain.LOCKED_EXIT

        arenaDoor = (TOP + HALL_HEIGHT) * width() + CENTER
        map!![arenaDoor] = Terrain.DOOR

        Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY)
        Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, 1, Terrain.BOOKSHELF)
        map!![arenaDoor + width()] = Terrain.EMPTY
        Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF)
        Painter.fill(this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF)

        entrance = (TOP + HALL_HEIGHT + 3 + Random.Int(CHAMBER_HEIGHT - 2)) * width() + LEFT + Random.Int(HALL_WIDTH - 2)
        map!![entrance] = Terrain.ENTRANCE

        for (i in 0 until length() - width()) {
            if (map!![i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map!![i] = Terrain.EMPTY_DECO
            } else if (map!![i] == Terrain.WALL
                    && DungeonTileSheet.floorTile(map!![i + width()])
                    && Random.Int(21 - Dungeon.depth) == 0) {
                map!![i] = Terrain.WALL_DECO
            }
        }

        return true
    }

    fun pedestal(left: Boolean): Int {
        return if (left) {
            (TOP + HALL_HEIGHT / 2) * width() + CENTER - 2
        } else {
            (TOP + HALL_HEIGHT / 2) * width() + CENTER + 2
        }
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
                pos = Random.IntRange(LEFT + 1, LEFT + HALL_WIDTH - 2) + Random.IntRange(TOP + HALL_HEIGHT + 1, TOP + HALL_HEIGHT + CHAMBER_HEIGHT) * width()
            } while (pos == entrance)
            drop(item, pos).type = Heap.Type.REMAINS
        }
    }

    override fun randomRespawnCell(): Int {
        var cell = entrance + PathFinder.NEIGHBOURS8!![Random.Int(8)]
        while (!passable[cell]) {
            cell = entrance + PathFinder.NEIGHBOURS8!![Random.Int(8)]
        }
        return cell
    }

    override fun press(cell: Int, hero: Char) {

        super.press(cell, hero)

        if (!enteredArena && outsideEntraceRoom(cell) && hero === Dungeon.hero!!) {

            enteredArena = true
            seal()

            for (m in mobs) {
                //bring the first ally with you
                if (m.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ALLY) {
                    m.pos = Dungeon.hero!!.pos + if (Random.Int(2) == 0) +1 else -1
                    m.sprite!!.place(m.pos)
                    break
                }
            }

            val boss = King()
            boss.state = boss.WANDERING
            var count = 0
            do {
                boss.pos = Random.Int(length())
            } while (!passable[boss.pos] ||
                    !outsideEntraceRoom(boss.pos) ||
                    heroFOV[boss.pos] && count++ < 20)
            GameScene.add(boss)

            if (heroFOV[boss.pos]) {
                boss.notice()
                boss.sprite!!.alpha(0f)
                boss.sprite!!.parent!!.add(AlphaTweener(boss.sprite!!, 1f, 0.1f))
            }

            Level.set(arenaDoor, Terrain.LOCKED_DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
    }

    override fun drop(item: Item?, cell: Int): Heap {

        if (!keyDropped && item is SkeletonKey) {

            keyDropped = true
            unseal()

            Level.set(arenaDoor, Terrain.DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }

        return super.drop(item, cell)
    }

    private fun outsideEntraceRoom(cell: Int): Boolean {
        return cell / width() < arenaDoor / width()
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

    companion object {

        private val TOP = 2
        private val HALL_WIDTH = 7
        private val HALL_HEIGHT = 15
        private val CHAMBER_HEIGHT = 4

        private val WIDTH = 32

        private val LEFT = (WIDTH - HALL_WIDTH) / 2
        private val CENTER = LEFT + HALL_WIDTH / 2

        private val DOOR = "door"
        private val ENTERED = "entered"
        private val DROPPED = "droppped"
    }
}
