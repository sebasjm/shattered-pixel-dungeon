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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Blending
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.NoosaScriptNoLighting
import com.watabou.noosa.RenderedText
import com.watabou.noosa.SkinnedBlock
import com.watabou.noosa.audio.Sample

import java.io.FileNotFoundException
import java.io.IOException

class InterlevelScene : PixelScene() {
    private var phase: Phase? = null
    private var timeLeft: Float = 0.toFloat()

    private var message: RenderedText? = null
    private var waitingTime: Float = 0.toFloat()

    enum class Mode {
        DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, RESET, NONE
    }

    private enum class Phase {
        FADE_IN, STATIC, FADE_OUT
    }

    override fun create() {
        super.create()

        val loadingAsset: String
        val loadingDepth: Int
        val scrollSpeed: Float
        when (mode) {
            InterlevelScene.Mode.CONTINUE -> {
                loadingDepth = GamesInProgress.check(GamesInProgress.curSlot)!!.depth
                scrollSpeed = 5f
            }
            InterlevelScene.Mode.DESCEND -> {
                if (Dungeon.hero!! == null)
                    loadingDepth = 1
                else
                    loadingDepth = Dungeon.depth + 1
                scrollSpeed = 5f
            }
            InterlevelScene.Mode.FALL -> {
                loadingDepth = Dungeon.depth + 1
                scrollSpeed = 100f
            }
            InterlevelScene.Mode.ASCEND -> {
                loadingDepth = Dungeon.depth - 1
                scrollSpeed = -5f
            }
            InterlevelScene.Mode.RETURN -> {
                loadingDepth = returnDepth
                scrollSpeed = (if (returnDepth > Dungeon.depth) 15 else -15).toFloat()
            }
            else -> {
                loadingDepth = Dungeon.depth
                scrollSpeed = 0f
            }
        }
        if (loadingDepth <= 5)
            loadingAsset = Assets.LOADING_SEWERS
        else if (loadingDepth <= 10)
            loadingAsset = Assets.LOADING_PRISON
        else if (loadingDepth <= 15)
            loadingAsset = Assets.LOADING_CAVES
        else if (loadingDepth <= 21)
            loadingAsset = Assets.LOADING_CITY
        else
            loadingAsset = Assets.LOADING_HALLS

        val bg = object : SkinnedBlock(Camera.main!!.width.toFloat(), Camera.main!!.height.toFloat(), loadingAsset) {
            override fun script(): NoosaScript {
                return NoosaScriptNoLighting.get()
            }

            override fun draw() {
                Blending.disable()
                super.draw()
                Blending.enable()
            }

            override fun update() {
                super.update()
                offset(0f, Game.elapsed * scrollSpeed)
            }
        }
        bg.scale(4f, 4f)
        add(bg)

        val im = object : Image(TextureCache.createGradient(-0x56000000, -0x45000000, -0x34000000, -0x23000000, -0x1000000)) {
            override fun update() {
                super.update()
                if (phase == Phase.FADE_IN)
                    aa = Math.max(0f, timeLeft - 0.6f)
                else if (phase == Phase.FADE_OUT)
                    aa = Math.max(0f, 0.4f - timeLeft)
                else
                    aa = 0f
            }
        }
        im.angle = 90f
        im.x = Camera.main!!.width.toFloat()
        im.scale.x = Camera.main!!.height / 5f
        im.scale.y = Camera.main!!.width.toFloat()
        add(im)

        val text = Messages.get(Mode::class.java, mode!!.name)

        message = PixelScene.renderText(text, 9)
        message!!.x = (Camera.main!!.width - message!!.width()) / 2
        message!!.y = (Camera.main!!.height - message!!.height()) / 2
        PixelScene.align(message!!)
        add(message)

        phase = Phase.FADE_IN
        timeLeft = TIME_TO_FADE

        if (thread == null) {
            thread = object : Thread() {
                override fun run() {

                    try {

                        when (mode) {
                            InterlevelScene.Mode.DESCEND -> descend()
                            InterlevelScene.Mode.ASCEND -> ascend()
                            InterlevelScene.Mode.CONTINUE -> restore()
                            InterlevelScene.Mode.RESURRECT -> resurrect()
                            InterlevelScene.Mode.RETURN -> returnTo()
                            InterlevelScene.Mode.FALL -> fall()
                            InterlevelScene.Mode.RESET -> reset()
                        }

                        if (Dungeon.depth % 5 == 0) {
                            Sample.INSTANCE.load(Assets.SND_BOSS)
                        }

                    } catch (e: Exception) {

                        error = e

                    }

                    if (phase == Phase.STATIC && error == null) {
                        phase = Phase.FADE_OUT
                        timeLeft = TIME_TO_FADE
                    }
                }
            }
            thread!!.start()
        }
        waitingTime = 0f
    }

