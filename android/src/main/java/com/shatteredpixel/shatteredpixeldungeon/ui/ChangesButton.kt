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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button

class ChangesButton : Button() {

    protected var image: Image? = null

    init {

        width = image!!.width
        height = image!!.height
    }

    override fun createChildren() {
        super.createChildren()

        image = Icons.NOTES.get()
        add(image!!)
    }

    override fun layout() {
        super.layout()

        image!!.x = x
        image!!.y = y
    }

    override fun onTouchDown() {
        image!!.brightness(1.5f)
        Sample.INSTANCE.play(Assets.SND_CLICK)
    }

    override fun onTouchUp() {
        image!!.resetColor()
    }

    override fun onClick() {
        ShatteredPixelDungeon.switchNoFade(ChangesScene::class.java)
    }
}
