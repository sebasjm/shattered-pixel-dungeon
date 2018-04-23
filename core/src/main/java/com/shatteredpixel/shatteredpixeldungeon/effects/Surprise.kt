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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.Visual

class Surprise : Image(Effects.get(Effects.Type.EXCLAMATION)) {

    private var time: Float = 0.toFloat()

    init {
        origin.set(width / 2, height / 2)
    }

    fun reset(p: Int) {
        revive()

        x = p % Dungeon.level!!.width() * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2
        y = p / Dungeon.level!!.width() * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2

        time = TIME_TO_FADE
    }

    fun reset(v: Visual?) {
        revive()

        point(v!!.center(this))

        time = TIME_TO_FADE
    }

    override fun update() {
        super.update()

        time -= Game.elapsed
        if (time <= 0) {
            kill()
        } else {
            val p = time / TIME_TO_FADE
            alpha(p)
            scale.y = 1 + p / 2
        }
    }

    companion object {

        private val TIME_TO_FADE = 0.8f

        @JvmOverloads
        fun hit(ch: Char, angle: Float = 0f) {
            if (ch.sprite!!.parent != null) {
                val s = ch.sprite!!.parent!!.recycle(Surprise::class.java) as Surprise
                ch.sprite!!.parent!!.bringToFront(s)
                s.reset(ch.sprite)
                s.angle = angle
            }
        }

        @JvmOverloads
        fun hit(pos: Int, angle: Float = 0f) {
            val parent = Dungeon.hero!!.sprite!!.parent
            val s = parent!!.recycle(Surprise::class.java) as Surprise
            parent.bringToFront(s)
            s.reset(pos)
            s.angle = angle
        }
    }
}
