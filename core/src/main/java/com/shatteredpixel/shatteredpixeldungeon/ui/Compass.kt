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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.utils.PointF

class Compass(private val cell: Int) : Image() {
    private val cellCenter: PointF

    private val lastScroll = PointF()

    init {
        copy(Icons.COMPASS.get())
        origin.set(width / 2, RADIUS)
        cellCenter = DungeonTilemap.tileCenterToWorld(cell)
        visible = false
    }

    override fun update() {
        super.update()

        if (cell < 0 || cell >= Dungeon.level!!.length()) {
            visible = false
            return
        }

        if (!visible) {
            visible = Dungeon.level!!.visited!![cell] || Dungeon.level!!.mapped!![cell]
        }

        if (visible) {
            val scroll = Camera.main.scroll
            if (scroll != lastScroll) {
                lastScroll.set(scroll)
                val center = Camera.main.center().offset(scroll)
                angle = Math.atan2((cellCenter.x - center.x).toDouble(), (center.y - cellCenter.y).toDouble()).toFloat() * RAD_2_G
            }
        }
    }

    companion object {

        private val RAD_2_G = 180f / 3.1415926f
        private val RADIUS = 12f
    }
}
