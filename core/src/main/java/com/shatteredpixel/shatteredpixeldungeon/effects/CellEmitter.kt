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

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.PointF

object CellEmitter {

    operator fun get(cell: Int): Emitter {

        val p = DungeonTilemap.tileToWorld(cell)

        val emitter = GameScene.emitter()
        emitter!!.pos(p.x, p.y, DungeonTilemap.SIZE.toFloat(), DungeonTilemap.SIZE.toFloat())

        return emitter
    }

    fun center(cell: Int): Emitter {

        val p = DungeonTilemap.tileToWorld(cell)

        val emitter = GameScene.emitter()
        emitter!!.pos(p.x + DungeonTilemap.SIZE / 2, p.y + DungeonTilemap.SIZE / 2)

        return emitter
    }

    fun bottom(cell: Int): Emitter {

        val p = DungeonTilemap.tileToWorld(cell)

        val emitter = GameScene.emitter()
        emitter!!.pos(p.x, p.y + DungeonTilemap.SIZE, DungeonTilemap.SIZE.toFloat(), 0f)

        return emitter
    }
}
