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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

abstract class MissileWeapon : Weapon() {

    protected var sticky = true
    protected var durability = MAX_DURABILITY

    var holster: Boolean = false

    //used to reduce durability from the source weapon stack, rather than the one being thrown.
    protected var parent: MissileWeapon? = null

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        stackable = true
        levelKnown = true

        defaultAction = Item.AC_THROW
        usesTargeting = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.remove(EquipableItem.AC_EQUIP)
        return actions
    }

    override fun collect(container: Bag): Boolean {
        if (container is MagicalHolster) holster = true
        return super.collect(container)
    }

    override fun throwPos(user: Hero?, dst: Int): Int {
        return if (hasEnchant(Projecting::class.java)
                && !Dungeon.level!!.solid[dst] && Dungeon.level!!.distance(user!!.pos, dst) <= 4) {
            dst
        } else {
            super.throwPos(user, dst)
        }
    }

    override fun onThrow(cell: Int) {
        val enemy = Actor.findChar(cell)
        if (enemy == null || enemy === Item.curUser) {
            parent = null
            super.onThrow(cell)
        } else {
            if (!Item.curUser.shoot(enemy, this)) {
                rangedMiss(cell)
            } else {

                rangedHit(enemy, cell)

            }
        }
    }

    override fun random(): Item {
        if (!stackable) return this

        //2: 66.67% (2/3)
        //3: 26.67% (4/15)
        //4: 6.67%  (1/15)
        quantity = 2
        if (Random.Int(3) == 0) {
            quantity++
            if (Random.Int(5) == 0) {
                quantity++
            }
        }
        return this
    }

    override fun castDelay(user: Char, dst: Int): Float {
        var delay = speedFactor(user)

        val enemy = Actor.findChar(dst)

        if (enemy != null) {
            val mark = user.buff<SnipersMark>(SnipersMark::class.java)
            if (mark != null) {
                if (mark.`object` == enemy.id()) {
                    delay *= 0.5f
                }
            }
        }

        return delay
    }

    protected open fun rangedHit(enemy: Char, cell: Int) {
        //if this weapon was thrown from a source stack, degrade that stack.
        //unless a weapon is about to break, then break the one being thrown
        if (parent != null) {
            if (parent!!.durability <= parent!!.durabilityPerUse()) {
                durability = 0f
                parent!!.durability = MAX_DURABILITY
            } else {
                parent!!.durability -= parent!!.durabilityPerUse()
            }
            parent = null
        } else {
            durability -= durabilityPerUse()
        }
        if (durability > 0) {
            //attempt to stick the missile weapon to the enemy, just drop it if we can't.
            if (enemy.isAlive && sticky) {
                val p = Buff.affect<PinCushion>(enemy, PinCushion::class.java)
                if (p!!.target === enemy) {
                    p!!.stick(this)
                    return
                }
            }
            Dungeon.level!!.drop(this, enemy.pos).sprite!!.drop()
        }
    }

    protected open fun rangedMiss(cell: Int) {
        parent = null
        super.onThrow(cell)
    }

    protected open fun durabilityPerUse(): Float {
        var usage = MAX_DURABILITY / 10f

        if (Dungeon.hero!!.heroClass == HeroClass.HUNTRESS)
            usage /= 1.5f
        else if (holster) usage /= MagicalHolster.HOLSTER_DURABILITY_FACTOR

        usage /= RingOfSharpshooting.durabilityMultiplier(Dungeon.hero)

        return usage
    }

    override fun damageRoll(owner: Char): Int {
        var damage = imbue.damageFactor(super.damageRoll(owner))
        damage = Math.round(damage * RingOfSharpshooting.damageMultiplier(owner))

        if (owner is Hero && owner.heroClass == HeroClass.HUNTRESS) {
            val exStr = owner.STR() - STRReq()
            if (exStr > 0) {
                damage += Random.IntRange(0, exStr)
            }
        }

        return damage
    }

    override fun reset() {
        super.reset()
        durability = MAX_DURABILITY
    }

    override fun merge(other: Item): Item {
        super.merge(other)
        if (isSimilar(other)) {
            durability += (other as MissileWeapon).durability
            durability -= MAX_DURABILITY
            while (durability <= 0) {
                quantity -= 1
                durability += MAX_DURABILITY
            }
        }
        return this
    }

    override fun split(amount: Int): Item? {
        val split = super.split(amount)

        //unless the thrown weapon will break, split off a max durability item and
        //have it reduce the durability of the main stack. Cleaner to the player this way
        if (split != null) {
            val m = split as MissileWeapon
            m.durability = MAX_DURABILITY
            m.parent = this
        }

        return split
    }

    override fun doPickUp(hero: Hero): Boolean {
        parent = null
        return super.doPickUp(hero)
    }

    override fun info(): String {

        var info = desc()

        info += "\n\n" + Messages.get(MissileWeapon::class.java, "stats",
                Math.round(imbue.damageFactor(min()) * RingOfSharpshooting.damageMultiplier(Dungeon.hero)),
                Math.round(imbue.damageFactor(max()) * RingOfSharpshooting.damageMultiplier(Dungeon.hero)),
                STRReq())

        if (STRReq() > Dungeon.hero!!.STR()) {
            info += " " + Messages.get(Weapon::class.java, "too_heavy")
        } else if (Dungeon.hero!!.heroClass == HeroClass.HUNTRESS && Dungeon.hero!!.STR() > STRReq()) {
            info += " " + Messages.get(Weapon::class.java, "excess_str", Dungeon.hero!!.STR() - STRReq())
        }

        if (enchantment != null && (cursedKnown || !enchantment!!.curse())) {
            info += "\n\n" + Messages.get(Weapon::class.java, "enchanted", enchantment!!.name())
            info += " " + Messages.get(enchantment, "desc")
        }

        if (cursed && isEquipped(Dungeon.hero)) {
            info += "\n\n" + Messages.get(Weapon::class.java, "cursed_worn")
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon::class.java, "cursed")
        }

        info += "\n\n" + Messages.get(MissileWeapon::class.java, "distance")

        info += "\n\n" + Messages.get(this, "durability")

        if (durabilityPerUse() > 0) {
            info += " " + Messages.get(this, "uses_left",
                    Math.ceil((durability / durabilityPerUse()).toDouble()).toInt(),
                    Math.ceil((MAX_DURABILITY / durabilityPerUse()).toDouble()).toInt())
        }


        return info
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DURABILITY, durability)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        //compatibility with pre-0.6.3 saves
        if (bundle.contains(DURABILITY)) {
            durability = bundle.getInt(DURABILITY).toFloat()
        } else {
            durability = 100f
            //reduces quantity roughly in line with new durability system
            if (this !is TippedDart) {
                quantity = Math.ceil((quantity / 5f).toDouble()).toInt()
            }
        }
    }

    companion object {

        protected val MAX_DURABILITY = 100f

        private val DURABILITY = "durability"
    }
}
