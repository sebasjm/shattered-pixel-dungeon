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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite
import com.watabou.utils.Random

class Bat : Mob() {

    init {
        spriteClass = BatSprite::class.java

        HT = 30
        HP = HT
        defenseSkill = 15
        baseSpeed = 2f

        EXP = 7
        maxLvl = 15

        flying = true

        loot = PotionOfHealing()
        lootChance = 0.1667f //by default, see rollToDropLoot()
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(5, 18)
    }

    override fun attackSkill(target: Char): Int {
        return 16
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 4)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        val reg = Math.min(damage, HT - HP)

        if (reg > 0) {
            HP += reg
            sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)
        }

        return damage
    }

    override fun rollToDropLoot() {
        lootChance *= (7f - Dungeon.LimitedDrops.BAT_HP.count) / 7f
        super.rollToDropLoot()
    }

    override fun createLoot(): Item? {
        Dungeon.LimitedDrops.BAT_HP.count++
        return super.createLoot()
    }

}
