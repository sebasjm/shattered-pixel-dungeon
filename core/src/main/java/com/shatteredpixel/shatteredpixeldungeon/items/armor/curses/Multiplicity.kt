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

package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class Multiplicity : Armor.Glyph() {

    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {

        if (Random.Int(20) == 0) {
            val spawnPoints = ArrayList<Int>()

            for (i in PathFinder.NEIGHBOURS8.indices) {
                val p = defender.pos + PathFinder.NEIGHBOURS8[i]
                if (Actor.findChar(p) == null && (Dungeon.level!!.passable[p] || Dungeon.level!!.avoid[p])) {
                    spawnPoints.add(p)
                }
            }

            if (spawnPoints.size > 0) {

                var m: Mob? = null
                if (Random.Int(2) == 0 && defender is Hero) {
                    m = MirrorImage()
                    m.duplicate(defender)

                } else {
                    //FIXME should probably have a mob property for this
                    if (attacker.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.BOSS) || attacker.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.MINIBOSS)
                            || attacker is Mimic || attacker is Statue) {
                        m = Dungeon.level!!.createMob()
                    } else {
                        try {
                            Actor.fixTime()

                            m = attacker.javaClass.newInstance()
                            val store = Bundle()
                            attacker.storeInBundle(store)
                            m!!.restoreFromBundle(store)
                            m.HP = m.HT

                            //If a thief has stolen an item, that item is not duplicated.
                            if (m is Thief) {
                                m.item = null
                            }

                        } catch (e: Exception) {
                            ShatteredPixelDungeon.reportException(e)
                            m = null
                        }

                    }

                }

                if (m != null) {
                    GameScene.add(m)
                    ScrollOfTeleportation.appear(m, Random.element(spawnPoints)!!)
                }

            }
        }

        return damage
    }

    override fun glowing(): ItemSprite.Glowing {
        return BLACK
    }

    override fun curse(): Boolean {
        return true
    }

    companion object {

        private val BLACK = ItemSprite.Glowing(0x000000)
    }
}
