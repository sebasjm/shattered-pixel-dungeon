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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpinnerSprite
import com.watabou.utils.Random

class Spinner : Mob() {

    init {
        spriteClass = SpinnerSprite::class.java

        HT = 50
        HP = HT
        defenseSkill = 14

        EXP = 9
        maxLvl = 16

        loot = MysteryMeat()
        lootChance = 0.125f

        FLEEING = Fleeing()
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(10, 25)
    }

    override fun attackSkill(target: Char): Int {
        return 20
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 6)
    }

    override fun act(): Boolean {
        val result = super.act()

        if (state === FLEEING && buff<Terror>(Terror::class.java) == null &&
                enemy != null && enemySeen && enemy!!.buff<Poison>(Poison::class.java) == null) {
            state = HUNTING
        }
        return result
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        if (Random.Int(2) == 0) {
            Buff.affect<Poison>(enemy, Poison::class.java)!!.set(Random.Int(7, 9).toFloat())
            state = FLEEING
        }

        return damage
    }

    override fun move(step: Int) {
        if (state === FLEEING) {
            GameScene.add(Blob.seed<Web>(pos, Random.Int(5, 7), Web::class.java))
        }
        super.move(step)
    }

    init {
        resistances.add(Poison::class.java)
    }

    init {
        immunities.add(Web::class.java)
    }

    private inner class Fleeing : Mob.Fleeing() {
        override fun nowhereToRun() {
            if (buff<Terror>(Terror::class.java) == null) {
                state = HUNTING
            } else {
                super.nowhereToRun()
            }
        }
    }
}
