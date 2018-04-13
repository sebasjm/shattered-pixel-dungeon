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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ImpShopkeeper
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom
import com.watabou.utils.Bundle

//shops probably shouldn't extend special room, because of cases like this.
class ImpShopRoom : ShopRoom() {

    private var impSpawned = false

    //force a certain size here to guarantee enough room for 48 items, and the same center space
    override fun minWidth(): Int {
        return 9
    }

    override fun minHeight(): Int {
        return 9
    }

    override fun maxWidth(): Int {
        return 9
    }

    override fun maxHeight(): Int {
        return 9
    }

    override fun maxConnections(direction: Int): Int {
        return 2
    }

    override fun paint(level: Level) {
        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)
        Painter.fill(level, this, 3, Terrain.WATER)

        for (door in connected.values) {
            door.set(Room.Door.Type.REGULAR)
        }

        if (Imp.Quest.isCompleted) {
            impSpawned = true
            placeItems(level)
            placeShopkeeper(level)
        } else {
            impSpawned = false
        }

    }

    override fun placeShopkeeper(level: Level) {

        val pos = level.pointToCell(center())

        val shopkeeper = ImpShopkeeper()
        shopkeeper.pos = pos
        level.mobs.add(shopkeeper)

    }

    //fix for connections not being bundled normally
    override fun entrance(): Room.Door {
        return if (connected.isEmpty()) Room.Door(left, top + 2) else super.entrance()
    }

    private fun spawnShop(level: Level) {
        impSpawned = true
        super.paint(level)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(IMP, impSpawned)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        impSpawned = bundle.getBoolean(IMP)
    }

    override fun onLevelLoad(level: Level) {
        super.onLevelLoad(level)

        if (Imp.Quest.isCompleted && !impSpawned) {
            impSpawned = true
            placeItems(level)
            placeShopkeeper(level)
        }
    }

    companion object {

        private val IMP = "imp_spawned"
    }
}
