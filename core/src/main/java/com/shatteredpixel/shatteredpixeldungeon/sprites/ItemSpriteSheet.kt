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

package com.shatteredpixel.shatteredpixeldungeon.sprites

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.watabou.noosa.TextureFilm

object ItemSpriteSheet {

    private val WIDTH = 16

    var film = TextureFilm(Assets.ITEMS, 16, 16)

    private val PLACEHOLDERS = xy(1, 1)   //16 slots
    //null warning occupies space 0, should only show up if there's a bug.
    val NULLWARN = PLACEHOLDERS + 0
    val WEAPON_HOLDER = PLACEHOLDERS + 1
    val ARMOR_HOLDER = PLACEHOLDERS + 2
    val WAND_HOLDER = PLACEHOLDERS + 3
    val RING_HOLDER = PLACEHOLDERS + 4
    val ARTIFACT_HOLDER = PLACEHOLDERS + 5
    val POTION_HOLDER = PLACEHOLDERS + 6
    val SCROLL_HOLDER = PLACEHOLDERS + 7
    val SOMETHING = PLACEHOLDERS + 8

    private val UNCOLLECTIBLE = xy(1, 2)   //16 slots
    val GOLD = UNCOLLECTIBLE + 0
    val DEWDROP = UNCOLLECTIBLE + 1
    val PETAL = UNCOLLECTIBLE + 2
    val SANDBAG = UNCOLLECTIBLE + 3
    val DBL_BOMB = UNCOLLECTIBLE + 4
    val GUIDE_PAGE = UNCOLLECTIBLE + 5

    private val CONTAINERS = xy(1, 3)   //16 slots
    val BONES = CONTAINERS + 0
    val REMAINS = CONTAINERS + 1
    val TOMB = CONTAINERS + 2
    val GRAVE = CONTAINERS + 3
    val CHEST = CONTAINERS + 4
    val LOCKED_CHEST = CONTAINERS + 5
    val CRYSTAL_CHEST = CONTAINERS + 6
    val EBONY_CHEST = CONTAINERS + 7

    private val SINGLE_USE = xy(1, 4)   //16 slots
    val ANKH = SINGLE_USE + 0
    val STYLUS = SINGLE_USE + 1
    val WEIGHT = SINGLE_USE + 2
    val SEAL = SINGLE_USE + 3
    val TORCH = SINGLE_USE + 4
    val BEACON = SINGLE_USE + 5
    val BOMB = SINGLE_USE + 6
    val HONEYPOT = SINGLE_USE + 7
    val SHATTPOT = SINGLE_USE + 8
    val IRON_KEY = SINGLE_USE + 9
    val GOLDEN_KEY = SINGLE_USE + 10
    val CRYSTAL_KEY = SINGLE_USE + 11
    val SKELETON_KEY = SINGLE_USE + 12
    val MASTERY = SINGLE_USE + 13
    val KIT = SINGLE_USE + 14
    val AMULET = SINGLE_USE + 15

    //32 free slots

    private val WEP_TIER1 = xy(1, 7)   //8 slots
    val WORN_SHORTSWORD = WEP_TIER1 + 0
    val CUDGEL = WEP_TIER1 + 1
    val KNUCKLEDUSTER = WEP_TIER1 + 2
    val RAPIER = WEP_TIER1 + 3
    val DAGGER = WEP_TIER1 + 4
    val MAGES_STAFF = WEP_TIER1 + 5

    private val WEP_TIER2 = xy(9, 7)   //8 slots
    val SHORTSWORD = WEP_TIER2 + 0
    val HAND_AXE = WEP_TIER2 + 1
    val SPEAR = WEP_TIER2 + 2
    val QUARTERSTAFF = WEP_TIER2 + 3
    val DIRK = WEP_TIER2 + 4

    private val WEP_TIER3 = xy(1, 8)   //8 slots
    val SWORD = WEP_TIER3 + 0
    val MACE = WEP_TIER3 + 1
    val SCIMITAR = WEP_TIER3 + 2
    val ROUND_SHIELD = WEP_TIER3 + 3
    val SAI = WEP_TIER3 + 4
    val WHIP = WEP_TIER3 + 5

