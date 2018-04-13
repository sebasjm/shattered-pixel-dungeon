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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.TouchArea
import com.watabou.noosa.ui.Component
import com.watabou.utils.Point
import com.watabou.utils.PointF

open class ScrollPane(protected var content: Component) : Component() {

    protected var controller: TouchController
    protected var thumb: ColorBlock

    protected var minX: Float = 0.toFloat()
    protected var minY: Float = 0.toFloat()
    protected var maxX: Float = 0.toFloat()
    protected var maxY: Float = 0.toFloat()

    init {
        addToBack(content)

        width = content.width()
        height = content.height()

        content.camera = Camera(0, 0, 1, 1, PixelScene.defaultZoom.toFloat())
        Camera.add(content.camera)
    }

    override fun destroy() {
        super.destroy()
        Camera.remove(content.camera)
    }

    fun scrollTo(x: Float, y: Float) {
        content.camera!!.scroll.set(x, y)
    }

    override fun createChildren() {
        controller = TouchController()
        add(controller)

        thumb = ColorBlock(1f, 1f, THUMB_COLOR)
        thumb.am = THUMB_ALPHA
        add(thumb)
    }

    override fun layout() {

        content.setPos(0f, 0f)
        controller.x = x
        controller.y = y
        controller.width = width
        controller.height = height

        val p = camera()!!.cameraToScreen(x, y)
        val cs = content.camera
        cs!!.x = p.x
        cs.y = p.y
        cs.resize(width.toInt(), height.toInt())

        thumb.visible = height < content.height()
        if (thumb.visible) {
            thumb.scale.set(2f, height * height / content.height())
            thumb.x = right() - thumb.width()
            thumb.y = y
        }
    }

    fun content(): Component {
        return content
    }

    open fun onClick(x: Float, y: Float) {}

    inner class TouchController : TouchArea(0, 0, 0, 0) {

        private val dragThreshold: Float

        private var dragging = false
        private val lastPos = PointF()

        init {
            dragThreshold = (PixelScene.defaultZoom * 8).toFloat()
        }

        override fun onTouchUp(touch: Touch) {
            if (dragging) {

                dragging = false
                thumb.am = THUMB_ALPHA

            } else {

                val p = content.camera!!.screenToCamera(touch.current.x.toInt(), touch.current.y.toInt())
                this@ScrollPane.onClick(p.x, p.y)

            }
        }

        override fun onDrag(t: Touch) {
            if (dragging) {

                val c = content.camera

                c!!.scroll.offset(PointF.diff(lastPos, t.current).invScale(c.zoom))
                if (c.scroll.x + width > content.width()) {
                    c.scroll.x = content.width() - width
                }
                if (c.scroll.x < 0) {
                    c.scroll.x = 0f
                }
                if (c.scroll.y + height > content.height()) {
                    c.scroll.y = content.height() - height
                }
                if (c.scroll.y < 0) {
                    c.scroll.y = 0f
                }

                thumb.y = y + height * c.scroll.y / content.height()

                lastPos.set(t.current)

            } else if (PointF.distance(t.current, t.start) > dragThreshold) {

                dragging = true
                lastPos.set(t.current)
                thumb.am = 1f

            }
        }
    }

    companion object {

        protected val THUMB_COLOR = -0x847f8d
        protected val THUMB_ALPHA = 0.5f
    }
}
