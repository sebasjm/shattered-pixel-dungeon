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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet
import com.watabou.noosa.Camera
import com.watabou.noosa.Group
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random
import com.watabou.utils.Rect

class CavesBossLevel : Level() {

    private var arenaDoor: Int = 0
    private var enteredArena = false
    private var keyDropped = false

    init {
        color1 = 0x534f3e
        color2 = 0xb9d661

        viewDistance = Math.min(6, viewDistance)
    }

    override fun tilesTex(): String? {
        return Assets.TILES_CAVES
    }

    override fun waterTex(): String? {
        return Assets.WATER_CAVES
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

        setSize(WIDTH, HEIGHT)

        val space = Rect()

        space.set(
                Random.IntRange(2, 2 + (width * 0.2f).toInt()),
                Random.IntRange(2, 2 + (height * 0.2f).toInt()),
                Random.IntRange((width * 0.8f - 2).toInt(), width - 2),
                Random.IntRange((height * 0.8f - 2).toInt(), height - 2)
        )

        Painter.fillEllipse(this, space, Terrain.EMPTY)

        exit = space.left + space.width() / 2 + (space.top - 1) * width()

        map[exit] = Terrain.LOCKED_EXIT

        Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1,
                ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL)
        Painter.fill(this, ROOM_LEFT, ROOM_TOP + 1,
                ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY)

        Painter.fill(this, ROOM_LEFT, ROOM_TOP,
                ROOM_RIGHT - ROOM_LEFT + 1, 1, Terrain.EMPTY_DECO)

        arenaDoor = Random.Int(ROOM_LEFT, ROOM_RIGHT) + (ROOM_BOTTOM + 1) * width()
        map[arenaDoor] = Terrain.DOOR

        entrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1) + Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * width()
        map[entrance] = Terrain.ENTRANCE

        val patch = Patch.generate(width, height, 0.30f, 6, true)
        for (i in 0 until length()) {
            if (map!![i] == Terrain.EMPTY && patch[i]) {
                map[i] = Terrain.WATER
            }
        }

        for (i in 0 until length()) {
            if (map!![i] == Terrain.EMPTY && Random.Int(6) == 0) {
                map[i] = Terrain.INACTIVE_TRAP
                val t = ToxicTrap().reveal()
                t.active = false
                setTrap(t, i)
            }
        }

        for (i in width() + 1 until length() - width()) {
            if (map!![i] == Terrain.EMPTY) {
                var n = 0
                if (map!![i + 1] == Terrain.WALL) {
                    n++
                }
                if (map!![i - 1] == Terrain.WALL) {
                    n++
                }
                if (map!![i + width()] == Terrain.WALL) {
                    n++
                }
                if (map!![i - width()] == Terrain.WALL) {
                    n++
                }
                if (Random.Int(8) <= n) {
                    map[i] = Terrain.EMPTY_DECO
                }
            }
        }

        for (i in 0 until length() - width()) {
            if (map!![i] == Terrain.WALL
                    && DungeonTileSheet.floorTile(map!![i + width()])
                    && Random.Int(3) == 0) {
                map[i] = Terrain.WALL_DECO
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
        var cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)]
        while (!passable[cell]) {
            cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)]
        }
        return cell
    }

    override fun press(cell: Int, hero: Char) {

        super.press(cell, hero)

        if (!enteredArena && outsideEntraceRoom(cell) && hero === Dungeon.hero) {

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

            val boss = DM300()
            boss.state = boss.WANDERING
            do {
                boss.pos = Random.Int(length())
            } while (!passable[boss.pos] ||
                    !outsideEntraceRoom(boss.pos) ||
                    heroFOV[boss.pos])
            GameScene.add(boss)

            Level.set(arenaDoor, Terrain.WALL)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()

            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            Camera.main.shake(3f, 0.7f)
            Sample.INSTANCE.play(Assets.SND_ROCKS)
        }
    }

    override fun drop(item: Item?, cell: Int): Heap {

        if (!keyDropped && item is SkeletonKey) {

            keyDropped = true
            unseal()

            CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10)

            Level.set(arenaDoor, Terrain.EMPTY_DECO)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }

        return super.drop(item, cell)
    }

    private fun outsideEntraceRoom(cell: Int): Boolean {
        val cx = cell % width()
        val cy = cell / width()
        return cx < ROOM_LEFT - 1 || cx > ROOM_RIGHT + 1 || cy < ROOM_TOP - 1 || cy > ROOM_BOTTOM + 1
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.GRASS -> return Messages.get(CavesLevel::class.java, "grass_name")
            Terrain.HIGH_GRASS -> return Messages.get(CavesLevel::class.java, "high_grass_name")
            Terrain.WATER -> return Messages.get(CavesLevel::class.java, "water_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.ENTRANCE -> return Messages.get(CavesLevel::class.java, "entrance_desc")
            Terrain.EXIT -> return Messages.get(CavesLevel::class.java, "exit_desc")
            Terrain.HIGH_GRASS -> return Messages.get(CavesLevel::class.java, "high_grass_desc")
            Terrain.WALL_DECO -> return Messages.get(CavesLevel::class.java, "wall_deco_desc")
            Terrain.BOOKSHELF -> return Messages.get(CavesLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        CavesLevel.addCavesVisuals(this, visuals)
        return visuals
    }

    companion object {

        private val WIDTH = 32
        private val HEIGHT = 32

        private val ROOM_LEFT = WIDTH / 2 - 3
        private val ROOM_RIGHT = WIDTH / 2 + 1
        private val ROOM_TOP = HEIGHT / 2 - 2
        private val ROOM_BOTTOM = HEIGHT / 2 + 2

        private val DOOR = "door"
        private val ENTERED = "entered"
        private val DROPPED = "droppped"
    }
}
