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

package com.shatteredpixel.shatteredpixeldungeon.items.armor

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.Camera
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder

class WarriorArmor : ClassArmor() {

    init {
        image = ItemSpriteSheet.ARMOR_WARRIOR
    }

    override fun doSpecial() {
        GameScene.selectCell(leaper)
    }

    companion object {

        private val LEAP_TIME = 1
        private val SHOCK_TIME = 3

        protected var leaper: CellSelector.Listener = object : CellSelector.Listener {

            override fun onSelect(target: Int?) {
                if (target != null && target != Item.curUser.pos) {

                    val route = Ballistica(Item.curUser.pos, target, Ballistica.PROJECTILE)
                    var cell = route.collisionPos!!

                    //can't occupy the same cell as another char, so move back one.
                    if (Actor.findChar(cell) != null && cell != Item.curUser.pos)
                        cell = route.path[route.dist!! - 1]


                    Item.curUser.HP -= Item.curUser.HP / 3

                    val dest = cell
                    Item.curUser.busy()
                    Item.curUser.sprite!!.jump(Item.curUser.pos, cell, Callback {
                        Item.curUser.move(dest)
                        Dungeon.level!!.press(dest, Item.curUser, true)
                        Dungeon.observe()
                        GameScene.updateFog()

                        for (i in PathFinder.NEIGHBOURS8.indices) {
                            val mob = Actor.findChar(Item.curUser.pos + PathFinder.NEIGHBOURS8[i])
                            if (mob != null && mob !== Item.curUser) {
                                Buff.prolong<Paralysis>(mob, Paralysis::class.java, SHOCK_TIME.toFloat())
                            }
                        }

                        CellEmitter.center(dest).burst(Speck.factory(Speck.DUST), 10)
                        Camera.main.shake(2f, 0.5f)

                        Item.curUser.spendAndNext(LEAP_TIME.toFloat())
                    })
                }
            }

            override fun prompt(): String {
                return Messages.get(WarriorArmor::class.java, "prompt")
            }
        }
    }
}