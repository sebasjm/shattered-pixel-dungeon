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

package com.shatteredpixel.shatteredpixeldungeon.windows

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.NinePatch
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Component

class WndBlacksmith(troll: Blacksmith, hero: Hero) : Window() {

    private var btnPressed: ItemButton? = null

    private val btnItem1: ItemButton
    private val btnItem2: ItemButton
    private val btnReforge: RedButton

    protected var itemSelector: WndBag.Listener = WndBag.Listener { item ->
        if (item != null) {
            btnPressed!!.item(item)

            if (btnItem1.item != null && btnItem2.item != null) {
                val result = Blacksmith.verify(btnItem1.item, btnItem2.item)
                if (result != null) {
                    GameScene.show(WndMessage(result))
                    btnReforge.enable(false)
                } else {
                    btnReforge.enable(true)
                }
            }
        }
    }

    init {

        val titlebar = IconTitle()
        titlebar.icon(troll.sprite())
        titlebar.label(Messages.titleCase(troll.name))
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        val message = PixelScene.renderMultiline(Messages.get(this, "prompt"), 6)
        message.maxWidth(WIDTH)
        message.setPos(0f, titlebar.bottom() + GAP)
        add(message)

        btnItem1 = object : ItemButton() {
            override fun onClick() {
                btnPressed = btnItem1
                GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLE, Messages.get(WndBlacksmith::class.java, "select"))
            }
        }
        btnItem1.setRect((WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
        add(btnItem1)

        btnItem2 = object : ItemButton() {
            override fun onClick() {
                btnPressed = btnItem2
                GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLE, Messages.get(WndBlacksmith::class.java, "select"))
            }
        }
        btnItem2.setRect(btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
        add(btnItem2)

        btnReforge = object : RedButton(Messages.get(this, "reforge")) {
            override fun onClick() {
                Blacksmith.upgrade(btnItem1.item!!, btnItem2.item!!)
                hide()
            }
        }
        btnReforge.enable(false)
        btnReforge.setRect(0f, btnItem1.bottom() + BTN_GAP, WIDTH.toFloat(), 20f)
        add(btnReforge)


        resize(WIDTH, btnReforge.bottom().toInt())
    }

    open class ItemButton : Component() {

        protected var bg: NinePatch? = null
        var slot: ItemSlot

        var item: Item? = null

        override fun createChildren() {
            super.createChildren()

            bg = Chrome.get(Chrome.Type.BUTTON)
            add(bg)

            slot = object : ItemSlot() {
                override fun onTouchDown() {
                    bg!!.brightness(1.2f)
                    Sample.INSTANCE.play(Assets.SND_CLICK)
                }

                override fun onTouchUp() {
                    bg!!.resetColor()
                }

                override fun onClick() {
                    this@ItemButton.onClick()
                }
            }
            slot.enable(true)
            add(slot)
        }

        protected open fun onClick() {}

        override fun layout() {
            super.layout()

            bg!!.x = x
            bg!!.y = y
            bg!!.size(width, height)

            slot.setRect(x + 2, y + 2, width - 4, height - 4)
        }

        fun item(item: Item?) {
            slot.item(this.item = item)
        }
    }

    companion object {

        private val BTN_SIZE = 36
        private val GAP = 2f
        private val BTN_GAP = 10f
        private val WIDTH = 116
    }
}
