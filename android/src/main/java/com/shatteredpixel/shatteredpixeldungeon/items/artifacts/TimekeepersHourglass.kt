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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.watabou.noosa.Group
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class TimekeepersHourglass : Artifact() {

    //keeps track of generated sandbags.
    var sandBags = 0

    init {
        image = ItemSpriteSheet.ARTIFACT_HOURGLASS

        levelCap = 5

        charge = 5 + level()
        partialCharge = 0f
        chargeCap = 5 + level()

        defaultAction = AC_ACTIVATE
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && charge > 0 && !cursed)
            actions.add(AC_ACTIVATE)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_ACTIVATE) {

            if (!isEquipped(hero))
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
            else if (activeBuff != null) {
                if (activeBuff is timeStasis) { //do nothing
                } else {
                    activeBuff!!.detach()
                    GLog.i(Messages.get(this.javaClass, "deactivate"))
                }
            } else if (charge <= 1)
                GLog.i(Messages.get(this.javaClass, "no_charge"))
            else if (cursed)
                GLog.i(Messages.get(this.javaClass, "cursed"))
            else
                GameScene.show(
                        object : WndOptions(Messages.get(this@TimekeepersHourglass.javaClass, "name"),
                                Messages.get(this.javaClass, "prompt"),
                                Messages.get(this.javaClass, "stasis"),
                                Messages.get(this.javaClass, "freeze")) {
                            override fun onSelect(index: Int) {
                                if (index == 0) {
                                    GLog.i(Messages.get(TimekeepersHourglass::class.java, "onstasis"))
                                    GameScene.flash(0xFFFFFF)
                                    Sample.INSTANCE.play(Assets.SND_TELEPORT)

                                    activeBuff = timeStasis()
                                    activeBuff!!.attachTo(Dungeon.hero!!)
                                } else if (index == 1) {
                                    GLog.i(Messages.get(TimekeepersHourglass::class.java, "onfreeze"))
                                    GameScene.flash(0xFFFFFF)
                                    Sample.INSTANCE.play(Assets.SND_TELEPORT)

                                    activeBuff = timeFreeze()
                                    activeBuff!!.attachTo(Dungeon.hero!!)
                                    (activeBuff as timeFreeze).processTime(0f)
                                }
                            }
                        }
                )
        }
    }

    override fun activate(ch: Char) {
        super.activate(ch)
        if (activeBuff != null)
            activeBuff!!.attachTo(ch)
    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {
            if (activeBuff != null) {
                activeBuff!!.detach()
                activeBuff = null
            }
            return true
        } else
            return false
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return hourglassRecharge()
    }

    override fun upgrade(): Item {
        chargeCap += 1

        //for artifact transmutation.
        while (level() + 1 > sandBags)
            sandBags++

        return super.upgrade()
    }

    override fun desc(): String {
        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!)) {
            if (!cursed) {
                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this.javaClass, "desc_hint")

            } else
                desc += "\n\n" + Messages.get(this.javaClass, "desc_cursed")
        }
        return desc
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(SANDBAGS, sandBags)

        if (activeBuff != null)
            bundle.put(BUFF, activeBuff)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        sandBags = bundle.getInt(SANDBAGS)

        //these buffs belong to hourglass, need to handle unbundling within the hourglass class.
        if (bundle.contains(BUFF)) {
            val buffBundle = bundle.getBundle(BUFF)

            if (buffBundle.contains(PARTIALTIME))
                activeBuff = timeFreeze()
            else
                activeBuff = timeStasis()

            activeBuff!!.restoreFromBundle(buffBundle)
        }
    }

    inner class hourglassRecharge : Artifact.ArtifactBuff() {
        override fun act(): Boolean {

            val lock = target!!.buff<LockedFloor>(LockedFloor::class.java)
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (90f - (chargeCap - charge) * 3f)

                if (partialCharge >= 1) {
                    partialCharge--
                    charge++

                    if (charge == chargeCap) {
                        partialCharge = 0f
                    }
                }
            } else if (cursed && Random.Int(10) == 0)
                (target!! as Hero).spend(Actor.TICK)

            updateQuickslot()

            spend(Actor.TICK)

            return true
        }
    }

    inner class timeStasis : Artifact.ArtifactBuff() {

        override fun attachTo(target: Char): Boolean {

            if (super.attachTo(target)) {

                val usedCharge = Math.min(charge, 2)
                //buffs always act last, so the stasis buff should end a turn early.
                spend((5 * usedCharge - 1).toFloat())
                (target as Hero).spendAndNext((5 * usedCharge).toFloat())

                //shouldn't punish the player for going into stasis frequently
                val hunger = target.buff<Hunger>(Hunger::class.java)
                if (hunger != null && !hunger.isStarving)
                    hunger.satisfy((5 * usedCharge).toFloat())

                charge -= usedCharge

                target.invisible++

                updateQuickslot()

                Dungeon.observe()

                return true
            } else {
                return false
            }
        }

        override fun act(): Boolean {
            detach()
            return true
        }

        override fun detach() {
            if (target!!.invisible > 0)
                target!!.invisible--
            super.detach()
            activeBuff = null
            Dungeon.observe()
        }
    }

    inner class timeFreeze : Artifact.ArtifactBuff() {

        internal var partialTime = 1f

        internal var presses = ArrayList<Int>()

        fun processTime(time: Float) {
            partialTime += time

            while (partialTime >= 2f) {
                partialTime -= 2f
                charge--
            }

            updateQuickslot()

            if (charge < 0) {
                charge = 0
                detach()
            }

        }

        fun setDelayedPress(cell: Int) {
            if (!presses.contains(cell))
                presses.add(cell)
        }

        private fun triggerPresses() {
            for (cell in presses)
                Dungeon.level!!.press(cell, null, true)

            presses = ArrayList()
        }

        override fun attachTo(target: Char): Boolean {
            if (Dungeon.level != null)
                for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>())
                    mob.sprite!!.add(CharSprite.State.PARALYSED)
            Group.freezeEmitters = true
            return super.attachTo(target)
        }

        override fun detach() {
            for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>())
                mob.sprite!!.remove(CharSprite.State.PARALYSED)
            Group.freezeEmitters = false

            updateQuickslot()
            super.detach()
            activeBuff = null
            triggerPresses()
        }

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)

            val values = IntArray(presses.size)
            for (i in values.indices)
                values[i] = presses[i]
            bundle.put(PRESSES, values)

            bundle.put(PARTIALTIME, partialTime)
        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)

            val values = bundle.getIntArray(PRESSES)
            for (value in values!!)
                presses.add(value)

            partialTime = bundle.getFloat(PARTIALTIME)
        }

    }

    class sandBag : Item() {

        init {
            image = ItemSpriteSheet.SANDBAG
        }

        override fun doPickUp(hero: Hero): Boolean {
            val hourglass = hero.belongings.getItem<TimekeepersHourglass>(TimekeepersHourglass::class.java)
            if (hourglass != null && !hourglass.cursed) {
                hourglass.upgrade()
                Sample.INSTANCE.play(Assets.SND_DEWDROP)
                if (hourglass.level() == hourglass.levelCap)
                    GLog.p(Messages.get(this.javaClass, "maxlevel"))
                else
                    GLog.i(Messages.get(this.javaClass, "levelup"))
                hero.spendAndNext(Item.TIME_TO_PICK_UP)
                return true
            } else {
                GLog.w(Messages.get(this.javaClass, "no_hourglass"))
                return false
            }
        }

        override fun price(): Int {
            return 10
        }
    }

    companion object {

        private val PRESSES = "presses"
        private val PARTIALTIME = "partialtime"

        val AC_ACTIVATE = "ACTIVATE"


        private val SANDBAGS = "sandbags"
        private val BUFF = "buff"
    }


}
