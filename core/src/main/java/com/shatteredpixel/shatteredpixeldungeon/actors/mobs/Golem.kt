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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite
import com.watabou.utils.Random

class Golem : Mob() {

    init {
        spriteClass = GolemSprite::class.java

        HT = 85
        HP = HT
        defenseSkill = 18

        EXP = 12
        maxLvl = 22

        properties.add(Char.Property.INORGANIC)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(25, 40)
    }

    override fun attackSkill(target: Char): Int {
        return 28
    }

    override fun attackDelay(): Float {
        return 1.5f
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 12)
    }

    override fun rollToDropLoot() {
        Imp.Quest.process(this)

        super.rollToDropLoot()
    }

    init {
        immunities.add(Amok::class.java)
        immunities.add(Terror::class.java)
        immunities.add(Sleep::class.java)
    }
}
