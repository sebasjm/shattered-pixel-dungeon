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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knuckles
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Bundle

enum class HeroClass private constructor(private val title: String, vararg subClasses: HeroSubClass) {

    WARRIOR("warrior", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR),
    MAGE("mage", HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK),
    ROGUE("rogue", HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER),
    HUNTRESS("huntress", HeroSubClass.WARDEN, HeroSubClass.SNIPER);

    private val subClasses: Array<out HeroSubClass>

    init {
        this.subClasses = subClasses
    }

    fun initHero(hero: Hero) {

        hero.heroClass = this

        initCommon(hero)

        when (this) {
            WARRIOR -> initWarrior(hero)

            MAGE -> initMage(hero)

            ROGUE -> initRogue(hero)

            HUNTRESS -> initHuntress(hero)
        }

    }

    fun masteryBadge(): Badges.Badge? {
        when (this) {
            WARRIOR -> return Badges.Badge.MASTERY_WARRIOR
            MAGE -> return Badges.Badge.MASTERY_MAGE
            ROGUE -> return Badges.Badge.MASTERY_ROGUE
            HUNTRESS -> return Badges.Badge.MASTERY_HUNTRESS
        }
        return null
    }

    fun title(): String {
        return Messages.get(HeroClass::class.java, title)
    }

    fun subClasses(): Array<HeroSubClass> {
        return subClasses as Array<HeroSubClass>
    }

    fun spritesheet(): String? {

        when (this) {
            WARRIOR -> return Assets.WARRIOR
            MAGE -> return Assets.MAGE
            ROGUE -> return Assets.ROGUE
            HUNTRESS -> return Assets.HUNTRESS
        }

        return null
    }

    fun perks(): Array<String>? {

        when (this) {
            WARRIOR -> return arrayOf(Messages.get(HeroClass::class.java, "warrior_perk1"), Messages.get(HeroClass::class.java, "warrior_perk2"), Messages.get(HeroClass::class.java, "warrior_perk3"), Messages.get(HeroClass::class.java, "warrior_perk4"), Messages.get(HeroClass::class.java, "warrior_perk5"))
            MAGE -> return arrayOf(Messages.get(HeroClass::class.java, "mage_perk1"), Messages.get(HeroClass::class.java, "mage_perk2"), Messages.get(HeroClass::class.java, "mage_perk3"), Messages.get(HeroClass::class.java, "mage_perk4"), Messages.get(HeroClass::class.java, "mage_perk5"))
            ROGUE -> return arrayOf(Messages.get(HeroClass::class.java, "rogue_perk1"), Messages.get(HeroClass::class.java, "rogue_perk2"), Messages.get(HeroClass::class.java, "rogue_perk3"), Messages.get(HeroClass::class.java, "rogue_perk4"), Messages.get(HeroClass::class.java, "rogue_perk5"))
            HUNTRESS -> return arrayOf(Messages.get(HeroClass::class.java, "huntress_perk1"), Messages.get(HeroClass::class.java, "huntress_perk2"), Messages.get(HeroClass::class.java, "huntress_perk3"), Messages.get(HeroClass::class.java, "huntress_perk4"), Messages.get(HeroClass::class.java, "huntress_perk5"))
        }

        return null
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(CLASS, toString())
    }

    companion object {

        private fun initCommon(hero: Hero) {
            var i = ClothArmor().identify()
            if (!Challenges.isItemBlocked(i)) hero.belongings.armor = i as ClothArmor

            i = Food()
            if (!Challenges.isItemBlocked(i)) i.collect()

            if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
                SmallRation().collect()
            }

        }

        private fun initWarrior(hero: Hero) {
            hero.belongings.weapon = WornShortsword()
            hero.belongings.weapon!!.identify()

            val stones = ThrowingStone()
            stones.identify().quantity(3).collect()
            Dungeon.quickslot.setSlot(0, stones)

            if (hero.belongings.armor != null) {
                hero.belongings.armor!!.affixSeal(BrokenSeal())
            }

            PotionBandolier().collect()
            Dungeon.LimitedDrops.POTION_BANDOLIER.drop()
            PotionOfHealing().identify()
        }

        private fun initMage(hero: Hero) {
            val staff: MagesStaff

            staff = MagesStaff(WandOfMagicMissile())

            hero.belongings.weapon = staff
            hero.belongings.weapon!!.identify()
            hero.belongings.weapon!!.activate(hero)

            Dungeon.quickslot.setSlot(0, staff)

            ScrollHolder().collect()
            Dungeon.LimitedDrops.SCROLL_HOLDER.drop()
            ScrollOfUpgrade().identify()
        }

        private fun initRogue(hero: Hero) {
            hero.belongings.weapon = Dagger()
            hero.belongings.weapon!!.identify()

            val cloak = CloakOfShadows()
            hero.belongings.misc1 = cloak
            hero.belongings.misc1!!.identify()
            hero.belongings.misc1!!.activate(hero)

            val knives = ThrowingKnife()
            knives.quantity(3).collect()

            Dungeon.quickslot.setSlot(0, cloak)
            Dungeon.quickslot.setSlot(1, knives)

            VelvetPouch().collect()
            Dungeon.LimitedDrops.VELVET_POUCH.drop()
            ScrollOfMagicMapping().identify()
        }

        private fun initHuntress(hero: Hero) {
            hero.belongings.weapon = Knuckles()
            hero.belongings.weapon!!.identify()

            val boomerang = Boomerang()
            boomerang.identify().collect()

            Dungeon.quickslot.setSlot(0, boomerang)

            VelvetPouch().collect()
            Dungeon.LimitedDrops.VELVET_POUCH.drop()
            PotionOfMindVision().identify()
        }

        private val CLASS = "class"

        fun restoreInBundle(bundle: Bundle): HeroClass {
            val value = bundle.getString(CLASS)
            return if (value.length > 0) valueOf(value) else ROGUE
        }
    }
}
