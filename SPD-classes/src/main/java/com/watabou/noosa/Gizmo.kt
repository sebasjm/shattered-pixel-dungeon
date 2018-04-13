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

open class Gizmo {

    var exists: Boolean = false
    var alive: Boolean = false
    var active: Boolean = false
    var visible: Boolean = false

    var parent: Group? = null

    var camera: Camera? = null

    open val isVisible: Boolean
        get() = if (parent == null) {
            visible
        } else {
            visible && parent!!.isVisible
        }

    val isActive: Boolean
        get() = if (parent == null) {
            active
        } else {
            active && parent!!.isActive
        }

    init {
        exists = true
        alive = true
        active = true
        visible = true
    }

    open fun destroy() {
        parent = null
    }

    open fun update() {}

    open fun draw() {}

    open fun kill() {
        alive = false
        exists = false
    }

    // Not exactly opposite to "kill" method
    open fun revive() {
        alive = true
        exists = true
    }

    open fun camera(): Camera? {
        return if (camera != null) {
            this.camera
        } else if (parent != null) {
            this.camera = this.parent!!.camera()
            this.camera
        } else {
            null
        }
    }

    fun killAndErase() {
        kill()
        if (parent != null) {
            parent!!.erase(this)
        }
    }

    fun remove() {
        if (parent != null) {
            parent!!.remove(this)
        }
    }
}
