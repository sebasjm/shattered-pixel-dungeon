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
import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder
import com.watabou.utils.PointF
import com.watabou.utils.Random

class WandOfPrismaticLight : DamageWand() {

    init {
        image = ItemSpriteSheet.WAND_PRISMATIC_LIGHT

        collisionProperties = Ballistica.MAGIC_BOLT
    }

    override fun min(lvl: Int): Int {
        return 1 + lvl
    }

    override fun max(lvl: Int): Int {
        return 5 + 3 * lvl
    }

    override fun onZap(beam: Ballistica) {
        val ch = Actor.findChar(beam.collisionPos!!)
        if (ch != null) {
            processSoulMark(ch, chargesPerCast())
            affectTarget(ch)
        }
        affectMap(beam)

        if (Dungeon.level!!.viewDistance < 6) {
            if (Dungeon.isChallenged(Challenges.DARKNESS)) {
                Buff.prolong<Light>(Item.curUser, Light::class.java, 2f + level())
            } else {
                Buff.prolong<Light>(Item.curUser, Light::class.java, 10f + level() * 5)
            }
        }
    }

    private fun affectTarget(ch: Char) {
        val dmg = damageRoll()

        //three in (5+lvl) chance of failing
        if (Random.Int(5 + level()) >= 3) {
            Buff.prolong<Blindness>(ch, Blindness::class.java, 2f + level() * 0.333f)
            ch.sprite!!.emitter().burst(Speck.factory(Speck.LIGHT), 6)
        }

        if (ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.DEMONIC) || ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.UNDEAD)) {
            ch.sprite!!.emitter().start(ShadowParticle.UP, 0.05f, 10 + level())
            Sample.INSTANCE.play(Assets.SND_BURNING)

            ch.damage(Math.round(dmg * 1.333f), this)
        } else {
            ch.sprite!!.centerEmitter().burst(RainbowParticle.BURST, 10 + level())

            ch.damage(dmg, this)
        }

    }

    private fun affectMap(beam: Ballistica) {
        var noticed = false
        for (c in beam.subPath(0, beam.dist!!)) {
            for (n in PathFinder.NEIGHBOURS9) {
                val cell = c + n

                if (Dungeon.level!!.discoverable[cell])
                    Dungeon.level!!.mapped[cell] = true

                val terr = Dungeon.level!!.map!![cell]
                if (Terrain.flags[terr] and Terrain.SECRET != 0) {

                    Dungeon.level!!.discover(cell)

                    GameScene.discoverTile(cell, terr)
                    ScrollOfMagicMapping.discover(cell)

                    noticed = true
                }
            }

            CellEmitter.center(c).burst(RainbowParticle.BURST, Random.IntRange(1, 2))
        }
        if (noticed)
            Sample.INSTANCE.play(Assets.SND_SECRET)

        GameScene.updateFog()
    }

    override fun fx(beam: Ballistica, callback: Callback) {
        Item.curUser.sprite!!.parent!!.add(
                Beam.LightRay(Item.curUser.sprite!!.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos!!)))
        callback.call()
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        //cripples enemy
        Buff.prolong<Cripple>(defender, Cripple::class.java, 1f + staff.level())
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(Random.Int(0x1000000))
        particle.am = 0.5f
        particle.setLifespan(1f)
        particle.speed.polar(Random.Float(PointF.PI2), 2f)
        particle.setSize(1f, 2f)
        particle.radiateXY(0.5f)
    }

}
