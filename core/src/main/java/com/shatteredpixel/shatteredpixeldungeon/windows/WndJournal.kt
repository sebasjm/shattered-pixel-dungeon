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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
import com.shatteredpixel.shatteredpixeldungeon.journal.Document
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane
import com.watabou.noosa.BitmapText
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashMap

//FIXME a lot of cleanup and improvements to do here
class WndJournal : WndTabbed() {

    private val guideTab: GuideTab
    private val notesTab: NotesTab
    private val catalogTab: CatalogTab

    init {

        val width = if (SPDSettings.landscape()) WIDTH_L else WIDTH_P
        val height = if (SPDSettings.landscape()) HEIGHT_L else HEIGHT_P

        resize(width, height)

        guideTab = GuideTab()
        add(guideTab)
        guideTab.setRect(0f, 0f, width.toFloat(), height.toFloat())
        guideTab.updateList()

        notesTab = NotesTab()
        add(notesTab)
        notesTab.setRect(0f, 0f, width.toFloat(), height.toFloat())
        notesTab.updateList()

        catalogTab = CatalogTab()
        add(catalogTab)
        catalogTab.setRect(0f, 0f, width.toFloat(), height.toFloat())
        catalogTab.updateList()

        val tabs = arrayOf<WndTabbed.Tab>(object : WndTabbed.LabeledTab(Messages.get(this, "guide")) {
            override fun select(value: Boolean) {
                super.select(value)
                guideTab.visible = value
                guideTab.active = guideTab.visible
                if (value) last_index = 0
            }
        }, object : WndTabbed.LabeledTab(Messages.get(this, "notes")) {
            override fun select(value: Boolean) {
                super.select(value)
                notesTab.visible = value
                notesTab.active = notesTab.visible
                if (value) last_index = 1
            }
        }, object : WndTabbed.LabeledTab(Messages.get(this, "items")) {
            override fun select(value: Boolean) {
                super.select(value)
                catalogTab.visible = value
                catalogTab.active = catalogTab.visible
                if (value) last_index = 2
            }
        })

        for (tab in tabs) {
            add(tab)
        }

        layoutTabs()

        select(last_index)
    }

    private open class ListItem @JvmOverloads constructor(icon: Image, text: String, d: Int = -1) : Component() {

        protected var label: RenderedTextMultiline
        protected var depth: BitmapText
        protected var line: ColorBlock
        protected var icon: Image

        init {

            this.icon.copy(icon)

            label.text(text)

            if (d >= 0) {
                depth.text(Integer.toString(d))
                depth.measure()

                if (d == Dungeon.depth) {
                    label.hardlight(Window.TITLE_COLOR)
                    depth.hardlight(Window.TITLE_COLOR)
                }
            }
        }

        override fun createChildren() {
            label = PixelScene.renderMultiline(7)
            add(label)

            icon = Image()
            add(icon)

            depth = BitmapText(PixelScene.pixelFont)
            add(depth)

            line = ColorBlock(1f, 1f, -0xddddde)
            add(line)

        }

        override fun layout() {

            icon.y = y + 1f + (height() - 1f - icon.height()) / 2f
            PixelScene.align(icon)

            depth.x = icon.x + (icon.width - depth.width()) / 2f
            depth.y = icon.y + (icon.height - depth.height()) / 2f + 1f
            PixelScene.align(depth)

            line.size(width, 1f)
            line.x = 0f
            line.y = y

            label.maxWidth((width - icon.width() - 8f - 1f).toInt())
            label.setPos(icon.x + icon.width() + 1f, y + 1f + (height() - label.height()) / 2f)
            PixelScene.align(label)
        }
    }

    private class GuideTab : Component() {

        private var list: ScrollPane? = null
        private val pages = ArrayList<GuideItem>()

        override fun createChildren() {
            list = object : ScrollPane(Component()) {
                override fun onClick(x: Float, y: Float) {
                    val size = pages.size
                    for (i in 0 until size) {
                        if (pages[i].onClick(x, y)) {
                            break
                        }
                    }
                }
            }
            add(list)
        }

        override fun layout() {
            super.layout()
            list!!.setRect(0f, 0f, width, height)
        }

        private fun updateList() {
            val content = list!!.content()

            var pos = 0f

            val line = ColorBlock(width(), 1f, -0xddddde)
            line.y = pos
            content.add(line)

            val title = PixelScene.renderMultiline(Document.ADVENTURERS_GUIDE.title(), 9)
            title.hardlight(Window.TITLE_COLOR)
            title.maxWidth(width().toInt() - 2)
            title.setPos((width() - title.width()) / 2f, pos + 1f + (ITEM_HEIGHT - title.height()) / 2f)
            PixelScene.align(title)
            content.add(title)

            pos += Math.max(ITEM_HEIGHT.toFloat(), title.height())

            for (page in Document.ADVENTURERS_GUIDE.pages()) {
                val item = GuideItem(page)

                item.setRect(0f, pos, width(), ITEM_HEIGHT.toFloat())
                content.add(item)

                pos += item.height()
                pages.add(item)
            }

            content.setSize(width(), pos)
            list!!.setSize(list!!.width(), list!!.height())
        }

