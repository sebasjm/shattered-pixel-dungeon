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

package com.shatteredpixel.shatteredpixeldungeon.sprites

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.DarkBlock
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText
import com.shatteredpixel.shatteredpixeldungeon.effects.IceBlock
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash
import com.shatteredpixel.shatteredpixeldungeon.effects.TorchHalo
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.ui.CharHealthIndicator
import com.watabou.glwrap.Matrix
import com.watabou.glwrap.Vertexbuffer
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.MovieClip
import com.watabou.noosa.Visual
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.noosa.tweeners.PosTweener
import com.watabou.noosa.tweeners.Tweener
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import com.watabou.utils.Random

open class CharSprite : MovieClip(), Tweener.Listener, MovieClip.Listener {

    //the amount the sprite is raised from flat when viewed in a raised perspective
    protected var perspectiveRaise = 6 / 16f //6 pixels

    //the width and height of the shadow are a percentage of sprite size
    //offset is the number of pixels the shadow is moved down or up (handy for some animations)
    protected var renderShadow = false
    protected var shadowWidth = 1.2f
    protected var shadowHeight = 0.25f
    protected var shadowOffset = 0.25f

    protected var idle: MovieClip.Animation? = null
    protected var run: MovieClip.Animation? = null
    protected var attack: MovieClip.Animation? = null
    protected var operate: MovieClip.Animation? = null
    protected var zap: MovieClip.Animation? = null
    protected var die: MovieClip.Animation? = null

    protected var animCallback: Callback? = null

    protected var motion: Tweener? = null

    protected var burning: Emitter? = null
    protected var chilled: Emitter? = null
    protected var marked: Emitter? = null
    protected var levitation: Emitter? = null
    protected var healing: Emitter? = null

    protected var iceBlock: IceBlock? = null
    protected var darkBlock: DarkBlock? = null
    protected var halo: TorchHalo? = null
    protected var invisible: AlphaTweener? = null

    protected var emo: EmoIcon? = null
    protected var health: CharHealthIndicator? = null

    private var jumpTweener: Tweener? = null
    private var jumpCallback: Callback? = null

    private var flashTime = 0f

    protected var sleeping = false

    var ch: Char? = null

    //used to prevent the actor associated with this sprite from acting until movement completes
    @Volatile
    var isMoving = false

    private val shadowMatrix = FloatArray(16)

    enum class State {
        BURNING, LEVITATING, INVISIBLE, PARALYSED, FROZEN, ILLUMINATED, CHILLED, DARKENED, MARKED, HEALING
    }

    init {
        listener = this
    }

    override fun play(anim: MovieClip.Animation?) {
        //Shouldn't interrupt the dieing animation
        if (curAnim == null || curAnim !== die) {
            super.play(anim!!)
        }
    }

    open fun link(ch: Char) {
        this.ch = ch
        ch.sprite = this

        place(ch.pos)
        turnTo(ch.pos, Random.Int(Dungeon.level!!.length()))
        renderShadow = true

        if (ch !== Dungeon.hero!!) {
            if (health == null) {
                health = CharHealthIndicator(ch)
            } else {
                health!!.target(ch)
            }
        }

        ch.updateSpriteState()
    }

    fun worldToCamera(cell: Int): PointF {

        val csize = DungeonTilemap.SIZE

        return PointF(
                PixelScene.align(Camera.main!!, (cell % Dungeon.level!!.width() + 0.5f) * csize - width * 0.5f),
                PixelScene.align(Camera.main!!, (cell / Dungeon.level!!.width() + 1.0f) * csize - height - csize * perspectiveRaise)
        )
    }

    open fun place(cell: Int) {
        point(worldToCamera(cell))
    }

    fun showStatus(color: Int, text: String, vararg args: Any) {
        var text = text
        if (visible) {
            if (args.size > 0) {
                text = Messages.format(text, *args)
            }
            if (ch != null) {
                FloatingText.show(x + width * 0.5f, y, ch!!.pos, text, color)
            } else {
                FloatingText.show(x + width * 0.5f, y, text, color)
            }
        }
    }

    fun idle() {
        play(idle)
    }

    open fun move(from: Int, to: Int) {
        turnTo(from, to)

        play(run)

        motion = PosTweener(this, worldToCamera(to), MOVE_INTERVAL)
        motion!!.listener = this
        parent!!.add(motion!!)

        isMoving = true

        if (visible && Dungeon.level!!.water[from] && !ch!!.flying) {
            GameScene.ripple(from)
        }

    }

    fun interruptMotion() {
        if (motion != null) {
            motion!!.stop(false)
        }
    }

    open fun attack(cell: Int) {
        turnTo(ch!!.pos, cell)
        play(attack)
    }

    fun attack(cell: Int, callback: Callback) {
        animCallback = callback
        turnTo(ch!!.pos, cell)
        play(attack)
    }

    fun operate(cell: Int) {
        turnTo(ch!!.pos, cell)
        play(operate)
    }

    open fun zap(cell: Int) {
        turnTo(ch!!.pos, cell)
        play(zap)
    }

