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

package com.shatteredpixel.shatteredpixeldungeon.sprites

import android.graphics.Bitmap

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Matrix
import com.watabou.glwrap.Vertexbuffer
import com.watabou.noosa.Game
import com.watabou.noosa.MovieClip
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.PointF
import com.watabou.utils.Random

open class ItemSprite : MovieClip {

    var heap: Heap? = null

    private var glowing: Glowing? = null
    //FIXME: a lot of this emitter functionality isn't very well implemented.
    //right now I want to ship 0.3.0, but should refactor in the future.
    protected var emitter: Emitter? = null
    private var phase: Float = 0.toFloat()
    private var glowUp: Boolean = false

    private var dropInterval: Float = 0.toFloat()

    //the amount the sprite is raised from flat when viewed in a raised perspective
    protected var perspectiveRaise = 5 / 16f //5 pixels

    //the width and height of the shadow are a percentage of sprite size
    //offset is the number of pixels the shadow is moved down or up (handy for some animations)
    protected var renderShadow = false
    protected var shadowWidth = 1f
    protected var shadowHeight = 0.25f
    protected var shadowOffset = 0.5f

    private val shadowMatrix = FloatArray(16)

    constructor(item: Item) : super(Assets.ITEMS) {

        view(item)
    }

    @JvmOverloads constructor(image: Int = ItemSpriteSheet.SOMETHING, glowing: Glowing? = null) : super(Assets.ITEMS) {

        view(image, glowing)
    }

    fun originToCenter() {
        origin.set(width / 2, height / 2)
    }

    @JvmOverloads
    fun link(heap: Heap = heap) {
        this.heap = heap
        view(heap.image(), heap.glowing())
        renderShadow = true
        place(heap.pos)
    }

    override fun revive() {
        super.revive()

        speed.set(0f)
        acc.set(0f)
        dropInterval = 0f

        heap = null
        if (emitter != null) {
            emitter!!.killAndErase()
            emitter = null
        }
    }

    fun visible(value: Boolean) {
        this.visible = value
        if (emitter != null && !visible) {
            emitter!!.killAndErase()
            emitter = null
        }
    }

    fun worldToCamera(cell: Int): PointF {
        val csize = DungeonTilemap.SIZE

        return PointF(
                cell % Dungeon.level!!.width() * csize + (csize - width()) * 0.5f,
                cell / Dungeon.level!!.width() * csize + (csize - height()) - csize * perspectiveRaise
        )
    }

    fun place(p: Int) {
        if (Dungeon.level != null) {
            point(worldToCamera(p))
            shadowOffset = 0.5f
        }
    }

    open fun drop() {

        if (heap!!.isEmpty) {
            return
        } else if (heap!!.size() == 1) {
            // normally this would happen for any heap, however this is not applied to heaps greater than 1 in size
            // in order to preserve an amusing visual bug/feature that used to trigger for heaps with size > 1
            // where as long as the player continually taps, the heap sails up into the air.
            place(heap!!.pos)
        }

        dropInterval = DROP_INTERVAL

        speed.set(0f, -100f)
        acc.set(0f, -speed.y / DROP_INTERVAL * 2)

        if (heap != null && heap!!.seen && heap!!.peek() is Gold) {
            CellEmitter.center(heap!!.pos).burst(Speck.factory(Speck.COIN), 5)
            Sample.INSTANCE.play(Assets.SND_GOLD, 1f, 1f, Random.Float(0.9f, 1.1f))
        }
    }

    fun drop(from: Int) {

        if (heap!!.pos == from) {
            drop()
        } else {

            val px = x
            val py = y
            drop()

            place(from)

            speed.offset((px - x) / DROP_INTERVAL, (py - y) / DROP_INTERVAL)
        }
    }

    fun view(item: Item): ItemSprite {
        view(item.image(), item.glowing())
        val emitter = item.emitter()
        if (emitter != null && parent != null) {
            emitter.pos(this)
            parent!!.add(emitter)
            this.emitter = emitter
        }
        return this
    }

