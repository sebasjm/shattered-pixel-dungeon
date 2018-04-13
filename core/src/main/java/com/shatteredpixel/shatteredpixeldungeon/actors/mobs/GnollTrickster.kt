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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class GnollTrickster : Gnoll() {

    private var combo = 0

    init {
        spriteClass = GnollTricksterSprite::class.java

        HT = 20
        HP = HT
        defenseSkill = 5

        EXP = 5

        state = WANDERING

        //at half quantity, see createLoot()
        loot = Generator.Category.MISSILE
        lootChance = 1f

        properties.add(Char.Property.MINIBOSS)
    }

    override fun attackSkill(target: Char): Int {
        return 16
    }

    override fun canAttack(enemy: Char?): Boolean {
        val attack = Ballistica(pos, enemy!!.pos, Ballistica.PROJECTILE)
        return !Dungeon.level!!.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        //The gnoll's attacks get more severe the more the player lets it hit them
        combo++
        val effect = Random.Int(4) + combo

        if (effect > 2) {

            if (effect >= 6 && enemy.buff<Burning>(Burning::class.java) == null) {

                if (Dungeon.level!!.flamable[enemy.pos])
                    GameScene.add(Blob.seed<Fire>(enemy.pos, 4, Fire::class.java))
                Buff.affect<Burning>(enemy, Burning::class.java)!!.reignite(enemy)

            } else
                Buff.affect<Poison>(enemy, Poison::class.java)!!.set((effect - 2).toFloat())

        }
        return damage
    }

    override fun getCloser(target: Int): Boolean {
        combo = 0 //if he's moving, he isn't attacking, reset combo.
        return if (state === HUNTING) {
            enemySeen && getFurther(target)
        } else {
            super.getCloser(target)
        }
    }

    override fun createLoot(): Item? {
        val drop = super.createLoot() as MissileWeapon
        //half quantity, rounded up
        drop.quantity((drop.quantity() + 1) / 2)
        return drop
    }

    override fun die(cause: Any) {
        super.die(cause)

        Ghost.Quest.process()
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(COMBO, combo)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        combo = bundle.getInt(COMBO)
    }

    companion object {

        private val COMBO = "combo"
    }

}
