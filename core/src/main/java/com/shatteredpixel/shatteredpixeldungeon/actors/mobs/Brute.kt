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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.BruteSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random

open class Brute : Mob() {

    private var enraged = false

    init {
        spriteClass = BruteSprite::class.java

        HT = 40
        HP = HT
        defenseSkill = 15

        EXP = 8
        maxLvl = 15

        loot = Gold::class.java
        lootChance = 0.5f
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        enraged = HP < HT / 4
    }

    override fun damageRoll(): Int {
        return if (enraged)
            Random.NormalIntRange(15, 45)
        else
            Random.NormalIntRange(6, 26)
    }

    override fun attackSkill(target: Char?): Int {
        return 20
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 8)
    }

    override fun damage(dmg: Int, src: Any) {
        super.damage(dmg, src)

        if (isAlive && !enraged && HP < HT / 4) {
            enraged = true
            spend(Actor.TICK)
            if (Dungeon.level!!.heroFOV[pos]) {
                sprite!!.showStatus(CharSprite.NEGATIVE, Messages.get(this.javaClass, "enraged"))
            }
        }
    }

    init {
        immunities.add(Terror::class.java)
    }
}
