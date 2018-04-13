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

package com.shatteredpixel.shatteredpixeldungeon.tiles

import android.util.SparseIntArray

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.watabou.utils.Random

import java.util.Arrays
import java.util.HashSet

object DungeonTileSheet {

    private val WIDTH = 16

    //used in cases like map-edge decision making.
    val NULL_TILE = -1


    /**********************************************************************
     * Floor Tiles
     */

    private val GROUND = xy(1, 1)   //32 slots
    val FLOOR = GROUND + 0
    val FLOOR_DECO = GROUND + 1
    val GRASS = GROUND + 2
    val EMBERS = GROUND + 3
    val FLOOR_SP = GROUND + 4

    val FLOOR_ALT_1 = GROUND + 6
    val FLOOR_DECO_ALT = GROUND + 7
    val GRASS_ALT = GROUND + 8
    val EMBERS_ALT = GROUND + 9
    val FLOOR_SP_ALT = GROUND + 10

    val FLOOR_ALT_2 = GROUND + 12

    val ENTRANCE = GROUND + 16
    val EXIT = GROUND + 17
    val WELL = GROUND + 18
    val EMPTY_WELL = GROUND + 19
    val PEDESTAL = GROUND + 20


    /**********************************************************************
     * Water Tiles
     */

    val WATER = xy(1, 3)   //16 slots
    //next 15 slots are all water stitching with ground.

    //These tiles can stitch with water
    var waterStitcheable = HashSet(Arrays.asList(
            Terrain.EMPTY, Terrain.GRASS, Terrain.EMPTY_WELL,
            Terrain.ENTRANCE, Terrain.EXIT, Terrain.EMBERS,
            Terrain.BARRICADE, Terrain.HIGH_GRASS, Terrain.SECRET_TRAP,
            Terrain.TRAP, Terrain.INACTIVE_TRAP, Terrain.EMPTY_DECO,
            Terrain.SIGN, Terrain.WELL, Terrain.STATUE, Terrain.ALCHEMY,
            Terrain.DOOR, Terrain.OPEN_DOOR, Terrain.LOCKED_DOOR
    ))


    /**********************************************************************
     * Chasm Tiles
     */

    val CHASM = xy(1, 4)   //16 tiles
    //chasm stitching visuals...
    val CHASM_FLOOR = CHASM + 1
    val CHASM_FLOOR_SP = CHASM + 2
    val CHASM_WALL = CHASM + 3
    val CHASM_WATER = CHASM + 4

    //tiles that can stitch with chasms (from above), and which visual represents the stitching
    var chasmStitcheable = SparseIntArray(32)

    /**********************************************************************
     * Flat Tiles
     */

    private val FLAT_WALLS = xy(1, 5)   //16 slots
    val FLAT_WALL = FLAT_WALLS + 0
    val FLAT_WALL_DECO = FLAT_WALLS + 1
    val FLAT_BOOKSHELF = FLAT_WALLS + 2

    val FLAT_WALL_ALT = FLAT_WALLS + 4
    val FLAT_WALL_DECO_ALT = FLAT_WALLS + 5
    val FLAT_BOOKSHELF_ALT = FLAT_WALLS + 6

    private val FLAT_DOORS = xy(1, 6)   //16 slots
    val FLAT_DOOR = FLAT_DOORS + 0
    val FLAT_DOOR_OPEN = FLAT_DOORS + 1
    val FLAT_DOOR_LOCKED = FLAT_DOORS + 2
    val UNLOCKED_EXIT = FLAT_DOORS + 3
    val LOCKED_EXIT = FLAT_DOORS + 4

    val FLAT_OTHER = xy(1, 7)   //16 slots
    val FLAT_SIGN = FLAT_OTHER + 0
    val FLAT_STATUE = FLAT_OTHER + 1
    val FLAT_STATUE_SP = FLAT_OTHER + 2
    val FLAT_ALCHEMY_POT = FLAT_OTHER + 3
    val FLAT_BARRICADE = FLAT_OTHER + 4
    val FLAT_HIGH_GRASS = FLAT_OTHER + 5

    val FLAT_HIGH_GRASS_ALT = FLAT_OTHER + 7


    /**********************************************************************
     * Raised Tiles, Lower Layer
     */

    private val RAISED_WALLS = xy(1, 8)   //32 slots
    //+1 for open to the right, +2 for open to the left
    val RAISED_WALL = RAISED_WALLS + 0
    val RAISED_WALL_DECO = RAISED_WALLS + 4
    //wall that appears behind a top/bottom doorway
    val RAISED_WALL_DOOR = RAISED_WALLS + 8
    val RAISED_WALL_BOOKSHELF = RAISED_WALLS + 12

