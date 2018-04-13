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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheepSprite
import com.watabou.utils.Random

class Sheep : NPC() {

    var lifespan: Float = 0.toFloat()

    private var initialized = false

    init {
        spriteClass = SheepSprite::class.java
    }

    override fun act(): Boolean {
        if (initialized) {
            HP = 0

            destroy()
            sprite!!.die()

        } else {
            initialized = true
            spend(lifespan + Random.Float(2f))
        }
        return true
    }

    override fun damage(dmg: Int, src: Any) {}

    override fun add(buff: Buff) {}

    override fun interact(): Boolean {
        sprite!!.showStatus(CharSprite.NEUTRAL, Messages.get(this, Random.element(LINE_KEYS)))
        Dungeon.hero!!.spendAndNext(1f)
        return false
    }

    companion object {

        private val LINE_KEYS = arrayOf("Baa!", "Baa?", "Baa.", "Baa...")
    }
}