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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Matrix
import com.watabou.glwrap.Quad
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.TouchArea
import com.watabou.noosa.Visual
import com.watabou.noosa.audio.Music
import com.watabou.utils.Point
import com.watabou.utils.Random

import java.nio.FloatBuffer
import java.util.Calendar

class SurfaceScene : PixelScene() {

    private var viewport: Camera? = null
    override fun create() {

        super.create()

        Music.INSTANCE.play(Assets.HAPPY, true)

        PixelScene.uiCamera.visible = false

        val w = Camera.main.width
        val h = Camera.main.height

        val archs = Archs()
        archs.reversed = true
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)

        val vx = PixelScene.align((w - SKY_WIDTH) / 2f)
        val vy = PixelScene.align((h - SKY_HEIGHT - BUTTON_HEIGHT) / 2f)

        val s = Camera.main.cameraToScreen(vx, vy)
        viewport = Camera(s.x, s.y, SKY_WIDTH, SKY_HEIGHT, PixelScene.defaultZoom.toFloat())
        Camera.add(viewport)

        val window = Group()
        window.camera = viewport
        add(window)

        val dayTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 7

        val sky = Sky(dayTime)
        sky.scale.set(SKY_WIDTH.toFloat(), SKY_HEIGHT.toFloat())
        window.add(sky)

        if (!dayTime) {
            for (i in 0 until NSTARS) {
                val size = Random.Float()
                val star = ColorBlock(size, size, -0x1)
                star.x = Random.Float(SKY_WIDTH.toFloat()) - size / 2
                star.y = Random.Float(SKY_HEIGHT.toFloat()) - size / 2
                star.am = size * (1 - star.y / SKY_HEIGHT)
                window.add(star)
            }
        }

        val range = (SKY_HEIGHT * 2 / 3).toFloat()
        for (i in 0 until NCLOUDS) {
            val cloud = Cloud((NCLOUDS - 1 - i) * (range / NCLOUDS) + Random.Float(range / NCLOUDS), dayTime)
            window.add(cloud)
        }

        val nPatches = (sky.width() / GrassPatch.WIDTH + 1).toInt()

        for (i in 0 until nPatches * 4) {
            val patch = GrassPatch((i - 0.75f) * GrassPatch.WIDTH / 4, (SKY_HEIGHT + 1).toFloat(), dayTime)
            patch.brightness(if (dayTime) 0.7f else 0.4f)
            window.add(patch)
        }

        val a = Avatar(Dungeon.hero!!.heroClass)
        // Removing semitransparent contour
        a.am = 2f
        a.aa = -1f
        a.x = (SKY_WIDTH - a.width) / 2
        a.y = SKY_HEIGHT - a.height
        PixelScene.align(a)
        window.add(a)

        val pet = Pet()
        pet.bm = 1.2f
        pet.gm = pet.bm
        pet.rm = pet.gm
        pet.x = (SKY_WIDTH / 2 + 2).toFloat()
        pet.y = SKY_HEIGHT - pet.height
        PixelScene.align(pet)
        window.add(pet)

        window.add(object : TouchArea(sky) {
            override fun onClick(touch: Touch) {
                pet.jump()
            }
        })

        for (i in 0 until nPatches) {
            val patch = GrassPatch((i - 0.5f) * GrassPatch.WIDTH, SKY_HEIGHT.toFloat(), dayTime)
            patch.brightness(if (dayTime) 1.0f else 0.8f)
            window.add(patch)
        }

        val frame = Image(Assets.SURFACE)

        frame.frame(0, 0, FRAME_WIDTH, FRAME_HEIGHT)
        frame.x = vx - FRAME_MARGIN_X
        frame.y = vy - FRAME_MARGIN_TOP
        add(frame)

        if (dayTime) {
            a.brightness(1.2f)
            pet.brightness(1.2f)
        } else {
            frame.hardlight(0xDDEEFF)
        }

        val gameOver = object : RedButton(Messages.get(this, "exit")) {
            override fun onClick() {
                Game.switchScene(RankingsScene::class.java)
            }
        }
        gameOver.setSize((SKY_WIDTH - FRAME_MARGIN_X * 2).toFloat(), BUTTON_HEIGHT.toFloat())
        gameOver.setPos(frame.x + FRAME_MARGIN_X * 2, frame.y + frame.height + 4f)
        add(gameOver)

        Badges.validateHappyEnd()

