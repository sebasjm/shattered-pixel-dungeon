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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.particles.Emitter

class EyeSprite : MobSprite() {

    private var zapPos: Int = 0

    private val charging: MovieClip.Animation
    private val chargeParticles: Emitter

    init {

        texture(Assets.EYE)

        val frames = TextureFilm(texture, 16, 18)

        idle = MovieClip.Animation(8, true)
        idle!!.frames(frames, 0, 1, 2)

        charging = MovieClip.Animation(12, true)
        charging.frames(frames, 3, 4)

        chargeParticles = centerEmitter()
        chargeParticles.autoKill = false
        chargeParticles.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f)
        chargeParticles.on = false

        run = MovieClip.Animation(12, true)
        run!!.frames(frames, 5, 6)

        attack = MovieClip.Animation(8, false)
        attack!!.frames(frames, 4, 3)
        zap = attack!!.clone()

        die = MovieClip.Animation(8, false)
        die!!.frames(frames, 7, 8, 9)

        play(idle)
    }

    override fun link(ch: Char) {
        super.link(ch)
        if ((ch as Eye).beamCharged) play(charging)
    }

    override fun update() {
        super.update()
        chargeParticles.pos(center())
        chargeParticles.visible = visible
    }

    fun charge(pos: Int) {
        turnTo(ch!!.pos, pos)
        play(charging)
    }

    override fun play(anim: MovieClip.Animation?) {
        chargeParticles.on = anim === charging
        super.play(anim)
    }

    override fun zap(pos: Int) {
        zapPos = pos
        super.zap(pos)
    }

    override fun onComplete(anim: MovieClip.Animation) {
        super.onComplete(anim)

        if (anim === zap) {
            idle()
            if (Actor.findChar(zapPos) != null) {
                parent!!.add(Beam.DeathRay(center(), Actor.findChar(zapPos)!!.sprite!!.center()))
            } else {
                parent!!.add(Beam.DeathRay(center(), DungeonTilemap.raisedTileCenterToWorld(zapPos)))
            }
            (ch as Eye).deathGaze()
            ch!!.next()
        } else if (anim === die) {
            chargeParticles.killAndErase()
        }
    }
}
