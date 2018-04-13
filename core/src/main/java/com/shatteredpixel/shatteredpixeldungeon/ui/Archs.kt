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
import com.watabou.glwrap.Blending
import com.watabou.noosa.Game
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.NoosaScriptNoLighting
import com.watabou.noosa.SkinnedBlock
import com.watabou.noosa.ui.Component

class Archs : Component() {

    private var arcsBg: SkinnedBlock? = null
    private var arcsFg: SkinnedBlock? = null

    var reversed = false

    override fun createChildren() {
        arcsBg = object : SkinnedBlock(1f, 1f, Assets.ARCS_BG) {
            override fun script(): NoosaScript {
                return NoosaScriptNoLighting.get()
            }

            override fun draw() {
                //arch bg has no alpha component, this improves performance
                Blending.disable()
                super.draw()
                Blending.enable()
            }
        }
        arcsBg!!.autoAdjust = true
        arcsBg!!.offsetTo(0f, offsB)
        add(arcsBg)

        arcsFg = object : SkinnedBlock(1f, 1f, Assets.ARCS_FG) {
            override fun script(): NoosaScript {
                return NoosaScriptNoLighting.get()
            }
        }
        arcsFg!!.autoAdjust = true
        arcsFg!!.offsetTo(0f, offsF)
        add(arcsFg)
    }

    override fun layout() {
        arcsBg!!.size(width, height)
        arcsBg!!.offset(arcsBg!!.texture!!.width / 4 - width % arcsBg!!.texture!!.width / 2, 0f)

        arcsFg!!.size(width, height)
        arcsFg!!.offset(arcsFg!!.texture!!.width / 4 - width % arcsFg!!.texture!!.width / 2, 0f)
    }

    override fun update() {

        super.update()

        var shift = Game.elapsed * SCROLL_SPEED
        if (reversed) {
            shift = -shift
        }

        arcsBg!!.offset(0f, shift)
        arcsFg!!.offset(0f, shift * 2)

        offsB = arcsBg!!.offsetY()
        offsF = arcsFg!!.offsetY()
    }

    companion object {

        private val SCROLL_SPEED = 20f

        private var offsB = 0f
        private var offsF = 0f
    }
}
