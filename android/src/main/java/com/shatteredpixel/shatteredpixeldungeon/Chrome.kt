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

package com.shatteredpixel.shatteredpixeldungeon

import com.watabou.noosa.NinePatch

object Chrome {

    enum class Type {
        TOAST,
        TOAST_TR,
        WINDOW,
        BUTTON,
        TAG,
        GEM,
        SCROLL,
        TAB_SET,
        TAB_SELECTED,
        TAB_UNSELECTED
    }

    operator fun get(type: Type): NinePatch? {
        val Asset = Assets.CHROME
        when (type) {
            Chrome.Type.WINDOW -> return NinePatch(Asset, 0, 0, 20, 20, 6)
            Chrome.Type.TOAST -> return NinePatch(Asset, 22, 0, 18, 18, 5)
            Chrome.Type.TOAST_TR -> return NinePatch(Asset, 40, 0, 18, 18, 5)
            Chrome.Type.BUTTON -> return NinePatch(Asset, 58, 0, 6, 6, 2)
            Chrome.Type.TAG -> return NinePatch(Asset, 22, 18, 16, 14, 3)
            Chrome.Type.GEM -> return NinePatch(Asset, 0, 32, 32, 32, 13)
            Chrome.Type.SCROLL -> return NinePatch(Asset, 32, 32, 32, 32, 5, 11, 5, 11)
            Chrome.Type.TAB_SET -> return NinePatch(Asset, 64, 0, 20, 20, 6)
            Chrome.Type.TAB_SELECTED -> return NinePatch(Asset, 65, 22, 8, 13, 3, 7, 3, 5)
            Chrome.Type.TAB_UNSELECTED -> return NinePatch(Asset, 75, 22, 8, 13, 3, 7, 3, 5)
            else -> return null
        }
    }
}
