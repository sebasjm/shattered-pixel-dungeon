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

package com.watabou.noosa

import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Quad
import com.watabou.glwrap.Vertexbuffer
import com.watabou.utils.Rect
import com.watabou.utils.RectF

import java.nio.FloatBuffer
import java.util.Arrays

open class Tilemap(tx: Any, protected var tileset: TextureFilm) : Visual(0f, 0f, 0f, 0f) {

    protected var texture: SmartTexture

    protected var data: IntArray = IntArray(0)
    protected var mapWidth: Int = 0
    protected var mapHeight: Int = 0
    protected var size: Int = 0

    private val cellW: Float
    private val cellH: Float

    protected var vertices: FloatArray
    protected var quads: FloatBuffer? = null
    protected var buffer: Vertexbuffer? = null

    //@Volatile
    private val updated: Rect
    private var fullUpdate: Boolean = false
    private var updating: Rect? = null
    private var topLeftUpdating: Int = 0
    private var bottomRightUpdating: Int = 0

    private var camX: Int = 0
    private var camY: Int = 0
    private var camW: Int = 0
    private var camH: Int = 0
    private var topLeft: Int = 0
    private var bottomRight: Int = 0
    private var length: Int = 0

    init {

        this.texture = TextureCache.get(tx)

        val r = tileset.get(0)
        cellW = tileset.width(r)
        cellH = tileset.height(r)

        vertices = FloatArray(16)

        updated = Rect()
    }

    open fun map(data: IntArray, cols: Int) {

        this.data = data

        mapWidth = cols
        mapHeight = data.size / cols
        size = mapWidth * mapHeight

        width = cellW * mapWidth
        height = cellH * mapHeight

        quads = Quad.createSet(size)

        updateMap()
    }

    //forces a full update, including new buffer
    @Synchronized
    open fun updateMap() {
        updated.set(0, 0, mapWidth, mapHeight)
        fullUpdate = true
    }

    @Synchronized
    open fun updateMapCell(cell: Int) {
        updated.union(cell % mapWidth, cell / mapWidth)
    }

    @Synchronized
    private fun moveToUpdating() {
        updating = Rect(updated)
        updated.setEmpty()
    }

    protected fun updateVertices() {

        moveToUpdating()

        var x1: Float
        var y1: Float
        var x2: Float
        var y2: Float
        var pos: Int
        var uv: RectF?

        y1 = cellH * updating!!.top
        y2 = y1 + cellH

        for (i in updating!!.top until updating!!.bottom) {

            x1 = cellW * updating!!.left
            x2 = x1 + cellW

            pos = i * mapWidth + updating!!.left

            for (j in updating!!.left until updating!!.right) {

                if (topLeftUpdating == -1)
                    topLeftUpdating = pos

                bottomRightUpdating = pos + 1

                quads!!.position(pos * 16)

                uv = tileset.get(data[pos])

                if (needsRender(pos) && uv != null) {

                    vertices[0] = x1
                    vertices[1] = y1

                    vertices[2] = uv.left
                    vertices[3] = uv.top

                    vertices[4] = x2
                    vertices[5] = y1

                    vertices[6] = uv.right
                    vertices[7] = uv.top

                    vertices[8] = x2
                    vertices[9] = y2

                    vertices[10] = uv.right
                    vertices[11] = uv.bottom

                    vertices[12] = x1
                    vertices[13] = y2

                    vertices[14] = uv.left
                    vertices[15] = uv.bottom

                } else {

                    //If we don't need to draw this tile simply set the quad to size 0 at 0, 0.
                    // This does result in the quad being drawn, but we are skipping all
                    // pixel-filling. This is better than fully skipping rendering as we
                    // don't need to manage a buffer of drawable tiles with insertions/deletions.
                    Arrays.fill(vertices, 0f)
                }

                quads!!.put(vertices)

                pos++
                x1 = x2
                x2 += cellW

            }

            y1 = y2
            y2 += cellH
        }

    }

    override fun draw() {

        super.draw()

        if (!updated.isEmpty) {
            updateVertices()
            if (buffer == null)
                buffer = Vertexbuffer(quads!!)
            else {
                if (fullUpdate) {
                    buffer!!.updateVertices(quads)
                    fullUpdate = false
                } else {
                    buffer!!.updateVertices(quads,
                            topLeftUpdating * 16,
                            bottomRightUpdating * 16)
                }
            }
            topLeftUpdating = -1
            updating!!.setEmpty()
        }

        val c = Camera.main!!
        //we treat the position of the tilemap as (0,0) here
        camX = (c.scroll.x / cellW - x / cellW).toInt()
        camY = (c.scroll.y / cellH - y / cellH).toInt()
        camW = Math.ceil((c.width / cellW).toDouble()).toInt()
        camH = Math.ceil((c.height / cellH).toDouble()).toInt()

        if (camX >= mapWidth
                || camY >= mapHeight
                || camW + camW <= 0
                || camH + camH <= 0)
            return

        //determines the top-left visible tile, the bottom-right one, and the buffer length
        //between them, this culls a good number of none-visible tiles while keeping to 1 draw
        topLeft = Math.max(camX, 0) + Math.max(camY * mapWidth, 0)

        bottomRight = Math.min(camX + camW, mapWidth - 1) + Math.min((camY + camH) * mapWidth, (mapHeight - 1) * mapWidth)

        if (topLeft >= size || bottomRight < 0)
            length = 0
        else
            length = bottomRight - topLeft + 1

        if (length <= 0)
            return

        val script = NoosaScriptNoLighting.get()

        texture.bind()

        script.uModel.valueM4(matrix)

        script.camera(camera)

        script.drawQuadSet(buffer!!, length, topLeft)

    }

    override fun destroy() {
        super.destroy()
        if (buffer != null)
            buffer!!.delete()
    }

    protected open fun needsRender(pos: Int): Boolean {
        return true
    }
}