    fun view(image: Int, glowing: Glowing?): ItemSprite {
        if (this.emitter != null) this.emitter!!.killAndErase()
        emitter = null
        frame(image)
        glow(glowing)
        return this
    }

    fun frame(image: Int) {
        frame(ItemSpriteSheet.film.get(image))

        val height = ItemSpriteSheet.film.height(image)
        //adds extra raise to very short items, so they are visible
        if (height < 8f) {
            perspectiveRaise = (5 + 8 - height) / 16f
        }
    }

    @Synchronized
    fun glow(glowing: Glowing?) {
        this.glowing = glowing
        if (glowing == null) resetColor()
    }

    override fun kill() {
        super.kill()
        if (emitter != null) emitter!!.killAndErase()
        emitter = null
    }

    override fun updateMatrix() {
        super.updateMatrix()
        Matrix.copy(matrix, shadowMatrix)
        Matrix.translate(shadowMatrix,
                width() * (1f - shadowWidth) / 2f,
                height() * (1f - shadowHeight) + shadowOffset)
        Matrix.scale(shadowMatrix, shadowWidth, shadowHeight)
    }

    override fun draw() {
        if (texture == null || !dirty && buffer == null)
            return

        if (renderShadow) {
            if (dirty) {
                verticesBuffer.position(0)
                verticesBuffer.put(vertices)
                if (buffer == null)
                    buffer = Vertexbuffer(verticesBuffer)
                else
                    buffer!!.updateVertices(verticesBuffer)
                dirty = false
            }

            val script = script()

            texture!!.bind()

            script.camera(camera())

            updateMatrix()

            script.uModel.valueM4(shadowMatrix)
            script.lighting(
                    0f, 0f, 0f, am * .6f,
                    0f, 0f, 0f, aa * .6f)

            script.drawQuad(buffer)
        }

        super.draw()

    }

    @Synchronized
    override fun update() {
        super.update()

        visible = heap == null || heap!!.seen

        if (dropInterval > 0) {
            shadowOffset -= speed.y * Game.elapsed * 0.8f

            if ((dropInterval -= Game.elapsed) <= 0) {

                speed.set(0f)
                acc.set(0f)
                shadowOffset = 0.25f
                place(heap!!.pos)

                if (visible) {
                    var water = Dungeon.level!!.water[heap!!.pos]

                    if (water) {
                        GameScene.ripple(heap!!.pos)
                    } else {
                        val cell = Dungeon.level!!.map!![heap!!.pos]
                        water = cell == Terrain.WELL || cell == Terrain.ALCHEMY
                    }

                    if (heap!!.peek() !is Gold) {
                        Sample.INSTANCE.play(if (water) Assets.SND_WATER else Assets.SND_STEP, 0.8f, 0.8f, 1.2f)
                    }
                }
            }
        }

        if (visible && glowing != null) {
            if (glowUp && (phase += Game.elapsed) > glowing!!.period) {

                glowUp = false
                phase = glowing!!.period

            } else if (!glowUp && (phase -= Game.elapsed) < 0) {

                glowUp = true
                phase = 0f

            }

            val value = phase / glowing!!.period * 0.6f

            bm = 1 - value
            gm = bm
            rm = gm
            ra = glowing!!.red * value
            ga = glowing!!.green * value
            ba = glowing!!.blue * value
        }
    }

    class Glowing @JvmOverloads constructor(var color: Int, var period: Float = 1f) {
        var red: Float = 0.toFloat()
        var green: Float = 0.toFloat()
        var blue: Float = 0.toFloat()

        init {

            red = (color shr 16) / 255f
            green = (color shr 8 and 0xFF) / 255f
            blue = (color and 0xFF) / 255f
        }
    }

    companion object {

        val SIZE = 16

        private val DROP_INTERVAL = 0.4f

        fun pick(index: Int, x: Int, y: Int): Int {
            val bmp = TextureCache.get(Assets.ITEMS).bitmap
            val rows = bmp!!.width / SIZE
            val row = index / rows
            val col = index % rows
            return bmp.getPixel(col * SIZE + x, row * SIZE + y)
        }
    }
}