    private val WEP_TIER4 = xy(9, 8)   //8 slots
    val LONGSWORD = WEP_TIER4 + 0
    val BATTLE_AXE = WEP_TIER4 + 1
    val FLAIL = WEP_TIER4 + 2
    val RUNIC_BLADE = WEP_TIER4 + 3
    val ASSASSINS_BLADE = WEP_TIER4 + 4
    val CROSSBOW = WEP_TIER4 + 5

    private val WEP_TIER5 = xy(1, 9)   //8 slots
    val GREATSWORD = WEP_TIER5 + 0
    val WAR_HAMMER = WEP_TIER5 + 1
    val GLAIVE = WEP_TIER5 + 2
    val GREATAXE = WEP_TIER5 + 3
    val GREATSHIELD = WEP_TIER5 + 4
    val GAUNTLETS = WEP_TIER5 + 5

    //8 free slots

    private val MISSILE_WEP = xy(1, 10)  //16 slots. 3 per tier + boomerang
    val BOOMERANG = MISSILE_WEP + 0

    val DART = MISSILE_WEP + 1
    val THROWING_KNIFE = MISSILE_WEP + 2
    val THROWING_STONE = MISSILE_WEP + 3

    val FISHING_SPEAR = MISSILE_WEP + 4
    val SHURIKEN = MISSILE_WEP + 5

    val THROWING_SPEAR = MISSILE_WEP + 7
    val BOLAS = MISSILE_WEP + 8

    val JAVELIN = MISSILE_WEP + 10
    val TOMAHAWK = MISSILE_WEP + 11

    val TRIDENT = MISSILE_WEP + 13
    val THROWING_HAMMER = MISSILE_WEP + 14

    val TIPPED_DARTS = xy(1, 11)  //16 slots
    val ROT_DART = TIPPED_DARTS + 0
    val INCENDIARY_DART = TIPPED_DARTS + 1
    val HOLY_DART = TIPPED_DARTS + 2
    val BLINDING_DART = TIPPED_DARTS + 3
    val HEALING_DART = TIPPED_DARTS + 4
    val CHILLING_DART = TIPPED_DARTS + 5
    val SHOCKING_DART = TIPPED_DARTS + 6
    val POISON_DART = TIPPED_DARTS + 7
    val SLEEP_DART = TIPPED_DARTS + 8
    val PARALYTIC_DART = TIPPED_DARTS + 9
    val DISPLACING_DART = TIPPED_DARTS + 10

    private val ARMOR = xy(1, 12)  //16 slots
    val ARMOR_CLOTH = ARMOR + 0
    val ARMOR_LEATHER = ARMOR + 1
    val ARMOR_MAIL = ARMOR + 2
    val ARMOR_SCALE = ARMOR + 3
    val ARMOR_PLATE = ARMOR + 4
    val ARMOR_WARRIOR = ARMOR + 5
    val ARMOR_MAGE = ARMOR + 6
    val ARMOR_ROGUE = ARMOR + 7
    val ARMOR_HUNTRESS = ARMOR + 8

    //16 free slots

    private val WANDS = xy(1, 14)  //16 slots
    val WAND_MAGIC_MISSILE = WANDS + 0
    val WAND_FIREBOLT = WANDS + 1
    val WAND_FROST = WANDS + 2
    val WAND_LIGHTNING = WANDS + 3
    val WAND_DISINTEGRATION = WANDS + 4
    val WAND_PRISMATIC_LIGHT = WANDS + 5
    val WAND_CORROSION = WANDS + 6
    val WAND_LIVING_EARTH = WANDS + 7
    val WAND_BLAST_WAVE = WANDS + 8
    val WAND_CORRUPTION = WANDS + 9
    val WAND_WARDING = WANDS + 10
    val WAND_REGROWTH = WANDS + 11
    val WAND_TRANSFUSION = WANDS + 12

