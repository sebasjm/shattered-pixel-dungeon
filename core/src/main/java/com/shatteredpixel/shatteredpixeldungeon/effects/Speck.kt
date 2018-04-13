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

import android.annotation.SuppressLint
import android.util.SparseArray

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.ColorMath
import com.watabou.utils.PointF
import com.watabou.utils.Random

class Speck : Image() {

    private var type: Int = 0
    private var lifespan: Float = 0.toFloat()
    private var left: Float = 0.toFloat()

    init {

        texture(Assets.SPECKS)
        if (film == null) {
            film = TextureFilm(texture, SIZE, SIZE)
        }

        origin.set(SIZE / 2f)
    }

    fun reset(index: Int, x: Float, y: Float, type: Int) {
        var y = y
        revive()

        this.type = type
        when (type) {
            DISCOVER, RED_LIGHT -> frame(film!!.get(LIGHT))
            EVOKE, MASTERY, KIT, FORGE -> frame(film!!.get(STAR))
            RATTLE -> frame(film!!.get(BONE))
            JET, TOXIC, CORROSION, PARALYSIS, STENCH, CONFUSION, DUST -> frame(film!!.get(STEAM))
            else -> frame(film!!.get(type))
        }

        this.x = x - origin.x
        this.y = y - origin.y

        resetColor()
        scale.set(1f)
        speed.set(0f)
        acc.set(0f)
        angle = 0f
        angularSpeed = 0f

        when (type) {

            HEALING -> {
                speed.set(0f, -20f)
                lifespan = 1f
            }

            STAR -> {
                speed.polar(Random.Float(2 * 3.1415926f), Random.Float(128f))
                acc.set(0f, 128f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
                lifespan = 1f
            }

            FORGE -> {
                speed.polar(Random.Float(-3.1415926f), Random.Float(64f))
                acc.set(0f, 128f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
                lifespan = 0.51f
            }

            EVOKE -> {
                speed.polar(Random.Float(-3.1415926f), 50f)
                acc.set(0f, 50f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-180f, +180f)
                lifespan = 1f
            }

            KIT -> {
                speed.polar(index * 3.1415926f / 5, 50f)
                acc.set(-speed.x, -speed.y)
                angle = (index * 36).toFloat()
                angularSpeed = 360f
                lifespan = 1f
            }

            MASTERY -> {
                speed.set(if (Random.Int(2) == 0) Random.Float(-128f, -64f) else Random.Float(+64f, +128f), 0f)
                angularSpeed = (if (speed.x < 0) -180 else +180).toFloat()
                acc.set(-speed.x, 0f)
                lifespan = 0.5f
            }

            RED_LIGHT -> {
                tint(-0x340000)
                angle = Random.Float(360f)
                angularSpeed = 90f
                lifespan = 1f
            }
            LIGHT -> {
                angle = Random.Float(360f)
                angularSpeed = 90f
                lifespan = 1f
            }

            DISCOVER -> {
                angle = Random.Float(360f)
                angularSpeed = 90f
                lifespan = 0.5f
                am = 0f
            }

            QUESTION -> lifespan = 0.8f

            UP -> {
                speed.set(0f, -20f)
                lifespan = 1f
            }

            SCREAM -> lifespan = 0.9f

            BONE -> {
                lifespan = 0.2f
                speed.polar(Random.Float(2 * 3.1415926f), 24 / lifespan)
                acc.set(0f, 128f)
                angle = Random.Float(360f)
                angularSpeed = 360f
            }

            RATTLE -> {
                lifespan = 0.5f
                speed.set(0f, -200f)
                acc.set(0f, -2 * speed.y / lifespan)
                angle = Random.Float(360f)
                angularSpeed = 360f
            }

            WOOL -> {
                lifespan = 0.5f
                speed.set(0f, -50f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
            }

            ROCK -> {
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
                scale.set(Random.Float(1f, 2f))
                speed.set(0f, 64f)
                lifespan = 0.2f
                y -= speed.y * lifespan
            }

            NOTE -> {
                angularSpeed = Random.Float(-30f, +30f)
                speed.polar((angularSpeed - 90) * PointF.G2R, 30f)
                lifespan = 1f
            }

            CHANGE -> {
                angle = Random.Float(360f)
                speed.polar((angle - 90) * PointF.G2R, Random.Float(4f, 12f))
                lifespan = 1.5f
            }

            HEART -> {
                speed.set(Random.Int(-10, +10).toFloat(), -40f)
                angularSpeed = Random.Float(-45f, +45f)
                lifespan = 1f
            }

            BUBBLE -> {
                speed.set(0f, -15f)
                scale.set(Random.Float(0.8f, 1f))
                lifespan = Random.Float(0.8f, 1.5f)
            }

            STEAM -> {
                speed.y = -Random.Float(20f, 30f)
                angularSpeed = Random.Float(+180f)
                angle = Random.Float(360f)
                lifespan = 1f
            }

            JET -> {
                speed.y = +32f
                acc.y = -64f
                angularSpeed = Random.Float(180f, 360f)
                angle = Random.Float(360f)
                lifespan = 0.5f
            }

            TOXIC -> {
                hardlight(0x50FF60)
                angularSpeed = 30f
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }

            CORROSION -> {
                hardlight(0xAAAAAA)
                angularSpeed = 30f
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }

            PARALYSIS -> {
                hardlight(0xFFFF66)
                angularSpeed = -30f
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }

            STENCH -> {
                hardlight(0x003300)
                angularSpeed = -30f
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }

            CONFUSION -> {
                hardlight(Random.Int(0x1000000) or 0x000080)
                angularSpeed = Random.Float(-20f, +20f)
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }

            DUST -> {
                hardlight(0xFFFF66)
                angle = Random.Float(360f)
                speed.polar(Random.Float(2 * 3.1415926f), Random.Float(16f, 48f))
                lifespan = 0.5f
            }

            COIN -> {
                speed.polar(-PointF.PI * Random.Float(0.3f, 0.7f), Random.Float(48f, 96f))
                acc.y = 256f
                lifespan = -speed.y / acc.y * 2
            }
        }

        left = lifespan
    }

    @SuppressLint("FloatMath")
    override fun update() {
        super.update()

        left -= Game.elapsed
        if (left <= 0) {

            kill()

        } else {

            val p = 1 - left / lifespan    // 0 -> 1

            when (type) {

                STAR, FORGE -> {
                    scale.set(1 - p)
                    am = if (p < 0.2f) p * 5f else (1 - p) * 1.25f
                }

                KIT, MASTERY -> am = 1 - p * p

                EVOKE,

                HEALING -> am = if (p < 0.5f) 1 else 2 - p * 2

                RED_LIGHT, LIGHT -> am = scale.set(if (p < 0.2f) p * 5f else (1 - p) * 1.25f).x

                DISCOVER -> {
                    am = 1 - p
                    scale.set((if (p < 0.5f) p else 1 - p) * 2)
                }

                QUESTION -> scale.set((Math.sqrt((if (p < 0.5f) p else 1 - p).toDouble()) * 3).toFloat())

                UP -> scale.set((Math.sqrt((if (p < 0.5f) p else 1 - p).toDouble()) * 2).toFloat())

                SCREAM -> {
                    am = Math.sqrt(((if (p < 0.5f) p else 1 - p) * 2f).toDouble()).toFloat()
                    scale.set(p * 7)
                }

                BONE, RATTLE -> am = if (p < 0.9f) 1 else (1 - p) * 10

                ROCK -> am = if (p < 0.2f) p * 5 else 1

                NOTE -> am = 1 - p * p

                WOOL -> scale.set(1 - p)

                CHANGE -> {
                    am = Math.sqrt(((if (p < 0.5f) p else 1 - p) * 2).toDouble()).toFloat()
                    scale.y = (1 + p) * 0.5f
                    scale.x = scale.y * Math.cos((left * 15).toDouble()).toFloat()
                }

                HEART -> {
                    scale.set(1 - p)
                    am = 1 - p * p
                }

                BUBBLE -> am = if (p < 0.2f) p * 5 else 1

                STEAM, TOXIC, PARALYSIS, CONFUSION, DUST -> {
                    am = Math.sqrt(((if (p < 0.5f) p else 1 - p) * 0.5f).toDouble()).toFloat()
                    scale.set(1 + p)
                }

                CORROSION -> {
                    hardlight(ColorMath.interpolate(0xAAAAAA, 0xFF8800, p))
                    am = Math.sqrt((if (p < 0.5f) p else 1 - p).toDouble()).toFloat()
                    scale.set(1 + p)
                }
                STENCH -> {
                    am = Math.sqrt((if (p < 0.5f) p else 1 - p).toDouble()).toFloat()
                    scale.set(1 + p)
                }

                JET -> {
                    am = (if (p < 0.5f) p else 1 - p) * 2
                    scale.set(p * 1.5f)
                }

                COIN -> {
                    scale.x = Math.cos((left * 5).toDouble()).toFloat()
                    bm = (Math.abs(scale.x) + 1) * 0.5f
                    gm = bm
                    rm = gm
                    am = if (p < 0.9f) 1 else (1 - p) * 10
                }
            }
        }
    }

    companion object {

        val HEALING = 0
        val STAR = 1
        val LIGHT = 2
        val QUESTION = 3
        val UP = 4
        val SCREAM = 5
        val BONE = 6
        val WOOL = 7
        val ROCK = 8
        val NOTE = 9
        val CHANGE = 10
        val HEART = 11
        val BUBBLE = 12
        val STEAM = 13
        val COIN = 14

        val DISCOVER = 101
        val EVOKE = 102
        val MASTERY = 103
        val KIT = 104
        val RATTLE = 105
        val JET = 106
        val TOXIC = 107
        val CORROSION = 108
        val PARALYSIS = 109
        val DUST = 110
        val STENCH = 111
        val FORGE = 112
        val CONFUSION = 113
        val RED_LIGHT = 114

        private val SIZE = 7

        private var film: TextureFilm? = null

        private val factories = SparseArray<Emitter.Factory>()

        @JvmOverloads
        fun factory(type: Int, lightMode: Boolean = false): Emitter.Factory {

            var factory: Emitter.Factory? = factories.get(type)

            if (factory == null) {
                factory = object : Emitter.Factory() {
                    override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                        val p = emitter.recycle(Speck::class.java) as Speck
                        p.reset(index, x, y, type)
                    }

                    override fun lightMode(): Boolean {
                        return lightMode
                    }
                }
                factories.put(type, factory)
            }

            return factory
        }
    }
}
