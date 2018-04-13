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
import com.watabou.noosa.Image
import com.watabou.utils.PathFinder

class DungeonTerrainTilemap : DungeonTilemap(Dungeon.level!!.tilesTex()) {
    init {

        map(Dungeon.level!!.map, Dungeon.level!!.width())

        instance = this
    }

    override fun getTileVisual(pos: Int, tile: Int, flat: Boolean): Int {
        val visual = DungeonTileSheet.directVisuals.get(tile, -1)
        if (visual != -1) return DungeonTileSheet.getVisualWithAlts(visual, pos)

        if (tile == Terrain.WATER) {
            return DungeonTileSheet.stitchWaterTile(
                    map!![pos + PathFinder.CIRCLE4[0]],
                    map!![pos + PathFinder.CIRCLE4[1]],
                    map!![pos + PathFinder.CIRCLE4[2]],
                    map!![pos + PathFinder.CIRCLE4[3]]
            )

        } else if (tile == Terrain.CHASM) {
            return DungeonTileSheet.stitchChasmTile(if (pos > mapWidth) map!![pos - mapWidth] else -1)
        }

        return if (!flat) {
            if (DungeonTileSheet.doorTile(tile)) {
                DungeonTileSheet.getRaisedDoorTile(tile, map!![pos - mapWidth])
            } else if (DungeonTileSheet.wallStitcheable(tile)) {
                DungeonTileSheet.getRaisedWallTile(
                        tile,
                        pos,
                        if ((pos + 1) % mapWidth != 0) map!![pos + 1] else -1,
                        if (pos + mapWidth < size) map!![pos + mapWidth] else -1,
                        if (pos % mapWidth != 0) map!![pos - 1] else -1
                )
            } else if (tile == Terrain.SIGN) {
                DungeonTileSheet.RAISED_SIGN
            } else if (tile == Terrain.STATUE) {
                DungeonTileSheet.RAISED_STATUE
            } else if (tile == Terrain.STATUE_SP) {
                DungeonTileSheet.RAISED_STATUE_SP
            } else if (tile == Terrain.ALCHEMY) {
                DungeonTileSheet.RAISED_ALCHEMY_POT
            } else if (tile == Terrain.BARRICADE) {
                DungeonTileSheet.RAISED_BARRICADE
            } else if (tile == Terrain.HIGH_GRASS) {
                DungeonTileSheet.getVisualWithAlts(
                        DungeonTileSheet.RAISED_HIGH_GRASS,
                        pos)
            } else {
                DungeonTileSheet.NULL_TILE
            }
        } else {
            DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.directFlatVisuals.get(tile),
                    pos)
        }

    }

    override fun needsRender(pos: Int): Boolean {
        return data[pos] >= 0 && data[pos] != DungeonTileSheet.WATER
    }

    companion object {

        internal var instance: DungeonTerrainTilemap

        fun tile(pos: Int, tile: Int): Image {
            val img = Image(instance.texture)
            img.frame(instance.tileset.get(instance.getTileVisual(pos, tile, true)))
            return img
        }
    }
}
