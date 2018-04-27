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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.watabou.utils.Point

//import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;

class AltarRoom : SpecialRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, if (Dungeon.bossLevel(Dungeon.depth + 1)) Terrain.HIGH_GRASS else Terrain.CHASM)

        val c = center()
        val door = entrance()
        if (door.x == left || door.x == right) {
            val p = Painter.drawInside(level, this, door, Math.abs(door.x - c.x) - 2, Terrain.EMPTY_SP)
            while (p.y != c.y) {
                Painter.set(level, p, Terrain.EMPTY_SP)
                p.y += if (p.y < c.y) +1 else -1
            }
        } else {
            val p = Painter.drawInside(level, this, door, Math.abs(door.y - c.y) - 2, Terrain.EMPTY_SP)
            while (p.x != c.x) {
                Painter.set(level, p, Terrain.EMPTY_SP)
                p.x += if (p.x < c.x) +1 else -1
            }
        }

        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMBERS)
        Painter.set(level, c, Terrain.PEDESTAL)

        //TODO: find some use for sacrificial fire... but not the vanilla one. scroll of wipe out is too strong.
        /*SacrificialFire fire = (SacrificialFire)level.blobs.get( SacrificialFire.class );
		if (fire == null) {
			fire = new SacrificialFire();
		}
		fire.seed( c.x + c.y * Level.WIDTH, 5 + Dungeon.depth * 5 );
		level.blobs.put( SacrificialFire.class, fire );*/

        door.set(Room.Door.Type.EMPTY)
    }
}