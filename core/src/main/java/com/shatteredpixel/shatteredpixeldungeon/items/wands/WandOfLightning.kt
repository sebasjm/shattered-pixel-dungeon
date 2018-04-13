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

package com.shatteredpixel.shatteredpixeldungeon.items.wands

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Camera
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class WandOfLightning : DamageWand() {

    private val affected = ArrayList<Char>()

    internal var arcs = ArrayList<Lightning.Arc>()

    init {
        image = ItemSpriteSheet.WAND_LIGHTNING
    }

    override fun min(lvl: Int): Int {
        return 5 + lvl
    }

    override fun max(lvl: Int): Int {
        return 10 + 5 * lvl
    }

    override fun onZap(bolt: Ballistica) {

        //lightning deals less damage per-target, the more targets that are hit.
        var multipler = 0.4f + 0.6f / affected.size
        //if the main target is in water, all affected take full damage
        if (Dungeon.level!!.water[bolt.collisionPos]) multipler = 1f

        val min = 5 + level()
        val max = 10 + 5 * level()

        for (ch in affected) {
            processSoulMark(ch, chargesPerCast())
            ch.damage(Math.round(damageRoll() * multipler), this)

            if (ch === Dungeon.hero) Camera.main.shake(2f, 0.3f)
            ch.sprite!!.centerEmitter().burst(SparkParticle.FACTORY, 3)
            ch.sprite!!.flash()
        }

        if (!Item.curUser.isAlive) {
            Dungeon.fail(javaClass)
            GLog.n(Messages.get(this, "ondeath"))
        }
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        //acts like shocking enchantment
        Shocking().proc(staff, attacker, defender, damage)
    }

    private fun arc(ch: Char) {

        affected.add(ch)

        val dist: Int
        if (Dungeon.level!!.water[ch.pos] && !ch.flying)
            dist = 2
        else
            dist = 1

        PathFinder.buildDistanceMap(ch.pos, BArray.not(Dungeon.level!!.solid, null), dist)
        for (i in PathFinder.distance.indices) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                val n = Actor.findChar(i)
                if (n === Dungeon.hero && PathFinder.distance[i] > 1)
                //the hero is only zapped if they are adjacent
                    continue
                else if (n != null && !affected.contains(n)) {
                    arcs.add(Lightning.Arc(ch.sprite!!.center(), n.sprite!!.center()))
                    arc(n)
                }
            }
        }
    }

    override fun fx(bolt: Ballistica, callback: Callback) {

        affected.clear()
        arcs.clear()

        val cell = bolt.collisionPos!!

        val ch = Actor.findChar(cell)
        if (ch != null) {
            arcs.add(Lightning.Arc(Item.curUser.sprite!!.center(), ch.sprite!!.center()))
            arc(ch)
        } else {
            arcs.add(Lightning.Arc(Item.curUser.sprite!!.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos!!)))
            CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3)
        }

        //don't want to wait for the effect before processing damage.
        Item.curUser.sprite!!.parent!!.addToFront(Lightning(arcs, null))
        callback.call()
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(0xFFFFFF)
        particle.am = 0.6f
        particle.setLifespan(0.6f)
        particle.acc.set(0f, +10f)
        particle.speed.polar(-Random.Float(3.1415926f), 6f)
        particle.setSize(0f, 1.5f)
        particle.sizeJitter = 1f
        particle.shuffleXY(1f)
        val dst = Random.Float(1f)
        particle.x -= dst
        particle.y += dst
    }

}
