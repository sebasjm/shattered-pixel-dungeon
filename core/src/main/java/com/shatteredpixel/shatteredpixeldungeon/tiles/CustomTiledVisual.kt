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
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

abstract class CustomTiledVisual(tx: Any) : Tilemap(tx, TextureFilm(tx, SIZE, SIZE)), Bundlable {

    var tileX: Int = 0
    var tileY: Int = 0   //x and y coords for texture within a level
    var tileW = 1
    var tileH = 1 //width and height in tiles

    fun pos(pos: Int) {
        pos(pos % Dungeon.level!!.width(), pos / Dungeon.level!!.width())
    }

    fun pos(tileX: Int, tileY: Int) {
        this.tileX = tileX
        this.tileY = tileY
    }

    fun setRect(topLeft: Int, bottomRight: Int) {
        setRect(topLeft % Dungeon.level!!.width(),
                topLeft / Dungeon.level!!.width(),
                bottomRight % Dungeon.level!!.width() - topLeft % Dungeon.level!!.width(),
                bottomRight / Dungeon.level!!.width() - topLeft / Dungeon.level!!.width()
        )
    }

    fun setRect(tileX: Int, tileY: Int, tileW: Int, tileH: Int) {
        this.tileX = tileX
        this.tileY = tileY
        this.tileW = tileW
        this.tileH = tileH
    }

    open fun create(): CustomTiledVisual {
        camera = null
        x = (tileX * SIZE).toFloat()
        y = (tileY * SIZE).toFloat()
        return this
    }

    //assumes that width and height are already set.
    protected fun mapSimpleImage(txX: Int, txY: Int) {
        val data = IntArray(tileW * tileH)
        val texTileWidth = texture.width / SIZE
        var x = txX
        var y = txY
        for (i in data.indices) {
            data[i] = x + texTileWidth * y

            x++
            if (x - txX == tileW) {
                x = txX
                y++
            }
        }

        map(data, tileW)
    }

    //x and y here are the coordinates tapped within the tile visual
    open fun image(tileX: Int, tileY: Int): Image? {
        if (!needsRender(tileX + mapWidth * tileY)) {
            return null
        } else {
            val img = Image(texture)
            img.frame(tileset.get(data[tileX + mapWidth * tileY]))
            return img
        }
    }

    open fun name(tileX: Int, tileY: Int): String? {
        return null
    }

    open fun desc(tileX: Int, tileY: Int): String? {
        return null
    }

    override fun restoreFromBundle(bundle: Bundle) {
        tileX = bundle.getInt(TILE_X)
        tileY = bundle.getInt(TILE_Y)

        tileW = bundle.getInt(TILE_W)
        tileH = bundle.getInt(TILE_H)
    }

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(TILE_X, tileX)
        bundle.put(TILE_Y, tileY)

        bundle.put(TILE_W, tileW)
        bundle.put(TILE_H, tileH)
    }

    companion object {

        protected val SIZE = DungeonTilemap.SIZE

        private val TILE_X = "tileX"
        private val TILE_Y = "tileY"

        private val TILE_W = "tileW"
        private val TILE_H = "tileH"
    }
}
