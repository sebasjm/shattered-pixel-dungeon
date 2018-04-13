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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotLasherSprite
import com.watabou.utils.Random

class RotLasher : Mob() {

    init {
        spriteClass = RotLasherSprite::class.java

        HT = 40
        HP = HT
        defenseSkill = 0

        EXP = 1

        loot = Generator.Category.SEED
        lootChance = 1f

        WANDERING = Waiting()
        state = WANDERING

        properties.add(Char.Property.IMMOVABLE)
        properties.add(Char.Property.MINIBOSS)
    }

    override fun act(): Boolean {
        if (enemy == null || !Dungeon.level!!.adjacent(pos, enemy!!.pos)) {
            HP = Math.min(HT, HP + 3)
        }
        return super.act()
    }

    override fun damage(dmg: Int, src: Any) {
        if (src is Burning) {
            destroy()
            sprite!!.die()
        } else {
            super.damage(dmg, src)
        }
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        Buff.affect<Cripple>(enemy, Cripple::class.java, 2f)
        return super.attackProc(enemy, damage)
    }

    override fun reset(): Boolean {
        return true
    }

    override fun getCloser(target: Int): Boolean {
        return true
    }

    override fun getFurther(target: Int): Boolean {
        return true
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(8, 15)
    }

    override fun attackSkill(target: Char): Int {
        return 15
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 8)
    }

    init {
        immunities.add(ToxicGas::class.java)
    }

    private inner class Waiting : Mob.Wandering()
}
