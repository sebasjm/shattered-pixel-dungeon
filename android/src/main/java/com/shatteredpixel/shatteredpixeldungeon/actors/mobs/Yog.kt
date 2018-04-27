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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.BurningFistSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.LarvaSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.RottingFistSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashSet

class Yog : Mob() {

    init {
        spriteClass = YogSprite::class.java

        HT = 300
        HP = HT

        EXP = 50

        state = PASSIVE

        properties.add(Char.Property.BOSS)
        properties.add(Char.Property.IMMOVABLE)
        properties.add(Char.Property.DEMONIC)
    }

    fun spawnFists() {
        val fist1 = RottingFist()
        val fist2 = BurningFist()

        do {
            fist1.pos = pos + PathFinder.NEIGHBOURS8!![Random.Int(8)]
            fist2.pos = pos + PathFinder.NEIGHBOURS8!![Random.Int(8)]
        } while (!Dungeon.level!!.passable[fist1.pos] || !Dungeon.level!!.passable[fist2.pos] || fist1.pos == fist2.pos)

        GameScene.add(fist1)
        GameScene.add(fist2)

        notice()
    }

    override fun act(): Boolean {
        //heals 1 health per turn
        HP = Math.min(HT, HP + 1)

        return super.act()
    }

    override fun damage(dmg: Int, src: Any) {
        var dmg = dmg

        val fists = HashSet<Mob>()

        for (mob in Dungeon.level!!.mobs)
            if (mob is RottingFist || mob is BurningFist)
                fists.add(mob)

        dmg = dmg shr fists.size

        super.damage(dmg, src)

        val lock = Dungeon.hero!!.buff<LockedFloor>(LockedFloor::class.java)
        lock?.addTime(dmg * 0.5f)

    }

    override fun defenseProc(enemy: Char, damage: Int): Int {

        val spawnPoints = ArrayList<Int>()

        for (i in PathFinder.NEIGHBOURS8!!.indices) {
            val p = pos + PathFinder.NEIGHBOURS8!![i]
            if (Actor.findChar(p) == null && (Dungeon.level!!.passable[p] || Dungeon.level!!.avoid[p])) {
                spawnPoints.add(p)
            }
        }

        if (spawnPoints.size > 0) {
            val larva = Larva()
            larva.pos = Random.element(spawnPoints)!!

            GameScene.add(larva)
            Actor.addDelayed(Pushing(larva, pos, larva.pos), -1f)
        }

        for (mob in Dungeon.level!!.mobs) {
            if (mob is BurningFist || mob is RottingFist || mob is Larva) {
                mob.aggro(enemy)
            }
        }

        return super.defenseProc(enemy, damage)
    }

    override fun beckon(cell: Int) {}

    override fun die(cause: Any?) {

        for (mob in Dungeon.level!!.mobs.clone() as Iterable<Mob>) {
            if (mob is BurningFist || mob is RottingFist) {
                mob.die(cause)
            }
        }

        GameScene.bossSlain()
        Dungeon.level!!.drop(SkeletonKey(Dungeon.depth), pos).sprite!!.drop()
        super.die(cause)

        yell(Messages.get(this.javaClass, "defeated"))
    }

    override fun notice() {
        super.notice()
        BossHealthBar.assignBoss(this)
        yell(Messages.get(this.javaClass, "notice"))
    }

    init {

        immunities.add(Grim::class.java)
        immunities.add(Terror::class.java)
        immunities.add(Amok::class.java)
        immunities.add(Charm::class.java)
        immunities.add(Sleep::class.java)
        immunities.add(Burning::class.java)
        immunities.add(ToxicGas::class.java)
        immunities.add(ScrollOfPsionicBlast::class.java)
        immunities.add(Vertigo::class.java)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        BossHealthBar.assignBoss(this)
    }

    class RottingFist : Mob() {

        init {
            spriteClass = RottingFistSprite::class.java

            HT = 300
            HP = HT
            defenseSkill = 25

            EXP = 0

            state = WANDERING

            properties.add(Char.Property.BOSS)
            properties.add(Char.Property.DEMONIC)
            properties.add(Char.Property.ACIDIC)
        }

