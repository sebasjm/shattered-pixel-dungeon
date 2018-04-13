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

object Terrain {

    val CHASM = 0
    val EMPTY = 1
    val GRASS = 2
    val EMPTY_WELL = 3
    val WALL = 4
    val DOOR = 5
    val OPEN_DOOR = 6
    val ENTRANCE = 7
    val EXIT = 8
    val EMBERS = 9
    val LOCKED_DOOR = 10
    val PEDESTAL = 11
    val WALL_DECO = 12
    val BARRICADE = 13
    val EMPTY_SP = 14
    val HIGH_GRASS = 15

    val SECRET_DOOR = 16
    val SECRET_TRAP = 17
    val TRAP = 18
    val INACTIVE_TRAP = 19

    val EMPTY_DECO = 20
    val LOCKED_EXIT = 21
    val UNLOCKED_EXIT = 22
    val SIGN = 23
    val WELL = 24
    val STATUE = 25
    val STATUE_SP = 26
    val BOOKSHELF = 27
    val ALCHEMY = 28

    val WATER = 29

    val PASSABLE = 0x01
    val LOS_BLOCKING = 0x02
    val FLAMABLE = 0x04
    val SECRET = 0x08
    val SOLID = 0x10
    val AVOID = 0x20
    val LIQUID = 0x40
    val PIT = 0x80

    val flags = IntArray(256)

    init {
        flags[CHASM] = AVOID or PIT
        flags[EMPTY] = PASSABLE
        flags[GRASS] = PASSABLE or FLAMABLE
        flags[EMPTY_WELL] = PASSABLE
        flags[WATER] = PASSABLE or LIQUID
        flags[WALL] = LOS_BLOCKING or SOLID
        flags[DOOR] = PASSABLE or LOS_BLOCKING or FLAMABLE or SOLID
        flags[OPEN_DOOR] = PASSABLE or FLAMABLE
        flags[ENTRANCE] = PASSABLE/* | SOLID*/
        flags[EXIT] = PASSABLE
        flags[EMBERS] = PASSABLE
        flags[LOCKED_DOOR] = LOS_BLOCKING or SOLID
        flags[PEDESTAL] = PASSABLE
        flags[WALL_DECO] = flags[WALL]
        flags[BARRICADE] = FLAMABLE or SOLID or LOS_BLOCKING
        flags[EMPTY_SP] = flags[EMPTY]
        flags[HIGH_GRASS] = PASSABLE or LOS_BLOCKING or FLAMABLE

        flags[SECRET_DOOR] = flags[WALL] or SECRET
        flags[SECRET_TRAP] = flags[EMPTY] or SECRET
        flags[TRAP] = AVOID
        flags[INACTIVE_TRAP] = flags[EMPTY]

        flags[EMPTY_DECO] = flags[EMPTY]
        flags[LOCKED_EXIT] = SOLID
        flags[UNLOCKED_EXIT] = PASSABLE
        flags[SIGN] = PASSABLE or FLAMABLE
        flags[WELL] = AVOID
        flags[STATUE] = SOLID
        flags[STATUE_SP] = flags[STATUE]
        flags[BOOKSHELF] = flags[BARRICADE]
        flags[ALCHEMY] = SOLID

    }

    fun discover(terr: Int): Int {
        when (terr) {
            SECRET_DOOR -> return DOOR
            SECRET_TRAP -> return TRAP
            else -> return terr
        }
    }

    //removes signs, places floors instead
    fun convertTilesFrom0_6_0b(map: IntArray): IntArray {
        for (i in map.indices) {
            if (map[i] == 23) {
                map[i] = 1
            }
        }
        return map
    }

}
