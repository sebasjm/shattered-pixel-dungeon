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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Rankings
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesList
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button

import java.util.Locale

class WndRanking(rec: Rankings.Record) : WndTabbed() {

    private var thread: Thread? = null
    private var error: String? = null

    private val busy: Image

    init {
        resize(WIDTH, HEIGHT)

        thread = object : Thread() {
            override fun run() {
                try {
                    Badges.loadGlobal()
                    Rankings.INSTANCE.loadGameData(rec)
                } catch (e: Exception) {
                    error = Messages.get(WndRanking::class.java, "error")
                }

            }
        }
        thread!!.start()

        busy = Icons.BUSY.get()
        busy.origin.set(busy.width / 2, busy.height / 2)
        busy.angularSpeed = 720f
        busy.x = (WIDTH - busy.width) / 2
        busy.y = (HEIGHT - busy.height) / 2
        add(busy)
    }

    override fun update() {
        super.update()

        if (thread != null && !thread!!.isAlive) {
            thread = null
            if (error == null) {
                remove(busy)
                createControls()
            } else {
                hide()
                Game.scene()!!.add(WndError(error))
            }
        }
    }

    private fun createControls() {

        val labels = arrayOf(Messages.get(this, "stats"), Messages.get(this, "items"), Messages.get(this, "badges"))
        val pages = arrayOf(StatsTab(), ItemsTab(), BadgesTab())

        for (i in pages.indices) {

            add(pages[i])

            val tab = RankingTab(labels[i], pages[i])
            add(tab)
        }

        layoutTabs()

        select(0)
    }

    private inner class RankingTab(label: String, private val page: Group?) : WndTabbed.LabeledTab(label) {

        override fun select(value: Boolean) {
            super.select(value)
            if (page != null) {
                page.active = selected
                page.visible = page.active
            }
        }
    }

    private inner class StatsTab : Group() {

        private var GAP = 4

        init {

            if (Dungeon.challenges > 0) GAP--

            val heroClass = Dungeon.hero!!.className()

            val title = IconTitle()
            title.icon(HeroSprite.avatar(Dungeon.hero!!.heroClass, Dungeon.hero!!.tier()))
            title.label(Messages.get(this, "title", Dungeon.hero!!.lvl, heroClass).toUpperCase(Locale.ENGLISH))
            title.color(Window.SHPX_COLOR)
            title.setRect(0f, 0f, WIDTH.toFloat(), 0f)
            add(title)

            var pos = title.bottom()

            if (Dungeon.challenges > 0) {
                val btnChallenges = object : RedButton(Messages.get(this, "challenges")) {
                    override fun onClick() {
                        Game.scene()!!.add(WndChallenges(Dungeon.challenges, false))
                    }
                }
                val btnW = btnChallenges.reqWidth() + 2
                btnChallenges.setRect((WIDTH - btnW) / 2, pos, btnW, btnChallenges.reqHeight() + 2)
                add(btnChallenges)

                pos = btnChallenges.bottom()
            }

            pos += (GAP + GAP).toFloat()

            pos = statSlot(this, Messages.get(this, "str"), Integer.toString(Dungeon.hero!!.STR), pos)
            pos = statSlot(this, Messages.get(this, "health"), Integer.toString(Dungeon.hero!!.HT), pos)

            pos += GAP.toFloat()

            pos = statSlot(this, Messages.get(this, "duration"), Integer.toString(Statistics.duration.toInt()), pos)

            pos += GAP.toFloat()

            pos = statSlot(this, Messages.get(this, "depth"), Integer.toString(Statistics.deepestFloor), pos)
            pos = statSlot(this, Messages.get(this, "enemies"), Integer.toString(Statistics.enemiesSlain), pos)
            pos = statSlot(this, Messages.get(this, "gold"), Integer.toString(Statistics.goldCollected), pos)

            pos += GAP.toFloat()

            pos = statSlot(this, Messages.get(this, "food"), Integer.toString(Statistics.foodEaten), pos)
            pos = statSlot(this, Messages.get(this, "alchemy"), Integer.toString(Statistics.potionsCooked), pos)
            pos = statSlot(this, Messages.get(this, "ankhs"), Integer.toString(Statistics.ankhsUsed), pos)
        }

        private fun statSlot(parent: Group, label: String, value: String, pos: Float): Float {

            var txt = PixelScene.renderText(label, 7)
            txt.y = pos
            parent.add(txt)

            txt = PixelScene.renderText(value, 7)
            txt.x = WIDTH * 0.65f
            txt.y = pos
            PixelScene.align(txt)
            parent.add(txt)

            return pos + GAP.toFloat() + txt.baseLine()
        }
    }

