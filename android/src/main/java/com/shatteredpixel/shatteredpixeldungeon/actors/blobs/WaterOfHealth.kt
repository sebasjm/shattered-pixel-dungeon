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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle
import com.shatteredpixel.shatteredpixeldungeon.items.DewVial
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes.Landmark
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class WaterOfHealth : WellWater() {

    override fun affectHero(hero: Hero?): Boolean {

        if (!hero!!.isAlive) return false

        Sample.INSTANCE.play(Assets.SND_DRINK)

        hero.HP = hero.HT
        hero.sprite!!.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4)

        PotionOfHealing.cure(hero)
        hero.belongings.uncurseEquipped()
        (hero.buff<Hunger>(Hunger::class.java) as Hunger).satisfy(Hunger.STARVING)

        CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3)

        Dungeon.hero!!.interrupt()

        GLog.p(Messages.get(this.javaClass, "procced"))

        return true
    }

    override fun affectItem(item: Item): Item? {
        if (item is DewVial && !item.isFull) {
            item.fill()
            return item
        }

        return null
    }

    override fun record(): Landmark {
        return Landmark.WELL_OF_HEALTH
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.HEALING), 0.5f, 0)
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }
}
