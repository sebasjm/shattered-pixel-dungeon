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
import com.watabou.gltextures.SmartTexture
import com.watabou.glwrap.Texture
import com.watabou.noosa.NinePatch

class ShadowBox : NinePatch(Assets.SHADOW, 1) {
    init {

        //If this is the first time the texture is generated, set the filtering
        if (texture.id == -1)
            texture.filter(Texture.LINEAR, Texture.LINEAR)

        scale.set(SIZE, SIZE)
    }

    override fun size(width: Float, height: Float) {
        super.size(width / SIZE, height / SIZE)
    }

    fun boxRect(x: Float, y: Float, width: Float, height: Float) {
        this.x = x - SIZE
        this.y = y - SIZE
        size(width + SIZE * 2, height + SIZE * 2)
    }

    companion object {

        val SIZE = 16f
    }
}