        override fun attackSkill(target: Char?): Int {
            return 36
        }

        override fun damageRoll(): Int {
            return Random.NormalIntRange(20, 50)
        }

        override fun drRoll(): Int {
            return Random.NormalIntRange(0, 15)
        }

        override fun attackProc(enemy: Char, damage: Int): Int {
            var damage = damage
            damage = super.attackProc(enemy, damage)

            if (Random.Int(3) == 0) {
                Buff.affect<Ooze>(enemy, Ooze::class.java)
                enemy.sprite!!.burst(-0x1000000, 5)
            }

            return damage
        }

        public override fun act(): Boolean {

            if (Dungeon.level!!.water[pos] && HP < HT) {
                sprite!!.emitter().burst(ShadowParticle.UP, 2)
                HP += REGENERATION
            }

            return super.act()
        }

        override fun damage(dmg: Int, src: Any) {
            super.damage(dmg, src)
            val lock = Dungeon.hero!!.buff<LockedFloor>(LockedFloor::class.java)
            lock?.addTime(dmg * 0.5f)
        }

        init {
            immunities.add(Paralysis::class.java)
            immunities.add(Amok::class.java)
            immunities.add(Sleep::class.java)
            immunities.add(Terror::class.java)
            immunities.add(Poison::class.java)
            immunities.add(Vertigo::class.java)
        }

        companion object {

            private val REGENERATION = 4
        }
    }

    class BurningFist : Mob() {

        init {
            spriteClass = BurningFistSprite::class.java

            HT = 200
            HP = HT
            defenseSkill = 25

            EXP = 0

            state = WANDERING

            properties.add(Char.Property.BOSS)
            properties.add(Char.Property.DEMONIC)
            properties.add(Char.Property.FIERY)
        }

        override fun attackSkill(target: Char?): Int {
            return 36
        }

        override fun damageRoll(): Int {
            return Random.NormalIntRange(26, 32)
        }

        override fun drRoll(): Int {
            return Random.NormalIntRange(0, 15)
        }

        override fun canAttack(enemy: Char?): Boolean {
            return Ballistica(pos, enemy!!.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos
        }

        override fun attack(enemy: Char?): Boolean {

            if (!Dungeon.level!!.adjacent(pos, enemy!!.pos)) {
                spend(attackDelay())

                if (Char.hit(this, enemy, true)) {

                    val dmg = damageRoll()
                    enemy.damage(dmg, this)

                    enemy.sprite!!.bloodBurstA(sprite!!.center(), dmg)
                    enemy.sprite!!.flash()

                    if (!enemy.isAlive && enemy === Dungeon.hero!!) {
                        Dungeon.fail(javaClass)
                        GLog.n(Messages.get(Char::class.java, "kill", name))
                    }
                    return true

                } else {

                    enemy.sprite!!.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb())
                    return false
                }
            } else {
                return super.attack(enemy)
            }
        }

        public override fun act(): Boolean {

            for (i in PathFinder.NEIGHBOURS9!!.indices) {
                GameScene.add(Blob.seed<Fire>(pos + PathFinder.NEIGHBOURS9!![i], 2, Fire::class.java)!!)
            }

            return super.act()
        }

        override fun damage(dmg: Int, src: Any) {
            super.damage(dmg, src)
            val lock = Dungeon.hero!!.buff<LockedFloor>(LockedFloor::class.java)
            lock?.addTime(dmg * 0.5f)
        }

        init {
            resistances.add(ToxicGas::class.java)
        }

        init {
            immunities.add(Amok::class.java)
            immunities.add(Sleep::class.java)
            immunities.add(Terror::class.java)
            immunities.add(Vertigo::class.java)
        }
    }

    class Larva : Mob() {

        init {
            spriteClass = LarvaSprite::class.java

            HT = 25
            HP = HT
            defenseSkill = 20

            EXP = 0

            state = HUNTING

            properties.add(Char.Property.DEMONIC)
        }

        override fun attackSkill(target: Char?): Int {
            return 30
        }

        override fun damageRoll(): Int {
            return Random.NormalIntRange(22, 30)
        }

        override fun drRoll(): Int {
            return Random.NormalIntRange(0, 8)
        }

    }
}
