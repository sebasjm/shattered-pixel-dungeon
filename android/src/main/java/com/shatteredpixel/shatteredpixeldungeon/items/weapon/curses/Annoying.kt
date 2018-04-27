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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Random

class Annoying : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Int {

        if (Random.Int(20) == 0) {
            for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
                mob.beckon(attacker.pos)
            }
            attacker.sprite!!.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3)
            Sample.INSTANCE.play(Assets.SND_MIMIC)
            Invisibility.dispel()
            GLog.n(Messages.get(this.javaClass, "msg_" + (Random.Int(5) + 1)))
        }

        return damage
    }

    override fun curse(): Boolean {
        return true
    }

    override fun glowing(): ItemSprite.Glowing {
        return BLACK
    }

    companion object {

        private val BLACK = ItemSprite.Glowing(0x000000)
    }

}