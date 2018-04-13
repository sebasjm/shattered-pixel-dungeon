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

package com.watabou.noosa.particles

import com.watabou.glwrap.Blending
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Visual
import com.watabou.utils.PointF
import com.watabou.utils.Random

open class Emitter : Group() {

    protected var lightMode = false

    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var width: Float = 0.toFloat()
    var height: Float = 0.toFloat()

    protected var target: Visual? = null
    var fillTarget = true

    protected var interval: Float = 0.toFloat()
    protected var quantity: Int = 0

    var on = false

    var autoKill = true

    protected var count: Int = 0
    protected var time: Float = 0.toFloat()

    protected var factory: Factory? = null

    fun pos(p: PointF) {
        pos(p.x, p.y, 0f, 0f)
    }

    @JvmOverloads
    fun pos(x: Float, y: Float, width: Float = 0f, height: Float = 0f) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height

        target = null
    }

    fun pos(target: Visual) {
        this.target = target
    }

    fun pos(target: Visual, x: Float, y: Float, width: Float, height: Float) {
        pos(x, y, width, height)
        pos(target)
    }

    fun burst(factory: Factory, quantity: Int) {
        start(factory, 0f, quantity)
    }

    fun pour(factory: Factory, interval: Float) {
        start(factory, interval, 0)
    }

    fun start(factory: Factory, interval: Float, quantity: Int) {

        this.factory = factory
        this.lightMode = factory.lightMode()

        this.interval = interval
        this.quantity = quantity

        count = 0
        time = Random.Float(interval)

        on = true
    }

    override fun update() {

        if (on) {
            time += Game.elapsed
            while (time > interval) {
                time -= interval
                emit(count++)
                if (quantity > 0 && count >= quantity) {
                    on = false
                    break
                }
            }
        } else if (autoKill && countLiving() == 0) {
            kill()
        }

        super.update()
    }

    protected open fun emit(index: Int) {
        if (target == null) {
            factory!!.emit(
                    this,
                    index,
                    x + Random.Float(width),
                    y + Random.Float(height))
        } else {
            if (fillTarget) {
                factory!!.emit(
                        this,
                        index,
                        target!!.x + Random.Float(target!!.width),
                        target!!.y + Random.Float(target!!.height))
            } else {
                factory!!.emit(
                        this,
                        index,
                        target!!.x + x + Random.Float(width),
                        target!!.y + y + Random.Float(height))
            }
        }
    }

    override fun draw() {
        if (lightMode) {
            Blending.setLightMode()
            super.draw()
            Blending.setNormalMode()
        } else {
            super.draw()
        }
    }

    abstract class Factory {

        abstract fun emit(emitter: Emitter, index: Int, x: Float, y: Float)

        open fun lightMode(): Boolean {
            return false
        }
    }
}
