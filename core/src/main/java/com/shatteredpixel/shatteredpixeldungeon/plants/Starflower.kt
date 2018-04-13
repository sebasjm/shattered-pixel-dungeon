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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class Starflower : Plant() {

    init {
        image = 11
    }

    override fun activate() {
        val ch = Actor.findChar(pos)

        if (ch != null) Buff.prolong<Bless>(ch, Bless::class.java, Bless.DURATION)

        if (Random.Int(5) == 0) {
            Dungeon.level!!.drop(Seed(), pos).sprite!!.drop()
        }
    }

    class Seed : Plant.Seed() {

        init {
            image = ItemSpriteSheet.SEED_STARFLOWER

            plantClass = Starflower::class.java
            alchemyClass = PotionOfExperience::class.java
        }
    }
}
