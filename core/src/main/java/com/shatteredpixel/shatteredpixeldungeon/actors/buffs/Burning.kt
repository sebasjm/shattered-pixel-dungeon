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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class Burning : Buff(), Hero.Doom {

    private var left: Float = 0.toFloat()

    //for tracking burning of hero items
    private var burnIncrement = 0

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)
        bundle.put(BURN, burnIncrement)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
        burnIncrement = bundle.getInt(BURN)
    }

    override fun act(): Boolean {

        if (target!!.isAlive) {

            val damage = Random.NormalIntRange(1, 3 + target!!.HT / 40)
            Buff.detach(target!!, Chill::class.java)

            if (target!! is Hero) {

                val hero = target!! as Hero

                if (hero.belongings.armor != null && hero.belongings.armor!!.hasGlyph(Brimstone::class.java)) {
                    Buff.affect<Brimstone.BrimstoneShield>(target!!, Brimstone.BrimstoneShield::class.java)

                } else {

                    hero.damage(damage, this)
                    burnIncrement++

                    //at 4+ turns, there is a (turns-3)/3 chance an item burns
                    if (Random.Int(3) < burnIncrement - 3) {
                        burnIncrement = 0

                        val burnable = ArrayList<Item>()
                        //does not reach inside of containers
                        for (i in hero.belongings.backpack.items) {
                            if (i is Scroll && !(i is ScrollOfUpgrade || i is ScrollOfMagicalInfusion) || i is MysteryMeat) {
                                burnable.add(i)
                            }
                        }

                        if (!burnable.isEmpty()) {
                            val toBurn = Random.element(burnable)!!.detach(hero.belongings.backpack)
                            if (toBurn is MysteryMeat) {
                                val steak = ChargrilledMeat()
                                if (!steak.collect(hero.belongings.backpack)) {
                                    Dungeon.level!!.drop(steak, hero.pos).sprite!!.drop()
                                }
                            }
                            Heap.burnFX(hero.pos)
                            GLog.w(Messages.get(this.javaClass, "burnsup", Messages.capitalize(toBurn!!.toString())))
                        }
                    }
                }

            } else {
                target!!.damage(damage, this)
            }

            if (target!! is Thief) {

                val item = (target!! as Thief).item

                if (item is Scroll && !(item is ScrollOfUpgrade || item is ScrollOfMagicalInfusion)) {
                    target!!.sprite!!.emitter().burst(ElmoParticle.FACTORY, 6)
                    (target!! as Thief).item = null
                } else if (item is MysteryMeat) {
                    target!!.sprite!!.emitter().burst(ElmoParticle.FACTORY, 6)
                    (target!! as Thief).item = ChargrilledMeat()
                }

            }

        } else {

            val brimShield = target!!.buff<Brimstone.BrimstoneShield>(Brimstone.BrimstoneShield::class.java)
            brimShield?.startDecay()

            detach()
        }

        if (Dungeon.level!!.flamable[target!!.pos] && Blob.volumeAt(target!!.pos, Fire::class.java) == 0) {
            GameScene.add(Blob.seed<Fire>(target!!.pos, 4, Fire::class.java)!!)
        }

        spend(Actor.TICK)
        left -= Actor.TICK

        if (left <= 0 || Dungeon.level!!.water[target!!.pos] && !target!!.flying) {

            detach()
        }

        return true
    }

    fun reignite(ch: Char) {
        left = DURATION
    }

    override fun icon(): Int {
        return BuffIndicator.FIRE
    }

    override fun fx(on: Boolean) {
        if (on)
            target!!.sprite!!.add(CharSprite.State.BURNING)
        else
            target!!.sprite!!.remove(CharSprite.State.BURNING)
    }

    override fun heroMessage(): String? {
        return Messages.get(this.javaClass, "heromsg")
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    override fun desc(): String {
        return Messages.get(this.javaClass, "desc", dispTurns(left))
    }

    override fun onDeath() {

        Badges.validateDeathFromFire()

        Dungeon.fail(javaClass)
        GLog.n(Messages.get(this.javaClass, "ondeath"))
    }

    companion object {

        private val DURATION = 8f

        private val LEFT = "left"
        private val BURN = "burnIncrement"
    }
}
