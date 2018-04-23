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

package com.watabou.noosa.particles

import com.watabou.gltextures.SmartTexture
import com.watabou.noosa.Image
import com.watabou.utils.Random

class BitmaskEmitter(target: Image) : Emitter() {

    // DON'T USE WITH COMPLETELY TRANSPARENT IMAGES!!!

    private val map: SmartTexture?
    private val mapW: Int
    private val mapH: Int

    init {

        this.target = target

        map = target.texture!!
        mapW = map!!.bitmap!!.width
        mapH = map.bitmap!!.height
    }

    override fun emit(index: Int) {

        val frame = (target as Image).frame()
        val ofsX = frame.left * mapW
        val ofsY = frame.top * mapH

        var x: Float
        var y: Float
        do {
            x = Random.Float(frame.width()) * mapW
            y = Random.Float(frame.height()) * mapH
        } while (map!!.bitmap!!.getPixel((x + ofsX).toInt(), (y + ofsY).toInt()) and 0x000000FF == 0)

        factory!!.emit(this, index,
                target!!.x + x * target!!.scale.x,
                target!!.y + y * target!!.scale.y)
    }
}