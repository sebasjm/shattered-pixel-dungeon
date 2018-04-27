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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class ScrollOfMagicMapping : Scroll() {

    init {
        initials = 3
    }

    override fun doRead() {

        val length = Dungeon.level!!.length()
        val map = Dungeon.level!!.map
        val mapped = Dungeon.level!!.mapped
        val discoverable = Dungeon.level!!.discoverable

        var noticed = false

        for (i in 0 until length) {

            val terr = map!![i]

            if (discoverable[i]) {

                mapped[i] = true
                if (Terrain.flags[terr] and Terrain.SECRET != 0) {

                    Dungeon.level!!.discover(i)

                    if (Dungeon.level!!.heroFOV[i]) {
                        GameScene.discoverTile(i, terr)
                        discover(i)

                        noticed = true
                    }
                }
            }
        }
        GameScene.updateFog()

        GLog.i(Messages.get(this.javaClass, "layout"))
        if (noticed) {
            Sample.INSTANCE.play(Assets.SND_SECRET)
        }

        SpellSprite.show(Item.curUser!!, SpellSprite.MAP)
        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()

        setKnown()

        readAnimation()
    }

    override fun empoweredRead() {
        doRead()
        Buff.affect<MindVision>(Item.curUser!!, MindVision::class.java, MindVision.DURATION)
        Buff.affect<Awareness>(Item.curUser!!, Awareness::class.java, Awareness.DURATION)
        Dungeon.observe()
    }

    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }

    companion object {

        fun discover(cell: Int) {
            CellEmitter.get(cell).start(Speck.factory(Speck.DISCOVER), 0.1f, 4)
        }
    }
}
