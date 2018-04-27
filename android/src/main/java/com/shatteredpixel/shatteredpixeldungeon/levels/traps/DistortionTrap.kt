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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.watabou.noosa.Game

class DistortionTrap : Trap() {

    init {
        color = Trap.TEAL
        shape = Trap.LARGE_DOT
    }

    override fun activate() {
        InterlevelScene.returnDepth = Dungeon.depth
        val belongings = Dungeon.hero!!.belongings

        for (rec in Notes.getRecords()) {
            if (rec.depth() == Dungeon.depth) {
                Notes.remove(rec)
            }
        }

        for (i in belongings) {
            if (i is LloydsBeacon && i.returnDepth == Dungeon.depth)
                i.returnDepth = -1
        }

        InterlevelScene.mode = InterlevelScene.Mode.RESET
        Game.switchScene(InterlevelScene::class.java)
    }
}
