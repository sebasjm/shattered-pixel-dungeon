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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndItem
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class MagesStaff() : MeleeWeapon() {

    private var wand: Wand? = null

    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {

                if (!item.isIdentified) {
                    GLog.w(Messages.get(MagesStaff::class.java, "id_first"))
                    return
                } else if (item.cursed) {
                    GLog.w(Messages.get(MagesStaff::class.java, "cursed"))
                    return
                }

                if (wand == null) {
                    applyWand(item as Wand)
                } else {
                    val newLevel = if (item.level() >= level())
                        if (level() > 0)
                            item.level() + 1
                        else
                            item.level()
                    else
                        level()
                    GameScene.show(
                            object : WndOptions("",
                                    Messages.get(MagesStaff::class.java, "warning", newLevel),
                                    Messages.get(MagesStaff::class.java, "yes"),
                                    Messages.get(MagesStaff::class.java, "no")) {
                                override fun onSelect(index: Int) {
                                    if (index == 0) {
                                        applyWand(item as Wand)
                                    }
                                }
                            }
                    )
                }
            }
        }

        private fun applyWand(wand: Wand) {
            Sample.INSTANCE.play(Assets.SND_BURNING)
            Item.curUser!!.sprite!!.emitter().burst(ElmoParticle.FACTORY, 12)
            Item.evoke(Item.curUser!!)

            Dungeon.quickslot.clearItem(wand)

            wand.detach(Item.curUser!!.belongings.backpack)
            Badges.validateTutorial()

            GLog.p(Messages.get(MagesStaff::class.java, "imbue", wand.name()))
            imbueWand(wand, Item.curUser!!)

            updateQuickslot()
        }
    }

    private val StaffParticleFactory = object : Emitter.Factory() {
        override//reimplementing this is needed as instance creation of new staff particles must be within this class.
        fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
            var c = emitter.getFirstAvailable(StaffParticle::class.java) as StaffParticle
            if (c == null) {
                c = StaffParticle()
                emitter.add(c)
            }
            c.reset(x, y)
        }

        override//some particles need light mode, others don't
        fun lightMode(): Boolean {
            return !(wand is WandOfDisintegration
                    || wand is WandOfCorruption
                    || wand is WandOfCorrosion
                    || wand is WandOfRegrowth)
        }
    }

    init {
        image = ItemSpriteSheet.MAGES_STAFF

        tier = 1

        defaultAction = AC_ZAP
        usesTargeting = true

        unique = true
        bones = false
    }

    init {
        wand = null
    }

    override fun max(lvl: Int): Int {
        return 4 * (tier + 1) +    //8 base damage, down from 10
                lvl * (tier + 1)   //scaling unaffected
    }

    constructor(wand: Wand) : this() {
        wand.identify()
        wand.cursed = false
        this.wand = wand
        wand.maxCharges = Math.min(wand.maxCharges + 1, 10)
        wand.curCharges = wand.maxCharges
        name = Messages.get(wand.javaClass, "staff_name")
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_IMBUE)
        if (wand != null && wand!!.curCharges > 0) {
            actions.add(AC_ZAP)
        }
        return actions
    }

    override fun activate(ch: Char) {
        if (wand != null) wand!!.charge(ch, STAFF_SCALE_FACTOR)
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_IMBUE) {

            Item.curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WAND, Messages.get(this.javaClass, "prompt"))

        } else if (action == AC_ZAP) {

            if (wand == null) {
                GameScene.show(WndItem(null, this, true))
                return
            }

            wand!!.execute(hero, AC_ZAP)
        }
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {
        if (wand != null && Dungeon.hero!!.subClass == HeroSubClass.BATTLEMAGE) {
            if (wand!!.curCharges < wand!!.maxCharges) wand!!.partialCharge += 0.33f
            ScrollOfRecharging.charge(attacker as Hero)
            wand!!.onHit(this, attacker, defender, damage)
        }
        return super.proc(attacker, defender, damage)
    }

    override fun reachFactor(owner: Char): Int {
        var reach = super.reachFactor(owner)
        if (owner is Hero
                && wand is WandOfDisintegration
                && owner.subClass == HeroSubClass.BATTLEMAGE) {
            reach++
        }
        return reach
    }

    override fun collect(container: Bag): Boolean {
        if (super.collect(container)) {
            if (container.owner != null && wand != null) {
                wand!!.charge(container.owner, STAFF_SCALE_FACTOR)
            }
            return true
        } else {
            return false
        }
    }

    public override fun onDetach() {
        if (wand != null) wand!!.stopCharging()
    }

    fun imbueWand(wand: Wand, owner: Char?): Item {

        wand.cursed = false
        this.wand = null

        //syncs the level of the two items.
        var targetLevel = Math.max(this.level(), wand.level())

        //if the staff's level is being overridden by the wand, preserve 1 upgrade
        if (wand.level() >= this.level() && this.level() > 0) targetLevel++

        val staffLevelDiff = targetLevel - this.level()
        if (staffLevelDiff > 0)
            this.upgrade(staffLevelDiff)
        else if (staffLevelDiff < 0)
            this.degrade(Math.abs(staffLevelDiff))

        val wandLevelDiff = targetLevel - wand.level()
        if (wandLevelDiff > 0)
            wand.upgrade(wandLevelDiff)
        else if (wandLevelDiff < 0)
            wand.degrade(Math.abs(wandLevelDiff))

        this.wand = wand
        wand.maxCharges = Math.min(wand.maxCharges + 1, 10)
        wand.curCharges = wand.maxCharges
        wand.identify()
        if (owner != null) wand.charge(owner)

        name = Messages.get(wand.javaClass, "staff_name")

        //This is necessary to reset any particles.
        //FIXME this is gross, should implement a better way to fully reset quickslot visuals
        val slot = Dungeon.quickslot.getSlot(this)
        if (slot != -1) {
            Dungeon.quickslot.clearSlot(slot)
            updateQuickslot()
            Dungeon.quickslot.setSlot(slot, this)
            updateQuickslot()
        }

        Badges.validateItemLevelAquired(this)

        return this
    }

    fun gainCharge(amt: Float) {
        if (wand != null) {
            wand!!.gainCharge(amt)
        }
    }

    fun wandClass(): Class<out Wand>? {
        return if (wand != null) wand!!.javaClass else null
    }

    override fun upgrade(enchant: Boolean): Item {
        super.upgrade(enchant)

        if (wand != null) {
            val curCharges = wand!!.curCharges
            wand!!.upgrade()
            //gives the wand one additional charge
            wand!!.maxCharges = Math.min(wand!!.maxCharges + 1, 10)
            wand!!.curCharges = Math.min(wand!!.curCharges + 1, 10)
            updateQuickslot()
        }

        return this
    }

    override fun degrade(): Item {
        super.degrade()

        if (wand != null) {
            val curCharges = wand!!.curCharges
            wand!!.degrade()
            //gives the wand one additional charge
            wand!!.maxCharges = Math.min(wand!!.maxCharges + 1, 10)
            wand!!.curCharges = curCharges - 1
            updateQuickslot()
        }

        return this
    }

    override fun status(): String? {
        return if (wand == null)
            super.status()
        else
            wand!!.status()
    }

    override fun info(): String {
        var info = super.info()

        if (wand == null) {
            info += "\n\n" + Messages.get(this.javaClass, "no_wand")
        } else {
            info += "\n\n" + Messages.get(this.javaClass, "has_wand", Messages.get(wand!!.javaClass, "name")) + " " + wand!!.statsDesc()
        }

        return info
    }

    override fun emitter(): Emitter? {
        if (wand == null) return null
        val emitter = Emitter()
        emitter.pos(12.5f, 3f)
        emitter.fillTarget = false
        emitter.pour(StaffParticleFactory, 0.1f)
        return emitter
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(WAND, wand)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        wand = bundle.get(WAND) as Wand
        if (wand != null) {
            wand!!.maxCharges = Math.min(wand!!.maxCharges + 1, 10)
            name = Messages.get(wand!!.javaClass, "staff_name")
        }
    }

    override fun price(): Int {
        return 0
    }

    //determines particle effects to use based on wand the staff owns.
    inner class StaffParticle : PixelParticle() {

        private var minSize: Float = 0.toFloat()
        private var maxSize: Float = 0.toFloat()
        var sizeJitter = 0f

        fun reset(x: Float, y: Float) {
            revive()

            speed.set(0f)

            this.x = x
            this.y = y

            if (wand != null)
                wand!!.staffFx(this)

        }

        fun setSize(minSize: Float, maxSize: Float) {
            this.minSize = minSize
            this.maxSize = maxSize
        }


        override fun setLifespanListener(value: Float) {
            left = value
        }

//        override fun setLifespan(life: Float) {
//            left = life
//            lifespan = left
//        }

        fun shuffleXY(amt: Float) {
            x += Random.Float(-amt, amt)
            y += Random.Float(-amt, amt)
        }

        fun radiateXY(amt: Float) {
            val hypot = Math.hypot(speed.x.toDouble(), speed.y.toDouble()).toFloat()
            this.x += speed.x / hypot * amt
            this.y += speed.y / hypot * amt
        }

        override fun update() {
            super.update()
            size(minSize + left / lifespan * (maxSize - minSize) + Random.Float(sizeJitter))
        }
    }

    companion object {

        val AC_IMBUE = "IMBUE"
        val AC_ZAP = "ZAP"

        private val STAFF_SCALE_FACTOR = 0.75f

        private val WAND = "wand"
    }
}
