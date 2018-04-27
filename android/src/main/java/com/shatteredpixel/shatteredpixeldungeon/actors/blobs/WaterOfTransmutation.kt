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

import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Generator.Category
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes.Landmark
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.watabou.noosa.Game
import com.watabou.utils.Random

class WaterOfTransmutation : WellWater() {

    override fun affectItem(item: Item): Item? {
        var item : Item? = item

        if (item is MagesStaff) {
            item = changeStaff((item as MagesStaff?)!!)
        } else if (item is MeleeWeapon) {
            item = changeWeapon((item as MeleeWeapon?)!!)
        } else if (item is Scroll) {
            item = changeScroll(item as Scroll)
        } else if (item is Potion) {
            item = changePotion(item as Potion)
        } else if (item is Ring) {
            item = changeRing((item as Ring?)!!)
        } else if (item is Wand) {
            item = changeWand((item as Wand?)!!)
        } else if (item is Plant.Seed) {
            item = changeSeed((item as Plant.Seed?)!!)
        } else if (item is Artifact) {
            item = changeArtifact(item as Artifact)
        } else {
            item = null
        }

        //incase a never-seen item pops out
        if (item != null && item.isIdentified) {
            Catalog.setSeen(item.javaClass)
        }

        return item

    }

    override fun affectHero(hero: Hero?): Boolean {
        return false
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0)
    }

    override fun record(): Landmark {
        return Landmark.WELL_OF_TRANSMUTATION
    }

    private fun changeStaff(staff: MagesStaff): MagesStaff? {
        val wandClass = staff.wandClass()

        if (wandClass == null) {
            return null
        } else {
            var n: Wand
            do {
                n = Generator.random(Category.WAND) as Wand
            } while (Challenges.isItemBlocked(n) || n.javaClass == wandClass)
            n.level(0)
            staff.imbueWand(n, null)
        }

        return staff
    }

    private fun changeWeapon(w: MeleeWeapon): Weapon? {

        var n: Weapon
        val c = Generator.wepTiers[w.tier - 1]

        do {
            try {
                n = c.classes!![Random.chances(c.probs!!)].newInstance() as MeleeWeapon
            } catch (e: Exception) {
                Game.reportException(e)
                return null
            }

        } while (Challenges.isItemBlocked(n) || n.javaClass == w.javaClass)

        val level = w.level()
        if (level > 0) {
            n.upgrade(level)
        } else if (level < 0) {
            n.degrade(-level)
        }

        n.enchantment = w.enchantment
        n.levelKnown = w.levelKnown
        n.cursedKnown = w.cursedKnown
        n.cursed = w.cursed
        n.imbue = w.imbue

        return n

    }

    private fun changeRing(r: Ring): Ring {
        var n: Ring
        do {
            n = Generator.random(Category.RING) as Ring
        } while (Challenges.isItemBlocked(n) || n.javaClass == r.javaClass)

        n.level(0)

        val level = r.level()
        if (level > 0) {
            n.upgrade(level)
        } else if (level < 0) {
            n.degrade(-level)
        }

        n.levelKnown = r.levelKnown
        n.cursedKnown = r.cursedKnown
        n.cursed = r.cursed

        return n
    }

    private fun changeArtifact(a: Artifact): Artifact {
        val n = Generator.randomArtifact()

        if (n != null && !Challenges.isItemBlocked(n)) {
            n.cursedKnown = a.cursedKnown
            n.cursed = a.cursed
            n.levelKnown = a.levelKnown
            n.transferUpgrade(a.visiblyUpgraded())
        }

        return n!!
    }

    private fun changeWand(w: Wand): Wand {

        var n: Wand
        do {
            n = Generator.random(Category.WAND) as Wand
        } while (Challenges.isItemBlocked(n) || n.javaClass == w.javaClass)

        n.level(0)
        n.upgrade(w.level())

        n.levelKnown = w.levelKnown
        n.cursedKnown = w.cursedKnown
        n.cursed = w.cursed

        return n
    }

    private fun changeSeed(s: Plant.Seed): Plant.Seed {

        var n: Plant.Seed

        do {
            n = Generator.random(Category.SEED) as Plant.Seed
        } while (n.javaClass == s.javaClass)

        return n
    }

    private fun changeScroll(s: Scroll): Scroll {
        if (s is ScrollOfUpgrade) {

            return ScrollOfMagicalInfusion()

        } else if (s is ScrollOfMagicalInfusion) {

            return ScrollOfUpgrade()

        } else {

            var n: Scroll
            do {
                n = Generator.random(Category.SCROLL) as Scroll
            } while (n.javaClass == s.javaClass)
            return n
        }
    }

    private fun changePotion(p: Potion): Potion {
        if (p is PotionOfStrength) {

            return PotionOfMight()

        } else if (p is PotionOfMight) {

            return PotionOfStrength()

        } else {

            var n: Potion
            do {
                n = Generator.random(Category.POTION) as Potion
            } while (n.javaClass == p.javaClass)
            return n
        }
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }
}