        private class GuideItem(private val page: String) : ListItem(ItemSprite(ItemSpriteSheet.GUIDE_PAGE, null), Messages.titleCase(Document.ADVENTURERS_GUIDE.pageTitle(page)), -1) {

            private var found = false

            init {
                found = Document.ADVENTURERS_GUIDE.hasPage(page)

                if (!found) {
                    icon.hardlight(0.5f, 0.5f, 0.5f)
                    label.text(Messages.titleCase(Messages.get(this, "missing")))
                    label.hardlight(0x999999)
                }

            }

            fun onClick(x: Float, y: Float): Boolean {
                if (inside(x, y) && found) {
                    GameScene.show(WndStory(Document.ADVENTURERS_GUIDE.pageBody(page)))
                    return true
                } else {
                    return false
                }
            }

        }

    }

    private class NotesTab : Component() {

        private var list: ScrollPane? = null

        override fun createChildren() {
            list = ScrollPane(Component())
            add(list)
        }

        override fun layout() {
            super.layout()
            list!!.setRect(0f, 0f, width, height)
        }

        private fun updateList() {
            val content = list!!.content()

            var pos = 0f

            //Keys
            val keys = Notes.getRecords<Notes.KeyRecord>(Notes.KeyRecord::class.java)
            if (!keys.isEmpty()) {
                val line = ColorBlock(width(), 1f, -0xddddde)
                line.y = pos
                content.add(line)

                val title = PixelScene.renderMultiline(Messages.get(this, "keys"), 9)
                title.hardlight(Window.TITLE_COLOR)
                title.maxWidth(width().toInt() - 2)
                title.setPos((width() - title.width()) / 2f, pos + 1f + (ITEM_HEIGHT - title.height()) / 2f)
                PixelScene.align(title)
                content.add(title)

                pos += Math.max(ITEM_HEIGHT.toFloat(), title.height())
            }
            for (rec in keys) {
                val item = ListItem(Icons.get(Icons.DEPTH),
                        Messages.titleCase(rec.desc()), rec.depth())
                item.setRect(0f, pos, width(), ITEM_HEIGHT.toFloat())
                content.add(item)

                pos += item.height()
            }

            //Landmarks
            val landmarks = Notes.getRecords<Notes.LandmarkRecord>(Notes.LandmarkRecord::class.java)
            if (!landmarks.isEmpty()) {
                val line = ColorBlock(width(), 1f, -0xddddde)
                line.y = pos
                content.add(line)

                val title = PixelScene.renderMultiline(Messages.get(this, "landmarks"), 9)
                title.hardlight(Window.TITLE_COLOR)
                title.maxWidth(width().toInt() - 2)
                title.setPos((width() - title.width()) / 2f, pos + 1f + (ITEM_HEIGHT - title.height()) / 2f)
                PixelScene.align(title)
                content.add(title)

                pos += Math.max(ITEM_HEIGHT.toFloat(), title.height())
            }
            for (rec in landmarks) {
                val item = ListItem(Icons.get(Icons.DEPTH),
                        Messages.titleCase(rec.desc()), rec.depth())
                item.setRect(0f, pos, width(), ITEM_HEIGHT.toFloat())
                content.add(item)

                pos += item.height()
            }

            content.setSize(width(), pos)
            list!!.setSize(list!!.width(), list!!.height())
        }

    }

    private class CatalogTab : Component() {

        private var itemButtons: Array<RedButton>? = null

        private var list: ScrollPane? = null

        private val items = ArrayList<CatalogItem>()

        override fun createChildren() {
            itemButtons = arrayOfNulls(NUM_BUTTONS)
            for (i in 0 until NUM_BUTTONS) {
                val idx = i
                itemButtons[i] = object : RedButton("") {
                    override fun onClick() {
                        currentItemIdx = idx
                        updateList()
                    }
                }
                itemButtons!![i].icon(ItemSprite(ItemSpriteSheet.WEAPON_HOLDER + i, null))
                add(itemButtons!![i])
            }

            list = object : ScrollPane(Component()) {
                override fun onClick(x: Float, y: Float) {
                    val size = items.size
                    for (i in 0 until size) {
                        if (items[i].onClick(x, y)) {
                            break
                        }
                    }
                }
            }
            add(list)
        }

