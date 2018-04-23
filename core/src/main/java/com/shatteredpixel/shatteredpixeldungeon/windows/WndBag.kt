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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant.Seed
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.BitmapText
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample

class WndBag(bag: Bag, private val listener: Listener?, mode: Mode, private val title: String?) : WndTabbed() {
    private val mode: WndBag.Mode

    private val nCols: Int
    private val nRows: Int

    protected var count: Int = 0
    protected var col: Int = 0
    protected var row: Int = 0

    //FIXME this is getting cumbersome, there should be a better way to manage this
    enum class Mode {
        ALL,
        UNIDENTIFED,
        UNIDED_OR_CURSED,
        UPGRADEABLE,
        QUICKSLOT,
        FOR_SALE,
        WEAPON,
        ARMOR,
        ENCHANTABLE,
        WAND,
        SEED,
        FOOD,
        POTION,
        SCROLL,
        UNIDED_POTION_OR_SCROLL,
        EQUIPMENT,
        ALCHEMY
    }

    init {
        this.mode = mode

        lastMode = mode
        lastBag = bag

        nCols = if (SPDSettings.landscape()) COLS_L else COLS_P
        nRows = Math.ceil(((Belongings.BACKPACK_SIZE + 4) / nCols.toFloat()).toDouble()).toInt()

        val slotsWidth = SLOT_WIDTH * nCols + SLOT_MARGIN * (nCols - 1)
        val slotsHeight = SLOT_HEIGHT * nRows + SLOT_MARGIN * (nRows - 1)

        placeTitle(bag, slotsWidth)

        placeItems(bag)

        resize(slotsWidth, slotsHeight + TITLE_HEIGHT)

        val stuff = Dungeon.hero!!.belongings
        val bags = arrayOf(stuff.backpack, stuff.getItem<VelvetPouch>(VelvetPouch::class.java), stuff.getItem<ScrollHolder>(ScrollHolder::class.java), stuff.getItem<PotionBandolier>(PotionBandolier::class.java), stuff.getItem<MagicalHolster>(MagicalHolster::class.java))

        for (b in bags) {
            if (b != null) {
                val tab = BagTab(b)
                add(tab)
                tab.select(b === bag)
            }
        }

        layoutTabs()
    }

    protected fun placeTitle(bag: Bag, width: Int) {

        val txtTitle = PixelScene.renderText(
                if (title != null) Messages.titleCase(title) else Messages.titleCase(bag.name()!!), 9)
        txtTitle.hardlight(Window.TITLE_COLOR)
        txtTitle.x = 1f
        txtTitle.y = (TITLE_HEIGHT - txtTitle.baseLine()).toInt() / 2f - 1
        PixelScene.align(txtTitle)
        add(txtTitle)

        val gold = ItemSprite(ItemSpriteSheet.GOLD, null)
        gold.x = width.toFloat() - gold.width() - 1f
        gold.y = (TITLE_HEIGHT - gold.height()) / 2f - 1
        PixelScene.align(gold)
        add(gold)

        val amt = BitmapText(Integer.toString(Dungeon.gold), PixelScene.pixelFont!!)
        amt.hardlight(Window.TITLE_COLOR)
        amt.measure()
        amt.x = width.toFloat() - gold.width() - amt.width() - 2f
        amt.y = (TITLE_HEIGHT - amt.baseLine()) / 2f - 1
        PixelScene.align(amt)
        add(amt)
    }

    protected fun placeItems(container: Bag) {

        // Equipped items
        val stuff = Dungeon.hero!!.belongings
        placeItem(if (stuff.weapon != null) stuff.weapon else Placeholder(ItemSpriteSheet.WEAPON_HOLDER))
        placeItem(if (stuff.armor != null) stuff.armor else Placeholder(ItemSpriteSheet.ARMOR_HOLDER))
        placeItem(if (stuff.misc1 != null) stuff.misc1 else Placeholder(ItemSpriteSheet.RING_HOLDER))
        placeItem(if (stuff.misc2 != null) stuff.misc2 else Placeholder(ItemSpriteSheet.RING_HOLDER))

        val backpack = container === Dungeon.hero!!.belongings.backpack
        if (!backpack) {
            count = nCols
            col = 0
            row = 1
        }

        // Items in the bag
        for (item in container.items.toTypedArray<Item>()) {
            placeItem(item)
        }

        // Free Space
        while (count - (if (backpack) 4 else nCols) < container.size) {
            placeItem(null)
        }
    }

