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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class WandOfMagicMissile : DamageWand() {

    init {
        image = ItemSpriteSheet.WAND_MAGIC_MISSILE
    }

    override fun min(lvl: Int): Int {
        return 2 + lvl
    }

    override fun max(lvl: Int): Int {
        return 8 + 2 * lvl
    }

    override fun onZap(bolt: Ballistica) {

        val ch = Actor.findChar(bolt.collisionPos!!)
        if (ch != null) {

            processSoulMark(ch, chargesPerCast())
            ch.damage(damageRoll(), this)

            ch.sprite!!.burst(-0x1, level() / 2 + 2)

        } else {
            Dungeon.level!!.press(bolt.collisionPos!!, null, true)
        }
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        Buff.prolong<Recharging>(attacker, Recharging::class.java, 1 + staff.level() / 2f)
        SpellSprite.show(attacker, SpellSprite.CHARGE)

    }

    override fun initialCharges(): Int {
        return 3
    }

}
