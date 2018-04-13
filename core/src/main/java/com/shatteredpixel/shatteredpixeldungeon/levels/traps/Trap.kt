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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

abstract class Trap : Bundlable {

    var name = Messages.get(this, "name")

    var color: Int = 0
    var shape: Int = 0

    var pos: Int = 0

    var visible: Boolean = false
    var active = true

    fun set(pos: Int): Trap {
        this.pos = pos
        return this
    }

    fun reveal(): Trap {
        visible = true
        GameScene.updateMap(pos)
        return this
    }

    open fun hide(): Trap {
        visible = false
        GameScene.updateMap(pos)
        return this
    }

    open fun trigger() {
        if (active) {
            if (Dungeon.level!!.heroFOV[pos]) {
                Sample.INSTANCE.play(Assets.SND_TRAP)
            }
            disarm()
            reveal()
            activate()
        }
    }

    abstract fun activate()

    protected fun disarm() {
        Dungeon.level!!.disarmTrap(pos)
        active = false
    }

    override fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
        visible = bundle.getBoolean(VISIBLE)
        if (bundle.contains(ACTIVE)) {
            active = bundle.getBoolean(ACTIVE)
        }
    }

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
        bundle.put(VISIBLE, visible)
        bundle.put(ACTIVE, active)
    }

    fun desc(): String {
        return Messages.get(this, "desc")
    }

    companion object {

        //trap colors
        val RED = 0
        val ORANGE = 1
        val YELLOW = 2
        val GREEN = 3
        val TEAL = 4
        val VIOLET = 5
        val WHITE = 6
        val GREY = 7
        val BLACK = 8

        //trap shapes
        val DOTS = 0
        val WAVES = 1
        val GRILL = 2
        val STARS = 3
        val DIAMOND = 4
        val CROSSHAIR = 5
        val LARGE_DOT = 6

        private val POS = "pos"
        private val VISIBLE = "visible"
        private val ACTIVE = "active"
    }
}
