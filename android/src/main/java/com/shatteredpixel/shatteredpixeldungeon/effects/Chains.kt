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

package com.shatteredpixel.shatteredpixeldungeon.effects

import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.utils.Callback
import com.watabou.utils.PointF

class Chains(private val from: PointF, private val to: PointF, private val callback: Callback?) : Group() {

    private var spent = 0f
    private val duration: Float

    private val chains: Array<Image>
    private val numChains: Int
    private val distance: Float
    private var rotation = 0f

    constructor(from: Int, to: Int, callback: Callback) : this(DungeonTilemap.tileCenterToWorld(from),
            DungeonTilemap.tileCenterToWorld(to),
            callback) {
    }

    init {

        val dx = to.x - from.x
        val dy = to.y - from.y
        distance = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()


        duration = distance / 300f + 0.1f

        rotation = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat() + 90f

        numChains = Math.round(distance / 6f) + 1

        chains = (0 until numChains).map {
            val chain = Image(Effects.get(Effects.Type.CHAIN))
            chain.angle = rotation
            chain.origin.set(chain.width() / 2, chain.height())
            add(chain)
            chain
        }.toTypedArray()
    }

    override fun update() {
        spent += Game.elapsed
        if (spent > duration) {

            killAndErase()
            callback?.call()

        } else {
            val dx = to.x - from.x
            val dy = to.y - from.y
            for (i in chains.indices) {
                chains[i].center(PointF(
                        from.x + dx * (i / chains.size.toFloat()) * (spent / duration),
                        from.y + dy * (i / chains.size.toFloat()) * (spent / duration)
                ))
            }
        }
    }

    companion object {

        private val A = 180 / Math.PI
    }

}
