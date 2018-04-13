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

package com.watabou.noosa

import com.watabou.glscripts.Script

//This class should be used on heavy pixel-fill based loads when lighting is not needed.
// It skips the lighting component of the fragment shader, giving a significant performance boost

//Remember that switching programs is expensive
// if this script is to be used many times try to block them together
class NoosaScriptNoLighting : NoosaScript() {

    override fun lighting(rm: Float, gm: Float, bm: Float, am: Float, ra: Float, ga: Float, ba: Float, aa: Float) {
        //Does nothing
    }

    override fun shader(): String {
        return SHADER
    }

    companion object {

        override fun get(): NoosaScriptNoLighting {
            return Script.use(NoosaScriptNoLighting::class.java)
        }

        private val SHADER = "uniform mat4 uCamera;" +
                "uniform mat4 uModel;" +
                "attribute vec4 aXYZW;" +
                "attribute vec2 aUV;" +
                "varying vec2 vUV;" +
                "void main() {" +
                "  gl_Position = uCamera * uModel * aXYZW;" +
                "  vUV = aUV;" +
                "}" +

                "//\n" +

                "varying mediump vec2 vUV;" +
                "uniform lowp sampler2D uTex;" +
                "void main() {" +
                "  gl_FragColor = texture2D( uTex, vUV );" +
                "}"
    }
}
