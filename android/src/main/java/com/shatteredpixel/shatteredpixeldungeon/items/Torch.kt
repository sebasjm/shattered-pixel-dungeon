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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.particles.Emitter

import java.util.ArrayList

class Torch : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        image = ItemSpriteSheet.TORCH

        stackable = true

        defaultAction = AC_LIGHT
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_LIGHT)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_LIGHT) {

            hero.spend(TIME_TO_LIGHT)
            hero.busy()

            hero.sprite!!.operate(hero.pos)

            detach(hero.belongings.backpack)

            Buff.affect<Light>(hero, Light::class.java, Light.DURATION)

            val emitter = hero.sprite!!.centerEmitter()
            emitter.start(FlameParticle.FACTORY, 0.2f, 3)

        }
    }

    override fun price(): Int {
        return 10 * quantity
    }

    companion object {

        val AC_LIGHT = "LIGHT"

        val TIME_TO_LIGHT = 1f
    }

}
