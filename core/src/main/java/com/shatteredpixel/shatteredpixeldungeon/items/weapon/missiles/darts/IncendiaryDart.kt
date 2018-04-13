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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet

class IncendiaryDart : TippedDart() {

    init {
        image = ItemSpriteSheet.INCENDIARY_DART
    }

    override fun onThrow(cell: Int) {
        val enemy = Actor.findChar(cell)
        if ((enemy == null || enemy === Item.curUser) && Dungeon.level!!.flamable[cell]) {
            GameScene.add(Blob.seed<Fire>(cell, 4, Fire::class.java))
            Dungeon.level!!.drop(Dart(), cell).sprite!!.drop()
        } else {
            super.onThrow(cell)
        }
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {
        Buff.affect<Burning>(defender, Burning::class.java)!!.reignite(defender)
        return super.proc(attacker, defender, damage)
    }

}
