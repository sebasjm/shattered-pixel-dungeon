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
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.watabou.gltextures.BufferTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Image
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.NoosaScriptNoLighting
import com.watabou.utils.Rect

import java.util.ArrayList

class FogOfWar
/*
	TODO currently the center of each fox pixel is aligned with the inside of a cell
	might be possible to create a better fog effect by aligning them with edges of a cell,
	similar to the existing fog effect in vanilla (although probably with more precision)
	the advantage here is that it may be possible to totally eliminate the tile blocking map
	*/
(private val mapWidth: Int, private val mapHeight: Int) : Image() {
    private val mapLength: Int

    private val pWidth: Int
    private val pHeight: Int

    private var width2: Int = 0
    private var height2: Int = 0

    @Volatile
    private var toUpdate: ArrayList<Rect>? = null
    @Volatile
    private var updating: ArrayList<Rect>? = null

    private var visible: BooleanArray? = null
    private var visited: BooleanArray? = null
    private var mapped: BooleanArray? = null
    private var brightness: Int = 0

    init {
        mapLength = mapHeight * mapWidth

        pWidth = mapWidth * PIX_PER_TILE
        pHeight = mapHeight * PIX_PER_TILE

        width2 = 1
        while (width2 < pWidth) {
            width2 = width2 shl 1
        }

        height2 = 1
        while (height2 < pHeight) {
            height2 = height2 shl 1
        }

        val size = (DungeonTilemap.SIZE / PIX_PER_TILE).toFloat()
        width = width2 * size
        height = height2 * size

        val tx = BufferTexture(width2, height2)
        TextureCache.add(FogOfWar::class.java, tx)
        texture(tx)

        scale.set(
                (DungeonTilemap.SIZE / PIX_PER_TILE).toFloat(),
                (DungeonTilemap.SIZE / PIX_PER_TILE).toFloat())

        toUpdate = ArrayList()
        toUpdate!!.add(Rect(0, 0, mapWidth, mapHeight))
    }

    @Synchronized
    fun updateFog() {
        toUpdate!!.clear()
        toUpdate!!.add(Rect(0, 0, mapWidth, mapHeight))
    }

    @Synchronized
    fun updateFog(update: Rect) {
        for (r in toUpdate!!.toTypedArray<Rect>()) {
            if (!r.intersect(update).isEmpty) {
                toUpdate!!.remove(r)
                toUpdate!!.add(r.union(update))
                return
            }
        }
        toUpdate!!.add(update)
    }

    @Synchronized
    fun updateFog(cell: Int, radius: Int) {
        val update = Rect(
                cell % mapWidth - radius,
                cell / mapWidth - radius,
                cell % mapWidth - radius + 1 + 2 * radius,
                cell / mapWidth - radius + 1 + 2 * radius)
        update.left = Math.max(0, update.left)
        update.top = Math.max(0, update.top)
        update.right = Math.min(mapWidth, update.right)
        update.bottom = Math.min(mapHeight, update.bottom)
        if (update.isEmpty) return
        updateFog(update)
    }

    @Synchronized
    fun updateFogArea(x: Int, y: Int, w: Int, h: Int) {
        updateFog(Rect(x, y, x + w, y + h))
    }

    @Synchronized
    private fun moveToUpdating() {
        updating = toUpdate
        toUpdate = ArrayList()
    }

    private fun updateTexture(visible: BooleanArray, visited: BooleanArray?, mapped: BooleanArray?) {
        this.visible = visible
        this.visited = visited
        this.mapped = mapped
        this.brightness = SPDSettings.brightness() + 2

        moveToUpdating()

        var fullUpdate = false
        if (updating!!.size == 1) {
            val update = updating!![0]
            if (update.height() == mapHeight && update.width() == mapWidth) {
                fullUpdate = true
            }
        }

        val fog = texture as BufferTexture

        var cell: Int

        for (update in updating!!) {
            for (i in update.top..update.bottom) {
                cell = mapWidth * i + update.left
                for (j in update.left..update.right) {

                    if (cell >= Dungeon.level!!.length()) continue //do nothing

                    if (!Dungeon.level!!.discoverable[cell] || !visible[cell] && !visited!![cell] && !mapped!![cell]) {
                        //we skip filling cells here if it isn't a full update
                        // because they must already be dark
                        if (fullUpdate)
                            fillCell(fog, j, i, FOG_COLORS[INVISIBLE][brightness])
                        cell++
                        continue
                    }

                    //wall tiles
                    if (wall(cell)) {

                        //always dark if nothing is beneath them
                        if (cell + mapWidth >= mapLength) {
                            fillCell(fog, j, i, FOG_COLORS[INVISIBLE][brightness])

                            //internal wall tiles, need to check both the left and right side,
                            // to account for only one half of them being seen
                        } else if (wall(cell + mapWidth)) {

                            //left side
                            if (cell % mapWidth != 0) {

                                //picks the darkest fog between current tile, left, and below-left(if left is a wall).
                                if (wall(cell - 1)) {

                                    //if below-left is also a wall, then we should be dark no matter what.
                                    if (wall(cell + mapWidth - 1)) {
                                        fillLeft(fog, j, i, FOG_COLORS[INVISIBLE][brightness])
                                    } else {
                                        fillLeft(fog, j, i, FOG_COLORS[Math.max(getCellFog(cell), Math.max(getCellFog(cell + mapWidth - 1), getCellFog(cell - 1)))][brightness])
                                    }

                                } else {
                                    fillLeft(fog, j, i, FOG_COLORS[Math.max(getCellFog(cell), getCellFog(cell - 1))][brightness])
                                }

                            } else {
                                fillLeft(fog, j, i, FOG_COLORS[INVISIBLE][brightness])
                            }

                            //right side
                            if ((cell + 1) % mapWidth != 0) {

                                //picks the darkest fog between current tile, right, and below-right(if right is a wall).
                                if (wall(cell + 1)) {

                                    //if below-right is also a wall, then we should be dark no matter what.
                                    if (wall(cell + mapWidth + 1)) {
                                        fillRight(fog, j, i, FOG_COLORS[INVISIBLE][brightness])
                                    } else {
                                        fillRight(fog, j, i, FOG_COLORS[Math.max(getCellFog(cell), Math.max(getCellFog(cell + mapWidth + 1), getCellFog(cell + 1)))][brightness])
                                    }

                                } else {
                                    fillRight(fog, j, i, FOG_COLORS[Math.max(getCellFog(cell), getCellFog(cell + 1))][brightness])
                                }

                            } else {
                                fillRight(fog, j, i, FOG_COLORS[INVISIBLE][brightness])
                            }

                            //camera-facing wall tiles
                            //darkest between themselves and the tile below them
                        } else {
                            fillCell(fog, j, i, FOG_COLORS[Math.max(getCellFog(cell), getCellFog(cell + mapWidth))][brightness])
                        }

                        //other tiles, just their direct value
                    } else {
                        fillCell(fog, j, i, FOG_COLORS[getCellFog(cell)][brightness])
                    }

                    cell++
                }
            }

        }

        if (updating!!.size == 1 && !fullUpdate) {
            fog.update(updating!![0].top * PIX_PER_TILE, updating!![0].bottom * PIX_PER_TILE)
        } else {
            fog.update()
        }

    }

    private fun wall(cell: Int): Boolean {
        return DungeonTileSheet.wallStitcheable(Dungeon.level!!.map!![cell])
    }

    private fun getCellFog(cell: Int): Int {

        return if (visible!![cell]) {
            VISIBLE
        } else if (visited!![cell]) {
            VISITED
        } else if (mapped!![cell]) {
            MAPPED
        } else {
            INVISIBLE
        }
    }

    private fun fillLeft(fog: BufferTexture, x: Int, y: Int, color: Int) {
        for (i in 0 until PIX_PER_TILE) {
            fog.pixels.position((y * PIX_PER_TILE + i) * width2 + x * PIX_PER_TILE)
            for (j in 0 until PIX_PER_TILE / 2) {
                fog.pixels.put(color)
            }
        }
    }

    private fun fillRight(fog: BufferTexture, x: Int, y: Int, color: Int) {
        for (i in 0 until PIX_PER_TILE) {
            fog.pixels.position((y * PIX_PER_TILE + i) * width2 + x * PIX_PER_TILE + PIX_PER_TILE / 2)
            for (j in PIX_PER_TILE / 2 until PIX_PER_TILE) {
                fog.pixels.put(color)
            }
        }
    }

    private fun fillCell(fog: BufferTexture, x: Int, y: Int, color: Int) {
        for (i in 0 until PIX_PER_TILE) {
            fog.pixels.position((y * PIX_PER_TILE + i) * width2 + x * PIX_PER_TILE)
            for (j in 0 until PIX_PER_TILE) {
                fog.pixels.put(color)
            }
        }
    }

    override fun script(): NoosaScript {
        return NoosaScriptNoLighting.get()
    }

    override fun draw() {

        if (!toUpdate!!.isEmpty()) {
            updateTexture(Dungeon.level!!.heroFOV, Dungeon.level!!.visited, Dungeon.level!!.mapped)
        }

        super.draw()
    }

    override fun destroy() {
        super.destroy()
        if (texture != null) {
            TextureCache.remove(FogOfWar::class.java)
        }
    }

    companion object {

        //first index is visibility type, second is brightness level
        private val FOG_COLORS = arrayOf(intArrayOf(
                //visible
                0x55000000, 0x00000000, //-2 and -1 brightness
                0x00000000, //0 brightness
                0x00000000, 0x00000000 //1 and 2 brightness
        ), intArrayOf(
                //visited
                -0x23000000, -0x45000000, -0x67000000, 0x77000000, 0x55000000), intArrayOf(
                //mapped
                -0x22ddeef8, -0x44bbddef, -0x6699cce7, 0x77884411, 0x55AA552A), intArrayOf(
                //invisible
                -0x1000000, -0x1000000, -0x1000000, -0x1000000, -0x1000000))

        private val VISIBLE = 0
        private val VISITED = 1
        private val MAPPED = 2
        private val INVISIBLE = 3

        //should be divisible by 2
        private val PIX_PER_TILE = 2
    }
}
