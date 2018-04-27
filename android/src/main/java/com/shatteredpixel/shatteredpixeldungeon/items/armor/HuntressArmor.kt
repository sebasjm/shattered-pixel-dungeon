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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Callback

import java.util.HashMap

class HuntressArmor : ClassArmor() {

    private val targets = HashMap<Callback, Mob>()


    init {
        image = ItemSpriteSheet.ARMOR_HUNTRESS
    }

    override fun doSpecial() {

        val proto = Shuriken()

        for (mob in Dungeon.level!!.mobs) {
            if (Dungeon.level!!.heroFOV[mob.pos]) {

                val callback = object : Callback {
                    override fun call() {
                        Item.curUser!!.attack(targets[this])
                        targets.remove(this)
                        if (targets.isEmpty()) {
                            Item.curUser!!.spendAndNext(Item.curUser!!.attackDelay())
                        }
                    }
                }

                (Item.curUser!!.sprite!!.parent!!.recycle(MissileSprite::class.java) as MissileSprite).reset(Item.curUser!!.pos, mob.pos, proto, callback)

                targets[callback] = mob
            }
        }

        if (targets.size == 0) {
            GLog.w(Messages.get(this.javaClass, "no_enemies"))
            return
        }

        Item.curUser!!.HP -= Item.curUser!!.HP / 3

        Item.curUser!!.sprite!!.zap(Item.curUser!!.pos)
        Item.curUser!!.busy()
    }

}