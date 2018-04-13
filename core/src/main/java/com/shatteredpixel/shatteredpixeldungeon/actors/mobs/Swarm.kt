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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class Swarm : Mob() {

    internal var generation = 0

    init {
        spriteClass = SwarmSprite::class.java

        HT = 50
        HP = HT
        defenseSkill = 5

        EXP = 3
        maxLvl = 9

        flying = true

        loot = PotionOfHealing()
        lootChance = 0.1667f //by default, see rollToDropLoot()
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(GENERATION, generation)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        generation = bundle.getInt(GENERATION)
        if (generation > 0) EXP = 0
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(1, 4)
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {

        if (HP >= damage + 2) {
            val candidates = ArrayList<Int>()
            val solid = Dungeon.level!!.solid

            val neighbours = intArrayOf(pos + 1, pos - 1, pos + Dungeon.level!!.width(), pos - Dungeon.level!!.width())
            for (n in neighbours) {
                if (!solid[n] && Actor.findChar(n) == null) {
                    candidates.add(n)
                }
            }

            if (candidates.size > 0) {

                val clone = split()
                clone.HP = (HP - damage) / 2
                clone.pos = Random.element(candidates)!!
                clone.state = clone.HUNTING

                if (Dungeon.level!!.map!![clone.pos] == Terrain.DOOR) {
                    Door.enter(clone.pos)
                }

                GameScene.add(clone, SPLIT_DELAY)
                Actor.addDelayed(Pushing(clone, pos, clone.pos), -1f)

                HP -= clone.HP
            }
        }

        return super.defenseProc(enemy, damage)
    }

    override fun attackSkill(target: Char): Int {
        return 10
    }

    private fun split(): Swarm {
        val clone = Swarm()
        clone.generation = generation + 1
        clone.EXP = 0
        if (buff<Burning>(Burning::class.java) != null) {
            Buff.affect<Burning>(clone, Burning::class.java)!!.reignite(clone)
        }
        if (buff<Poison>(Poison::class.java) != null) {
            Buff.affect<Poison>(clone, Poison::class.java)!!.set(2f)
        }
        if (buff<Corruption>(Corruption::class.java) != null) {
            Buff.affect<Corruption>(clone, Corruption::class.java)
        }
        return clone
    }

    override fun rollToDropLoot() {
        lootChance = 1f / (6 * (generation + 1))
        lootChance *= (5f - Dungeon.LimitedDrops.SWARM_HP.count) / 5f
        super.rollToDropLoot()
    }

    override fun createLoot(): Item? {
        Dungeon.LimitedDrops.SWARM_HP.count++
        return super.createLoot()
    }

    companion object {

        private val SPLIT_DELAY = 1f

        private val GENERATION = "generation"
    }
}
