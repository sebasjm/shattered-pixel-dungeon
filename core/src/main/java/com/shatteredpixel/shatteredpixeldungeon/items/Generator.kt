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

package com.shatteredpixel.shatteredpixeldungeon.items

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact
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
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
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
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
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
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass
import com.watabou.utils.Bundle
import com.watabou.utils.GameMath
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap

object Generator {

    private val floorSetTierProbs = arrayOf(floatArrayOf(0f, 70f, 20f, 8f, 2f), floatArrayOf(0f, 25f, 50f, 20f, 5f), floatArrayOf(0f, 10f, 40f, 40f, 10f), floatArrayOf(0f, 5f, 20f, 50f, 25f), floatArrayOf(0f, 2f, 8f, 20f, 70f))

    private val categoryProbs = LinkedHashMap<Category, Float>()

    val wepTiers = arrayOf(Category.WEP_T1, Category.WEP_T2, Category.WEP_T3, Category.WEP_T4, Category.WEP_T5)

    val misTiers = arrayOf(Category.MIS_T1, Category.MIS_T2, Category.MIS_T3, Category.MIS_T4, Category.MIS_T5)

    private var spawnedArtifacts = ArrayList<Class<out Artifact>>()

    private val GENERAL_PROBS = "general_probs"
    private val SPAWNED_ARTIFACTS = "spawned_artifacts"

    enum class Category private constructor(var prob: Float, var superClass: Class<out Item>) {
        WEAPON(6, MeleeWeapon::class.java),
        WEP_T1(0, MeleeWeapon::class.java),
        WEP_T2(0, MeleeWeapon::class.java),
        WEP_T3(0, MeleeWeapon::class.java),
        WEP_T4(0, MeleeWeapon::class.java),
        WEP_T5(0, MeleeWeapon::class.java),

        ARMOR(4, Armor::class.java),

        MISSILE(3, MissileWeapon::class.java),
        MIS_T1(0, MissileWeapon::class.java),
        MIS_T2(0, MissileWeapon::class.java),
        MIS_T3(0, MissileWeapon::class.java),
        MIS_T4(0, MissileWeapon::class.java),
        MIS_T5(0, MissileWeapon::class.java),

        POTION(20, Potion::class.java),
        SCROLL(20, Scroll::class.java),

        WAND(3, Wand::class.java),
        RING(1, Ring::class.java),
        ARTIFACT(1, Artifact::class.java),

        SEED(0, Plant.Seed::class.java),

        FOOD(0, Food::class.java),

        GOLD(20, Gold::class.java);

        var classes: Array<Class<*>>
        var probs: FloatArray

