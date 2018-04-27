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

object BannerSprites {

    enum class Type {
        PIXEL_DUNGEON,
        BOSS_SLAIN,
        GAME_OVER,
        SELECT_YOUR_HERO,
        PIXEL_DUNGEON_SIGNS
    }

    operator fun get(type: Type): Image {
        val icon = Image(Assets.BANNERS)
        when (type) {
            BannerSprites.Type.PIXEL_DUNGEON -> icon.frame(icon.texture!!.uvRect(0f, 0f, 132f, 90f))
            BannerSprites.Type.BOSS_SLAIN -> icon.frame(icon.texture!!.uvRect(0f, 90f, 128f, 125f))
            BannerSprites.Type.GAME_OVER -> icon.frame(icon.texture!!.uvRect(0f, 125f, 128f, 160f))
            BannerSprites.Type.SELECT_YOUR_HERO -> icon.frame(icon.texture!!.uvRect(0f, 160f, 128f, 181f))
            BannerSprites.Type.PIXEL_DUNGEON_SIGNS -> icon.frame(icon.texture!!.uvRect(133f, 0f, 255f, 90f))
        }
        return icon
    }
}
