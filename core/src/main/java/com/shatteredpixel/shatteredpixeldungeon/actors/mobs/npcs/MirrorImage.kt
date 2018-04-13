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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite
import com.watabou.utils.Bundle

class MirrorImage : NPC() {

    var tier: Int = 0

    private var attack: Int = 0
    private var damage: Int = 0

    init {
        spriteClass = MirrorSprite::class.java

        alignment = Char.Alignment.ALLY
        state = HUNTING
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(TIER, tier)
        bundle.put(ATTACK, attack)
        bundle.put(DAMAGE, damage)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        tier = bundle.getInt(TIER)
        attack = bundle.getInt(ATTACK)
        damage = bundle.getInt(DAMAGE)
    }

    fun duplicate(hero: Hero) {
        tier = hero.tier()
        attack = hero.attackSkill(hero)
        damage = hero.damageRoll()
    }

    override fun attackSkill(target: Char): Int {
        return attack
    }

    override fun damageRoll(): Int {
        return damage
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)

        destroy()
        sprite!!.die()

        return damage
    }

    override fun sprite(): CharSprite? {
        val s = super.sprite()
        (s as MirrorSprite).updateArmor(tier)
        return s
    }

    override fun interact(): Boolean {

        val curPos = pos

        moveSprite(pos, Dungeon.hero!!.pos)
        move(Dungeon.hero!!.pos)

        Dungeon.hero!!.sprite!!.move(Dungeon.hero!!.pos, curPos)
        Dungeon.hero!!.move(curPos)

        Dungeon.hero!!.spend(1 / Dungeon.hero!!.speed())
        Dungeon.hero!!.busy()

        return true
    }

    init {
        immunities.add(ToxicGas::class.java)
        immunities.add(CorrosiveGas::class.java)
        immunities.add(Burning::class.java)
    }

    companion object {

        private val TIER = "tier"
        private val ATTACK = "attack"
        private val DAMAGE = "damage"
    }
}