        companion object {

            fun order(item: Item): Int {
                for (i in 0 until values().size) {
                    if (values()[i].superClass.isInstance(item)) {
                        return i
                    }
                }

                return if (item is Bag) Integer.MAX_VALUE else Integer.MAX_VALUE - 1
            }

            private val INITIAL_ARTIFACT_PROBS = floatArrayOf(0f, 1f, 0f, 1f, 0f, 1f, 1f, 1f, 1f, 0f, 1f, 0f, 1f)

            init {
                GOLD.classes = arrayOf(Gold::class.java)
                GOLD.probs = floatArrayOf(1f)

                SCROLL.classes = arrayOf(ScrollOfIdentify::class.java, ScrollOfTeleportation::class.java, ScrollOfRemoveCurse::class.java, ScrollOfUpgrade::class.java, ScrollOfRecharging::class.java, ScrollOfMagicMapping::class.java, ScrollOfRage::class.java, ScrollOfTerror::class.java, ScrollOfLullaby::class.java, ScrollOfMagicalInfusion::class.java, ScrollOfPsionicBlast::class.java, ScrollOfMirrorImage::class.java)
                SCROLL.probs = floatArrayOf(30f, 10f, 20f, 0f, 15f, 15f, 12f, 8f, 8f, 0f, 4f, 10f)

                POTION.classes = arrayOf(PotionOfHealing::class.java, PotionOfExperience::class.java, PotionOfToxicGas::class.java, PotionOfParalyticGas::class.java, PotionOfLiquidFlame::class.java, PotionOfLevitation::class.java, PotionOfStrength::class.java, PotionOfMindVision::class.java, PotionOfPurity::class.java, PotionOfInvisibility::class.java, PotionOfMight::class.java, PotionOfFrost::class.java)
                POTION.probs = floatArrayOf(45f, 4f, 15f, 10f, 15f, 10f, 0f, 20f, 12f, 10f, 0f, 10f)

                //TODO: add last ones when implemented
                WAND.classes = arrayOf(WandOfMagicMissile::class.java, WandOfLightning::class.java, WandOfDisintegration::class.java, WandOfFireblast::class.java, WandOfCorrosion::class.java, WandOfBlastWave::class.java,
                        //WandOfLivingEarth.class,
                        WandOfFrost::class.java, WandOfPrismaticLight::class.java,
                        //WandOfWarding.class,
                        WandOfTransfusion::class.java, WandOfCorruption::class.java, WandOfRegrowth::class.java)
                WAND.probs = floatArrayOf(5f, 4f, 4f, 4f, 4f, 3f, /*3,*/ 3f, 3f, /*3,*/ 3f, 3f, 3f)

                //see generator.randomWeapon
                WEAPON.classes = arrayOf()
                WEAPON.probs = floatArrayOf()

                WEP_T1.classes = arrayOf(WornShortsword::class.java, Knuckles::class.java, Dagger::class.java, MagesStaff::class.java)
                WEP_T1.probs = floatArrayOf(1f, 1f, 1f, 0f)

                WEP_T2.classes = arrayOf(Shortsword::class.java, HandAxe::class.java, Spear::class.java, Quarterstaff::class.java, Dirk::class.java)
                WEP_T2.probs = floatArrayOf(6f, 5f, 5f, 4f, 4f)

                WEP_T3.classes = arrayOf(Sword::class.java, Mace::class.java, Scimitar::class.java, RoundShield::class.java, Sai::class.java, Whip::class.java)
                WEP_T3.probs = floatArrayOf(6f, 5f, 5f, 4f, 4f, 4f)

                WEP_T4.classes = arrayOf(Longsword::class.java, BattleAxe::class.java, Flail::class.java, RunicBlade::class.java, AssassinsBlade::class.java, Crossbow::class.java)
                WEP_T4.probs = floatArrayOf(6f, 5f, 5f, 4f, 4f, 4f)

                WEP_T5.classes = arrayOf(Greatsword::class.java, WarHammer::class.java, Glaive::class.java, Greataxe::class.java, Greatshield::class.java, Gauntlet::class.java)
                WEP_T5.probs = floatArrayOf(6f, 5f, 5f, 4f, 4f, 4f)

                //see Generator.randomArmor
                ARMOR.classes = arrayOf(ClothArmor::class.java, LeatherArmor::class.java, MailArmor::class.java, ScaleArmor::class.java, PlateArmor::class.java)
                ARMOR.probs = floatArrayOf(0f, 0f, 0f, 0f, 0f)

                //see Generator.randomMissile
                MISSILE.classes = arrayOf()
                MISSILE.probs = floatArrayOf()

                MIS_T1.classes = arrayOf(Dart::class.java, ThrowingKnife::class.java)
                MIS_T1.probs = floatArrayOf(1f, 1f)

                MIS_T2.classes = arrayOf(FishingSpear::class.java, Shuriken::class.java)
                MIS_T2.probs = floatArrayOf(4f, 3f)

                MIS_T3.classes = arrayOf(ThrowingSpear::class.java, Bolas::class.java)
                MIS_T3.probs = floatArrayOf(4f, 3f)

                MIS_T4.classes = arrayOf(Javelin::class.java, Tomahawk::class.java)
                MIS_T4.probs = floatArrayOf(4f, 3f)

                MIS_T5.classes = arrayOf(Trident::class.java, ThrowingHammer::class.java)
                MIS_T5.probs = floatArrayOf(4f, 3f)

                FOOD.classes = arrayOf(Food::class.java, Pasty::class.java, MysteryMeat::class.java)
                FOOD.probs = floatArrayOf(4f, 1f, 0f)

                RING.classes = arrayOf(RingOfAccuracy::class.java, RingOfEvasion::class.java, RingOfElements::class.java, RingOfForce::class.java, RingOfFuror::class.java, RingOfHaste::class.java, RingOfEnergy::class.java, RingOfMight::class.java, RingOfSharpshooting::class.java, RingOfTenacity::class.java, RingOfWealth::class.java)
                RING.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)

                ARTIFACT.classes = arrayOf(CapeOfThorns::class.java, ChaliceOfBlood::class.java, CloakOfShadows::class.java, HornOfPlenty::class.java, MasterThievesArmband::class.java, SandalsOfNature::class.java, TalismanOfForesight::class.java, TimekeepersHourglass::class.java, UnstableSpellbook::class.java, AlchemistsToolkit::class.java, //currently removed from drop tables, pending rework.
                        DriedRose::class.java, LloydsBeacon::class.java, EtherealChains::class.java)
                ARTIFACT.probs = INITIAL_ARTIFACT_PROBS.clone()

                SEED.classes = arrayOf(Firebloom.Seed::class.java, Icecap.Seed::class.java, Sorrowmoss.Seed::class.java, Blindweed.Seed::class.java, Sungrass.Seed::class.java, Earthroot.Seed::class.java, Fadeleaf.Seed::class.java, Rotberry.Seed::class.java, BlandfruitBush.Seed::class.java, Dreamfoil.Seed::class.java, Stormvine.Seed::class.java, Starflower.Seed::class.java)
                SEED.probs = floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 0f, 2f, 10f, 10f, 1f)
            }
        }
    }

    fun reset() {
        for (cat in Category.values()) {
            categoryProbs[cat] = cat.prob
        }
    }

    fun random(): Item? {
        var cat = Random.chances(categoryProbs)
        if (cat == null) {
            reset()
            cat = Random.chances(categoryProbs)
        }
        categoryProbs[cat] = categoryProbs[cat] - 1
        return random(cat)
    }

    fun random(cat: Category): Item? {
        try {

            when (cat) {
                Generator.Category.ARMOR -> return randomArmor()
                Generator.Category.WEAPON -> return randomWeapon()
                Generator.Category.MISSILE -> return randomMissile()
                Generator.Category.ARTIFACT -> {
                    val item = randomArtifact()
                    //if we're out of artifacts, return a ring instead.
                    return item ?: random(Category.RING)
                }
                else -> return (cat.classes[Random.chances(cat.probs)].newInstance() as Item).random()
            }

        } catch (e: Exception) {

            ShatteredPixelDungeon.reportException(e)
            return null

        }

    }

    fun random(cl: Class<out Item>): Item? {
        try {

            return (cl.newInstance() as Item).random()

        } catch (e: Exception) {

            ShatteredPixelDungeon.reportException(e)
            return null

        }

    }

    @JvmOverloads
    fun randomArmor(floorSet: Int = Dungeon.depth / 5): Armor? {
        var floorSet = floorSet

        floorSet = GameMath.gate(0f, floorSet.toFloat(), (floorSetTierProbs.size - 1).toFloat()).toInt()

        try {
            val a = Category.ARMOR.classes[Random.chances(floorSetTierProbs[floorSet])].newInstance() as Armor
            a.random()
            return a
        } catch (e: Exception) {
            ShatteredPixelDungeon.reportException(e)
            return null
        }

    }

    @JvmOverloads
    fun randomWeapon(floorSet: Int = Dungeon.depth / 5): MeleeWeapon? {
        var floorSet = floorSet

        floorSet = GameMath.gate(0f, floorSet.toFloat(), (floorSetTierProbs.size - 1).toFloat()).toInt()

        try {
            val c = wepTiers[Random.chances(floorSetTierProbs[floorSet])]
            val w = c.classes[Random.chances(c.probs)].newInstance() as MeleeWeapon
            w.random()
            return w
        } catch (e: Exception) {
            ShatteredPixelDungeon.reportException(e)
            return null
        }

    }

    @JvmOverloads
    fun randomMissile(floorSet: Int = Dungeon.depth / 5): MissileWeapon? {
        var floorSet = floorSet

        floorSet = GameMath.gate(0f, floorSet.toFloat(), (floorSetTierProbs.size - 1).toFloat()).toInt()

        try {
            val c = misTiers[Random.chances(floorSetTierProbs[floorSet])]
            val w = c.classes[Random.chances(c.probs)].newInstance() as MissileWeapon
            w.random()
            return w
        } catch (e: Exception) {
            ShatteredPixelDungeon.reportException(e)
            return null
        }

    }

    //enforces uniqueness of artifacts throughout a run.
    fun randomArtifact(): Artifact? {

        try {
            val cat = Category.ARTIFACT
            val i = Random.chances(cat.probs)

            //if no artifacts are left, return null
            if (i == -1) {
                return null
            }

            val art = cat.classes[i] as Class<out Artifact>

            if (removeArtifact(art)) {
                val artifact = art.newInstance()

                artifact.random()

                return artifact
            } else {
                return null
            }

        } catch (e: Exception) {
            ShatteredPixelDungeon.reportException(e)
            return null
        }

    }

    fun removeArtifact(artifact: Class<out Artifact>): Boolean {
        if (spawnedArtifacts.contains(artifact))
            return false

        val cat = Category.ARTIFACT
        for (i in cat.classes.indices)
            if (cat.classes[i] == artifact) {
                if (cat.probs[i] == 1f) {
                    cat.probs[i] = 0f
                    spawnedArtifacts.add(artifact)
                    return true
                } else
                    return false
            }

        return false
    }

    //resets artifact probabilities, for new dungeons
    fun initArtifacts() {
        Category.ARTIFACT.probs = Category.INITIAL_ARTIFACT_PROBS.clone()
        spawnedArtifacts = ArrayList()
    }

    fun storeInBundle(bundle: Bundle) {
        val genProbs = categoryProbs.values.toTypedArray<Float>()
        val storeProbs = FloatArray(genProbs.size)
        for (i in storeProbs.indices) {
            storeProbs[i] = genProbs[i]
        }
        bundle.put(GENERAL_PROBS, storeProbs)

        bundle.put(SPAWNED_ARTIFACTS, spawnedArtifacts.toTypedArray<Class<*>>())
    }

    fun restoreFromBundle(bundle: Bundle) {
        if (bundle.contains(GENERAL_PROBS)) {
            val probs = bundle.getFloatArray(GENERAL_PROBS)
            for (i in probs!!.indices) {
                categoryProbs[Category.values()[i]] = probs[i]
            }
        } else {
            reset()
        }

        initArtifacts()
        if (bundle.contains(SPAWNED_ARTIFACTS)) {
            for (artifact in bundle.getClassArray(SPAWNED_ARTIFACTS)!!) {
                removeArtifact(artifact)
            }
            //pre-0.6.1 saves
        } else if (bundle.contains("artifacts")) {
            val names = bundle.getStringArray("artifacts")
            val cat = Category.ARTIFACT

            for (artifact in names!!)
                for (i in cat.classes.indices)
                    if (cat.classes[i].simpleName == artifact)
                        cat.probs[i] = 0f
        }
    }
}
