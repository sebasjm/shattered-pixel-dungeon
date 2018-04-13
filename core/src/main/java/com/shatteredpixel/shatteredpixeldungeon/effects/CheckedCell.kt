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

import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Game
import com.watabou.noosa.Image

class CheckedCell(pos: Int) : Image(TextureCache.createSolid(-0xaa5501)) {

    private var alpha: Float = 0.toFloat()

    init {

        origin.set(0.5f)

        point(DungeonTilemap.tileToWorld(pos).offset(
                (DungeonTilemap.SIZE / 2).toFloat(),
                (DungeonTilemap.SIZE / 2).toFloat()))

        alpha = 0.8f
    }

    override fun update() {
        if ((alpha -= Game.elapsed) > 0) {
            alpha(alpha)
            scale.set(DungeonTilemap.SIZE * alpha)
        } else {
            killAndErase()
        }
    }
}
