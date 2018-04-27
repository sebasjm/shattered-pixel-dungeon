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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

open class Artifact : KindofMisc() {

    protected var passiveBuff: Buff? = null
    protected var activeBuff: Buff? = null

    //level is used internally to track upgrades to artifacts, size/logic varies per artifact.
    //already inherited from item superclass
    //exp is used to count progress towards levels for some artifacts
    protected var exp = 0
    //levelCap is the artifact's maximum level
    protected var levelCap = 0

    //the current artifact charge
    protected var charge = 0
    //the build towards next charge, usually rolls over at 1.
    //better to keep charge as an int and use a separate float than casting.
    protected var partialCharge = 0f
    //the maximum charge, varies per artifact, not all artifacts use this.
    protected var chargeCap = 0

    //used by some artifacts to keep track of duration of effects or cooldowns to use.
    protected var cooldown = 0

    override val isUpgradable: Boolean
        get() = false

    override fun doEquip(hero: Hero): Boolean {

        if (hero.belongings.misc1 != null && hero.belongings.misc1!!.javaClass == this.javaClass || hero.belongings.misc2 != null && hero.belongings.misc2!!.javaClass == this.javaClass) {

            GLog.w(Messages.get(Artifact::class.java, "cannot_wear_two"))
            return false

        } else {

            if (super.doEquip(hero)) {

                identify()
                return true

            } else {

                return false

            }

        }

    }

    override fun activate(ch: Char) {
        passiveBuff = passiveBuff()
        passiveBuff!!.attachTo(ch)
    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {

            passiveBuff!!.detach()
            passiveBuff = null

            if (activeBuff != null) {
                activeBuff!!.detach()
                activeBuff = null
            }

            return true

        } else {

            return false

        }
    }

    override fun visiblyUpgraded(): Int {
        return if (levelKnown) Math.round(level() * 10 / levelCap.toFloat()) else 0
    }

    //transfers upgrades from another artifact, transfer level will equal the displayed level
    fun transferUpgrade(transferLvl: Int) {
        upgrade(Math.round((transferLvl * levelCap).toFloat() / 10))
    }

    override fun info(): String {
        return if (cursed && cursedKnown && !isEquipped(Dungeon.hero!!)) {

            desc() + "\n\n" + Messages.get(Artifact::class.java, "curse_known")

        } else {

            desc()

        }
    }

    override fun status(): String? {

        //if the artifact isn't IDed, or is cursed, don't display anything
        if (!isIdentified || cursed) {
            return null
        }

        //display the current cooldown
        if (cooldown != 0)
            return Messages.format("%d", cooldown)

        //display as percent
        if (chargeCap == 100)
            return Messages.format("%d%%", charge)

        //display as #/#
        if (chargeCap > 0)
            return Messages.format("%d/%d", charge, chargeCap)

        //if there's no cap -
        //- but there is charge anyway, display that charge
        return if (charge != 0) Messages.format("%d", charge) else null

        //otherwise, if there's no charge, return null.
    }

    //converts class names to be more concise and readable.
    protected fun convertName(className: String): String {
        var className = className
        //removes known redundant parts of names.
        className = className.replaceFirst("ScrollOf|PotionOf".toRegex(), "")

        //inserts a space infront of every uppercase character
        className = className.replace("(\\p{Ll})(\\p{Lu})".toRegex(), "$1 $2")

        return className
    }

    override fun random(): Item {
        //always +0

        //30% chance to be cursed
        if (Random.Float() < 0.3f) {
            cursed = true
        }
        return this
    }

    override fun price(): Int {
        var price = 100
        if (level() > 0)
            price += 20 * visiblyUpgraded()
        if (cursed && cursedKnown) {
            price /= 2
        }
        if (price < 1) {
            price = 1
        }
        return price
    }


    protected open fun passiveBuff(): ArtifactBuff? {
        return null
    }

    protected open fun activeBuff(): ArtifactBuff? {
        return null
    }

    open inner class ArtifactBuff : Buff() {

        val isCursed: Boolean
            get() = cursed

        fun itemLevel(): Int {
            return level()
        }

    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(EXP, exp)
        bundle.put(CHARGE, charge)
        bundle.put(PARTIALCHARGE, partialCharge)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        exp = bundle.getInt(EXP)
        if (chargeCap > 0)
            charge = Math.min(chargeCap, bundle.getInt(CHARGE))
        else
            charge = bundle.getInt(CHARGE)
        partialCharge = bundle.getFloat(PARTIALCHARGE)
    }

    companion object {

        private val IMAGE = "image"
        private val EXP = "exp"
        private val CHARGE = "charge"
        private val PARTIALCHARGE = "partialcharge"
    }
}
