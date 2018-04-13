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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Image
import com.watabou.utils.Bundle

class Sungrass : Plant() {

    init {
        image = 4
    }

    override fun activate() {
        val ch = Actor.findChar(pos)

        if (ch === Dungeon.hero) {
            Buff.affect<Health>(ch!!, Health::class.java)!!.boost(ch.HT)
        }

        if (Dungeon.level!!.heroFOV[pos]) {
            CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3)
        }
    }

    class Seed : Plant.Seed() {
        init {
            image = ItemSpriteSheet.SEED_SUNGRASS

            plantClass = Sungrass::class.java
            alchemyClass = PotionOfHealing::class.java

            bones = true
        }
    }

    class Health : Buff() {

        private var pos: Int = 0
        private var partialHeal: Float = 0.toFloat()
        private var level: Int = 0

        init {
            type = Buff.buffType.POSITIVE
        }

        override fun act(): Boolean {
            if (target.pos != pos) {
                detach()
            }

            //for the hero, full heal takes ~50/93/111/120 turns at levels 1/10/20/30
            partialHeal += (40 + target.HT) / 150f

            if (partialHeal > 1) {
                target.HP += partialHeal.toInt()
                level -= partialHeal.toInt()
                partialHeal -= partialHeal.toInt().toFloat()
                target.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)

                if (target.HP > target.HT) target.HP = target.HT
            }

            if (level <= 0) {
                detach()
            } else {
                BuffIndicator.refreshHero()
            }
            spend(STEP)
            return true
        }

        fun boost(amount: Int) {
            level += amount
            pos = target.pos
        }

        override fun icon(): Int {
            return BuffIndicator.HEALING
        }

        override fun tintIcon(icon: Image) {
            FlavourBuff.greyIcon(icon, target.HT / 4f, level.toFloat())
        }

        override fun toString(): String {
            return Messages.get(this, "name")
        }

        override fun desc(): String {
            return Messages.get(this, "desc", level)
        }

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(POS, pos)
            bundle.put(PARTIAL, partialHeal)
            bundle.put(LEVEL, level)
        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            pos = bundle.getInt(POS)
            partialHeal = bundle.getFloat(PARTIAL)
            level = bundle.getInt(LEVEL)

        }

        companion object {

            private val STEP = 1f

            private val POS = "pos"
            private val PARTIAL = "partial_heal"
            private val LEVEL = "level"
        }
    }
}
