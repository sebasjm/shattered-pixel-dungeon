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
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Bomb
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.Random

import java.io.IOException
import java.util.ArrayList

//helper class to contain all the cursed wand zapping logic, so the main wand class doesn't get huge.
object CursedWand {

    private val COMMON_CHANCE = 0.6f
    private val UNCOMMON_CHANCE = 0.3f
    private val RARE_CHANCE = 0.09f
    private val VERY_RARE_CHANCE = 0.01f

    fun cursedZap(wand: Wand, user: Hero, bolt: Ballistica) {
        when (Random.chances(floatArrayOf(COMMON_CHANCE, UNCOMMON_CHANCE, RARE_CHANCE, VERY_RARE_CHANCE))) {
            0 -> commonEffect(wand, user, bolt)
            1 -> uncommonEffect(wand, user, bolt)
            2 -> rareEffect(wand, user, bolt)
            3 -> veryRareEffect(wand, user, bolt)
            else -> commonEffect(wand, user, bolt)
        }
    }

    private fun commonEffect(wand: Wand, user: Hero, bolt: Ballistica) {
        when (Random.Int(4)) {

        //anti-entropy
            0 -> cursedFX(user, bolt, {
                val target = Actor.findChar(bolt.collisionPos!!)
                when (Random.Int(2)) {
                    0 -> {
                        if (target != null)
                            Buff.affect<Burning>(target, Burning::class.java)!!.reignite(target)
                        Buff.affect<Frost>(user, Frost::class.java, Frost.duration(user) * Random.Float(3f, 5f))
                    }
                    1 -> {
                        Buff.affect<Burning>(user, Burning::class.java)!!.reignite(user)
                        if (target != null)
                            Buff.affect<Frost>(target, Frost::class.java, Frost.duration(target) * Random.Float(3f, 5f))
                    }
                }
                wand.wandUsed()
            } as Callback )

        //spawns some regrowth
            1 -> cursedFX(user, bolt, {
                val c = Dungeon.level!!.map!![bolt.collisionPos!!]
                if (c == Terrain.EMPTY ||
                        c == Terrain.EMBERS ||
                        c == Terrain.EMPTY_DECO ||
                        c == Terrain.GRASS ||
                        c == Terrain.HIGH_GRASS) {
                    GameScene.add(Blob.seed<Regrowth>(bolt.collisionPos!!, 30, Regrowth::class.java)!!)
                }
                wand.wandUsed()
            } as Callback)

        //random teleportation
            2 -> when (Random.Int(2)) {
                0 -> {
                    ScrollOfTeleportation.teleportHero(user)
                    wand.wandUsed()
                }
                1 -> cursedFX(user, bolt, {
                    val ch = Actor.findChar(bolt.collisionPos!!)
                    if (ch === user) {
                        ScrollOfTeleportation.teleportHero(user)
                        wand.wandUsed()
                    } else if (ch != null && !ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.IMMOVABLE)) {
                        var count = 10
                        var pos: Int
                        do {
                            pos = Dungeon.level!!.randomRespawnCell()
                            if (count-- <= 0) {
                                break
                            }
                        } while (pos == -1)
                        if (pos == -1 || Dungeon.bossLevel()) {
                            GLog.w(Messages.get(ScrollOfTeleportation::class.java, "no_tele"))
                        } else {
                            ch.pos = pos
                            if ((ch as Mob).state === (ch as Mob).HUNTING) (ch as Mob).state = ch.WANDERING
                            ch.sprite!!.place(ch.pos)
                            ch.sprite!!.visible = Dungeon.level!!.heroFOV[pos]
                        }
                    }
                    wand.wandUsed()
                } as Callback )
            }

        //random gas at location
            3 -> cursedFX(user, bolt, {
                when (Random.Int(3)) {
                    0 -> GameScene.add(Blob.seed<ConfusionGas>(bolt.collisionPos!!, 800, ConfusionGas::class.java)!!)
                    1 -> GameScene.add(Blob.seed<ToxicGas>(bolt.collisionPos!!, 500, ToxicGas::class.java)!!)
                    2 -> GameScene.add(Blob.seed<ParalyticGas>(bolt.collisionPos!!, 200, ParalyticGas::class.java)!!)
                }
                wand.wandUsed()
            } as Callback )
        }

    }

    private fun uncommonEffect(wand: Wand, user: Hero, bolt: Ballistica) {
        when (Random.Int(4)) {

        //Random plant
            0 -> cursedFX(user, bolt, {
                var pos = bolt.collisionPos!!
                //place the plant infront of an enemy so they walk into it.
                if (Actor.findChar(pos) != null && bolt.dist!! > 1) {
                    pos = bolt.path[bolt.dist!! - 1]
                }

                if (pos == Terrain.EMPTY ||
                        pos == Terrain.EMBERS ||
                        pos == Terrain.EMPTY_DECO ||
                        pos == Terrain.GRASS ||
                        pos == Terrain.HIGH_GRASS) {
                    Dungeon.level!!.plant(Generator.random(Generator.Category.SEED) as Plant.Seed, pos)
                }
                wand.wandUsed()
            } as Callback )

        //Health transfer
            1 -> {
                val target = Actor.findChar(bolt.collisionPos!!)
                if (target != null) {
                    cursedFX(user, bolt, object : Callback {
                        override fun call() {
                            val damage = user.lvl * 2
                            when (Random.Int(2)) {
                                0 -> {
                                    user.HP = Math.min(user.HT, user.HP + damage)
                                    user.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 3)
                                    target.damage(damage, wand)
                                    target.sprite!!.emitter().start(ShadowParticle.UP, 0.05f, 10)
                                }
                                1 -> {
                                    user.damage(damage, this)
                                    user.sprite!!.emitter().start(ShadowParticle.UP, 0.05f, 10)
                                    target.HP = Math.min(target.HT, target.HP + damage)
                                    target.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 3)
                                    Sample.INSTANCE.play(Assets.SND_CURSED)
                                    if (!user.isAlive) {
                                        Dungeon.fail(wand.javaClass)
                                        GLog.n(Messages.get(CursedWand::class.java, "ondeath", wand.name()))
                                    }
                                }
                            }
                            wand.wandUsed()
                        }
                    })
                } else {
                    GLog.i(Messages.get(CursedWand::class.java, "nothing"))
                    wand.wandUsed()
                }
            }

        //Bomb explosion
            2 -> cursedFX(user, bolt, {
                Bomb().explode(bolt.collisionPos!!)
                wand.wandUsed()
            } as Callback )

        //shock and recharge
            3 -> {
                ShockingTrap().set(user.pos).activate()
                Buff.prolong<Recharging>(user, Recharging::class.java, 20f)
                ScrollOfRecharging.charge(user)
                SpellSprite.show(user, SpellSprite.CHARGE)
                wand.wandUsed()
            }
        }

    }

    private fun rareEffect(wand: Wand, user: Hero, bolt: Ballistica) {
        when (Random.Int(4)) {

        //sheep transformation
            0 -> cursedFX(user, bolt, {
                val ch = Actor.findChar(bolt.collisionPos!!)

                if (ch != null && ch !== user
                        && !ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.BOSS)
                        && !ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.MINIBOSS)) {
                    val sheep = Sheep()
                    sheep.lifespan = 10f
                    sheep.pos = ch.pos
                    ch.destroy()
                    ch.sprite!!.killAndErase()
                    Dungeon.level!!.mobs.remove(ch)
                    TargetHealthIndicator.instance!!.target(null)
                    GameScene.add(sheep)
                    CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4)
                } else {
                    GLog.i(Messages.get(CursedWand::class.java, "nothing"))
                }
                wand.wandUsed()
            } as Callback )

        //curses!
            1 -> {
                CursingTrap.curse(user)
                wand.wandUsed()
            }

        //inter-level teleportation
            2 -> if (Dungeon.depth > 1 && !Dungeon.bossLevel()) {

                //each depth has 1 more weight than the previous depth.
                val depths = FloatArray(Dungeon.depth - 1)
                for (i in 1 until Dungeon.depth) depths[i - 1] = i.toFloat()
                val depth = 1 + Random.chances(depths)

                val buff = Dungeon.hero!!.buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)
                buff?.detach()

                InterlevelScene.mode = InterlevelScene.Mode.RETURN
                InterlevelScene.returnDepth = depth
                InterlevelScene.returnPos = -1
                Game.switchScene(InterlevelScene::class.java)

            } else {
                ScrollOfTeleportation.teleportHero(user)
                wand.wandUsed()
            }

        //summon monsters
            3 -> {
                SummoningTrap().set(user.pos).activate()
                wand.wandUsed()
            }
        }
    }

    private fun veryRareEffect(wand: Wand, user: Hero, bolt: Ballistica) {
        when (Random.Int(4)) {

        //great forest fire!
            0 -> {
                for (i in 0 until Dungeon.level!!.length()) {
                    val c = Dungeon.level!!.map!![i]
                    if (c == Terrain.EMPTY ||
                            c == Terrain.EMBERS ||
                            c == Terrain.EMPTY_DECO ||
                            c == Terrain.GRASS ||
                            c == Terrain.HIGH_GRASS) {
                        GameScene.add(Blob.seed<Regrowth>(i, 15, Regrowth::class.java)!!)
                    }
                }
                do {
                    GameScene.add(Blob.seed<Fire>(Dungeon.level!!.randomDestination(), 10, Fire::class.java)!!)
                } while (Random.Int(5) != 0)
                Flare(8, 32f).color(0xFFFF66, true).show(user.sprite!!, 2f)
                Sample.INSTANCE.play(Assets.SND_TELEPORT)
                GLog.p(Messages.get(CursedWand::class.java, "grass"))
                GLog.w(Messages.get(CursedWand::class.java, "fire"))
                wand.wandUsed()
            }

        //superpowered mimic
            1 -> cursedFX(user, bolt, {
                val mimic = Mimic.spawnAt(bolt.collisionPos!!, ArrayList())
                if (mimic != null) {
                    mimic.adjustStats(Dungeon.depth + 10)
                    mimic.HP = mimic.HT
                    var reward: Item?
                    do {
                        reward = Generator.random(Random.oneOf<Generator.Category>(Generator.Category.WEAPON, Generator.Category.ARMOR,
                                Generator.Category.RING, Generator.Category.WAND))
                    } while (reward!!.level() < 1)
                    Sample.INSTANCE.play(Assets.SND_MIMIC, 1f, 1f, 0.5f)
                    mimic.items!!.clear()
                    mimic.items!!.add(reward)
                } else {
                    GLog.i(Messages.get(CursedWand::class.java, "nothing"))
                }

                wand.wandUsed()
            } as Callback)

        //crashes the game, yes, really.
            2 -> try {
                Dungeon.saveAll()
                if (Messages.lang() != Languages.ENGLISH) {
                    //Don't bother doing this joke to none-english speakers, I doubt it would translate.
                    GLog.i(Messages.get(CursedWand::class.java, "nothing"))
                    wand.wandUsed()
                } else {
                    GameScene.show(
                            object : WndOptions("CURSED WAND ERROR", "this application will now self-destruct", "abort", "retry", "fail") {

                                override fun onSelect(index: Int) {
                                    Game.instance!!.finish()
                                }

                                override fun onBackPressed() {
                                    //do nothing
                                }
                            }
                    )
                }
            } catch (e: IOException) {
                Game.reportException(e)
                //oookay maybe don't kill the game if the save failed.
                GLog.i(Messages.get(CursedWand::class.java, "nothing"))
                wand.wandUsed()
            }

        //random transmogrification
            3 -> {
                wand.wandUsed()
                wand.detach(user.belongings.backpack)
                var result: Item?
                do {
                    result = Generator.random(Random.oneOf<Generator.Category>(Generator.Category.WEAPON, Generator.Category.ARMOR,
                            Generator.Category.RING, Generator.Category.ARTIFACT))
                } while (result!!.cursed)
                if (result.isUpgradable) result.upgrade()
                result.cursedKnown = true
                result.cursed = result.cursedKnown
                GLog.w(Messages.get(CursedWand::class.java, "transmogrify"))
                Dungeon.level!!.drop(result, user.pos).sprite!!.drop()
                wand.wandUsed()
            }
        }
    }

    private fun cursedFX(user: Hero, bolt: Ballistica, callback: Callback) {
        MagicMissile.boltFromChar(user.sprite!!.parent!!,
                MagicMissile.RAINBOW,
                user.sprite!!,
                bolt.collisionPos!!,
                callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

}
