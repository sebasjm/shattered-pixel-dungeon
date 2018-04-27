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

package com.shatteredpixel.shatteredpixeldungeon.levels.painters

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet
import com.watabou.utils.Random
import com.watabou.utils.Rect

import java.util.ArrayList

class CavesPainter : RegularPainter() {

    override fun decorate(level: Level, rooms: ArrayList<Room>) {

        val w = level.width()
        val l = level.length()
        val map = level.map

        for (room in rooms) {
            if (!(room is EmptyRoom || room is CaveRoom)) {
                continue
            }

            if (room.width() <= 4 || room.height() <= 4) {
                continue
            }

            val s = room.square()

            if (Random.Int(s) > 8) {
                val corner = room.left + 1 + (room.top + 1) * w
                if (map!![corner - 1] == Terrain.WALL && map[corner - w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                    level.traps.remove(corner)
                }
            }

            if (Random.Int(s) > 8) {
                val corner = room.right - 1 + (room.top + 1) * w
                if (map!![corner + 1] == Terrain.WALL && map[corner - w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                    level.traps.remove(corner)
                }
            }

            if (Random.Int(s) > 8) {
                val corner = room.left + 1 + (room.bottom - 1) * w
                if (map!![corner - 1] == Terrain.WALL && map[corner + w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                    level.traps.remove(corner)
                }
            }

            if (Random.Int(s) > 8) {
                val corner = room.right - 1 + (room.bottom - 1) * w
                if (map!![corner + 1] == Terrain.WALL && map[corner + w] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                    level.traps.remove(corner)
                }
            }

            for (n in room.connected.keys) {
                if ((n is StandardRoom || n is ConnectionRoom) && Random.Int(3) == 0) {
                    Painter.set(level, room.connected[n]!!, Terrain.EMPTY_DECO)
                }
            }
        }

        for (i in w + 1 until l - w) {
            if (map!![i] == Terrain.EMPTY) {
                var n = 0
                if (map[i + 1] == Terrain.WALL) {
                    n++
                }
                if (map[i - 1] == Terrain.WALL) {
                    n++
                }
                if (map[i + w] == Terrain.WALL) {
                    n++
                }
                if (map[i - w] == Terrain.WALL) {
                    n++
                }
                if (Random.Int(6) <= n) {
                    map[i] = Terrain.EMPTY_DECO
                }
            }
        }

        for (i in 0 until l - w) {
            if (map!![i] == Terrain.WALL &&
                    DungeonTileSheet.floorTile(map[i + w])
                    && Random.Int(4) == 0) {
                map[i] = Terrain.WALL_DECO
            }
        }

        for (r in rooms) {
            if (r is EmptyRoom) {
                for (n in r.neigbours) {
                    if (n is EmptyRoom && !r.connected.containsKey(n)) {
                        val i = r.intersect(n)
                        if (i.left == i.right && i.bottom - i.top >= 5) {

                            i.top += 2
                            i.bottom -= 1

                            i.right++

                            Painter.fill(level, i.left, i.top, 1, i.height(), Terrain.CHASM)

                        } else if (i.top == i.bottom && i.right - i.left >= 5) {

                            i.left += 2
                            i.right -= 1

                            i.bottom++

                            Painter.fill(level, i.left, i.top, i.width(), 1, Terrain.CHASM)
                        }
                    }
                }
            }
        }
    }
}
