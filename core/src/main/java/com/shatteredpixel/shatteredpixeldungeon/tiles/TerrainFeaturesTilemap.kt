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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.watabou.noosa.Image
import com.watabou.noosa.tweeners.ScaleTweener
import com.watabou.utils.PointF
import com.watabou.utils.RectF
import com.watabou.utils.SparseArray

//TODO add in a proper set of vfx for plants growing/withering, grass burning, discovering traps
class TerrainFeaturesTilemap(private val plants: SparseArray<Plant>, private val traps: SparseArray<Trap>) : DungeonTilemap(Assets.TERRAIN_FEATURES) {

    init {

        map(Dungeon.level!!.map, Dungeon.level!!.width())

        instance = this
    }

    override fun getTileVisual(pos: Int, tile: Int, flat: Boolean): Int {
        if (traps.get(pos) != null) {
            val trap = traps.get(pos)
            return if (!trap.visible)
                -1
            else
                (if (trap.active) trap.color else Trap.BLACK) + trap.shape * 16
        }

        if (plants.get(pos) != null) {
            return plants.get(pos).image + 7 * 16
        }

        var stage = (Dungeon.depth - 1) / 5
        if (Dungeon.depth == 21) stage--
        if (tile == Terrain.HIGH_GRASS) {
            return 9 + 16 * stage + if (DungeonTileSheet.tileVariance[pos] >= 50) 1 else 0
        } else if (tile == Terrain.GRASS) {
            return 11 + 16 * stage + if (DungeonTileSheet.tileVariance[pos] >= 50) 1 else 0
        } else if (tile == Terrain.EMBERS) {
            return 13 + if (DungeonTileSheet.tileVariance[pos] >= 50) 1 else 0
        }

        return -1
    }

    fun growPlant(pos: Int) {
        val plant = tile(pos, map!![pos]) ?: return

        plant.origin.set(8f, 12f)
        plant.scale.set(0f)
        plant.point(DungeonTilemap.tileToWorld(pos))

        parent!!.add(plant)

        parent!!.add(object : ScaleTweener(plant, PointF(1f, 1f), 0.2f) {
            override fun onComplete() {
                plant.killAndErase()
                killAndErase()
                updateMapCell(pos)
            }
        })
    }

    override fun needsRender(pos: Int): Boolean {
        return data[pos] != -1
    }

    companion object {

        var instance: TerrainFeaturesTilemap? = null

        fun tile(pos: Int, tile: Int): Image? {
            val uv = instance!!.tileset.get(instance!!.getTileVisual(pos, tile, true)) ?: return null

            val img = Image(instance!!.texture)
            img.frame(uv)
            return img
        }
    }
}
