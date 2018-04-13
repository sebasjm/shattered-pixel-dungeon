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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import com.watabou.utils.Random

import java.util.ArrayList

abstract class Wand : Item() {

    var maxCharges = initialCharges()
    var curCharges = maxCharges
    var partialCharge = 0f

    protected var charger: Charger? = null

    private var curChargeKnown = false

    protected var usagesToKnow = USAGES_TO_KNOW

    protected var collisionProperties = Ballistica.MAGIC_BOLT

    override val isIdentified: Boolean
        get() = super.isIdentified && curChargeKnown

    init {
        defaultAction = AC_ZAP
        usesTargeting = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (curCharges > 0 || !curChargeKnown) {
            actions.add(AC_ZAP)
        }

        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_ZAP) {

            Item.curUser = hero
            Item.curItem = this
            GameScene.selectCell(zapper)

        }
    }

    protected abstract fun onZap(attack: Ballistica)

    abstract fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int)

    override fun collect(container: Bag): Boolean {
        if (super.collect(container)) {
            if (container.owner != null) {
                if (container is MagicalHolster)
                    charge(container.owner, container.HOLSTER_SCALE_FACTOR)
                else
                    charge(container.owner)
            }
            return true
        } else {
            return false
        }
    }

    fun gainCharge(amt: Float) {
        partialCharge += amt
        while (partialCharge >= 1) {
            curCharges = Math.min(maxCharges, curCharges + 1)
            partialCharge--
            updateQuickslot()
        }
    }

    fun charge(owner: Char?) {
        if (charger == null) charger = Charger()
        charger!!.attachTo(owner)
    }

    fun charge(owner: Char?, chargeScaleFactor: Float) {
        charge(owner)
        charger!!.setScaleFactor(chargeScaleFactor)
    }

    protected fun processSoulMark(target: Char, chargesUsed: Int) {
        if (target !== Dungeon.hero &&
                Dungeon.hero!!.subClass == HeroSubClass.WARLOCK &&
                Random.Float() < .09f + level().toFloat() * chargesUsed.toFloat() * 0.06f) {
            SoulMark.prolong<SoulMark>(target, SoulMark::class.java, SoulMark.DURATION + level())
        }
    }

    public override fun onDetach() {
        stopCharging()
    }

    fun stopCharging() {
        if (charger != null) {
            charger!!.detach()
            charger = null
        }
    }

    override fun level(value: Int) {
        super.level(value)
        updateLevel()
    }

    override fun identify(): Item {

        curChargeKnown = true
        super.identify()

        updateQuickslot()

        return this
    }

    override fun info(): String {
        var desc = desc()

        desc += "\n\n" + statsDesc()

        if (cursed && cursedKnown)
            desc += "\n\n" + Messages.get(Wand::class.java, "cursed")

        return desc
    }

    open fun statsDesc(): String {
        return Messages.get(this, "stats_desc")
    }

    override fun status(): String? {
        return if (levelKnown) {
            (if (curChargeKnown) curCharges else "?").toString() + "/" + maxCharges
        } else {
            null
        }
    }

    override fun upgrade(): Item {

        super.upgrade()

        if (Random.Float() > Math.pow(0.8, level().toDouble())) {
            cursed = false
        }

        updateLevel()
        curCharges = Math.min(curCharges + 1, maxCharges)
        updateQuickslot()

        return this
    }

    override fun degrade(): Item {
        super.degrade()

        updateLevel()
        updateQuickslot()

        return this
    }

    fun updateLevel() {
        maxCharges = Math.min(initialCharges() + level(), 10)
        curCharges = Math.min(curCharges, maxCharges)
    }

    protected open fun initialCharges(): Int {
        return 2
    }

    protected open fun chargesPerCast(): Int {
        return 1
    }

    protected open fun fx(bolt: Ballistica, callback: Callback) {
        MagicMissile.boltFromChar(Item.curUser.sprite!!.parent!!,
                MagicMissile.MAGIC_MISSILE,
                Item.curUser.sprite,
                bolt.collisionPos!!,
                callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    open fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(0xFFFFFF)
        particle.am = 0.3f
        particle.setLifespan(1f)
        particle.speed.polar(Random.Float(PointF.PI2), 2f)
        particle.setSize(1f, 2f)
        particle.radiateXY(0.5f)
    }

    fun wandUsed() {
        usagesToKnow -= if (cursed) 1 else chargesPerCast()
        curCharges -= if (cursed) 1 else chargesPerCast()
        if (!isIdentified && usagesToKnow <= 0) {
            identify()
            GLog.w(Messages.get(Wand::class.java, "identify", name()))
        } else {
            if (Item.curUser.heroClass == HeroClass.MAGE) levelKnown = true
            updateQuickslot()
        }

        Item.curUser.spendAndNext(TIME_TO_ZAP)
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

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, usagesToKnow)
        bundle.put(CUR_CHARGES, curCharges)
        bundle.put(CUR_CHARGE_KNOWN, curChargeKnown)
        bundle.put(PARTIALCHARGE, partialCharge)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if ((usagesToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
            usagesToKnow = USAGES_TO_KNOW
        }
        curCharges = bundle.getInt(CUR_CHARGES)
        curChargeKnown = bundle.getBoolean(CUR_CHARGE_KNOWN)
        partialCharge = bundle.getFloat(PARTIALCHARGE)
    }

    inner class Charger : Buff() {

        internal var scalingFactor = NORMAL_SCALE_FACTOR

        override fun attachTo(target: Char?): Boolean {
            super.attachTo(target)

            return true
        }

        override fun act(): Boolean {
            if (curCharges < maxCharges)
                recharge()

            if (partialCharge >= 1 && curCharges < maxCharges) {
                partialCharge--
                curCharges++
                updateQuickslot()
            }

            spend(Actor.TICK)

            return true
        }

        private fun recharge() {
            var missingCharges = maxCharges - curCharges
            missingCharges += Ring.getBonus(target, RingOfEnergy.Energy::class.java)
            missingCharges = Math.max(0, missingCharges)

            val turnsToCharge = (BASE_CHARGE_DELAY + SCALING_CHARGE_ADDITION * Math.pow(scalingFactor.toDouble(), missingCharges.toDouble())).toFloat()

            val lock = target.buff<LockedFloor>(LockedFloor::class.java)
            if (lock == null || lock.regenOn())
                partialCharge += 1f / turnsToCharge

            for (bonus in target.buffs<Recharging>(Recharging::class.java)) {
                if (bonus != null && bonus.remainder() > 0f) {
                    partialCharge += CHARGE_BUFF_BONUS * bonus.remainder()
                }
            }
        }

        fun gainCharge(charge: Float) {
            partialCharge += charge
            while (partialCharge >= 1f) {
                curCharges++
                partialCharge--
            }
            curCharges = Math.min(curCharges, maxCharges)
            updateQuickslot()
        }

        private fun setScaleFactor(value: Float) {
            this.scalingFactor = value
        }

        companion object {

            private val BASE_CHARGE_DELAY = 10f
            private val SCALING_CHARGE_ADDITION = 40f
            private val NORMAL_SCALE_FACTOR = 0.875f

            private val CHARGE_BUFF_BONUS = 0.25f
        }
    }

    companion object {

        private val USAGES_TO_KNOW = 20

        val AC_ZAP = "ZAP"

        private val TIME_TO_ZAP = 1f

        private val UNFAMILIRIARITY = "unfamiliarity"
        private val CUR_CHARGES = "curCharges"
        private val CUR_CHARGE_KNOWN = "curChargeKnown"
        private val PARTIALCHARGE = "partialCharge"

        protected var zapper: CellSelector.Listener = object : CellSelector.Listener {

            override fun onSelect(target: Int?) {

                if (target != null) {

                    //FIXME this safety check shouldn't be necessary
                    //it would be better to eliminate the curItem static variable.
                    val curWand: Wand
                    if (Item.curItem is Wand) {
                        curWand = Wand.curItem as Wand?
                    } else {
                        return
                    }

                    val shot = Ballistica(Item.curUser.pos, target, curWand.collisionProperties)
                    val cell = shot.collisionPos!!

                    if (target == Item.curUser.pos || cell == Item.curUser.pos) {
                        GLog.i(Messages.get(Wand::class.java, "self_target"))
                        return
                    }

                    Item.curUser.sprite!!.zap(cell)

                    //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                    if (Actor.findChar(target) != null)
                        QuickSlotButton.target(Actor.findChar(target))
                    else
                        QuickSlotButton.target(Actor.findChar(cell))

                    if (curWand.curCharges >= (if (curWand.cursed) 1 else curWand.chargesPerCast())) {

                        Item.curUser.busy()

                        if (curWand.cursed) {
                            CursedWand.cursedZap(curWand, Item.curUser, Ballistica(Item.curUser.pos, target, Ballistica.MAGIC_BOLT))
                            if (!curWand.cursedKnown) {
                                curWand.cursedKnown = true
                                GLog.n(Messages.get(Wand::class.java, "curse_discover", curWand.name()))
                            }
                        } else {
                            curWand.fx(shot, Callback {
                                curWand.onZap(shot)
                                curWand.wandUsed()
                            })
                        }

                        Invisibility.dispel()

                    } else {

                        GLog.w(Messages.get(Wand::class.java, "fizzles"))

                    }

                }
            }

            override fun prompt(): String {
                return Messages.get(Wand::class.java, "prompt")
            }
        }
    }
}
