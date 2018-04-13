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

import android.view.KeyEvent

import com.watabou.utils.Signal

import java.util.ArrayList

object Keys {

    val BACK = KeyEvent.KEYCODE_BACK
    val MENU = KeyEvent.KEYCODE_MENU

    var event = Signal<Key>(true)

    fun processTouchEvents(events: ArrayList<KeyEvent>) {

        val size = events.size
        for (i in 0 until size) {

            val e = events[i]

            when (e.action) {
                KeyEvent.ACTION_DOWN -> event.dispatch(Key(e.keyCode, true))
                KeyEvent.ACTION_UP -> event.dispatch(Key(e.keyCode, false))
            }
        }
    }

    class Key(var code: Int, var pressed: Boolean)
}
