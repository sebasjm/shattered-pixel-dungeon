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

class Dewdrop : Item() {

    init {
        image = ItemSpriteSheet.DEWDROP

        stackable = true
    }

    override fun doPickUp(hero: Hero): Boolean {

        val vial = hero.belongings.getItem<DewVial>(DewVial::class.java)

        if (vial != null && !vial.isFull) {

            vial.collectDew(this)

        } else {

            //20 drops for a full heal normally, 15 for the warden
            val healthPercent = if (hero.subClass == HeroSubClass.WARDEN) 0.0667f else 0.05f
            val heal = Math.round(hero.HT.toFloat() * healthPercent * quantity.toFloat())

            val effect = Math.min(hero.HT - hero.HP, heal)
            if (effect > 0) {
                hero.HP += effect
                hero.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)
                hero.sprite!!.showStatus(CharSprite.POSITIVE, Messages.get(this.javaClass, "value", effect))
            } else {
                GLog.i(Messages.get(this.javaClass, "already_full"))
                return false
            }

        }

        Sample.INSTANCE.play(Assets.SND_DEWDROP)
        hero.spendAndNext(Item.TIME_TO_PICK_UP)

        return true
    }

    override//max of one dew in a stack
    fun quantity(value: Int): Item {
        quantity = Math.min(value, 1)
        return this
    }

}
