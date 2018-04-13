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
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class Rotberry : Plant() {

    init {
        image = 7
    }

    override fun activate() {
        Dungeon.level!!.drop(Seed(), pos).sprite!!.drop()
    }

    override fun wither() {
        Dungeon.level!!.uproot(pos)

        if (Dungeon.level!!.heroFOV[pos]) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6)
        }

        //no warden benefit
    }

    class Seed : Plant.Seed() {
        init {
            image = ItemSpriteSheet.SEED_ROTBERRY

            plantClass = Rotberry::class.java
            alchemyClass = PotionOfStrength::class.java
        }
    }
}
