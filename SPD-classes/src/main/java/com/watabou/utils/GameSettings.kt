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

import android.content.SharedPreferences
import android.os.Build

import com.watabou.noosa.Game

open class GameSettings {
    companion object {

        private var prefs: SharedPreferences? = null

        private fun get(): SharedPreferences {
            if (prefs == null) {
                prefs = Game.instance!!.getPreferences(Game.MODE_PRIVATE)
            }
            return prefs
        }

        operator fun contains(key: String): Boolean {
            return get().contains(key)
        }

        @JvmOverloads
        fun getInt(key: String, defValue: Int, min: Int = Integer.MIN_VALUE, max: Int = Integer.MAX_VALUE): Int {
            try {
                val i = get().getInt(key, defValue)
                if (i < min || i > max) {
                    val `val` = GameMath.gate(min.toFloat(), i.toFloat(), max.toFloat()).toInt()
                    put(key, `val`)
                    return `val`
                } else {
                    return i
                }
            } catch (e: ClassCastException) {
                //ShatteredPixelDungeon.reportException(e);
                put(key, defValue)
                return defValue
            }

        }

        fun getBoolean(key: String, defValue: Boolean): Boolean {
            try {
                return get().getBoolean(key, defValue)
            } catch (e: ClassCastException) {
                //ShatteredPixelDungeon.reportException(e);
                put(key, defValue)
                return defValue
            }

        }

        @JvmOverloads
        fun getString(key: String, defValue: String, maxLength: Int = Integer.MAX_VALUE): String? {
            try {
                val s = get().getString(key, defValue)
                if (s != null && s.length > maxLength) {
                    put(key, defValue)
                    return defValue
                } else {
                    return s
                }
            } catch (e: ClassCastException) {
                //ShatteredPixelDungeon.reportException(e);
                put(key, defValue)
                return defValue
            }

        }

        //android 2.3+ supports apply, which is asyncronous, much nicer

        fun put(key: String, value: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                get().edit().putInt(key, value).apply()
            } else {
                get().edit().putInt(key, value).commit()
            }
        }

        fun put(key: String, value: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                get().edit().putBoolean(key, value).apply()
            } else {
                get().edit().putBoolean(key, value).commit()
            }
        }

        fun put(key: String, value: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                get().edit().putString(key, value).apply()
            } else {
                get().edit().putString(key, value).commit()
            }
        }
    }

}
