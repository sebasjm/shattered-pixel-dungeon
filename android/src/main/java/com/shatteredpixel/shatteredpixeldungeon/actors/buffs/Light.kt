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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image

class Light : FlavourBuff() {

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            if (Dungeon.level != null) {
                target.viewDistance = Math.max(Dungeon.level!!.viewDistance, DISTANCE)
                Dungeon.observe()
            }
            return true
        } else {
            return false
        }
    }

    override fun detach() {
        target!!.viewDistance = Dungeon.level!!.viewDistance
        Dungeon.observe(DISTANCE + 1)
        super.detach()
    }

    override fun icon(): Int {
        return BuffIndicator.LIGHT
    }

    override fun tintIcon(icon: Image) {
        FlavourBuff.greyIcon(icon, 20f, cooldown())
    }

    override fun fx(on: Boolean) {
        if (on)
            target!!.sprite!!.add(CharSprite.State.ILLUMINATED)
        else
            target!!.sprite!!.remove(CharSprite.State.ILLUMINATED)
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns())
    }

    companion object {

        val DURATION = 300f
        val DISTANCE = 6
    }
}
