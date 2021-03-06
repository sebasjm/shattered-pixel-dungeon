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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class Skeleton : Mob() {

    init {
        spriteClass = SkeletonSprite::class.java

        HT = 25
        HP = HT
        defenseSkill = 9

        EXP = 5
        maxLvl = 10

        loot = Generator.Category.WEAPON
        lootChance = 0.2f

        properties.add(Char.Property.UNDEAD)
        properties.add(Char.Property.INORGANIC)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(2, 10)
    }

    override fun die(cause: Any?) {

        super.die(cause)

        if (cause === Chasm::class.java) return

        var heroKilled = false
        for (i in PathFinder.NEIGHBOURS8!!.indices) {
            val ch = Actor.findChar(pos + PathFinder.NEIGHBOURS8!![i])
            if (ch != null && ch!!.isAlive) {
                val damage = Math.max(0, damageRoll() - ch!!.drRoll() / 2)
                ch!!.damage(damage, this)
                if (ch === Dungeon.hero!! && !ch!!.isAlive) {
                    heroKilled = true
                }
            }
        }

        if (Dungeon.level!!.heroFOV[pos]) {
            Sample.INSTANCE.play(Assets.SND_BONES)
        }

        if (heroKilled) {
            Dungeon.fail(javaClass)
            GLog.n(Messages.get(this.javaClass, "explo_kill"))
        }
    }

    override fun createLoot(): Item? {
        var loot: Item?
        do {
            loot = Generator.randomWeapon()
            //50% chance of re-rolling tier 4 or 5 melee weapons
        } while ((loot as MeleeWeapon).tier >= 4 && Random.Int(2) == 0)
        loot.level(0)
        return loot
    }

    override fun attackSkill(target: Char?): Int {
        return 12
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 5)
    }

}
