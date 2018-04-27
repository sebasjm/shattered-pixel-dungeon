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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import com.watabou.utils.Random

class WandOfTransfusion : Wand() {

    private var freeCharge = false

    init {
        image = ItemSpriteSheet.WAND_TRANSFUSION

        collisionProperties = Ballistica.PROJECTILE
    }

    override fun onZap(beam: Ballistica) {

        for (c in beam.subPath(0, beam.dist!!))
            CellEmitter.center(c).burst(BloodParticle.BURST, 1)

        val cell = beam.collisionPos!!

        val ch = Actor.findChar(cell)
        val heap = Dungeon.level!!.heaps.get(cell)

        //this wand does a bunch of different things depending on what it targets.

        //if we find a character..
        if (ch != null && ch is Mob) {

            processSoulMark(ch, chargesPerCast())

            //heals an ally, or a charmed enemy
            if (ch.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ALLY || ch.buff<Charm>(Charm::class.java) != null) {

                val missingHP = ch.HT - ch.HP
                //heals 30%+3%*lvl missing HP.
                val healing = Math.ceil((missingHP * (0.30f + 0.03f * level())).toDouble()).toInt()
                ch.HP += healing
                ch.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1 + level() / 2)
                ch.sprite!!.showStatus(CharSprite.POSITIVE, "+%dHP", healing)

                //harms the undead
            } else if (ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.UNDEAD)) {

                //deals 30%+5%*lvl total HP.
                val damage = Math.ceil((ch.HT * (0.3f + 0.05f * level())).toDouble()).toInt()
                ch.damage(damage, this)
                ch.sprite!!.emitter().start(ShadowParticle.UP, 0.05f, 10 + level())
                Sample.INSTANCE.play(Assets.SND_BURNING)

                //charms an enemy
            } else {

                var duration = (5 + level()).toFloat()
                Buff.affect<Charm>(ch, Charm::class.java, duration).`object` = Item.curUser!!.id()

                duration *= Random.Float(0.75f, 1f)
                Buff.affect<Charm>(Item.curUser!!, Charm::class.java, duration).`object` = ch.id()

                ch.sprite!!.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5)
                Item.curUser!!.sprite!!.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5)

            }


            //if we find an item...
        } else if (heap != null && heap.type == Heap.Type.HEAP) {
            val item = heap.peek()

            //30% + 10%*lvl chance to uncurse the item and reset it to base level if degraded.
            if (item != null && Random.Float() <= 0.3f + level() * 0.1f) {
                if (item.cursed) {
                    item.cursed = false
                    CellEmitter.get(cell).start(ShadowParticle.UP, 0.05f, 10)
                    Sample.INSTANCE.play(Assets.SND_BURNING)
                }

                val lvldiffFromBase = item.level() - if (item is Ring) 1 else 0
                if (lvldiffFromBase < 0) {
                    item.upgrade(-lvldiffFromBase)
                    CellEmitter.get(cell).start(Speck.factory(Speck.UP), 0.2f, 3)
                    Sample.INSTANCE.play(Assets.SND_BURNING)
                }
            }

            //if we find some trampled grass...
        } else if (Dungeon.level!!.map!![cell] == Terrain.GRASS) {

            //regrow one grass tile, suuuuuper useful...
            Level.set(cell, Terrain.HIGH_GRASS)
            GameScene.updateMap(cell)
            CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4)

            //If we find embers...
        } else if (Dungeon.level!!.map!![cell] == Terrain.EMBERS) {

            //30% + 3%*lvl chance to grow a random plant, or just regrow grass.
            if (Random.Float() <= 0.3f + level() * 0.03f) {
                Dungeon.level!!.plant(Generator.random(Generator.Category.SEED) as Plant.Seed, cell)
                CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 8)
                GameScene.updateMap(cell)
            } else {
                Level.set(cell, Terrain.HIGH_GRASS)
                GameScene.updateMap(cell)
                CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4)
            }

        } else
            return  //don't damage the hero if we can't find a target;

        if (!freeCharge) {
            damageHero()
        } else {
            freeCharge = false
        }
    }

    //this wand costs health too
    private fun damageHero() {
        // 10% of max hp
        val damage = Math.ceil((Item.curUser!!.HT * 0.10f).toDouble()).toInt()
        Item.curUser!!.damage(damage, this)

        if (!Item.curUser!!.isAlive) {
            Dungeon.fail(javaClass)
            GLog.n(Messages.get(this.javaClass, "ondeath"))
        }
    }

    override fun initialCharges(): Int {
        return 1
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        // lvl 0 - 10%
        // lvl 1 - 18%
        // lvl 2 - 25%
        if (Random.Int(level() + 10) >= 9) {
            //grants a free use of the staff
            freeCharge = true
            GLog.p(Messages.get(this.javaClass, "charged"))
            attacker.sprite!!.emitter().burst(BloodParticle.BURST, 20)
        }
    }

    override fun fx(beam: Ballistica, callback: Callback) {
        Item.curUser!!.sprite!!.parent!!.add(
                Beam.HealthRay(Item.curUser!!.sprite!!.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos!!)))
        callback.call()
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(0xCC0000)
        particle.am = 0.6f
        particle.lifespan = (1f)
        particle.speed.polar(Random.Float(PointF.PI2), 2f)
        particle.setSize(1f, 2f)
        particle.radiateXY(0.5f)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        freeCharge = bundle.getBoolean(FREECHARGE)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(FREECHARGE, freeCharge)
    }

    companion object {

        private val FREECHARGE = "freecharge"
    }

}
