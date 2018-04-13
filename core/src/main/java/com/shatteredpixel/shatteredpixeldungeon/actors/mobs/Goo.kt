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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GooWarn
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class Goo : Mob() {

    private var pumpedUp = 0

    private val PUMPEDUP = "pumpedup"

    init {
        HT = 100
        HP = HT
        EXP = 10
        defenseSkill = 8
        spriteClass = GooSprite::class.java

        loot = LloydsBeacon()
        lootChance = 0.333f

        properties.add(Char.Property.BOSS)
        properties.add(Char.Property.DEMONIC)
        properties.add(Char.Property.ACIDIC)
    }

    override fun damageRoll(): Int {
        val min = 1
        val max = if (HP * 2 <= HT) 15 else 10
        if (pumpedUp > 0) {
            pumpedUp = 0
            PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level!!.solid, null), 2)
            for (i in PathFinder.distance.indices) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE)
                    CellEmitter.get(i).burst(ElmoParticle.FACTORY, 10)
            }
            Sample.INSTANCE.play(Assets.SND_BURNING)
            return Random.NormalIntRange(min * 3, max * 3)
        } else {
            return Random.NormalIntRange(min, max)
        }
    }

    override fun attackSkill(target: Char): Int {
        var attack = 10
        if (HP * 2 <= HT) attack = 15
        if (pumpedUp > 0) attack *= 2
        return attack
    }

    override fun defenseSkill(enemy: Char): Int {
        return (super.defenseSkill(enemy) * if (HP * 2 <= HT) 1.5 else 1).toInt()
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 2)
    }

    public override fun act(): Boolean {

        if (Dungeon.level!!.water[pos] && HP < HT) {
            sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)
            if (HP * 2 == HT) {
                BossHealthBar.bleed(false)
                (sprite as GooSprite).spray(false)
            }
            HP++
        }

        return super.act()
    }

    override fun canAttack(enemy: Char?): Boolean {
        return if (pumpedUp > 0) distance(enemy) <= 2 else super.canAttack(enemy)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        if (Random.Int(3) == 0) {
            Buff.affect<Ooze>(enemy, Ooze::class.java)
            enemy.sprite!!.burst(0x000000, 5)
        }

        if (pumpedUp > 0) {
            Camera.main.shake(3f, 0.2f)
        }

        return damage
    }

    override fun doAttack(enemy: Char?): Boolean {
        if (pumpedUp == 1) {
            (sprite as GooSprite).pumpUp()
            PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level!!.solid, null), 2)
            for (i in PathFinder.distance.indices) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE)
                    GameScene.add(Blob.seed<GooWarn>(i, 2, GooWarn::class.java))
            }
            pumpedUp++

            spend(attackDelay())

            return true
        } else if (pumpedUp >= 2 || Random.Int(if (HP * 2 <= HT) 2 else 5) > 0) {

            val visible = Dungeon.level!!.heroFOV[pos]

            if (visible) {
                if (pumpedUp >= 2) {
                    (sprite as GooSprite).pumpAttack()
                } else
                    sprite!!.attack(enemy!!.pos)
            } else {
                attack(enemy)
            }

            spend(attackDelay())

            return !visible

        } else {

            pumpedUp++

            (sprite as GooSprite).pumpUp()

            for (i in PathFinder.NEIGHBOURS9.indices) {
                val j = pos + PathFinder.NEIGHBOURS9[i]
                if (!Dungeon.level!!.solid[j]) {
                    GameScene.add(Blob.seed<GooWarn>(j, 2, GooWarn::class.java))
                }
            }

            if (Dungeon.level!!.heroFOV[pos]) {
                sprite!!.showStatus(CharSprite.NEGATIVE, Messages.get(this, "!!!"))
                GLog.n(Messages.get(this, "pumpup"))
            }

            spend(attackDelay())

            return true
        }
    }

    override fun attack(enemy: Char?): Boolean {
        val result = super.attack(enemy)
        pumpedUp = 0
        return result
    }

    override fun getCloser(target: Int): Boolean {
        pumpedUp = 0
        return super.getCloser(target)
    }

    override fun move(step: Int) {
        Dungeon.level!!.seal()
        super.move(step)
    }

    override fun damage(dmg: Int, src: Any) {
        val bleeding = HP * 2 <= HT
        super.damage(dmg, src)
        if (HP * 2 <= HT && !bleeding) {
            BossHealthBar.bleed(true)
            sprite!!.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"))
            (sprite as GooSprite).spray(true)
            yell(Messages.get(this, "gluuurp"))
        }
        val lock = Dungeon.hero!!.buff<LockedFloor>(LockedFloor::class.java)
        lock?.addTime((dmg * 2).toFloat())
    }

    override fun die(cause: Any) {

        super.die(cause)

        Dungeon.level!!.unseal()

        GameScene.bossSlain()
        Dungeon.level!!.drop(SkeletonKey(Dungeon.depth), pos).sprite!!.drop()

        Badges.validateBossSlain()

        yell(Messages.get(this, "defeated"))
    }

    override fun notice() {
        super.notice()
        BossHealthBar.assignBoss(this)
        yell(Messages.get(this, "notice"))
    }

    override fun storeInBundle(bundle: Bundle) {

        super.storeInBundle(bundle)

        bundle.put(PUMPEDUP, pumpedUp)
    }

    override fun restoreFromBundle(bundle: Bundle) {

        super.restoreFromBundle(bundle)

        pumpedUp = bundle.getInt(PUMPEDUP)
        if (state !== SLEEPING) BossHealthBar.assignBoss(this)
        if (HP * 2 <= HT) BossHealthBar.bleed(true)

    }

}
