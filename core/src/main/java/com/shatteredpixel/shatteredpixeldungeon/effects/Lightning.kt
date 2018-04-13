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
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.glwrap.Blending
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import com.watabou.utils.Random

import java.util.Arrays

class Lightning(private val arcs: List<Arc>, private val callback: Callback?) : Group() {

    private var life: Float = 0.toFloat()

    constructor(from: Int, to: Int, callback: Callback) : this(Arrays.asList<Arc>(Arc(from, to)), callback) {}

    constructor(from: PointF, to: Int, callback: Callback) : this(Arrays.asList<Arc>(Arc(from, to)), callback) {}

    constructor(from: Int, to: PointF, callback: Callback) : this(Arrays.asList<Arc>(Arc(from, to)), callback) {}

    constructor(from: PointF, to: PointF, callback: Callback) : this(Arrays.asList<Arc>(Arc(from, to)), callback) {}

    init {
        for (arc in this.arcs)
            add(arc)

        life = DURATION

        Sample.INSTANCE.play(Assets.SND_LIGHTNING)
    }

    override fun update() {
        if ((life -= Game.elapsed) < 0) {

            killAndErase()
            callback?.call()

        } else {

            val alpha = life / DURATION

            for (arc in arcs) {
                arc.alpha(alpha)
            }

            super.update()
        }
    }

    override fun draw() {
        Blending.setLightMode()
        super.draw()
        Blending.setNormalMode()
    }

    //A lightning object is meant to be loaded up with arcs.
    //these act as a means of easily expressing lighting between two points.
    class Arc(//starting and ending x/y values
            private val start: PointF, private val end: PointF) : Group() {

        private val arc1: Image
        private val arc2: Image

        constructor(from: Int, to: Int) : this(DungeonTilemap.tileCenterToWorld(from),
                DungeonTilemap.tileCenterToWorld(to)) {
        }

        constructor(from: PointF, to: Int) : this(from, DungeonTilemap.tileCenterToWorld(to)) {}

        constructor(from: Int, to: PointF) : this(DungeonTilemap.tileCenterToWorld(from), to) {}

        init {

            arc1 = Image(Effects.get(Effects.Type.LIGHTNING))
            arc1.x = start.x - arc1.origin.x
            arc1.y = start.y - arc1.origin.y
            arc1.origin.set(0f, arc1.height() / 2)
            add(arc1)

            arc2 = Image(Effects.get(Effects.Type.LIGHTNING))
            arc2.origin.set(0f, arc2.height() / 2)
            add(arc2)
        }

        fun alpha(alpha: Float) {
            arc2.am = alpha
            arc1.am = arc2.am
        }

        override fun update() {
            val x2 = (start.x + end.x) / 2 + Random.Float(-4f, +4f)
            val y2 = (start.y + end.y) / 2 + Random.Float(-4f, +4f)

            var dx = x2 - start.x
            var dy = y2 - start.y
            arc1.angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
            arc1.scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / arc1.width

            dx = end.x - x2
            dy = end.y - y2
            arc2.angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
            arc2.scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / arc2.width
            arc2.x = x2 - arc2.origin.x
            arc2.y = y2 - arc2.origin.x
        }
    }

    companion object {

        private val DURATION = 0.3f

        private val A = 180 / Math.PI
    }
}
