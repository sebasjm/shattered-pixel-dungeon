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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder

import java.util.Arrays
import java.util.Collections

class Preparation : Buff(), ActionIndicator.Action {

    private var turnsInvis = 0

    override val icon: Image
        get() {
            val actionIco = Effects.get(Effects.Type.WOUND)
            tintIcon(actionIco)
            return actionIco
        }

    private val attack = object : CellSelector.Listener {

        override fun onSelect(cell: Int?) {
            if (cell == null) return
            val enemy = Actor.findChar(cell)
            if (enemy == null || Dungeon.hero!!.isCharmedBy(enemy) || enemy is NPC) {
                GLog.w(Messages.get(Preparation::class.java, "no_target"))
            } else {

                //just attack them then!
                if (Dungeon.hero!!.canAttack(enemy)) {
                    if (Dungeon.hero!!.handle(cell)) {
                        Dungeon.hero!!.next()
                    }
                    return
                }

                val lvl = AttackLevel.getLvl(turnsInvis)

                val passable = Dungeon.level!!.passable.clone()
                PathFinder.buildDistanceMap(Dungeon.hero!!.pos, passable, lvl.blinkDistance + 1)
                if (PathFinder.distance[cell] == Integer.MAX_VALUE) {
                    GLog.w(Messages.get(Preparation::class.java, "out_of_reach"))
                    return
                }

                //we can move through enemies when determining blink distance,
                // but not when actually jumping to a location
                for (ch in Actor.chars()) {
                    if (ch !== Dungeon.hero) passable[ch.pos] = false
                }

                val path = PathFinder.find(Dungeon.hero!!.pos, cell, passable)
                val attackPos = if (path == null) -1 else path[path.size - 2]

                if (attackPos == -1 || Dungeon.level!!.distance(attackPos, Dungeon.hero!!.pos) > lvl.blinkDistance) {
                    GLog.w(Messages.get(Preparation::class.java, "out_of_reach"))
                    return
                }

                Dungeon.hero!!.pos = attackPos
                Dungeon.level!!.press(Dungeon.hero!!.pos, Dungeon.hero)
                //prevents the hero from being interrupted by seeing new enemies
                Dungeon.observe()
                Dungeon.hero!!.checkVisibleMobs()

                Dungeon.hero!!.sprite!!.place(Dungeon.hero!!.pos)
                Dungeon.hero!!.sprite!!.turnTo(Dungeon.hero!!.pos, cell)
                CellEmitter.get(Dungeon.hero!!.pos).burst(Speck.factory(Speck.WOOL), 6)
                Sample.INSTANCE.play(Assets.SND_PUFF)

                if (Dungeon.hero!!.handle(cell)) {
                    Dungeon.hero!!.next()
                }
            }
        }

        override fun prompt(): String {
            return Messages.get(Preparation::class.java, "prompt", AttackLevel.getLvl(turnsInvis).blinkDistance)
        }
    }

    init {
        //always acts after other buffs, so invisibility effects can process first
        actPriority = BUFF_PRIO - 1
    }

    enum class AttackLevel private constructor(internal val turnsReq: Int, internal val baseDmgBonus: Float, internal val missingHPBonus: Float, internal val damageRolls: Int, internal val blinkDistance: Int) {
        LVL_1(1, 0.1f, 0.0f, 1, 0),
        LVL_2(3, 0.2f, 0.0f, 1, 1),
        LVL_3(6, 0.3f, 0.0f, 2, 3),
        LVL_4(11, 0.4f, 0.6f, 2, 5),
        LVL_5(16, 0.5f, 1.0f, 3, 7);

