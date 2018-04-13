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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class Tengu : Mob() {

    override//Tengu has special death rules, see prisonbosslevel.progress()
    val isAlive: Boolean
        get() = Dungeon.level!!.mobs.contains(this)

    init {
        spriteClass = TenguSprite::class.java

        HT = 120
        HP = HT
        EXP = 20
        defenseSkill = 20

        HUNTING = Hunting()

        flying = true //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

        properties.add(Char.Property.BOSS)
    }

    override fun onAdd() {
        //when he's removed and re-added to the fight, his time is always set to now.
        spend(-cooldown())
        super.onAdd()
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(6, 20)
    }

    override fun attackSkill(target: Char): Int {
        return 20
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 5)
    }

    override fun damage(dmg: Int, src: Any) {
        var dmg = dmg

        val beforeHitHP = HP
        super.damage(dmg, src)
        dmg = beforeHitHP - HP

        val lock = Dungeon.hero!!.buff<LockedFloor>(LockedFloor::class.java)
        if (lock != null) {
            val multiple = if (beforeHitHP > HT / 2) 1 else 4
            lock.addTime((dmg * multiple).toFloat())
        }

        //phase 2 of the fight is over
        if (HP == 0 && beforeHitHP <= HT / 2) {
            (Dungeon.level as PrisonBossLevel).progress()
            return
        }

        val hpBracket = if (beforeHitHP > HT / 2) 12 else 20

        //phase 1 of the fight is over
        if (beforeHitHP > HT / 2 && HP <= HT / 2) {
            HP = HT / 2 - 1
            yell(Messages.get(this, "interesting"))
            (Dungeon.level as PrisonBossLevel).progress()
            BossHealthBar.bleed(true)

            //if tengu has lost a certain amount of hp, jump
        } else if (beforeHitHP / hpBracket != HP / hpBracket) {
            jump()
        }
    }

    override fun die(cause: Any) {

        if (Dungeon.hero!!.subClass == HeroSubClass.NONE) {
            Dungeon.level!!.drop(TomeOfMastery(), pos).sprite!!.drop()
        }

        GameScene.bossSlain()
        super.die(cause)

        Badges.validateBossSlain()

        val beacon = Dungeon.hero!!.belongings.getItem<LloydsBeacon>(LloydsBeacon::class.java)
        beacon?.upgrade()

        yell(Messages.get(this, "defeated"))
    }

    override fun canAttack(enemy: Char?): Boolean {
        return Ballistica(pos, enemy!!.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos
    }

    //tengu's attack is always visible
    override fun doAttack(enemy: Char?): Boolean {
        if (enemy === Dungeon.hero)
            Dungeon.hero!!.resting = false
        sprite!!.attack(enemy!!.pos)
        spend(attackDelay())
        return true
    }

    private fun jump() {

        val level = Dungeon.level

        //incase tengu hasn't had a chance to act yet
        if (fieldOfView == null || fieldOfView!!.size != Dungeon.level!!.length()) {
            fieldOfView = BooleanArray(Dungeon.level!!.length())
            Dungeon.level!!.updateFieldOfView(this, fieldOfView)
        }

        if (enemy == null) enemy = chooseEnemy()
        if (enemy == null) return

        var newPos: Int
        //if we're in phase 1, want to warp around within the room
        if (HP > HT / 2) {

            //place new traps
            for (i in 0..3) {
                var trapPos: Int
                do {
                    trapPos = Random.Int(level!!.length())
                } while (level!!.map!![trapPos] != Terrain.INACTIVE_TRAP && level.map!![trapPos] != Terrain.TRAP)

                if (level.map!![trapPos] == Terrain.INACTIVE_TRAP) {
                    level.setTrap(GrippingTrap().reveal(), trapPos)
                    Level.set(trapPos, Terrain.TRAP)
                    ScrollOfMagicMapping.discover(trapPos)
                }
            }

            var tries = 50
            do {
                newPos = Random.IntRange(3, 7) + 32 * Random.IntRange(26, 30)
            } while ((level!!.adjacent(newPos, enemy!!.pos) || Actor.findChar(newPos) != null) && --tries > 0)
            if (tries <= 0) return

            //otherwise go wherever, as long as it's a little bit away
        } else {
            do {
                newPos = Random.Int(level!!.length())
            } while (level!!.solid[newPos] ||
                    level.distance(newPos, enemy!!.pos) < 8 ||
                    Actor.findChar(newPos) != null)
        }

        if (level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6)

        sprite!!.move(pos, newPos)
        move(newPos)

        if (level.heroFOV[newPos]) CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6)
        Sample.INSTANCE.play(Assets.SND_PUFF)

        spend(1 / speed())
    }

    override fun notice() {
        super.notice()
        BossHealthBar.assignBoss(this)
        if (HP <= HT / 2) BossHealthBar.bleed(true)
        if (HP == HT) {
            yell(Messages.get(this, "notice_mine", Dungeon.hero!!.givenName()))
        } else {
            yell(Messages.get(this, "notice_face", Dungeon.hero!!.givenName()))
        }
    }

    init {
        resistances.add(ToxicGas::class.java)
        resistances.add(Poison::class.java)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        BossHealthBar.assignBoss(this)
        if (HP <= HT / 2) BossHealthBar.bleed(true)
    }

    //tengu is always hunting
    private inner class Hunting : Mob.Hunting() {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV
            if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

                return doAttack(enemy)

            } else {

                if (enemyInFOV) {
                    target = enemy!!.pos
                } else {
                    chooseEnemy()
                    if (enemy != null) {
                        target = enemy!!.pos
                    }
                }

                spend(Actor.TICK)
                return true

            }
        }
    }
}
