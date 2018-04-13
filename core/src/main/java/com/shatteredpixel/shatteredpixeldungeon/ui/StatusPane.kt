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
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGame
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.NinePatch
import com.watabou.noosa.TouchArea
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.ui.Button
import com.watabou.noosa.ui.Component
import com.watabou.utils.ColorMath

class StatusPane : Component() {

    private var bg: NinePatch? = null
    private var avatar: Image? = null
    private var warning: Float = 0.toFloat()

    private var lastTier = 0

    private var rawShielding: Image? = null
    private var shieldedHP: Image? = null
    private var hp: Image? = null
    private var exp: Image? = null

    private var bossHP: BossHealthBar? = null

    private var lastLvl = -1

    private var level: BitmapText? = null
    private var depth: BitmapText? = null

    private var danger: DangerIndicator? = null
    private var buffs: BuffIndicator? = null
    private var compass: Compass? = null

    private var btnJournal: JournalButton? = null
    private var btnMenu: MenuButton? = null

    private var pickedUp: Toolbar.PickedUpItem? = null

    override fun createChildren() {

        bg = NinePatch(Assets.STATUS, 0, 0, 128, 36, 85, 0, 45, 0)
        add(bg)

        add(object : TouchArea(0f, 1f, 31f, 31f) {
            override fun onClick(touch: Touch) {
                val sprite = Dungeon.hero!!.sprite
                if (!sprite!!.isVisible) {
                    Camera.main.focusOn(sprite)
                }
                GameScene.show(WndHero())
            }
        })

        btnJournal = JournalButton()
        add(btnJournal)

        btnMenu = MenuButton()
        add(btnMenu)

        avatar = HeroSprite.avatar(Dungeon.hero!!.heroClass, lastTier)
        add(avatar)

        compass = Compass(if (Statistics.amuletObtained) Dungeon.level!!.entrance else Dungeon.level!!.exit)
        add(compass)

        rawShielding = Image(Assets.SHLD_BAR)
        rawShielding!!.alpha(0.5f)
        add(rawShielding)

        shieldedHP = Image(Assets.SHLD_BAR)
        add(shieldedHP)

        hp = Image(Assets.HP_BAR)
        add(hp)

        exp = Image(Assets.XP_BAR)
        add(exp)

        bossHP = BossHealthBar()
        add(bossHP)

        level = BitmapText(PixelScene.pixelFont)
        level!!.hardlight(0xFFEBA4)
        add(level)

        depth = BitmapText(Integer.toString(Dungeon.depth), PixelScene.pixelFont)
        depth!!.hardlight(0xCACFC2)
        depth!!.measure()
        add(depth)

        danger = DangerIndicator()
        add(danger)

        buffs = BuffIndicator(Dungeon.hero)
        add(buffs)

        add(pickedUp = Toolbar.PickedUpItem())
    }

    override fun layout() {

        height = 32f

        bg!!.size(width, bg!!.height)

        avatar!!.x = bg!!.x + 15 - avatar!!.width / 2f
        avatar!!.y = bg!!.y + 16 - avatar!!.height / 2f
        PixelScene.align(avatar!!)

        compass!!.x = avatar!!.x + avatar!!.width / 2f - compass!!.origin.x
        compass!!.y = avatar!!.y + avatar!!.height / 2f - compass!!.origin.y
        PixelScene.align(compass!!)

        rawShielding!!.x = 30f
        shieldedHP!!.x = rawShielding!!.x
        hp!!.x = shieldedHP!!.x
        rawShielding!!.y = 3f
        shieldedHP!!.y = rawShielding!!.y
        hp!!.y = shieldedHP!!.y

        bossHP!!.setPos(6 + (width - bossHP!!.width()) / 2, 20f)

        depth!!.x = width - 35.5f - depth!!.width() / 2f
        depth!!.y = 8f - depth!!.baseLine() / 2f
        PixelScene.align(depth!!)

        danger!!.setPos(width - danger!!.width(), 20f)

        buffs!!.setPos(31f, 9f)

        btnJournal!!.setPos(width - 42, 1f)

        btnMenu!!.setPos(width - btnMenu!!.width(), 1f)
    }

