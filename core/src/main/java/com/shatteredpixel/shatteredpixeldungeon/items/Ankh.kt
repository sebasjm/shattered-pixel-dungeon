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
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle

import java.util.ArrayList

class Ankh : Item() {

    var isBlessed: Boolean? = false
        private set

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        image = ItemSpriteSheet.ANKH

        //You tell the ankh no, don't revive me, and then it comes back to revive you again in another run.
        //I'm not sure if that's enthusiasm or passive-aggression.
        bones = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        val vial = hero.belongings.getItem<DewVial>(DewVial::class.java)
        if (vial != null && vial.isFull && (!isBlessed)!!)
            actions.add(AC_BLESS)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_BLESS) {

            val vial = hero.belongings.getItem<DewVial>(DewVial::class.java)
            if (vial != null) {
                isBlessed = true
                vial.empty()
                GLog.p(Messages.get(this, "bless"))
                hero.spend(1f)
                hero.busy()


                Sample.INSTANCE.play(Assets.SND_DRINK)
                CellEmitter.get(hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
                hero.sprite!!.operate(hero.pos)
            }
        }
    }

    override fun desc(): String {
        return if (isBlessed!!)
            Messages.get(this, "desc_blessed")
        else
            super.desc()
    }

    override fun glowing(): Glowing? {
        return if (isBlessed) WHITE else null
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(BLESSED, isBlessed!!)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        isBlessed = bundle.getBoolean(BLESSED)
    }

    override fun price(): Int {
        return 50 * quantity
    }

    companion object {

        val AC_BLESS = "BLESS"

        private val WHITE = Glowing(0xFFFFCC)

        private val BLESSED = "blessed"
    }
}
