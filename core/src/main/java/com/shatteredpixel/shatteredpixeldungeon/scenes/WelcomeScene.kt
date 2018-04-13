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

package com.shatteredpixel.shatteredpixeldungeon.scenes

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.Rankings
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStartGame
import com.watabou.glwrap.Blending
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.FileUtils

class WelcomeScene : PixelScene() {

    override fun create() {
        super.create()

        val previousVersion = SPDSettings.version()

        if (ShatteredPixelDungeon.versionCode == previousVersion) {
            ShatteredPixelDungeon.switchNoFade(TitleScene::class.java)
            return
        }

        PixelScene.uiCamera.visible = false

        val w = Camera.main.width
        val h = Camera.main.height

        val title = BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON)
        title.brightness(0.6f)
        add(title)

        val topRegion = Math.max(95f, h * 0.45f)

        title.x = (w - title.width()) / 2f
        if (SPDSettings.landscape())
            title.y = (topRegion - title.height()) / 2f
        else
            title.y = 16 + (topRegion - title.height() - 16f) / 2f

        PixelScene.align(title)

        val signs = object : Image(BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON_SIGNS)) {
            private var time = 0f
            override fun update() {
                super.update()
                am = Math.max(0f, Math.sin((time += Game.elapsed).toDouble()).toFloat())
                if (time >= 1.5f * Math.PI) time = 0f
            }

            override fun draw() {
                Blending.setLightMode()
                super.draw()
                Blending.setNormalMode()
            }
        }
        signs.x = title.x + (title.width() - signs.width()) / 2f
        signs.y = title.y
        add(signs)

        val okay = object : DarkRedButton(Messages.get(this, "continue")) {
            override fun onClick() {
                super.onClick()
                if (previousVersion == 0) {
                    SPDSettings.version(ShatteredPixelDungeon.versionCode)
                    this@WelcomeScene.add(WndStartGame(1))
                } else {
                    updateVersion(previousVersion)
                    ShatteredPixelDungeon.switchScene(TitleScene::class.java)
                }
            }
        }

        if (previousVersion != 0) {
            val changes = object : DarkRedButton(Messages.get(this, "changelist")) {
                override fun onClick() {
                    super.onClick()
                    updateVersion(previousVersion)
                    ShatteredPixelDungeon.switchScene(ChangesScene::class.java)
                }
            }
            okay.setRect(title.x, (h - 20).toFloat(), title.width() / 2 - 2, 16f)
            okay.textColor(0xBBBB33)
            add(okay)

            changes.setRect(okay.right() + 2, (h - 20).toFloat(), title.width() / 2 - 2, 16f)
            changes.textColor(0xBBBB33)
            add(changes)
        } else {
            okay.setRect(title.x, (h - 20).toFloat(), title.width(), 16f)
            okay.textColor(0xBBBB33)
            add(okay)
        }

        val text = PixelScene.renderMultiline(6)
        var message: String
        if (previousVersion == 0) {
            message = Messages.get(this, "welcome_msg")
        } else if (previousVersion <= ShatteredPixelDungeon.versionCode) {
            if (previousVersion < LATEST_UPDATE) {
                message = Messages.get(this, "update_intro")
                message += "\n\n" + Messages.get(this, "update_msg")
            } else {
                //TODO: change the messages here in accordance with the type of patch.
                message = Messages.get(this, "patch_intro")
                message += "\n\n" + Messages.get(this, "patch_bugfixes")
                message += "\n" + Messages.get(this, "patch_translations")
                message += "\n" + Messages.get(this, "patch_balance")

            }
        } else {
            message = Messages.get(this, "what_msg")
        }
        text.text(message, w - 20)
        val textSpace = h.toFloat() - title.y - (title.height() - 10) - okay.height() - 2f
        text.setPos((w - text.width()) / 2f, title.y + (title.height() - 10) + (textSpace - text.height()) / 2)
        add(text)

    }

    private fun updateVersion(previousVersion: Int) {

        //update rankings, to update any data which may be outdated
        if (previousVersion < LATEST_UPDATE) {
            try {
                Rankings.INSTANCE.load()
                Rankings.INSTANCE.save()
            } catch (e: Exception) {
                //if we encounter a fatal error, then just clear the rankings
                FileUtils.deleteFile(Rankings.RANKINGS_FILE)
            }

        }

        //convert game saves from the old format
        if (previousVersion <= ShatteredPixelDungeon.v0_6_2e) {
            //old save file names for warrior, mage, rogue, huntress
            val classes = arrayOf("warrior", "mage", "game", "ranger")
            for (i in 1..classes.size) {
                var name = classes[i - 1]
                if (FileUtils.fileExists("$name.dat")) {
                    try {
                        var gamedata = FileUtils.bundleFromFile("$name.dat")
                        FileUtils.bundleToFile(GamesInProgress.gameFile(i), gamedata)
                        FileUtils.deleteFile("$name.dat")

                        //rogue's safe files have a different name
                        if (name == "game") name = "depth"

                        var depth = 1
                        while (FileUtils.fileExists("$name$depth.dat")) {
                            gamedata = FileUtils.bundleFromFile("$name$depth.dat")
                            FileUtils.bundleToFile(GamesInProgress.depthFile(i, depth), gamedata)
                            FileUtils.deleteFile("$name$depth.dat")
                            depth++
                        }
                    } catch (e: Exception) {
                    }

                }
            }
        }

        //remove changed badges
        if (previousVersion <= ShatteredPixelDungeon.v0_6_0b) {
            Badges.disown(Badges.Badge.ALL_WANDS_IDENTIFIED)
            Badges.disown(Badges.Badge.ALL_RINGS_IDENTIFIED)
            Badges.disown(Badges.Badge.ALL_SCROLLS_IDENTIFIED)
            Badges.disown(Badges.Badge.ALL_POTIONS_IDENTIFIED)
            Badges.disown(Badges.Badge.ALL_ITEMS_IDENTIFIED)
            Badges.saveGlobal()
        }

        SPDSettings.version(ShatteredPixelDungeon.versionCode)
    }

    private fun placeTorch(x: Float, y: Float) {
        val fb = Fireball()
        fb.setPos(x, y)
        add(fb)
    }

    private open inner class DarkRedButton internal constructor(text: String) : RedButton(text) {
        init {
            bg!!.brightness(0.4f)
        }

        override fun onTouchDown() {
            bg!!.brightness(0.5f)
            Sample.INSTANCE.play(Assets.SND_CLICK)
        }

        override fun onTouchUp() {
            super.onTouchUp()
            bg!!.brightness(0.4f)
        }
    }

    companion object {

        private val LATEST_UPDATE = ShatteredPixelDungeon.v0_6_4
    }
}
