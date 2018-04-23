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

package com.shatteredpixel.shatteredpixeldungeon.items.wands

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder
import com.watabou.utils.PointF
import com.watabou.utils.Random

class WandOfBlastWave : DamageWand() {

    init {
        image = ItemSpriteSheet.WAND_BLAST_WAVE

        collisionProperties = Ballistica.PROJECTILE
    }

    override fun min(lvl: Int): Int {
        return 1 + lvl
    }

    override fun max(lvl: Int): Int {
        return 5 + 3 * lvl
    }

    override fun onZap(bolt: Ballistica) {
        Sample.INSTANCE.play(Assets.SND_BLAST)
        BlastWave.blast(bolt.collisionPos!!)

        val damage = damageRoll()

        //presses all tiles in the AOE first
        for (i in PathFinder.NEIGHBOURS9!!) {
            Dungeon.level!!.press(bolt.collisionPos!! + i, Actor.findChar(bolt.collisionPos!! + i), true)
        }

        //throws other chars around the center.
        for (i in PathFinder.NEIGHBOURS8!!) {
            val ch = Actor.findChar(bolt.collisionPos!! + i)

            if (ch != null) {
                processSoulMark(ch, chargesPerCast())
                ch.damage(Math.round(damage * 0.667f), this)

                if (ch.isAlive) {
                    val trajectory = Ballistica(ch.pos, ch.pos + i, Ballistica.MAGIC_BOLT)
                    val strength = 1 + Math.round(level() / 2f)
                    throwChar(ch, trajectory, strength)
                }
            }
        }

        //throws the char at the center of the blast
        val ch = Actor.findChar(bolt.collisionPos!!)
        if (ch != null) {
            processSoulMark(ch, chargesPerCast())
            ch.damage(damage, this)

            if (ch.isAlive && bolt.path.size > bolt.dist!! + 1) {
                val trajectory = Ballistica(ch.pos, bolt.path[bolt.dist!! + 1], Ballistica.MAGIC_BOLT)
                val strength = level() + 3
                throwChar(ch, trajectory, strength)
            }
        }

        if (!Item.curUser!!.isAlive) {
            Dungeon.fail(javaClass)
            GLog.n(Messages.get(this.javaClass, "ondeath"))
        }
    }

    override//behaves just like glyph of Repulsion
    fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        val level = Math.max(0, staff.level())

        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        if (Random.Int(level + 4) >= 3) {
            val oppositeHero = defender.pos + (defender.pos - attacker.pos)
            val trajectory = Ballistica(defender.pos, oppositeHero, Ballistica.MAGIC_BOLT)
            throwChar(defender, trajectory, 2)
        }
    }

    override fun fx(bolt: Ballistica, callback: Callback) {
        MagicMissile.boltFromChar(Item.curUser!!.sprite!!.parent!!,
                MagicMissile.FORCE,
                Item.curUser!!.sprite!!,
                bolt.collisionPos!!,
                callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(0x664422)
        particle.am = 0.6f
        particle.lifespan = (3f)
        particle.speed.polar(Random.Float(PointF.PI2), 0.3f)
        particle.setSize(1f, 2f)
        particle.radiateXY(2.5f)
    }

    class BlastWave : Image(Effects.get(Effects.Type.RIPPLE)) {

        private var time: Float = 0.toFloat()

        init {
            origin.set(width / 2, height / 2)
        }

        fun reset(pos: Int) {
            revive()

            x = pos % Dungeon.level!!.width() * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2
            y = pos / Dungeon.level!!.width() * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2

            time = TIME_TO_FADE
        }

        override fun update() {
            super.update()

            time -= Game.elapsed
            if (time <= 0) {
                kill()
            } else {
                val p = time / TIME_TO_FADE
                alpha(p)
                scale.x = (1 - p) * 3
                scale.y = scale.x
            }
        }

        companion object {

            private val TIME_TO_FADE = 0.2f

            fun blast(pos: Int) {
                val parent = Dungeon.hero!!.sprite!!.parent
                val b = parent!!.recycle(BlastWave::class.java) as BlastWave
                parent.bringToFront(b)
                b.reset(pos)
            }
        }

    }

    companion object {

        fun throwChar(ch: Char, trajectory: Ballistica, power: Int) {
            var dist = Math.min(trajectory.dist!!, power)

            if (ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.BOSS))
                dist /= 2

            if (dist == 0 || ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.IMMOVABLE)) return

            if (Actor.findChar(trajectory.path[dist]) != null) {
                dist--
            }

            val newPos = trajectory.path[dist]

            if (newPos == ch.pos) return

            val finalDist = dist
            val initialpos = ch.pos

            Actor.addDelayed(Pushing(ch, ch.pos, newPos, object : Callback {
                override fun call() {
                    if (initialpos != ch.pos) {
                        //something cased movement before pushing resolved, cancel to be safe.
                        ch.sprite!!.place(ch.pos)
                        return
                    }
                    ch.pos = newPos
                    if (ch.pos == trajectory.collisionPos) {
                        ch.damage(Random.NormalIntRange((finalDist + 1) / 2, finalDist), this)
                        Buff.prolong<Paralysis>(ch, Paralysis::class.java, Random.NormalIntRange((finalDist + 1) / 2, finalDist).toFloat())
                    }
                    Dungeon.level!!.press(ch.pos, ch, true)
                    if (ch === Dungeon.hero!!) {
                        Dungeon.observe()
                    }
                }
            }), -1f)
        }
    }
}
