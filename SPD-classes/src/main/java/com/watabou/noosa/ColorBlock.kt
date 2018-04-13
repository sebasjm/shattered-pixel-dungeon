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

import com.watabou.gltextures.TextureCache

open class ColorBlock(width: Float, height: Float, color: Int) : Image(TextureCache.createSolid(color)), Resizable {

    init {
        scale.set(width, height)
        origin.set(0f, 0f)
    }

    override fun size(width: Float, height: Float) {
        scale.set(width, height)
    }

    override fun width(): Float {
        return scale.x
    }

    override fun height(): Float {
        return scale.y
    }
}
