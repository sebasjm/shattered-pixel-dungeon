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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Gizmo
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Button
import com.watabou.noosa.ui.Component
import com.watabou.utils.Point
import com.watabou.utils.PointF

class Toolbar : Component() {

    private var btnWait: Tool? = null
    private var btnSearch: Tool? = null
    private var btnInventory: Tool? = null
    private var btnQuick: Array<QuickslotTool>? = null

    private var pickedUp: PickedUpItem? = null

    private var lastEnabled = true
    var examining = false

    enum class Mode {
        SPLIT,
        GROUP,
        CENTER
    }

    init {

        instance = this

        height = btnInventory!!.height()
    }

    override fun createChildren() {

        add(btnWait = object : Tool(24, 0, 20, 26) {
            override fun onClick() {
                examining = false
                Dungeon.hero!!.rest(false)
            }

            override fun onLongClick(): Boolean {
                examining = false
                Dungeon.hero!!.rest(true)
                return true
            }
        })

        add(btnSearch = object : Tool(44, 0, 20, 26) {
            override fun onClick() {
                if (!examining) {
                    GameScene.selectCell(informer)
                    examining = true
                } else {
                    informer.onSelect(null)
                    Dungeon.hero!!.search(true)
                }
            }

            override fun onLongClick(): Boolean {
                Dungeon.hero!!.search(true)
                return true
            }
        })

        btnQuick = arrayOfNulls(4)

        add(btnQuick[3] = QuickslotTool(64, 0, 22, 24, 3))

        add(btnQuick[2] = QuickslotTool(64, 0, 22, 24, 2))

        add(btnQuick[1] = QuickslotTool(64, 0, 22, 24, 1))

        add(btnQuick[0] = QuickslotTool(64, 0, 22, 24, 0))

        add(btnInventory = object : Tool(0, 0, 24, 26) {
            private var gold: GoldIndicator? = null

            override fun onClick() {
                GameScene.show(WndBag(Dungeon.hero!!.belongings.backpack, null, WndBag.Mode.ALL, null))
            }

            override fun onLongClick(): Boolean {
                WndJournal.last_index = 2 //catalog page
                GameScene.show(WndJournal())
                return true
            }

            override fun createChildren() {
                super.createChildren()
                gold = GoldIndicator()
                add(gold)
            }

            override fun layout() {
                super.layout()
                gold!!.fill(this)
            }
        })

        add(pickedUp = PickedUpItem())
    }