    private val RINGS = xy(1, 15)  //16 slots
    val RING_GARNET = RINGS + 0
    val RING_RUBY = RINGS + 1
    val RING_TOPAZ = RINGS + 2
    val RING_EMERALD = RINGS + 3
    val RING_ONYX = RINGS + 4
    val RING_OPAL = RINGS + 5
    val RING_TOURMALINE = RINGS + 6
    val RING_SAPPHIRE = RINGS + 7
    val RING_AMETHYST = RINGS + 8
    val RING_QUARTZ = RINGS + 9
    val RING_AGATE = RINGS + 10
    val RING_DIAMOND = RINGS + 11

    private val ARTIFACTS = xy(1, 16)  //32 slots
    val ARTIFACT_CLOAK = ARTIFACTS + 0
    val ARTIFACT_ARMBAND = ARTIFACTS + 1
    val ARTIFACT_CAPE = ARTIFACTS + 2
    val ARTIFACT_TALISMAN = ARTIFACTS + 3
    val ARTIFACT_HOURGLASS = ARTIFACTS + 4
    val ARTIFACT_TOOLKIT = ARTIFACTS + 5
    val ARTIFACT_SPELLBOOK = ARTIFACTS + 6
    val ARTIFACT_BEACON = ARTIFACTS + 7
    val ARTIFACT_CHAINS = ARTIFACTS + 8
    val ARTIFACT_HORN1 = ARTIFACTS + 9
    val ARTIFACT_HORN2 = ARTIFACTS + 10
    val ARTIFACT_HORN3 = ARTIFACTS + 11
    val ARTIFACT_HORN4 = ARTIFACTS + 12
    val ARTIFACT_CHALICE1 = ARTIFACTS + 13
    val ARTIFACT_CHALICE2 = ARTIFACTS + 14
    val ARTIFACT_CHALICE3 = ARTIFACTS + 15
    val ARTIFACT_SANDALS = ARTIFACTS + 16
    val ARTIFACT_SHOES = ARTIFACTS + 17
    val ARTIFACT_BOOTS = ARTIFACTS + 18
    val ARTIFACT_GREAVES = ARTIFACTS + 19
    val ARTIFACT_ROSE1 = ARTIFACTS + 20
    val ARTIFACT_ROSE2 = ARTIFACTS + 21
    val ARTIFACT_ROSE3 = ARTIFACTS + 22

    //32 free slots

    private val SCROLLS = xy(1, 20)  //16 slots
    val SCROLL_KAUNAN = SCROLLS + 0
    val SCROLL_SOWILO = SCROLLS + 1
    val SCROLL_LAGUZ = SCROLLS + 2
    val SCROLL_YNGVI = SCROLLS + 3
    val SCROLL_GYFU = SCROLLS + 4
    val SCROLL_RAIDO = SCROLLS + 5
    val SCROLL_ISAZ = SCROLLS + 6
    val SCROLL_MANNAZ = SCROLLS + 7
    val SCROLL_NAUDIZ = SCROLLS + 8
    val SCROLL_BERKANAN = SCROLLS + 9
    val SCROLL_ODAL = SCROLLS + 10
    val SCROLL_TIWAZ = SCROLLS + 11

    private val STONES = xy(1, 21)  //16 slots
    val STONE_KAUNAN = STONES + 0
    val STONE_SOWILO = STONES + 1
    val STONE_LAGUZ = STONES + 2
    val STONE_YNGVI = STONES + 3
    val STONE_GYFU = STONES + 4
    val STONE_RAIDO = STONES + 5
    val STONE_ISAZ = STONES + 6
    val STONE_MANNAZ = STONES + 7
    val STONE_NAUDIZ = STONES + 8
    val STONE_BERKANAN = STONES + 9
    val STONE_ODAL = STONES + 10
    val STONE_TIWAZ = STONES + 11

