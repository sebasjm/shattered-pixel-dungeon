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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual
import com.watabou.noosa.Image
import com.watabou.utils.Random

import java.util.ArrayList

class MassGraveRoom : SpecialRoom() {

    override fun minWidth(): Int {
        return 7
    }

    override fun minHeight(): Int {
        return 7
    }

    override fun paint(level: Level) {

        val entrance = entrance()
        entrance.set(Room.Door.Type.BARRICADE)
        level.addItemToSpawn(PotionOfLiquidFlame())

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)

        val b = Bones()

        b.setRect(left + 1, top, width() - 2, height() - 1)
        level.customTiles.add(b)

        //50% 1 skeleton, 50% 2 skeletons
        for (i in 0..Random.Int(2)) {
            val skele = Skeleton()

            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != Terrain.EMPTY_SP || level.findMob(pos) != null)
            skele.pos = pos
            level.mobs.add(skele)
        }

        val items = ArrayList<Item>()
        //100% corpse dust, 2x100% 1 coin, 2x30% coins, 1x60% random item, 1x30% armor
        items.add(CorpseDust())
        items.add(Gold(1))
        items.add(Gold(1))
        if (Random.Float() <= 0.3f) items.add(Gold())
        if (Random.Float() <= 0.3f) items.add(Gold())
        if (Random.Float() <= 0.6f) items.add(Generator.random()!!)
        if (Random.Float() <= 0.3f) items.add(Generator.randomArmor()!!)

        for (item in items) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != Terrain.EMPTY_SP || level.heaps.get(pos) != null)
            val h = level.drop(item, pos)
            h.type = Heap.Type.SKELETON
        }
    }

    class Bones : CustomTiledVisual(Assets.PRISON_QUEST) {

        override fun create(): CustomTiledVisual {
            val data = IntArray(tileW * tileH)
            for (i in data.indices) {
                if (i < tileW)
                    data[i] = WALL_OVERLAP
                else
                    data[i] = FLOOR
            }
            map(data, tileW)
            return super.create()
        }

        override fun image(tileX: Int, tileY: Int): Image? {
            return if (tileY == 0)
                null
            else
                super.image(tileX, tileY)
        }

        override fun name(tileX: Int, tileY: Int): String? {
            return Messages.get(this.javaClass, "name")
        }

        override fun desc(tileX: Int, tileY: Int): String? {
            return Messages.get(this.javaClass, "desc")
        }

        companion object {

            private val WALL_OVERLAP = 3
            private val FLOOR = 7
        }
    }
}
