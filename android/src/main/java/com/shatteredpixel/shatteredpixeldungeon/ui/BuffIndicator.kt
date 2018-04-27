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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.noosa.ui.Button
import com.watabou.noosa.ui.Component

import java.util.ArrayList
import java.util.LinkedHashMap

class BuffIndicator(private val ch: Char) : Component() {

    private var texture: SmartTexture? = null
    private var film: TextureFilm? = null

    private val buffIcons = LinkedHashMap<Buff, BuffIcon>()
    private var needsRefresh: Boolean = false

    init {
        if (ch === Dungeon.hero!!) {
            heroInstance = this
        }
    }

    override fun destroy() {
        super.destroy()

        if (this === heroInstance) {
            heroInstance = null
        }
    }

    override fun createChildren() {
        texture = TextureCache.get(Assets.BUFFS_SMALL)
        film = TextureFilm(texture!!, SIZE, SIZE)
    }

    @Synchronized
    override fun update() {
        super.update()
        if (needsRefresh) {
            needsRefresh = false
            layout()
        }
    }

    override fun layout() {

        val newBuffs = ArrayList<Buff>()
        for (buff in ch.buffs()) {
            if (buff.icon() != NONE) {
                newBuffs.add(buff)
            }
        }

        //remove any icons no longer present
        for (buff in buffIcons.keys.toTypedArray<Buff>()) {
            if (!newBuffs.contains(buff)) {
                val icon = buffIcons[buff]!!.icon
                icon.origin.set((SIZE / 2).toFloat())
                add(icon)
                add(object : AlphaTweener(icon, 0f, 0.6f) {
                    override fun updateValues(progress: Float) {
                        super.updateValues(progress)
                        image.scale.set(1 + 5 * progress)
                    }

                    override fun onComplete() {
                        image.killAndErase()
                    }
                })

                buffIcons[buff]!!.destroy()
                remove(buffIcons[buff]!!)
                buffIcons.remove(buff)
            }
        }

        //add new icons
        for (buff in newBuffs) {
            if (!buffIcons.containsKey(buff)) {
                val icon = BuffIcon(buff)
                add(icon)
                buffIcons[buff] = icon
            }
        }

        //layout
        var pos = 0
        for (icon in buffIcons.values) {
            icon.updateIcon()
            icon.setRect(x + pos * (SIZE + 2), y, 9f, 12f)
            pos++
        }
    }

    private inner class BuffIcon(private val buff: Buff) : Button() {

        var icon: Image

        init {
            icon = Image(texture!!)
            icon.frame(film!!.get(buff.icon()))
            add(icon)
        }

        fun updateIcon() {
            icon.frame(film!!.get(buff.icon()))
            buff.tintIcon(icon)
        }

        override fun layout() {
            super.layout()
            icon.x = this.x + 1
            icon.y = this.y + 2
        }

        override fun onClick() {
            if (buff.icon() != NONE)
                GameScene.show(WndInfoBuff(buff))
        }
    }

    companion object {

        //transparent icon
        val NONE = 63

        //TODO consider creating an enum to store both index, and tint. Saves making separate images for color differences.
        val MIND_VISION = 0
        val LEVITATION = 1
        val FIRE = 2
        val POISON = 3
        val PARALYSIS = 4
        val HUNGER = 5
        val STARVATION = 6
        val SLOW = 7
        val OOZE = 8
        val AMOK = 9
        val TERROR = 10
        val ROOTS = 11
        val INVISIBLE = 12
        val SHADOWS = 13
        val WEAKNESS = 14
        val FROST = 15
        val BLINDNESS = 16
        val COMBO = 17
        val FURY = 18
        val HEALING = 19
        val ARMOR = 20
        val HEART = 21
        val LIGHT = 22
        val CRIPPLE = 23
        val BARKSKIN = 24
        val IMMUNITY = 25
        val BLEEDING = 26
        val MARK = 27
        val DEFERRED = 28
        val DROWSY = 29
        val MAGIC_SLEEP = 30
        val THORNS = 31
        val FORESIGHT = 32
        val VERTIGO = 33
        val RECHARGING = 34
        val LOCKED_FLOOR = 35
        val CORRUPT = 36
        val BLESS = 37
        val RAGE = 38
        val SACRIFICE = 39
        val BERSERK = 40
        val MOMENTUM = 41
        val PREPARATION = 42

        val SIZE = 7

        private var heroInstance: BuffIndicator? = null

        fun refreshHero() {
            if (heroInstance != null) {
                heroInstance!!.needsRefresh = true
            }
        }
    }
}
