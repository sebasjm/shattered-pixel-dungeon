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

import android.telecom.Call
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.GuardSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import com.watabou.utils.Random

class Guard : Mob() {

    //they can only use their chains once
    private var chainsUsed = false

    private val CHAINSUSED = "chainsused"

    init {
        spriteClass = GuardSprite::class.java

        HT = 40
        HP = HT
        defenseSkill = 10

        EXP = 6
        maxLvl = 14

        loot = null    //see createloot.
        lootChance = 0.25f

        properties.add(Char.Property.UNDEAD)

        HUNTING = Hunting()
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(4, 12)
    }

    private fun chain(target: Int): Boolean {
        if (chainsUsed || enemy!!.properties().contains(Char.Property.IMMOVABLE))
            return false

        val chain = Ballistica(pos, target, Ballistica.PROJECTILE)

        if (chain.collisionPos != enemy!!.pos
                || chain.path.size < 2
                || Dungeon.level!!.pit[chain.path[1]])
            return false
        else {
            var newPos = -1
            for (i in chain.subPath(1, chain.dist!!)) {
                if (!Dungeon.level!!.solid[i] && Actor.findChar(i) == null) {
                    newPos = i
                    break
                }
            }

            if (newPos == -1) {
                return false
            } else {
                val newPosFinal = newPos
                this.target = newPos
                yell(Messages.get(this.javaClass, "scorpion"))
                sprite!!.parent!!.add(Chains(sprite!!.center(), enemy!!.sprite!!.center(), {
                    Actor.addDelayed(Pushing(enemy!!, enemy!!.pos, newPosFinal, {
                        enemy!!.pos = newPosFinal
                        Dungeon.level!!.press(newPosFinal, enemy, true)
                        Buff.prolong<Cripple>(enemy!!, Cripple::class.java, 4f)
                        if (enemy === Dungeon.hero!!) {
                            Dungeon.hero!!.interrupt()
                            Dungeon.observe()
                            GameScene.updateFog()
                        }
                    } as Callback), -1f)
                    next()
                } as Callback))
            }
        }
        chainsUsed = true
        return true
    }

    override fun attackSkill(target: Char?): Int {
        return 14
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 8)
    }

    override fun createLoot(): Item? {
        //first see if we drop armor, overall chance is 1/8
        if (Random.Int(2) == 0) {
            var loot: Armor?
            do {
                loot = Generator.randomArmor()
                //50% chance of re-rolling tier 4 or 5 items
            } while (loot!!.tier >= 4 && Random.Int(2) == 0)
            loot.level(0)
            return loot
            //otherwise, we may drop a health potion. overall chance is 1/8 * (6-potions dropped)/6
            //with 0 potions dropped that simplifies to 1/8
        } else {
            if (Random.Float() < (6f - Dungeon.LimitedDrops.GUARD_HP.count) / 6f) {
                Dungeon.LimitedDrops.GUARD_HP.drop()
                return PotionOfHealing()
            }
        }

        return null
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(CHAINSUSED, chainsUsed)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        chainsUsed = bundle.getBoolean(CHAINSUSED)
    }

    private inner class Hunting : Mob.Hunting() {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV

            return if (!chainsUsed
                    && enemyInFOV
                    && !isCharmedBy(enemy!!)
                    && !canAttack(enemy)
                    && Dungeon.level!!.distance(pos, enemy!!.pos) < 5
                    && Random.Int(3) == 0

                    && chain(enemy!!.pos)) {
                false
            } else {
                super.act(enemyInFOV, justAlerted)
            }

        }
    }
}
