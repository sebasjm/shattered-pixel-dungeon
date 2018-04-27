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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator

class Amok : FlavourBuff() {

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun icon(): Int {
        return BuffIndicator.AMOK
    }

    override fun detach() {
        super.detach()
        if (target!! is Mob)
            (target!! as Mob).aggro(null)
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns())
    }
}
