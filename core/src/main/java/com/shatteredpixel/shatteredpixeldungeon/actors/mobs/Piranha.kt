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

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PoolRoom
import com.shatteredpixel.shatteredpixeldungeon.sprites.PiranhaSprite
import com.watabou.utils.Random

class Piranha : Mob() {

    init {
        spriteClass = PiranhaSprite::class.java

        baseSpeed = 2f

        EXP = 0

        loot = MysteryMeat::class.java
        lootChance = 1f

        HUNTING = Hunting()

        properties.add(Char.Property.BLOB_IMMUNE)
    }

    init {

        HT = 10 + Dungeon.depth * 5
        HP = HT
        defenseSkill = 10 + Dungeon.depth * 2
    }

    override fun act(): Boolean {

        if (!Dungeon.level!!.water[pos]) {
            die(null)
            sprite!!.killAndErase()
            return true
        } else {
            return super.act()
        }
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(Dungeon.depth, 4 + Dungeon.depth * 2)
    }

    override fun attackSkill(target: Char?): Int {
        return 20 + Dungeon.depth * 2
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, Dungeon.depth)
    }

    override fun die(cause: Any?) {
        super.die(cause)

        Statistics.piranhasKilled++
        Badges.validatePiranhasKilled()
    }

    override fun reset(): Boolean {
        return true
    }

    override fun getCloser(target: Int): Boolean {

        if (rooted) {
            return false
        }

        val step = Dungeon.findStep(this, pos, target,
                Dungeon.level!!.water,
                fieldOfView!!)
        if (step != -1) {
            move(step)
            return true
        } else {
            return false
        }
    }

    override fun getFurther(target: Int): Boolean {
        val step = Dungeon.flee(this, pos, target,
                Dungeon.level!!.water,
                fieldOfView!!)
        if (step != -1) {
            move(step)
            return true
        } else {
            return false
        }
    }

    init {
        immunities.add(Burning::class.java)
    }

    private inner class Hunting : Mob.Hunting() {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            val result = super.act(enemyInFOV, justAlerted)
            //this causes piranha to move away when a door is closed on them in a pool room.
            if (state === WANDERING && Dungeon.level is RegularLevel) {
                val curRoom = (Dungeon.level as RegularLevel).room(pos)
                if (curRoom is PoolRoom) {
                    target = Dungeon.level!!.pointToCell(curRoom.random(1))
                }
            }
            return result
        }
    }
}
