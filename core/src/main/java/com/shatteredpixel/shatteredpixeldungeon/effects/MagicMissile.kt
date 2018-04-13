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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Visual
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Callback
import com.watabou.utils.ColorMath
import com.watabou.utils.PointF
import com.watabou.utils.Random

class MagicMissile : Emitter() {

    private var callback: Callback? = null

    private var sx: Float = 0.toFloat()
    private var sy: Float = 0.toFloat()
    private var time: Float = 0.toFloat()

    fun reset(type: Int, from: Int, to: Int, callback: Callback) {
        reset(type,
                DungeonTilemap.raisedTileCenterToWorld(from),
                DungeonTilemap.raisedTileCenterToWorld(to),
                callback)
    }

    fun reset(type: Int, from: Visual, to: Visual?, callback: Callback) {
        reset(type,
                from.center(),
                to!!.center(),
                callback)
    }

    fun reset(type: Int, from: Visual, to: Int, callback: Callback) {
        reset(type,
                from.center(),
                DungeonTilemap.raisedTileCenterToWorld(to),
                callback)
    }

    fun reset(type: Int, from: PointF, to: PointF, callback: Callback) {
        this.callback = callback

        revive()

        x = from.x
        y = from.y
        width = 0f
        height = 0f

        val d = PointF.diff(to, from)
        val speed = PointF(d).normalize().scale(SPEED)
        sx = speed.x
        sy = speed.y
        time = d.length() / SPEED

        when (type) {
            MAGIC_MISSILE -> {
                size(4f)
                pour(WhiteParticle.FACTORY, 0.01f)
            }
            FROST -> pour(MagicParticle.FACTORY, 0.01f)
            FIRE -> {
                size(4f)
                pour(FlameParticle.FACTORY, 0.01f)
            }
            CORROSION -> {
                size(3f)
                pour(CorrosionParticle.MISSILE, 0.01f)
            }
            FOLIAGE -> {
                size(4f)
                pour(LeafParticle.GENERAL, 0.01f)
            }
            FORCE -> pour(SlowParticle.FACTORY, 0.01f)
            BEACON -> pour(ForceParticle.FACTORY, 0.01f)
            SHADOW -> {
                size(4f)
                pour(ShadowParticle.MISSILE, 0.01f)
            }
            RAINBOW -> {
                size(4f)
                pour(RainbowParticle.BURST, 0.01f)
            }

            FIRE_CONE -> {
                size(10f)
                pour(FlameParticle.FACTORY, 0.03f)
            }
            FOLIAGE_CONE -> {
                size(10f)
                pour(LeafParticle.GENERAL, 0.03f)
            }
            else -> {
                size(4f)
                pour(WhiteParticle.FACTORY, 0.01f)
            }
        }
    }

    fun size(size: Float) {
        x -= size / 2
        y -= size / 2
        height = size
        width = height
    }

    override fun update() {
        super.update()
        if (on) {
            val d = Game.elapsed
            x += sx * d
            y += sy * d
            if ((time -= d) <= 0) {
                on = false
                if (callback != null) callback!!.call()
            }
        }
    }

    class MagicParticle : PixelParticle() {
        init {

            color(0x88CCFF)
            lifespan = 0.5f

            speed.set(Random.Float(-10f, +10f), Random.Float(-10f, +10f))
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            left = lifespan
        }

        fun resetAttract(x: Float, y: Float) {
            revive()

            //size = 8;
            left = lifespan

            speed.polar(Random.Float(PointF.PI2), Random.Float(16f, 32f))
            this.x = x - speed.x * lifespan
            this.y = y - speed.y * lifespan
        }

        override fun update() {
            super.update()
            // alpha: 1 -> 0; size: 1 -> 4
            size(4 - (am = left / lifespan) * 3)
        }

        companion object {

            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(MagicParticle::class.java) as MagicParticle).reset(x, y)
                }

                override fun lightMode(): Boolean {
                    return true
                }
            }

            val ATTRACTING: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(MagicParticle::class.java) as MagicParticle).resetAttract(x, y)
                }

                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }

    class EarthParticle : PixelParticle.Shrinking() {
        init {

            lifespan = 0.5f

            color(ColorMath.random(0x555555, 0x777766))

            acc.set(0f, +40f)
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            left = lifespan
            size = 4f

            speed.set(Random.Float(-10f, +10f), Random.Float(-10f, +10f))
        }

        companion object {

            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(EarthParticle::class.java) as EarthParticle).reset(x, y)
                }
            }
        }
    }

    class WhiteParticle : PixelParticle() {
        init {

            lifespan = 0.4f

            am = 0.5f
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            left = lifespan
        }

        override fun update() {
            super.update()
            // size: 3 -> 0
            size(left / lifespan * 3)
        }

        companion object {

            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(WhiteParticle::class.java) as WhiteParticle).reset(x, y)
                }

                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }

    class SlowParticle : PixelParticle() {

        private var emitter: Emitter? = null

        init {

            lifespan = 0.6f

            color(0x664422)
            size(2f)
        }

        fun reset(x: Float, y: Float, emitter: Emitter) {
            revive()

            this.x = x
            this.y = y
            this.emitter = emitter

            left = lifespan

            acc.set(0f)
            speed.set(Random.Float(-20f, +20f), Random.Float(-20f, +20f))
        }

        override fun update() {
            super.update()

            am = left / lifespan
            acc.set((emitter!!.x - x) * 10, (emitter!!.y - y) * 10)
        }

        companion object {

            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(SlowParticle::class.java) as SlowParticle).reset(x, y, emitter)
                }

                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }

    class ForceParticle : PixelParticle.Shrinking() {

        fun reset(index: Int, x: Float, y: Float) {
            super.reset(x, y, 0xFFFFFF, 8f, 0.5f)

            speed.polar(PointF.PI2 / 8 * index, 12f)
            this.x -= speed.x * lifespan
            this.y -= speed.y * lifespan
        }

        override fun update() {
            super.update()

            am = (1 - left / lifespan) / 2
        }

        companion object {

            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(ForceParticle::class.java) as ForceParticle).reset(index, x, y)
                }
            }
        }
    }

    class ColdParticle : PixelParticle.Shrinking() {
        init {

            lifespan = 0.6f

            color(0x2244FF)
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            left = lifespan
            size = 8f
        }

        override fun update() {
            super.update()

            am = 1 - left / lifespan
        }

        companion object {

            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(ColdParticle::class.java) as ColdParticle).reset(x, y)
                }

                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }

    companion object {

        private val SPEED = 200f

        //missile types
        val MAGIC_MISSILE = 0
        val FROST = 1
        val FIRE = 2
        val CORROSION = 3
        val FOLIAGE = 4
        val FORCE = 5
        val BEACON = 6
        val SHADOW = 7
        val RAINBOW = 8

        val FIRE_CONE = 100
        val FOLIAGE_CONE = 101

        //convenience method for the common case of a bolt going from a character to a tile or enemy
        fun boltFromChar(group: Group, type: Int, sprite: Visual, to: Int, callback: Callback) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            if (Actor.findChar(to) != null) {
                missile.reset(type, sprite, Actor.findChar(to)!!.sprite, callback)
            } else {
                missile.reset(type, sprite, to, callback)
            }
        }
    }
}
