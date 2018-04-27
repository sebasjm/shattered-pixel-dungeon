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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.sprites.BanditSprite
import com.watabou.utils.Random

class Bandit : Thief() {

//    var item: Item? = null

    init {
        spriteClass = BanditSprite::class.java

        //1 in 50 chance to be a crazy bandit, equates to overall 1/150 chance.
        lootChance = 0.333f
    }

    override fun steal(hero: Hero): Boolean {
        if (super.steal(hero)) {

            Buff.prolong<Blindness>(hero, Blindness::class.java, Random.Int(2, 5).toFloat())
            Buff.affect<Poison>(hero, Poison::class.java)!!.set(Random.Int(5, 7).toFloat())
            Buff.prolong<Cripple>(hero, Cripple::class.java, Random.Int(3, 8).toFloat())
            Dungeon.observe()

            return true
        } else {
            return false
        }
    }

    override fun die(cause: Any?) {
        super.die(cause)
        Badges.validateRare(this)
    }
}
