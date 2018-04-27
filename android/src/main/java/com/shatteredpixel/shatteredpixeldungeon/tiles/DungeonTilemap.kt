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
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.Tilemap
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.utils.GameMath
import com.watabou.utils.PathFinder
import com.watabou.utils.PointF

abstract class DungeonTilemap(tex: String) : Tilemap(tex, TextureFilm(tex, SIZE, SIZE)) {

    protected var map: IntArray? = null

    override//we need to retain two arrays, map is the dungeon tilemap which we can reference.
    // Data is our own internal image representation of the tiles, which may differ.
    fun map(data: IntArray, cols: Int) {
        map = data
        super.map(IntArray(data.size), cols)
    }

    @Synchronized
    override fun updateMap() {
        super.updateMap()
        for (i in data.indices)
            data[i] = getTileVisual(i, map!![i], false)
    }

    @Synchronized
    override fun updateMapCell(cell: Int) {
        //update in a 3x3 grid to account for neighbours which might also be affected
        if (Dungeon.level!!.insideMap(cell)) {
            super.updateMapCell(cell - mapWidth - 1)
            super.updateMapCell(cell + mapWidth + 1)
            for (i in PathFinder.NEIGHBOURS9!!)
                data[cell + i] = getTileVisual(cell + i, map!![cell + i], false)

            //unless we're at the level's edge, then just do the one tile.
        } else {
            super.updateMapCell(cell)
            data[cell] = getTileVisual(cell, map!![cell], false)
        }
    }

    protected abstract fun getTileVisual(pos: Int, tile: Int, flat: Boolean): Int

    //wall assist is used to make raised perspective tapping a bit easier.
    // If the pressed tile is a wall tile, the tap can be 'bumped' down into a none-wall tile.
    // currently this happens if the bottom 1/4 of the wall tile is pressed.
    @JvmOverloads
    fun screenToTile(x: Int, y: Int, wallAssist: Boolean = false): Int {
        val p = camera()!!.screenToCamera(x, y).offset(this.point().negate()).invScale(SIZE.toFloat())

        //snap to the edges of the tilemap
        p.x = GameMath.gate(0f, p.x, Dungeon.level!!.width() - 0.001f)
        p.y = GameMath.gate(0f, p.y, Dungeon.level!!.height() - 0.001f)

        var cell = p.x.toInt() + p.y.toInt() * Dungeon.level!!.width()

        if (wallAssist
                && map != null
                && DungeonTileSheet.wallStitcheable(map!![cell])) {

            if (cell + mapWidth < size
                    && p.y % 1 >= 0.75f
                    && !DungeonTileSheet.wallStitcheable(map!![cell + mapWidth])) {
                cell += mapWidth
            }

        }

        return cell
    }

    override fun overlapsPoint(x: Float, y: Float): Boolean {
        return true
    }

    fun discover(pos: Int, oldValue: Int) {

        val visual = getTileVisual(pos, oldValue, false)
        if (visual < 0) return

        val tile = Image(texture)
        tile.frame(tileset.get(getTileVisual(pos, oldValue, false)))
        tile.point(tileToWorld(pos))

        parent!!.add(tile)

        parent!!.add(object : AlphaTweener(tile, 0f, 0.6f) {
            override fun onComplete() {
                tile.killAndErase()
                killAndErase()
            }
        })
    }

    override fun overlapsScreenPoint(x: Int, y: Int): Boolean {
        return true
    }

    companion object {

        val SIZE = 16

        fun tileToWorld(pos: Int): PointF {
            return PointF((pos % Dungeon.level!!.width()).toFloat(), (pos / Dungeon.level!!.width()).toFloat()).scale(SIZE.toFloat())
        }

        fun tileCenterToWorld(pos: Int): PointF {
            return PointF(
                    (pos % Dungeon.level!!.width() + 0.5f) * SIZE,
                    (pos / Dungeon.level!!.width() + 0.5f) * SIZE)
        }

        fun raisedTileCenterToWorld(pos: Int): PointF {
            return PointF(
                    (pos % Dungeon.level!!.width() + 0.5f) * SIZE,
                    (pos / Dungeon.level!!.width() + 0.1f) * SIZE)
        }
    }

}
