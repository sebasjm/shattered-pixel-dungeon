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

package com.shatteredpixel.shatteredpixeldungeon.utils

import com.watabou.utils.Random

//This class defines the parameters for seeds in ShatteredPD and contains a few convenience methods
object DungeonSeed {

    private val TOTAL_SEEDS = 5429503678976L //26^9 possible seeds

    fun randomSeed(): Long {
        return Random.Long(TOTAL_SEEDS)
    }

    //Seed codes take the form @@@-@@@-@@@ where @ is any letter from A to Z (only uppercase)
    //This is effectively a base-26 number system, therefore 26^9 unique seeds are possible.

    //Seed codes exist to make sharing and inputting seeds easier
    //ZZZ-ZZZ-ZZZ is much easier to enter and share than 5,429,503,678,975


    //Takes a seed code (@@@@@@@@@) and converts it to the equivalent long value
    fun convertFromCode(code: String): Long {
        if (code.length != 9)
            throw IllegalArgumentException("codes must be 9 A-Z characters.")

        var result: Long = 0
        for (i in 8 downTo 0) {
            val c = code[i]
            if (c > 'Z' || c < 'A')
                throw IllegalArgumentException("codes must be 9 A-Z characters.")

            result += ((c.toInt() - 65) * Math.pow(26.0, (8 - i).toDouble())).toLong()
        }
        return result
    }

    //Takes a long value and converts it to the equivalent seed code
    fun convertToCode(seed: Long): String {
        if (seed < 0 || seed >= TOTAL_SEEDS)
            throw IllegalArgumentException("seeds must be within the range [0, TOTAL_SEEDS)")

        //this almost gives us the right answer, but its 0-p instead of A-Z
        val interrim = java.lang.Long.toString(seed, 26)
        var result = ""

        //so we convert
        for (i in 0..8) {

            if (i < interrim.length) {
                var c = interrim[i]
                if (c <= '9')
                    c += 17.toChar() //convert 0-9 to A-J
                else
                    c -= 22.toChar() //convert a-p to K-Z

                result += c

            } else {
                result = 'A' + result //pad with A (zeroes) until we reach length of 9

            }
        }

        return result
    }

    //Using this we can let users input 'fun' plaintext seeds and convert them to a long equivalent.
    // This is basically the same as string.hashcode except with long, and accounting for overflow
    // to ensure the produced seed is always in the range [0, TOTAL_SEEDS)
    fun convertFromText(inputText: String): Long {
        var total: Long = 0
        for (c in inputText.toCharArray()) {
            total = 31 * total + c.toLong()
        }
        if (total < 0) total += java.lang.Long.MAX_VALUE
        total %= TOTAL_SEEDS
        return total
    }

}
