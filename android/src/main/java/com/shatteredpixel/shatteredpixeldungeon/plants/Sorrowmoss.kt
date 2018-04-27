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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class Sorrowmoss : Plant() {

    init {
        image = 2
    }

    override fun activate() {
        val ch = Actor.findChar(pos)

        if (ch != null) {
            Buff.affect<Poison>(ch, Poison::class.java)!!.set((4 + Dungeon.depth / 2).toFloat())
        }

        if (Dungeon.level!!.heroFOV[pos]) {
            CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3)
        }
    }

    class Seed : Plant.Seed() {
        init {
            image = ItemSpriteSheet.SEED_SORROWMOSS

            plantClass = Sorrowmoss::class.java
            alchemyClass = PotionOfToxicGas::class.java
        }
    }
}