        fadeIn()
    }

    override fun destroy() {
        Badges.saveGlobal()

        Camera.remove(viewport)
        super.destroy()
    }

    override fun onBackPressed() {}

    private class Sky(dayTime: Boolean) : Visual(0, 0, 1, 1) {

        private val texture: SmartTexture
        private val verticesBuffer: FloatBuffer

        init {

            texture = TextureCache.createGradient(*if (dayTime) day else night)

            val vertices = FloatArray(16)
            verticesBuffer = Quad.create()

            vertices[2] = 0.25f
            vertices[6] = 0.25f
            vertices[10] = 0.75f
            vertices[14] = 0.75f

            vertices[3] = 0f
            vertices[7] = 1f
            vertices[11] = 1f
            vertices[15] = 0f


            vertices[0] = 0f
            vertices[1] = 0f

            vertices[4] = 1f
            vertices[5] = 0f

            vertices[8] = 1f
            vertices[9] = 1f

            vertices[12] = 0f
            vertices[13] = 1f

            verticesBuffer.position(0)
            verticesBuffer.put(vertices)
        }

        override fun draw() {

            super.draw()

            val script = NoosaScript.get()

            texture.bind()

            script.camera(camera())

            script.uModel.valueM4(matrix)
            script.lighting(
                    rm, gm, bm, am,
                    ra, ga, ba, aa)

            script.drawQuad(verticesBuffer)
        }

        companion object {

            private val day = intArrayOf(-0xbb7701, -0x331101)
            private val night = intArrayOf(-0xffeeab, -0xcca680)
        }
    }

    private class Cloud(y: Float, dayTime: Boolean) : Image(Assets.SURFACE) {

        init {

            var index: Int
            do {
                index = Random.Int(3)
            } while (index == lastIndex)

            when (index) {
                0 -> frame(88, 0, 49, 20)
                1 -> frame(88, 20, 49, 22)
                2 -> frame(88, 42, 50, 18)
            }

            lastIndex = index

            this.y = y

            scale.set(1 - y / SKY_HEIGHT)
            x = Random.Float(SKY_WIDTH + width()) - width()
            speed.x = scale.x * if (dayTime) +8 else -8

            if (dayTime) {
                tint(0xCCEEFF, 1 - scale.y)
            } else {
                bm = +3.0f
                gm = bm
                rm = gm
                ba = -2.1f
                ga = ba
                ra = ga
            }
        }

        override fun update() {
            super.update()
            if (speed.x > 0 && x > SKY_WIDTH) {
                x = -width()
            } else if (speed.x < 0 && x < -width()) {
                x = SKY_WIDTH.toFloat()
            }
        }

        companion object {

            private var lastIndex = -1
        }
    }

    private class Avatar(cl: HeroClass) : Image(Assets.AVATARS) {

        init {
            frame(TextureFilm(texture, WIDTH, HEIGHT).get(cl.ordinal))
        }

        companion object {

            private val WIDTH = 24
            private val HEIGHT = 32
        }
    }

    private class Pet : RatSprite() {

        fun jump() {
            play(run)
        }

        override fun onComplete(anim: MovieClip.Animation) {
            if (anim === run) {
                idle()
            }
        }
    }

    private class GrassPatch(private val tx: Float, private val ty: Float, private val forward: Boolean) : Image(Assets.SURFACE) {

        private var a = Random.Float(5f).toDouble()
        private var angle: Double = 0.toDouble()

        init {

            frame(88 + Random.Int(4) * WIDTH, 60, WIDTH, HEIGHT)
        }

        override fun update() {
            super.update()
            a += Random.Float(Game.elapsed * 5).toDouble()
            angle = (2 + Math.cos(a)) * if (forward) +0.2 else -0.2

            scale.y = Math.cos(angle).toFloat()

            x = tx + Math.tan(angle).toFloat() * width
            y = ty - scale.y * height
        }

        override fun updateMatrix() {
            super.updateMatrix()
            Matrix.skewX(matrix, (angle / Matrix.G2RAD).toFloat())
        }

        companion object {

            val WIDTH = 16
            val HEIGHT = 14
        }
    }

    companion object {

        private val FRAME_WIDTH = 88
        private val FRAME_HEIGHT = 125

        private val FRAME_MARGIN_TOP = 9
        private val FRAME_MARGIN_X = 4

        private val BUTTON_HEIGHT = 20

        private val SKY_WIDTH = 80
        private val SKY_HEIGHT = 112

        private val NSTARS = 100
        private val NCLOUDS = 5
    }
}
