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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Button
import com.watabou.utils.PathFinder

class QuickSlotButton(private val slotNum: Int) : Button(), WndBag.Listener {

    private var slot: ItemSlot? = null

    init {
        item(select(slotNum))

        instance[slotNum] = this
    }

    override fun destroy() {
        super.destroy()

        reset()
    }

    override fun createChildren() {
        super.createChildren()

        slot = object : ItemSlot() {
            override fun onClick() {
                if (targeting) {
                    val cell = autoAim(lastTarget!!, select(slotNum)!!)

                    if (cell != -1) {
                        GameScene.handleCell(cell)
                    } else {
                        //couldn't auto-aim, just target the position and hope for the best.
                        GameScene.handleCell(lastTarget!!.pos)
                    }
                } else {
                    val item = select(slotNum)
                    if (item!!.usesTargeting)
                        useTargeting()
                    item.execute(Dungeon.hero)
                }
            }

            override fun onLongClick(): Boolean {
                return this@QuickSlotButton.onLongClick()
            }

            override fun onTouchDown() {
                icon.lightness(0.7f)
            }

            override fun onTouchUp() {
                icon.resetColor()
            }
        }
        slot!!.showParams(true, false, true)
        add(slot)

        crossB = Icons.TARGET.get()
        crossB!!.visible = false
        add(crossB)

        crossM = Image()
        crossM!!.copy(crossB)
    }

    override fun layout() {
        super.layout()

        slot!!.fill(this)

        crossB!!.x = x + (width - crossB!!.width) / 2
        crossB!!.y = y + (height - crossB!!.height) / 2
        PixelScene.align(crossB!!)
    }

    override fun onClick() {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, Messages.get(this, "select_item"))
    }

    override fun onLongClick(): Boolean {
        GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, Messages.get(this, "select_item"))
        return true
    }

    override fun onSelect(item: Item?) {
        if (item != null) {
            Dungeon.quickslot.setSlot(slotNum, item)
            refresh()
        }
    }

    fun item(item: Item?) {
        slot!!.item(item)
        enableSlot()
    }

    fun enable(value: Boolean) {
        active = value
        if (value) {
            enableSlot()
        } else {
            slot!!.enable(false)
        }
    }

    private fun enableSlot() {
        slot!!.enable(Dungeon.quickslot.isNonePlaceholder(slotNum)!!)
    }

    private fun useTargeting() {

        if (lastTarget != null &&
                Actor.chars().contains(lastTarget) &&
                lastTarget!!.isAlive &&
                Dungeon.level!!.heroFOV[lastTarget!!.pos]) {

            targeting = true
            val sprite = lastTarget!!.sprite

            sprite!!.parent!!.addToFront(crossM)
            crossM!!.point(sprite.center(crossM))

            crossB!!.point(slot!!.icon.center(crossB))
            crossB!!.visible = true

        } else {

            lastTarget = null
            targeting = false

        }

    }

    companion object {

        private var instance = arrayOfNulls<QuickSlotButton>(4)

        private var crossB: Image? = null
        private var crossM: Image? = null

        private var targeting = false
        var lastTarget: Char? = null

        fun reset() {
            instance = arrayOfNulls(4)

            lastTarget = null
        }

        private fun select(slotNum: Int): Item? {
            return Dungeon.quickslot.getItem(slotNum)
        }

        //FIXME: this is currently very expensive, should either optimize ballistica or this, or both
        @JvmOverloads
        fun autoAim(target: Char, item: Item = Item()): Int {

            //first try to directly target
            if (item.throwPos(Dungeon.hero, target.pos) == target.pos) {
                return target.pos
            }

            //Otherwise pick nearby tiles to try and 'angle' the shot, auto-aim basically.
            PathFinder.buildDistanceMap(target.pos, BArray.not(BooleanArray(Dungeon.level!!.length()), null), 2)
            for (i in PathFinder.distance.indices) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE && item.throwPos(Dungeon.hero, i) == target.pos)
                    return i
            }

            //couldn't find a cell, give up.
            return -1
        }

        fun refresh() {
            for (i in instance.indices) {
                if (instance[i] != null) {
                    instance[i].item(select(i))
                }
            }
        }

        fun target(target: Char) {
            if (target !== Dungeon.hero) {
                lastTarget = target

                TargetHealthIndicator.instance.target(target)
            }
        }

        fun cancel() {
            if (targeting) {
                crossB!!.visible = false
                crossM!!.remove()
                targeting = false
            }
        }
    }
}//will use generic projectile logic if no item is specified
