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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM300Sprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class DM300 : Mob() {

    init {
        spriteClass = DM300Sprite::class.java

        HT = 200
        HP = HT
        EXP = 30
        defenseSkill = 18

        loot = CapeOfThorns()
        lootChance = 0.333f

        properties.add(Char.Property.BOSS)
        properties.add(Char.Property.INORGANIC)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(20, 25)
    }

    override fun attackSkill(target: Char?): Int {
        return 28
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 10)
    }

    public override fun act(): Boolean {

        GameScene.add(Blob.seed<ToxicGas>(pos, 30, ToxicGas::class.java)!!)

        return super.act()
    }

    override fun move(step: Int) {
        super.move(step)

        if (Dungeon.level!!.map!![step] == Terrain.INACTIVE_TRAP && HP < HT) {

            HP += Random.Int(1, HT - HP)
            sprite!!.emitter().burst(ElmoParticle.FACTORY, 5)

            if (Dungeon.level!!.heroFOV[step] && Dungeon.hero!!.isAlive) {
                GLog.n(Messages.get(this.javaClass, "repair"))
            }
        }

        val cells = intArrayOf(step - 1, step + 1, step - Dungeon.level!!.width(), step + Dungeon.level!!.width(), step - 1 - Dungeon.level!!.width(), step - 1 + Dungeon.level!!.width(), step + 1 - Dungeon.level!!.width(), step + 1 + Dungeon.level!!.width())
        val cell = cells[Random.Int(cells.size)]

        if (Dungeon.level!!.heroFOV[cell]) {
            CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10)
            Camera.main!!.shake(3f, 0.7f)
            Sample.INSTANCE.play(Assets.SND_ROCKS)

            if (Dungeon.level!!.water[cell]) {
                GameScene.ripple(cell)
            } else if (Dungeon.level!!.map!![cell] == Terrain.EMPTY) {
                Level.set(cell, Terrain.EMPTY_DECO)
                GameScene.updateMap(cell)
            }
        }

        val ch = Actor.findChar(cell)
        if (ch != null && ch !== this) {
            Buff.prolong<Paralysis>(ch, Paralysis::class.java, 2f)
        }
    }

    override fun damage(dmg: Int, src: Any) {
        super.damage(dmg, src)
        val lock = Dungeon.hero!!.buff<LockedFloor>(LockedFloor::class.java)
        if (lock != null && !isImmune(src.javaClass)) lock.addTime(dmg * 1.5f)
    }

    override fun die(cause: Any?) {

        super.die(cause)

        GameScene.bossSlain()
        Dungeon.level!!.drop(SkeletonKey(Dungeon.depth), pos).sprite!!.drop()

        Badges.validateBossSlain()

        val beacon = Dungeon.hero!!.belongings.getItem<LloydsBeacon>(LloydsBeacon::class.java)
        beacon?.upgrade()

        yell(Messages.get(this.javaClass, "defeated"))
    }

    override fun notice() {
        super.notice()
        BossHealthBar.assignBoss(this)
        yell(Messages.get(this.javaClass, "notice"))
    }

    init {
        immunities.add(ToxicGas::class.java)
        immunities.add(Terror::class.java)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        BossHealthBar.assignBoss(this)
    }
}
