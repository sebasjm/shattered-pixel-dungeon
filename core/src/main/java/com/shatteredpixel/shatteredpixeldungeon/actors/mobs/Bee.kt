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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.sprites.BeeSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.HashSet

class Bee : Mob() {

    private var level: Int = 0

    //-1 refers to a pot that has gone missing.
    private var potPos: Int = 0
    //-1 for no owner
    private var potHolder: Int = 0

    init {
        spriteClass = BeeSprite::class.java

        viewDistance = 4

        EXP = 0

        flying = true
        state = WANDERING
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
        bundle.put(POTPOS, potPos)
        bundle.put(POTHOLDER, potHolder)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        spawn(bundle.getInt(LEVEL))
        potPos = bundle.getInt(POTPOS)
        potHolder = bundle.getInt(POTHOLDER)
    }

    fun spawn(level: Int) {
        this.level = level

        HT = (2 + level) * 4
        defenseSkill = 9 + level
    }

    fun setPotInfo(potPos: Int, potHolder: Char?) {
        this.potPos = potPos
        if (potHolder == null)
            this.potHolder = -1
        else
            this.potHolder = potHolder.id()
    }

    override fun attackSkill(target: Char?): Int {
        return defenseSkill
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(HT / 10, HT / 4)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        if (enemy is Mob) {
            enemy.aggro(this)
        }
        return damage
    }

    override fun chooseEnemy(): Char? {
        //if the pot is no longer present, default to regular AI behaviour
        if (potHolder == -1 && potPos == -1)
            return super.chooseEnemy()
        else if (Actor.findById(potHolder) != null)
            return Actor.findById(potHolder) as Char
        else {

            //try to find a new enemy in these circumstances
            if (enemy == null || !enemy!!.isAlive || state === WANDERING
                    || Dungeon.level!!.distance(enemy!!.pos, potPos) > 3
                    || alignment == Char.Alignment.ALLY && enemy!!.alignment == Char.Alignment.ALLY) {

                //find all mobs near the pot
                val enemies = HashSet<Char>()
                for (mob in Dungeon.level!!.mobs) {
                    if (mob !is Bee
                            && Dungeon.level!!.distance(mob.pos, potPos) <= 3
                            && mob.alignment != Char.Alignment.NEUTRAL
                            && !(alignment == Char.Alignment.ALLY && mob.alignment == Char.Alignment.ALLY)) {
                        enemies.add(mob)
                    }
                }

                return if (!enemies.isEmpty()) {
                    Random.element(enemies)
                } else {
                    if (alignment != Char.Alignment.ALLY && Dungeon.level!!.distance(Dungeon.hero!!.pos, potPos) <= 3) {
                        Dungeon.hero!!
                    } else {
                        null
                    }
                }

            } else {
                return enemy
            }


        }//if the pot is on the ground
        //if something is holding the pot, target that
    }

    override fun getCloser(target: Int): Boolean {
        var target = target
        if (enemy != null && Actor.findById(potHolder) === enemy) {
            target = enemy!!.pos
        } else if (potPos != -1 && (state === WANDERING || Dungeon.level!!.distance(target, potPos) > 3)) {
            target = potPos
            this.target = target
        }
        return super.getCloser(target)
    }

    init {
        immunities.add(Poison::class.java)
        immunities.add(Amok::class.java)
    }

    companion object {

        private val LEVEL = "level"
        private val POTPOS = "potpos"
        private val POTHOLDER = "potholder"
    }
}