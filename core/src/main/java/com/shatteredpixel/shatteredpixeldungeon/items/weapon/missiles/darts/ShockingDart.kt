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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.PointF
import com.watabou.utils.Random

import java.util.ArrayList

class ShockingDart : TippedDart() {

    init {
        image = ItemSpriteSheet.SHOCKING_DART
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {

        defender.damage(Random.NormalIntRange(8, 12), this)

        val s = defender.sprite
        val arcs = ArrayList<Lightning.Arc>()
        arcs.add(Lightning.Arc(PointF(s!!.x, s.y + s.height / 2), PointF(s.x + s.width, s.y + s.height / 2)))
        arcs.add(Lightning.Arc(PointF(s.x + s.width / 2, s.y), PointF(s.x + s.width / 2, s.y + s.height)))
        defender.sprite!!.parent!!.add(Lightning(arcs, null))

        return super.proc(attacker, defender, damage)
    }
}