    private val POTIONS = xy(1, 22)  //16 slots
    val POTION_CRIMSON = POTIONS + 0
    val POTION_AMBER = POTIONS + 1
    val POTION_GOLDEN = POTIONS + 2
    val POTION_JADE = POTIONS + 3
    val POTION_TURQUOISE = POTIONS + 4
    val POTION_AZURE = POTIONS + 5
    val POTION_INDIGO = POTIONS + 6
    val POTION_MAGENTA = POTIONS + 7
    val POTION_BISTRE = POTIONS + 8
    val POTION_CHARCOAL = POTIONS + 9
    val POTION_SILVER = POTIONS + 10
    val POTION_IVORY = POTIONS + 11

    private val SEEDS = xy(1, 23)  //16 slots
    val SEED_ROTBERRY = SEEDS + 0
    val SEED_FIREBLOOM = SEEDS + 1
    val SEED_STARFLOWER = SEEDS + 2
    val SEED_BLINDWEED = SEEDS + 3
    val SEED_SUNGRASS = SEEDS + 4
    val SEED_ICECAP = SEEDS + 5
    val SEED_STORMVINE = SEEDS + 6
    val SEED_SORROWMOSS = SEEDS + 7
    val SEED_DREAMFOIL = SEEDS + 8
    val SEED_EARTHROOT = SEEDS + 9
    val SEED_FADELEAF = SEEDS + 10
    val SEED_BLANDFRUIT = SEEDS + 11

    //16 free slots

    private val FOOD = xy(1, 25)  //16 slots
    val MEAT = FOOD + 0
    val STEAK = FOOD + 1
    val OVERPRICED = FOOD + 2
    val CARPACCIO = FOOD + 3
    val BLANDFRUIT = FOOD + 4
    val RATION = FOOD + 5
    val PASTY = FOOD + 6
    val PUMPKIN_PIE = FOOD + 7
    val CANDY_CANE = FOOD + 8

    private val QUEST = xy(1, 26)  //32 slots
    val SKULL = QUEST + 0
    val DUST = QUEST + 1
    val CANDLE = QUEST + 2
    val EMBER = QUEST + 3
    val PICKAXE = QUEST + 4
    val ORE = QUEST + 5
    val TOKEN = QUEST + 6

    private val BAGS = xy(1, 28)  //16 slots
    val VIAL = BAGS + 0
    val POUCH = BAGS + 1
    val HOLDER = BAGS + 2
    val BANDOLIER = BAGS + 3
    val HOLSTER = BAGS + 4

    private fun xy(x: Int, y: Int): Int {
        var x = x
        var y = y
        x -= 1
        y -= 1
        return x + WIDTH * y
    }

    init {
        assignItemRect(NULLWARN, 16, 7)
        assignItemRect(WEAPON_HOLDER, 14, 14)
        assignItemRect(ARMOR_HOLDER, 14, 12)
        assignItemRect(WAND_HOLDER, 14, 14)
        assignItemRect(RING_HOLDER, 8, 10)
        assignItemRect(ARTIFACT_HOLDER, 15, 15)
        assignItemRect(POTION_HOLDER, 10, 14)
        assignItemRect(SCROLL_HOLDER, 15, 14)
        assignItemRect(SOMETHING, 8, 13)
    }

    init {
        assignItemRect(GOLD, 15, 13)
        assignItemRect(DEWDROP, 10, 10)
        assignItemRect(PETAL, 8, 8)
        assignItemRect(SANDBAG, 10, 10)
        assignItemRect(DBL_BOMB, 14, 13)
        assignItemRect(GUIDE_PAGE, 10, 11)
    }

    init {
        assignItemRect(BONES, 14, 11)
        assignItemRect(REMAINS, 14, 11)
        assignItemRect(TOMB, 14, 15)
        assignItemRect(GRAVE, 14, 15)
        assignItemRect(CHEST, 16, 14)
        assignItemRect(LOCKED_CHEST, 16, 14)
        assignItemRect(CRYSTAL_CHEST, 16, 14)
        assignItemRect(EBONY_CHEST, 16, 14)
    }

