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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual
import com.watabou.utils.Point

class RitualSiteRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(super.minWidth(), 5)
    }

    override fun minHeight(): Int {
        return Math.max(super.minHeight(), 5)
    }

    override fun paint(level: Level) {

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY)

        val vis = RitualMarker()
        val c = center()
        vis.pos(c.x - 1, c.y - 1)

        level.customTiles.add(vis)

        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMPTY_DECO)

        level.addItemToSpawn(CeremonialCandle())
        level.addItemToSpawn(CeremonialCandle())
        level.addItemToSpawn(CeremonialCandle())
        level.addItemToSpawn(CeremonialCandle())

        CeremonialCandle.ritualPos = c.x + level.width() * c.y
    }

    class RitualMarker : CustomTiledVisual(Assets.PRISON_QUEST) {

        override fun create(): CustomTiledVisual {
            tileW = 3
            tileH = tileW
            mapSimpleImage(0, 0)
            return super.create()
        }

        override fun name(tileX: Int, tileY: Int): String? {
            return Messages.get(this, "name")
        }

        override fun desc(tileX: Int, tileY: Int): String? {
            return Messages.get(this, "desc")
        }
    }

}
