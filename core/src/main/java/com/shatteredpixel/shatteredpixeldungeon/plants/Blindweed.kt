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

package com.shatteredpixel.shatteredpixeldungeon.plants

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class Blindweed : Plant() {

    init {
        image = 3
    }

    override fun activate() {
        val ch = Actor.findChar(pos)

        if (ch != null) {
            val len = Random.Int(5, 10)
            Buff.prolong<Blindness>(ch, Blindness::class.java, len.toFloat())
            Buff.prolong<Cripple>(ch, Cripple::class.java, len.toFloat())
            if (ch is Mob) {
                if (ch.state === ch.HUNTING) ch.state = ch.WANDERING
                ch.beckon(Dungeon.level!!.randomDestination())
            }
        }

        if (Dungeon.level!!.heroFOV[pos]) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4)
        }
    }

    class Seed : Plant.Seed() {
        init {
            image = ItemSpriteSheet.SEED_BLINDWEED

            plantClass = Blindweed::class.java
            alchemyClass = PotionOfInvisibility::class.java
        }
    }
}
