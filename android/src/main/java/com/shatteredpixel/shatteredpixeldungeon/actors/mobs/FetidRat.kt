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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite
import com.watabou.utils.Random

class FetidRat : Rat() {

    init {
        spriteClass = FetidRatSprite::class.java

        HT = 20
        HP = HT
        defenseSkill = 5

        EXP = 4

        state = WANDERING

        properties.add(Char.Property.MINIBOSS)
        properties.add(Char.Property.DEMONIC)
    }

    override fun attackSkill(target: Char?): Int {
        return 12
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 2)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        if (Random.Int(3) == 0) {
            Buff.affect<Ooze>(enemy, Ooze::class.java)
        }

        return damage
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {

        GameScene.add(Blob.seed<StenchGas>(pos, 20, StenchGas::class.java)!!)

        return super.defenseProc(enemy, damage)
    }

    override fun die(cause: Any?) {
        super.die(cause)

        Ghost.Quest.process()
    }

    init {
        immunities.add(StenchGas::class.java)
    }
}