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

package com.watabou.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

class Highlighter(text: String) {

    var text: String

    var mask: BooleanArray

    val isHighlighted: Boolean
        get() {
            for (i in mask.indices) {
                if (mask[i]) {
                    return true
                }
            }
            return false
        }

    init {

        val stripped = STRIPPER.matcher(text).replaceAll("")
        mask = BooleanArray(stripped.length)

        val m = HIGHLIGHTER.matcher(stripped)

        var pos = 0
        var lastMatch = 0

        while (m.find()) {
            pos += m.start() - lastMatch
            val groupLen = m.group(1).length
            for (i in pos until pos + groupLen) {
                mask[i] = true
            }
            pos += groupLen
            lastMatch = m.end()
        }

        m.reset(text)
        val sb = StringBuffer()
        while (m.find()) {
            m.appendReplacement(sb, m.group(1))
        }
        m.appendTail(sb)

        this.text = sb.toString()
    }

    fun inverted(): BooleanArray {
        val result = BooleanArray(mask.size)
        for (i in result.indices) {
            result[i] = !mask[i]
        }
        return result
    }

    companion object {

        private val HIGHLIGHTER = Pattern.compile("_(.*?)_")
        private val STRIPPER = Pattern.compile("[ \n]")
    }
}