    override fun layout() {

        val visible = IntArray(4)
        val slots = SPDSettings.quickSlots()

        for (i in 0..3)
            visible[i] = (if (slots > i) y + 2 else y + 25).toInt()

        for (i in 0..3) {
            btnQuick!![i].active = slots > i
            btnQuick!![i].visible = btnQuick!![i].active
            //decides on quickslot layout, depending on available screen size.
            if (slots == 4 && width < 152) {
                if (width < 138) {
                    if (SPDSettings.flipToolbar() && i == 3 || !SPDSettings.flipToolbar() && i == 0) {
                        btnQuick!![i].border(0, 0)
                        btnQuick!![i].frame(88, 0, 17, 24)
                    } else {
                        btnQuick!![i].border(0, 1)
                        btnQuick!![i].frame(88, 0, 18, 24)
                    }
                } else {
                    if (i == 0 && !SPDSettings.flipToolbar() || i == 3 && SPDSettings.flipToolbar()) {
                        btnQuick!![i].border(0, 2)
                        btnQuick!![i].frame(106, 0, 19, 24)
                    } else if (i == 0 && SPDSettings.flipToolbar() || i == 3 && !SPDSettings.flipToolbar()) {
                        btnQuick!![i].border(2, 1)
                        btnQuick!![i].frame(86, 0, 20, 24)
                    } else {
                        btnQuick!![i].border(0, 1)
                        btnQuick!![i].frame(88, 0, 18, 24)
                    }
                }
            } else {
                btnQuick!![i].border(2, 2)
                btnQuick!![i].frame(64, 0, 22, 24)
            }

        }

        var right = width
        when (Mode.valueOf(SPDSettings.toolbarMode())) {
            Toolbar.Mode.SPLIT -> {
                btnWait!!.setPos(x, y)
                btnSearch!!.setPos(btnWait!!.right(), y)

                btnInventory!!.setPos(right - btnInventory!!.width(), y)

                btnQuick!![0].setPos(btnInventory!!.left() - btnQuick!![0].width(), visible[0].toFloat())
                btnQuick!![1].setPos(btnQuick!![0].left() - btnQuick!![1].width(), visible[1].toFloat())
                btnQuick!![2].setPos(btnQuick!![1].left() - btnQuick!![2].width(), visible[2].toFloat())
                btnQuick!![3].setPos(btnQuick!![2].left() - btnQuick!![3].width(), visible[3].toFloat())
            }

        //center = group but.. well.. centered, so all we need to do is pre-emptively set the right side further in.
            Toolbar.Mode.CENTER -> {
                var toolbarWidth = btnWait!!.width() + btnSearch!!.width() + btnInventory!!.width()
                for (slot in btnQuick!!) {
                    if (slot.visible) toolbarWidth += slot.width()
                }
                right = (width + toolbarWidth) / 2
                btnWait!!.setPos(right - btnWait!!.width(), y)
                btnSearch!!.setPos(btnWait!!.left() - btnSearch!!.width(), y)
                btnInventory!!.setPos(btnSearch!!.left() - btnInventory!!.width(), y)

                btnQuick!![0].setPos(btnInventory!!.left() - btnQuick!![0].width(), visible[0].toFloat())
                btnQuick!![1].setPos(btnQuick!![0].left() - btnQuick!![1].width(), visible[1].toFloat())
                btnQuick!![2].setPos(btnQuick!![1].left() - btnQuick!![2].width(), visible[2].toFloat())
                btnQuick!![3].setPos(btnQuick!![2].left() - btnQuick!![3].width(), visible[3].toFloat())
            }

            Toolbar.Mode.GROUP -> {
                btnWait!!.setPos(right - btnWait!!.width(), y)
                btnSearch!!.setPos(btnWait!!.left() - btnSearch!!.width(), y)
                btnInventory!!.setPos(btnSearch!!.left() - btnInventory!!.width(), y)
                btnQuick!![0].setPos(btnInventory!!.left() - btnQuick!![0].width(), visible[0].toFloat())
                btnQuick!![1].setPos(btnQuick!![0].left() - btnQuick!![1].width(), visible[1].toFloat())
                btnQuick!![2].setPos(btnQuick!![1].left() - btnQuick!![2].width(), visible[2].toFloat())
                btnQuick!![3].setPos(btnQuick!![2].left() - btnQuick!![3].width(), visible[3].toFloat())
            }
        }
        right = width

        if (SPDSettings.flipToolbar()) {

            btnWait!!.setPos(right - btnWait!!.right(), y)
            btnSearch!!.setPos(right - btnSearch!!.right(), y)
            btnInventory!!.setPos(right - btnInventory!!.right(), y)

            for (i in 0..3) {
                btnQuick!![i].setPos(right - btnQuick!![i].right(), visible[i].toFloat())
            }

        }

    }

    override fun update() {
        super.update()

        if (lastEnabled != (Dungeon.hero!!.ready && Dungeon.hero!!.isAlive)) {
            lastEnabled = Dungeon.hero!!.ready && Dungeon.hero!!.isAlive

            for (tool in members!!) {
                if (tool is Tool) {
                    tool.enable(lastEnabled)
                }
            }
        }

        if (!Dungeon.hero!!.isAlive) {
            btnInventory!!.enable(true)
        }
    }

