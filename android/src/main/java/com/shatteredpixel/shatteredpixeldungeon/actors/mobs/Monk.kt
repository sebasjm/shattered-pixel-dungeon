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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knuckles
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

open class Monk : Mob() {

    private var hitsToDisarm = 0

    init {
        spriteClass = MonkSprite::class.java

        HT = 70
        HP = HT
        defenseSkill = 30

        EXP = 11
        maxLvl = 21

        loot = Food()
        lootChance = 0.083f

        properties.add(Char.Property.UNDEAD)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(12, 25)
    }

    override fun attackSkill(target: Char?): Int {
        return 30
    }

    override fun attackDelay(): Float {
        return 0.5f
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 2)
    }

    override fun rollToDropLoot() {
        Imp.Quest.process(this)

        super.rollToDropLoot()
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)

        if (enemy === Dungeon.hero!!) {

            val hero = Dungeon.hero!!
            val weapon = hero!!.belongings.weapon

            if (weapon != null
                    && weapon !is Knuckles
                    && weapon !is Gauntlet
                    && !weapon.cursed) {
                if (hitsToDisarm == 0) hitsToDisarm = Random.NormalIntRange(4, 8)

                if (--hitsToDisarm == 0) {
                    hero.belongings.weapon = null
                    Dungeon.quickslot.convertToPlaceholder(weapon)
                    weapon.updateQuickslot()
                    Dungeon.level!!.drop(weapon, hero.pos).sprite!!.drop()
                    GLog.w(Messages.get(this.javaClass, "disarm", weapon.name()))
                }
            }
        }

        return damage
    }

    init {
        immunities.add(Amok::class.java)
        immunities.add(Terror::class.java)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DISARMHITS, hitsToDisarm)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        hitsToDisarm = bundle.getInt(DISARMHITS)
    }

    companion object {

        private val DISARMHITS = "hitsToDisarm"
    }
}
