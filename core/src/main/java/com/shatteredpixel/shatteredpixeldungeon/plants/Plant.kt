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

package com.shatteredpixel.shatteredpixeldungeon.plants

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

abstract class Plant : Bundlable {

    var plantName = Messages.get(this.javaClass, "name")

    var image: Int = 0
    var pos: Int = 0

    fun trigger() {

        val ch = Actor.findChar(pos)

        if (ch is Hero) {
            ch.interrupt()
            if (ch.subClass == HeroSubClass.WARDEN) {
                Buff.affect<Barkskin>(ch, Barkskin::class.java)!!.level(ch.HT / 3)
            }
        }

        wither()
        activate()
    }

    abstract fun activate()

    open fun wither() {
        Dungeon.level!!.uproot(pos)

        if (Dungeon.level!!.heroFOV[pos]) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6)
        }

        if (Dungeon.hero!!.subClass == HeroSubClass.WARDEN) {

            var naturalismLevel = 0
            val naturalism = Dungeon.hero!!.buff<SandalsOfNature.Naturalism>(SandalsOfNature.Naturalism::class.java)
            if (naturalism != null) {
                naturalismLevel = naturalism.itemLevel() + 1
            }

            if (Random.Int(5 - naturalismLevel / 2) == 0) {
                val seed = Generator.random(Generator.Category.SEED)

                if (seed is BlandfruitBush.Seed) {
                    if (Random.Int(3) - Dungeon.LimitedDrops.BLANDFRUIT_SEED.count >= 0) {
                        Dungeon.level!!.drop(seed, pos).sprite!!.drop()
                        Dungeon.LimitedDrops.BLANDFRUIT_SEED.count++
                    }
                } else
                    Dungeon.level!!.drop(seed, pos).sprite!!.drop()
            }
            if (Random.Int(5 - naturalismLevel) == 0) {
                Dungeon.level!!.drop(Dewdrop(), pos).sprite!!.drop()
            }
        }
    }

    override fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
    }

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
    }

    fun desc(): String {
        return Messages.get(this.javaClass, "desc")
    }

    open class Seed : Item() {

        protected var plantClass: Class<out Plant>? = null

        var alchemyClass: Class<out Item>? = null

        override val isUpgradable: Boolean
            get() = false

        override val isIdentified: Boolean
            get() = true

        init {
            stackable = true
            defaultAction = Item.AC_THROW
        }

        override fun actions(hero: Hero): ArrayList<String> {
            val actions = super.actions(hero)
            actions.add(AC_PLANT)
            return actions
        }

        override fun onThrow(cell: Int) {
            if (Dungeon.level!!.map!![cell] == Terrain.ALCHEMY
                    || Dungeon.level!!.pit[cell]
                    || Dungeon.level!!.traps.get(cell) != null
                    || Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
                super.onThrow(cell)
            } else {
                Dungeon.level!!.plant(this, cell)
            }
        }

        override fun execute(hero: Hero, action: String?) {

            super.execute(hero, action)

            if (action == AC_PLANT) {

                hero.spend(TIME_TO_PLANT)
                hero.busy()
                (detach(hero.belongings.backpack) as Seed).onThrow(hero.pos)

                hero.sprite!!.operate(hero.pos)

            }
        }

        fun couch(pos: Int, level: Level): Plant? {
            try {
                if (level.heroFOV != null && level.heroFOV[pos]) {
                    Sample.INSTANCE.play(Assets.SND_PLANT)
                }
                val plant = plantClass!!.newInstance()
                plant.pos = pos
                return plant
            } catch (e: Exception) {
                Game.reportException(e)
                return null
            }

        }

        override fun price(): Int {
            return 10 * quantity
        }

        override fun desc(): String {
            return Messages.get(plantClass, "desc")
        }

        override fun info(): String {
            return Messages.get(Seed::class.java, "info", desc())
        }

        companion object {

            val AC_PLANT = "PLANT"

            private val TIME_TO_PLANT = 1f
        }
    }

    companion object {

        private val POS = "pos"
    }
}
