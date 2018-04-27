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

package com.shatteredpixel.shatteredpixeldungeon.windows

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Image

class WndInfoCell(cell: Int) : Window() {

    init {

        var tile = Dungeon.level!!.map!![cell]
        if (Dungeon.level!!.water[cell]) {
            tile = Terrain.WATER
        } else if (Dungeon.level!!.pit[cell]) {
            tile = Terrain.CHASM
        }

        var customTile: CustomTiledVisual? = null
        var customImage: Image? = null
        var x = cell % Dungeon.level!!.width()
        var y = cell / Dungeon.level!!.width()
        for (i in Dungeon.level!!.customTiles) {
            if (x >= i.tileX && x < i.tileX + i.tileW && y >= i.tileY && y < i.tileY + i.tileH) {
                customImage = i.image(x - i.tileX, y - i.tileY)
                if (customImage != null) {
                    x -= i.tileX
                    y -= i.tileY
                    customTile = i
                    break
                }
            }
        }


        var desc = ""

        val titlebar = IconTitle()
        if (customTile != null) {
            titlebar.icon(customImage!!)

            val customName = customTile.name(x, y)
            if (customName != null) {
                titlebar.label(customName)
            } else {
                titlebar.label(Dungeon.level!!.tileName(tile))
            }

            val customDesc = customTile.desc(x, y)
            if (customDesc != null) {
                desc += customDesc
            } else {
                desc += Dungeon.level!!.tileDesc(tile)
            }

        } else {

            if (tile == Terrain.WATER) {
                val water = Image(Dungeon.level!!.waterTex()!!)
                water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE)
                titlebar.icon(water)
            } else {
                titlebar.icon(DungeonTerrainTilemap.tile(cell, tile))
            }
            titlebar.label(Dungeon.level!!.tileName(tile))
            desc += Dungeon.level!!.tileDesc(tile)

        }
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        val info = PixelScene.renderMultiline(6)
        add(info)

        for (blob in Dungeon.level!!.blobs.values) {
            if (blob.volume > 0 && blob.cur!![cell] > 0 && blob.tileDesc() != null) {
                if (desc.length > 0) {
                    desc += "\n\n"
                }
                desc += blob.tileDesc()
            }
        }

        info.text(if (desc.length == 0) Messages.get(this.javaClass, "nothing") else desc)
        info.maxWidth(WIDTH)
        info.setPos(titlebar.left(), titlebar.bottom() + GAP)

        resize(WIDTH, (info.top() + info.height()).toInt())
    }

    companion object {

        private val GAP = 2f

        private val WIDTH = 120
    }
}
