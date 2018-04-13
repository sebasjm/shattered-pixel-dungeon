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

package com.shatteredpixel.shatteredpixeldungeon.items.quest

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NewbornElemental
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.audio.Sample
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList


class CeremonialCandle : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        image = ItemSpriteSheet.CANDLE

        defaultAction = Item.AC_THROW

        unique = true
        stackable = true
    }

    override fun doDrop(hero: Hero) {
        super.doDrop(hero)
        checkCandles()
    }

    override fun onThrow(cell: Int) {
        super.onThrow(cell)
        checkCandles()
    }

    companion object {

        //generated with the wandmaker quest
        var ritualPos: Int = 0

        private fun checkCandles() {
            val heapTop = Dungeon.level!!.heaps.get(ritualPos - Dungeon.level!!.width())
            val heapRight = Dungeon.level!!.heaps.get(ritualPos + 1)
            val heapBottom = Dungeon.level!!.heaps.get(ritualPos + Dungeon.level!!.width())
            val heapLeft = Dungeon.level!!.heaps.get(ritualPos - 1)

            if (heapTop != null &&
                    heapRight != null &&
                    heapBottom != null &&
                    heapLeft != null) {

                if (heapTop.peek() is CeremonialCandle &&
                        heapRight.peek() is CeremonialCandle &&
                        heapBottom.peek() is CeremonialCandle &&
                        heapLeft.peek() is CeremonialCandle) {

                    heapTop.pickUp()
                    heapRight.pickUp()
                    heapBottom.pickUp()
                    heapLeft.pickUp()

                    val elemental = NewbornElemental()
                    val ch = Actor.findChar(ritualPos)
                    if (ch != null) {
                        val candidates = ArrayList<Int>()
                        for (n in PathFinder.NEIGHBOURS8) {
                            val cell = ritualPos + n
                            if ((Dungeon.level!!.passable[cell] || Dungeon.level!!.avoid[cell]) && Actor.findChar(cell) == null) {
                                candidates.add(cell)
                            }
                        }
                        if (candidates.size > 0) {
                            elemental.pos = Random.element(candidates)!!
                        } else {
                            elemental.pos = ritualPos
                        }
                    } else {
                        elemental.pos = ritualPos
                    }
                    elemental.state = elemental.HUNTING
                    GameScene.add(elemental, 1f)

                    for (i in PathFinder.NEIGHBOURS9) {
                        CellEmitter.get(ritualPos + i).burst(ElmoParticle.FACTORY, 10)
                    }
                    Sample.INSTANCE.play(Assets.SND_BURNING)
                }
            }

        }
    }
}
