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

package com.shatteredpixel.shatteredpixeldungeon.items

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.Game

import java.io.IOException
import java.util.ArrayList

class Amulet : Item() {

    override val isIdentified: Boolean
        get() = true

    override val isUpgradable: Boolean
        get() = false

    init {
        image = ItemSpriteSheet.AMULET

        unique = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_END)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_END) {
            showAmuletScene(false)
        }
    }

    override fun doPickUp(hero: Hero): Boolean {
        if (super.doPickUp(hero)) {

            if (!Statistics.amuletObtained) {
                Statistics.amuletObtained = true
                Badges.validateVictory()
                hero.spend(-Item.TIME_TO_PICK_UP)

                //add a delayed actor here so pickup behaviour can fully process.
                Actor.addDelayed(object : Actor() {
                    override fun act(): Boolean {
                        Actor.remove(this)
                        showAmuletScene(true)
                        return false
                    }
                }, -5f)
            }

            return true
        } else {
            return false
        }
    }

    private fun showAmuletScene(showText: Boolean) {
        try {
            Dungeon.saveAll()
            AmuletScene.noText = !showText
            Game.switchScene(AmuletScene::class.java)
        } catch (e: IOException) {
            Game.reportException(e)
        }

    }

    companion object {

        private val AC_END = "END"
    }

}
