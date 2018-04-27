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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm

class WndInfoBuff(buff: Buff) : Window() {

    private val icons: SmartTexture
    private val film: TextureFilm

    init {

        val titlebar = IconTitle()

        icons = TextureCache.get(Assets.BUFFS_LARGE)
        film = TextureFilm(icons, 16, 16)

        val buffIcon = Image(icons)
        buffIcon.frame(film.get(buff.icon()))
        buff.tintIcon(buffIcon)

        titlebar.icon(buffIcon)
        titlebar.label(Messages.titleCase(buff.toString()), Window.TITLE_COLOR)
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)

        val txtInfo = PixelScene.renderMultiline(buff.desc(), 6)
        txtInfo.maxWidth(WIDTH)
        txtInfo.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(txtInfo)

        resize(WIDTH, (txtInfo.top() + txtInfo.height()).toInt())
    }

    companion object {

        private val GAP = 2f

        private val WIDTH = 120
    }
}