    init {
        assignItemRect(ANKH, 10, 16)
        assignItemRect(STYLUS, 12, 13)
        assignItemRect(WEIGHT, 14, 12)
        assignItemRect(SEAL, 9, 15)
        assignItemRect(TORCH, 12, 15)
        assignItemRect(BEACON, 16, 15)
        assignItemRect(BOMB, 10, 13)
        assignItemRect(HONEYPOT, 14, 12)
        assignItemRect(SHATTPOT, 14, 12)
        assignItemRect(IRON_KEY, 8, 14)
        assignItemRect(GOLDEN_KEY, 8, 14)
        assignItemRect(CRYSTAL_KEY, 8, 14)
        assignItemRect(SKELETON_KEY, 8, 14)
        assignItemRect(MASTERY, 13, 16)
        assignItemRect(KIT, 16, 15)
        assignItemRect(AMULET, 16, 16)
    }

    init {
        assignItemRect(WORN_SHORTSWORD, 13, 13)
        assignItemRect(KNUCKLEDUSTER, 15, 10)
        assignItemRect(DAGGER, 12, 13)
        assignItemRect(MAGES_STAFF, 15, 16)
    }

    init {
        assignItemRect(SHORTSWORD, 13, 13)
        assignItemRect(HAND_AXE, 12, 14)
        assignItemRect(SPEAR, 16, 16)
        assignItemRect(QUARTERSTAFF, 16, 16)
        assignItemRect(DIRK, 13, 14)
    }

    init {
        assignItemRect(SWORD, 14, 14)
        assignItemRect(MACE, 15, 15)
        assignItemRect(SCIMITAR, 13, 16)
        assignItemRect(ROUND_SHIELD, 16, 16)
        assignItemRect(SAI, 16, 16)
        assignItemRect(WHIP, 14, 14)
    }

    init {
        assignItemRect(LONGSWORD, 15, 15)
        assignItemRect(BATTLE_AXE, 16, 16)
        assignItemRect(FLAIL, 14, 14)
        assignItemRect(RUNIC_BLADE, 14, 14)
        assignItemRect(ASSASSINS_BLADE, 14, 15)
        assignItemRect(CROSSBOW, 15, 15)
    }

    init {
        assignItemRect(GREATSWORD, 16, 16)
        assignItemRect(WAR_HAMMER, 16, 16)
        assignItemRect(GLAIVE, 16, 16)
        assignItemRect(GREATAXE, 12, 16)
        assignItemRect(GREATSHIELD, 12, 16)
        assignItemRect(GAUNTLETS, 13, 15)
    }

    init {
        assignItemRect(BOOMERANG, 14, 14)

        assignItemRect(DART, 15, 15)
        assignItemRect(THROWING_KNIFE, 12, 13)
        assignItemRect(THROWING_STONE, 9, 9)

        assignItemRect(FISHING_SPEAR, 11, 11)
        assignItemRect(SHURIKEN, 12, 12)

        assignItemRect(THROWING_SPEAR, 13, 13)
        assignItemRect(BOLAS, 15, 14)

        assignItemRect(JAVELIN, 16, 16)
        assignItemRect(TOMAHAWK, 13, 13)

        assignItemRect(TRIDENT, 16, 16)
        assignItemRect(THROWING_HAMMER, 12, 12)
    }

    init {
        for (i in TIPPED_DARTS until TIPPED_DARTS + 16)
            assignItemRect(i, 15, 15)
    }

    init {
        assignItemRect(ARMOR_CLOTH, 15, 12)
        assignItemRect(ARMOR_LEATHER, 14, 13)
        assignItemRect(ARMOR_MAIL, 14, 12)
        assignItemRect(ARMOR_SCALE, 14, 11)
        assignItemRect(ARMOR_PLATE, 12, 12)
        assignItemRect(ARMOR_WARRIOR, 12, 12)
        assignItemRect(ARMOR_MAGE, 15, 15)
        assignItemRect(ARMOR_ROGUE, 14, 12)
        assignItemRect(ARMOR_HUNTRESS, 13, 15)
    }

