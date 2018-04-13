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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Collections

class CursingTrap : Trap() {

    init {
        color = Trap.VIOLET
        shape = Trap.WAVES
    }

    override fun activate() {
        if (Dungeon.level!!.heroFOV[pos]) {
            CellEmitter.get(pos).burst(ShadowParticle.UP, 5)
            Sample.INSTANCE.play(Assets.SND_CURSED)
        }

        val heap = Dungeon.level!!.heaps.get(pos)
        if (heap != null) {
            for (item in heap.items!!) {
                if (item.isUpgradable)
                    curse(item)
            }
        }

        if (Dungeon.hero!!.pos == pos) {
            curse(Dungeon.hero!!)
        }
    }

    companion object {

        fun curse(hero: Hero) {
            //items the trap wants to curse because it will create a more negative effect
            val priorityCurse = ArrayList<Item>()
            //items the trap can curse if nothing else is available.
            val canCurse = ArrayList<Item>()

            val weapon = hero.belongings.weapon
            if (weapon is Weapon && !weapon.cursed && weapon !is Boomerang) {
                if (weapon.enchantment == null)
                    priorityCurse.add(weapon)
                else
                    canCurse.add(weapon)
            }

            val armor = hero.belongings.armor
            if (armor != null && !armor.cursed) {
                if (armor.glyph == null)
                    priorityCurse.add(armor)
                else
                    canCurse.add(armor)
            }

            val misc1 = hero.belongings.misc1
            if (misc1 != null) {
                canCurse.add(misc1)
            }

            val misc2 = hero.belongings.misc2
            if (misc2 != null) {
                canCurse.add(misc2)
            }

            Collections.shuffle(priorityCurse)
            Collections.shuffle(canCurse)

            val numCurses = if (Random.Int(2) == 0) 1 else 2

            for (i in 0 until numCurses) {
                if (!priorityCurse.isEmpty()) {
                    curse(priorityCurse.removeAt(0))
                } else if (!canCurse.isEmpty()) {
                    curse(canCurse.removeAt(0))
                }
            }

            EquipableItem.equipCursed(hero)
            GLog.n(Messages.get(CursingTrap::class.java, "curse"))
        }

        private fun curse(item: Item) {
            item.cursedKnown = true
            item.cursed = item.cursedKnown

            if (item is Weapon) {
                val w = item
                if (w.enchantment == null) {
                    w.enchantment = Weapon.Enchantment.randomCurse()
                }
            }
            if (item is Armor) {
                val a = item
                if (a.glyph == null) {
                    a.glyph = Armor.Glyph.randomCurse()
                }
            }
        }
    }
}
