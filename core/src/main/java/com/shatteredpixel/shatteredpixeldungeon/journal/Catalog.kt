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

package com.shatteredpixel.shatteredpixeldungeon.journal

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RogueArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knuckles
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang
import com.watabou.utils.Bundle

import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap

enum class Catalog {

    WEAPONS,
    ARMOR,
    WANDS,
    RINGS,
    ARTIFACTS,
    POTIONS,
    SCROLLS;

    private val seen = LinkedHashMap<Class<out Item>, Boolean>()

    fun items(): Collection<Class<out Item>> {
        return seen.keys
    }

    fun allSeen(): Boolean {
        for (item in items()) {
            if (!seen[item]) {
                return false
            }
        }
        return true
    }

    companion object {

        init {
            WEAPONS.seen[WornShortsword::class.java] = false
            WEAPONS.seen[Knuckles::class.java] = false
            WEAPONS.seen[Dagger::class.java] = false
            WEAPONS.seen[MagesStaff::class.java] = false
            WEAPONS.seen[Boomerang::class.java] = false
            WEAPONS.seen[Shortsword::class.java] = false
            WEAPONS.seen[HandAxe::class.java] = false
            WEAPONS.seen[Spear::class.java] = false
            WEAPONS.seen[Quarterstaff::class.java] = false
            WEAPONS.seen[Dirk::class.java] = false
            WEAPONS.seen[Sword::class.java] = false
            WEAPONS.seen[Mace::class.java] = false
            WEAPONS.seen[Scimitar::class.java] = false
            WEAPONS.seen[RoundShield::class.java] = false
            WEAPONS.seen[Sai::class.java] = false
            WEAPONS.seen[Whip::class.java] = false
            WEAPONS.seen[Longsword::class.java] = false
            WEAPONS.seen[BattleAxe::class.java] = false
            WEAPONS.seen[Flail::class.java] = false
            WEAPONS.seen[RunicBlade::class.java] = false
            WEAPONS.seen[AssassinsBlade::class.java] = false
            WEAPONS.seen[Crossbow::class.java] = false
            WEAPONS.seen[Greatsword::class.java] = false
            WEAPONS.seen[WarHammer::class.java] = false
            WEAPONS.seen[Glaive::class.java] = false
            WEAPONS.seen[Greataxe::class.java] = false
            WEAPONS.seen[Greatshield::class.java] = false
            WEAPONS.seen[Gauntlet::class.java] = false

            ARMOR.seen[ClothArmor::class.java] = false
            ARMOR.seen[LeatherArmor::class.java] = false
            ARMOR.seen[MailArmor::class.java] = false
            ARMOR.seen[ScaleArmor::class.java] = false
            ARMOR.seen[PlateArmor::class.java] = false
            ARMOR.seen[WarriorArmor::class.java] = false
            ARMOR.seen[MageArmor::class.java] = false
            ARMOR.seen[RogueArmor::class.java] = false
            ARMOR.seen[HuntressArmor::class.java] = false

            WANDS.seen[WandOfMagicMissile::class.java] = false
            WANDS.seen[WandOfLightning::class.java] = false
            WANDS.seen[WandOfDisintegration::class.java] = false
            WANDS.seen[WandOfFireblast::class.java] = false
            WANDS.seen[WandOfCorrosion::class.java] = false
            WANDS.seen[WandOfBlastWave::class.java] = false
            //WANDS.seen.put( WandOfLivingEarth.class,          false);
            WANDS.seen[WandOfFrost::class.java] = false
            WANDS.seen[WandOfPrismaticLight::class.java] = false
            //WANDS.seen.put( WandOfWarding.class,              false);
            WANDS.seen[WandOfTransfusion::class.java] = false
            WANDS.seen[WandOfCorruption::class.java] = false
            WANDS.seen[WandOfRegrowth::class.java] = false

            RINGS.seen[RingOfAccuracy::class.java] = false
            RINGS.seen[RingOfEnergy::class.java] = false
            RINGS.seen[RingOfElements::class.java] = false
            RINGS.seen[RingOfEvasion::class.java] = false
            RINGS.seen[RingOfForce::class.java] = false
            RINGS.seen[RingOfFuror::class.java] = false
            RINGS.seen[RingOfHaste::class.java] = false
            RINGS.seen[RingOfMight::class.java] = false
            RINGS.seen[RingOfSharpshooting::class.java] = false
            RINGS.seen[RingOfTenacity::class.java] = false
            RINGS.seen[RingOfWealth::class.java] = false

            //ARTIFACTS.seen.put( AlchemistsToolkit.class,      false);
            ARTIFACTS.seen[CapeOfThorns::class.java] = false
            ARTIFACTS.seen[ChaliceOfBlood::class.java] = false
            ARTIFACTS.seen[CloakOfShadows::class.java] = false
            ARTIFACTS.seen[DriedRose::class.java] = false
            ARTIFACTS.seen[EtherealChains::class.java] = false
            ARTIFACTS.seen[HornOfPlenty::class.java] = false
            ARTIFACTS.seen[LloydsBeacon::class.java] = false
            ARTIFACTS.seen[MasterThievesArmband::class.java] = false
            ARTIFACTS.seen[SandalsOfNature::class.java] = false
            ARTIFACTS.seen[TalismanOfForesight::class.java] = false
            ARTIFACTS.seen[TimekeepersHourglass::class.java] = false
            ARTIFACTS.seen[UnstableSpellbook::class.java] = false

            POTIONS.seen[PotionOfHealing::class.java] = false
            POTIONS.seen[PotionOfStrength::class.java] = false
            POTIONS.seen[PotionOfLiquidFlame::class.java] = false
            POTIONS.seen[PotionOfFrost::class.java] = false
            POTIONS.seen[PotionOfToxicGas::class.java] = false
            POTIONS.seen[PotionOfParalyticGas::class.java] = false
            POTIONS.seen[PotionOfPurity::class.java] = false
            POTIONS.seen[PotionOfLevitation::class.java] = false
            POTIONS.seen[PotionOfMindVision::class.java] = false
            POTIONS.seen[PotionOfInvisibility::class.java] = false
            POTIONS.seen[PotionOfExperience::class.java] = false
            POTIONS.seen[PotionOfMight::class.java] = false

            SCROLLS.seen[ScrollOfIdentify::class.java] = false
            SCROLLS.seen[ScrollOfUpgrade::class.java] = false
            SCROLLS.seen[ScrollOfRemoveCurse::class.java] = false
            SCROLLS.seen[ScrollOfMagicMapping::class.java] = false
            SCROLLS.seen[ScrollOfTeleportation::class.java] = false
            SCROLLS.seen[ScrollOfRecharging::class.java] = false
            SCROLLS.seen[ScrollOfMirrorImage::class.java] = false
            SCROLLS.seen[ScrollOfTerror::class.java] = false
            SCROLLS.seen[ScrollOfLullaby::class.java] = false
            SCROLLS.seen[ScrollOfRage::class.java] = false
            SCROLLS.seen[ScrollOfPsionicBlast::class.java] = false
            SCROLLS.seen[ScrollOfMagicalInfusion::class.java] = false
        }

        var catalogBadges = LinkedHashMap<Catalog, Badges.Badge>()

        init {
            catalogBadges[WEAPONS] = Badges.Badge.ALL_WEAPONS_IDENTIFIED
            catalogBadges[ARMOR] = Badges.Badge.ALL_ARMOR_IDENTIFIED
            catalogBadges[WANDS] = Badges.Badge.ALL_WANDS_IDENTIFIED
            catalogBadges[RINGS] = Badges.Badge.ALL_RINGS_IDENTIFIED
            catalogBadges[ARTIFACTS] = Badges.Badge.ALL_ARTIFACTS_IDENTIFIED
            catalogBadges[POTIONS] = Badges.Badge.ALL_POTIONS_IDENTIFIED
            catalogBadges[SCROLLS] = Badges.Badge.ALL_SCROLLS_IDENTIFIED
        }

        fun isSeen(itemClass: Class<out Item>): Boolean {
            for (cat in values()) {
                if (cat.seen.containsKey(itemClass)) {
                    return cat.seen[itemClass]
                }
            }
            return false
        }

        fun setSeen(itemClass: Class<out Item>) {
            for (cat in values()) {
                if (cat.seen.containsKey(itemClass) && !cat.seen[itemClass]) {
                    cat.seen[itemClass] = true
                    Journal.saveNeeded = true
                }
            }
            Badges.validateItemsIdentified()
        }

        private val CATALOGS = "catalogs"

        fun store(bundle: Bundle) {

            Badges.loadGlobal()

            val seen = ArrayList<String>()

            //if we have identified all items of a set, we use the badge to keep track instead.
            if (!Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED)) {
                for (cat in values()) {
                    if (!Badges.isUnlocked(catalogBadges[cat])) {
                        for (item in cat.items()) {
                            if (cat.seen[item]) seen.add(item.simpleName)
                        }
                    }
                }
            }

            bundle.put(CATALOGS, seen.toTypedArray<String>())

        }

        fun restore(bundle: Bundle) {

            Badges.loadGlobal()

            //logic for if we have all badges
            if (Badges.isUnlocked(Badges.Badge.ALL_ITEMS_IDENTIFIED)) {
                for (cat in values()) {
                    for (item in cat.items()) {
                        cat.seen[item] = true
                    }
                }
                return
            }

            //catalog-specific badge logic
            for (cat in values()) {
                if (Badges.isUnlocked(catalogBadges[cat])) {
                    for (item in cat.items()) {
                        cat.seen[item] = true
                    }
                }
            }

            //general save/load
            if (bundle.contains(CATALOGS)) {
                val seen = Arrays.asList(*bundle.getStringArray(CATALOGS)!!)

                //pre-0.6.3 saves
                //TODO should adjust this to tie into the bundling system's class array
                if (seen.contains("WandOfVenom")) {
                    WANDS.seen[WandOfCorrosion::class.java] = true
                }

                for (cat in values()) {
                    for (item in cat.items()) {
                        if (seen.contains(item.simpleName)) {
                            cat.seen[item] = true
                        }
                    }
                }
            }
        }
    }

}
