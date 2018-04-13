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

package com.watabou.utils

import java.util.LinkedList

class Signal<T> @JvmOverloads constructor(private val stackMode: Boolean = false) {

    private val listeners = LinkedList<Signal.Listener<T>>()

    private var canceled: Boolean = false

    @Synchronized
    fun add(listener: Listener<T>) {
        if (!listeners.contains(listener)) {
            if (stackMode) {
                listeners.addFirst(listener)
            } else {
                listeners.addLast(listener)
            }
        }
    }

    @Synchronized
    fun remove(listener: Listener<T>) {
        listeners.remove(listener)
    }

    @Synchronized
    fun removeAll() {
        listeners.clear()
    }

    @Synchronized
    fun replace(listener: Listener<T>) {
        removeAll()
        add(listener)
    }

    @Synchronized
    fun numListeners(): Int {
        return listeners.size
    }

    @Synchronized
    fun dispatch(t: T?) {

        val list = listeners.toTypedArray()

        canceled = false
        for (listener in list) {

            if (listeners.contains(listener)) {
                listener.onSignal(t!!)
                if (canceled) {
                    return
                }
            }

        }
    }

    fun cancel() {
        canceled = true
    }

    interface Listener<in T> {
        fun onSignal(t: T)
    }
}
