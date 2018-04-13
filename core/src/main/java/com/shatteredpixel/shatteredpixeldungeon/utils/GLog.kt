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

package com.shatteredpixel.shatteredpixeldungeon.utils

import android.util.Log

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Signal

object GLog {

    val TAG = "GAME"

    val POSITIVE = "++ "
    val NEGATIVE = "-- "
    val WARNING = "** "
    val HIGHLIGHT = "@@ "

    var update = Signal<String>()

    fun i(text: String, vararg args: Any) {
        var text = text

        if (args.size > 0) {
            text = Messages.format(text, *args)
        }

        Log.i(TAG, text)
        update.dispatch(text)
    }

    fun p(text: String, vararg args: Any) {
        i(POSITIVE + text, *args)
    }

    fun n(text: String, vararg args: Any) {
        i(NEGATIVE + text, *args)
    }

    fun w(text: String, vararg args: Any) {
        i(WARNING + text, *args)
    }

    fun h(text: String, vararg args: Any) {
        i(HIGHLIGHT + text, *args)
    }
}
