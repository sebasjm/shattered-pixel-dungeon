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

package com.shatteredpixel.shatteredpixeldungeon

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Acidic
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Albino
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bandit
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Senior
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shielded
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Game
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import com.watabou.utils.FileUtils

import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.HashSet

object Badges {

    private var global: HashSet<Badge>? = null
    private var local: HashSet<Badge> = HashSet()

    private var saveNeeded = false

    var loadingListener: Callback? = null

    val BADGES_FILE = "badges.dat"
    private val BADGES = "badges"

    enum class Badge private constructor(var image: Int = -1, var meta: Boolean = false) {
        MONSTERS_SLAIN_1(0),
        MONSTERS_SLAIN_2(1),
        MONSTERS_SLAIN_3(2),
        MONSTERS_SLAIN_4(3),
        GOLD_COLLECTED_1(4),
        GOLD_COLLECTED_2(5),
        GOLD_COLLECTED_3(6),
        GOLD_COLLECTED_4(7),
        LEVEL_REACHED_1(8),
        LEVEL_REACHED_2(9),
        LEVEL_REACHED_3(10),
        LEVEL_REACHED_4(11),
        ALL_WEAPONS_IDENTIFIED(16),
        ALL_ARMOR_IDENTIFIED(17),
        ALL_WANDS_IDENTIFIED(18),
        ALL_RINGS_IDENTIFIED(19),
        ALL_ARTIFACTS_IDENTIFIED(20),
        ALL_POTIONS_IDENTIFIED(21),
        ALL_SCROLLS_IDENTIFIED(22),
        ALL_ITEMS_IDENTIFIED(23, true),
        //these names are a bit outdated, but it doesn't really matter.
        BAG_BOUGHT_SEED_POUCH,
        BAG_BOUGHT_SCROLL_HOLDER,
        BAG_BOUGHT_POTION_BANDOLIER,
        BAG_BOUGHT_WAND_HOLSTER,
        ALL_BAGS_BOUGHT(58),
        DEATH_FROM_FIRE(24),
        DEATH_FROM_POISON(25),
        DEATH_FROM_GAS(26),
        DEATH_FROM_HUNGER(27),
        DEATH_FROM_GLYPH(57),
        DEATH_FROM_FALLING(59),
        YASD(34, true),
        BOSS_SLAIN_1_WARRIOR,
        BOSS_SLAIN_1_MAGE,
        BOSS_SLAIN_1_ROGUE,
        BOSS_SLAIN_1_HUNTRESS,
        BOSS_SLAIN_1(12),
        BOSS_SLAIN_2(13),
        BOSS_SLAIN_3(14),
        BOSS_SLAIN_4(15),
        BOSS_SLAIN_1_ALL_CLASSES(32, true),
        BOSS_SLAIN_3_GLADIATOR,
        BOSS_SLAIN_3_BERSERKER,
        BOSS_SLAIN_3_WARLOCK,
        BOSS_SLAIN_3_BATTLEMAGE,
        BOSS_SLAIN_3_FREERUNNER,
        BOSS_SLAIN_3_ASSASSIN,
        BOSS_SLAIN_3_SNIPER,
        BOSS_SLAIN_3_WARDEN,
        BOSS_SLAIN_3_ALL_SUBCLASSES(33, true),
        STRENGTH_ATTAINED_1(40),
        STRENGTH_ATTAINED_2(41),
        STRENGTH_ATTAINED_3(42),
        STRENGTH_ATTAINED_4(43),
        FOOD_EATEN_1(44),
        FOOD_EATEN_2(45),
        FOOD_EATEN_3(46),
        FOOD_EATEN_4(47),
        MASTERY_WARRIOR,
        MASTERY_MAGE,
        MASTERY_ROGUE,
        MASTERY_HUNTRESS,
        ITEM_LEVEL_1(48),
        ITEM_LEVEL_2(49),
        ITEM_LEVEL_3(50),
        ITEM_LEVEL_4(51),
        RARE_ALBINO,
        RARE_BANDIT,
        RARE_SHIELDED,
        RARE_SENIOR,
        RARE_ACIDIC,
        RARE(37, true),
        TUTORIAL_WARRIOR,
        TUTORIAL_MAGE,
        VICTORY_WARRIOR,
        VICTORY_MAGE,
        VICTORY_ROGUE,
        VICTORY_HUNTRESS,
        VICTORY(31),
        VICTORY_ALL_CLASSES(36, true),
        MASTERY_COMBO(56),
        POTIONS_COOKED_1(52),
        POTIONS_COOKED_2(53),
        POTIONS_COOKED_3(54),
        POTIONS_COOKED_4(55),
        NO_MONSTERS_SLAIN(28),
        GRIM_WEAPON(29),
        PIRANHAS(30),
        GAMES_PLAYED_1(60, true),
        GAMES_PLAYED_2(61, true),
        GAMES_PLAYED_3(62, true),
        GAMES_PLAYED_4(63, true),
        HAPPY_END(38),
        CHAMPION(39, true);

