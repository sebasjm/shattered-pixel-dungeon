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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Yog
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.noosa.Group
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class HallsBossLevel : Level() {

    private var stairs = -1
    private var enteredArena = false
    private var keyDropped = false

    init {
        color1 = 0x801500
        color2 = 0xa68521

        viewDistance = Math.min(4, viewDistance)
    }

    override fun tilesTex(): String? {
        return Assets.TILES_HALLS
    }

    override fun waterTex(): String? {
        return Assets.WATER_HALLS
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STAIRS, stairs)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stairs = bundle.getInt(STAIRS)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }

    override fun build(): Boolean {

        setSize(32, 32)

        for (i in 0..4) {

            val top = Random.IntRange(2, ROOM_TOP - 1)
            val bottom = Random.IntRange(ROOM_BOTTOM + 1, 22)
            Painter.fill(this, 2 + i * 4, top, 4, bottom - top + 1, Terrain.EMPTY)

            if (i == 2) {
                exit = i * 4 + 3 + (top - 1) * width()
            }

            for (j in 0..3) {
                if (Random.Int(2) == 0) {
                    val y = Random.IntRange(top + 1, bottom - 1)
                    map[i * 4 + j + y * width()] = Terrain.WALL_DECO
                }
            }
        }

        map[exit] = Terrain.LOCKED_EXIT

        Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1,
                ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL)
        Painter.fill(this, ROOM_LEFT, ROOM_TOP,
                ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP + 1, Terrain.EMPTY)

        entrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1) + Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * width()
        map[entrance] = Terrain.ENTRANCE

        val patch = Patch.generate(width, height, 0.30f, 6, true)
        for (i in 0 until length()) {
            if (map!![i] == Terrain.EMPTY && patch[i]) {
                map[i] = Terrain.WATER
            }
        }

        for (i in 0 until length()) {
            if (map!![i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO
            }
        }

        return true
    }

    override fun createMob(): Mob? {
        return null
    }

    override fun createMobs() {}

    override fun respawner(): Actor {
        return null
    }

    override fun createItems() {
        val item = Bones.get()
        if (item != null) {
            var pos: Int
            do {
                pos = Random.IntRange(ROOM_LEFT, ROOM_RIGHT) + Random.IntRange(ROOM_TOP + 1, ROOM_BOTTOM) * width()
            } while (pos == entrance)
            drop(item, pos).type = Heap.Type.REMAINS
        }
    }

    override fun randomRespawnCell(): Int {
        val pos = if (entrance == -1) stairs else entrance
        var cell = pos + PathFinder.NEIGHBOURS8[Random.Int(8)]
        while (!passable[cell]) {
            cell = pos + PathFinder.NEIGHBOURS8[Random.Int(8)]
        }
        return cell
    }

    override fun press(cell: Int, hero: Char) {

        super.press(cell, hero)

        if (!enteredArena && hero === Dungeon.hero && cell != entrance) {

            enteredArena = true
            seal()

            for (i in ROOM_LEFT - 1..ROOM_RIGHT + 1) {
                doMagic((ROOM_TOP - 1) * width() + i)
                doMagic((ROOM_BOTTOM + 1) * width() + i)
            }
            for (i in ROOM_TOP until ROOM_BOTTOM + 1) {
                doMagic(i * width() + ROOM_LEFT - 1)
                doMagic(i * width() + ROOM_RIGHT + 1)
            }
            doMagic(entrance)
            GameScene.updateMap()

            Dungeon.observe()

            val boss = Yog()
            do {
                boss.pos = Random.Int(length())
            } while (!passable[boss.pos] || heroFOV[boss.pos])
            GameScene.add(boss)
            boss.spawnFists()

            stairs = entrance
            entrance = -1
        }
    }

    private fun doMagic(cell: Int) {
        Level.set(cell, Terrain.EMPTY_SP)
        CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.1f, 3)
    }

    override fun drop(item: Item?, cell: Int): Heap {

        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            unseal()

            entrance = stairs
            Level.set(entrance, Terrain.ENTRANCE)
            GameScene.updateMap(entrance)
        }

        return super.drop(item, cell)
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(HallsLevel::class.java, "water_name")
            Terrain.GRASS -> return Messages.get(HallsLevel::class.java, "grass_name")
            Terrain.HIGH_GRASS -> return Messages.get(HallsLevel::class.java, "high_grass_name")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(HallsLevel::class.java, "statue_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(HallsLevel::class.java, "water_desc")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(HallsLevel::class.java, "statue_desc")
            Terrain.BOOKSHELF -> return Messages.get(HallsLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        HallsLevel.addHallsVisuals(this, visuals)
        return visuals
    }

    companion object {

        private val WIDTH = 32
        private val HEIGHT = 32

        private val ROOM_LEFT = WIDTH / 2 - 1
        private val ROOM_RIGHT = WIDTH / 2 + 1
        private val ROOM_TOP = HEIGHT / 2 - 1
        private val ROOM_BOTTOM = HEIGHT / 2 + 1

        private val STAIRS = "stairs"
        private val ENTERED = "entered"
        private val DROPPED = "droppped"
    }
}
