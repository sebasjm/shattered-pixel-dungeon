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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShopkeeperSprite
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem

open class Shopkeeper : NPC() {

    init {
        spriteClass = ShopkeeperSprite::class.java

        properties.add(Char.Property.IMMOVABLE)
    }

    override fun act(): Boolean {

        throwItem()

        sprite!!.turnTo(pos, Dungeon.hero!!.pos)
        spend(Actor.TICK)
        return true
    }

    override fun damage(dmg: Int, src: Any) {
        flee()
    }

    override fun add(buff: Buff) {
        flee()
    }

    open fun flee() {
        for (heap in Dungeon.level!!.heaps.values()) {
            if (heap.type == Heap.Type.FOR_SALE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4)
                heap.destroy()
            }
        }

        destroy()

        sprite!!.killAndErase()
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6)
    }

    override fun reset(): Boolean {
        return true
    }

    override fun interact(): Boolean {
        sell()
        return false
    }

    companion object {

        fun sell(): WndBag {
            return GameScene.selectItem(itemSelector, WndBag.Mode.FOR_SALE, Messages.get(Shopkeeper::class.java, "sell"))
        }

        private val itemSelector = WndBag.Listener { item ->
            if (item != null) {
                val parentWnd = sell()
                GameScene.show(WndTradeItem(item, parentWnd))
            }
        }
    }
}
