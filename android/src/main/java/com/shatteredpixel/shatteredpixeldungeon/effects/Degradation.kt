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

import com.watabou.glwrap.Blending
import com.watabou.noosa.Group
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random

class Degradation private constructor(p: PointF, matrix: IntArray) : Group() {

    init {

        var i = 0
        while (i < matrix.size) {
            add(Speck(p.x, p.y, matrix[i], matrix[i + 1]))
            add(Speck(p.x, p.y, matrix[i], matrix[i + 1]))
            i += 2
        }
    }

    override fun update() {
        super.update()
        if (countLiving() == 0) {
            killAndErase()
        }
    }

    override fun draw() {
        Blending.setLightMode()
        super.draw()
        Blending.setNormalMode()
    }

    class Speck(x0: Float, y0: Float, mx: Int, my: Int) : PixelParticle() {

        init {
            var x0 = x0
            var y0 = y0
            color(COLOR)

            val x1 = x0 + mx * SIZE
            val y1 = y0 + my * SIZE

            val p = PointF().polar(Random.Float(2 * PointF.PI), 8f)
            x0 += p.x
            y0 += p.y

            val dx = x1 - x0
            val dy = y1 - y0

            x = x0
            y = y0
            speed.set(dx, dy)
            acc.set(-dx / 4, -dy / 4)

            lifespan = 2f
            left = lifespan
        }

        override fun update() {
            super.update()

            am = 1 - Math.abs(left / lifespan - 0.5f) * 2
            am *= am
            size(am * SIZE)
        }

        companion object {

            private val COLOR = 0xFF4422
            private val SIZE = 3
        }
    }

    companion object {

        private val WEAPON = intArrayOf(+2, -2, +1, -1, 0, 0, -1, +1, -2, +2, -2, 0, 0, +2)

        private val ARMOR = intArrayOf(-2, -1, -1, -1, +1, -1, +2, -1, -2, 0, -1, 0, 0, 0, +1, 0, +2, 0, -1, +1, +1, +1, -1, +2, 0, +2, +1, +2)

        private val RING = intArrayOf(0, -1, -1, 0, 0, 0, +1, 0, -1, +1, +1, +1, -1, +2, 0, +2, +1, +2)

        private val WAND = intArrayOf(+2, -2, +1, -1, 0, 0, -1, +1, -2, +2, +1, -2, +2, -1)

        fun weapon(p: PointF): Degradation {
            return Degradation(p, WEAPON)
        }

        fun armor(p: PointF): Degradation {
            return Degradation(p, ARMOR)
        }

        fun ring(p: PointF): Degradation {
            return Degradation(p, RING)
        }

        fun wand(p: PointF): Degradation {
            return Degradation(p, WAND)
        }
    }
}
