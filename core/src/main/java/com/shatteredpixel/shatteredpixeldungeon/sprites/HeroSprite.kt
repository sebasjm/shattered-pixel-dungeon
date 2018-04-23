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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import com.watabou.utils.asCallback

class HeroSprite : CharSprite() {

    private var fly: MovieClip.Animation? = null
    private var read: MovieClip.Animation? = null

    init {

        link(Dungeon.hero!!)

        texture(Dungeon.hero!!.heroClass.spritesheet()!!)
        updateArmor()

        if (ch!!.isAlive)
            idle()
        else
            die()
    }

    fun updateArmor() {

        val film = TextureFilm(tiers(), (ch as Hero).tier(), FRAME_WIDTH, FRAME_HEIGHT)

        idle = MovieClip.Animation(1, true)
        idle!!.frames(film, 0, 0, 0, 1, 0, 0, 1, 1)

        run = MovieClip.Animation(RUN_FRAMERATE, true)
        run!!.frames(film, 2, 3, 4, 5, 6, 7)

        die = MovieClip.Animation(20, false)
        die!!.frames(film, 8, 9, 10, 11, 12, 11)

        attack = MovieClip.Animation(15, false)
        attack!!.frames(film, 13, 14, 15, 0)

        zap = attack!!.clone()

        operate = MovieClip.Animation(8, false)
        operate!!.frames(film, 16, 17, 16, 17)

        fly = MovieClip.Animation(1, true)
        fly!!.frames(film, 18)

        read = MovieClip.Animation(20, false)
        read!!.frames(film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19)

        idle()
    }

    override fun place(p: Int) {
        super.place(p)
        Camera.main!!.target = this
    }

    override fun move(from: Int, to: Int) {
        super.move(from, to)
        if (ch!!.flying) {
            play(fly)
        }
        Camera.main!!.target = this
    }

    override fun jump(from: Int, to: Int, callback: Callback) {
        super.jump(from, to, callback)
        play(fly)
    }

    fun read() {
        animCallback =  {
            idle()
            ch!!.onOperateComplete()
        } .asCallback()
        play(read)
    }

    override fun bloodBurstA(from: PointF, damage: Int) {
        //Does nothing.

        /*
		 * This is both for visual clarity, and also for content ratings regarding violence
		 * towards human characters. The heroes are the only human or human-like characters which
		 * participate in combat, so removing all blood associated with them is a simple way to
		 * reduce the violence rating of the game.
		 */
    }

    override fun update() {
        sleeping = ch!!.isAlive && (ch as Hero).resting

        super.update()
    }

    fun sprint(speed: Float) {
        run!!.delay = 1f / speed / RUN_FRAMERATE.toFloat()
    }

    companion object {

        private val FRAME_WIDTH = 12
        private val FRAME_HEIGHT = 15

        private val RUN_FRAMERATE = 20

        private var tiers: TextureFilm? = null

        fun tiers(): TextureFilm {
            if (tiers == null) {
                val texture = TextureCache.get(Assets.ROGUE)
                tiers = TextureFilm(texture, texture.width, FRAME_HEIGHT)
            }

            return tiers!!
        }

        fun avatar(cl: HeroClass, armorTier: Int): Image {

            val patch = tiers().get(armorTier)!!
            val avatar = Image(cl.spritesheet()!!)
            val frame = avatar.texture!!.uvRect(1f, 0f, FRAME_WIDTH.toFloat(), FRAME_HEIGHT.toFloat())
            frame.shift(patch.left, patch.top)
            avatar.frame(frame)

            return avatar
        }
    }
}
