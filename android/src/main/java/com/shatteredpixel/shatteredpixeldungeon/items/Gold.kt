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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class Gold @JvmOverloads constructor(value: Int = 1) : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        image = ItemSpriteSheet.GOLD
        stackable = true
    }

    init {
        this.quantity = value
    }

    override fun actions(hero: Hero): ArrayList<String> {
        return ArrayList()
    }

    override fun doPickUp(hero: Hero): Boolean {

        Dungeon.gold += quantity
        Statistics.goldCollected += quantity
        Badges.validateGoldCollected()

        val thievery = hero.buff<MasterThievesArmband.Thievery>(MasterThievesArmband.Thievery::class.java)
        thievery?.collect(quantity)

        GameScene.pickUp(this, hero.pos)
        hero.sprite!!.showStatus(CharSprite.NEUTRAL, TXT_VALUE, quantity)
        hero.spendAndNext(Item.TIME_TO_PICK_UP)

        Sample.INSTANCE.play(Assets.SND_GOLD, 1f, 1f, Random.Float(0.9f, 1.1f))

        return true
    }

    override fun random(): Item {
        quantity = Random.Int(30 + Dungeon.depth * 10, 60 + Dungeon.depth * 20)
        return this
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(VALUE, quantity)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        quantity = bundle.getInt(VALUE)
    }

    companion object {

        private val TXT_VALUE = "%+d"

        private val VALUE = "value"
    }
}
