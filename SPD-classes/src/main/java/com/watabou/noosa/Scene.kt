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

import com.watabou.input.Keys
import com.watabou.utils.Signal

open class Scene : Group() {

    private var keyListener: Signal.Listener<Keys.Key>? = null

    open fun create() {
        keyListener = object: Signal.Listener<Keys.Key> {
            override fun onSignal(key: Keys.Key) {
                if (Game.instance != null && key.pressed) {
                    when (key.code) {
                        Keys.BACK -> onBackPressed()
                        Keys.MENU -> onMenuPressed()
                    }
                }
            }
        }
        Keys.event.add(keyListener!!)
    }

    override fun destroy() {
        Keys.event.remove(keyListener!!)
        super.destroy()
    }

    open fun onPause() {

    }

    fun onResume() {

    }

    override fun update() {
        super.update()
    }

    override fun camera(): Camera? {
        return Camera.main!!
    }

    protected open fun onBackPressed() {
        Game.instance!!.finish()
    }

    protected open fun onMenuPressed() {

    }

}
