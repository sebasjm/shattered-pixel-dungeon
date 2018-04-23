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
import com.watabou.noosa.Image

class ResumeIndicator : Tag(0xCDD5C0) {

    private var icon: Image? = null

    init {

        setSize(24f, 24f)

        visible = false

    }

    override fun createChildren() {
        super.createChildren()

        icon = Icons.get(Icons.RESUME)
        add(icon!!)
    }

    override fun layout() {
        super.layout()

        icon!!.x = x + 1f + (width - icon!!.width) / 2f
        icon!!.y = y + (height - icon!!.height) / 2f
        PixelScene.align(icon!!)
    }

    override fun onClick() {
        Dungeon.hero!!.resume()
    }

    override fun update() {
        if (!Dungeon.hero!!.isAlive)
            visible = false
        else if (visible != (Dungeon.hero!!.lastAction != null)) {
            visible = Dungeon.hero!!.lastAction != null
            if (visible)
                flash()
        }
        super.update()
    }
}
