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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal.WarriorShield
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle

class Berserk : Buff() {
    private var state = State.NORMAL
    private var exhaustion: Int = 0
    private var levelRecovery: Float = 0.toFloat()

    private var pastRages = 0

    private enum class State {
        NORMAL, BERSERK, EXHAUSTED, RECOVERING
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STATE, state)
        if (state == State.EXHAUSTED) bundle.put(EXHAUSTION, exhaustion)
        if (state == State.EXHAUSTED || state == State.RECOVERING) bundle.put(LEVEL_RECOVERY, levelRecovery)
        bundle.put(PAST_RAGES, pastRages)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        state = bundle.getEnum(STATE, State::class.java)
        if (state == State.EXHAUSTED) exhaustion = bundle.getInt(EXHAUSTION)
        if (state == State.EXHAUSTED || state == State.RECOVERING) levelRecovery = bundle.getFloat(LEVEL_RECOVERY)
        pastRages = bundle.getInt(PAST_RAGES)
    }

    override fun act(): Boolean {
        if (berserking()) {
            if (target!!.HP <= 0) {
                target!!.SHLD -= Math.min(target!!.SHLD, 2)
                if (target!!.SHLD == 0) {
                    target!!.die(this)
                    if (!target!!.isAlive) Dungeon.fail(this.javaClass)
                }
            } else {
                state = State.EXHAUSTED
                exhaustion = EXHAUSTION_START
                levelRecovery = LEVEL_RECOVER_START
                BuffIndicator.refreshHero()
                target!!.SHLD = 0
                pastRages++
            }
        } else {

            if (target!!.HP > targetHPMax()) {
                target!!.HP = Math.max(targetHPMax(), target!!.HP - 1)
                if (target!! is Hero) {
                    (target!! as Hero).resting = false
                    target!!.remove(MagicalSleep::class.java)
                }
            }

            if (state == State.EXHAUSTED) {
                exhaustion--
                if (exhaustion == 0) {
                    state = State.RECOVERING
                    BuffIndicator.refreshHero()
                }
            }
        }
        spend(Actor.TICK)
        return true
    }

    fun damageFactor(dmg: Int): Int {
        val bonus: Float

        if (state == State.BERSERK) {
            bonus = 2f
        } else if (state == State.EXHAUSTED) {
            bonus = 1f - Math.sqrt(exhaustion.toDouble()).toFloat() / 10f
        } else {
            val percentMissing = 1f - target!!.HP / targetHPMax().toFloat()
            bonus = 1f + 0.5f * Math.pow(percentMissing.toDouble(), 2.0).toFloat()
        }

        return Math.round(dmg * bonus)
    }

    fun targetHPMax(): Int {
        return Math.round(target!!.HT * Math.round(20 * Math.pow(0.8, pastRages.toDouble())) / 20f)
    }

    fun berserking(): Boolean {
        if (target!!.HP == 0 && state == State.NORMAL) {

            val shield = target!!.buff<WarriorShield>(WarriorShield::class.java)
            if (shield != null) {
                state = State.BERSERK
                BuffIndicator.refreshHero()
                target!!.SHLD = shield.maxShield() * 5

                SpellSprite.show(target!!, SpellSprite.BERSERK)
                Sample.INSTANCE.play(Assets.SND_CHALLENGE)
                GameScene.flash(0xFF0000)
            }

        }

        return state == State.BERSERK
    }

    fun recover(percent: Float) {
        if (levelRecovery > 0) {
            levelRecovery -= percent
            if (levelRecovery <= 0) {
                state = State.NORMAL
                BuffIndicator.refreshHero()
                levelRecovery = 0f
            }
        }
    }

    override fun icon(): Int {
        return BuffIndicator.BERSERK
    }

    override fun tintIcon(icon: Image) {
        when (state) {
            Berserk.State.NORMAL -> icon.hardlight(1f, 0.67f, 0.2f)
            Berserk.State.BERSERK -> icon.hardlight(1f, 0.1f, 0.1f)
            Berserk.State.EXHAUSTED -> icon.resetColor()
            Berserk.State.RECOVERING ->
                //icon.hardlight(0.12f, 0.20f, 0.55f);
                icon.hardlight(0.35f, 0.45f, 0.75f)
            else -> icon.hardlight(1f, 0.67f, 0.2f)
        }
    }

    override fun toString(): String {
        when (state) {
            Berserk.State.NORMAL -> return Messages.get(this.javaClass, "angered")
            Berserk.State.BERSERK -> return Messages.get(this.javaClass, "berserk")
            Berserk.State.EXHAUSTED -> return Messages.get(this.javaClass, "exhausted")
            Berserk.State.RECOVERING -> return Messages.get(this.javaClass, "recovering")
            else -> return Messages.get(this.javaClass, "angered")
        }
    }

    override fun desc(): String {
        val dispDamage = damageFactor(100).toFloat()
        var text: String
        when (state) {
            Berserk.State.NORMAL -> text = Messages.get(this.javaClass, "angered_desc", dispDamage)
            Berserk.State.BERSERK -> return Messages.get(this.javaClass, "berserk_desc")
            Berserk.State.EXHAUSTED -> text = Messages.get(this.javaClass, "exhausted_desc", exhaustion, dispDamage)
            Berserk.State.RECOVERING -> text = Messages.get(this.javaClass, "recovering_desc", levelRecovery, dispDamage)
            else -> text = Messages.get(this.javaClass, "angered_desc", dispDamage)
        }
        if (pastRages == 0) {
            text += "\n\n" + Messages.get(this.javaClass, "no_rages")
        } else {
            val dispPercent = (targetHPMax() / target!!.HT.toFloat() * 100).toInt()
            text += "\n\n" + Messages.get(this.javaClass, "past_rages", pastRages, dispPercent)
        }
        return text
    }

    companion object {

        private val EXHAUSTION_START = 30

        private val LEVEL_RECOVER_START = 2f

        private val STATE = "state"
        private val EXHAUSTION = "exhaustion"
        private val LEVEL_RECOVERY = "levelrecovery"
        private val PAST_RAGES = "pastrages"
    }
}
