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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.RenderedText
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.ui.Component

import java.util.ArrayList
import java.util.Locale

class WndHero : WndTabbed() {

    private val stats: StatsTab
    private val buffs: BuffsTab

    private val icons: SmartTexture
    private val film: TextureFilm

    init {

        resize(WIDTH, HEIGHT)

        icons = TextureCache.get(Assets.BUFFS_LARGE)
        film = TextureFilm(icons, 16, 16)

        stats = StatsTab()
        add(stats)

        buffs = BuffsTab()
        add(buffs)
        buffs.setRect(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat())
        buffs.setupList()

        add(object : WndTabbed.LabeledTab(Messages.get(this.javaClass, "stats")) {
            override fun select(value: Boolean) {
                super.select(value)
                stats.active = selected
                stats.visible = stats.active
            }
        })
        add(object : WndTabbed.LabeledTab(Messages.get(this.javaClass, "buffs")) {
            override fun select(value: Boolean) {
                super.select(value)
                buffs.active = selected
                buffs.visible = buffs.active
            }
        })

        layoutTabs()

        select(0)
    }

    private inner class StatsTab : Group() {

        private var pos: Float = 0.toFloat()

        init {

            val hero = Dungeon.hero!!

            val title = IconTitle()
            title.icon(HeroSprite.avatar(hero.heroClass, hero.tier()))
            if (hero.givenName() == hero.className())
                title.label(Messages.get(this.javaClass, "title", hero.lvl, hero.className()).toUpperCase(Locale.ENGLISH))
            else
                title.label((hero.givenName() + "\n" + Messages.get(this.javaClass, "title", hero.lvl, hero.className())).toUpperCase(Locale.ENGLISH))
            title.color(Window.SHPX_COLOR)
            title.setRect(0f, 0f, WIDTH.toFloat(), 0f)
            add(title)

            pos = title.bottom() + 2 * BIG_GAP

            statSlot(Messages.get(this.javaClass, "str"), hero.STR())
            if (hero.SHLD > 0)
                statSlot(Messages.get(this.javaClass, "health"), hero.HP.toString() + "+" + hero.SHLD + "/" + hero.HT)
            else
                statSlot(Messages.get(this.javaClass, "health"), hero.HP.toString() + "/" + hero.HT)
            statSlot(Messages.get(this.javaClass, "exp"), hero.exp.toString() + "/" + hero.maxExp())

            pos += BIG_GAP.toFloat()

            statSlot(Messages.get(this.javaClass, "gold"), Statistics.goldCollected)
            statSlot(Messages.get(this.javaClass, "depth"), Statistics.deepestFloor)

            pos += BIG_GAP.toFloat()
        }

        private fun statSlot(label: String, value: String) {

            var txt = PixelScene.renderText(label, 8)
            txt.y = pos
            add(txt)

            txt = PixelScene.renderText(value, 8)
            txt.x = WIDTH * 0.6f
            txt.y = pos
            PixelScene.align(txt)
            add(txt)

            pos += BIG_GAP + txt.baseLine()
        }

        private fun statSlot(label: String, value: Int) {
            statSlot(label, Integer.toString(value))
        }

        fun height(): Float {
            return pos
        }

    }

    private inner class BuffsTab : Component() {

        private var pos: Float = 0.toFloat()
        private val buffList: ScrollPane
        private val slots = ArrayList<BuffSlot>()

        init {
            buffList = object : ScrollPane(Component()) {
                override fun onClick(x: Float, y: Float) {
                    val size = slots.size
                    for (i in 0 until size) {
                        if (slots[i].onClick(x, y)) {
                            break
                        }
                    }
                }
            }
            add(buffList)
        }

        override fun layout() {
            super.layout()
            buffList.setRect(0f, 0f, width, height)
        }

        fun setupList() {
            val content = buffList.content()
            for (buff in Dungeon.hero!!.buffs()) {
                if (buff.icon() != BuffIndicator.NONE) {
                    val slot = BuffSlot(buff)
                    slot.setRect(0f, pos, WIDTH.toFloat(), slot.icon.height())
                    content.add(slot)
                    slots.add(slot)
                    pos += GAP + slot.height()
                }
            }
            content.setSize(buffList.width(), pos)
            buffList.setSize(buffList.width(), buffList.height())
        }

        private inner class BuffSlot(private val buff: Buff) : Component() {

            internal var icon: Image
            internal var txt: RenderedText

            init {
                val index = buff.icon()

                icon = Image(icons)
                icon.frame(film.get(index))
                buff.tintIcon(icon)
                icon.y = this.y
                add(icon)

                txt = PixelScene.renderText(buff.toString(), 8)
                txt.x = icon.width + GAP
                txt.y = this.y + (icon.height - txt.baseLine()).toInt() / 2
                add(txt)

            }

            override fun layout() {
                super.layout()
                icon.y = this.y
                txt.x = icon.width + GAP
                txt.y = pos + (icon.height - txt.baseLine()).toInt() / 2
            }

            fun onClick(x: Float, y: Float): Boolean {
                if (inside(x, y)) {
                    GameScene.show(WndInfoBuff(buff))
                    return true
                } else {
                    return false
                }
            }
        }

    }

    companion object {

        private val BIG_GAP = 5
        private val GAP = 2
        private val WIDTH = 115
        private val HEIGHT = 100
    }
}
