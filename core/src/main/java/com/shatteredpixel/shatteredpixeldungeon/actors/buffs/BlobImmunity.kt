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

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image

class BlobImmunity : FlavourBuff() {

    override fun icon(): Int {
        return BuffIndicator.IMMUNITY
    }

    override fun tintIcon(icon: Image) {
        FlavourBuff.greyIcon(icon, 5f, cooldown())
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    init {
        //all harmful blobs
        immunities.add(ConfusionGas::class.java)
        immunities.add(CorrosiveGas::class.java)
        immunities.add(Electricity::class.java)
        immunities.add(Fire::class.java)
        immunities.add(Freezing::class.java)
        immunities.add(ParalyticGas::class.java)
        immunities.add(Regrowth::class.java)
        immunities.add(StenchGas::class.java)
        immunities.add(ToxicGas::class.java)
        immunities.add(Web::class.java)
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns())
    }

    companion object {

        val DURATION = 20f
    }
}
