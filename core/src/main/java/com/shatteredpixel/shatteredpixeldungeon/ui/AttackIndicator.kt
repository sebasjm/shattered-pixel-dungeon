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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.watabou.noosa.Game
import com.watabou.utils.Random

import java.util.ArrayList

class AttackIndicator : Tag(DangerIndicator.COLOR) {

    private var sprite: CharSprite? = null
    private val candidates = ArrayList<Mob>()

    private var enabled = true

    init {

        instance = this
        lastTarget = null

        setSize(24f, 24f)
        visible(false)
        enable(false)
    }

    override fun createChildren() {
        super.createChildren()
    }

    @Synchronized
    override fun layout() {
        super.layout()

        if (sprite != null) {
            sprite!!.x = x + (width - sprite!!.width()) / 2
            sprite!!.y = y + (height - sprite!!.height()) / 2
            PixelScene.align(sprite!!)
        }
    }

    @Synchronized
    override fun update() {
        super.update()

        if (!bg!!.visible) {
            enable(false)
            if (delay > 0f) delay -= Game.elapsed
            if (delay <= 0f) active = false
        } else {
            delay = 0.75f
            active = true

            if (Dungeon.hero!!.isAlive) {

                enable(Dungeon.hero!!.ready)

            } else {
                visible(false)
                enable(false)
            }
        }
    }

    @Synchronized
    private fun checkEnemies() {

        candidates.clear()
        val v = Dungeon.hero!!.visibleEnemies()
        for (i in 0 until v) {
            val mob = Dungeon.hero!!.visibleEnemy(i)
            if (Dungeon.hero!!.canAttack(mob)) {
                candidates.add(mob)
            }
        }

        if (!candidates.contains(lastTarget)) {
            if (candidates.isEmpty()) {
                lastTarget = null
            } else {
                active = true
                lastTarget = Random.element(candidates)
                updateImage()
                flash()
            }
        } else {
            if (!bg!!.visible) {
                active = true
                flash()
            }
        }

        visible(lastTarget != null)
        enable(bg!!.visible)
    }

    @Synchronized
    private fun updateImage() {

        if (sprite != null) {
            sprite!!.killAndErase()
            sprite = null
        }

        try {
            sprite = lastTarget!!.spriteClass!!.newInstance()
            active = true
            sprite!!.idle()
            sprite!!.paused = true
            add(sprite)

            sprite!!.x = x + (width - sprite!!.width()) / 2 + 1f
            sprite!!.y = y + (height - sprite!!.height()) / 2
            PixelScene.align(sprite!!)

        } catch (e: Exception) {
            ShatteredPixelDungeon.reportException(e)
        }

    }

    @Synchronized
    private fun enable(value: Boolean) {
        enabled = value
        if (sprite != null) {
            sprite!!.alpha(if (value) ENABLED else DISABLED)
        }
    }

    @Synchronized
    private fun visible(value: Boolean) {
        bg!!.visible = value
        if (sprite != null) {
            sprite!!.visible = value
        }
    }

    override fun onClick() {
        if (enabled) {
            if (Dungeon.hero!!.handle(lastTarget!!.pos)) {
                Dungeon.hero!!.next()
            }
        }
    }

    companion object {

        private val ENABLED = 1.0f
        private val DISABLED = 0.3f

        private var delay: Float = 0.toFloat()

        private var instance: AttackIndicator

        private var lastTarget: Mob? = null

        fun target(target: Char) {
            lastTarget = target as Mob
            instance.updateImage()

            TargetHealthIndicator.instance.target(target)
        }

        fun updateState() {
            instance.checkEnemies()
        }
    }
}
