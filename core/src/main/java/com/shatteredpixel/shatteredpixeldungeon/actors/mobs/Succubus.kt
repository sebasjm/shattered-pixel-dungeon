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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuccubusSprite
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class Succubus : Mob() {

    private var delay = 0

    init {
        spriteClass = SuccubusSprite::class.java

        HT = 80
        HP = HT
        defenseSkill = 25
        viewDistance = Light.DISTANCE

        EXP = 12
        maxLvl = 25

        loot = ScrollOfLullaby()
        lootChance = 0.05f

        properties.add(Char.Property.DEMONIC)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(22, 30)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)

        if (Random.Int(3) == 0) {
            Buff.affect<Charm>(enemy, Charm::class.java, Random.IntRange(3, 7).toFloat()).`object` = id()
            enemy.sprite!!.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5)
            Sample.INSTANCE.play(Assets.SND_CHARMS)
        }

        return damage
    }

    override fun getCloser(target: Int): Boolean {
        if (fieldOfView!![target] && Dungeon.level!!.distance(pos, target) > 2 && delay <= 0) {

            blink(target)
            spend(-1 / speed())
            return true

        } else {

            delay--
            return super.getCloser(target)

        }
    }

    private fun blink(target: Int) {

        val route = Ballistica(pos, target, Ballistica.PROJECTILE)
        var cell = route.collisionPos!!

        //can't occupy the same cell as another char, so move back one.
        if (Actor.findChar(cell) != null && cell != this.pos)
            cell = route.path[route.dist!! - 1]

        if (Dungeon.level!!.avoid[cell]) {
            val candidates = ArrayList<Int>()
            for (n in PathFinder.NEIGHBOURS8) {
                cell = route.collisionPos!! + n
                if (Dungeon.level!!.passable[cell] && Actor.findChar(cell) == null) {
                    candidates.add(cell)
                }
            }
            if (candidates.size > 0)
                cell = Random.element(candidates)!!
            else {
                delay = BLINK_DELAY
                return
            }
        }

        ScrollOfTeleportation.appear(this, cell)

        delay = BLINK_DELAY
    }

    override fun attackSkill(target: Char): Int {
        return 40
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 10)
    }

    init {
        immunities.add(Sleep::class.java)
    }

    companion object {

        private val BLINK_DELAY = 5
    }
}
