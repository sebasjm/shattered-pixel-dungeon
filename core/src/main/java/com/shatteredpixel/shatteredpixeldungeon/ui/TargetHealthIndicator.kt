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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite

class TargetHealthIndicator : HealthBar() {

    private var target: Char? = null

    init {

        instance = this
    }

    override fun update() {
        super.update()

        if (target != null && target!!.isAlive && target!!.sprite!!.visible) {
            val sprite = target!!.sprite
            width = sprite!!.width
            x = sprite.x
            y = sprite.y - 3
            level(target)
            visible = true
        } else {
            visible = false
        }
    }

    fun target(ch: Char?) {
        if (ch != null && ch.isAlive) {
            target = ch
        } else {
            target = null
        }
    }

    fun target(): Char? {
        return target
    }

    companion object {

        var instance: TargetHealthIndicator
    }
}
