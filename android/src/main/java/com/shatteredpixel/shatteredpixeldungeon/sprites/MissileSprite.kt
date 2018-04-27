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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Visual
import com.watabou.noosa.tweeners.PosTweener
import com.watabou.noosa.tweeners.Tweener
import com.watabou.utils.Callback
import com.watabou.utils.PointF

import java.util.HashMap

class MissileSprite : ItemSprite(), Tweener.Listener {

    private var callback: Callback? = null

    fun reset(from: Int, to: Int, item: Item, listener: Callback) {
        reset(DungeonTilemap.tileToWorld(from), DungeonTilemap.tileToWorld(to), item, listener)
    }

    fun reset(from: Visual, to: Visual, item: Item, listener: Callback) {
        reset(from.center(this), to.center(this), item, listener)
    }

    fun reset(from: Visual, to: Int, item: Item, listener: Callback) {
        reset(from.center(this), DungeonTilemap.tileToWorld(to), item, listener)
    }

    fun reset(from: Int, to: Visual, item: Item, listener: Callback) {
        reset(DungeonTilemap.tileToWorld(from), to.center(this), item, listener)
    }

    fun reset(from: PointF, to: PointF, item: Item?, listener: Callback) {
        revive()

        if (item == null)
            view(0, null)
        else
            view(item.image(), item.glowing())

        setup(from,
                to,
                item,
                listener)
    }

    //TODO it might be nice to have a source and destination angle, to improve thrown weapon visuals
    private fun setup(from: PointF, to: PointF, item: Item?, listener: Callback) {

        originToCenter()

        this.callback = listener

        point(from)

        val d = PointF.diff(to, from)
        speed.set(d).normalize().scale(SPEED)

        angularSpeed = DEFAULT_ANGULAR_SPEED.toFloat()
        for (cls in ANGULAR_SPEEDS.keys) {
            if (cls.isAssignableFrom(item!!.javaClass)) {
                angularSpeed = ANGULAR_SPEEDS[cls]!!.toFloat()
                break
            }
        }

        angle = 135 - (Math.atan2(d.x.toDouble(), d.y.toDouble()) / 3.1415926 * 180).toFloat()

        if (d.x >= 0) {
            flipHorizontal = false
            updateFrame()

        } else {
            angularSpeed = -angularSpeed
            angle += 90f
            flipHorizontal = true
            updateFrame()
        }

        var speed = SPEED
        if (item is Dart && Dungeon.hero!!.belongings.weapon is Crossbow) {
            speed *= 3f
        }
        val tweener = PosTweener(this, to, d.length() / speed)
        tweener.listener = this
        parent!!.add(tweener)
    }

    override fun onComplete(tweener: Tweener) {
        kill()
        if (callback != null) {
            callback!!.call()
        }
    }

    companion object {

        private val SPEED = 240f

        private val DEFAULT_ANGULAR_SPEED = 720

        private val ANGULAR_SPEEDS = HashMap<Class<out Item>, Int>()

        init {
            ANGULAR_SPEEDS[Dart::class.java] = 0
            ANGULAR_SPEEDS[ThrowingKnife::class.java] = 0
            ANGULAR_SPEEDS[FishingSpear::class.java] = 0
            ANGULAR_SPEEDS[ThrowingSpear::class.java] = 0
            ANGULAR_SPEEDS[Javelin::class.java] = 0
            ANGULAR_SPEEDS[Trident::class.java] = 0

            //720 is default

            ANGULAR_SPEEDS[Boomerang::class.java] = 1440
            ANGULAR_SPEEDS[Bolas::class.java] = 1440

            ANGULAR_SPEEDS[Shuriken::class.java] = 2160
        }
    }
}
