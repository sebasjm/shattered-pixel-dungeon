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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.WarlockSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Callback
import com.watabou.utils.Random

class Warlock : Mob(), Callback {

    init {
        spriteClass = WarlockSprite::class.java

        HT = 70
        HP = HT
        defenseSkill = 18

        EXP = 11
        maxLvl = 21

        loot = Generator.Category.POTION
        lootChance = 0.83f

        properties.add(Char.Property.UNDEAD)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(16, 22)
    }

    override fun attackSkill(target: Char?): Int {
        return 25
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 8)
    }

    override fun canAttack(enemy: Char?): Boolean {
        return Ballistica(pos, enemy!!.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos
    }

    override fun doAttack(enemy: Char?): Boolean {

        if (Dungeon.level!!.adjacent(pos, enemy!!.pos)) {

            return super.doAttack(enemy)

        } else {

            val visible = fieldOfView!![pos] || fieldOfView!![enemy.pos]
            if (visible) {
                sprite!!.zap(enemy.pos)
            } else {
                zap()
            }

            return !visible
        }
    }

    private fun zap() {
        spend(TIME_TO_ZAP)

        if (Char.hit(this, enemy!!, true)) {
            if (enemy === Dungeon.hero!! && Random.Int(2) == 0) {
                Buff.prolong<Weakness>(enemy!!, Weakness::class.java, Weakness.DURATION)
            }

            val dmg = Random.Int(12, 18)
            enemy!!.damage(dmg, this)

            if (!enemy!!.isAlive && enemy === Dungeon.hero!!) {
                Dungeon.fail(javaClass)
                GLog.n(Messages.get(this.javaClass, "bolt_kill"))
            }
        } else {
            enemy!!.sprite!!.showStatus(CharSprite.NEUTRAL, enemy!!.defenseVerb())
        }
    }

    fun onZapComplete() {
        zap()
        next()
    }

    override fun call() {
        next()
    }

    public override fun createLoot(): Item? {
        val loot = super.createLoot()

        if (loot is PotionOfHealing) {

            //count/10 chance of not dropping potion
            if (Random.Float() < (8f - Dungeon.LimitedDrops.WARLOCK_HP.count) / 8f) {
                Dungeon.LimitedDrops.WARLOCK_HP.count++
            } else {
                return null
            }

        }

        return loot
    }

    init {
        resistances.add(Grim::class.java)
    }

    companion object {

        private val TIME_TO_ZAP = 1f
    }
}
