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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.PathFinder
import com.watabou.utils.Random
import com.watabou.utils.asCallback

import java.util.ArrayList

class EtherealChains : Artifact() {

    private val caster = object : CellSelector.Listener {

        override fun onSelect(target: Int?) {
            if (target != null && (Dungeon.level!!.visited!![target] || Dungeon.level!!.mapped!![target])) {

                //chains cannot be used to go where it is impossible to walk to
                PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level!!.passable, Dungeon.level!!.avoid, null))
                if (PathFinder.distance!![Item.curUser!!.pos] == Integer.MAX_VALUE) {
                    GLog.w(Messages.get(EtherealChains::class.java, "cant_reach"))
                    return
                }

                val chain = Ballistica(Item.curUser!!.pos, target, Ballistica.STOP_TARGET)

                if (Actor.findChar(chain.collisionPos!!) != null) {
                    chainEnemy(chain, Item.curUser!!, Actor.findChar(chain.collisionPos!!)!!)
                } else {
                    chainLocation(chain, Item.curUser!!)
                }

            }

        }

        override fun prompt(): String {
            return Messages.get(EtherealChains::class.java, "prompt")
        }
    }

    init {
        image = ItemSpriteSheet.ARTIFACT_CHAINS

        levelCap = 5
        exp = 0

        charge = 5

        defaultAction = AC_CAST
        usesTargeting = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && charge > 0 && !cursed)
            actions.add(AC_CAST)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_CAST) {

            Item.curUser = hero

            if (!isEquipped(hero)) {
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
                QuickSlotButton.cancel()

            } else if (charge < 1) {
                GLog.i(Messages.get(this.javaClass, "no_charge"))
                QuickSlotButton.cancel()

            } else if (cursed) {
                GLog.w(Messages.get(this.javaClass, "cursed"))
                QuickSlotButton.cancel()

            } else {
                GameScene.selectCell(caster)
            }

        }
    }

    //pulls an enemy to a position along the chain's path, as close to the hero as possible
    private fun chainEnemy(chain: Ballistica, hero: Hero?, enemy: Char) {

        if (enemy.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.IMMOVABLE)) {
            GLog.w(Messages.get(this.javaClass, "cant_pull"))
            return
        }

        var bestPos = -1
        for (i in chain.subPath(1, chain.dist!!)) {
            //prefer to the earliest point on the path
            if (!Dungeon.level!!.solid[i] && Actor.findChar(i) == null) {
                bestPos = i
                break
            }
        }

        if (bestPos == -1) {
            GLog.i(Messages.get(this.javaClass, "does_nothing"))
            return
        }

        val pulledPos = bestPos

        val chargeUse = Dungeon.level!!.distance(enemy.pos, pulledPos)
        if (chargeUse > charge) {
            GLog.w(Messages.get(this.javaClass, "no_charge"))
            return
        } else {
            charge -= chargeUse
            updateQuickslot()
        }

        hero!!.busy()
        hero.sprite!!.parent!!.add(Chains(hero.sprite!!.center(), enemy.sprite!!.center(), {
            Actor.add(Pushing(enemy, enemy.pos, pulledPos, { Dungeon.level!!.press(pulledPos, enemy, true) } .asCallback() ))
            enemy.pos = pulledPos
            Dungeon.observe()
            GameScene.updateFog()
            hero.spendAndNext(1f)
        } .asCallback() ))
    }

    //pulls the hero along the chain to the collosionPos, if possible.
    private fun chainLocation(chain: Ballistica, hero: Hero?) {

        //don't pull if the collision spot is in a wall
        if (Dungeon.level!!.solid[chain.collisionPos!!]) {
            GLog.i(Messages.get(this.javaClass, "inside_wall"))
            return
        }

        //don't pull if there are no solid objects next to the pull location
        var solidFound = false
        for (i in PathFinder.NEIGHBOURS8!!) {
            if (Dungeon.level!!.solid[chain.collisionPos!! + i]) {
                solidFound = true
                break
            }
        }
        if (!solidFound) {
            GLog.i(Messages.get(EtherealChains::class.java, "nothing_to_grab"))
            return
        }

        val newHeroPos = chain.collisionPos!!

        val chargeUse = Dungeon.level!!.distance(hero!!.pos, newHeroPos)
        if (chargeUse > charge) {
            GLog.w(Messages.get(EtherealChains::class.java, "no_charge"))
            return
        } else {
            charge -= chargeUse
            updateQuickslot()
        }

        hero.busy()
        hero.sprite!!.parent!!.add(Chains(hero.sprite!!.center(), DungeonTilemap.raisedTileCenterToWorld(newHeroPos), {
            Actor.add(Pushing(hero, hero.pos, newHeroPos, { Dungeon.level!!.press(newHeroPos, hero) } .asCallback() ))
            hero.spendAndNext(1f)
            hero.pos = newHeroPos
            Dungeon.observe()
            GameScene.updateFog()
        } .asCallback() ))
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return chainsRecharge()
    }

    override fun desc(): String {
        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!)) {
            desc += "\n\n"
            if (cursed)
                desc += Messages.get(this.javaClass, "desc_cursed")
            else
                desc += Messages.get(this.javaClass, "desc_equipped")
        }
        return desc
    }

    inner class chainsRecharge : Artifact.ArtifactBuff() {

        override fun act(): Boolean {
            val chargeTarget = 5 + level() * 2
            val lock = target!!.buff<LockedFloor>(LockedFloor::class.java)
            if (charge < chargeTarget && !cursed && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (40f - (chargeTarget - charge) * 2f)
            } else if (cursed && Random.Int(100) == 0) {
                Buff.prolong<Cripple>(target!!, Cripple::class.java, 10f)
            }

            if (partialCharge >= 1) {
                partialCharge--
                charge++
            }

            updateQuickslot()

            spend(Actor.TICK)

            return true
        }

        fun gainExp(levelPortion: Float) {
            var levelPortion = levelPortion
            if (cursed) return

            exp += Math.round(levelPortion * 100)

            //past the soft charge cap, gaining  charge from leveling is slowed.
            if (charge > 5 + level() * 2) {
                levelPortion *= (5 + level().toFloat() * 2) / charge
            }
            partialCharge += levelPortion * 10f

            if (exp > 100 + level() * 50 && level() < levelCap) {
                exp -= 100 + level() * 50
                GLog.p(Messages.get(this.javaClass, "levelup"))
                upgrade()
            }

        }
    }

    companion object {

        val AC_CAST = "CAST"
    }
}
