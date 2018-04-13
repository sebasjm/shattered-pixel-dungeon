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

package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor.Glyph
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing
import com.watabou.noosa.Camera
import com.watabou.utils.Random

class Entanglement : Glyph() {

    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {

        val level = Math.max(0, armor.level())

        if (Random.Int(4) == 0) {

            Buff.prolong<Roots>(defender, Roots::class.java, (3 - level / 5).toFloat())
            Buff.affect<Earthroot.Armor>(defender, Earthroot.Armor::class.java)!!.level(4 + 4 * level)
            CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8)
            Camera.main.shake(1f, 0.4f)

        }

        return damage
    }

    override fun glowing(): Glowing {
        return BROWN
    }

    companion object {

        private val BROWN = ItemSprite.Glowing(0x663300)
    }

}
