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
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Sample
import com.watabou.utils.DeviceCompat

class WndSettings : WndTabbed() {

    private val display: DisplayTab
    private val ui: UITab
    private val audio: AudioTab

    init {

        display = DisplayTab()
        add(display)

        ui = UITab()
        add(ui)

        audio = AudioTab()
        add(audio)

        add(object : WndTabbed.LabeledTab(Messages.get(this@WndSettings.javaClass, "display")) {
            override fun select(value: Boolean) {
                super.select(value)
                display.active = value
                display.visible = display.active
                if (value) last_index = 0
            }
        })

        add(object : WndTabbed.LabeledTab(Messages.get(this@WndSettings.javaClass, "ui")) {
            override fun select(value: Boolean) {
                super.select(value)
                ui.active = value
                ui.visible = ui.active
                if (value) last_index = 1
            }
        })

        add(object : WndTabbed.LabeledTab(Messages.get(this@WndSettings.javaClass, "audio")) {
            override fun select(value: Boolean) {
                super.select(value)
                audio.active = value
                audio.visible = audio.active
                if (value) last_index = 2
            }
        })

        resize(WIDTH, HEIGHT)

        layoutTabs()

        select(last_index)

    }

    private inner class DisplayTab : Group() {
        init {

            val scale = object : OptionSlider(Messages.get(this@DisplayTab.javaClass, "scale"),
                    Math.ceil((2 * Game.density).toDouble()).toInt().toString() + "X",
                    PixelScene.maxDefaultZoom.toString() + "X",
                    Math.ceil((2 * Game.density).toDouble()).toInt(),
                    PixelScene.maxDefaultZoom) {
                override fun onChange() {
                    if (selectedValue != SPDSettings.scale()) {
                        SPDSettings.scale(selectedValue)
                        ShatteredPixelDungeon.switchNoFade(Game.scene()!!.javaClass as Class<out PixelScene>, object : Game.SceneChangeCallback {
                            override fun beforeCreate() {
                                //do nothing
                            }

                            override fun afterCreate() {
                                Game.scene()!!.add(WndSettings())
                            }
                        })
                    }
                }
            }
            if (Math.ceil((2 * Game.density).toDouble()).toInt() < PixelScene.maxDefaultZoom) {
                scale.selectedValue = PixelScene.defaultZoom
                scale.setRect(0f, 0f, WIDTH.toFloat(), SLIDER_HEIGHT.toFloat())
                add(scale)
            }

            val chkSaver = object : CheckBox(Messages.get(this@DisplayTab.javaClass, "saver")) {
                override fun onClick() {
                    super.onClick()
                    if (checked()) {
                        checked(!checked())
                        Game.scene()!!.add(object : WndOptions(
                                Messages.get(DisplayTab::class.java, "saver"),
                                Messages.get(DisplayTab::class.java, "saver_desc"),
                                Messages.get(DisplayTab::class.java, "okay"),
                                Messages.get(DisplayTab::class.java, "cancel")) {
                            override fun onSelect(index: Int) {
                                if (index == 0) {
                                    checked(!checked())
                                    SPDSettings.powerSaver(checked())
                                }
                            }
                        })
                    } else {
                        SPDSettings.powerSaver(checked())
                    }
                }
            }
            if (PixelScene.maxScreenZoom >= 2) {
                chkSaver.setRect(0f, scale.bottom() + GAP_TINY, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
                chkSaver.checked(SPDSettings.powerSaver())
                add(chkSaver)
            }

            val btnOrientation = object : RedButton(if (SPDSettings.landscape())
                Messages.get(this.javaClass, "portrait")
            else
                Messages.get(this.javaClass, "landscape")) {
                override fun onClick() {
                    SPDSettings.landscape(!SPDSettings.landscape())
                }
            }
            btnOrientation.setRect(0f, chkSaver.bottom() + GAP_TINY, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnOrientation)


            val brightness = object : OptionSlider(Messages.get(this@DisplayTab.javaClass, "brightness"),
                    Messages.get(this.javaClass, "dark"), Messages.get(this.javaClass, "bright"), -2, 2) {
                override fun onChange() {
                    SPDSettings.brightness(selectedValue)
                }
            }
            brightness.selectedValue = SPDSettings.brightness()
            brightness.setRect(0f, btnOrientation.bottom() + GAP_LRG, WIDTH.toFloat(), SLIDER_HEIGHT.toFloat())
            add(brightness)

            val tileGrid = object : OptionSlider(Messages.get(this@DisplayTab.javaClass, "visual_grid"),
                    Messages.get(this.javaClass, "off"), Messages.get(this.javaClass, "high"), -1, 3) {
                override fun onChange() {
                    SPDSettings.visualGrid(selectedValue)
                }
            }
            tileGrid.selectedValue = SPDSettings.visualGrid()
            tileGrid.setRect(0f, brightness.bottom() + GAP_TINY, WIDTH.toFloat(), SLIDER_HEIGHT.toFloat())
            add(tileGrid)


        }
    }

    private inner class UITab : Group() {
        init {

            val barDesc = PixelScene.renderText(Messages.get(this.javaClass, "mode"), 9)
            barDesc.x = (WIDTH - barDesc.width()) / 2
            PixelScene.align(barDesc)
            add(barDesc)

            val btnSplit = object : RedButton(Messages.get(this@UITab.javaClass, "split")) {
                override fun onClick() {
                    SPDSettings.toolbarMode(Toolbar.Mode.SPLIT.name)
                    Toolbar.updateLayout()
                }
            }
            btnSplit.setRect(0f, barDesc.y + barDesc.baseLine() + GAP_TINY.toFloat(), 36f, 16f)
            add(btnSplit)

            val btnGrouped = object : RedButton(Messages.get(this@UITab.javaClass, "group")) {
                override fun onClick() {
                    SPDSettings.toolbarMode(Toolbar.Mode.GROUP.name)
                    Toolbar.updateLayout()
                }
            }
            btnGrouped.setRect(btnSplit.right() + GAP_TINY, barDesc.y + barDesc.baseLine() + GAP_TINY.toFloat(), 36f, 16f)
            add(btnGrouped)

            val btnCentered = object : RedButton(Messages.get(this@UITab.javaClass, "center")) {
                override fun onClick() {
                    SPDSettings.toolbarMode(Toolbar.Mode.CENTER.name)
                    Toolbar.updateLayout()
                }
            }
            btnCentered.setRect(btnGrouped.right() + GAP_TINY, barDesc.y + barDesc.baseLine() + GAP_TINY.toFloat(), 36f, 16f)
            add(btnCentered)

            val chkFlipToolbar = object : CheckBox(Messages.get(this@UITab.javaClass, "flip_toolbar")) {
                override fun onClick() {
                    super.onClick()
                    SPDSettings.flipToolbar(checked())
                    Toolbar.updateLayout()
                }
            }
            chkFlipToolbar.setRect(0f, btnGrouped.bottom() + GAP_TINY, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            chkFlipToolbar.checked(SPDSettings.flipToolbar())
            add(chkFlipToolbar)

            val chkFlipTags = object : CheckBox(Messages.get(this@UITab.javaClass, "flip_indicators")) {
                override fun onClick() {
                    super.onClick()
                    SPDSettings.flipTags(checked())
                    GameScene.layoutTags()
                }
            }
            chkFlipTags.setRect(0f, chkFlipToolbar.bottom() + GAP_TINY, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            chkFlipTags.checked(SPDSettings.flipTags())
            add(chkFlipTags)

            val slots = object : OptionSlider(Messages.get(this@UITab.javaClass, "quickslots"), "0", "4", 0, 4) {
                override fun onChange() {
                    SPDSettings.quickSlots(selectedValue)
                    Toolbar.updateLayout()
                }
            }
            slots.selectedValue = SPDSettings.quickSlots()
            slots.setRect(0f, chkFlipTags.bottom() + GAP_TINY, WIDTH.toFloat(), SLIDER_HEIGHT.toFloat())
            add(slots)

            val chkImmersive = object : CheckBox(Messages.get(this@UITab.javaClass, "nav_bar")) {
                override fun onClick() {
                    super.onClick()
                    SPDSettings.fullscreen(checked())
                }
            }
            chkImmersive.setRect(0f, slots.bottom() + GAP_SML, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            chkImmersive.checked(SPDSettings.fullscreen())
            chkImmersive.enable(DeviceCompat.supportsFullScreen())
            add(chkImmersive)

            val chkFont = object : CheckBox(Messages.get(this@UITab.javaClass, "system_font")) {
                override fun onClick() {
                    super.onClick()
                    ShatteredPixelDungeon.switchNoFade(Game.scene()!!.javaClass as Class<out PixelScene>, object : Game.SceneChangeCallback {
                        override fun beforeCreate() {
                            SPDSettings.systemFont(checked())
                        }

                        override fun afterCreate() {
                            Game.scene()!!.add(WndSettings())
                        }
                    })
                }
            }
            chkFont.setRect(0f, chkImmersive.bottom() + GAP_TINY, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            chkFont.checked(SPDSettings.systemFont())
            add(chkFont)
        }

    }

    private inner class AudioTab : Group() {
        init {
            val musicVol = object : OptionSlider(Messages.get(this@AudioTab.javaClass, "music_vol"), "0", "10", 0, 10) {
                override fun onChange() {
                    SPDSettings.musicVol(selectedValue)
                }
            }
            musicVol.selectedValue = SPDSettings.musicVol()
            musicVol.setRect(0f, 0f, WIDTH.toFloat(), SLIDER_HEIGHT.toFloat())
            add(musicVol)

            val musicMute = object : CheckBox(Messages.get(this@AudioTab.javaClass, "music_mute")) {
                override fun onClick() {
                    super.onClick()
                    SPDSettings.music(!checked())
                }
            }
            musicMute.setRect(0f, musicVol.bottom() + GAP_TINY, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            musicMute.checked(!SPDSettings.music())
            add(musicMute)


            val SFXVol = object : OptionSlider(Messages.get(this@AudioTab.javaClass, "sfx_vol"), "0", "10", 0, 10) {
                override fun onChange() {
                    SPDSettings.SFXVol(selectedValue)
                }
            }
            SFXVol.selectedValue = SPDSettings.SFXVol()
            SFXVol.setRect(0f, musicMute.bottom() + GAP_LRG, WIDTH.toFloat(), SLIDER_HEIGHT.toFloat())
            add(SFXVol)

            val btnSound = object : CheckBox(Messages.get(this@AudioTab.javaClass, "sfx_mute")) {
                override fun onClick() {
                    super.onClick()
                    SPDSettings.soundFx(!checked())
                    Sample.INSTANCE.play(Assets.SND_CLICK)
                }
            }
            btnSound.setRect(0f, SFXVol.bottom() + GAP_TINY, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            btnSound.checked(!SPDSettings.soundFx())
            add(btnSound)

            resize(WIDTH, btnSound.bottom().toInt())
        }

    }

    companion object {

        private val WIDTH = 112
        private val HEIGHT = 138
        private val SLIDER_HEIGHT = 24
        private val BTN_HEIGHT = 18
        private val GAP_TINY = 2
        private val GAP_SML = 6
        private val GAP_LRG = 18

        private var last_index = 0
    }
}
