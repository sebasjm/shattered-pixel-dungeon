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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image
import com.watabou.utils.Bundle

class ToxicImbue : Buff() {

    protected var left: Float = 0.toFloat()

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)

    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
    }

    fun set(duration: Float) {
        this.left = duration
    }


    override fun act(): Boolean {
        GameScene.add(Blob.seed<ToxicGas>(target!!.pos, 50, ToxicGas::class.java)!!)

        spend(Actor.TICK)
        left -= Actor.TICK
        if (left <= 0) {
            detach()
        } else if (left < 5) {
            BuffIndicator.refreshHero()
        }

        return true
    }

    override fun icon(): Int {
        return BuffIndicator.IMMUNITY
    }

    override fun tintIcon(icon: Image) {
        FlavourBuff.greyIcon(icon, 5f, left)
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns(left))
    }

    init {
        immunities.add(ToxicGas::class.java)
        immunities.add(Poison::class.java)
    }

    companion object {

        val DURATION = 30f

        private val LEFT = "left"
    }
}