        override fun layout() {
            super.layout()

            val perRow = NUM_BUTTONS
            val buttonWidth = width() / perRow

            for (i in 0 until NUM_BUTTONS) {
                itemButtons!![i].setRect(i % perRow * buttonWidth, (i / perRow * (BUTTON_HEIGHT + 1)).toFloat(),
                        buttonWidth, BUTTON_HEIGHT.toFloat())
                PixelScene.align(itemButtons!![i])
            }

            list!!.setRect(0f, itemButtons!![NUM_BUTTONS - 1].bottom() + 1, width,
                    height - itemButtons!![NUM_BUTTONS - 1].bottom() - 1f)
        }

        private fun updateList() {

            items.clear()

            for (i in 0 until NUM_BUTTONS) {
                if (i == currentItemIdx) {
                    itemButtons!![i].icon()!!.color(Window.TITLE_COLOR)
                } else {
                    itemButtons!![i].icon()!!.resetColor()
                }
            }

            val content = list!!.content()
            content.clear()
            list!!.scrollTo(0f, 0f)

            val itemClasses: ArrayList<Class<out Item>>
            val known = HashMap<Class<out Item>, Boolean>()
            if (currentItemIdx == WEAPON_IDX) {
                itemClasses = ArrayList(Catalog.WEAPONS.items())
                for (cls in itemClasses) known[cls] = true
            } else if (currentItemIdx == ARMOR_IDX) {
                itemClasses = ArrayList(Catalog.ARMOR.items())
                for (cls in itemClasses) known[cls] = true
            } else if (currentItemIdx == WAND_IDX) {
                itemClasses = ArrayList(Catalog.WANDS.items())
                for (cls in itemClasses) known[cls] = true
            } else if (currentItemIdx == RING_IDX) {
                itemClasses = ArrayList(Catalog.RINGS.items())
                for (cls in itemClasses) known[cls] = Ring.known!!.contains(cls)
            } else if (currentItemIdx == ARTIF_IDX) {
                itemClasses = ArrayList(Catalog.ARTIFACTS.items())
                for (cls in itemClasses) known[cls] = true
            } else if (currentItemIdx == POTION_IDX) {
                itemClasses = ArrayList(Catalog.POTIONS.items())
                for (cls in itemClasses) known[cls] = Potion.known!!.contains(cls)
            } else if (currentItemIdx == SCROLL_IDX) {
                itemClasses = ArrayList(Catalog.SCROLLS.items())
                for (cls in itemClasses) known[cls] = Scroll.known!!.contains(cls)
            } else {
                itemClasses = ArrayList()
            }

            Collections.sort(itemClasses) { a, b ->
                var result = 0

                //specifically known items appear first, then seen items, then unknown items.
                if (known[a] && Catalog.isSeen(a)) result -= 2
                if (known[b] && Catalog.isSeen(b)) result += 2
                if (Catalog.isSeen(a)) result--
                if (Catalog.isSeen(b)) result++

                result
            }

            var pos = 0f
            for (itemClass in itemClasses) {
                try {
                    val item = CatalogItem(itemClass.newInstance(), known[itemClass], Catalog.isSeen(itemClass))
                    item.setRect(0f, pos, width, ITEM_HEIGHT.toFloat())
                    content.add(item)
                    items.add(item)

                    pos += item.height()
                } catch (e: Exception) {
                    ShatteredPixelDungeon.reportException(e)
                }

            }

            content.setSize(width, pos)
            list!!.setSize(list!!.width(), list!!.height())
        }

        private class CatalogItem(private val item: Item, IDed: Boolean, private val seen: Boolean) : ListItem(ItemSprite(item), Messages.titleCase(item.trueName())) {

            init {

                if (!seen) {
                    icon.copy(ItemSprite(ItemSpriteSheet.WEAPON_HOLDER + currentItemIdx, null))
                    label.text("???")
                    label.hardlight(0x999999)
                } else if (!IDed) {
                    icon.copy(ItemSprite(ItemSpriteSheet.WEAPON_HOLDER + currentItemIdx, null))
                    label.hardlight(0xCCCCCC)
                }

            }

            fun onClick(x: Float, y: Float): Boolean {
                if (inside(x, y) && seen) {
                    GameScene.show(WndTitledMessage(Image(icon),
                            Messages.titleCase(item.trueName()), item.desc()))
                    return true
                } else {
                    return false
                }
            }
        }

        companion object {
            private val NUM_BUTTONS = 7

            private var currentItemIdx = 0

            private val WEAPON_IDX = 0
            private val ARMOR_IDX = 1
            private val WAND_IDX = 2
            private val RING_IDX = 3
            private val ARTIF_IDX = 4
            private val POTION_IDX = 5
            private val SCROLL_IDX = 6

            private val BUTTON_HEIGHT = 17
        }

    }

    companion object {

        private val WIDTH_P = 120
        private val HEIGHT_P = 160

        private val WIDTH_L = 160
        private val HEIGHT_L = 128

        private val ITEM_HEIGHT = 18

        var last_index = 0
    }

}
