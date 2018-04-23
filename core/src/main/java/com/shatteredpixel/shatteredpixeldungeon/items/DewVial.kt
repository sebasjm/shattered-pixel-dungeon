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

package com.shatteredpixel.shatteredpixeldungeon.items

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.GameMath

import java.util.ArrayList

class DewVial : Item() {

    private var volume = 0

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    val isFull: Boolean
        get() = volume >= MAX_VOLUME

    init {
        image = ItemSpriteSheet.VIAL

        defaultAction = AC_DRINK

        unique = true
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(VOLUME, volume)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        volume = bundle.getInt(VOLUME)
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (volume > 0) {
            actions.add(AC_DRINK)
        }
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_DRINK) {

            if (volume > 0) {

                //20 drops for a full heal normally, 15 for the warden
                val dropHealPercent = if (hero.subClass == HeroSubClass.WARDEN) 0.0667f else 0.05f
                val missingHealthPercent = 1f - hero.HP / hero.HT.toFloat()

                //trimming off 0.01 drops helps with floating point errors
                var dropsNeeded = Math.ceil((missingHealthPercent / dropHealPercent - 0.01f).toDouble()).toInt()
                dropsNeeded = GameMath.gate(1f, dropsNeeded.toFloat(), volume.toFloat()).toInt()

                val heal = Math.round(hero.HT.toFloat() * dropHealPercent * dropsNeeded.toFloat())

                val effect = Math.min(hero.HT - hero.HP, heal)
                if (effect > 0) {
                    hero.HP += effect
                    hero.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1 + dropsNeeded / 5)
                    hero.sprite!!.showStatus(CharSprite.POSITIVE, Messages.get(this.javaClass, "value", effect))
                }

                volume -= dropsNeeded

                hero.spend(TIME_TO_DRINK)
                hero.busy()

                Sample.INSTANCE.play(Assets.SND_DRINK)
                hero.sprite!!.operate(hero.pos)

                updateQuickslot()


            } else {
                GLog.w(Messages.get(this.javaClass, "empty"))
            }

        }
    }

    fun empty() {
        volume = 0
        updateQuickslot()
    }

    fun collectDew(dew: Dewdrop) {

        GLog.i(Messages.get(this.javaClass, "collected"))
        volume += dew.quantity
        if (volume >= MAX_VOLUME) {
            volume = MAX_VOLUME
            GLog.p(Messages.get(this.javaClass, "full"))
        }

        updateQuickslot()
    }

    fun fill() {
        volume = MAX_VOLUME
        updateQuickslot()
    }

    override fun status(): String? {
        return Messages.format(TXT_STATUS, volume, MAX_VOLUME)
    }

    companion object {

        private val MAX_VOLUME = 20

        private val AC_DRINK = "DRINK"

        private val TIME_TO_DRINK = 1f

        private val TXT_STATUS = "%d/%d"

        private val VOLUME = "volume"
    }

}
