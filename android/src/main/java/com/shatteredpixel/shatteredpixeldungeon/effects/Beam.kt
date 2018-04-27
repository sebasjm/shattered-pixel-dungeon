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

package com.shatteredpixel.shatteredpixeldungeon.effects

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.watabou.glwrap.Blending
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PointF

open class Beam private constructor(s: PointF, e: PointF, asset: Effects.Type, duration: Float) : Image(Effects.get(asset)) {

    private var duration: Float = 0.toFloat()

    private var timeLeft: Float = 0.toFloat()

    init {

        origin.set(0f, height / 2)

        x = s.x - origin.x
        y = s.y - origin.y

        val dx = e.x - s.x
        val dy = e.y - s.y
        angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
        scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / width

        Sample.INSTANCE.play(Assets.SND_RAY)

        this.duration = duration
        timeLeft = this.duration
    }

    class DeathRay(s: PointF, e: PointF) : Beam(s, e, Effects.Type.DEATH_RAY, 0.5f)

    class LightRay(s: PointF, e: PointF) : Beam(s, e, Effects.Type.LIGHT_RAY, 1f)

    class HealthRay(s: PointF, e: PointF) : Beam(s, e, Effects.Type.HEALTH_RAY, 0.75f)

    override fun update() {
        super.update()

        val p = timeLeft / duration
        alpha(p)
        scale.set(scale.x, p)

        timeLeft -= Game.elapsed
        if (timeLeft <= 0) {
            killAndErase()
        }
    }

    override fun draw() {
        Blending.setLightMode()
        super.draw()
        Blending.setNormalMode()
    }

    companion object {

        private val A = 180 / Math.PI
    }
}
