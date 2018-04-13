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

package com.watabou.glscripts

import com.watabou.glwrap.Program
import com.watabou.glwrap.Shader
import com.watabou.noosa.Game

import java.util.HashMap

open class Script : Program() {

    fun compile(src: String) {

        val srcShaders = src.split("//\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        attach(Shader.createCompiled(Shader.VERTEX, srcShaders[0]))
        attach(Shader.createCompiled(Shader.FRAGMENT, srcShaders[1]))
        link()

    }

    fun unuse() {}

    companion object {

        private val all = HashMap<Class<out Script>, Script>()

        private var curScript: Script? = null
        private var curScriptClass: Class<out Script>? = null

        @Synchronized
        fun <T : Script> use(c: Class<T>): T {

            if (c != curScriptClass) {

                var script: Script? = all[c]
                if (script == null) {
                    try {
                        script = c.newInstance()
                    } catch (e: Exception) {
                        Game.reportException(e)
                    }

                    all[c] = script!!
                }

                if (curScript != null) {
                    curScript!!.unuse()
                }

                curScript = script
                curScriptClass = c
                curScript!!.use()

            }

            return curScript as T
        }

        fun reset() {
            for (script in all.values) {
                script.delete()
            }
            all.clear()

            curScript = null
            curScriptClass = null
        }
    }
}
