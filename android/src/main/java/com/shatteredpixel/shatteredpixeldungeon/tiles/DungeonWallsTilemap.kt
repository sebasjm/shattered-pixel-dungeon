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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain

class DungeonWallsTilemap : DungeonTilemap(Dungeon.level!!.tilesTex()!!) {
    init {
        map(Dungeon.level!!.map, Dungeon.level!!.width())
    }

    override fun getTileVisual(pos: Int, tile: Int, flat: Boolean): Int {

        if (flat) return -1

        if (DungeonTileSheet.wallStitcheable(tile)) {
            if (pos + mapWidth < size && !DungeonTileSheet.wallStitcheable(map!![pos + mapWidth])) {

                if (map!![pos + mapWidth] == Terrain.DOOR) {
                    return DungeonTileSheet.DOOR_SIDEWAYS
                } else if (map!![pos + mapWidth] == Terrain.LOCKED_DOOR) {
                    return DungeonTileSheet.DOOR_SIDEWAYS_LOCKED
                } else if (map!![pos + mapWidth] == Terrain.OPEN_DOOR) {
                    return DungeonTileSheet.NULL_TILE
                }

            } else {
                return DungeonTileSheet.stitchInternalWallTile(
                        tile,
                        if ((pos + 1) % mapWidth != 0) map!![pos + 1] else -1,
                        if ((pos + 1) % mapWidth != 0 && pos + mapWidth < size) map!![pos + 1 + mapWidth] else -1,
                        if (pos + mapWidth < size) map!![pos + mapWidth] else -1,
                        if (pos % mapWidth != 0 && pos + mapWidth < size) map!![pos - 1 + mapWidth] else -1,
                        if (pos % mapWidth != 0) map!![pos - 1] else -1
                )
            }

        }

        if (pos + mapWidth < size && DungeonTileSheet.wallStitcheable(map!![pos + mapWidth])) {

            return DungeonTileSheet.stitchWallOverhangTile(
                    tile,
                    if ((pos + 1) % mapWidth != 0) map!![pos + 1 + mapWidth] else -1,
                    map!![pos + mapWidth],
                    if (pos % mapWidth != 0) map!![pos - 1 + mapWidth] else -1
            )

        } else if (Dungeon.level!!.insideMap(pos) && (map!![pos + mapWidth] == Terrain.DOOR || map!![pos + mapWidth] == Terrain.LOCKED_DOOR)) {
            return DungeonTileSheet.DOOR_OVERHANG
        } else if (Dungeon.level!!.insideMap(pos) && map!![pos + mapWidth] == Terrain.OPEN_DOOR) {
            return DungeonTileSheet.DOOR_OVERHANG_OPEN
        } else if (pos + mapWidth < size && (map!![pos + mapWidth] == Terrain.STATUE || map!![pos + mapWidth] == Terrain.STATUE_SP)) {
            return DungeonTileSheet.STATUE_OVERHANG
        } else if (pos + mapWidth < size && map!![pos + mapWidth] == Terrain.ALCHEMY) {
            return DungeonTileSheet.ALCHEMY_POT_OVERHAND
        } else if (pos + mapWidth < size && map!![pos + mapWidth] == Terrain.BARRICADE) {
            return DungeonTileSheet.BARRICADE_OVERHANG
        } else if (pos + mapWidth < size && map!![pos + mapWidth] == Terrain.HIGH_GRASS) {
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.HIGH_GRASS_OVERHANG, pos + mapWidth)
        }

        return -1
    }

    override fun overlapsPoint(x: Float, y: Float): Boolean {
        return true
    }

    override fun overlapsScreenPoint(x: Int, y: Int): Boolean {
        return true
    }

    override fun needsRender(pos: Int): Boolean {
        return data[pos] != -1
    }
}
