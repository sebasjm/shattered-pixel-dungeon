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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.ColorMath
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

class WandOfCorrosion : Wand() {

    init {
        image = ItemSpriteSheet.WAND_CORROSION

        collisionProperties = Ballistica.STOP_TARGET or Ballistica.STOP_TERRAIN
    }

    override fun onZap(bolt: Ballistica) {
        val corrosiveGas = Blob.seed<CorrosiveGas>(bolt.collisionPos!!, 50 + 10 * level(), CorrosiveGas::class.java)
        CellEmitter.center(bolt.collisionPos!!).burst(CorrosionParticle.SPLASH, 10)
        (corrosiveGas as CorrosiveGas).setStrength(level() + 1)
        GameScene.add(corrosiveGas)

        for (i in PathFinder.NEIGHBOURS9) {
            val ch = Actor.findChar(bolt.collisionPos!! + i)
            if (ch != null) {
                processSoulMark(ch, chargesPerCast())
            }
        }

        if (Actor.findChar(bolt.collisionPos!!) == null) {
            Dungeon.level!!.press(bolt.collisionPos!!, null, true)
        }
    }

    override fun fx(bolt: Ballistica, callback: Callback) {
        MagicMissile.boltFromChar(
                Item.curUser.sprite!!.parent!!,
                MagicMissile.CORROSION,
                Item.curUser.sprite,
                bolt.collisionPos!!,
                callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        if (Random.Int(level() + 3) >= 2) {

            Buff.affect<Ooze>(defender, Ooze::class.java)
            CellEmitter.center(defender.pos).burst(CorrosionParticle.SPLASH, 5)

        }
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(ColorMath.random(0xAAAAAA, 0xFF8800))
        particle.am = 0.6f
        particle.setLifespan(1f)
        particle.acc.set(0f, 20f)
        particle.setSize(0.5f, 3f)
        particle.shuffleXY(1f)
    }

}
