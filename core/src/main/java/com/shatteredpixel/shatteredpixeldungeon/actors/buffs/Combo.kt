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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.utils.*

class Combo : Buff(), ActionIndicator.Action {

    private var count = 0
    private var comboTime = 0f
    private var misses = 0

    override val icon: Image
        get() {
            val icon: Image
            if ((target!! as Hero).belongings.weapon != null) {
                icon = ItemSprite((target!! as Hero).belongings.weapon!!.image, null)
            } else {
                icon = ItemSprite(object : Item() {
                    init {
                        image = ItemSpriteSheet.WEAPON_HOLDER
                    }
                })
            }

            if (count >= 10)
                icon.tint(-0x10000)
            else if (count >= 8)
                icon.tint(-0x3400)
            else if (count >= 6)
                icon.tint(-0x100)
            else if (count >= 4)
                icon.tint(-0x330100)
            else
                icon.tint(-0xff0100)

            return icon
        }

    private val finisher = object : CellSelector.Listener {

        private var type: finisherType? = null

        override fun onSelect(cell: Int?) {
            if (cell == null) return
            val enemy = Actor.findChar(cell)
            if (enemy == null
                    || !Dungeon.level!!.heroFOV[cell]
                    || !(target!! as Hero).canAttack(enemy)
                    || target!!.isCharmedBy(enemy)) {
                GLog.w(Messages.get(Combo::class.java, "bad_target"))
            } else {
                target!!.sprite!!.attack(cell, {
                    if (count >= 10)
                        type = finisherType.FURY
                    else if (count >= 8)
                        type = finisherType.CRUSH
                    else if (count >= 6)
                        type = finisherType.SLAM
                    else if (count >= 4)
                        type = finisherType.CLEAVE
                    else
                        type = finisherType.CLOBBER
                    doAttack(enemy)
                } .asCallback())
            }
        }

        private fun doAttack(enemy: Char?) {

            AttackIndicator.target(enemy!!)

            var dmg = target!!.damageRoll()

            //variance in damage dealt
            when (type) {
                Combo.finisherType.CLOBBER -> dmg = Math.round(dmg * 0.6f)
                Combo.finisherType.CLEAVE -> dmg = Math.round(dmg * 1.5f)
                Combo.finisherType.SLAM -> {
                    //rolls 2 times, takes the highest roll
                    var dmgReroll = target!!.damageRoll()
                    if (dmgReroll > dmg) dmg = dmgReroll
                    dmg = Math.round(dmg * 1.6f)
                }
                Combo.finisherType.CRUSH -> {
                    //rolls 4 times, takes the highest roll
                    for (i in 1..3) {
                        var dmgReroll = target!!.damageRoll()
                        if (dmgReroll > dmg) dmg = dmgReroll
                    }
                    dmg = Math.round(dmg * 2.5f)
                }
                Combo.finisherType.FURY -> dmg = Math.round(dmg * 0.6f)
            }

            dmg -= enemy!!.drRoll()
            dmg = target!!.attackProc(enemy, dmg)
            dmg = enemy.defenseProc(target!!, dmg)
            enemy.damage(dmg, this)

            //special effects
            when (type) {
                Combo.finisherType.CLOBBER -> if (enemy.isAlive) {
                    if (!enemy.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.IMMOVABLE)) {
                        for (i in PathFinder.NEIGHBOURS8!!.indices) {
                            val ofs = PathFinder.NEIGHBOURS8!![i]
                            if (enemy.pos - target!!.pos == ofs) {
                                val newPos = enemy.pos + ofs
                                if ((Dungeon.level!!.passable[newPos] || Dungeon.level!!.avoid[newPos]) && Actor.findChar(newPos) == null) {

                                    Actor.addDelayed(Pushing(enemy, enemy.pos, newPos), -1f)

                                    enemy.pos = newPos
                                    Dungeon.level!!.press(newPos, enemy)

                                }
                                break
                            }
                        }
                    }
                    Buff.prolong<Vertigo>(enemy, Vertigo::class.java, Random.NormalIntRange(1, 4).toFloat())
                }
                Combo.finisherType.SLAM -> target!!.SHLD = Math.max(target!!.SHLD, dmg / 2)
                else -> {
                }
            }//nothing

            if (target!!.buff<FireImbue>(FireImbue::class.java) != null)
                target!!.buff<FireImbue>(FireImbue::class.java)!!.proc(enemy)
            if (target!!.buff<EarthImbue>(EarthImbue::class.java) != null)
                target!!.buff<EarthImbue>(EarthImbue::class.java)!!.proc(enemy)

            Sample.INSTANCE.play(Assets.SND_HIT, 1f, 1f, Random.Float(0.8f, 1.25f))
            enemy.sprite!!.bloodBurstA(target!!.sprite!!.center(), dmg)
            enemy.sprite!!.flash()

            if (!enemy.isAlive) {
                GLog.i(Messages.capitalize(Messages.get(Char::class.java, "defeat", enemy.name)))
            }

            val hero = target!! as Hero

            //Post-attack behaviour
            when (type) {
                Combo.finisherType.CLEAVE -> {
                    if (!enemy.isAlive) {
                        //combo isn't reset, but rather increments with a cleave kill, and grants more time.
                        hit()
                        comboTime = 10f
                    } else {
                        detach()
                        ActionIndicator.clearAction(this@Combo)
                    }
                    hero.spendAndNext(hero.attackDelay())
                }

                Combo.finisherType.FURY -> {
                    count--
                    //fury attacks as many times as you have combo count
                    if (count > 0 && enemy.isAlive) {
                        target!!.sprite!!.attack(enemy.pos, { doAttack(enemy) } .asCallback())
                    } else {
                        detach()
                        ActionIndicator.clearAction(this@Combo)
                        hero.spendAndNext(hero.attackDelay())
                    }
                }

                else -> {
                    detach()
                    ActionIndicator.clearAction(this@Combo)
                    hero.spendAndNext(hero.attackDelay())
                }
            }

        }

