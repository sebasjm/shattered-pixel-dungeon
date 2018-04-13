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

package com.shatteredpixel.shatteredpixeldungeon.scenes

import android.content.Intent
import android.net.Uri

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.TouchArea

class AboutScene : PixelScene() {

    override fun create() {
        super.create()

        val colWidth = (Camera.main.width / if (SPDSettings.landscape()) 2 else 1).toFloat()
        val colTop = (Camera.main.height / 2 - if (SPDSettings.landscape()) 30 else 90).toFloat()
        val wataOffset = if (SPDSettings.landscape()) colWidth else 0

        val shpx = Icons.SHPX.get()
        shpx.x = (colWidth - shpx.width()) / 2
        shpx.y = colTop
        PixelScene.align(shpx)
        add(shpx)

        Flare(7, 64f).color(0x225511, true).show(shpx, 0f).angularSpeed = +20f

        val shpxtitle = PixelScene.renderText(TTL_SHPX, 8)
        shpxtitle.hardlight(Window.SHPX_COLOR)
        add(shpxtitle)

        shpxtitle.x = (colWidth - shpxtitle.width()) / 2
        shpxtitle.y = shpx.y + shpx.height + 5f
        PixelScene.align(shpxtitle)

        val shpxtext = PixelScene.renderMultiline(TXT_SHPX, 8)
        shpxtext.maxWidth(Math.min(colWidth, 120f).toInt())
        add(shpxtext)

        shpxtext.setPos((colWidth - shpxtext.width()) / 2, shpxtitle.y + shpxtitle.height() + 12f)
        PixelScene.align(shpxtext)

        val shpxlink = PixelScene.renderMultiline(LNK_SHPX, 8)
        shpxlink.maxWidth(shpxtext.maxWidth())
        shpxlink.hardlight(Window.SHPX_COLOR)
        add(shpxlink)

        shpxlink.setPos((colWidth - shpxlink.width()) / 2, shpxtext.bottom() + 6)
        PixelScene.align(shpxlink)

        val shpxhotArea = object : TouchArea(shpxlink.left(), shpxlink.top(), shpxlink.width(), shpxlink.height()) {
            override fun onClick(touch: Touch) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$LNK_SHPX"))
                Game.instance!!.startActivity(intent)
            }
        }
        add(shpxhotArea)

        val wata = Icons.WATA.get()
        wata.x = wataOffset + (colWidth - wata.width()) / 2
        wata.y = if (SPDSettings.landscape())
            colTop
        else
            shpxlink.top() + wata.height + 20f
        PixelScene.align(wata)
        add(wata)

        Flare(7, 64f).color(0x112233, true).show(wata, 0f).angularSpeed = +20f

        val wataTitle = PixelScene.renderText(TTL_WATA, 8)
        wataTitle.hardlight(Window.TITLE_COLOR)
        add(wataTitle)

        wataTitle.x = wataOffset + (colWidth - wataTitle.width()) / 2
        wataTitle.y = wata.y + wata.height + 11f
        PixelScene.align(wataTitle)

        val wataText = PixelScene.renderMultiline(TXT_WATA, 8)
        wataText.maxWidth(Math.min(colWidth, 120f).toInt())
        add(wataText)

        wataText.setPos(wataOffset + (colWidth - wataText.width()) / 2, wataTitle.y + wataTitle.height() + 12f)
        PixelScene.align(wataText)

        val wataLink = PixelScene.renderMultiline(LNK_WATA, 8)
        wataLink.maxWidth(Math.min(colWidth, 120f).toInt())
        wataLink.hardlight(Window.TITLE_COLOR)
        add(wataLink)

        wataLink.setPos(wataOffset + (colWidth - wataLink.width()) / 2, wataText.bottom() + 6)
        PixelScene.align(wataLink)

        val hotArea = object : TouchArea(wataLink.left(), wataLink.top(), wataLink.width(), wataLink.height()) {
            override fun onClick(touch: Touch) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$LNK_WATA"))
                Game.instance!!.startActivity(intent)
            }
        }
        add(hotArea)


        val archs = Archs()
        archs.setSize(Camera.main.width.toFloat(), Camera.main.height.toFloat())
        addToBack(archs)

        val btnExit = ExitButton()
        btnExit.setPos(Camera.main.width - btnExit.width(), 0f)
        add(btnExit)

        fadeIn()
    }

    override fun onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene::class.java)
    }

    companion object {

        private val TTL_SHPX = "Shattered Pixel Dungeon"

        private val TXT_SHPX = "Design, Code, & Graphics: Evan"

        private val LNK_SHPX = "ShatteredPixel.com"

        private val TTL_WATA = "Pixel Dungeon"

        private val TXT_WATA = "Code & Graphics: Watabou\n" + "Music: Cube_Code"

        private val LNK_WATA = "pixeldungeon.watabou.ru"
    }
}