    init {
        for (i in WANDS until WANDS + 16)
            assignItemRect(i, 14, 14)
    }

    init {
        for (i in RINGS until RINGS + 16)
            assignItemRect(i, 8, 10)
    }

    init {
        assignItemRect(ARTIFACT_CLOAK, 9, 15)
        assignItemRect(ARTIFACT_ARMBAND, 16, 13)
        assignItemRect(ARTIFACT_CAPE, 16, 14)
        assignItemRect(ARTIFACT_TALISMAN, 15, 13)
        assignItemRect(ARTIFACT_HOURGLASS, 13, 16)
        assignItemRect(ARTIFACT_TOOLKIT, 15, 13)
        assignItemRect(ARTIFACT_SPELLBOOK, 13, 16)
        assignItemRect(ARTIFACT_BEACON, 16, 16)
        assignItemRect(ARTIFACT_CHAINS, 16, 16)
        assignItemRect(ARTIFACT_HORN1, 15, 15)
        assignItemRect(ARTIFACT_HORN2, 15, 15)
        assignItemRect(ARTIFACT_HORN3, 15, 15)
        assignItemRect(ARTIFACT_HORN4, 15, 15)
        assignItemRect(ARTIFACT_CHALICE1, 12, 15)
        assignItemRect(ARTIFACT_CHALICE2, 12, 15)
        assignItemRect(ARTIFACT_CHALICE3, 12, 15)
        assignItemRect(ARTIFACT_SANDALS, 16, 6)
        assignItemRect(ARTIFACT_SHOES, 16, 6)
        assignItemRect(ARTIFACT_BOOTS, 16, 9)
        assignItemRect(ARTIFACT_GREAVES, 16, 14)
        assignItemRect(ARTIFACT_ROSE1, 14, 14)
        assignItemRect(ARTIFACT_ROSE2, 14, 14)
        assignItemRect(ARTIFACT_ROSE3, 14, 14)
    }

    init {
        for (i in SCROLLS until SCROLLS + 16)
            assignItemRect(i, 15, 14)
    }

    init {
        for (i in STONES until STONES + 16)
            assignItemRect(i, 11, 11)
    }

    init {
        for (i in POTIONS until POTIONS + 16)
            assignItemRect(i, 10, 14)
    }

    init {
        for (i in SEEDS until SEEDS + 16)
            assignItemRect(i, 10, 10)
    }

    init {
        assignItemRect(MEAT, 15, 11)
        assignItemRect(STEAK, 15, 11)
        assignItemRect(OVERPRICED, 14, 11)
        assignItemRect(CARPACCIO, 15, 11)
        assignItemRect(BLANDFRUIT, 9, 12)
        assignItemRect(RATION, 16, 12)
        assignItemRect(PASTY, 16, 11)
        assignItemRect(PUMPKIN_PIE, 16, 12)
        assignItemRect(CANDY_CANE, 13, 16)
    }

    init {
        assignItemRect(SKULL, 16, 11)
        assignItemRect(DUST, 12, 11)
        assignItemRect(CANDLE, 12, 12)
        assignItemRect(EMBER, 12, 11)
        assignItemRect(PICKAXE, 14, 14)
        assignItemRect(ORE, 15, 15)
        assignItemRect(TOKEN, 12, 12)

    }

    init {
        assignItemRect(VIAL, 12, 12)
        assignItemRect(POUCH, 14, 15)
        assignItemRect(HOLDER, 16, 16)
        assignItemRect(BANDOLIER, 15, 16)
        assignItemRect(HOLSTER, 15, 16)
    }

    //64 free slots


    private fun assignItemRect(item: Int, width: Int, height: Int) {
        val x = item % WIDTH * WIDTH
        val y = item / WIDTH * WIDTH
        film.add(item, x, y, x + width, y + height)
    }

}
