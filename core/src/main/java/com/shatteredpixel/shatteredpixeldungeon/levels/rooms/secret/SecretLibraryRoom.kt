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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.Random

import java.util.HashMap

//TODO specific implementation
class SecretLibraryRoom : SecretRoom() {

    override fun minWidth(): Int {
        return Math.max(7, super.minWidth())
    }

    override fun minHeight(): Int {
        return Math.max(7, super.minHeight())
    }

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.BOOKSHELF)

        Painter.fillEllipse(level, this, 2, Terrain.EMPTY_SP)

        val entrance = entrance()
        if (entrance.x == left || entrance.x == right) {
            Painter.drawInside(level, this, entrance, (width() - 3) / 2, Terrain.EMPTY_SP)
        } else {
            Painter.drawInside(level, this, entrance, (height() - 3) / 2, Terrain.EMPTY_SP)
        }
        entrance.set(Room.Door.Type.HIDDEN)

        val n = Random.IntRange(2, 3)
        val chances = HashMap(scrollChances)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != Terrain.EMPTY_SP || level.heaps.get(pos) != null)

            try {
                val scrollCls = Random.chances(chances)
                chances[scrollCls] = 0f
                level.drop(scrollCls!!.newInstance(), pos)
            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
            }

        }
    }

    companion object {

        private val scrollChances = HashMap<Class<out Scroll>, Float>()

        init {
            scrollChances[ScrollOfIdentify::class.java] = 1f
            scrollChances[ScrollOfTeleportation::class.java] = 1f
            scrollChances[ScrollOfRemoveCurse::class.java] = 3f
            scrollChances[ScrollOfRecharging::class.java] = 1f
            scrollChances[ScrollOfMagicMapping::class.java] = 3f
            scrollChances[ScrollOfRage::class.java] = 1f
            scrollChances[ScrollOfTerror::class.java] = 2f
            scrollChances[ScrollOfLullaby::class.java] = 2f
            scrollChances[ScrollOfPsionicBlast::class.java] = 5f
            scrollChances[ScrollOfMirrorImage::class.java] = 1f
        }
    }

}
