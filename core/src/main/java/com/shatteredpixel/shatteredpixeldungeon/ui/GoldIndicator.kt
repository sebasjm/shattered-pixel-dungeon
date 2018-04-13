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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Game
import com.watabou.noosa.ui.Component

class GoldIndicator : Component() {

    private var lastValue = 0

    private var tf: BitmapText? = null

    private var time: Float = 0.toFloat()

    override fun createChildren() {
        tf = BitmapText(PixelScene.pixelFont)
        tf!!.hardlight(0xFFFF00)
        add(tf)

        visible = false
    }

    override fun layout() {
        tf!!.x = x + (width - tf!!.width()) / 2
        tf!!.y = bottom() - tf!!.height()
    }

    override fun update() {
        super.update()

        if (visible) {

            time -= Game.elapsed
            if (time > 0) {
                tf!!.alpha(if (time > TIME / 2) 1f else time * 2 / TIME)
            } else {
                visible = false
            }

        }

        if (Dungeon.gold != lastValue) {

            lastValue = Dungeon.gold

            tf!!.text(Integer.toString(lastValue))
            tf!!.measure()

            visible = true
            time = TIME

            layout()
        }
    }

    companion object {

        private val TIME = 2f
    }
}