    val RAISED_WALL_ALT = RAISED_WALLS + 16
    val RAISED_WALL_DECO_ALT = RAISED_WALLS + 20
    val RAISED_WALL_BOOKSHELF_ALT = RAISED_WALLS + 28

    //we use an array instead of a collection because the small element count
    // makes array traversal much faster than something like HashSet.contains.

    //These tiles count as wall for the purposes of wall stitching
    private val wallStitcheable = intArrayOf(Terrain.WALL, Terrain.WALL_DECO, Terrain.SECRET_DOOR, Terrain.LOCKED_EXIT, Terrain.UNLOCKED_EXIT, Terrain.BOOKSHELF, NULL_TILE)

    private val RAISED_DOORS = xy(1, 10)  //16 slots
    val RAISED_DOOR = RAISED_DOORS + 0
    val RAISED_DOOR_OPEN = RAISED_DOORS + 1
    val RAISED_DOOR_LOCKED = RAISED_DOORS + 2
    //floor tile that appears on a top/bottom doorway
    val RAISED_DOOR_SIDEWAYS = RAISED_DOORS + 3

    private val doorTiles = intArrayOf(Terrain.DOOR, Terrain.LOCKED_DOOR, Terrain.OPEN_DOOR)

    private val RAISED_OTHER = xy(1, 11)  //16 slots
    val RAISED_SIGN = RAISED_OTHER + 0
    val RAISED_STATUE = RAISED_OTHER + 1
    val RAISED_STATUE_SP = RAISED_OTHER + 2
    val RAISED_ALCHEMY_POT = RAISED_OTHER + 3
    val RAISED_BARRICADE = RAISED_OTHER + 4
    val RAISED_HIGH_GRASS = RAISED_OTHER + 5

    val RAISED_HIGH_GRASS_ALT = RAISED_OTHER + 7


    /**********************************************************************
     * Raised Tiles, Upper Layer
     */

    //+1 for open right, +2 for open right-below, +4 for open left-below, +8 for open left.
    val WALLS_INTERNAL = xy(1, 12)  //32 slots
    private val WALL_INTERNAL = WALLS_INTERNAL + 0
    private val WALL_INTERNAL_WOODEN = WALLS_INTERNAL + 16

    //+1 for open to the down-right, +2 for open to the down-left
    private val WALLS_OVERHANG = xy(1, 14)  //32 slots
    val WALL_OVERHANG = WALLS_OVERHANG + 0
    val DOOR_SIDEWAYS_OVERHANG = WALLS_OVERHANG + 4
    val DOOR_SIDEWAYS_OVERHANG_OPEN = WALLS_OVERHANG + 8
    val DOOR_SIDEWAYS_OVERHANG_LOCKED = WALLS_OVERHANG + 12
    val WALL_OVERHANG_WOODEN = WALLS_OVERHANG + 16

    //no attachment to adjacent walls
    val DOOR_OVERHANG = WALL_OVERHANG + 21
    val DOOR_OVERHANG_OPEN = WALL_OVERHANG + 22
    val DOOR_SIDEWAYS = WALL_OVERHANG + 23
    val DOOR_SIDEWAYS_LOCKED = WALL_OVERHANG + 24

    val STATUE_OVERHANG = WALL_OVERHANG + 26
    val ALCHEMY_POT_OVERHAND = WALL_OVERHANG + 27
    val BARRICADE_OVERHANG = WALL_OVERHANG + 28
    val HIGH_GRASS_OVERHANG = WALL_OVERHANG + 29

    val HIGH_GRASS_OVERHANG_ALT = WALL_OVERHANG + 31

    /**********************************************************************
     * Logic for the selection of tile visuals
     */

    //These visuals always directly represent a game tile with no stitching required
    var directVisuals = SparseIntArray(32)

    //These visuals directly represent game tiles (no stitching) when terrain is being shown as flat
    var directFlatVisuals = SparseIntArray(32)


    /**********************************************************************
     * Logic for the selection of alternate tile visuals
     */

    var tileVariance: ByteArray

    //These alt visuals will trigger 50% of the time (45% of the time if a rare alt is also present)
    var commonAltVisuals = SparseIntArray(32)

    //These alt visuals trigger 5% of the time (and also override common alts when they show up)
    var rareAltVisuals = SparseIntArray(32)

    private fun xy(x: Int, y: Int): Int {
        var x = x
        var y = y
        x -= 1
        y -= 1
        return x + WIDTH * y
    }

    //+1 for ground above, +2 for ground right, +4 for ground below, +8 for ground left.
    fun stitchWaterTile(top: Int, right: Int, bottom: Int, left: Int): Int {
        var result = WATER
        if (waterStitcheable.contains(top)) result += 1
        if (waterStitcheable.contains(right)) result += 2
        if (waterStitcheable.contains(bottom)) result += 4
        if (waterStitcheable.contains(left)) result += 8
        return result
    }


