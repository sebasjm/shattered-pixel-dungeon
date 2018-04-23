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

package com.watabou.noosa

import com.watabou.utils.RectF
import java.util.*

open class MovieClip : Image {

    protected var curAnim: Animation? = null
    protected var curFrame: Int = 0
    protected var frameTimer: Float = 0.toFloat()
    protected var finished: Boolean = false

    var paused = false

    var listener: Listener? = null

    constructor() : super() {}

    constructor(tx: Any) : super(tx) {}

    override fun update() {
        super.update()
        if (!paused) {
            updateAnimation()
        }
    }

    fun looping(): Boolean {
        return curAnim != null && curAnim!!.looped
    }

    @Synchronized
    protected fun updateAnimation() {
        if (curAnim != null && curAnim!!.delay > 0 && (curAnim!!.looped || !finished)) {

            val lastFrame = curFrame

            frameTimer += Game.elapsed
            while (frameTimer > curAnim!!.delay) {
                frameTimer -= curAnim!!.delay
                if (curFrame == curAnim!!.frames!!.size - 1) {
                    if (curAnim!!.looped) {
                        curFrame = 0
                    }
                    finished = true
                    if (listener != null) {
                        listener!!.onComplete(curAnim!!)
                        // This check can probably be removed
                        if (curAnim == null) {
                            return
                        }
                    }

                } else {
                    curFrame++
                }
            }

            if (curFrame != lastFrame) {
                frame(curAnim!!.frames!![curFrame]!!)
            }

        }
    }

    open fun play(anim: Animation?) {
        play(anim, false)
    }

    @Synchronized
    fun play(anim: Animation?, force: Boolean) {

        if (!force && curAnim != null && curAnim === anim && (curAnim!!.looped || !finished)) {
            return
        }

        curAnim = anim
        curFrame = 0
        finished = false

        frameTimer = 0f

        if (anim != null) {
            frame(anim.frames!![curFrame]!!)
        }
    }

    class Animation(fps: Int, var looped: Boolean) {

        var delay: Float = 0.toFloat()
        var frames: Array<RectF?>? = null

        init {
            this.delay = 1f / fps
        }

        fun frames(vararg frames: RectF?): Animation {
            this.frames = frames.asList().toTypedArray()
            return this
        }

        fun frames(film: TextureFilm, vararg frames: Any): Animation {
            this.frames = arrayOfNulls(frames.size)
            for (i in frames.indices) {
                this.frames!![i] = film.get(frames[i])
            }
            return this
        }

        fun clone(): Animation {
            return Animation(Math.round(1 / delay), looped).frames(*frames!!)
        }
    }

    interface Listener {
        fun onComplete(anim: Animation)
    }
}
