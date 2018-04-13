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

package com.watabou.input

import android.view.MotionEvent

import com.watabou.noosa.Game
import com.watabou.utils.PointF
import com.watabou.utils.Signal

import java.util.ArrayList
import java.util.HashMap

object Touchscreen {

    var event = Signal<Touch>(true)

    var pointers = HashMap<Int, Touch>()

    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var touched: Boolean = false

    fun processTouchEvents(events: ArrayList<MotionEvent>) {

        val size = events.size
        for (i in 0 until size) {

            val e = events[i]
            val touch: Touch

            when (e.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    touched = true
                    touch = Touch(e, 0)
                    pointers[e.getPointerId(0)] = touch
                    event.dispatch(touch)
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    val index = e.actionIndex
                    touch = Touch(e, index)
                    pointers[e.getPointerId(index)] = touch
                    event.dispatch(touch)
                }

                MotionEvent.ACTION_MOVE -> {
                    val count = e.pointerCount
                    for (j in 0 until count) {
                        if (pointers.containsKey(e.getPointerId(j))) {
                            pointers[e.getPointerId(j)]!!.update(e, j)
                        }
                    }
                    event.dispatch(null)
                }

                MotionEvent.ACTION_POINTER_UP -> event.dispatch(pointers.remove(e.getPointerId(e.actionIndex))!!.up())

                MotionEvent.ACTION_UP -> {
                    touched = false
                    event.dispatch(pointers.remove(e.getPointerId(0))!!.up())
                }
            }

            e.recycle()
        }
    }

    class Touch(e: MotionEvent, index: Int) {

        var start: PointF
        var current: PointF
        var down: Boolean = false

        init {

            var x = e.getX(index)
            var y = e.getY(index)

            x /= Game.dispWidth / Game.width.toFloat()
            y /= Game.dispHeight / Game.height.toFloat()

            start = PointF(x, y)
            current = PointF(x, y)

            down = true
        }

        fun update(e: MotionEvent, index: Int) {
            var x = e.getX(index)
            var y = e.getY(index)

            x /= Game.dispWidth / Game.width.toFloat()
            y /= Game.dispHeight / Game.height.toFloat()

            current.set(x, y)
        }

        fun up(): Touch {
            down = false
            return this
        }
    }

}