    protected fun placeItem(item: Item?) {

        val x = col * (SLOT_WIDTH + SLOT_MARGIN)
        val y = TITLE_HEIGHT + row * (SLOT_HEIGHT + SLOT_MARGIN)

        add(ItemButton(item).setPos(x.toFloat(), y.toFloat()))

        if (++col >= nCols) {
            col = 0
            row++
        }

        count++
    }

    override fun onMenuPressed() {
        if (listener == null) {
            hide()
        }
    }

    override fun onBackPressed() {
        listener?.onSelect(null)
        super.onBackPressed()
    }

    override fun onClick(tab: WndTabbed.Tab) {
        hide()
        GameScene.show(WndBag((tab as BagTab).bag, listener, mode, title))
    }

    override fun tabHeight(): Int {
        return 20
    }

    private inner class BagTab(val bag: Bag) : WndTabbed.Tab() {

        private val icon: Image

        init {

            icon = icon()
            add(icon)
        }

        public override fun select(value: Boolean) {
            super.select(value)
            icon.am = if (selected) 1.0f else 0.6f
        }

        override fun layout() {
            super.layout()

            icon.copy(icon())
            icon.x = x + (width - icon.width) / 2
            icon.y = y + (height - icon.height) / 2 - 2f - (if (selected) 0 else 1).toFloat()
            if (!selected && icon.y < y + CUT) {
                val frame = icon.frame()
                frame.top += (y + CUT - icon.y) / icon.texture!!.height
                icon.frame(frame)
                icon.y = y + CUT
            }
        }

        private fun icon(): Image {
            return if (bag is VelvetPouch) {
                Icons.get(Icons.SEED_POUCH)
            } else if (bag is ScrollHolder) {
                Icons.get(Icons.SCROLL_HOLDER)
            } else if (bag is MagicalHolster) {
                Icons.get(Icons.WAND_HOLSTER)
            } else if (bag is PotionBandolier) {
                Icons.get(Icons.POTION_BANDOLIER)
            } else {
                Icons.get(Icons.BACKPACK)
            }
        }
    }

    open class Placeholder(image: Int) : Item() {

        override val isIdentified: Boolean
            get() = true

        init {
            name = null
        }

        init {
            this.image = image
        }

        override fun isEquipped(hero: Hero): Boolean {
            return true
        }
    }

