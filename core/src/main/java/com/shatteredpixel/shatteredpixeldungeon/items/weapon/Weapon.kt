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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Displacing
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Exhausting
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Fragile
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wayward
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Dazzling
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Eldritch
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Stunning
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Venomous
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vorpal
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random

abstract class Weapon : KindOfWeapon() {

    var ACC = 1f    // Accuracy modifier
    var DLY = 1f    // Speed modifier
    var RCH = 1    // Reach modifier (only applies to melee hits)
    var imbue = Imbue.NONE

    private var hitsToKnow = HITS_TO_KNOW

    var enchantment: Enchantment? = null

    enum class Imbue private constructor(private val damageFactor: Float, private val delayFactor: Float) {
        NONE(1.0f, 1.00f),
        LIGHT(0.7f, 0.67f),
        HEAVY(1.5f, 1.67f);

        fun damageFactor(dmg: Int): Int {
            return Math.round(dmg * damageFactor)
        }

        fun delayFactor(dly: Float): Float {
            return dly * delayFactor
        }
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {
        var damage = damage

        if (enchantment != null) {
            damage = enchantment!!.proc(this, attacker, defender, damage)
        }

        if (!levelKnown) {
            if (--hitsToKnow <= 0) {
                identify()
                GLog.i(Messages.get(Weapon::class.java, "identify"))
                Badges.validateItemLevelAquired(this)
            }
        }

        return damage
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, hitsToKnow)
        bundle.put(ENCHANTMENT, enchantment)
        bundle.put(IMBUE, imbue)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if ((hitsToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
            hitsToKnow = HITS_TO_KNOW
        }
        enchantment = bundle.get(ENCHANTMENT) as Enchantment
        imbue = bundle.getEnum(IMBUE, Imbue::class.java)
    }

    override fun accuracyFactor(owner: Char): Float {

        var encumbrance = 0

        if (owner is Hero) {
            encumbrance = STRReq() - owner.STR()
        }

        if (hasEnchant(Wayward::class.java))
            encumbrance = Math.max(3, encumbrance + 3)

        val ACC = this.ACC

        return if (encumbrance > 0) (ACC / Math.pow(1.5, encumbrance.toDouble())).toFloat() else ACC
    }

    override fun speedFactor(owner: Char): Float {

        var encumbrance = 0
        if (owner is Hero) {
            encumbrance = STRReq() - owner.STR()
        }

        var DLY = imbue.delayFactor(this.DLY)

        DLY = RingOfFuror.modifyAttackDelay(DLY, owner)

        return if (encumbrance > 0) (DLY * Math.pow(1.2, encumbrance.toDouble())).toFloat() else DLY
    }

    override fun reachFactor(owner: Char): Int {
        return if (hasEnchant(Projecting::class.java)) RCH + 1 else RCH
    }

    fun STRReq(): Int {
        return STRReq(level())
    }

    abstract fun STRReq(lvl: Int): Int

    override fun upgrade(): Item {
        return upgrade(false)
    }

    open fun upgrade(enchant: Boolean): Item {

        if (enchant && (enchantment == null || enchantment!!.curse())) {
            enchant(Enchantment.random())
        } else if (!enchant && Random.Float() > Math.pow(0.9, level().toDouble())) {
            enchant(null)
        }

        cursed = false

        return super.upgrade()
    }

    override fun name(): String {
        return if (enchantment != null && (cursedKnown || !enchantment!!.curse())) enchantment!!.name(super.name()) else super.name()
    }

    override fun random(): Item {
        //+0: 75% (3/4)
        //+1: 20% (4/20)
        //+2: 5%  (1/20)
        var n = 0
        if (Random.Int(4) == 0) {
            n++
            if (Random.Int(5) == 0) {
                n++
            }
        }
        level(n)

        //30% chance to be cursed
        //10% chance to be enchanted
        val effectRoll = Random.Float()
        if (effectRoll < 0.3f) {
            enchant(Enchantment.randomCurse())
            cursed = true
        } else if (effectRoll >= 0.9f) {
            enchant()
        }

        return this
    }

    fun enchant(ench: Enchantment?): Weapon {
        enchantment = ench
        return this
    }

    fun enchant(): Weapon {

        val oldEnchantment = if (enchantment != null) enchantment!!.javaClass else null
        var ench = Enchantment.random()
        while (ench!!.javaClass == oldEnchantment) {
            ench = Enchantment.random()
        }

        return enchant(ench)
    }

    fun hasEnchant(type: Class<out Enchantment>): Boolean {
        return enchantment != null && enchantment!!.javaClass == type
    }

    fun hasGoodEnchant(): Boolean {
        return enchantment != null && !enchantment!!.curse()
    }

    fun hasCurseEnchant(): Boolean {
        return enchantment != null && enchantment!!.curse()
    }

    override fun glowing(): ItemSprite.Glowing? {
        return if (enchantment != null && (cursedKnown || !enchantment!!.curse())) enchantment!!.glowing() else null
    }

    abstract class Enchantment : Bundlable {

        abstract fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Int

        fun name(): String {
            return if (!curse())
                name(Messages.get(this, "enchant"))
            else
                name(Messages.get(Item::class.java, "curse"))
        }

        fun name(weaponName: String): String {
            return Messages.get(this, "name", weaponName)
        }

        fun desc(): String {
            return Messages.get(this, "desc")
        }

        open fun curse(): Boolean {
            return false
        }

        override fun restoreFromBundle(bundle: Bundle) {}

        override fun storeInBundle(bundle: Bundle) {}

        abstract fun glowing(): ItemSprite.Glowing

        companion object {

            private val enchants = arrayOf<Class<*>>(Blazing::class.java, Venomous::class.java, Vorpal::class.java, Shocking::class.java, Chilling::class.java, Eldritch::class.java, Lucky::class.java, Projecting::class.java, Unstable::class.java, Dazzling::class.java, Grim::class.java, Stunning::class.java, Vampiric::class.java)
            private val chances = floatArrayOf(10f, 10f, 10f, 10f, 5f, 5f, 5f, 5f, 5f, 5f, 2f, 2f, 2f)

            private val curses = arrayOf<Class<*>>(Annoying::class.java, Displacing::class.java, Exhausting::class.java, Fragile::class.java, Sacrificial::class.java, Wayward::class.java)

            fun random(): Enchantment? {
                try {
                    return (enchants[Random.chances(chances)] as Class<Enchantment>).newInstance()
                } catch (e: Exception) {
                    ShatteredPixelDungeon.reportException(e)
                    return null
                }

            }

            fun randomCurse(): Enchantment? {
                try {
                    return (Random.oneOf(*curses) as Class<Enchantment>).newInstance()
                } catch (e: Exception) {
                    ShatteredPixelDungeon.reportException(e)
                    return null
                }

            }
        }

    }

    companion object {

        private val HITS_TO_KNOW = 20

        private val TXT_TO_STRING = "%s :%d"

        private val UNFAMILIRIARITY = "unfamiliarity"
        private val ENCHANTMENT = "enchantment"
        private val IMBUE = "imbue"
    }
}
