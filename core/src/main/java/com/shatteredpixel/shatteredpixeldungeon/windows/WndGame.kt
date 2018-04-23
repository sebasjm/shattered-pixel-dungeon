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
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Game

import java.io.IOException

class WndGame : Window() {

    private var pos: Int = 0

    init {

        addButton(object : RedButton(Messages.get(this@WndGame.javaClass, "settings")) {
            override fun onClick() {
                hide()
                GameScene.show(WndSettings())
            }
        })

        // Challenges window
        if (Dungeon.challenges > 0) {
            addButton(object : RedButton(Messages.get(this@WndGame.javaClass, "challenges")) {
                override fun onClick() {
                    hide()
                    GameScene.show(WndChallenges(Dungeon.challenges, false))
                }
            })
        }

        // Restart
        if (!Dungeon.hero!!.isAlive) {

            val btnStart: RedButton
            btnStart = object : RedButton(Messages.get(this@WndGame.javaClass, "start")) {
                override fun onClick() {
                    GamesInProgress.selectedClass = Dungeon.hero!!.heroClass
                    InterlevelScene.noStory = true
                    GameScene.show(WndStartGame(GamesInProgress.firstEmpty()))
                }
            }
            addButton(btnStart)
            btnStart.textColor(Window.TITLE_COLOR)

            addButton(object : RedButton(Messages.get(this@WndGame.javaClass, "rankings")) {
                override fun onClick() {
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND
                    Game.switchScene(RankingsScene::class.java)
                }
            })
        }

        addButtons(
                // Main menu
                object : RedButton(Messages.get(this@WndGame.javaClass, "menu")) {
                    override fun onClick() {
                        try {
                            Dungeon.saveAll()
                        } catch (e: IOException) {
                            Game.reportException(e)
                        }

                        Game.switchScene(TitleScene::class.java)
                    }
                },
                // Quit
                object : RedButton(Messages.get(this@WndGame.javaClass, "exit")) {
                    override fun onClick() {
                        try {
                            Dungeon.saveAll()
                        } catch (e: IOException) {
                            Game.reportException(e)
                        }

                        Game.instance!!.finish()
                    }
                }
        )

        // Cancel
        addButton(object : RedButton(Messages.get(this@WndGame.javaClass, "return")) {
            override fun onClick() {
                hide()
            }
        })

        resize(WIDTH, pos)
    }

    private fun addButton(btn: RedButton) {
        add(btn)
        btn.setRect(0f, (if (pos > 0) {pos += GAP; pos} else 0).toFloat(), WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        pos += BTN_HEIGHT
    }

    private fun addButtons(btn1: RedButton, btn2: RedButton) {
        add(btn1)
        btn1.setRect(0f, (if (pos > 0) {pos += GAP; pos} else 0).toFloat(), ((WIDTH - GAP) / 2).toFloat(), BTN_HEIGHT.toFloat())
        add(btn2)
        btn2.setRect(btn1.right() + GAP, btn1.top(), WIDTH.toFloat() - btn1.right() - GAP.toFloat(), BTN_HEIGHT.toFloat())
        pos += BTN_HEIGHT
    }

    companion object {

        private val WIDTH = 120
        private val BTN_HEIGHT = 20
        private val GAP = 2
    }
}
