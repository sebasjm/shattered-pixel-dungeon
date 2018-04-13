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

package com.shatteredpixel.shatteredpixeldungeon.effects

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.watabou.noosa.Image

object Effects {

    enum class Type {
        RIPPLE,
        LIGHTNING,
        WOUND,
        EXCLAMATION,
        CHAIN,
        DEATH_RAY,
        LIGHT_RAY,
        HEALTH_RAY
    }

    operator fun get(type: Type): Image {
        val icon = Image(Assets.EFFECTS)
        when (type) {
            Effects.Type.RIPPLE -> icon.frame(icon.texture!!.uvRect(0f, 0f, 16f, 16f))
            Effects.Type.LIGHTNING -> icon.frame(icon.texture!!.uvRect(16f, 0f, 32f, 8f))
            Effects.Type.WOUND -> icon.frame(icon.texture!!.uvRect(16f, 8f, 32f, 16f))
            Effects.Type.EXCLAMATION -> icon.frame(icon.texture!!.uvRect(0f, 16f, 6f, 25f))
            Effects.Type.CHAIN -> icon.frame(icon.texture!!.uvRect(6f, 16f, 11f, 22f))
            Effects.Type.DEATH_RAY -> icon.frame(icon.texture!!.uvRect(16f, 16f, 32f, 24f))
            Effects.Type.LIGHT_RAY -> icon.frame(icon.texture!!.uvRect(16f, 23f, 32f, 31f))
            Effects.Type.HEALTH_RAY -> icon.frame(icon.texture!!.uvRect(16f, 30f, 32f, 38f))
        }
        return icon
    }
}
