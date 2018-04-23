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
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Button

open class ItemSlot() : Button() {

    var icon: ItemSprite? = null
    protected var item: Item? = null
    protected var topLeft: BitmapText? = null
    protected var topRight: BitmapText? = null
    protected var bottomRight: BitmapText? = null
    protected var bottomRightIcon: Image? = null
    protected var iconVisible = true

    init {
        icon!!.visible(false)
        enable(false)
    }

    constructor(item: Item?) : this() {
        item(item)
    }

    override fun createChildren() {

        super.createChildren()

        icon = ItemSprite()
        add(icon!!)

        topLeft = BitmapText(PixelScene.pixelFont!!)
        add(topLeft!!)

        topRight = BitmapText(PixelScene.pixelFont!!)
        add(topRight!!)

        bottomRight = BitmapText(PixelScene.pixelFont!!)
        add(bottomRight!!)
    }

    override fun layout() {
        super.layout()

        icon!!.x = x + (width - icon!!.width) / 2f
        icon!!.y = y + (height - icon!!.height) / 2f
        PixelScene.align(icon!!)

        if (topLeft != null) {
            topLeft!!.measure()
            if (topLeft!!.width > width) {
                topLeft!!.scale.set(PixelScene.align(0.8f))
            } else {
                topLeft!!.scale.set(1f)
            }
            topLeft!!.x = x
            topLeft!!.y = y
            PixelScene.align(topLeft!!)
        }

        if (topRight != null) {
            topRight!!.x = x + (width - topRight!!.width())
            topRight!!.y = y
            PixelScene.align(topRight!!)
        }

        if (bottomRight != null) {
            bottomRight!!.x = x + (width - bottomRight!!.width())
            bottomRight!!.y = y + (height - bottomRight!!.height())
            PixelScene.align(bottomRight!!)
        }

        if (bottomRightIcon != null) {
            bottomRightIcon!!.x = x + (width - bottomRightIcon!!.width()) - 1
            bottomRightIcon!!.y = y + (height - bottomRightIcon!!.height())
            PixelScene.align(bottomRightIcon!!)
        }
    }

    open fun item(item: Item?) {
        if (this.item === item) {
            if (item != null) {
                icon!!.frame(item.image())
                icon!!.glow(item.glowing())
            }
            updateText()
            return
        }

        this.item = item

        if (item == null) {

            enable(false)
            icon!!.visible(false)

            updateText()

        } else {

            enable(true)
            icon!!.visible(true)

            icon!!.view(item)
            updateText()
        }
    }

    private fun updateText() {

        if (bottomRightIcon != null) {
            remove(bottomRightIcon!!)
            bottomRightIcon = null
        }

        if (item == null) {
            bottomRight!!.visible = false
            topRight!!.visible = bottomRight!!.visible
            topLeft!!.visible = topRight!!.visible
            return
        } else {
            bottomRight!!.visible = true
            topRight!!.visible = bottomRight!!.visible
            topLeft!!.visible = topRight!!.visible
        }

        topLeft!!.text(item!!.status())

        val isArmor = item is Armor
        val isWeapon = item is Weapon
        if (isArmor || isWeapon) {

            if (item!!.levelKnown || isWeapon && item !is MeleeWeapon) {

                val str = if (isArmor) (item as Armor).STRReq() else (item as Weapon).STRReq()
                topRight!!.text(Messages.format(TXT_STRENGTH, str))
                if (str > Dungeon.hero!!.STR()) {
                    topRight!!.hardlight(DEGRADED)
                } else {
                    topRight!!.resetColor()
                }

            } else {

                topRight!!.text(Messages.format(TXT_TYPICAL_STR, if (isArmor)
                    (item as Armor).STRReq(0)
                else
                    (item as Weapon).STRReq(0)))
                topRight!!.hardlight(WARNING)

            }
            topRight!!.measure()

        } else if (item is Key && item !is SkeletonKey) {
            topRight!!.text(Messages.format(TXT_KEY_DEPTH, (item as Key).depth))
            topRight!!.measure()
        } else {

            topRight!!.text(null)

        }

        val level = item!!.visiblyUpgraded()

        if (level != 0) {
            bottomRight!!.text(if (item!!.levelKnown) Messages.format(TXT_LEVEL, level) else TXT_CURSED)
            bottomRight!!.measure()
            bottomRight!!.hardlight(if (level > 0) UPGRADED else DEGRADED)
        } else if (item is Scroll || item is Potion) {
            bottomRight!!.text(null)

            val iconInt: Int?
            if (item is Scroll) {
                iconInt = (item as Scroll).initials()
            } else {
                iconInt = (item as Potion).initials()
            }
            if (iconInt != null && iconVisible) {
                bottomRightIcon = Image(Assets.CONS_ICONS)
                val left = iconInt * 7
                val top = if (item is Potion) 0 else 8
                bottomRightIcon!!.frame(left, top, 7, 8)
                add(bottomRightIcon!!)
            }

        } else {
            bottomRight!!.text(null)
        }

        layout()
    }

    fun enable(value: Boolean) {

        active = value

        val alpha = if (value) ENABLED else DISABLED
        icon!!.alpha(alpha)
        topLeft!!.alpha(alpha)
        topRight!!.alpha(alpha)
        bottomRight!!.alpha(alpha)
        if (bottomRightIcon != null) bottomRightIcon!!.alpha(alpha)
    }

    fun showParams(TL: Boolean, TR: Boolean, BR: Boolean) {
        if (TL)
            add(topLeft!!)
        else
            remove(topLeft!!)

        if (TR)
            add(topRight!!)
        else
            remove(topRight!!)

        if (BR)
            add(bottomRight!!)
        else
            remove(bottomRight!!)
        iconVisible = BR
    }

    companion object {

        val DEGRADED = 0xFF4444
        val UPGRADED = 0x44FF44
        val FADED = 0x999999
        val WARNING = 0xFF8800

        private val ENABLED = 1.0f
        private val DISABLED = 0.3f

        private val TXT_STRENGTH = ":%d"
        private val TXT_TYPICAL_STR = "%d?"
        private val TXT_KEY_DEPTH = "\u007F%d"

        private val TXT_LEVEL = "%+d"
        private val TXT_CURSED = ""//"-";

        // Special "virtual items"
        val CHEST: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.CHEST
            }
        }
        val LOCKED_CHEST: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.LOCKED_CHEST
            }
        }
        val CRYSTAL_CHEST: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.CRYSTAL_CHEST
            }
        }
        val TOMB: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.TOMB
            }
        }
        val SKELETON: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.BONES
            }
        }
        val REMAINS: Item = object : Item() {
            override fun image(): Int {
                return ItemSpriteSheet.REMAINS
            }
        }
    }
}
