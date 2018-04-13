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

import com.watabou.glwrap.Matrix
import com.watabou.utils.Point
import com.watabou.utils.PointF

open class Visual(var x: Float, var y: Float, var width: Float, var height: Float) : Gizmo() {

    var scale: PointF
    var origin: PointF

    protected var matrix: FloatArray

    var rm: Float = 0.toFloat()
    var gm: Float = 0.toFloat()
    var bm: Float = 0.toFloat()
    var am: Float = 0.toFloat()
    var ra: Float = 0.toFloat()
    var ga: Float = 0.toFloat()
    var ba: Float = 0.toFloat()
    var aa: Float = 0.toFloat()

    var speed: PointF
    var acc: PointF

    var angle: Float = 0.toFloat()
    var angularSpeed: Float = 0.toFloat()

    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    private var lastW: Float = 0.toFloat()
    private var lastH: Float = 0.toFloat()
    private var lastA: Float = 0.toFloat()
    private val lastScale = PointF()
    private val lastOrigin = PointF()

    // true if its bounding box intersects its camera's bounds
    override//x coord
    //y coord
    val isVisible: Boolean
        get() {
            val c = camera()

            if (c == null || !visible) return false
            if (x > c.scroll.x + c.width)
                return false
            else if (!(x >= c.scroll.x || x + width() >= c.scroll.x))
                return false
            if (y > c.scroll.y + c.height)
                return false
            else if (!(y >= c.scroll.y || y + height() >= c.scroll.y))
                return false

            return true
        }

    init {

        scale = PointF(1f, 1f)
        origin = PointF()

        matrix = FloatArray(16)

        resetColor()

        speed = PointF()
        acc = PointF()
    }

    override fun update() {
        updateMotion()
    }

    override//TODO caching the last value of all these variables does improve performance a bunch
    // by letting us skip many calls to updateMatrix, but it is quite messy. It would be better to
    // control their editing and have a single boolean to tell if the matrix needs updating.
    fun draw() {
        if (lastX != x ||
                lastY != y ||
                lastW != width ||
                lastH != height ||
                lastA != angle ||
                lastScale.x != scale.x ||
                lastScale.y != scale.y ||
                lastOrigin.x != origin.x ||
                lastOrigin.y != origin.y) {

            lastX = x
            lastY = y
            lastW = width
            lastH = height
            lastA = angle
            lastScale.x = scale.x
            lastScale.y = scale.y
            lastOrigin.x = origin.x
            lastOrigin.y = origin.y

            updateMatrix()
        }
    }

    protected open fun updateMatrix() {
        Matrix.setIdentity(matrix)
        Matrix.translate(matrix, x, y)
        if (origin.x != 0f || origin.y != 0f)
            Matrix.translate(matrix, origin.x, origin.y)
        if (angle != 0f) {
            Matrix.rotate(matrix, angle)
        }
        if (scale.x != 1f || scale.y != 1f) {
            Matrix.scale(matrix, scale.x, scale.y)
        }
        if (origin.x != 0f || origin.y != 0f)
            Matrix.translate(matrix, -origin.x, -origin.y)
    }

    fun point(): PointF {
        return PointF(x, y)
    }

    fun point(p: PointF): PointF {
        x = p.x
        y = p.y
        return p
    }

    fun point(p: Point): Point {
        x = p.x.toFloat()
        y = p.y.toFloat()
        return p
    }

    fun center(): PointF {
        return PointF(x + width / 2, y + height / 2)
    }

    fun center(p: PointF): PointF {
        x = p.x - width / 2
        y = p.y - height / 2
        return p
    }

    //returns the point needed to center the argument visual on this visual
    fun center(v: Visual): PointF {
        return PointF(
                x + (width() - v.width()) / 2f,
                y + (height() - v.height()) / 2f
        )
    }

    open fun width(): Float {
        return width * scale.x
    }

    open fun height(): Float {
        return height * scale.y
    }

    protected fun updateMotion() {

        if (acc.x != 0f)
            speed.x += acc.x * Game.elapsed
        if (speed.x != 0f)
            x += speed.x * Game.elapsed

        if (acc.y != 0f)
            speed.y += acc.y * Game.elapsed
        if (speed.y != 0f)
            y += speed.y * Game.elapsed

        if (angularSpeed != 0f)
            angle += angularSpeed * Game.elapsed
    }

    fun alpha(value: Float) {
        am = value
        aa = 0f
    }

    fun alpha(): Float {
        return am + aa
    }

    fun invert() {
        bm = -1f
        gm = bm
        rm = gm
        ba = +1f
        ga = ba
        ra = ga
    }

    fun lightness(value: Float) {
        if (value < 0.5f) {
            bm = value * 2f
            gm = bm
            rm = gm
            ba = 0f
            ga = ba
            ra = ga
        } else {
            bm = 2f - value * 2f
            gm = bm
            rm = gm
            ba = value * 2f - 1f
            ga = ba
            ra = ga
        }
    }

    fun brightness(value: Float) {
        bm = value
        gm = bm
        rm = gm
    }

    fun tint(r: Float, g: Float, b: Float, strength: Float) {
        bm = 1f - strength
        gm = bm
        rm = gm
        ra = r * strength
        ga = g * strength
        ba = b * strength
    }

    fun tint(color: Int, strength: Float) {
        bm = 1f - strength
        gm = bm
        rm = gm
        ra = (color shr 16 and 0xFF) / 255f * strength
        ga = (color shr 8 and 0xFF) / 255f * strength
        ba = (color and 0xFF) / 255f * strength
    }

    //color must include an alpha component (e.g. 0x80FF0000 for red at 0.5 strength)
    fun tint(color: Int) {
        tint(color and 0xFFFFFF, (color shr 24 and 0xFF) / 0xFF.toFloat())
    }

    fun color(r: Float, g: Float, b: Float) {
        bm = 0f
        gm = bm
        rm = gm
        ra = r
        ga = g
        ba = b
    }

    fun color(color: Int) {
        color((color shr 16 and 0xFF) / 255f, (color shr 8 and 0xFF) / 255f, (color and 0xFF) / 255f)
    }

    fun hardlight(r: Float, g: Float, b: Float) {
        ba = 0f
        ga = ba
        ra = ga
        rm = r
        gm = g
        bm = b
    }

    fun hardlight(color: Int) {
        hardlight((color shr 16) / 255f, (color shr 8 and 0xFF) / 255f, (color and 0xFF) / 255f)
    }

    open fun resetColor() {
        am = 1f
        bm = am
        gm = bm
        rm = gm
        aa = 0f
        ba = aa
        ga = ba
        ra = ga
    }

    open fun overlapsPoint(x: Float, y: Float): Boolean {
        return x >= this.x && x < this.x + width * scale.x && y >= this.y && y < this.y + height * scale.y
    }

    open fun overlapsScreenPoint(x: Int, y: Int): Boolean {
        val c = camera() ?: return false

        val p = c.screenToCamera(x, y)
        return overlapsPoint(p.x, p.y)
    }
}
