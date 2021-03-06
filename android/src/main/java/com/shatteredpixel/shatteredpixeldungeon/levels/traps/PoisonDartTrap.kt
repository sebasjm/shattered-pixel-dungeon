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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.Random
import com.watabou.utils.asCallback

class PoisonDartTrap : Trap() {

    init {
        color = Trap.GREEN
        shape = Trap.CROSSHAIR
    }

    override fun hide(): Trap {
        //this one can't be hidden
        return reveal()
    }

    override fun activate() {
        var target = Actor.findChar(pos)

        //find the closest char that can be aimed at
        if (target == null) {
            for (ch in Actor.chars()) {
                val bolt = Ballistica(pos, ch.pos, Ballistica.PROJECTILE)
                if (bolt.collisionPos == ch.pos && (target == null || Dungeon.level!!.trueDistance(pos, ch.pos) < Dungeon.level!!.trueDistance(pos, target.pos))) {
                    target = ch
                }
            }
        }
        if (target != null) {
            val finalTarget = target
            val trap = this
            if (Dungeon.level!!.heroFOV[pos] || Dungeon.level!!.heroFOV[target.pos]) {
                Actor.add(object : Actor() {

                    init {
                        //it's a visual effect, gets priority no matter what
                        actPriority = VFX_PRIO
                    }

                    override fun act(): Boolean {
                        val toRemove = this
                        (Game.scene()!!.recycle(MissileSprite::class.java) as MissileSprite).reset(pos, finalTarget.sprite!!, PoisonDart(), {
                            val dmg = Random.NormalIntRange(1, 4) - finalTarget.drRoll()
                            finalTarget.damage(dmg, trap)
                            if (finalTarget === Dungeon.hero!! && !finalTarget.isAlive) {
                                Dungeon.fail(trap.javaClass)
                            }
                            Buff.affect<Poison>(finalTarget, Poison::class.java)!!
                                    .set((4 + Dungeon.depth).toFloat())
                            Sample.INSTANCE.play(Assets.SND_HIT, 1f, 1f, Random.Float(0.8f, 1.25f))
                            finalTarget.sprite!!.bloodBurstA(finalTarget.sprite!!.center(), dmg)
                            finalTarget.sprite!!.flash()
                            Actor.remove(toRemove)
                            next()
                        } .asCallback())
                        return false
                    }
                })
            } else {
                finalTarget.damage(Random.NormalIntRange(1, 4) - finalTarget.drRoll(), trap)
                Buff.affect<Poison>(finalTarget, Poison::class.java)!!
                        .set((4 + Dungeon.depth).toFloat())
            }
        }
    }
}
