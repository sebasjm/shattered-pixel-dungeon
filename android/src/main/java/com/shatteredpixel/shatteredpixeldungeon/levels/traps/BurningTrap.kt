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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.utils.PathFinder

class BurningTrap : Trap() {

    init {
        color = Trap.ORANGE
        shape = Trap.DOTS
    }

    override fun activate() {

        for (i in PathFinder.NEIGHBOURS9!!) {
            if (!Dungeon.level!!.solid[pos + i]) {
                GameScene.add(Blob.seed<Fire>(pos + i, 2, Fire::class.java)!!)
                CellEmitter.get(pos + i).burst(FlameParticle.FACTORY, 5)
            }
        }

    }
}
