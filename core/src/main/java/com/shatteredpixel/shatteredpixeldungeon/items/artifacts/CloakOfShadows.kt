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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.utils.Bundle

import java.util.ArrayList

class CloakOfShadows : Artifact() {

    private var stealthed = false

    init {
        image = ItemSpriteSheet.ARTIFACT_CLOAK

        exp = 0
        levelCap = 10

        charge = Math.min(level() + 3, 10)
        partialCharge = 0f
        chargeCap = Math.min(level() + 3, 10)

        defaultAction = AC_STEALTH

        unique = true
        bones = false
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && charge > 1)
            actions.add(AC_STEALTH)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_STEALTH) {

            if (!stealthed) {
                if (!isEquipped(hero))
                    GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
                else if (charge <= 0)
                    GLog.i(Messages.get(this, "no_charge"))
                else {
                    stealthed = true
                    hero.spend(1f)
                    hero.busy()
                    Sample.INSTANCE.play(Assets.SND_MELD)
                    activeBuff = activeBuff()
                    activeBuff!!.attachTo(hero)
                    if (hero.sprite!!.parent != null) {
                        hero.sprite!!.parent!!.add(AlphaTweener(hero.sprite, 0.4f, 0.4f))
                    } else {
                        hero.sprite!!.alpha(0.4f)
                    }
                    hero.sprite!!.operate(hero.pos)
                }
            } else {
                stealthed = false
                activeBuff!!.detach()
                activeBuff = null
                hero.spend(1f)
                hero.sprite!!.operate(hero.pos)
            }

        }
    }

    override fun activate(ch: Char) {
        super.activate(ch)
        if (stealthed) {
            activeBuff = activeBuff()
            activeBuff!!.attachTo(ch)
        }
    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {
            stealthed = false
            return true
        } else
            return false
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return cloakRecharge()
    }

    override fun activeBuff(): Artifact.ArtifactBuff? {
        return cloakStealth()
    }

    override fun upgrade(): Item {
        chargeCap = Math.min(chargeCap + 1, 10)
        return super.upgrade()
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STEALTHED, stealthed)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stealthed = bundle.getBoolean(STEALTHED)
        // pre-0.6.2 saves
        if (bundle.contains("cooldown")) {
            exp = 0
            level(Math.ceil((level() * 0.7f).toDouble()).toInt())
            chargeCap = Math.min(3 + level(), 10)
            charge = chargeCap
        }
    }

    override fun price(): Int {
        return 0
    }

    inner class cloakRecharge : Artifact.ArtifactBuff() {
        override fun act(): Boolean {
            if (charge < chargeCap) {
                val lock = target.buff<LockedFloor>(LockedFloor::class.java)
                if (!stealthed && (lock == null || lock.regenOn())) {
                    var turnsToCharge = (50 - (chargeCap - charge)).toFloat()
                    if (level() > 7) turnsToCharge -= 10 * (level() - 7) / 3f
                    partialCharge += 1f / turnsToCharge
                }

                if (partialCharge >= 1) {
                    charge++
                    partialCharge -= 1f
                    if (charge == chargeCap) {
                        partialCharge = 0f
                    }

                }
            } else
                partialCharge = 0f

            if (cooldown > 0)
                cooldown--

            updateQuickslot()

            spend(Actor.TICK)

            return true
        }

    }

    inner class cloakStealth : Artifact.ArtifactBuff() {
        internal var turnsToCost = 0

        override fun icon(): Int {
            return BuffIndicator.INVISIBLE
        }

        override fun attachTo(target: Char): Boolean {
            if (super.attachTo(target)) {
                target.invisible++
                if (target is Hero && target.subClass == HeroSubClass.ASSASSIN) {
                    Buff.affect<Preparation>(target, Preparation::class.java)
                }
                return true
            } else {
                return false
            }
        }

        override fun act(): Boolean {
            turnsToCost--

            if (turnsToCost <= 0) {
                charge--
                if (charge < 0) {
                    charge = 0
                    detach()
                    GLog.w(Messages.get(this, "no_charge"))
                    (target as Hero).interrupt()
                } else {
                    //target hero level is 1 + 2*cloak level
                    var lvlDiffFromTarget = (target as Hero).lvl - (1 + level() * 2)
                    //plus an extra one for each level after 6
                    if (level() >= 7) {
                        lvlDiffFromTarget -= level() - 6
                    }
                    if (lvlDiffFromTarget >= 0) {
                        exp += Math.round(10f * Math.pow(1.1, lvlDiffFromTarget.toDouble())).toInt()
                    } else {
                        exp += Math.round(10f * Math.pow(0.75, (-lvlDiffFromTarget).toDouble())).toInt()
                    }

                    if (exp >= (level() + 1) * 50 && level() < levelCap) {
                        upgrade()
                        exp -= level() * 50
                        GLog.p(Messages.get(this, "levelup"))

                    }
                    turnsToCost = 5
                }
                updateQuickslot()
            }

            spend(Actor.TICK)

            return true
        }

        fun dispel() {
            updateQuickslot()
            detach()
        }

        override fun fx(on: Boolean) {
            if (on)
                target.sprite!!.add(CharSprite.State.INVISIBLE)
            else if (target.invisible == 0) target.sprite!!.remove(CharSprite.State.INVISIBLE)
        }

        override fun toString(): String {
            return Messages.get(this, "name")
        }

        override fun desc(): String {
            return Messages.get(this, "desc")
        }

        override fun detach() {
            if (target.invisible > 0)
                target.invisible--
            stealthed = false

            updateQuickslot()
            super.detach()
        }
    }

    companion object {

        val AC_STEALTH = "STEALTH"

        private val STEALTHED = "stealthed"
    }
}
