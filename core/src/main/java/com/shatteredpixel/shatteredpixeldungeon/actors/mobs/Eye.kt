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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.EyeSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class Eye : Mob() {

    private var beam: Ballistica? = null
    private var beamTarget = -1
    private var beamCooldown: Int = 0
    var beamCharged: Boolean = false

    init {
        spriteClass = EyeSprite::class.java

        HT = 100
        HP = HT
        defenseSkill = 20
        viewDistance = Light.DISTANCE

        EXP = 13
        maxLvl = 25

        flying = true

        HUNTING = Hunting()

        loot = Dewdrop()
        lootChance = 0.5f

        properties.add(Char.Property.DEMONIC)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(20, 30)
    }

    override fun attackSkill(target: Char): Int {
        return 30
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 10)
    }

    override fun canAttack(enemy: Char?): Boolean {

        if (beamCooldown == 0) {
            val aim = Ballistica(pos, enemy!!.pos, Ballistica.STOP_TERRAIN)

            if (enemy.invisible == 0 && !isCharmedBy(enemy) && fieldOfView!![enemy.pos] && aim.subPath(1, aim.dist!!).contains(enemy.pos)) {
                beam = aim
                beamTarget = aim.collisionPos!!
                return true
            } else
            //if the beam is charged, it has to attack, will aim at previous location of target.
                return beamCharged
        } else
            return super.canAttack(enemy)
    }

    override fun act(): Boolean {
        if (beamCharged && state !== HUNTING) {
            beamCharged = false
        }
        if (beam == null && beamTarget != -1) {
            beam = Ballistica(pos, beamTarget, Ballistica.STOP_TERRAIN)
            sprite!!.turnTo(pos, beamTarget)
        }
        if (beamCooldown > 0)
            beamCooldown--
        return super.act()
    }

    override fun doAttack(enemy: Char?): Boolean {

        if (beamCooldown > 0) {
            return super.doAttack(enemy)
        } else if (!beamCharged) {
            (sprite as EyeSprite).charge(enemy!!.pos)
            spend(attackDelay() * 2f)
            beamCharged = true
            return true
        } else {

            spend(attackDelay())

            beam = Ballistica(pos, beamTarget, Ballistica.STOP_TERRAIN)
            if (Dungeon.level!!.heroFOV[pos] || Dungeon.level!!.heroFOV[beam!!.collisionPos]) {
                sprite!!.zap(beam!!.collisionPos!!)
                return false
            } else {
                deathGaze()
                return true
            }
        }

    }

    override fun damage(dmg: Int, src: Any) {
        var dmg = dmg
        if (beamCharged) dmg /= 4
        super.damage(dmg, src)
    }

    fun deathGaze() {
        if (!beamCharged || beamCooldown > 0 || beam == null)
            return

        beamCharged = false
        beamCooldown = Random.IntRange(3, 6)

        var terrainAffected = false

        for (pos in beam!!.subPath(1, beam!!.dist!!)) {

            if (Dungeon.level!!.flamable[pos]) {

                Dungeon.level!!.destroy(pos)
                GameScene.updateMap(pos)
                terrainAffected = true

            }

            val ch = Actor.findChar(pos) ?: continue

            if (Char.hit(this, ch, true)) {
                ch.damage(Random.NormalIntRange(30, 50), this)

                if (Dungeon.level!!.heroFOV[pos]) {
                    ch.sprite!!.flash()
                    CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2))
                }

                if (!ch.isAlive && ch === Dungeon.hero) {
                    Dungeon.fail(javaClass)
                    GLog.n(Messages.get(this, "deathgaze_kill"))
                }
            } else {
                ch.sprite!!.showStatus(CharSprite.NEUTRAL, ch.defenseVerb())
            }
        }

        if (terrainAffected) {
            Dungeon.observe()
        }

        beam = null
        beamTarget = -1
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(BEAM_TARGET, beamTarget)
        bundle.put(BEAM_COOLDOWN, beamCooldown)
        bundle.put(BEAM_CHARGED, beamCharged)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (bundle.contains(BEAM_TARGET))
            beamTarget = bundle.getInt(BEAM_TARGET)
        beamCooldown = bundle.getInt(BEAM_COOLDOWN)
        beamCharged = bundle.getBoolean(BEAM_CHARGED)
    }

    init {
        resistances.add(WandOfDisintegration::class.java)
        resistances.add(Grim::class.java)
    }

    init {
        immunities.add(Terror::class.java)
    }

    private inner class Hunting : Mob.Hunting() {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            //even if enemy isn't seen, attack them if the beam is charged
            if (beamCharged && enemy != null && canAttack(enemy)) {
                enemySeen = enemyInFOV
                return doAttack(enemy)
            }
            return super.act(enemyInFOV, justAlerted)
        }
    }

    companion object {

        private val BEAM_TARGET = "beamTarget"
        private val BEAM_COOLDOWN = "beamCooldown"
        private val BEAM_CHARGED = "beamCharged"
    }
}
