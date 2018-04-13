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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.watabou.noosa.Game

class Enchanting(item: Item) : ItemSprite(item.image(), null) {

    private val color: Int

    private var target: Char? = null

    private var phase: Phase? = null
    private var duration: Float = 0.toFloat()
    private var passed: Float = 0.toFloat()

    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    init {
        originToCenter()

        color = item.glowing()!!.color

        phase = Phase.FADE_IN
        duration = FADE_IN_TIME
        passed = 0f
    }

    override fun update() {
        super.update()

        x = target!!.sprite!!.center().x - SIZE / 2
        y = target!!.sprite!!.y - SIZE

        when (phase) {
            Enchanting.Phase.FADE_IN -> {
                alpha(passed / duration * ALPHA)
                scale.set(passed / duration)
            }
            Enchanting.Phase.STATIC -> tint(color, passed / duration * 0.8f)
            Enchanting.Phase.FADE_OUT -> {
                alpha((1 - passed / duration) * ALPHA)
                scale.set(1 + passed / duration)
            }
        }

        if ((passed += Game.elapsed) > duration) {
            when (phase) {
                Enchanting.Phase.FADE_IN -> {
                    phase = Phase.STATIC
                    duration = STATIC_TIME
                }
                Enchanting.Phase.STATIC -> {
                    phase = Phase.FADE_OUT
                    duration = FADE_OUT_TIME
                }
                Enchanting.Phase.FADE_OUT -> kill()
            }

            passed = 0f
        }
    }

    companion object {
        private val SIZE = 16

        private val FADE_IN_TIME = 0.2f
        private val STATIC_TIME = 1.0f
        private val FADE_OUT_TIME = 0.4f

        private val ALPHA = 0.6f

        fun show(ch: Char, item: Item) {

            if (!ch.sprite!!.visible) {
                return
            }

            val sprite = Enchanting(item)
            sprite.target = ch
            ch.sprite!!.parent!!.add(sprite)
        }
    }
}