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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite
import com.watabou.utils.Random

open class Scorpio : Mob() {

    init {
        spriteClass = ScorpioSprite::class.java

        HT = 95
        HP = HT
        defenseSkill = 24
        viewDistance = Light.DISTANCE

        EXP = 14
        maxLvl = 25

        loot = PotionOfHealing()
        lootChance = 0.2f

        properties.add(Char.Property.DEMONIC)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(26, 36)
    }

    override fun attackSkill(target: Char): Int {
        return 36
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 16)
    }

    override fun canAttack(enemy: Char?): Boolean {
        val attack = Ballistica(pos, enemy!!.pos, Ballistica.PROJECTILE)
        return !Dungeon.level!!.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        if (Random.Int(2) == 0) {
            Buff.prolong<Cripple>(enemy, Cripple::class.java, Cripple.DURATION)
        }

        return damage
    }

    override fun getCloser(target: Int): Boolean {
        return if (state === HUNTING) {
            enemySeen && getFurther(target)
        } else {
            super.getCloser(target)
        }
    }

    override fun createLoot(): Item? {
        //(9-count) / 9 chance of getting healing, otherwise mystery meat
        if (Random.Float() < (9f - Dungeon.LimitedDrops.SCORPIO_HP.count) / 9f) {
            Dungeon.LimitedDrops.SCORPIO_HP.count++
            return loot as Item
        } else {
            return MysteryMeat()
        }
    }

}
