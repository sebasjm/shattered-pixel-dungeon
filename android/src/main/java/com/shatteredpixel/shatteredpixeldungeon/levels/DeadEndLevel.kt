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

class DeadEndLevel : Level() {

    init {
        color1 = 0x534f3e
        color2 = 0xb9d661
    }

    override fun tilesTex(): String? {
        return Assets.TILES_CAVES
    }

    override fun waterTex(): String? {
        return Assets.WATER_HALLS
    }

    override fun build(): Boolean {

        setSize(7, 7)

        for (i in 2 until SIZE) {
            for (j in 2 until SIZE) {
                map!![i * width() + j] = Terrain.EMPTY
            }
        }

        for (i in 1..SIZE) {
            map!![width() * i + SIZE] = Terrain.WATER
            map!![width() * i + 1] = map!![width() * i + SIZE]
            map!![width() * SIZE + i] = map!![width() * i + 1]
            map!![width() + i] = map!![width() * SIZE + i]
        }

        entrance = SIZE * width() + SIZE / 2 + 1
        map!![entrance] = Terrain.ENTRANCE

        exit = 0

        return true
    }

    override fun createMob(): Mob? {
        return null
    }

    override fun createMobs() {}

    override fun respawner(): Actor? {
        return null
    }

    override fun createItems() {}

    override fun randomRespawnCell(): Int {
        return entrance - width()
    }

    companion object {

        private val SIZE = 5
    }

}
