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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.noosa.Group
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.Arrays

class LastLevel : Level() {

    private var pedestal: Int = 0

    init {
        color1 = 0x801500
        color2 = 0xa68521
    }

    override fun tilesTex(): String? {
        return Assets.TILES_HALLS
    }

    override fun waterTex(): String? {
        return Assets.WATER_HALLS
    }

    override fun create() {
        super.create()
        for (i in 0 until length()) {
            val flags = Terrain.flags[map!![i]]
            if (flags and Terrain.PIT != 0) {
                avoid[i] = false
                passable[i] = avoid[i]
                solid[i] = true
            }
        }
    }

    override fun build(): Boolean {

        setSize(16, 64)
        Arrays.fill(map!!, Terrain.CHASM)

        val mid = width / 2

        Painter.fill(this, 0, height - 1, width, 1, Terrain.WALL)
        Painter.fill(this, mid - 1, 10, 3, height - 11, Terrain.EMPTY)
        Painter.fill(this, mid - 2, height - 3, 5, 1, Terrain.EMPTY)
        Painter.fill(this, mid - 3, height - 2, 7, 1, Terrain.EMPTY)

        Painter.fill(this, mid - 2, 9, 5, 7, Terrain.EMPTY)
        Painter.fill(this, mid - 3, 10, 7, 5, Terrain.EMPTY)

        entrance = (height - 2) * width() + mid
        map[entrance] = Terrain.ENTRANCE

        pedestal = 12 * width() + mid
        map[pedestal] = Terrain.PEDESTAL
        map[pedestal + 1 + width()] = Terrain.STATUE_SP
        map[pedestal - 1 + width()] = map[pedestal + 1 + width()]
        map[pedestal + 1 - width()] = map[pedestal - 1 + width()]
        map[pedestal - 1 - width()] = map[pedestal + 1 - width()]

        exit = pedestal

        var pos = pedestal

        map[pos + 2] = Terrain.WATER
        map[pos - 2] = map[pos + 2]
        map[pos + 1] = map[pos - 2]
        map[pos - 1] = map[pos + 1]
        map[pos - width()] = map[pos - 1]
        pos += width()
        map[pos + 3] = Terrain.WATER
        map[pos - 3] = map[pos + 3]
        map[pos + 2] = map[pos - 3]
        map[pos - 2] = map[pos + 2]
        map[pos] = map[pos - 2]
        pos += width()
        map[pos + 3] = Terrain.WATER
        map[pos + 2] = map[pos + 3]
        map[pos + 1] = map[pos + 2]
        map[pos] = map[pos + 1]
        map[pos - 1] = map[pos]
        map[pos - 2] = map[pos - 1]
        map[pos - 3] = map[pos - 2]
        pos += width()
        map[pos + 2] = Terrain.WATER
        map[pos - 2] = map[pos + 2]

        for (i in 0 until length()) {
            if (map!![i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO
            }
        }

        feeling = Level.Feeling.NONE

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
        drop(Amulet(), pedestal)
    }

    override fun randomRespawnCell(): Int {
        var cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)]
        while (!passable[cell]) {
            cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)]
        }
        return cell
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

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (i in 0 until length()) {
            val flags = Terrain.flags[map!![i]]
            if (flags and Terrain.PIT != 0) {
                avoid[i] = false
                passable[i] = avoid[i]
                solid[i] = true
            }
        }
    }
}
