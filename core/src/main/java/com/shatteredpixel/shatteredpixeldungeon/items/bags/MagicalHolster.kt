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

package com.shatteredpixel.shatteredpixeldungeon.items.bags

import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class MagicalHolster : Bag() {

    init {
        image = ItemSpriteSheet.HOLSTER

        size = 12
    }

    override fun grab(item: Item): Boolean {
        return item is Wand || item is MissileWeapon
    }

    override fun collect(container: Bag): Boolean {
        if (super.collect(container)) {
            if (owner != null) {
                for (item in items) {
                    if (item is Wand) {
                        item.charge(owner, HOLSTER_SCALE_FACTOR)
                    } else if (item is MissileWeapon) {
                        item.holster = true
                    }
                }
            }
            return true
        } else {
            return false
        }
    }

    override fun onDetach() {
        super.onDetach()
        for (item in items) {
            if (item is Wand) {
                item.stopCharging()
            } else if (item is MissileWeapon) {
                item.holster = false
            }
        }
    }

    override fun price(): Int {
        return 60
    }

    companion object {

        val HOLSTER_SCALE_FACTOR = 0.85f
        val HOLSTER_DURABILITY_FACTOR = 1.2f
    }

}
