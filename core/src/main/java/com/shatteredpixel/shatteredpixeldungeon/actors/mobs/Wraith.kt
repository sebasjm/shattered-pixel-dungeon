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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class Wraith : Mob() {

    private var level: Int = 0

    init {
        spriteClass = WraithSprite::class.java

        HT = 1
        HP = HT
        EXP = 0

        flying = true

        properties.add(Char.Property.UNDEAD)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getInt(LEVEL)
        adjustStats(level)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(1 + level / 2, 2 + level)
    }

    override fun attackSkill(target: Char?): Int {
        return 10 + level
    }

    fun adjustStats(level: Int) {
        this.level = level
        defenseSkill = attackSkill(null) * 5
        enemySeen = true
    }

    override fun reset(): Boolean {
        state = WANDERING
        return true
    }

    init {
        immunities.add(Grim::class.java)
        immunities.add(Terror::class.java)
    }

    companion object {

        private val SPAWN_DELAY = 2f

        private val LEVEL = "level"

        fun spawnAround(pos: Int) {
            for (n in PathFinder.NEIGHBOURS4!!) {
                val cell = pos + n
                if (Dungeon.level!!.passable[cell] && Actor.findChar(cell) == null) {
                    spawnAt(cell)
                }
            }
        }

        fun spawnAt(pos: Int): Wraith? {
            if (Dungeon.level!!.passable[pos] && Actor.findChar(pos) == null) {

                val w = Wraith()
                w.adjustStats(Dungeon.depth)
                w.pos = pos
                w.state = w.HUNTING
                GameScene.add(w, SPAWN_DELAY)

                w.sprite!!.alpha(0f)
                w.sprite!!.parent!!.add(AlphaTweener(w.sprite!!, 1f, 0.5f))

                w.sprite!!.emitter().burst(ShadowParticle.CURSE, 5)

                return w
            } else {
                return null
            }
        }
    }
}
