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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.ArmorKit
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.KingSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.UndeadSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class King : Mob() {

    private var nextPedestal = true

    init {
        spriteClass = KingSprite::class.java

        HT = 300
        HP = HT
        EXP = 40
        defenseSkill = 25

        Undead.count = 0

        properties.add(Char.Property.BOSS)
        properties.add(Char.Property.UNDEAD)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(PEDESTAL, nextPedestal)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        nextPedestal = bundle.getBoolean(PEDESTAL)
        BossHealthBar.assignBoss(this)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(25, 40)
    }

    override fun attackSkill(target: Char): Int {
        return 32
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 14)
    }

    override fun getCloser(target: Int): Boolean {
        return if (canTryToSummon())
            super.getCloser((Dungeon.level as CityBossLevel).pedestal(nextPedestal))
        else
            super.getCloser(target)
    }

    override fun canAttack(enemy: Char?): Boolean {
        return if (canTryToSummon())
            pos == (Dungeon.level as CityBossLevel).pedestal(nextPedestal)
        else
            Dungeon.level!!.adjacent(pos, enemy!!.pos)
    }

    private fun canTryToSummon(): Boolean {
        if (Undead.count < maxArmySize()) {
            val ch = Actor.findChar((Dungeon.level as CityBossLevel).pedestal(nextPedestal))
            return ch === this || ch == null
        } else {
            return false
        }
    }

    override fun attack(enemy: Char?): Boolean {
        if (canTryToSummon() && pos == (Dungeon.level as CityBossLevel).pedestal(nextPedestal)) {
            summon()
            return true
        } else {
            if (Actor.findChar((Dungeon.level as CityBossLevel).pedestal(nextPedestal)) === enemy) {
                nextPedestal = !nextPedestal
            }
            return super.attack(enemy)
        }
    }

    override fun damage(dmg: Int, src: Any) {
        super.damage(dmg, src)
        val lock = Dungeon.hero!!.buff<LockedFloor>(LockedFloor::class.java)
        lock?.addTime(dmg.toFloat())
    }

    override fun die(cause: Any) {

        GameScene.bossSlain()
        Dungeon.level!!.drop(ArmorKit(), pos).sprite!!.drop()
        Dungeon.level!!.drop(SkeletonKey(Dungeon.depth), pos).sprite!!.drop()

        super.die(cause)

        Badges.validateBossSlain()

        val beacon = Dungeon.hero!!.belongings.getItem<LloydsBeacon>(LloydsBeacon::class.java)
        beacon?.upgrade()

        yell(Messages.get(this, "defeated", Dungeon.hero!!.givenName()))
    }

    override fun aggro(ch: Char) {
        super.aggro(ch)
        for (mob in Dungeon.level!!.mobs) {
            if (mob is Undead) {
                mob.aggro(ch)
            }
        }
    }

    private fun maxArmySize(): Int {
        return 1 + MAX_ARMY_SIZE * (HT - HP) / HT
    }

    private fun summon() {

        nextPedestal = !nextPedestal

        sprite!!.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2)
        Sample.INSTANCE.play(Assets.SND_CHALLENGE)

        val passable = Dungeon.level!!.passable.clone()
        for (c in Actor.chars()) {
            passable[c.pos] = false
        }

        val undeadsToSummon = maxArmySize() - Undead.count

        PathFinder.buildDistanceMap(pos, passable, undeadsToSummon)
        PathFinder.distance[pos] = Integer.MAX_VALUE
        var dist = 1

        undeadLabel@ for (i in 0 until undeadsToSummon) {
            do {
                for (j in 0 until Dungeon.level!!.length()) {
                    if (PathFinder.distance[j] == dist) {

                        val undead = Undead()
                        undead.pos = j
                        GameScene.add(undead)

                        ScrollOfTeleportation.appear(undead, j)
                        Flare(3, 32f).color(0x000000, false).show(undead.sprite, 2f)

                        PathFinder.distance[j] = Integer.MAX_VALUE

                        continue@undeadLabel
                    }
                }
                dist++
            } while (dist < undeadsToSummon)
        }

        yell(Messages.get(this, "arise"))
    }

    override fun notice() {
        super.notice()
        BossHealthBar.assignBoss(this)
        yell(Messages.get(this, "notice"))
    }

    init {
        resistances.add(WandOfDisintegration::class.java)
    }

    init {
        immunities.add(Paralysis::class.java)
        immunities.add(Vertigo::class.java)
        immunities.add(Blindness::class.java)
        immunities.add(Terror::class.java)
    }

    class Undead : Mob() {

        init {
            spriteClass = UndeadSprite::class.java

            HT = 28
            HP = HT
            defenseSkill = 15

            EXP = 0

            state = WANDERING

            properties.add(Char.Property.UNDEAD)
            properties.add(Char.Property.INORGANIC)
        }

        override fun onAdd() {
            count++
            super.onAdd()
        }

        override fun onRemove() {
            count--
            super.onRemove()
        }

        override fun damageRoll(): Int {
            return Random.NormalIntRange(15, 25)
        }

        override fun attackSkill(target: Char): Int {
            return 16
        }

        override fun attackProc(enemy: Char, damage: Int): Int {
            var damage = damage
            damage = super.attackProc(enemy, damage)
            if (Random.Int(MAX_ARMY_SIZE) == 0) {
                Buff.prolong<Paralysis>(enemy, Paralysis::class.java, 1f)
            }

            return damage
        }

        override fun damage(dmg: Int, src: Any) {
            super.damage(dmg, src)
            if (src is ToxicGas) {
                src.clear(pos)
            }
        }

        override fun die(cause: Any) {
            super.die(cause)

            if (Dungeon.level!!.heroFOV[pos]) {
                Sample.INSTANCE.play(Assets.SND_BONES)
            }
        }

        override fun drRoll(): Int {
            return Random.NormalIntRange(0, 5)
        }

        init {
            immunities.add(Grim::class.java)
            immunities.add(Paralysis::class.java)
        }

        companion object {

            var count = 0
        }
    }

    companion object {

        private val MAX_ARMY_SIZE = 5

        private val PEDESTAL = "pedestal"
    }
}
