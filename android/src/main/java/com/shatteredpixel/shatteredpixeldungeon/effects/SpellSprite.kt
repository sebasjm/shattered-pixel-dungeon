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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm

import java.util.HashMap

class SpellSprite : Image(Assets.SPELL_ICONS) {

    private var target: Char? = null

    private var phase: Phase? = null
    private var duration: Float = 0.toFloat()
    private var passed: Float = 0.toFloat()

    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    init {

        if (film == null) {
            film = TextureFilm(texture!!, SIZE)
        }
    }

    fun reset(index: Int) {
        frame(film!!.get(index))
        origin.set(width / 2, height / 2)

        phase = Phase.FADE_IN

        duration = FADE_IN_TIME
        passed = 0f
    }

    override fun update() {
        super.update()

        if (target!!.sprite != null) {
            x = target!!.sprite!!.center().x - SIZE / 2
            y = target!!.sprite!!.y - SIZE
        }

        when (phase) {
            SpellSprite.Phase.FADE_IN -> {
                alpha(passed / duration)
                scale.set(passed / duration)
            }
            SpellSprite.Phase.STATIC -> {
            }
            SpellSprite.Phase.FADE_OUT -> alpha(1 - passed / duration)
        }

        passed += Game.elapsed
        if (passed > duration) {
            when (phase) {
                SpellSprite.Phase.FADE_IN -> {
                    phase = Phase.STATIC
                    duration = STATIC_TIME
                }
                SpellSprite.Phase.STATIC -> {
                    phase = Phase.FADE_OUT
                    duration = FADE_OUT_TIME
                }
                SpellSprite.Phase.FADE_OUT -> kill()
            }

            passed = 0f
        }
    }

    override fun kill() {
        super.kill()
        all.remove(target)
    }

    companion object {

        val FOOD = 0
        val MAP = 1
        val CHARGE = 2
        val MASTERY = 3
        val BERSERK = 4

        private val SIZE = 16

        private val FADE_IN_TIME = 0.2f
        private val STATIC_TIME = 0.8f
        private val FADE_OUT_TIME = 0.4f

        private var film: TextureFilm? = null

        private val all = HashMap<Char, SpellSprite>()

        fun show(ch: Char, index: Int) {

            if (!ch.sprite!!.visible) {
                return
            }

            val old = all[ch]
            old?.kill()

            val sprite = GameScene.spellSprite()
            sprite.revive()
            sprite.reset(index)
            sprite.target = ch
            all[ch] = sprite
        }
    }
}
