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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Callback
import com.watabou.utils.PointF
import com.watabou.utils.Random

class WandOfFrost : DamageWand() {

    init {
        image = ItemSpriteSheet.WAND_FROST
    }

    override fun min(lvl: Int): Int {
        return 2 + lvl
    }

    override fun max(lvl: Int): Int {
        return 8 + 5 * lvl
    }

    override fun onZap(bolt: Ballistica) {

        val heap = Dungeon.level!!.heaps.get(bolt.collisionPos!!)
        heap?.freeze()

        val ch = Actor.findChar(bolt.collisionPos!!)
        if (ch != null) {

            var damage = damageRoll()

            if (ch.buff<Frost>(Frost::class.java) != null) {
                return  //do nothing, can't affect a frozen target
            }
            if (ch.buff<Chill>(Chill::class.java) != null) {
                //7.5% less damage per turn of chill remaining
                val chill = ch.buff<Chill>(Chill::class.java)!!.cooldown()
                damage = Math.round(damage * Math.pow(0.9, chill.toDouble())).toInt()
            } else {
                ch.sprite!!.burst(-0x663301, level() / 2 + 2)
            }

            processSoulMark(ch, chargesPerCast())
            ch.damage(damage, this)

            if (ch.isAlive) {
                if (Dungeon.level!!.water[ch.pos])
                    Buff.prolong<Chill>(ch, Chill::class.java, (4 + level()).toFloat())
                else
                    Buff.prolong<Chill>(ch, Chill::class.java, (2 + level()).toFloat())
            }
        } else {
            Dungeon.level!!.press(bolt.collisionPos!!, null, true)
        }
    }

    override fun fx(bolt: Ballistica, callback: Callback) {
        MagicMissile.boltFromChar(Item.curUser.sprite!!.parent!!,
                MagicMissile.FROST,
                Item.curUser.sprite,
                bolt.collisionPos!!,
                callback)
        Sample.INSTANCE.play(Assets.SND_ZAP)
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        val chill = defender.buff<Chill>(Chill::class.java)
        if (chill != null && Random.IntRange(2, 10) <= chill.cooldown()) {
            //need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
            object : FlavourBuff() {
                init {
                    actPriority = VFX_PRIO
                }

                override fun act(): Boolean {
                    Buff.affect<Frost>(target, Frost::class.java, Frost.duration(target) * Random.Float(1f, 2f))
                    return super.act()
                }
            }.attachTo(defender)
        }
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(0x88CCFF)
        particle.am = 0.6f
        particle.setLifespan(2f)
        val angle = Random.Float(PointF.PI2)
        particle.speed.polar(angle, 2f)
        particle.acc.set(0f, 1f)
        particle.setSize(0f, 1.5f)
        particle.radiateXY(Random.Float(1f))
    }

}
