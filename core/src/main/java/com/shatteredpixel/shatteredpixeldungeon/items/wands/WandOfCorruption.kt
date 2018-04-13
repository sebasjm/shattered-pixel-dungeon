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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.King
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Yog
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.Random

import java.util.HashMap

//TODO final balancing decisions here
class WandOfCorruption : Wand() {

    init {
        image = ItemSpriteSheet.WAND_CORRUPTION
    }

    override fun onZap(bolt: Ballistica) {
        val ch = Actor.findChar(bolt.collisionPos!!)

        if (ch != null) {

            if (ch !is Mob) {
                return
            }

            val enemy = ch

            val corruptingPower = (2 + level()).toFloat()

            //base enemy resistance is usually based on their exp, but in special cases it is based on other criteria
            var enemyResist = (1 + enemy.EXP).toFloat()
            if (ch is Mimic || ch is Statue) {
                enemyResist = (1 + Dungeon.depth).toFloat()
            } else if (ch is Piranha || ch is Bee) {
                enemyResist = 1 + Dungeon.depth / 2f
            } else if (ch is Wraith) {
                //this is so low because wraiths are always at max hp
                enemyResist = 0.5f + Dungeon.depth / 8f
            } else if (ch is Yog.BurningFist || ch is Yog.RottingFist) {
                enemyResist = (1 + 30).toFloat()
            } else if (ch is Yog.Larva || ch is King.Undead) {
                enemyResist = (1 + 5).toFloat()
            } else if (ch is Swarm) {
                //child swarms don't give exp, so we force this here.
                enemyResist = (1 + 3).toFloat()
            }

            //100% health: 3x resist   75%: 2.1x resist   50%: 1.5x resist   25%: 1.1x resist
            enemyResist *= (1 + 2 * Math.pow((enemy.HP / enemy.HT.toFloat()).toDouble(), 2.0)).toFloat()

            //debuffs placed on the enemy reduce their resistance
            for (buff in enemy.buffs()) {
                if (MAJOR_DEBUFFS.containsKey(buff.javaClass))
                    enemyResist *= MAJOR_DEBUFF_WEAKEN
                else if (MINOR_DEBUFFS.containsKey(buff.javaClass))
                    enemyResist *= MINOR_DEBUFF_WEAKEN
                else if (buff.type == Buff.buffType.NEGATIVE) enemyResist *= MINOR_DEBUFF_WEAKEN
            }

            //cannot re-corrupt or doom an enemy, so give them a major debuff instead
            if (enemy.buff<Corruption>(Corruption::class.java) != null || enemy.buff<Doom>(Doom::class.java) != null) {
                enemyResist = corruptingPower * .99f
            }

            if (corruptingPower > enemyResist) {
                corruptEnemy(enemy)
            } else {
                val debuffChance = corruptingPower / enemyResist
                if (Random.Float() < debuffChance) {
                    debuffEnemy(enemy, MAJOR_DEBUFFS)
                } else {
                    debuffEnemy(enemy, MINOR_DEBUFFS)
                }
            }

            processSoulMark(ch, chargesPerCast())

        } else {
            Dungeon.level!!.press(bolt.collisionPos!!, null, true)
        }
    }

    private fun debuffEnemy(enemy: Mob, category: HashMap<Class<out Buff>, Float>) {

        //do not consider buffs which are already assigned, or that the enemy is immune to.
        val debuffs = HashMap(category)
        for (existing in enemy.buffs()) {
            if (debuffs.containsKey(existing.javaClass)) {
                debuffs[existing.javaClass] = 0f
            }
        }
        for (toAssign in debuffs.keys) {
            if (debuffs[toAssign] > 0 && enemy.isImmune(toAssign)) {
                debuffs[toAssign] = 0f
            }
        }

        //all buffs with a > 0 chance are flavor buffs
        val debuffCls = Random.chances(debuffs) as Class<out FlavourBuff>

        if (debuffCls != null) {
            Buff.append<out FlavourBuff>(enemy, debuffCls, (6 + level() * 3).toFloat())
        } else {
            //if no debuff can be applied (all are present), then go up one tier
            if (category === MINOR_DEBUFFS)
                debuffEnemy(enemy, MAJOR_DEBUFFS)
            else if (category === MAJOR_DEBUFFS) corruptEnemy(enemy)
        }
    }