    private inner class ItemsTab : Group() {

        private var pos: Float = 0.toFloat()

        init {

            val stuff = Dungeon.hero!!.belongings
            if (stuff.weapon != null) {
                addItem(stuff.weapon)
            }
            if (stuff.armor != null) {
                addItem(stuff.armor)
            }
            if (stuff.misc1 != null) {
                addItem(stuff.misc1)
            }
            if (stuff.misc2 != null) {
                addItem(stuff.misc2)
            }

            pos = 0f
            for (i in 0..3) {
                if (Dungeon.quickslot.getItem(i) != null) {
                    val slot = QuickSlotButton(Dungeon.quickslot.getItem(i))

                    slot.setRect(pos, 116f, 28f, 28f)

                    add(slot)

                } else {
                    val bg = ColorBlock(28f, 28f, -0x66aca9b3)
                    bg.x = pos
                    bg.y = 116f
                    add(bg)
                }
                pos += 29f
            }
        }

        private fun addItem(item: Item?) {
            val slot = ItemButton(item)
            slot.setRect(0f, pos, width.toFloat(), ItemButton.HEIGHT.toFloat())
            add(slot)

            pos += slot.height() + 1
        }
    }

    private inner class BadgesTab : Group() {
        init {

            camera = this@WndRanking.camera

            val list = BadgesList(false)
            add(list)

            list.setSize(WIDTH.toFloat(), HEIGHT.toFloat())
        }
    }

    private inner class ItemButton(private val item: Item) : Button() {

        private var slot: ItemSlot? = null
        private var bg: ColorBlock? = null
        private var name: RenderedText? = null

        init {

            slot!!.item(item)
            if (item.cursed && item.cursedKnown) {
                bg!!.ra = +0.2f
                bg!!.ga = -0.1f
            } else if (!item.isIdentified) {
                bg!!.ra = 0.1f
                bg!!.ba = 0.1f
            }
        }

        override fun createChildren() {

            bg = ColorBlock(HEIGHT.toFloat(), HEIGHT.toFloat(), -0x66aca9b3)
            add(bg)

            slot = ItemSlot()
            add(slot)

            name = PixelScene.renderText("?", 7)
            add(name)

            super.createChildren()
        }

        override fun layout() {
            bg!!.x = x
            bg!!.y = y

            slot!!.setRect(x, y, HEIGHT.toFloat(), HEIGHT.toFloat())
            PixelScene.align(slot!!)

            name!!.x = slot!!.right() + 2
            name!!.y = y + (height - name!!.baseLine()) / 2
            PixelScene.align(name!!)

            var str = Messages.titleCase(item.name())
            name!!.text(str)
            if (name!!.width() > width - name!!.x) {
                do {
                    str = str.substring(0, str.length - 1)
                    name!!.text("$str...")
                } while (name!!.width() > width - name!!.x)
            }

            super.layout()
        }

        override fun onTouchDown() {
            bg!!.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }

        override fun onTouchUp() {
            bg!!.brightness(1.0f)
        }

        override fun onClick() {
            Game.scene()!!.add(WndItem(null, item))
        }

        companion object {

            val HEIGHT = 28
        }
    }

    private inner class QuickSlotButton internal constructor(private val item: Item) : ItemSlot(item) {
        private var bg: ColorBlock? = null

        override fun createChildren() {
            bg = ColorBlock(HEIGHT.toFloat(), HEIGHT.toFloat(), -0x66aca9b3)
            add(bg)

            super.createChildren()
        }

        override fun layout() {
            bg!!.x = x
            bg!!.y = y

            super.layout()
        }

        override fun onTouchDown() {
            bg!!.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }

        override fun onTouchUp() {
            bg!!.brightness(1.0f)
        }

        override fun onClick() {
            Game.scene()!!.add(WndItem(null, item))
        }

        companion object {

            val HEIGHT = 28
        }
    }

    companion object {

        private val WIDTH = 115
        private val HEIGHT = 144
    }
}