    open fun turnTo(from: Int, to: Int) {
        val fx = from % Dungeon.level!!.width()
        val tx = to % Dungeon.level!!.width()
        if (tx > fx) {
            flipHorizontal = false
        } else if (tx < fx) {
            flipHorizontal = true
        }
    }

    open fun jump(from: Int, to: Int, callback: Callback) {
        jumpCallback = callback

        val distance = Dungeon.level!!.distance(from, to)
        jumpTweener = JumpTweener(this, worldToCamera(to), (distance * 4).toFloat(), distance * 0.1f)
        jumpTweener!!.listener = this
        parent!!.add(jumpTweener!!)

        turnTo(from, to)
    }

    open fun die() {
        sleeping = false
        play(die)

        if (emo != null) {
            emo!!.killAndErase()
        }

        if (health != null) {
            health!!.killAndErase()
        }
    }

    fun emitter(): Emitter {
        val emitter = GameScene.emitter()
        emitter!!.pos(this)
        return emitter
    }

    fun centerEmitter(): Emitter {
        val emitter = GameScene.emitter()
        emitter!!.pos(center())
        return emitter
    }

    fun bottomEmitter(): Emitter {
        val emitter = GameScene.emitter()
        emitter!!.pos(x, y + height, width, 0f)
        return emitter
    }

    fun burst(color: Int, n: Int) {
        if (visible) {
            Splash.at(center(), color, n)
        }
    }

    open fun bloodBurstA(from: PointF, damage: Int) {
        if (visible) {
            val c = center()
            val n = Math.min(9 * Math.sqrt(damage.toDouble() / ch!!.HT), 9.0).toInt()
            Splash.at(c, PointF.angle(from, c), 3.1415926f / 2, blood(), n)
        }
    }

    open fun blood(): Int {
        return -0x450000
    }

    fun flash() {
        ga = 1f
        ba = ga
        ra = ba
        flashTime = FLASH_INTERVAL
    }

    fun add(state: State) {
        when (state) {
            CharSprite.State.BURNING -> {
                burning = emitter()
                burning!!.pour(FlameParticle.FACTORY, 0.06f)
                if (visible) {
                    Sample.INSTANCE.play(Assets.SND_BURNING)
                }
            }
            CharSprite.State.LEVITATING -> {
                levitation = emitter()
                levitation!!.pour(Speck.factory(Speck.JET), 0.02f)
            }
            CharSprite.State.INVISIBLE -> {
                if (invisible != null) {
                    invisible!!.killAndErase()
                }
                invisible = AlphaTweener(this, 0.4f, 0.4f)
                if (parent != null) {
                    parent!!.add(invisible!!)
                } else
                    alpha(0.4f)
            }
            CharSprite.State.PARALYSED -> paused = true
            CharSprite.State.FROZEN -> {
                iceBlock = IceBlock.freeze(this)
                paused = true
            }
            CharSprite.State.ILLUMINATED -> {
                halo = TorchHalo(this)
                GameScene.effect(halo!!)
            }
            CharSprite.State.CHILLED -> {
                chilled = emitter()
                chilled!!.pour(SnowParticle.FACTORY, 0.1f)
            }
            CharSprite.State.DARKENED -> darkBlock = DarkBlock.darken(this)
            CharSprite.State.MARKED -> {
                marked = emitter()
                marked!!.pour(ShadowParticle.UP, 0.1f)
            }
            CharSprite.State.HEALING -> {
                healing = emitter()
                healing!!.pour(Speck.factory(Speck.HEALING), 0.5f)
            }
        }
    }

    fun remove(state: State) {
        when (state) {
            CharSprite.State.BURNING -> if (burning != null) {
                burning!!.on = false
                burning = null
            }
            CharSprite.State.LEVITATING -> if (levitation != null) {
                levitation!!.on = false
                levitation = null
            }
            CharSprite.State.INVISIBLE -> {
                if (invisible != null) {
                    invisible!!.killAndErase()
                    invisible = null
                }
                alpha(1f)
            }
            CharSprite.State.PARALYSED -> paused = false
            CharSprite.State.FROZEN -> {
                if (iceBlock != null) {
                    iceBlock!!.melt()
                    iceBlock = null
                }
                paused = false
            }
            CharSprite.State.ILLUMINATED -> if (halo != null) {
                halo!!.putOut()
            }
            CharSprite.State.CHILLED -> if (chilled != null) {
                chilled!!.on = false
                chilled = null
            }
            CharSprite.State.DARKENED -> if (darkBlock != null) {
                darkBlock!!.lighten()
                darkBlock = null
            }
            CharSprite.State.MARKED -> if (marked != null) {
                marked!!.on = false
                marked = null
            }
            CharSprite.State.HEALING -> if (healing != null) {
                healing!!.on = false
                healing = null
            }
        }
    }

