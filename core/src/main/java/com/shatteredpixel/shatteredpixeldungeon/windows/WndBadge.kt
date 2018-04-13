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

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Image

class WndBadge(badge: Badges.Badge) : Window() {

    init {

        val icon = BadgeBanner.image(badge.image)
        icon.scale.set(2f)
        add(icon)

        //TODO: this used to be centered, should probably figure that out.
        val info = PixelScene.renderMultiline(badge.desc(), 8)
        info.maxWidth(WIDTH - MARGIN * 2)
        PixelScene.align(info)
        add(info)

        val w = Math.max(icon.width(), info.width()) + MARGIN * 2

        icon.x = (w - icon.width()) / 2f
        icon.y = MARGIN.toFloat()
        PixelScene.align(icon)

        info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN.toFloat())
        resize(w.toInt(), (info.bottom() + MARGIN).toInt())

        BadgeBanner.highlight(icon, badge.image)
    }

    companion object {

        private val WIDTH = 120
        private val MARGIN = 4
    }
}