    override fun update() {
        super.update()

        val health = Dungeon.hero!!.HP.toFloat()
        val shield = Dungeon.hero!!.SHLD.toFloat()
        val max = Dungeon.hero!!.HT.toFloat()

        if (!Dungeon.hero!!.isAlive) {
            avatar!!.tint(0x000000, 0.5f)
        } else if (health / max < 0.3f) {
            warning += Game.elapsed * 5f * (0.4f - health / max)
            warning %= 1f
            avatar!!.tint(ColorMath.interpolate(warning, *warningColors), 0.5f)
        } else {
            avatar!!.resetColor()
        }

        hp!!.scale.x = Math.max(0f, (health - shield) / max)
        shieldedHP!!.scale.x = health / max
        rawShielding!!.scale.x = shield / max

        exp!!.scale.x = width / exp!!.width * Dungeon.hero!!.exp / Dungeon.hero!!.maxExp()

        if (Dungeon.hero!!.lvl != lastLvl) {

            if (lastLvl != -1) {
                val emitter = recycle(Emitter::class.java) as Emitter
                emitter.revive()
                emitter.pos(27f, 27f)
                emitter.burst(Speck.factory(Speck.STAR), 12)
            }

            lastLvl = Dungeon.hero!!.lvl
            level!!.text(Integer.toString(lastLvl))
            level!!.measure()
            level!!.x = 27.5f - level!!.width() / 2f
            level!!.y = 28.0f - level!!.baseLine() / 2f
            PixelScene.align(level!!)
        }

        val tier = Dungeon.hero!!.tier()
        if (tier != lastTier) {
            lastTier = tier
            avatar!!.copy(HeroSprite.avatar(Dungeon.hero!!.heroClass, tier))
        }
    }

    fun pickup(item: Item, cell: Int) {
        pickedUp!!.reset(item,
                cell,
                btnJournal!!.journalIcon!!.x + btnJournal!!.journalIcon!!.width() / 2f,
                btnJournal!!.journalIcon!!.y + btnJournal!!.journalIcon!!.height() / 2f)
    }

    fun flash() {
        btnJournal!!.flashing = true
    }

    fun updateKeys() {
        btnJournal!!.updateKeyDisplay()
    }

    private class JournalButton : Button() {

        private var bg: Image? = null
        private var journalIcon: Image? = null
        private var keyIcon: KeyDisplay? = null

        private var flashing: Boolean = false

        private var time: Float = 0.toFloat()

        init {

            width = bg!!.width + 13 //includes the depth display to the left
            height = bg!!.height + 4
        }

        override fun createChildren() {
            super.createChildren()

            bg = Image(Assets.MENU, 2, 2, 13, 11)
            add(bg)

            journalIcon = Image(Assets.MENU, 31, 0, 11, 7)
            add(journalIcon)

            keyIcon = KeyDisplay()
            add(keyIcon)
            updateKeyDisplay()
        }

        override fun layout() {
            super.layout()

            bg!!.x = x + 13
            bg!!.y = y + 2

            journalIcon!!.x = bg!!.x + (bg!!.width() - journalIcon!!.width()) / 2f
            journalIcon!!.y = bg!!.y + (bg!!.height() - journalIcon!!.height()) / 2f
            PixelScene.align(journalIcon!!)

            keyIcon!!.x = bg!!.x + 1
            keyIcon!!.y = bg!!.y + 1
            keyIcon!!.width = bg!!.width - 2
            keyIcon!!.height = bg!!.height - 2
            PixelScene.align(keyIcon!!)
        }

        override fun update() {
            super.update()

            if (flashing) {
                journalIcon!!.am = Math.abs(Math.cos((3 * (time += Game.elapsed)).toDouble())).toFloat()
                keyIcon!!.am = journalIcon!!.am
                if (time >= 0.333f * Math.PI) {
                    time = 0f
                }
            }
        }

        fun updateKeyDisplay() {
            keyIcon!!.updateKeys()
            keyIcon!!.visible = keyIcon!!.keyCount() > 0
            journalIcon!!.visible = !keyIcon!!.visible
            if (keyIcon!!.keyCount() > 0) {
                bg!!.brightness(.8f - Math.min(6, keyIcon!!.keyCount()) / 20f)
            } else {
                bg!!.resetColor()
            }
        }

        override fun onTouchDown() {
            bg!!.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK)
        }

        override fun onTouchUp() {
            if (keyIcon!!.keyCount() > 0) {
                bg!!.brightness(.8f - Math.min(6, keyIcon!!.keyCount()) / 20f)
            } else {
                bg!!.resetColor()
            }
        }

        override fun onClick() {
            flashing = false
            time = 0f
            journalIcon!!.am = 1f
            keyIcon!!.am = journalIcon!!.am
            GameScene.show(WndJournal())
        }

    }

    private class MenuButton : Button() {

        private var image: Image? = null

        init {

            width = image!!.width + 4
            height = image!!.height + 4
        }

        override fun createChildren() {
            super.createChildren()

            image = Image(Assets.MENU, 17, 2, 12, 11)
            add(image)
        }

        override fun layout() {
            super.layout()

            image!!.x = x + 2
            image!!.y = y + 2
        }

        override fun onTouchDown() {
            image!!.brightness(1.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK)
        }

        override fun onTouchUp() {
            image!!.resetColor()
        }

        override fun onClick() {
            GameScene.show(WndGame())
        }
    }

    companion object {

        private val warningColors = intArrayOf(0x660000, 0xCC0000, 0x660000)
    }
}
