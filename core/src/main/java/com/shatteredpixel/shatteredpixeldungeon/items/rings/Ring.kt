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

package com.shatteredpixel.shatteredpixeldungeon.items.rings

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

open class Ring : KindofMisc() {

    protected var buff: Buff? = null

    private var gem: String? = null

    private var ticksToKnow = TICKS_TO_KNOW

    val isKnown: Boolean
        get() = handler!!.isKnown(this)

    override val isIdentified: Boolean
        get() = super.isIdentified && isKnown

    init {
        reset()
    }

    override fun reset() {
        super.reset()
        if (handler != null) {
            image = handler!!.image(this)
            gem = handler!!.label(this)
        }
    }

    override fun activate(ch: Char) {
        buff = buff()
        buff!!.attachTo(ch)
    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {

            hero!!.remove(buff!!)
            buff = null

            return true

        } else {

            return false

        }
    }

    protected fun setKnown() {
        if (!isKnown) {
            handler!!.know(this)
        }

        if (Dungeon.hero!!.isAlive) {
            Catalog.setSeen(javaClass)
        }
    }

    override fun name(): String {
        return if (isKnown) super.name() else Messages.get(Ring::class.java, gem!!)
    }

    override fun info(): String {

        var desc = if (isKnown) desc() else Messages.get(this.javaClass, "unknown_desc")

        if (cursed && isEquipped(Dungeon.hero!!)) {

            desc += "\n\n" + Messages.get(Ring::class.java, "cursed_worn")

        } else if (cursed && cursedKnown) {

            desc += "\n\n" + Messages.get(Ring::class.java, "curse_known")

        }

        return desc
    }

    override fun upgrade(): Item {
        super.upgrade()

        if (Random.Float() > Math.pow(0.8, level().toDouble())) {
            cursed = false
        }

        return this
    }

    override fun identify(): Item {
        setKnown()
        return super.identify()
    }

    override fun random(): Item {
        //+0: 66.67% (2/3)
        //+1: 26.67% (4/15)
        //+2: 6.67%  (1/15)
        var n = 0
        if (Random.Int(3) == 0) {
            n++
            if (Random.Int(5) == 0) {
                n++
            }
        }
        level(n)

        //30% chance to be cursed
        if (Random.Float() < 0.3f) {
            cursed = true
        }

        return this
    }

    override fun price(): Int {
        var price = 75
        if (cursed && cursedKnown) {
            price /= 2
        }
        if (levelKnown) {
            if (level() > 0) {
                price *= level() + 1
            } else if (level() < 0) {
                price /= 1 - level()
            }
        }
        if (price < 1) {
            price = 1
        }
        return price
    }

    protected open fun buff(): RingBuff? {
        return null
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, ticksToKnow)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        ticksToKnow = bundle.getInt(UNFAMILIRIARITY)
        if (ticksToKnow == 0) {
            ticksToKnow = TICKS_TO_KNOW
        }

        //pre-0.6.1 saves
        if (level() < 0) {
            upgrade(-level())
        }
    }

    open inner class RingBuff : Buff() {

        override fun act(): Boolean {

            if (!isIdentified && --ticksToKnow <= 0) {
                identify()
                GLog.w(Messages.get(Ring::class.java, "identify", this@Ring.toString()))
                Badges.validateItemLevelAquired(this@Ring)
            }

            spend(Actor.TICK)

            return true
        }

        fun level(): Int {
            return if (this@Ring.cursed) {
                Math.min(0, this@Ring.level() - 2)
            } else {
                this@Ring.level() + 1
            }
        }

    }

    companion object {

        private val TICKS_TO_KNOW = 200

        private val rings = arrayOf<Class<*>>(RingOfAccuracy::class.java, RingOfEvasion::class.java, RingOfElements::class.java, RingOfForce::class.java, RingOfFuror::class.java, RingOfHaste::class.java, RingOfEnergy::class.java, RingOfMight::class.java, RingOfSharpshooting::class.java, RingOfTenacity::class.java, RingOfWealth::class.java)

        private val gems = object : HashMap<String, Int>() {
            init {
                put("garnet", ItemSpriteSheet.RING_GARNET)
                put("ruby", ItemSpriteSheet.RING_RUBY)
                put("topaz", ItemSpriteSheet.RING_TOPAZ)
                put("emerald", ItemSpriteSheet.RING_EMERALD)
                put("onyx", ItemSpriteSheet.RING_ONYX)
                put("opal", ItemSpriteSheet.RING_OPAL)
                put("tourmaline", ItemSpriteSheet.RING_TOURMALINE)
                put("sapphire", ItemSpriteSheet.RING_SAPPHIRE)
                put("amethyst", ItemSpriteSheet.RING_AMETHYST)
                put("quartz", ItemSpriteSheet.RING_QUARTZ)
                put("agate", ItemSpriteSheet.RING_AGATE)
                put("diamond", ItemSpriteSheet.RING_DIAMOND)
            }
        }

        private var handler: ItemStatusHandler<Ring>? = null

        fun initGems() {
            handler = ItemStatusHandler(rings as Array<Class<out Ring>>, gems)
        }

        fun save(bundle: Bundle) {
            handler!!.save(bundle)
        }

        fun saveSelectively(bundle: Bundle, items: ArrayList<Item>) {
            handler!!.saveSelectively(bundle, items)
        }

        fun restore(bundle: Bundle) {
            handler = ItemStatusHandler(rings as Array<Class<out Ring>>, gems, bundle)
        }

        val known: HashSet<Class<out Ring>>?
            get() = handler!!.known()

        val unknown: HashSet<Class<out Ring>>
            get() = handler!!.unknown()

        fun allKnown(): Boolean {
            return handler!!.known()!!.size == rings.size - 2
        }

        private val UNFAMILIRIARITY = "unfamiliarity"

        fun getBonus(target: Char, type: Class<out RingBuff>): Int {
            var bonus = 0
            for (buff in target.buffs(type)) {
                bonus += buff.level()
            }
            return bonus
        }
    }
}
