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
import com.watabou.noosa.RenderedText
import com.watabou.noosa.ui.Component

import java.util.ArrayList
import java.util.Arrays

class RenderedTextMultiline : Component {

    private var maxWidth = Integer.MAX_VALUE
    var nLines: Int = 0

    private var text: String? = null
    private var tokens: List<String>? = null
    private var words: ArrayList<RenderedText>? = ArrayList()

    private var size: Int = 0
    private var zoom: Float = 0.toFloat()
    private var color = -1

    private var chinese = false

    constructor(size: Int) {
        this.size = size
    }

    constructor(text: String, size: Int) {
        this.size = size
        text(text)
    }

    fun text(text: String?) {
        this.text = text

        if (text != null && text != "") {
            //conversion for chinese text

            chinese = text.replace("\\p{Han}".toRegex(), "").length != text.length

            if (chinese) {
                tokens = Arrays.asList(*text.split("".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
            } else {
                tokens = Arrays.asList(*text.split("(?<= )|(?= )|(?<=\n)|(?=\n)".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
            }
            build()
        }
    }

    fun text(text: String, maxWidth: Int) {
        this.maxWidth = maxWidth
        text(text)
    }

    fun text(): String? {
        return text
    }

    fun maxWidth(maxWidth: Int) {
        if (this.maxWidth != maxWidth) {
            this.maxWidth = maxWidth
            layout()
        }
    }

    fun maxWidth(): Int {
        return maxWidth
    }

    @Synchronized
    private fun build() {
        clear()
        words = ArrayList()
        var highlighting = false
        for (str in tokens!!) {
            if (str == UNDERSCORE) {
                highlighting = !highlighting
            } else if (str == NEWLINE) {
                words!!.add(null)
            } else if (str != SPACE) {
                val word: RenderedText
                if (str.startsWith(UNDERSCORE) && str.endsWith(UNDERSCORE)) {
                    word = RenderedText(str.substring(1, str.length - 1), size)
                    word.hardlight(0xFFFF44)
                } else {
                    if (str.startsWith(UNDERSCORE)) {
                        highlighting = !highlighting
                        word = RenderedText(str.substring(1, str.length), size)
                    } else if (str.endsWith(UNDERSCORE)) {
                        word = RenderedText(str.substring(0, str.length - 1), size)
                    } else {
                        word = RenderedText(str, size)
                    }
                    if (highlighting)
                        word.hardlight(0xFFFF44)
                    else if (color != -1) word.hardlight(color)

                    if (str.endsWith(UNDERSCORE)) highlighting = !highlighting
                }
                word.scale.set(zoom)
                words!!.add(word)
                add(word)

                if (height < word.baseLine()) height = word.baseLine()

            }
        }
        layout()
    }

    @Synchronized
    fun zoom(zoom: Float) {
        this.zoom = zoom
        for (word in words!!) {
            word?.scale?.set(zoom)
        }
    }

    @Synchronized
    fun hardlight(color: Int) {
        this.color = color
        for (word in words!!) {
            word?.hardlight(color)
        }
    }

    @Synchronized
    fun invert() {
        if (words != null) {
            for (word in words!!) {
                if (word != null) {
                    word.ra = 0.77f
                    word.ga = 0.73f
                    word.ba = 0.62f
                    word.rm = -0.77f
                    word.gm = -0.73f
                    word.bm = -0.62f
                }
            }
        }
    }

    @Synchronized
    override fun layout() {
        super.layout()
        var x = this.x
        var y = this.y
        var height = 0f
        nLines = 1

        for (word in words!!) {
            if (word == null) {
                //newline
                y += height + 0.5f
                x = this.x
                nLines++
            } else {
                if (word.height() > height) height = word.baseLine()

                if (x - this.x + word.width() > maxWidth) {
                    y += height + 0.5f
                    x = this.x
                    nLines++
                }

                word.x = x
                word.y = y
                PixelScene.align(word)
                x += word.width()
                if (!chinese)
                    x++
                else
                    x--

                if (x - this.x > width) width = x - this.x

            }
        }
        this.height = y - this.y + height + 0.5f
    }

    companion object {

        private val SPACE = " "
        private val NEWLINE = "\n"
        private val UNDERSCORE = "_"
    }
}
