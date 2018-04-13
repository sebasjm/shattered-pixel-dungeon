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

import com.watabou.input.Touchscreen
import com.watabou.input.Touchscreen.Touch
import com.watabou.utils.Signal

open class TouchArea : Visual, Signal.Listener<Touchscreen.Touch?> {

    // Its target can be toucharea itself
    var target: Visual

    protected var touch: Touchscreen.Touch? = null

    //if true, this TouchArea will always block input, even when it is inactive
    var blockWhenInactive = false

    constructor(target: Visual) : super(0f, 0f, 0f, 0f) {
        this.target = target

        Touchscreen.event.add(this)
    }

    constructor(x: Float, y: Float, width: Float, height: Float) : super(x, y, width, height) {
        this.target = this

        visible = false

        Touchscreen.event.add(this)
    }

    override fun onSignal(touch: Touch?) {

        val hit = touch != null && target.overlapsScreenPoint(touch.current.x.toInt(), touch.current.y.toInt())

        if (!isActive) {
            if (hit && blockWhenInactive) Touchscreen.event.cancel()
            return
        }

        if (hit) {

            if (touch!!.down || touch === this.touch) Touchscreen.event.cancel()

            if (touch.down) {

                if (this.touch == null) {
                    this.touch = touch
                }
                onTouchDown(touch)

            } else {

                onTouchUp(touch)

                if (this.touch === touch) {
                    this.touch = null
                    onClick(touch)
                }

            }

        } else {

            if (touch == null && this.touch != null) {
                onDrag(this.touch!!)
            } else if (this.touch != null && !touch!!.down) {
                onTouchUp(touch)
                this.touch = null
            }

        }
    }

    protected open fun onTouchDown(touch: Touch) {}

    protected open fun onTouchUp(touch: Touch) {}

    protected open fun onClick(touch: Touch) {}

    protected open fun onDrag(touch: Touch) {}

    open fun reset() {
        touch = null
    }

    override fun destroy() {
        Touchscreen.event.remove(this)
        super.destroy()
    }
}
