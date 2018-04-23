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
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.IntroScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.ui.Button
import com.watabou.noosa.ui.Component

class WndStartGame(slot: Int) : Window() {

    init {

        Badges.loadGlobal()
        Journal.loadGlobal()

        val title = PixelScene.renderText(Messages.get(this.javaClass, "title"), 12)
        title.hardlight(Window.TITLE_COLOR)
        title.x = (WIDTH - title.width()) / 2f
        title.y = 2f
        add(title)

        val heroBtnSpacing = (WIDTH - 4 * HeroBtn.WIDTH) / 5f

        var curX = heroBtnSpacing
        for (cl in HeroClass.values()) {
            val button = HeroBtn(cl)
            button.setRect(curX, title.baseLine() + 4, HeroBtn.WIDTH.toFloat(), HeroBtn.HEIGHT.toFloat())
            curX += HeroBtn.WIDTH + heroBtnSpacing
            add(button)
        }

        val separator = ColorBlock(1f, 1f, -0xddddde)
        separator.size(WIDTH.toFloat(), 1f)
        separator.x = 0f
        separator.y = title.baseLine() + 6f + HeroBtn.HEIGHT.toFloat()
        add(separator)

        val ava = HeroPane()
        ava.setRect(20f, separator.y + 2, (WIDTH - 30).toFloat(), 80f)
        add(ava)

        val start = object : RedButton(Messages.get(this@WndStartGame.javaClass, "start")) {
            override fun onClick() {
                if (GamesInProgress.selectedClass == null) return

                super.onClick()

                GamesInProgress.curSlot = slot
                Dungeon.hero = null
                InterlevelScene.mode = InterlevelScene.Mode.DESCEND

                if (SPDSettings.intro()) {
                    SPDSettings.intro(false)
                    Game.switchScene(IntroScene::class.java)
                } else {
                    Game.switchScene(InterlevelScene::class.java)
                }
            }

            override fun update() {
                if (!visible && GamesInProgress.selectedClass != null) {
                    visible = true
                }
                super.update()
            }
        }
        start.visible = false
        start.setRect(0f, (HEIGHT - 20).toFloat(), WIDTH.toFloat(), 20f)
        add(start)

        if (Badges.isUnlocked(Badges.Badge.VICTORY)) {
            val challengeButton = object : IconButton(
                    Icons.get(if (SPDSettings.challenges() > 0) Icons.CHALLENGE_ON else Icons.CHALLENGE_OFF)) {
                override fun onClick() {
                    Game.scene()!!.add(object : WndChallenges(SPDSettings.challenges(), true) {
                        override fun onBackPressed() {
                            super.onBackPressed()
                            icon(Icons.get(if (SPDSettings.challenges() > 0)
                                Icons.CHALLENGE_ON
                            else
                                Icons.CHALLENGE_OFF))
                        }
                    })
                }

                override fun update() {
                    if (!visible && GamesInProgress.selectedClass != null) {
                        visible = true
                    }
                    super.update()
                }
            }
            challengeButton.setRect((WIDTH - 20).toFloat(), (HEIGHT - 20).toFloat(), 20f, 20f)
            challengeButton.visible = false
            add(challengeButton)

        } else {
            Dungeon.challenges = 0
            SPDSettings.challenges(0)
        }

        resize(WIDTH, HEIGHT)

    }

    private class HeroBtn internal constructor(private val cl: HeroClass) : Button() {

        private var hero: Image? = null

        init {

            if (cl == HeroClass.WARRIOR) {
                hero = Image(Assets.WARRIOR, 0, 90, 12, 15)
            } else if (cl == HeroClass.MAGE) {
                hero = Image(Assets.MAGE, 0, 90, 12, 15)
            } else if (cl == HeroClass.ROGUE) {
                hero = Image(Assets.ROGUE, 0, 90, 12, 15)
            } else if (cl == HeroClass.HUNTRESS) {
                hero = Image(Assets.HUNTRESS, 0, 90, 12, 15)
            }
            add(hero)

        }

        override fun layout() {
            super.layout()
            if (hero != null) {
                hero!!.x = x + (width - hero!!.width()) / 2f
                hero!!.y = y + (height - hero!!.height()) / 2f
                PixelScene.align(hero!!)
            }
        }

        override fun update() {
            super.update()
            if (cl != GamesInProgress.selectedClass) {
                if (cl == HeroClass.HUNTRESS && !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_3)) {
                    hero!!.brightness(0f)
                } else {
                    hero!!.brightness(0.6f)
                }
            } else {
                hero!!.brightness(1f)
            }
        }