    @Synchronized
    override//syncronized due to EmoIcon handling
    fun update() {

        super.update()

        if (paused && listener != null) {
            listener!!.onComplete(curAnim!!)
        }

        if (flashTime > 0) {
            flashTime -= Game.elapsed
            if (flashTime <= 0) {
                resetColor()
            }
        }

        if (burning != null) {
            burning!!.visible = visible
        }
        if (levitation != null) {
            levitation!!.visible = visible
        }
        if (iceBlock != null) {
            iceBlock!!.visible = visible
        }
        if (chilled != null) {
            chilled!!.visible = visible
        }
        if (marked != null) {
            marked!!.visible = visible
        }
        if (sleeping) {
            showSleep()
        } else {
            hideSleep()
        }
        if (emo != null && emo!!.alive) {
            emo!!.visible = visible
        }
    }

    override fun resetColor() {
        super.resetColor()
        if (invisible != null) {
            alpha(0.4f)
        }
    }

    @Synchronized
    fun showSleep() {
        if (emo is EmoIcon.Sleep) {

        } else {
            if (emo != null) {
                emo!!.killAndErase()
            }
            emo = EmoIcon.Sleep(this)
            emo!!.visible = visible
        }
        idle()
    }

    @Synchronized
    fun hideSleep() {
        if (emo is EmoIcon.Sleep) {
            emo!!.killAndErase()
            emo = null
        }
    }

    @Synchronized
    fun showAlert() {
        if (emo is EmoIcon.Alert) {

        } else {
            if (emo != null) {
                emo!!.killAndErase()
            }
            emo = EmoIcon.Alert(this)
            emo!!.visible = visible
        }
    }

    @Synchronized
    fun hideAlert() {
        if (emo is EmoIcon.Alert) {
            emo!!.killAndErase()
            emo = null
        }
    }

    @Synchronized
    fun showLost() {
        if (emo is EmoIcon.Lost) {

        } else {
            if (emo != null) {
                emo!!.killAndErase()
            }
            emo = EmoIcon.Lost(this)
            emo!!.visible = visible
        }
    }

    @Synchronized
    fun hideLost() {
        if (emo is EmoIcon.Lost) {
            emo!!.killAndErase()
            emo = null
        }
    }

    override fun kill() {
        super.kill()

        if (emo != null) {
            emo!!.killAndErase()
        }

        for (s in State.values()) {
            remove(s)
        }

        if (health != null) {
            health!!.killAndErase()
        }
    }

    override fun updateMatrix() {
        super.updateMatrix()
        Matrix.copy(matrix, shadowMatrix)
        Matrix.translate(shadowMatrix,
                width() * (1f - shadowWidth) / 2f,
                height() * (1f - shadowHeight) + shadowOffset)
        Matrix.scale(shadowMatrix, shadowWidth, shadowHeight)
    }

    override fun draw() {
        if (texture == null || !dirty && buffer == null)
            return

        if (renderShadow) {
            if (dirty) {
                verticesBuffer.position(0)
                verticesBuffer.put(vertices)
                if (buffer == null)
                    buffer = Vertexbuffer(verticesBuffer)
                else
                    buffer!!.updateVertices(verticesBuffer)
                dirty = false
            }

            val script = script()

            texture!!.bind()

            script.camera(camera())

            updateMatrix()

            script.uModel.valueM4(shadowMatrix)
            script.lighting(
                    0f, 0f, 0f, am * .6f,
                    0f, 0f, 0f, aa * .6f)

            script.drawQuad(buffer!!)
        }

        super.draw()

    }

    override fun onComplete(tweener: Tweener) {
        if (tweener === jumpTweener) {

            if (visible && Dungeon.level!!.water[ch!!.pos] && !ch!!.flying) {
                GameScene.ripple(ch!!.pos)
            }
            if (jumpCallback != null) {
                jumpCallback!!.call()
            }

        } else if (tweener === motion) {

            synchronized(this) {
                isMoving = false

                motion!!.killAndErase()
                motion = null
                ch!!.onMotionComplete()

                (this as java.lang.Object).notifyAll()
            }

        }
    }

    override fun onComplete(anim: MovieClip.Animation) {

        if (animCallback != null) {
            val executing = animCallback
            animCallback = null
            executing!!.call()
        } else {

            if (anim === attack) {

                idle()
                ch!!.onAttackComplete()

            } else if (anim === operate) {

                idle()
                ch!!.onOperateComplete()

            }

        }
    }

    private class JumpTweener(var visual: Visual, var end: PointF, var height: Float, time: Float) : Tweener(visual, time) {

        var start: PointF

        init {
            start = visual.point()
        }

        override fun updateValues(progress: Float) {
            visual.point(PointF.inter(start, end, progress).offset(0f, -height * 4f * progress * (1 - progress)))
        }
    }

    companion object {

        // Color constants for floating text
        val DEFAULT = 0xFFFFFF
        val POSITIVE = 0x00FF00
        val NEGATIVE = 0xFF0000
        val WARNING = 0xFF8800
        val NEUTRAL = 0xFFFF00

        private val MOVE_INTERVAL = 0.1f
        private val FLASH_INTERVAL = 0.05f
    }
}
