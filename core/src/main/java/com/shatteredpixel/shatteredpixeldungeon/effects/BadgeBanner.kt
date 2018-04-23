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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PointF

class BadgeBanner private constructor(private val index: Int) : Image(Assets.BADGES) {
    private var state: State? = null
    private var time: Float = 0.toFloat()

    private enum class State {
        FADE_IN, STATIC, FADE_OUT
    }

    init {

        if (atlas == null) {
            atlas = TextureFilm(texture!!, 16, 16)
        }

        frame(atlas!!.get(index))
        origin.set(width / 2, height / 2)

        alpha(0f)
        scale.set(2 * DEFAULT_SCALE)

        state = State.FADE_IN
        time = FADE_IN_TIME

        Sample.INSTANCE.play(Assets.SND_BADGE)
    }

    override fun update() {
        super.update()

        time -= Game.elapsed
        if (time >= 0) {

            when (state) {
                BadgeBanner.State.FADE_IN -> {
                    val p = time / FADE_IN_TIME
                    scale.set((1 + p) * DEFAULT_SCALE)
                    alpha(1 - p)
                }
                BadgeBanner.State.STATIC -> {
                }
                BadgeBanner.State.FADE_OUT -> alpha(time / FADE_OUT_TIME)
            }

        } else {

            when (state) {
                BadgeBanner.State.FADE_IN -> {
                    time = STATIC_TIME
                    state = State.STATIC
                    scale.set(DEFAULT_SCALE)
                    alpha(1f)
                    highlight(this, index)
                }
                BadgeBanner.State.STATIC -> {
                    time = FADE_OUT_TIME
                    state = State.FADE_OUT
                }
                BadgeBanner.State.FADE_OUT -> killAndErase()
            }

        }
    }

    override fun kill() {
        if (current === this) {
            current = null
        }
        super.kill()
    }

    companion object {

        private val DEFAULT_SCALE = 3f

        private val FADE_IN_TIME = 0.2f
        private val STATIC_TIME = 1f
        private val FADE_OUT_TIME = 1.0f

        private var atlas: TextureFilm? = null

        private var current: BadgeBanner? = null

        fun highlight(image: Image, index: Int) {

            val p = PointF()

            when (index) {
                0, 1, 2, 3 -> p.offset(7f, 3f)
                4, 5, 6, 7 -> p.offset(6f, 5f)
                8, 9, 10, 11 -> p.offset(6f, 3f)
                12, 13, 14, 15 -> p.offset(7f, 4f)
                16 -> p.offset(6f, 3f)
                17 -> p.offset(5f, 4f)
                18 -> p.offset(7f, 3f)
                20 -> p.offset(7f, 3f)
                21 -> p.offset(7f, 3f)
                22 -> p.offset(6f, 4f)
                23 -> p.offset(4f, 5f)
                24 -> p.offset(6f, 4f)
                25 -> p.offset(6f, 5f)
                26 -> p.offset(5f, 5f)
                27 -> p.offset(6f, 4f)
                28 -> p.offset(3f, 5f)
                29 -> p.offset(5f, 4f)
                30 -> p.offset(5f, 4f)
                31 -> p.offset(5f, 5f)
                32, 33 -> p.offset(7f, 4f)
                34 -> p.offset(6f, 4f)
                35 -> p.offset(6f, 4f)
                36 -> p.offset(6f, 5f)
                37 -> p.offset(4f, 4f)
                38 -> p.offset(5f, 5f)
                39 -> p.offset(5f, 4f)
                40, 41, 42, 43 -> p.offset(5f, 4f)
                44, 45, 46, 47 -> p.offset(5f, 5f)
                48, 49, 50, 51 -> p.offset(7f, 4f)
                52, 53, 54, 55 -> p.offset(4f, 4f)
                56 -> p.offset(3f, 7f)
                57 -> p.offset(4f, 5f)
                58 -> p.offset(6f, 4f)
                59 -> p.offset(7f, 4f)
                60, 61, 62, 63 -> p.offset(4f, 4f)
            }

            p.x *= image.scale.x
            p.y *= image.scale.y
            p.offset(
                    -image.origin.x * (image.scale.x - 1),
                    -image.origin.y * (image.scale.y - 1))
            p.offset(image.point())

            val star = Speck()
            star.reset(0, p.x, p.y, Speck.DISCOVER)
            star.camera = image.camera()
            image.parent!!.add(star)
        }

        fun show(image: Int): BadgeBanner {
            if (current != null) {
                current!!.killAndErase()
            }
            current = BadgeBanner(image)
            return current!!
        }

        fun image(index: Int): Image {
            val image = Image(Assets.BADGES)
            if (atlas == null) {
                atlas = TextureFilm(image.texture!!, 16, 16)
            }
            image.frame(atlas!!.get(index))
            return image
        }
    }
}
