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

package com.shatteredpixel.shatteredpixeldungeon.effects

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText
import com.watabou.utils.SparseArray

import java.util.ArrayList

class FloatingText : RenderedText() {

    private var timeLeft: Float = 0.toFloat()

    private var key = -1

    private var cameraZoom = -1f

    init {
        speed.y = -DISTANCE / LIFESPAN
    }

    override fun update() {
        super.update()

        if (timeLeft > 0) {
            timeLeft -= Game.elapsed
            if (timeLeft <= 0) {
                kill()
            } else {
                val p = timeLeft / LIFESPAN
                alpha(if (p > 0.5f) 1f else p * 2)
            }
        }
    }

    override fun kill() {
        if (key != -1) {
            synchronized(stacks) {
                stacks.get(key).remove(this)
            }
            key = -1
        }
        super.kill()
    }

    override fun destroy() {
        kill()
        super.destroy()
    }

    fun reset(x: Float, y: Float, text: String, color: Int) {

        revive()

        if (cameraZoom != Camera.main!!.zoom) {
            cameraZoom = Camera.main!!.zoom
            PixelScene.chooseFont(9f, cameraZoom)
            size(9 * cameraZoom.toInt())
            scale.set(1 / cameraZoom)
        }

        text(text)
        hardlight(color)

        this.x = PixelScene.align(Camera.main!!, x - width() / 2)
        this.y = PixelScene.align(Camera.main!!, y - height())

        timeLeft = LIFESPAN
    }

    companion object {

        private val LIFESPAN = 1f
        private val DISTANCE = DungeonTilemap.SIZE.toFloat()

        private val stacks = SparseArray<ArrayList<FloatingText>>()

        /* STATIC METHODS */

        fun show(x: Float, y: Float, text: String, color: Int) {
            val txt = GameScene.status()
            txt?.reset(x, y, text, color)
        }

        fun show(x: Float, y: Float, key: Int, text: String, color: Int) {
            val txt = GameScene.status()
            if (txt != null) {
                txt.reset(x, y, text, color)
                push(txt, key)
            }
        }

        private fun push(txt: FloatingText, key: Int) {

            synchronized(stacks) {
                txt.key = key

                var stack: ArrayList<FloatingText>? = stacks.get(key)
                if (stack == null) {
                    stack = ArrayList()
                    stacks.put(key, stack)
                }

                if (stack.size > 0) {
                    var below = txt
                    var aboveIndex = stack.size - 1
                    while (aboveIndex >= 0) {
                        val above = stack[aboveIndex]
                        if (above.y + above.height() > below.y) {
                            above.y = below.y - above.height()

                            below = above
                            aboveIndex--
                        } else {
                            break
                        }
                    }
                }

                stack.add(txt)
            }
        }
    }
}
