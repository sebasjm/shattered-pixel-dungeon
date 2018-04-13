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

package com.watabou.noosa.tweeners

import com.watabou.noosa.Game
import com.watabou.noosa.Gizmo

abstract class Tweener(var target: Gizmo, var interval: Float) : Gizmo() {
    var elapsed: Float = 0.toFloat()

    var listener: Listener? = null

    init {

        elapsed = 0f
    }

    override fun update() {
        if (elapsed < 0) {
            onComplete()
            kill()
            return
        }
        elapsed += Game.elapsed
        if (elapsed >= interval) {
            updateValues(1f)
            onComplete()
            kill()
        } else {
            updateValues(elapsed / interval)
        }
    }

    fun stop(complete: Boolean) {
        elapsed = if (complete) interval else -1
    }

    protected open fun onComplete() {
        if (listener != null) {
            listener!!.onComplete(this)
        }
    }

    protected abstract fun updateValues(progress: Float)

    interface Listener {
        fun onComplete(tweener: Tweener)
    }
}
