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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle

class Shadows : Invisibility() {

    protected var left: Float = 0.toFloat()

    init {
        type = Buff.buffType.SILENT
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)

    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            Sample.INSTANCE.play(Assets.SND_MELD)
            if (Dungeon.level != null)
                Dungeon.observe()
            return true
        } else {
            return false
        }
    }

    override fun detach() {
        super.detach()
        Dungeon.observe()
    }

    override fun act(): Boolean {
        if (target!!.isAlive) {

            spend(Actor.TICK * 2)

            if (--left <= 0 || Dungeon.hero!!.visibleEnemies() > 0) {
                detach()
            }

        } else {

            detach()

        }

        return true
    }

    fun prolong() {
        left = 2f
    }

    override fun icon(): Int {
        return BuffIndicator.SHADOWS
    }

    override fun tintIcon(icon: Image) {
        icon.resetColor()
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc")
    }

    companion object {

        private val LEFT = "left"
    }
}