    override fun update() {
        super.update()

        waitingTime += Game.elapsed

        val p = timeLeft / TIME_TO_FADE

        when (phase) {

            InterlevelScene.Phase.FADE_IN -> {
                message!!.alpha(1 - p)
                timeLeft -= Game.elapsed
                if (timeLeft <= 0) {
                    if (!thread!!.isAlive && error == null) {
                        phase = Phase.FADE_OUT
                        timeLeft = TIME_TO_FADE
                    } else {
                        phase = Phase.STATIC
                    }
                }
            }

            InterlevelScene.Phase.FADE_OUT -> {
                message!!.alpha(p)

                timeLeft -= Game.elapsed
                if (timeLeft <= 0) {
                    Game.switchScene(GameScene::class.java)
                    thread = null
                    error = null
                }
            }

            InterlevelScene.Phase.STATIC -> if (error != null) {
                val errorMsg: String
                if (error is FileNotFoundException)
                    errorMsg = Messages.get(this.javaClass, "file_not_found")
                else if (error is IOException)
                    errorMsg = Messages.get(this.javaClass, "io_error")
                else if (error!!.message != null && error!!.message == "old save")
                    errorMsg = Messages.get(this.javaClass, "io_error")
                else
                    throw RuntimeException("fatal error occured while moving between floors", error)

                add(object : WndError(errorMsg) {
                    override fun onBackPressed() {
                        super.onBackPressed()
                        Game.switchScene(StartScene::class.java)
                    }
                })
                thread = null
                error = null
            } else if (waitingTime.toInt() == 10) {
                waitingTime = 11f
                var s = ""
                for (t in thread!!.stackTrace) {
                    s += "\n"
                    s += t.toString()
                }
                Game.reportException(
                        RuntimeException("waited more than 10 seconds on levelgen. " +
                                "Seed:" + Dungeon.seed + " depth:" + Dungeon.depth + " trace:" +
                                s)
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun descend() {

        Actor.fixTime()

        if (Dungeon.hero!! == null) {
            DriedRose.clearHeldGhostHero()
            Dungeon.init()
            if (noStory) {
                Dungeon.chapters.add(WndStory.ID_SEWERS)
                noStory = false
            }
            GameLog.wipe()
        } else {
            DriedRose.holdGhostHero(Dungeon.level!!)
            Dungeon.saveAll()
        }

        val level: Level
        if (Dungeon.depth >= Statistics.deepestFloor) {
            level = Dungeon.newLevel()
        } else {
            Dungeon.depth++
            level = Dungeon.loadLevel(GamesInProgress.curSlot)
        }
        Dungeon.switchLevel(level, level.entrance)
    }

    @Throws(IOException::class)
    private fun fall() {

        Actor.fixTime()
        DriedRose.holdGhostHero(Dungeon.level!!)

        Dungeon.saveAll()

        val level: Level
        if (Dungeon.depth >= Statistics.deepestFloor) {
            level = Dungeon.newLevel()
        } else {
            Dungeon.depth++
            level = Dungeon.loadLevel(GamesInProgress.curSlot)
        }
        Dungeon.switchLevel(level, level.fallCell(fallIntoPit))
    }

    @Throws(IOException::class)
    private fun ascend() {

        Actor.fixTime()
        DriedRose.holdGhostHero(Dungeon.level!!)

        Dungeon.saveAll()
        Dungeon.depth--
        val level = Dungeon.loadLevel(GamesInProgress.curSlot)
        Dungeon.switchLevel(level, level.exit)
    }

    @Throws(IOException::class)
    private fun returnTo() {

        Actor.fixTime()
        DriedRose.holdGhostHero(Dungeon.level!!)

        Dungeon.saveAll()
        Dungeon.depth = returnDepth
        val level = Dungeon.loadLevel(GamesInProgress.curSlot)
        Dungeon.switchLevel(level, returnPos)
    }

    @Throws(IOException::class)
    private fun restore() {

        Actor.fixTime()
        DriedRose.clearHeldGhostHero()

        GameLog.wipe()

        Dungeon.loadGame(GamesInProgress.curSlot)
        if (Dungeon.depth == -1) {
            Dungeon.depth = Statistics.deepestFloor
            Dungeon.switchLevel(Dungeon.loadLevel(GamesInProgress.curSlot), -1)
        } else {
            val level = Dungeon.loadLevel(GamesInProgress.curSlot)
            Dungeon.switchLevel(level, Dungeon.hero!!.pos)
        }
    }

    @Throws(IOException::class)
    private fun resurrect() {

        Actor.fixTime()
        DriedRose.holdGhostHero(Dungeon.level!!)

        if (Dungeon.level!!.locked) {
            Dungeon.hero!!.resurrect(Dungeon.depth)
            Dungeon.depth--
            val level = Dungeon.newLevel()
            Dungeon.switchLevel(level, level.entrance)
        } else {
            Dungeon.hero!!.resurrect(-1)
            Dungeon.resetLevel()
        }
    }

    @Throws(IOException::class)
    private fun reset() {

        Actor.fixTime()
        DriedRose.holdGhostHero(Dungeon.level!!)

        SpecialRoom.resetPitRoom(Dungeon.depth + 1)

        Dungeon.depth--
        val level = Dungeon.newLevel()
        Dungeon.switchLevel(level, level.entrance)
    }

    override fun onBackPressed() {
        //Do nothing
    }

    companion object {

        private val TIME_TO_FADE = 1f
        var mode: Mode? = null

        var returnDepth: Int = 0
        var returnPos: Int = 0

        var noStory = false

        var fallIntoPit: Boolean = false

        private var thread: Thread? = null
        private var error: Exception? = null
    }
}
