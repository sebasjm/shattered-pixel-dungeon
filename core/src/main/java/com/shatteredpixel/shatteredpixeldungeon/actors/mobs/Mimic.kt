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
import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class Mimic : Mob() {

    private var level: Int = 0

    var items: ArrayList<Item>? = null

    init {
        spriteClass = MimicSprite::class.java

        properties.add(Char.Property.DEMONIC)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        if (items != null) bundle.put(ITEMS, items!!)
        bundle.put(LEVEL, level)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        if (bundle.contains(ITEMS)) {
            items = ArrayList(bundle.getCollection(ITEMS) as Collection<*> as Collection<Item>)
        }
        adjustStats(bundle.getInt(LEVEL))
        super.restoreFromBundle(bundle)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(HT / 10, HT / 4)
    }

    override fun attackSkill(target: Char?): Int {
        return 9 + level
    }

    fun adjustStats(level: Int) {
        this.level = level

        HT = (1 + level) * 6
        HP = HT
        EXP = 2 + 2 * (level - 1) / 5
        defenseSkill = attackSkill(null) / 2

        enemySeen = true
    }

    override fun rollToDropLoot() {

        if (items != null) {
            for (item in items!!) {
                Dungeon.level!!.drop(item, pos).sprite!!.drop()
            }
            items = null
        }
        super.rollToDropLoot()
    }

    override fun reset(): Boolean {
        state = WANDERING
        return true
    }

    init {
        immunities.add(ScrollOfPsionicBlast::class.java)
    }

    companion object {

        private val LEVEL = "level"
        private val ITEMS = "items"

        fun spawnAt(pos: Int, items: List<Item>): Mimic? {
            if (Dungeon.level!!.pit[pos]) return null
            val ch = Actor.findChar(pos)
            if (ch != null) {
                val candidates = ArrayList<Int>()
                for (n in PathFinder.NEIGHBOURS8!!) {
                    val cell = pos + n
                    if ((Dungeon.level!!.passable[cell] || Dungeon.level!!.avoid[cell]) && Actor.findChar(cell) == null) {
                        candidates.add(cell)
                    }
                }
                if (candidates.size > 0) {
                    val newPos = Random.element(candidates)!!
                    Actor.addDelayed(Pushing(ch, ch.pos, newPos), -1f)

                    ch.pos = newPos
                    Dungeon.level!!.press(newPos, ch)

                } else {
                    return null
                }
            }

            val m = Mimic()
            m.items = ArrayList(items)
            m.adjustStats(Dungeon.depth)
            m.pos = pos
            m.state = m.HUNTING
            GameScene.add(m, 1f)

            m.sprite!!.turnTo(pos, Dungeon.hero!!.pos)

            if (Dungeon.level!!.heroFOV[m.pos]) {
                CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10)
                Sample.INSTANCE.play(Assets.SND_MIMIC)
            }

            //generate an extra reward for killing the mimic
            var reward: Item? = null
            do {
                when (Random.Int(5)) {
                    0 -> reward = Gold().random()
                    1 -> reward = Generator.randomMissile()
                    2 -> reward = Generator.randomArmor()
                    3 -> reward = Generator.randomWeapon()
                    4 -> reward = Generator.random(Generator.Category.RING)
                }
            } while (reward == null || Challenges.isItemBlocked(reward))
            m.items!!.add(reward)

            return m
        }
    }
}
