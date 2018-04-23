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

package com.shatteredpixel.shatteredpixeldungeon.items.wands

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Random

//for wands that directly damage a target
//wands with AOE effects count here (e.g. fireblast), but wands with indrect damage do not (e.g. venom, transfusion)
abstract class DamageWand : Wand() {

    fun min(): Int {
        return min(level())
    }

    abstract fun min(lvl: Int): Int

    fun max(): Int {
        return max(level())
    }

    abstract fun max(lvl: Int): Int

    fun damageRoll(): Int {
        return Random.NormalIntRange(min(), max())
    }

    fun damageRoll(lvl: Int): Int {
        return Random.NormalIntRange(min(lvl), max(lvl))
    }

    override fun statsDesc(): String {
        return if (levelKnown)
            Messages.get(this.javaClass, "stats_desc", min(), max())
        else
            Messages.get(this.javaClass, "stats_desc", min(0), max(0))
    }
}