        override fun onClick() {
            super.onClick()

            if (cl == HeroClass.HUNTRESS && !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_3)) {
                Game.scene()!!.add(
                        WndMessage(Messages.get(WndStartGame::class.java, "huntress_unlock")))
            } else {
                GamesInProgress.selectedClass = cl
            }
        }

        companion object {

            val WIDTH = 24
            val HEIGHT = 16
        }
    }

    private inner class HeroPane : Component() {

        private var cl: HeroClass? = null

        private var avatar: Image? = null

        private var heroItem: IconButton? = null
        private var heroLoadout: IconButton? = null
        private var heroMisc: IconButton? = null
        private var heroSubclass: IconButton? = null

        private var name: RenderedText? = null

        override fun createChildren() {
            super.createChildren()

            avatar = Image(Assets.AVATARS)
            avatar!!.scale.set(2f)
            add(avatar)

            heroItem = object : IconButton() {
                override fun onClick() {
                    if (cl == null) return
                    Game.scene()!!.add(WndMessage(Messages.get(cl!!.javaClass, cl!!.name + "_desc_item")))
                }
            }
            heroItem!!.setSize(BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
            add(heroItem)

            heroLoadout = object : IconButton() {
                override fun onClick() {
                    if (cl == null) return
                    Game.scene()!!.add(WndMessage(Messages.get(cl!!.javaClass, cl!!.name + "_desc_loadout")))
                }
            }
            heroLoadout!!.setSize(BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
            add(heroLoadout)

            heroMisc = object : IconButton() {
                override fun onClick() {
                    if (cl == null) return
                    Game.scene()!!.add(WndMessage(Messages.get(cl!!.javaClass, cl!!.name + "_desc_misc")))
                }
            }
            heroMisc!!.setSize(BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
            add(heroMisc)

            heroSubclass = object : IconButton(ItemSprite(ItemSpriteSheet.MASTERY, null)) {
                override fun onClick() {
                    if (cl == null) return
                    var msg = Messages.get(cl!!.javaClass, cl!!.name + "_desc_subclasses")
                    for (sub in cl!!.subClasses()) {
                        msg += "\n\n" + sub.desc()
                    }
                    Game.scene()!!.add(WndMessage(msg))
                }
            }
            heroSubclass!!.setSize(BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
            add(heroSubclass)

            name = PixelScene.renderText(12)
            add(name)

            visible = false
        }

        override fun layout() {
            super.layout()

            avatar!!.x = x
            avatar!!.y = y + (height - avatar!!.height() - name!!.baseLine() - 2f) / 2f
            PixelScene.align(avatar!!)

            name!!.x = x + (avatar!!.width() - name!!.width()) / 2f
            name!!.y = avatar!!.y + avatar!!.height() + 2f
            PixelScene.align(name!!)

            heroItem!!.setPos(x + width - BTN_SIZE, y)
            heroLoadout!!.setPos(x + width - BTN_SIZE, heroItem!!.bottom())
            heroMisc!!.setPos(x + width - BTN_SIZE, heroLoadout!!.bottom())
            heroSubclass!!.setPos(x + width - BTN_SIZE, heroMisc!!.bottom())
        }

        @Synchronized
        override fun update() {
            super.update()
            if (GamesInProgress.selectedClass != cl) {
                cl = GamesInProgress.selectedClass
                if (cl != null) {
                    avatar!!.frame(cl!!.ordinal * 24, 0, 24, 32)

                    name!!.text(Messages.capitalize(cl!!.title()))

                    when (cl) {
                        HeroClass.WARRIOR -> {
                            heroItem!!.icon(ItemSprite(ItemSpriteSheet.SEAL, null))
                            heroLoadout!!.icon(ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD, null))
                            heroMisc!!.icon(ItemSprite(ItemSpriteSheet.RATION, null))
                        }
                        HeroClass.MAGE -> {
                            heroItem!!.icon(ItemSprite(ItemSpriteSheet.MAGES_STAFF, null))
                            heroLoadout!!.icon(ItemSprite(ItemSpriteSheet.HOLDER, null))
                            heroMisc!!.icon(ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE, null))
                        }
                        HeroClass.ROGUE -> {
                            heroItem!!.icon(ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK, null))
                            heroLoadout!!.icon(ItemSprite(ItemSpriteSheet.DAGGER, null))
                            heroMisc!!.icon(Icons.get(Icons.DEPTH))
                        }
                        HeroClass.HUNTRESS -> {
                            heroItem!!.icon(ItemSprite(ItemSpriteSheet.BOOMERANG, null))
                            heroLoadout!!.icon(ItemSprite(ItemSpriteSheet.KNUCKLEDUSTER, null))
                            heroMisc!!.icon(ItemSprite(ItemSpriteSheet.DART, null))
                        }
                    }

                    layout()

                    visible = true
                } else {
                    visible = false
                }
            }
        }

    }

    companion object {

        private val BTN_SIZE = 20
        private val WIDTH = 120
        private val HEIGHT = 140
    }

}