        override fun prompt(): String {
            return if (count >= 10)
                Messages.get(Combo::class.java, "fury_prompt")
            else if (count >= 8)
                Messages.get(Combo::class.java, "crush_prompt")
            else if (count >= 6)
                Messages.get(Combo::class.java, "slam_prompt")
            else if (count >= 4)
                Messages.get(Combo::class.java, "cleave_prompt")
            else
                Messages.get(Combo::class.java, "clobber_prompt")
        }
    }

    override fun icon(): Int {
        return BuffIndicator.COMBO
    }

    override fun tintIcon(icon: Image) {
        if (comboTime >= 3f) {
            icon.resetColor()
        } else {
            icon.tint(0xb3b3b3, 0.5f + 0.5f * (3f + 1 - comboTime) / 3f)
        }
    }

    override fun toString(): String {
        return Messages.get(this.javaClass, "name")
    }

    fun hit() {

        count++
        comboTime = 4f
        misses = 0
        BuffIndicator.refreshHero()

        if (count >= 2) {

            ActionIndicator.action = this
            Badges.validateMasteryCombo(count)

            GLog.p(Messages.get(this.javaClass, "combo", count))

        }

    }

    fun miss() {
        misses++
        comboTime = 4f
        if (misses >= 2) {
            detach()
        }
    }

    override fun detach() {
        super.detach()
        ActionIndicator.clearAction(this)
    }

    override fun act(): Boolean {
        comboTime -= Actor.TICK
        spend(Actor.TICK)
        BuffIndicator.refreshHero()
        if (comboTime <= 0) {
            detach()
        }
        return true
    }

    override fun desc(): String {
        var desc = Messages.get(this.javaClass, "desc")

        if (count >= 10)
            desc += "\n\n" + Messages.get(this.javaClass, "fury_desc")
        else if (count >= 8)
            desc += "\n\n" + Messages.get(this.javaClass, "crush_desc")
        else if (count >= 6)
            desc += "\n\n" + Messages.get(this.javaClass, "slam_desc")
        else if (count >= 4)
            desc += "\n\n" + Messages.get(this.javaClass, "cleave_desc")
        else if (count >= 2) desc += "\n\n" + Messages.get(this.javaClass, "clobber_desc")

        return desc
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(COUNT, count)
        bundle.put(TIME, comboTime)
        bundle.put(MISSES, misses)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        count = bundle.getInt(COUNT)
        if (count >= 2) ActionIndicator.action = this
        comboTime = bundle.getFloat(TIME)
        misses = bundle.getInt(MISSES)
    }

    override fun doAction() {
        GameScene.selectCell(finisher)
    }

    private enum class finisherType {
        CLOBBER, CLEAVE, SLAM, CRUSH, FURY
    }

    companion object {

        private val COUNT = "count"
        private val TIME = "combotime"
        private val MISSES = "misses"
    }
}