        fun desc(): String {
            return Messages.get(this.javaClass, name)
        }
    }

    fun reset() {
        local.clear()
        loadGlobal()
    }

    private fun restore(bundle: Bundle?): HashSet<Badge> {
        val badges = HashSet<Badge>()
        if (bundle == null) return badges

        val names = bundle.getStringArray(BADGES) ?: return badges

        for (i in names!!.indices) {
            try {
                badges.add(Badge.valueOf(names[i]))
            } catch (e: Exception) {
                Game.reportException(e)
            }

        }

        return badges
    }

    private fun store(bundle: Bundle, badges: HashSet<Badge>) {
        val names = badges.map { it.toString() }.toTypedArray()

        bundle.put(BADGES, names)
    }

    fun loadLocal(bundle: Bundle) {
        local = restore(bundle)
    }

    fun saveLocal(bundle: Bundle) {
        store(bundle, local)
    }

    fun loadGlobal() {
        if (global == null) {
            try {
                val bundle = FileUtils.bundleFromFile(BADGES_FILE)
                global = restore(bundle)

            } catch (e: IOException) {
                global = HashSet()
            }

        }
    }

    fun saveGlobal() {
        if (saveNeeded) {

            val bundle = Bundle()
            store(bundle, global!!)

            try {
                FileUtils.bundleToFile(BADGES_FILE, bundle)
                saveNeeded = false
            } catch (e: IOException) {
                Game.reportException(e)
            }

        }
    }

