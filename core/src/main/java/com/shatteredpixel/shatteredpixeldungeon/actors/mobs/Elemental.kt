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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElementalSprite
import com.watabou.utils.Random

open class Elemental : Mob() {

    init {
        spriteClass = ElementalSprite::class.java

        HT = 65
        HP = HT
        defenseSkill = 20

        EXP = 10
        maxLvl = 20

        flying = true

        loot = PotionOfLiquidFlame()
        lootChance = 0.1f

        properties.add(Char.Property.FIERY)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(16, 26)
    }

    override fun attackSkill(target: Char): Int {
        return 25
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 5)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        if (Random.Int(2) == 0) {
            Buff.affect<Burning>(enemy, Burning::class.java)!!.reignite(enemy)
        }

        return damage
    }

    override fun add(buff: Buff) {
        if (buff is Frost || buff is Chill) {
            if (Dungeon.level!!.water[this.pos])
                damage(Random.NormalIntRange(HT / 2, HT), buff)
            else
                damage(Random.NormalIntRange(1, HT * 2 / 3), buff)
        } else {
            super.add(buff)
        }
    }

}
