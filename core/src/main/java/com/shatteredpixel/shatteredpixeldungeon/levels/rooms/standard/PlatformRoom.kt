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

import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.Random
import com.watabou.utils.Rect

import java.util.ArrayList

class PlatformRoom : StandardRoom() {

    override fun minWidth(): Int {
        return Math.max(super.minWidth(), 6)
    }

    override fun minHeight(): Int {
        return Math.max(super.minHeight(), 6)
    }

    override fun sizeCatProbs(): FloatArray {
        return floatArrayOf(6f, 3f, 1f)
    }

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)

        Painter.fill(level, this, 1, Terrain.CHASM)

        val platforms = ArrayList<Rect>()
        splitPlatforms(Rect(left + 2, top + 2, right - 2, bottom - 2), platforms)

        for (platform in platforms) {
            Painter.fill(level, platform.left, platform.top, platform.width() + 1, platform.height() + 1, Terrain.EMPTY_SP)
        }

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
            Painter.drawInside(level, this, door, 2, Terrain.EMPTY_SP)
        }

    }

    private fun splitPlatforms(curPlatform: Rect, allPlatforms: ArrayList<Rect>) {
        val curArea = (curPlatform.width() + 1) * (curPlatform.height() + 1)

        //chance to split scales between 0% and 100% between areas of 25 and 36
        if (Random.Float() < (curArea - 25) / 11f) {
            if (curPlatform.width() > curPlatform.height() || curPlatform.width() == curPlatform.height() && Random.Int(2) == 0) {

                //split the platform
                val splitX = Random.IntRange(curPlatform.left + 2, curPlatform.right - 2)
                splitPlatforms(Rect(curPlatform.left, curPlatform.top, splitX - 1, curPlatform.bottom), allPlatforms)
                splitPlatforms(Rect(splitX + 1, curPlatform.top, curPlatform.right, curPlatform.bottom), allPlatforms)

                //add a bridge between
                val bridgeY = Random.NormalIntRange(curPlatform.top, curPlatform.bottom)
                allPlatforms.add(Rect(splitX - 1, bridgeY, splitX + 1, bridgeY))

            } else {

                //split the platform
                val splitY = Random.IntRange(curPlatform.top + 2, curPlatform.bottom - 2)
                splitPlatforms(Rect(curPlatform.left, curPlatform.top, curPlatform.right, splitY - 1), allPlatforms)
                splitPlatforms(Rect(curPlatform.left, splitY + 1, curPlatform.right, curPlatform.bottom), allPlatforms)

                //add a bridge between
                val bridgeX = Random.NormalIntRange(curPlatform.left, curPlatform.right)
                allPlatforms.add(Rect(bridgeX, splitY - 1, bridgeX, splitY + 1))

            }
        } else {
            allPlatforms.add(curPlatform)
        }
    }

}
