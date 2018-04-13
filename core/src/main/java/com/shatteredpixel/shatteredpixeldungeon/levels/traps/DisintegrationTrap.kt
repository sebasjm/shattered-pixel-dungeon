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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Random

class DisintegrationTrap : Trap() {

    init {
        color = Trap.VIOLET
        shape = Trap.CROSSHAIR
    }

    override fun hide(): Trap {
        //this one can't be hidden
        return reveal()
    }

    override fun activate() {
        var target = Actor.findChar(pos)

        //find the closest char that can be aimed at
        if (target == null) {
            for (ch in Actor.chars()) {
                val bolt = Ballistica(pos, ch.pos, Ballistica.PROJECTILE)
                if (bolt.collisionPos == ch.pos && (target == null || Dungeon.level!!.trueDistance(pos, ch.pos) < Dungeon.level!!.trueDistance(pos, target.pos))) {
                    target = ch
                }
            }
        }

        val heap = Dungeon.level!!.heaps.get(pos)
        heap?.explode()

        if (target != null) {
            if (Dungeon.level!!.heroFOV[pos] || Dungeon.level!!.heroFOV[target.pos]) {
                Sample.INSTANCE.play(Assets.SND_RAY)
                ShatteredPixelDungeon.scene()!!.add(Beam.DeathRay(DungeonTilemap.tileCenterToWorld(pos), target.sprite!!.center()))
            }
            target.damage(Math.max(target.HT / 5, Random.Int(target.HP / 2, 2 * target.HP / 3)), this)
            if (target === Dungeon.hero) {
                val hero = target
                if (!hero.isAlive) {
                    Dungeon.fail(javaClass)
                    GLog.n(Messages.get(this, "ondeath"))
                } else {
                    var item = hero.belongings.randomUnequipped()
                    var bag = hero.belongings.backpack
                    //bags do not protect against this trap
                    if (item is Bag) {
                        bag = item
                        item = Random.element(bag.items)
                    }
                    if (item == null || item.level() > 0 || item.unique) return
                    if (!item.stackable) {
                        item.detachAll(bag)
                        GLog.w(Messages.get(this, "one", item.name()))
                    } else {
                        val n = Random.NormalIntRange(1, (item.quantity() + 1) / 2)
                        for (i in 1..n)
                            item.detach(bag)
                        GLog.w(Messages.get(this, "some", item.name()))
                    }
                }
            }
        }

    }
}
