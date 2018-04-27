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

package com.shatteredpixel.shatteredpixeldungeon.plants

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class Fadeleaf : Plant() {

    init {
        image = 6
    }

    override fun activate() {
        val ch = Actor.findChar(pos)

        if (ch is Hero) {

            ScrollOfTeleportation.teleportHero(ch)
            ch.curAction = null

        } else if (ch is Mob && !ch.properties().contains(com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.IMMOVABLE)) {

            var count = 10
            var newPos: Int
            do {
                newPos = Dungeon.level!!.randomRespawnCell()
                if (count-- <= 0) {
                    break
                }
            } while (newPos == -1)

            if (newPos != -1 && !Dungeon.bossLevel()) {

                ch.pos = newPos
                if (ch.state === ch.HUNTING) ch.state = ch.WANDERING
                ch.sprite!!.place(ch.pos)
                ch.sprite!!.visible = Dungeon.level!!.heroFOV[ch.pos]

            }

        }

        if (Dungeon.level!!.heroFOV[pos]) {
            CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
        }
    }

    class Seed : Plant.Seed() {
        init {
            image = ItemSpriteSheet.SEED_FADELEAF

            plantClass = Fadeleaf::class.java
            alchemyClass = PotionOfMindVision::class.java
        }
    }
}
