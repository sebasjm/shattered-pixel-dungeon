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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.asCallback

class GrimTrap : Trap() {

    init {
        color = Trap.GREY
        shape = Trap.LARGE_DOT
    }

    override fun hide(): Trap {
        //cannot hide this trap
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
            val damage: Int

            //almost kill the player
            if (finalTarget === Dungeon.hero!! && finalTarget.HP.toFloat() / finalTarget.HT >= 0.9f) {
                damage = finalTarget.HP - 1
                //kill 'em
            } else {
                damage = finalTarget.HP
            }

            val finalDmg = damage

            Actor.add(object : Actor() {

                init {
                    //it's a visual effect, gets priority no matter what
                    actPriority = VFX_PRIO
                }

                override fun act(): Boolean {
                    val toRemove = this
                    (finalTarget.sprite!!.parent!!.recycle(MagicMissile::class.java) as MagicMissile).reset(
                            MagicMissile.SHADOW,
                            DungeonTilemap.tileCenterToWorld(pos),
                            finalTarget.sprite!!.center()
                    , {
                        finalTarget.damage(finalDmg, trap)
                        if (finalTarget === Dungeon.hero!!) {
                            Sample.INSTANCE.play(Assets.SND_CURSED)
                            if (!finalTarget.isAlive) {
                                Dungeon.fail(GrimTrap::class.java)
                                GLog.n(Messages.get(GrimTrap::class.java, "ondeath"))
                            }
                        } else {
                            Sample.INSTANCE.play(Assets.SND_BURNING)
                        }
                        finalTarget.sprite!!.emitter().burst(ShadowParticle.UP, 10)
                        Actor.remove(toRemove)
                        next()
                    } .asCallback())
                    return false
                }
            })
        } else {
            CellEmitter.get(pos).burst(ShadowParticle.UP, 10)
            Sample.INSTANCE.play(Assets.SND_BURNING)
        }
    }
}
