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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotHeartSprite
import com.watabou.utils.Random

class RotHeart : Mob() {

    init {
        spriteClass = RotHeartSprite::class.java

        HT = 80
        HP = HT
        defenseSkill = 0

        EXP = 4

        state = PASSIVE

        properties.add(Char.Property.IMMOVABLE)
        properties.add(Char.Property.MINIBOSS)
    }

    override fun damage(dmg: Int, src: Any) {
        //TODO: when effect properties are done, change this to FIRE
        if (src is Burning) {
            destroy()
            sprite!!.die()
        } else {
            super.damage(dmg, src)
        }
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {
        GameScene.add(Blob.seed<ToxicGas>(pos, 20, ToxicGas::class.java)!!)

        return super.defenseProc(enemy, damage)
    }

    override fun beckon(cell: Int) {
        //do nothing
    }

    override fun getCloser(target: Int): Boolean {
        return false
    }

    override fun destroy() {
        super.destroy()
        for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (mob is RotLasher) {
                mob.die(null)
            }
        }
    }

    override fun die(cause: Any?) {
        super.die(cause)
        Dungeon.level!!.drop(Rotberry.Seed(), pos).sprite!!.drop()
    }

    override fun reset(): Boolean {
        return true
    }

    override fun damageRoll(): Int {
        return 0
    }

    override fun attackSkill(target: Char?): Int {
        return 0
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 5)
    }

    init {
        immunities.add(Paralysis::class.java)
        immunities.add(Amok::class.java)
        immunities.add(Sleep::class.java)
        immunities.add(ToxicGas::class.java)
        immunities.add(Terror::class.java)
        immunities.add(Vertigo::class.java)
    }

}
