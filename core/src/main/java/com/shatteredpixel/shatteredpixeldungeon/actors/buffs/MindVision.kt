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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image

class MindVision : FlavourBuff() {

    var distance = 2

    init {
        type = Buff.buffType.POSITIVE
    }

    override fun icon(): Int {
        return BuffIndicator.MIND_VISION
    }

    override fun tintIcon(icon: Image) {
        FlavourBuff.greyIcon(icon, 5f, cooldown())
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun detach() {
        super.detach()
        Dungeon.observe()
        GameScene.updateFog()
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns())
    }

    companion object {

        val DURATION = 20f
    }
}
