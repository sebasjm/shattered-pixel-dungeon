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

package com.shatteredpixel.shatteredpixeldungeon.windows

import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText

import java.util.ArrayList

open class WndChallenges(checked: Int, private val editable: Boolean) : Window() {
    private val boxes: ArrayList<CheckBox>

    init {

        val title = PixelScene.renderText(Messages.get(this.javaClass, "title"), 9)
        title.hardlight(Window.TITLE_COLOR)
        title.x = (WIDTH - title.width()) / 2
        title.y = (TTL_HEIGHT - title.height()) / 2
        PixelScene.align(title)
        add(title)

        boxes = ArrayList()

        var pos = TTL_HEIGHT.toFloat()
        for (i in Challenges.NAME_IDS.indices) {

            val challenge = Challenges.NAME_IDS[i]

            val cb = CheckBox(Messages.get(Challenges::class.java, challenge))
            cb.checked(checked and Challenges.MASKS[i] != 0)
            cb.active = editable

            if (i > 0) {
                pos += GAP.toFloat()
            }
            cb.setRect(0f, pos, (WIDTH - 16).toFloat(), BTN_HEIGHT.toFloat())

            add(cb)
            boxes.add(cb)

            val info = object : IconButton(Icons.get(Icons.INFO)) {
                override fun onClick() {
                    super.onClick()
                    Game.scene()!!.add(
                            WndMessage(Messages.get(Challenges::class.java, challenge + "_desc"))
                    )
                }
            }
            info.setRect(cb.right(), pos, 16f, BTN_HEIGHT.toFloat())
            add(info)

            pos = cb.bottom()
        }

        resize(WIDTH, pos.toInt())
    }

    override fun onBackPressed() {

        if (editable) {
            var value = 0
            for (i in boxes.indices) {
                if (boxes[i].checked()) {
                    value = value or Challenges.MASKS[i]
                }
            }
            SPDSettings.challenges(value)
        }

        super.onBackPressed()
    }

    companion object {

        private val WIDTH = 120
        private val TTL_HEIGHT = 12
        private val BTN_HEIGHT = 18
        private val GAP = 1
    }
}