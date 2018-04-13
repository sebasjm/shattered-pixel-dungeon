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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.watabou.utils.Point
import com.watabou.utils.Random

import java.util.HashMap

//TODO specific implementation
class SecretLaboratoryRoom : SecretRoom() {

    override fun paint(level: Level) {

        Painter.fill(level, this, Terrain.WALL)
        Painter.fill(level, this, 1, Terrain.EMPTY_SP)

        entrance().set(Room.Door.Type.HIDDEN)

        val pot = center()
        Painter.set(level, pot, Terrain.ALCHEMY)

        val alchemy = Alchemy()
        alchemy.seed(level, pot.x + level.width() * pot.y, Random.IntRange(30, 60))
        level.blobs[Alchemy::class.java] = alchemy

        val n = Random.IntRange(2, 3)
        val chances = HashMap(potionChances)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = level.pointToCell(random())
            } while (level.map!![pos] != Terrain.EMPTY_SP || level.heaps.get(pos) != null)

            try {
                val potionCls = Random.chances(chances)
                chances[potionCls] = 0f
                level.drop(potionCls!!.newInstance(), pos)
            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
            }

        }

    }

    companion object {

        private val potionChances = HashMap<Class<out Potion>, Float>()

        init {
            potionChances[PotionOfHealing::class.java] = 2f
            potionChances[PotionOfExperience::class.java] = 5f
            potionChances[PotionOfToxicGas::class.java] = 1f
            potionChances[PotionOfParalyticGas::class.java] = 3f
            potionChances[PotionOfLiquidFlame::class.java] = 1f
            potionChances[PotionOfLevitation::class.java] = 1f
            potionChances[PotionOfMindVision::class.java] = 3f
            potionChances[PotionOfPurity::class.java] = 2f
            potionChances[PotionOfInvisibility::class.java] = 1f
            potionChances[PotionOfFrost::class.java] = 1f
        }
    }

}