    private inner class ItemButton(private val item2: Item?) : ItemSlot(item2) {
        private var bg: ColorBlock? = null

        init {
            if (item2 is Gold) {
                bg!!.visible = false
            }

            width = SLOT_WIDTH.toFloat()
            height = SLOT_HEIGHT.toFloat()
        }

        override fun createChildren() {
            bg = ColorBlock(SLOT_WIDTH.toFloat(), SLOT_HEIGHT.toFloat(), NORMAL)
            add(bg)

            super.createChildren()
        }

        override fun layout() {
            bg!!.x = x
            bg!!.y = y

            super.layout()
        }

        override fun item(item: Item?) {

            super.item(item)
            if (item != null) {

                bg!!.texture(TextureCache.createSolid(if (item.isEquipped(Dungeon.hero!!)) EQUIPPED else NORMAL))
                if (item.cursed && item.cursedKnown) {
                    bg!!.ra = +0.3f
                    bg!!.ga = -0.15f
                } else if (!item.isIdentified) {
                    bg!!.ra = 0.2f
                    bg!!.ba = 0.2f
                }

                if (item.name() == null) {
                    enable(false)
                } else {
                    enable(
                            mode == Mode.FOR_SALE && !item.unique && item.price() > 0 && (!item.isEquipped(Dungeon.hero!!) || !item.cursed) ||
                                    mode == Mode.UPGRADEABLE && item.isUpgradable ||
                                    mode == Mode.UNIDENTIFED && !item.isIdentified ||
                                    mode == Mode.UNIDED_OR_CURSED && (item is EquipableItem || item is Wand) && (!item.isIdentified || item.cursed) ||
                                    mode == Mode.QUICKSLOT && item.defaultAction != null ||
                                    mode == Mode.WEAPON && (item is MeleeWeapon || item is Boomerang) ||
                                    mode == Mode.ARMOR && item is Armor ||
                                    mode == Mode.ENCHANTABLE && (item is MeleeWeapon || item is Boomerang || item is Armor) ||
                                    mode == Mode.WAND && item is Wand ||
                                    mode == Mode.SEED && item is Seed ||
                                    mode == Mode.FOOD && item is Food ||
                                    mode == Mode.POTION && item is Potion ||
                                    mode == Mode.SCROLL && item is Scroll ||
                                    mode == Mode.UNIDED_POTION_OR_SCROLL && !item.isIdentified && (item is Scroll || item is Potion) ||
                                    mode == Mode.EQUIPMENT && item is EquipableItem ||
                                    mode == Mode.ALCHEMY && (item is Seed && item !is BlandfruitBush.Seed || item is Blandfruit && item.potionAttrib == null || item.javaClass == Dart::class.java) ||
                                    mode == Mode.ALL
                    )
                    //extra logic for cursed weapons or armor
                    if (!active && mode == Mode.UNIDED_OR_CURSED) {
                        if (item is Weapon) {
                            val w = item as Weapon?
                            enable(w!!.hasCurseEnchant())
                        }
                        if (item is Armor) {
                            val a = item as Armor?
                            enable(a!!.hasCurseGlyph())
                        }
                    }
                }
            } else {
                bg!!.color(NORMAL)
            }
        }

        override fun onTouchDown() {
            bg!!.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }

        override fun onTouchUp() {
            bg!!.brightness(1.0f)
        }

        override fun onClick() {
            if (!lastBag!!.contains(item2) && !item2!!.isEquipped(Dungeon.hero!!)) {

                hide()

            } else if (listener != null) {

                hide()
                listener.onSelect(item2)

            } else {

                GameScene.show(WndItem(this@WndBag, item2!!))

            }
        }

        override fun onLongClick(): Boolean {
            if (listener == null && item2!!.defaultAction != null) {
                hide()
                Dungeon.quickslot.setSlot(0, item2!!)
                QuickSlotButton.refresh()
                return true
            } else {
                return false
            }
        }

    }

    interface Listener {
        fun onSelect(item: Item?)
    }

    companion object {
        private val NORMAL = -0x66aca9b3
        private val EQUIPPED = -0x666e6c74

        protected val COLS_P = 4
        protected val COLS_L = 6

        protected val SLOT_WIDTH = 28
        protected val SLOT_HEIGHT = 28
        protected val SLOT_MARGIN = 1

        protected val TITLE_HEIGHT = 14

        private var lastMode: Mode? = null
        private var lastBag: Bag? = null

        fun lastBag(listener: Listener, mode: Mode, title: String): WndBag {

            return if (mode == lastMode && lastBag != null &&
                    Dungeon.hero!!.belongings.backpack.contains(lastBag!!)) {

                WndBag(lastBag!!, listener, mode, title)

            } else {

                WndBag(Dungeon.hero!!.belongings.backpack, listener, mode, title)

            }
        }

        fun getBag(bagClass: Class<out Bag>, listener: Listener, mode: Mode, title: String): WndBag {
            val bag = Dungeon.hero!!.belongings.getItem(bagClass)
            return if (bag != null)
                WndBag(bag, listener, mode, title)
            else
                lastBag(listener, mode, title)
        }
    }
}
