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

package com.shatteredpixel.shatteredpixeldungeon.items.keys

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle

abstract class Key : Item() {

    var depth: Int = 0

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        stackable = true
        unique = true
    }

    override fun isSimilar(item: Item): Boolean {
        return item.javaClass == javaClass && (item as Key).depth == depth
    }

    override fun doPickUp(hero: Hero): Boolean {
        GameScene.pickUpJournal(this, hero.pos)
        WndJournal.last_index = 1
        Notes.add(this)
        Sample.INSTANCE.play(Assets.SND_ITEM)
        hero.spendAndNext(Item.TIME_TO_PICK_UP)
        GameScene.updateKeyDisplay()
        return true
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DEPTH, depth)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        depth = bundle.getInt(DEPTH)
    }

    companion object {

        val TIME_TO_UNLOCK = 1f

        private val DEPTH = "depth"
    }

}
