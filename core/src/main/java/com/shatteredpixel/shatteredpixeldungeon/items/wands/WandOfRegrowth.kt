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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.ColorMath
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashSet

class WandOfRegrowth : Wand() {

    //the actual affected cells
    private var affectedCells: HashSet<Int>? = null
    //the cells to trace growth particles to, for visual effects.
    private var visualCells: HashSet<Int>? = null
    private var direction = 0

    init {
        image = ItemSpriteSheet.WAND_REGROWTH

        collisionProperties = Ballistica.STOP_TERRAIN
    }

    override fun onZap(bolt: Ballistica) {

        //ignore tiles which can't have anything grow in them.
        val i = affectedCells!!.iterator()
        while (i.hasNext()) {
            val c = Dungeon.level!!.map!![i.next()]
            if (!(c == Terrain.EMPTY ||
                            c == Terrain.EMBERS ||
                            c == Terrain.EMPTY_DECO ||
                            c == Terrain.GRASS ||
                            c == Terrain.HIGH_GRASS)) {
                i.remove()
            }
        }

        val numPlants: Float
        val numDews: Float
        val numPods: Float
        val numStars: Float

        val chrgUsed = chargesPerCast()
        //numbers greater than n*100% means n guaranteed plants, e.g. 210% = 2 plants w/10% chance for 3 plants.
        numPlants = 0.2f + chrgUsed.toFloat() * chrgUsed.toFloat() * 0.020f //scales from 22% to 220%
        numDews = 0.05f + chrgUsed.toFloat() * chrgUsed.toFloat() * 0.016f //scales from 6.6% to 165%
        numPods = 0.02f + chrgUsed.toFloat() * chrgUsed.toFloat() * 0.013f //scales from 3.3% to 135%
        numStars = chrgUsed * chrgUsed * chrgUsed / 5f * 0.005f //scales from 0.1% to 100%
        placePlants(numPlants, numDews, numPods, numStars)

        for (i in affectedCells!!) {
            val c = Dungeon.level!!.map!![i]
            if (c == Terrain.EMPTY ||
                    c == Terrain.EMBERS ||
                    c == Terrain.EMPTY_DECO) {
                Level.set(i, Terrain.GRASS)
            }

            val ch = Actor.findChar(i)
            if (ch !=
                    null) {
                processSoulMark(ch, chargesPerCast())
            }

            GameScene.add(Blob.seed<Regrowth>(i, 10, Regrowth::class.java))

        }
    }

    private fun spreadRegrowth(cell: Int, strength: Float) {
        if (strength >= 0 && Dungeon.level!!.passable[cell] && !Dungeon.level!!.losBlocking[cell]) {
            affectedCells!!.add(cell)
            if (strength >= 1.5f) {
                spreadRegrowth(cell + PathFinder.CIRCLE8[left(direction)], strength - 1.5f)
                spreadRegrowth(cell + PathFinder.CIRCLE8[direction], strength - 1.5f)
                spreadRegrowth(cell + PathFinder.CIRCLE8[right(direction)], strength - 1.5f)
            } else {
                visualCells!!.add(cell)
            }
        } else if (!Dungeon.level!!.passable[cell] || Dungeon.level!!.losBlocking[cell])
            visualCells!!.add(cell)
    }

    private fun placePlants(numPlants: Float, numDews: Float, numPods: Float, numStars: Float) {
        var numPlants = numPlants
        var numDews = numDews
        var numPods = numPods
        var numStars = numStars
        val cells = affectedCells!!.iterator()
        val floor = Dungeon.level

        while (cells.hasNext() && Random.Float() <= numPlants) {
            val seed = Generator.random(Generator.Category.SEED) as Plant.Seed

            if (seed is BlandfruitBush.Seed) {
                if (Random.Int(3) - Dungeon.LimitedDrops.BLANDFRUIT_SEED.count >= 0) {
                    floor!!.plant(seed, cells.next())
                    Dungeon.LimitedDrops.BLANDFRUIT_SEED.count++
                }
            } else
                floor!!.plant(seed, cells.next())

            numPlants--
        }

        while (cells.hasNext() && Random.Float() <= numDews) {
            floor!!.plant(Dewcatcher.Seed(), cells.next())
            numDews--
        }

        while (cells.hasNext() && Random.Float() <= numPods) {
            floor!!.plant(Seedpod.Seed(), cells.next())
            numPods--
        }

        while (cells.hasNext() && Random.Float() <= numStars) {
            floor!!.plant(Starflower.Seed(), cells.next())
            numStars--
        }

    }