    fun validateMonstersSlain() {
        var badge: Badge? = null

        if (!local.contains(Badge.MONSTERS_SLAIN_1) && Statistics.enemiesSlain >= 10) {
            badge = Badge.MONSTERS_SLAIN_1
            local.add(badge)
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_2) && Statistics.enemiesSlain >= 50) {
            badge = Badge.MONSTERS_SLAIN_2
            local.add(badge)
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_3) && Statistics.enemiesSlain >= 150) {
            badge = Badge.MONSTERS_SLAIN_3
            local.add(badge)
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_4) && Statistics.enemiesSlain >= 250) {
            badge = Badge.MONSTERS_SLAIN_4
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validateGoldCollected() {
        var badge: Badge? = null

        if (!local.contains(Badge.GOLD_COLLECTED_1) && Statistics.goldCollected >= 100) {
            badge = Badge.GOLD_COLLECTED_1
            local.add(badge)
        }
        if (!local.contains(Badge.GOLD_COLLECTED_2) && Statistics.goldCollected >= 500) {
            badge = Badge.GOLD_COLLECTED_2
            local.add(badge)
        }
        if (!local.contains(Badge.GOLD_COLLECTED_3) && Statistics.goldCollected >= 2500) {
            badge = Badge.GOLD_COLLECTED_3
            local.add(badge)
        }
        if (!local.contains(Badge.GOLD_COLLECTED_4) && Statistics.goldCollected >= 7500) {
            badge = Badge.GOLD_COLLECTED_4
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validateLevelReached() {
        var badge: Badge? = null

        if (!local.contains(Badge.LEVEL_REACHED_1) && Dungeon.hero!!.lvl >= 6) {
            badge = Badge.LEVEL_REACHED_1
            local.add(badge)
        }
        if (!local.contains(Badge.LEVEL_REACHED_2) && Dungeon.hero!!.lvl >= 12) {
            badge = Badge.LEVEL_REACHED_2
            local.add(badge)
        }
        if (!local.contains(Badge.LEVEL_REACHED_3) && Dungeon.hero!!.lvl >= 18) {
            badge = Badge.LEVEL_REACHED_3
            local.add(badge)
        }
        if (!local.contains(Badge.LEVEL_REACHED_4) && Dungeon.hero!!.lvl >= 24) {
            badge = Badge.LEVEL_REACHED_4
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validateStrengthAttained() {
        var badge: Badge? = null

        if (!local.contains(Badge.STRENGTH_ATTAINED_1) && Dungeon.hero!!.STR >= 13) {
            badge = Badge.STRENGTH_ATTAINED_1
            local.add(badge)
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_2) && Dungeon.hero!!.STR >= 15) {
            badge = Badge.STRENGTH_ATTAINED_2
            local.add(badge)
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_3) && Dungeon.hero!!.STR >= 17) {
            badge = Badge.STRENGTH_ATTAINED_3
            local.add(badge)
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_4) && Dungeon.hero!!.STR >= 19) {
            badge = Badge.STRENGTH_ATTAINED_4
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validateFoodEaten() {
        var badge: Badge? = null

        if (!local.contains(Badge.FOOD_EATEN_1) && Statistics.foodEaten >= 10) {
            badge = Badge.FOOD_EATEN_1
            local.add(badge)
        }
        if (!local.contains(Badge.FOOD_EATEN_2) && Statistics.foodEaten >= 20) {
            badge = Badge.FOOD_EATEN_2
            local.add(badge)
        }
        if (!local.contains(Badge.FOOD_EATEN_3) && Statistics.foodEaten >= 30) {
            badge = Badge.FOOD_EATEN_3
            local.add(badge)
        }
        if (!local.contains(Badge.FOOD_EATEN_4) && Statistics.foodEaten >= 40) {
            badge = Badge.FOOD_EATEN_4
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validatePotionsCooked() {
        var badge: Badge? = null

        if (!local.contains(Badge.POTIONS_COOKED_1) && Statistics.potionsCooked >= 3) {
            badge = Badge.POTIONS_COOKED_1
            local.add(badge)
        }
        if (!local.contains(Badge.POTIONS_COOKED_2) && Statistics.potionsCooked >= 6) {
            badge = Badge.POTIONS_COOKED_2
            local.add(badge)
        }
        if (!local.contains(Badge.POTIONS_COOKED_3) && Statistics.potionsCooked >= 9) {
            badge = Badge.POTIONS_COOKED_3
            local.add(badge)
        }
        if (!local.contains(Badge.POTIONS_COOKED_4) && Statistics.potionsCooked >= 12) {
            badge = Badge.POTIONS_COOKED_4
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validatePiranhasKilled() {
        var badge: Badge? = null

        if (!local.contains(Badge.PIRANHAS) && Statistics.piranhasKilled >= 6) {
            badge = Badge.PIRANHAS
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validateItemLevelAquired(item: Item) {

        // This method should be called:
        // 1) When an item is obtained (Item.collect)
        // 2) When an item is upgraded (ScrollOfUpgrade, ScrollOfWeaponUpgrade, ShortSword, WandOfMagicMissile)
        // 3) When an item is identified

        // Note that artifacts should never trigger this badge as they are alternatively upgraded
        if (!item.levelKnown || item is Artifact) {
            return
        }

        var badge: Badge? = null
        if (!local.contains(Badge.ITEM_LEVEL_1) && item.level() >= 3) {
            badge = Badge.ITEM_LEVEL_1
            local.add(badge)
        }
        if (!local.contains(Badge.ITEM_LEVEL_2) && item.level() >= 6) {
            badge = Badge.ITEM_LEVEL_2
            local.add(badge)
        }
        if (!local.contains(Badge.ITEM_LEVEL_3) && item.level() >= 9) {
            badge = Badge.ITEM_LEVEL_3
            local.add(badge)
        }
        if (!local.contains(Badge.ITEM_LEVEL_4) && item.level() >= 12) {
            badge = Badge.ITEM_LEVEL_4
            local.add(badge)
        }

        displayBadge(badge)
    }

    fun validateAllBagsBought(bag: Item) {

        var badge: Badge? = null
        if (bag is VelvetPouch) {
            badge = Badge.BAG_BOUGHT_SEED_POUCH
        } else if (bag is ScrollHolder) {
            badge = Badge.BAG_BOUGHT_SCROLL_HOLDER
        } else if (bag is PotionBandolier) {
            badge = Badge.BAG_BOUGHT_POTION_BANDOLIER
        } else if (bag is MagicalHolster) {
            badge = Badge.BAG_BOUGHT_WAND_HOLSTER
        }

        if (badge != null) {

            local.add(badge)

            if (!local.contains(Badge.ALL_BAGS_BOUGHT) &&
                    local.contains(Badge.BAG_BOUGHT_SEED_POUCH) &&
                    local.contains(Badge.BAG_BOUGHT_SCROLL_HOLDER) &&
                    local.contains(Badge.BAG_BOUGHT_POTION_BANDOLIER) &&
                    local.contains(Badge.BAG_BOUGHT_WAND_HOLSTER)) {

                badge = Badge.ALL_BAGS_BOUGHT
                local.add(badge)
                displayBadge(badge)
            }
        }
    }

    fun validateItemsIdentified() {

        for (cat in Catalog.values()) {
            if (cat.allSeen()) {
                val b = Catalog.catalogBadges[cat]
                if (!global!!.contains(b)) {
                    displayBadge(b)
                }
            }
        }

        if (!global!!.contains(Badge.ALL_ITEMS_IDENTIFIED) &&
                global!!.contains(Badge.ALL_WEAPONS_IDENTIFIED) &&
                global!!.contains(Badge.ALL_ARMOR_IDENTIFIED) &&
                global!!.contains(Badge.ALL_WANDS_IDENTIFIED) &&
                global!!.contains(Badge.ALL_RINGS_IDENTIFIED) &&
                global!!.contains(Badge.ALL_ARTIFACTS_IDENTIFIED) &&
                global!!.contains(Badge.ALL_POTIONS_IDENTIFIED) &&
                global!!.contains(Badge.ALL_SCROLLS_IDENTIFIED)) {

            displayBadge(Badge.ALL_ITEMS_IDENTIFIED)
        }
    }

    fun validateDeathFromFire() {
        val badge = Badge.DEATH_FROM_FIRE
        local.add(badge)
        displayBadge(badge)

        validateYASD()
    }

    fun validateDeathFromPoison() {
        val badge = Badge.DEATH_FROM_POISON
        local.add(badge)
        displayBadge(badge)

        validateYASD()
    }

    fun validateDeathFromGas() {
        val badge = Badge.DEATH_FROM_GAS
        local.add(badge)
        displayBadge(badge)

        validateYASD()
    }

    fun validateDeathFromHunger() {
        val badge = Badge.DEATH_FROM_HUNGER
        local.add(badge)
        displayBadge(badge)

        validateYASD()
    }

    fun validateDeathFromGlyph() {
        val badge = Badge.DEATH_FROM_GLYPH
        local.add(badge)
        displayBadge(badge)

        validateYASD()
    }

    fun validateDeathFromFalling() {
        val badge = Badge.DEATH_FROM_FALLING
        local.add(badge)
        displayBadge(badge)

        validateYASD()
    }

    private fun validateYASD() {
        if (global!!.contains(Badge.DEATH_FROM_FIRE) &&
                global!!.contains(Badge.DEATH_FROM_POISON) &&
                global!!.contains(Badge.DEATH_FROM_GAS) &&
                global!!.contains(Badge.DEATH_FROM_HUNGER) &&
                global!!.contains(Badge.DEATH_FROM_GLYPH) &&
                global!!.contains(Badge.DEATH_FROM_FALLING)) {

            val badge = Badge.YASD
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateBossSlain() {
        var badge: Badge? = null
        when (Dungeon.depth) {
            5 -> badge = Badge.BOSS_SLAIN_1
            10 -> badge = Badge.BOSS_SLAIN_2
            15 -> badge = Badge.BOSS_SLAIN_3
            20 -> badge = Badge.BOSS_SLAIN_4
        }

        if (badge != null) {
            local.add(badge)
            displayBadge(badge)

            if (badge == Badge.BOSS_SLAIN_1) {
                when (Dungeon.hero!!.heroClass) {
                    HeroClass.WARRIOR -> badge = Badge.BOSS_SLAIN_1_WARRIOR
                    HeroClass.MAGE -> badge = Badge.BOSS_SLAIN_1_MAGE
                    HeroClass.ROGUE -> badge = Badge.BOSS_SLAIN_1_ROGUE
                    HeroClass.HUNTRESS -> badge = Badge.BOSS_SLAIN_1_HUNTRESS
                }
                local.add(badge)
                if (!global!!.contains(badge)) {
                    global!!.add(badge)
                    saveNeeded = true
                }

                if (global!!.contains(Badge.BOSS_SLAIN_1_WARRIOR) &&
                        global!!.contains(Badge.BOSS_SLAIN_1_MAGE) &&
                        global!!.contains(Badge.BOSS_SLAIN_1_ROGUE) &&
                        global!!.contains(Badge.BOSS_SLAIN_1_HUNTRESS)) {

                    badge = Badge.BOSS_SLAIN_1_ALL_CLASSES
                    if (!global!!.contains(badge)) {
                        displayBadge(badge)
                        global!!.add(badge)
                        saveNeeded = true
                    }
                }
            } else if (badge == Badge.BOSS_SLAIN_3) {
                when (Dungeon.hero!!.subClass) {
                    HeroSubClass.GLADIATOR -> badge = Badge.BOSS_SLAIN_3_GLADIATOR
                    HeroSubClass.BERSERKER -> badge = Badge.BOSS_SLAIN_3_BERSERKER
                    HeroSubClass.WARLOCK -> badge = Badge.BOSS_SLAIN_3_WARLOCK
                    HeroSubClass.BATTLEMAGE -> badge = Badge.BOSS_SLAIN_3_BATTLEMAGE
                    HeroSubClass.FREERUNNER -> badge = Badge.BOSS_SLAIN_3_FREERUNNER
                    HeroSubClass.ASSASSIN -> badge = Badge.BOSS_SLAIN_3_ASSASSIN
                    HeroSubClass.SNIPER -> badge = Badge.BOSS_SLAIN_3_SNIPER
                    HeroSubClass.WARDEN -> badge = Badge.BOSS_SLAIN_3_WARDEN
                    else -> return
                }
                local.add(badge)
                if (!global!!.contains(badge)) {
                    global!!.add(badge)
                    saveNeeded = true
                }

                if (global!!.contains(Badge.BOSS_SLAIN_3_GLADIATOR) &&
                        global!!.contains(Badge.BOSS_SLAIN_3_BERSERKER) &&
                        global!!.contains(Badge.BOSS_SLAIN_3_WARLOCK) &&
                        global!!.contains(Badge.BOSS_SLAIN_3_BATTLEMAGE) &&
                        global!!.contains(Badge.BOSS_SLAIN_3_FREERUNNER) &&
                        global!!.contains(Badge.BOSS_SLAIN_3_ASSASSIN) &&
                        global!!.contains(Badge.BOSS_SLAIN_3_SNIPER) &&
                        global!!.contains(Badge.BOSS_SLAIN_3_WARDEN)) {

                    badge = Badge.BOSS_SLAIN_3_ALL_SUBCLASSES
                    if (!global!!.contains(badge)) {
                        displayBadge(badge)
                        global!!.add(badge)
                        saveNeeded = true
                    }
                }
            }
        }
    }

    fun validateMastery() {

        var badge: Badge? = null
        when (Dungeon.hero!!.heroClass) {
            HeroClass.WARRIOR -> badge = Badge.MASTERY_WARRIOR
            HeroClass.MAGE -> badge = Badge.MASTERY_MAGE
            HeroClass.ROGUE -> badge = Badge.MASTERY_ROGUE
            HeroClass.HUNTRESS -> badge = Badge.MASTERY_HUNTRESS
        }

        if (!global!!.contains(badge)) {
            global!!.add(badge)
            saveNeeded = true
        }
    }

    fun validateMasteryCombo(n: Int) {
        if (!local.contains(Badge.MASTERY_COMBO) && n == 10) {
            val badge = Badge.MASTERY_COMBO
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateRare(mob: Mob) {

        var badge: Badge? = null
        if (mob is Albino) {
            badge = Badge.RARE_ALBINO
        } else if (mob is Bandit) {
            badge = Badge.RARE_BANDIT
        } else if (mob is Shielded) {
            badge = Badge.RARE_SHIELDED
        } else if (mob is Senior) {
            badge = Badge.RARE_SENIOR
        } else if (mob is Acidic) {
            badge = Badge.RARE_ACIDIC
        }
        if (!global!!.contains(badge)) {
            global!!.add(badge!!)
            saveNeeded = true
        }

        if (global!!.contains(Badge.RARE_ALBINO) &&
                global!!.contains(Badge.RARE_BANDIT) &&
                global!!.contains(Badge.RARE_SHIELDED) &&
                global!!.contains(Badge.RARE_SENIOR) &&
                global!!.contains(Badge.RARE_ACIDIC)) {

            badge = Badge.RARE
            displayBadge(badge)
        }
    }

    fun validateVictory() {

        var badge = Badge.VICTORY
        displayBadge(badge)

        when (Dungeon.hero!!.heroClass) {
            HeroClass.WARRIOR -> badge = Badge.VICTORY_WARRIOR
            HeroClass.MAGE -> badge = Badge.VICTORY_MAGE
            HeroClass.ROGUE -> badge = Badge.VICTORY_ROGUE
            HeroClass.HUNTRESS -> badge = Badge.VICTORY_HUNTRESS
        }
        local.add(badge)
        if (!global!!.contains(badge)) {
            global!!.add(badge)
            saveNeeded = true
        }

        if (global!!.contains(Badge.VICTORY_WARRIOR) &&
                global!!.contains(Badge.VICTORY_MAGE) &&
                global!!.contains(Badge.VICTORY_ROGUE) &&
                global!!.contains(Badge.VICTORY_HUNTRESS)) {

            badge = Badge.VICTORY_ALL_CLASSES
            displayBadge(badge)
        }
    }

    fun validateTutorial() {
        var badge: Badge? = null
        when (Dungeon.hero!!.heroClass) {
            HeroClass.WARRIOR -> badge = Badge.TUTORIAL_WARRIOR
            HeroClass.MAGE -> badge = Badge.TUTORIAL_MAGE
            else -> {
            }
        }

        if (badge != null) {
            local.add(badge)
            if (!global!!.contains(badge)) {
                global!!.add(badge)
                saveNeeded = true
            }
        }
    }

    fun validateNoKilling() {
        if (!local.contains(Badge.NO_MONSTERS_SLAIN) && Statistics.completedWithNoKilling) {
            val badge = Badge.NO_MONSTERS_SLAIN
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateGrimWeapon() {
        if (!local.contains(Badge.GRIM_WEAPON)) {
            val badge = Badge.GRIM_WEAPON
            local.add(badge)
            displayBadge(badge)
        }
    }

    fun validateGamesPlayed() {
        var badge: Badge? = null
        if (Rankings.INSTANCE.totalNumber >= 10) {
            badge = Badge.GAMES_PLAYED_1
        }
        if (Rankings.INSTANCE.totalNumber >= 50) {
            badge = Badge.GAMES_PLAYED_2
        }
        if (Rankings.INSTANCE.totalNumber >= 250) {
            badge = Badge.GAMES_PLAYED_3
        }
        if (Rankings.INSTANCE.totalNumber >= 1000) {
            badge = Badge.GAMES_PLAYED_4
        }

        displayBadge(badge)
    }

    fun validateHappyEnd() {
        displayBadge(Badge.HAPPY_END)
    }

    fun validateChampion() {
        displayBadge(Badge.CHAMPION)
    }

    private fun displayBadge(badge: Badge?) {

        if (badge == null) {
            return
        }

        if (global!!.contains(badge)) {

            if (!badge.meta) {
                GLog.h(Messages.get(Badges::class.java, "endorsed", badge.desc()))
            }

        } else {

            global!!.add(badge)
            saveNeeded = true

            if (badge.meta) {
                GLog.h(Messages.get(Badges::class.java, "new_super", badge.desc()))
            } else {
                GLog.h(Messages.get(Badges::class.java, "new", badge.desc()))
            }
            PixelScene.showBadge(badge)
        }
    }

    fun isUnlocked(badge: Badge): Boolean {
        return global!!.contains(badge)
    }

    fun disown(badge: Badge) {
        loadGlobal()
        global!!.remove(badge)
        saveNeeded = true
    }

    fun filtered(global: Boolean): List<Badge> {

        val filtered = HashSet(if (global) Badges.global else Badges.local)

        val iterator = filtered.iterator()
        while (iterator.hasNext()) {
            val badge = iterator.next()
            if (!global && badge.meta || badge.image == -1) {
                iterator.remove()
            }
        }

        leaveBest(filtered, Badge.MONSTERS_SLAIN_1, Badge.MONSTERS_SLAIN_2, Badge.MONSTERS_SLAIN_3, Badge.MONSTERS_SLAIN_4)
        leaveBest(filtered, Badge.GOLD_COLLECTED_1, Badge.GOLD_COLLECTED_2, Badge.GOLD_COLLECTED_3, Badge.GOLD_COLLECTED_4)
        leaveBest(filtered, Badge.BOSS_SLAIN_1, Badge.BOSS_SLAIN_2, Badge.BOSS_SLAIN_3, Badge.BOSS_SLAIN_4)
        leaveBest(filtered, Badge.LEVEL_REACHED_1, Badge.LEVEL_REACHED_2, Badge.LEVEL_REACHED_3, Badge.LEVEL_REACHED_4)
        leaveBest(filtered, Badge.STRENGTH_ATTAINED_1, Badge.STRENGTH_ATTAINED_2, Badge.STRENGTH_ATTAINED_3, Badge.STRENGTH_ATTAINED_4)
        leaveBest(filtered, Badge.FOOD_EATEN_1, Badge.FOOD_EATEN_2, Badge.FOOD_EATEN_3, Badge.FOOD_EATEN_4)
        leaveBest(filtered, Badge.ITEM_LEVEL_1, Badge.ITEM_LEVEL_2, Badge.ITEM_LEVEL_3, Badge.ITEM_LEVEL_4)
        leaveBest(filtered, Badge.POTIONS_COOKED_1, Badge.POTIONS_COOKED_2, Badge.POTIONS_COOKED_3, Badge.POTIONS_COOKED_4)
        leaveBest(filtered, Badge.DEATH_FROM_FIRE, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_GAS, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_HUNGER, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_POISON, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_GLYPH, Badge.YASD)
        leaveBest(filtered, Badge.DEATH_FROM_FALLING, Badge.YASD)
        leaveBest(filtered, Badge.ALL_WEAPONS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_ARMOR_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_WANDS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_RINGS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_ARTIFACTS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_POTIONS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.ALL_SCROLLS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED)
        leaveBest(filtered, Badge.GAMES_PLAYED_1, Badge.GAMES_PLAYED_2, Badge.GAMES_PLAYED_3, Badge.GAMES_PLAYED_4)

        val list = ArrayList(filtered)
        Collections.sort(list)

        return list
    }

    private fun leaveBest(list: HashSet<Badge>, vararg badges: Badge) {
        for (i in badges.size - 1 downTo 1) {
            if (list.contains(badges[i])) {
                for (j in 0 until i) {
                    list.remove(badges[j])
                }
                break
            }
        }
    }
}
