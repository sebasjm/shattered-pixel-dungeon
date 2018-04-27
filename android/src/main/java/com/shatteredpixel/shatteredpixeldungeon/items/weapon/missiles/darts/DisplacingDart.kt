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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashMap

class DisplacingDart : TippedDart() {

    init {
        image = ItemSpriteSheet.DISPLACING_DART
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {

        if (!defender.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.IMMOVABLE)) {

            val startDist = Dungeon.level!!.distance(attacker.pos, defender.pos)

            val positions = HashMap<Int, ArrayList<Int>>()

            for (pos in 0 until Dungeon.level!!.length()) {
                if (Dungeon.level!!.heroFOV[pos]
                        && Dungeon.level!!.passable[pos]
                        && Actor.findChar(pos) == null) {

                    val dist = Dungeon.level!!.distance(attacker.pos, pos)
                    if (dist > startDist) {
                        if (positions[dist] == null) {
                            positions[dist] = ArrayList()
                        }
                        positions[dist]!!.add(pos)
                    }

                }
            }

            val probs = FloatArray(ShadowCaster.MAX_DISTANCE + 1)

            for (i in 0..ShadowCaster.MAX_DISTANCE) {
                if (positions[i] != null) {
                    probs[i] = (i - startDist).toFloat()
                }
            }

            val chosenDist = Random.chances(probs)

            if (chosenDist != -1) {
                val pos = positions[chosenDist]!!.get(Random.index(positions[chosenDist]!!))
                ScrollOfTeleportation.appear(defender, pos)
                Dungeon.level!!.press(pos, defender)
            }

        }

        return super.proc(attacker, defender, damage)
    }
}