    private fun left(direction: Int): Int {
        return if (direction == 0) 7 else direction - 1
    }

    private fun right(direction: Int): Int {
        return if (direction == 7) 0 else direction + 1
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        //like pre-nerf vampiric enchantment, except with herbal healing buff

        val level = Math.max(0, staff.level())

        // lvl 0 - 33%
        // lvl 1 - 43%
        // lvl 2 - 50%
        val maxValue = damage * (level + 2) / (level + 6)
        val effValue = Math.min(Random.IntRange(0, maxValue), attacker.HT - attacker.HP)

        Buff.affect<Sungrass.Health>(attacker, Sungrass.Health::class.java)!!.boost(effValue)

    }

    override fun fx(bolt: Ballistica, callback: Callback) {

        affectedCells = HashSet()
        visualCells = HashSet()

        val maxDist = Math.round(1.2f + chargesPerCast() * .8f)
        val dist = Math.min(bolt.dist!!, maxDist)

        for (i in PathFinder.CIRCLE8.indices) {
            if (bolt.sourcePos!! + PathFinder.CIRCLE8[i] == bolt.path[1]) {
                direction = i
                break
            }
        }

        var strength = maxDist.toFloat()
        for (c in bolt.subPath(1, dist)) {
            strength-- //as we start at dist 1, not 0.
            if (!Dungeon.level!!.losBlocking[c]) {
                affectedCells!!.add(c)
                spreadRegrowth(c + PathFinder.CIRCLE8[left(direction)], strength - 1)
                spreadRegrowth(c + PathFinder.CIRCLE8[direction], strength - 1)
                spreadRegrowth(c + PathFinder.CIRCLE8[right(direction)], strength - 1)
            } else {
                visualCells!!.add(c)
            }
        }

        //going to call this one manually
        visualCells!!.remove(bolt.path[dist])

        for (cell in visualCells!!) {
            //this way we only get the cells at the tip, much better performance.
            (Item.curUser.sprite!!.parent!!.recycle(MagicMissile::class.java) as MagicMissile).reset(
                    MagicMissile.FOLIAGE_CONE,
                    Item.curUser.sprite,
                    cell, null
            )
        }
        MagicMissile.boltFromChar(Item.curUser.sprite!!.parent!!,
                MagicMissile.FOLIAGE_CONE,
                Item.curUser.sprite,
                bolt.path[dist / 2],
                callback)

        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    override fun initialCharges(): Int {
        return 1
    }

    override//consumes all available charges, needs at least one.
    fun chargesPerCast(): Int {
        return Math.max(1, curCharges)
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(ColorMath.random(0x004400, 0x88CC44))
        particle.am = 1f
        particle.setLifespan(1f)
        particle.setSize(1f, 1.5f)
        particle.shuffleXY(0.5f)
        val dst = Random.Float(11f)
        particle.x -= dst
        particle.y += dst
    }

    class Dewcatcher : Plant() {

        init {
            image = 12
        }

        override fun activate() {

            val nDrops = Random.NormalIntRange(3, 6)

            val candidates = ArrayList<Int>()
            for (i in PathFinder.NEIGHBOURS8) {
                if (Dungeon.level!!.passable[pos + i]) {
                    candidates.add(pos + i)
                }
            }

            var i = 0
            while (i < nDrops && !candidates.isEmpty()) {
                val c = Random.element(candidates)
                Dungeon.level!!.drop(Dewdrop(), c!!).sprite!!.drop(pos)
                candidates.remove(c)
                i++
            }

        }

        //seed is never dropped, only care about plant class
        class Seed : Plant.Seed() {
            init {
                plantClass = Dewcatcher::class.java
            }
        }
    }

    class Seedpod : Plant() {

        init {
            image = 13
        }

        override fun activate() {

            val nSeeds = Random.NormalIntRange(2, 4)

            val candidates = ArrayList<Int>()
            for (i in PathFinder.NEIGHBOURS8) {
                if (Dungeon.level!!.passable[pos + i]) {
                    candidates.add(pos + i)
                }
            }

            var i = 0
            while (i < nSeeds && !candidates.isEmpty()) {
                val c = Random.element(candidates)
                Dungeon.level!!.drop(Generator.random(Generator.Category.SEED), c!!).sprite!!.drop(pos)
                candidates.remove(c)
                i++
            }

        }

        //seed is never dropped, only care about plant class
        class Seed : Plant.Seed() {
            init {
                plantClass = Seedpod::class.java
            }
        }

    }

}