    private fun corruptEnemy(enemy: Mob) {
        //cannot re-corrupt or doom an enemy, so give them a major debuff instead
        if (enemy.buff<Corruption>(Corruption::class.java) != null || enemy.buff<Doom>(Doom::class.java) != null) {
            GLog.w(Messages.get(this, "already_corrupted"))
            return
        }

        if (!enemy.isImmune(Corruption::class.java)) {
            enemy.HP = enemy.HT
            for (buff in enemy.buffs()) {
                if (buff.type == Buff.buffType.NEGATIVE && buff !is SoulMark) {
                    buff.detach()
                } else if (buff is PinCushion) {
                    buff.detach()
                }
            }
            Buff.affect<Corruption>(enemy, Corruption::class.java)

            Statistics.enemiesSlain++
            Badges.validateMonstersSlain()
            Statistics.qualifiedForNoKilling = false
            if (enemy.EXP > 0 && Item.curUser.lvl <= enemy.maxLvl) {
                Item.curUser.sprite!!.showStatus(CharSprite.POSITIVE, Messages.get(enemy, "exp", enemy.EXP))
                Item.curUser.earnExp(enemy.EXP)
            }
            enemy.rollToDropLoot()
        } else {
            Buff.affect<Doom>(enemy, Doom::class.java)
        }
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        if (Random.Int(level() + 4) >= 3) {
            Buff.prolong<Amok>(defender, Amok::class.java, (4 + level() * 2).toFloat())
        }
    }

    override fun fx(bolt: Ballistica, callback: Callback) {
        MagicMissile.boltFromChar(Item.curUser.sprite!!.parent!!,
                MagicMissile.SHADOW,
                Item.curUser.sprite,
                bolt.collisionPos!!,
                callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(0)
        particle.am = 0.6f
        particle.setLifespan(2f)
        particle.speed.set(0f, 5f)
        particle.setSize(0.5f, 2f)
        particle.shuffleXY(1f)
    }

    companion object {

        //Note that some debuffs here have a 0% chance to be applied.
        // This is because the wand of corruption considers them to be a certain level of harmful
        // for the purposes of reducing resistance, but does not actually apply them itself

        private val MINOR_DEBUFF_WEAKEN = 4 / 5f
        private val MINOR_DEBUFFS = HashMap<Class<out Buff>, Float>()

        init {
            MINOR_DEBUFFS[Weakness::class.java] = 2f
            MINOR_DEBUFFS[Cripple::class.java] = 1f
            MINOR_DEBUFFS[Blindness::class.java] = 1f
            MINOR_DEBUFFS[Terror::class.java] = 1f

            MINOR_DEBUFFS[Chill::class.java] = 0f
            MINOR_DEBUFFS[Ooze::class.java] = 0f
            MINOR_DEBUFFS[Roots::class.java] = 0f
            MINOR_DEBUFFS[Vertigo::class.java] = 0f
            MINOR_DEBUFFS[Drowsy::class.java] = 0f
            MINOR_DEBUFFS[Bleeding::class.java] = 0f
            MINOR_DEBUFFS[Burning::class.java] = 0f
            MINOR_DEBUFFS[Poison::class.java] = 0f
        }

        private val MAJOR_DEBUFF_WEAKEN = 2 / 3f
        private val MAJOR_DEBUFFS = HashMap<Class<out Buff>, Float>()

        init {
            MAJOR_DEBUFFS[Amok::class.java] = 3f
            MAJOR_DEBUFFS[Slow::class.java] = 2f
            MAJOR_DEBUFFS[Paralysis::class.java] = 1f

            MAJOR_DEBUFFS[Charm::class.java] = 0f
            MAJOR_DEBUFFS[MagicalSleep::class.java] = 0f
            MAJOR_DEBUFFS[SoulMark::class.java] = 0f
            MAJOR_DEBUFFS[Corrosion::class.java] = 0f
            MAJOR_DEBUFFS[Frost::class.java] = 0f
            MAJOR_DEBUFFS[Doom::class.java] = 0f
        }
    }

}
