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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class FlockTrap : Trap() {

    init {
        color = Trap.WHITE
        shape = Trap.WAVES
    }


    override fun activate() {
        //use an actor as we want to put this on a slight delay so all chars get a chance to act this turn first.
        Actor.add(object : Actor() {

            init {
                actPriority = BUFF_PRIO
            }

            override fun act(): Boolean {
                PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level!!.solid, null), 2)
                for (i in PathFinder.distance!!.indices) {
                    if (PathFinder.distance!![i] < Integer.MAX_VALUE) {
                        if (Dungeon.level!!.insideMap(i)
                                && Actor.findChar(i) == null
                                && !Dungeon.level!!.pit[i]) {
                            val sheep = Sheep()
                            sheep.lifespan = Random.NormalIntRange(3 + Dungeon.depth / 4, 6 + Dungeon.depth / 2).toFloat()
                            sheep.pos = i
                            Dungeon.level!!.press(sheep.pos, sheep)
                            GameScene.add(sheep)
                            CellEmitter.get(i).burst(Speck.factory(Speck.WOOL), 4)
                        }
                    }
                }
                Sample.INSTANCE.play(Assets.SND_PUFF)
                Actor.remove(this)
                return true
            }
        })

    }

}
