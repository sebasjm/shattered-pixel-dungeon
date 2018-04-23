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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.GameMath
import com.watabou.utils.Point
import com.watabou.utils.Rect

open class RingTunnelRoom : TunnelRoom() {

    //caches the value so multiple calls will always return the same.
    private var connSpace: Rect? = null

    protected override val connectionSpace: Rect
        get() {
            if (connSpace == null) {
                val c = doorCenter

                c.x = GameMath.gate((left + 2).toFloat(), c.x.toFloat(), (right - 2).toFloat()).toInt()
                c.y = GameMath.gate((top + 2).toFloat(), c.y.toFloat(), (bottom - 2).toFloat()).toInt()


                connSpace = Rect(c.x - 1, c.y - 1, c.x + 1, c.y + 1)
            }

            return connSpace!!
        }

    override fun minWidth(): Int {
        return Math.max(5, super.minWidth())
    }

    override fun minHeight(): Int {
        return Math.max(5, super.minHeight())
    }

    override fun paint(level: Level) {
        super.paint(level)

        val floor = level.tunnelTile()

        val ring = connectionSpace

        Painter.fill(level, ring.left, ring.top, 3, 3, floor)
        Painter.fill(level, ring.left + 1, ring.top + 1, 1, 1, Terrain.WALL)
    }
}
