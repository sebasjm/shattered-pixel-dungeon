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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder
import com.watabou.utils.asCallback

import java.util.ArrayList

class LloydsBeacon : Artifact() {

    var returnDepth = -1
    var returnPos: Int = 0

    protected var zapper: CellSelector.Listener = object : CellSelector.Listener {

        override fun onSelect(target: Int?) {

            if (target == null) return

            Invisibility.dispel()
            charge -= if (Dungeon.depth > 20) 2 else 1
            updateQuickslot()

            if (Actor.findChar(target) === Item.curUser!!) {
                ScrollOfTeleportation.teleportHero(Item.curUser!!)
                Item.curUser!!.spendAndNext(1f)
            } else {
                val bolt = Ballistica(Item.curUser!!.pos, target, Ballistica.MAGIC_BOLT)
                val ch = Actor.findChar(bolt.collisionPos!!)

                if (ch === Item.curUser!!) {
                    ScrollOfTeleportation.teleportHero(Item.curUser!!)
                    Item.curUser!!.spendAndNext(1f)
                } else {
                    Sample.INSTANCE.play(Assets.SND_ZAP)
                    Item.curUser!!.sprite!!.zap(bolt.collisionPos!!)
                    Item.curUser!!.busy()

                    MagicMissile.boltFromChar(Item.curUser!!.sprite!!.parent!!,
                            MagicMissile.BEACON,
                            Item.curUser!!.sprite!!,
                            bolt.collisionPos!!, {
                                if (ch != null) {

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

                                    } else if (ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.IMMOVABLE)) {

                                        GLog.w(Messages.get(LloydsBeacon::class.java, "tele_fail"))

                                    } else {

                                        ch.pos = pos
                                        if (ch is Mob && ch.state === ch.HUNTING) {
                                            ch.state = ch.WANDERING
                                        }
                                        ch.sprite!!.place(ch.pos)
                                        ch.sprite!!.visible = Dungeon.level!!.heroFOV[pos]

                                    }
                                }
                                Item.curUser!!.spendAndNext(1f)
                            } .asCallback()
                    )

                }


            }

        }

        override fun prompt(): String {
            return Messages.get(LloydsBeacon::class.java, "prompt")
        }
    }

    init {
        image = ItemSpriteSheet.ARTIFACT_BEACON

        levelCap = 3

        charge = 0
        chargeCap = 3 + level()

        defaultAction = AC_ZAP
        usesTargeting = true
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DEPTH, returnDepth)
        if (returnDepth != -1) {
            bundle.put(POS, returnPos)
        }
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        returnDepth = bundle.getInt(DEPTH)
        returnPos = bundle.getInt(POS)
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_ZAP)
        actions.add(AC_SET)
        if (returnDepth != -1) {
            actions.add(AC_RETURN)
        }
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action === AC_SET || action === AC_RETURN) {

            if (Dungeon.bossLevel()) {
                hero.spend(LloydsBeacon.TIME_TO_USE)
                GLog.w(Messages.get(this.javaClass, "preventing"))
                return
            }

            for (i in PathFinder.NEIGHBOURS8!!.indices) {
                val ch = Actor.findChar(hero.pos + PathFinder.NEIGHBOURS8!![i])
                if (ch != null && ch.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ENEMY) {
                    GLog.w(Messages.get(this.javaClass, "creatures"))
                    return
                }
            }
        }

        if (action === AC_ZAP) {

            Item.curUser = hero
            val chargesToUse = if (Dungeon.depth > 20) 2 else 1

            if (!isEquipped(hero)) {
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
                QuickSlotButton.cancel()

            } else if (charge < chargesToUse) {
                GLog.i(Messages.get(this.javaClass, "no_charge"))
                QuickSlotButton.cancel()

            } else {
                GameScene.selectCell(zapper)
            }

        } else if (action === AC_SET) {

            returnDepth = Dungeon.depth
            returnPos = hero.pos

            hero.spend(LloydsBeacon.TIME_TO_USE)
            hero.busy()

            hero.sprite!!.operate(hero.pos)
            Sample.INSTANCE.play(Assets.SND_BEACON)

            GLog.i(Messages.get(this.javaClass, "return"))

        } else if (action === AC_RETURN) {

            if (returnDepth == Dungeon.depth) {
                ScrollOfTeleportation.appear(hero, returnPos)
                Dungeon.level!!.press(returnPos, hero)
                Dungeon.observe()
                GameScene.updateFog()
            } else {

                val buff = Dungeon.hero!!.buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)
                buff?.detach()

                InterlevelScene.mode = InterlevelScene.Mode.RETURN
                InterlevelScene.returnDepth = returnDepth
                InterlevelScene.returnPos = returnPos
                Game.switchScene(InterlevelScene::class.java)
            }


        }
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return beaconRecharge()
    }

    override fun upgrade(): Item {
        if (level() == levelCap) return this
        chargeCap++
        GLog.p(Messages.get(this.javaClass, "levelup"))
        return super.upgrade()
    }

    override fun desc(): String {
        var desc = super.desc()
        if (returnDepth != -1) {
            desc += "\n\n" + Messages.get(this.javaClass, "desc_set", returnDepth)
        }
        return desc
    }

    override fun glowing(): Glowing? {
        return if (returnDepth != -1) WHITE else null
    }

    inner class beaconRecharge : Artifact.ArtifactBuff() {
        override fun act(): Boolean {
            val lock = target!!.buff<LockedFloor>(LockedFloor::class.java)
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (100f - (chargeCap - charge) * 10f)

                if (partialCharge >= 1) {
                    partialCharge--
                    charge++

                    if (charge == chargeCap) {
                        partialCharge = 0f
                    }
                }
            }

            updateQuickslot()
            spend(Actor.TICK)
            return true
        }
    }

    companion object {

        val TIME_TO_USE = 1f

        val AC_ZAP = "ZAP"
        val AC_SET = "SET"
        val AC_RETURN = "RETURN"

        private val DEPTH = "depth"
        private val POS = "pos"

        private val WHITE = Glowing(0xFFFFFF)
    }
}