    fun floorTile(tile: Int): Boolean {
        return tile == Terrain.WATER || directVisuals.get(tile, CHASM) < CHASM
    }

    init {
        //floor
        chasmStitcheable.put(Terrain.EMPTY, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.GRASS, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.EMBERS, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.EMPTY_WELL, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.HIGH_GRASS, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.EMPTY_DECO, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.SIGN, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.EMPTY_WELL, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.WELL, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.STATUE, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.SECRET_TRAP, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.INACTIVE_TRAP, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.TRAP, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.BOOKSHELF, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.BARRICADE, CHASM_FLOOR)
        chasmStitcheable.put(Terrain.PEDESTAL, CHASM_FLOOR)

        //special floor
        chasmStitcheable.put(Terrain.EMPTY_SP, CHASM_FLOOR_SP)
        chasmStitcheable.put(Terrain.STATUE_SP, CHASM_FLOOR_SP)

        //wall
        chasmStitcheable.put(Terrain.WALL, CHASM_WALL)
        chasmStitcheable.put(Terrain.DOOR, CHASM_WALL)
        chasmStitcheable.put(Terrain.OPEN_DOOR, CHASM_WALL)
        chasmStitcheable.put(Terrain.LOCKED_DOOR, CHASM_WALL)
        chasmStitcheable.put(Terrain.WALL_DECO, CHASM_WALL)

        //water
        chasmStitcheable.put(Terrain.WATER, CHASM_WATER)
    }

    fun stitchChasmTile(above: Int): Int {
        return chasmStitcheable.get(above, CHASM)
    }

    fun wallStitcheable(tile: Int): Boolean {
        for (i in wallStitcheable)
            if (tile == i)
                return true
        return false
    }

    fun getRaisedWallTile(tile: Int, pos: Int, right: Int, below: Int, left: Int): Int {
        var result: Int

        if (below == -1 || wallStitcheable(below))
            return -1
        else if (doorTile(below))
            result = RAISED_WALL_DOOR
        else if (tile == Terrain.WALL || tile == Terrain.SECRET_DOOR)
            result = RAISED_WALL
        else if (tile == Terrain.WALL_DECO)
            result = RAISED_WALL_DECO
        else if (tile == Terrain.BOOKSHELF)
            result = RAISED_WALL_BOOKSHELF
        else
            return -1

        result = getVisualWithAlts(result, pos)

        if (!wallStitcheable(right)) result += 1
        if (!wallStitcheable(left)) result += 2
        return result
    }


    fun getRaisedDoorTile(tile: Int, below: Int): Int {
        return if (wallStitcheable(below))
            RAISED_DOOR_SIDEWAYS
        else if (tile == Terrain.DOOR)
            DungeonTileSheet.RAISED_DOOR
        else if (tile == Terrain.OPEN_DOOR)
            DungeonTileSheet.RAISED_DOOR_OPEN
        else if (tile == Terrain.LOCKED_DOOR)
            DungeonTileSheet.RAISED_DOOR_LOCKED
        else
            -1
    }

    fun doorTile(tile: Int): Boolean {
        for (i in doorTiles)
            if (tile == i)
                return true
        return false
    }

    fun stitchInternalWallTile(tile: Int, right: Int, rightBelow: Int, below: Int, leftBelow: Int, left: Int): Int {
        var result: Int

        if (tile == Terrain.BOOKSHELF || below == Terrain.BOOKSHELF)
            result = WALL_INTERNAL_WOODEN
        else
            result = WALL_INTERNAL

        if (!wallStitcheable(right)) result += 1
        if (!wallStitcheable(rightBelow)) result += 2
        if (!wallStitcheable(leftBelow)) result += 4
        if (!wallStitcheable(left)) result += 8
        return result
    }

    fun stitchWallOverhangTile(tile: Int, rightBelow: Int, below: Int, leftBelow: Int): Int {
        var visual: Int
        if (tile == Terrain.DOOR)
            visual = DOOR_SIDEWAYS_OVERHANG
        else if (tile == Terrain.OPEN_DOOR)
            visual = DOOR_SIDEWAYS_OVERHANG_OPEN
        else if (tile == Terrain.LOCKED_DOOR)
            visual = DOOR_SIDEWAYS_OVERHANG_LOCKED
        else if (below == Terrain.BOOKSHELF)
            visual = WALL_OVERHANG_WOODEN
        else
            visual = WALL_OVERHANG

        if (!wallStitcheable(rightBelow)) visual += 1
        if (!wallStitcheable(leftBelow)) visual += 2

        return visual
    }

