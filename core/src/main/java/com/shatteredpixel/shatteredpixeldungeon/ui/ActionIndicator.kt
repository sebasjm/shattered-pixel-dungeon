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

class ActionIndicator : Tag(0xFFFF4C) {

    internal var icon: Image? = null

    private var needsLayout = false

    init {

        instance = this

        setSize(24f, 24f)
        visible = false
    }

    override fun destroy() {
        super.destroy()
        instance = null
    }

    @Synchronized
    override fun layout() {
        super.layout()

        if (icon != null) {
            icon!!.x = x + (width - icon!!.width()) / 2
            icon!!.y = y + (height - icon!!.height()) / 2
            PixelScene.align(icon!!)
            if (!members!!.contains(icon))
                add(icon)
        }
    }

    @Synchronized
    override fun update() {
        super.update()

        if (!Dungeon.hero!!.ready) {
            if (icon != null) icon!!.alpha(0.5f)
        } else {
            if (icon != null) icon!!.alpha(1f)
        }

        if (!visible && action != null) {
            visible = true
            updateIcon()
            flash()
        } else {
            visible = action != null
        }

        if (needsLayout) {
            layout()
            needsLayout = false
        }
    }

    override fun onClick() {
        if (action != null && Dungeon.hero!!.ready)
            action!!.doAction()
    }

    interface Action {

        val icon: Image

        fun doAction()

    }

    companion object {

        var action: Action? = null
        var instance: ActionIndicator? = null

        fun setAction(action: Action) {
            ActionIndicator.action = action
            updateIcon()
        }

        fun clearAction(action: Action) {
            if (ActionIndicator.action === action)
                ActionIndicator.action = null
        }

        fun updateIcon() {
            if (instance != null) {
                synchronized(instance) {
                    if (instance!!.icon != null) {
                        instance!!.icon!!.killAndErase()
                        instance!!.icon = null
                    }
                    if (action != null) {
                        instance!!.icon = action!!.icon
                        instance!!.needsLayout = true
                    }
                }
            }
        }
    }

}