        fun canInstakill(defender: Char): Boolean {
            return (this == LVL_5
                    && !defender.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.MINIBOSS)
                    && !defender.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.BOSS))
        }

        fun damageRoll(attacker: Char, defender: Char): Int {
            var dmg = attacker.damageRoll()
            for (i in 1 until damageRolls) {
                val newDmg = attacker.damageRoll()
                if (newDmg > dmg) dmg = newDmg
            }
            val defenderHPPercent = 1f - defender.HP / defender.HT.toFloat()
            return Math.round(dmg * (1f + baseDmgBonus + missingHPBonus * defenderHPPercent))
        }

        companion object {

            fun getLvl(turnsInvis: Int): AttackLevel {
                val values = Arrays.asList(*values())
                Collections.reverse(values)
                for (lvl in values) {
                    if (turnsInvis >= lvl.turnsReq) {
                        return lvl
                    }
                }
                return LVL_1
            }
        }
    }

    override fun act(): Boolean {
        if (target.invisible > 0) {
            turnsInvis++
            if (AttackLevel.getLvl(turnsInvis).blinkDistance > 0 && target === Dungeon.hero) {
                ActionIndicator.setAction(this)
            }
            BuffIndicator.refreshHero()
            spend(Actor.TICK)
        } else {
            detach()
        }
        return true
    }

    override fun detach() {
        super.detach()
        ActionIndicator.clearAction(this)
    }

    fun damageRoll(attacker: Char, defender: Char): Int {
        val lvl = AttackLevel.getLvl(turnsInvis)
        if (lvl.canInstakill(defender)) {
            val dmg = lvl.damageRoll(attacker, defender)
            defender.damage(Math.max(defender.HT, dmg), attacker)
            //even though the defender is dead, other effects should still proc (enchants, etc.)
            return Math.max(defender.HT, dmg)
        } else {
            return lvl.damageRoll(attacker, defender)
        }
    }

    override fun icon(): Int {
        return BuffIndicator.PREPARATION
    }

    override fun tintIcon(icon: Image) {
        when (AttackLevel.getLvl(turnsInvis)) {
            Preparation.AttackLevel.LVL_1 -> icon.hardlight(1f, 1f, 1f)
            Preparation.AttackLevel.LVL_2 -> icon.hardlight(0f, 1f, 0f)
            Preparation.AttackLevel.LVL_3 -> icon.hardlight(1f, 1f, 0f)
            Preparation.AttackLevel.LVL_4 -> icon.hardlight(1f, 0.6f, 0f)
            Preparation.AttackLevel.LVL_5 -> icon.hardlight(1f, 0f, 0f)
        }
    }

    override fun toString(): String {
        return Messages.get(this, "name")
    }

    override fun desc(): String {
        var desc = Messages.get(this, "desc")

        val lvl = AttackLevel.getLvl(turnsInvis)

        if (lvl.canInstakill(Rat())) {
            desc += "\n\n" + Messages.get(this, "desc_dmg_instakill",
                    (lvl.baseDmgBonus * 100).toInt(),
                    (lvl.baseDmgBonus * 100 + lvl.missingHPBonus * 100).toInt())
        } else if (lvl.missingHPBonus > 0) {
            desc += "\n\n" + Messages.get(this, "desc_dmg_scale",
                    (lvl.baseDmgBonus * 100).toInt(),
                    (lvl.baseDmgBonus * 100 + lvl.missingHPBonus * 100).toInt())
        } else {
            desc += "\n\n" + Messages.get(this, "desc_dmg", (lvl.baseDmgBonus * 100).toInt())
        }

        if (lvl.damageRolls > 1) {
            desc += " " + Messages.get(this, "desc_dmg_likely")
        }

        if (lvl.blinkDistance > 0) {
            desc += "\n\n" + Messages.get(this, "desc_blink", lvl.blinkDistance)
        }

        desc += "\n\n" + Messages.get(this, "desc_invis_time", turnsInvis)

        if (lvl.ordinal != AttackLevel.values().size - 1) {
            val next = AttackLevel.values()[lvl.ordinal + 1]
            desc += "\n" + Messages.get(this, "desc_invis_next", next.turnsReq)
        }

        return desc
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        turnsInvis = bundle.getInt(TURNS)
        if (AttackLevel.getLvl(turnsInvis).blinkDistance > 0) {
            ActionIndicator.setAction(this)
        }
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(TURNS, turnsInvis)
    }

    override fun doAction() {
        GameScene.selectCell(attack)
    }

    companion object {

        private val TURNS = "turnsInvis"
    }
}