    fun pickup(item: Item, cell: Int) {
        pickedUp!!.reset(item,
                cell,
                btnInventory!!.centerX(),
                btnInventory!!.centerY())
    }

    private open class Tool(x: Int, y: Int, width: Int, height: Int) : Button() {

        private var base: Image? = null

        init {

            hotArea.blockWhenInactive = true
            frame(x, y, width, height)
        }

        fun frame(x: Int, y: Int, width: Int, height: Int) {
            base!!.frame(x, y, width, height)

            this.width = width.toFloat()
            this.height = height.toFloat()
        }

        override fun createChildren() {
            super.createChildren()

            base = Image(Assets.TOOLBAR)
            add(base)
        }

        override fun layout() {
            super.layout()

            base!!.x = x
            base!!.y = y
        }

        override fun onTouchDown() {
            base!!.brightness(1.4f)
        }

        override fun onTouchUp() {
            if (active) {
                base!!.resetColor()
            } else {
                base!!.tint(BGCOLOR, 0.7f)
            }
        }

        open fun enable(value: Boolean) {
            if (value != active) {
                if (value) {
                    base!!.resetColor()
                } else {
                    base!!.tint(BGCOLOR, 0.7f)
                }
                active = value
            }
        }

        companion object {

            private val BGCOLOR = 0x7B8073
        }
    }

    private class QuickslotTool(x: Int, y: Int, width: Int, height: Int, slotNum: Int) : Tool(x, y, width, height) {

        private val slot: QuickSlotButton
        private var borderLeft = 2
        private var borderRight = 2

        init {

            slot = QuickSlotButton(slotNum)
            add(slot)
        }

        fun border(left: Int, right: Int) {
            borderLeft = left
            borderRight = right
            layout()
        }

        override fun layout() {
            super.layout()
            slot.setRect(x + borderLeft, y + 2, width - borderLeft.toFloat() - borderRight.toFloat(), height - 4)
        }

        override fun enable(value: Boolean) {
            super.enable(value)
            slot.enable(value)
        }
    }

    class PickedUpItem : ItemSprite() {

        private var startScale: Float = 0.toFloat()
        private var startX: Float = 0.toFloat()
        private var startY: Float = 0.toFloat()
        private var endX: Float = 0.toFloat()
        private var endY: Float = 0.toFloat()
        private var left: Float = 0.toFloat()

        init {

            originToCenter()

            visible = false
            active = visible
        }

        fun reset(item: Item, cell: Int, endX: Float, endY: Float) {
            view(item)

            visible = true
            active = visible

            val tile = DungeonTerrainTilemap.raisedTileCenterToWorld(cell)
            val screen = Camera.main.cameraToScreen(tile.x, tile.y)
            val start = camera()!!.screenToCamera(screen.x, screen.y)

            this.startX = start.x - ItemSprite.SIZE / 2
            x = this.startX
            this.startY = start.y - ItemSprite.SIZE / 2
            y = this.startY

            this.endX = endX - ItemSprite.SIZE / 2
            this.endY = endY - ItemSprite.SIZE / 2
            left = DURATION

            scale.set(startScale = Camera.main.zoom / camera()!!.zoom)

        }

        override fun update() {
            super.update()

            if ((left -= Game.elapsed) <= 0) {

                active = false
                visible = active
                if (emitter != null) emitter!!.on = false

            } else {
                val p = left / DURATION
                scale.set(startScale * Math.sqrt(p.toDouble()).toFloat())

                x = startX * p + endX * (1 - p)
                y = startY * p + endY * (1 - p)
            }
        }

        companion object {

            private val DURATION = 0.5f
        }
    }

    companion object {

        private var instance: Toolbar?

        fun updateLayout() {
            if (instance != null) instance!!.layout()
        }

        private val informer = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                instance!!.examining = false
                GameScene.examineCell(cell)
            }

            override fun prompt(): String {
                return Messages.get(Toolbar::class.java, "examine_prompt")
            }
        }
    }
}