    init {
        directVisuals.put(Terrain.EMPTY, FLOOR)
        directVisuals.put(Terrain.GRASS, GRASS)
        directVisuals.put(Terrain.EMPTY_WELL, EMPTY_WELL)
        directVisuals.put(Terrain.ENTRANCE, ENTRANCE)
        directVisuals.put(Terrain.EXIT, EXIT)
        directVisuals.put(Terrain.EMBERS, EMBERS)
        directVisuals.put(Terrain.PEDESTAL, PEDESTAL)
        directVisuals.put(Terrain.EMPTY_SP, FLOOR_SP)

        directVisuals.put(Terrain.SECRET_TRAP, directVisuals.get(Terrain.EMPTY))
        directVisuals.put(Terrain.TRAP, directVisuals.get(Terrain.EMPTY))
        directVisuals.put(Terrain.INACTIVE_TRAP, directVisuals.get(Terrain.EMPTY))

        directVisuals.put(Terrain.EMPTY_DECO, FLOOR_DECO)
        directVisuals.put(Terrain.LOCKED_EXIT, LOCKED_EXIT)
        directVisuals.put(Terrain.UNLOCKED_EXIT, UNLOCKED_EXIT)
        directVisuals.put(Terrain.WELL, WELL)

    }

    init {
        directFlatVisuals.put(Terrain.WALL, FLAT_WALL)
        directFlatVisuals.put(Terrain.DOOR, FLAT_DOOR)
        directFlatVisuals.put(Terrain.OPEN_DOOR, FLAT_DOOR_OPEN)
        directFlatVisuals.put(Terrain.LOCKED_DOOR, FLAT_DOOR_LOCKED)
        directFlatVisuals.put(Terrain.WALL_DECO, FLAT_WALL_DECO)
        directFlatVisuals.put(Terrain.BOOKSHELF, FLAT_BOOKSHELF)
        directFlatVisuals.put(Terrain.SIGN, FLAT_SIGN)
        directFlatVisuals.put(Terrain.STATUE, FLAT_STATUE)
        directFlatVisuals.put(Terrain.STATUE_SP, FLAT_STATUE_SP)
        directFlatVisuals.put(Terrain.ALCHEMY, FLAT_ALCHEMY_POT)
        directFlatVisuals.put(Terrain.BARRICADE, FLAT_BARRICADE)
        directFlatVisuals.put(Terrain.HIGH_GRASS, FLAT_HIGH_GRASS)

        directFlatVisuals.put(Terrain.SECRET_DOOR, directFlatVisuals.get(Terrain.WALL))
    }

    fun setupVariance(size: Int, seed: Long) {
        Random.seed(seed)
        tileVariance = ByteArray(size)
        for (i in tileVariance.indices)
            tileVariance[i] = Random.Int(100).toByte()
        Random.seed()
    }

    init {
        commonAltVisuals.put(FLOOR, FLOOR_ALT_1)
        commonAltVisuals.put(GRASS, GRASS_ALT)
        commonAltVisuals.put(FLAT_WALL, FLAT_WALL_ALT)
        commonAltVisuals.put(EMBERS, EMBERS_ALT)
        commonAltVisuals.put(FLAT_WALL_DECO, FLAT_WALL_DECO_ALT)
        commonAltVisuals.put(FLOOR_SP, FLOOR_SP_ALT)
        commonAltVisuals.put(FLOOR_DECO, FLOOR_DECO_ALT)

        commonAltVisuals.put(FLAT_BOOKSHELF, FLAT_BOOKSHELF_ALT)
        commonAltVisuals.put(FLAT_HIGH_GRASS, FLAT_HIGH_GRASS_ALT)

        commonAltVisuals.put(RAISED_WALL, RAISED_WALL_ALT)
        commonAltVisuals.put(RAISED_WALL_DECO, RAISED_WALL_DECO_ALT)
        commonAltVisuals.put(RAISED_WALL_BOOKSHELF, RAISED_WALL_BOOKSHELF_ALT)

        commonAltVisuals.put(RAISED_HIGH_GRASS, RAISED_HIGH_GRASS_ALT)
        commonAltVisuals.put(HIGH_GRASS_OVERHANG, HIGH_GRASS_OVERHANG_ALT)
    }

    init {
        rareAltVisuals.put(FLOOR, FLOOR_ALT_2)
    }

    fun getVisualWithAlts(visual: Int, pos: Int): Int {
        return if (tileVariance[pos] >= 95 && rareAltVisuals.indexOfKey(visual) >= 0)
            rareAltVisuals.get(visual)
        else if (tileVariance[pos] >= 50 && commonAltVisuals.indexOfKey(visual) >= 0)
            commonAltVisuals.get(visual)
        else
            visual
    }

}
