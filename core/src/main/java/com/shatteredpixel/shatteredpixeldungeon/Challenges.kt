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

package com.shatteredpixel.shatteredpixeldungeon

import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing

object Challenges {

    //Some of these internal IDs are outdated and don't represent what these challenges do
    val NO_FOOD = 1
    val NO_ARMOR = 2
    val NO_HEALING = 4
    val NO_HERBALISM = 8
    val SWARM_INTELLIGENCE = 16
    val DARKNESS = 32
    val NO_SCROLLS = 64

    val MAX_VALUE = 127

    val NAME_IDS = arrayOf("no_food", "no_armor", "no_healing", "no_herbalism", "swarm_intelligence", "darkness", "no_scrolls")

    val MASKS = intArrayOf(NO_FOOD, NO_ARMOR, NO_HEALING, NO_HERBALISM, SWARM_INTELLIGENCE, DARKNESS, NO_SCROLLS)

    fun isItemBlocked(item: Item): Boolean {
        if (Dungeon.isChallenged(NO_FOOD)) {
            if (item is Food && item !is SmallRation) {
                return true
            } else if (item is HornOfPlenty) {
                return true
            }
        }

        if (Dungeon.isChallenged(NO_ARMOR)) {
            if (item is Armor && item !is ClothArmor) {
                return true
            }
        }

        if (Dungeon.isChallenged(NO_HEALING)) {
            if (item is PotionOfHealing) {
                return true
            } else if (item is Blandfruit && item.potionAttrib is PotionOfHealing) {
                return true
            }
        }

        if (Dungeon.isChallenged(NO_HERBALISM)) {
            if (item is Dewdrop) {
                return true
            }
        }

        return false

    }

}