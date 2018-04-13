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

package com.shatteredpixel.shatteredpixeldungeon.scenes

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.TouchArea
import com.watabou.utils.GameMath
import com.watabou.utils.PointF

class CellSelector(map: DungeonTilemap) : TouchArea(map) {

    var listener: Listener? = null

    var enabled: Boolean = false

    private val dragThreshold: Float

    private var pinching = false
    private var another: Touch? = null
    private var startZoom: Float = 0.toFloat()
    private var startSpan: Float = 0.toFloat()

    private var dragging = false
    private val lastPos = PointF()

    init {
        camera = map.camera()

        dragThreshold = (PixelScene.defaultZoom * DungeonTilemap.SIZE / 2).toFloat()
    }

    override fun onClick(touch: Touch) {
        if (dragging) {

            dragging = false

        } else {

            val p = Camera.main.screenToCamera(touch.current.x.toInt(), touch.current.y.toInt())
            for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
                if (mob.sprite != null && mob.sprite!!.overlapsPoint(p.x, p.y)) {
                    select(mob.pos)
                    return
                }
            }

            for (heap in Dungeon.level!!.heaps.values()) {
                if (heap.sprite != null && heap.sprite!!.overlapsPoint(p.x, p.y)) {
                    select(heap.pos)
                    return
                }
            }

            select((target as DungeonTilemap).screenToTile(
                    touch.current.x.toInt(),
                    touch.current.y.toInt(),
                    true))
        }
    }

    private fun zoom(value: Float): Float {
        var value = value

        value = GameMath.gate(PixelScene.minZoom, value, PixelScene.maxZoom)
        SPDSettings.zoom((value - PixelScene.defaultZoom).toInt())
        camera!!.zoom(value)

        //Resets character sprite positions with the new camera zoom
        //This is important as characters are centered on a 16x16 tile, but may have any sprite size
        //This can lead to none-whole coordinate, which need to be aligned with the zoom
        for (c in Actor.chars()) {
            if (c.sprite != null && !c.sprite!!.isMoving) {
                c.sprite!!.point(c.sprite!!.worldToCamera(c.pos))
            }
        }

        return value
    }

    fun select(cell: Int) {
        if (enabled && listener != null && cell != -1) {

            listener!!.onSelect(cell)
            GameScene.ready()

        } else {

            GameScene.cancel()

        }
    }

    override fun onTouchDown(t: Touch) {

        if (t !== touch && another == null) {

            if (!touch!!.down) {
                touch = t
                onTouchDown(t)
                return
            }

            pinching = true

            another = t
            startSpan = PointF.distance(touch!!.current, another!!.current)
            startZoom = camera!!.zoom

            dragging = false
        } else if (t !== touch) {
            reset()
        }
    }

    override fun onTouchUp(t: Touch) {
        if (pinching && (t === touch || t === another)) {

            pinching = false

            zoom(Math.round(camera!!.zoom).toFloat())

            dragging = true
            if (t === touch) {
                touch = another
            }
            another = null
            lastPos.set(touch!!.current)
        }
    }

    override fun onDrag(t: Touch) {

        camera!!.target = null

        if (pinching) {

            val curSpan = PointF.distance(touch!!.current, another!!.current)
            val zoom = startZoom * curSpan / startSpan
            camera!!.zoom(GameMath.gate(
                    PixelScene.minZoom,
                    zoom - zoom % 0.1f,
                    PixelScene.maxZoom))

        } else {

            if (!dragging && PointF.distance(t.current, t.start) > dragThreshold) {

                dragging = true
                lastPos.set(t.current)

            } else if (dragging) {
                camera!!.scroll.offset(PointF.diff(lastPos, t.current).invScale(camera!!.zoom))
                lastPos.set(t.current)
            }
        }

    }

    fun cancel() {

        if (listener != null) {
            listener!!.onSelect(null)
        }

        GameScene.ready()
    }

    override fun reset() {
        super.reset()
        another = null
        if (pinching) {
            pinching = false

            zoom(Math.round(camera!!.zoom).toFloat())
        }
    }

    fun enable(value: Boolean) {
        if (enabled != value) {
            enabled = value
        }
    }

    interface Listener {
        fun onSelect(cell: Int?)
        fun prompt(): String
    }
}
