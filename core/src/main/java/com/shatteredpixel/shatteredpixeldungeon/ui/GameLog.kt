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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.ui.Component
import com.watabou.utils.Signal

import java.util.ArrayList
import java.util.regex.Pattern

class GameLog : Component(), Signal.Listener<String> {

    private var lastEntry: RenderedTextMultiline? = null
    private var lastColor: Int = 0

    init {
        GLog.update.replace(this)

        recreateLines()
    }

    @Synchronized
    private fun recreateLines() {
        for (entry in entries) {
            lastEntry = PixelScene.renderMultiline(entry.text!!, 6)
            lastColor = entry.color
            lastEntry!!.hardlight(lastColor)
            add(lastEntry!!)
        }
    }

    @Synchronized
    fun newLine() {
        lastEntry = null
    }

    @Synchronized
    override fun onSignal(text: String) {
        var text = text

        if (length != entries.size) {
            clear()
            recreateLines()
        }

        var color = CharSprite.DEFAULT
        if (text.startsWith(GLog.POSITIVE)) {
            text = text.substring(GLog.POSITIVE.length)
            color = CharSprite.POSITIVE
        } else if (text.startsWith(GLog.NEGATIVE)) {
            text = text.substring(GLog.NEGATIVE.length)
            color = CharSprite.NEGATIVE
        } else if (text.startsWith(GLog.WARNING)) {
            text = text.substring(GLog.WARNING.length)
            color = CharSprite.WARNING
        } else if (text.startsWith(GLog.HIGHLIGHT)) {
            text = text.substring(GLog.HIGHLIGHT.length)
            color = CharSprite.NEUTRAL
        }

        if (lastEntry != null && color == lastColor && lastEntry!!.nLines < MAX_LINES) {

            val lastMessage = lastEntry!!.text()
            lastEntry!!.text(if (lastMessage!!.length == 0) text else "$lastMessage $text")

            entries[entries.size - 1].text = lastEntry!!.text()

        } else {

            lastEntry = PixelScene.renderMultiline(text, 6)
            lastEntry!!.hardlight(color)
            lastColor = color
            add(lastEntry!!)

            entries.add(Entry(text, color))

        }

        if (length > 0) {
            var nLines: Int
            do {
                nLines = 0
                for (i in 0 until length - 1) {
                    nLines += (members!![i] as RenderedTextMultiline).nLines
                }

                if (nLines > MAX_LINES) {
                    val r = members!![0] as RenderedTextMultiline
                    remove(r)
                    r.destroy()

                    entries.removeAt(0)
                }
            } while (nLines > MAX_LINES)
            if (entries.isEmpty()) {
                lastEntry = null
            }
        }

        layout()
    }

    override fun layout() {
        var pos = y
        for (i in length - 1 downTo 0) {
            val entry = members!![i] as RenderedTextMultiline
            entry.maxWidth(width.toInt())
            entry.setPos(x, pos - entry.height())
            pos -= entry.height()
        }
    }

    override fun destroy() {
        GLog.update.remove(this)
        super.destroy()
    }

    private class Entry(var text: String?, var color: Int)

    companion object {

        private val MAX_LINES = 3

        private val PUNCTUATION = Pattern.compile(".*[.,;?! ]$")

        private val entries = ArrayList<Entry>()

        fun wipe() {
            entries.clear()
        }
    }
}
