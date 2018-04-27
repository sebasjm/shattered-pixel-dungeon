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

import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component

//a notification window that the player can't get rid of quickly, good for forcibly telling a message
//USE THIS SPARINGLY
class WndHardNotification(titlebar: Component, message: String, private val btnMessage: String, time: Int) : WndTitledMessage(titlebar, message) {

    internal var btnOkay: RedButton

    private var timeLeft: Double = 0.toDouble()

    constructor(icon: Image, title: String, message: String, btnMessage: String, time: Int) : this(IconTitle(icon, title), message, btnMessage, time) {}

    init {

        timeLeft = time.toDouble()

        btnOkay = object : RedButton("$btnMessage ($time)") {
            override fun onClick() {
                hide()
            }
        }
        btnOkay.setRect(0f, (height + WndTitledMessage.GAP).toFloat(), width.toFloat(), 16f)
        btnOkay.enable(false)
        add(btnOkay)

        resize(width, btnOkay.bottom().toInt())
    }

    override fun update() {
        super.update()

        timeLeft -= Game.elapsed.toDouble()
        if (timeLeft <= 0) {
            btnOkay.enable(true)
            btnOkay.text(btnMessage)
        } else {
            btnOkay.text(btnMessage + " (" + Math.ceil(timeLeft).toInt() + ")")
        }

    }

    override fun onBackPressed() {
        if (timeLeft <= 0) super.onBackPressed()
    }
}
