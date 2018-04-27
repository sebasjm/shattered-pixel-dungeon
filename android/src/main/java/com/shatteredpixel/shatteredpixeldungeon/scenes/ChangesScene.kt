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

package com.shatteredpixel.shatteredpixeldungeon.scenes

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh
import com.shatteredpixel.shatteredpixeldungeon.items.DewVial
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus
import com.shatteredpixel.shatteredpixeldungeon.items.Torch
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage
import com.watabou.input.Touchscreen
import com.watabou.noosa.*
import com.watabou.noosa.ui.Component

import java.util.ArrayList

//TODO: update this class with relevant info as new versions come out.
class ChangesScene : PixelScene() {

    private val infos = ArrayList<ChangeInfo>()

    override fun create() {
        super.create()

        val w = Camera.main!!.width
        val h = Camera.main!!.height

        val title = PixelScene.renderText(Messages.get(this.javaClass, "title"), 9)
        title.hardlight(Window.TITLE_COLOR)
        title.x = (w - title.width()) / 2f
        title.y = (16 - title.baseLine()) / 2f
        PixelScene.align(title)
        add(title)

        val btnExit = ExitButton()
        btnExit.setPos(Camera.main!!.width - btnExit.width(), 0f)
        add(btnExit)

        val panel = Chrome.get(Chrome.Type.TOAST)

        val pw = 135 + panel!!.marginLeft() + panel.marginRight() - 2
        val ph = h - 16

        panel.size(pw.toFloat(), ph.toFloat())
        panel.x = (w - pw) / 2f
        panel.y = title.y + title.height()
        PixelScene.align(panel)
        add(panel)

        val list = object : ScrollPane(Component()) {

            override fun onClick(x: Float, y: Float) {
                for (info in infos) {
                    if (info.onClick(x, y)) {
                        return
                    }
                }
            }

        }
        add(list)

        //**********************
        //       v0.6.4
        //**********************

        var changes = ChangeInfo("v0.6.4a", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ The dwarf king is now immune to blindness\n" +
                        "\n" +
                        "_-_ Made adjustments to sending gameplay data. Data use should be slightly reduced."))

        changes.addButton(ChangeButton(Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(this.javaClass, "bugfixes"),
                "Fixed (caused by 0.6.4):\n" +
                        "_-_ Various bugs caused by new loading animation\n" +
                        "_-_ Unique items being lost on ankh revive\n" +
                        "_-_ Various issues with tipped darts and tengu\n" +
                        "\n" +
                        "Fixed (existed before 0.6.4):\n" +
                        "_-_ Rare cases where music wouldn't play\n" +
                        "_-_ Unstable enchant not being able to activate venom"))

        changes.addButton(ChangeButton(Icons.get(Icons.LANGS), Messages.get(this.javaClass, "language"),
                "Updated Translations"))

        changes = ChangeInfo("v0.6.4", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo(Messages.get(this.javaClass, "new"), false, null)
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released April 1st, 2018\n" +
                        "_-_ 46 days after Shattered v0.6.3\n" +
                        "\n" +
                        "Commentary will be added here when this update is older."))

        changes.addButton(ChangeButton(Icons.get(Icons.CHALLENGE_ON), "Challenges",
                "Challenges have received several major changes, with the goal of making them more fair and interesting.\n" +
                        "\n" +
                        "_-_ Challenges now have descriptions\n" +
                        "\n" +
                        "_-_ On diet now provides small rations, rather than removing all food\n" +
                        "_-_ Cloth armor is now allowed on faith is my armor\n" +
                        "_-_ Pharmacophobia is unchanged\n" +
                        "_-_ Barren land now allows seeds to drop, but they cannot be planted\n" +
                        "_-_ Swarm intelligence now draws nearby enemies, not all enemies\n" +
                        "_-_ Into darkness now limits light more harshly, but provides torches\n" +
                        "_-_ Forbidden runes now removes 50% of upgrade scrolls, and no other scrolls"))

        changes.addButton(ChangeButton(Icons.get(Icons.INFO), "Start game UI",
                "The interface for starting and loading a game has been completely overhauled!\n" +
                        "\n" +
                        "_-_ Game now supports 4 save slots of any hero class, rather than 1 slot per class\n" +
                        "_-_ Hero select and challenge select are now more streamlined and informative\n" +
                        "_-_ Hero select is now a window, offering more flexibility of where games can be started\n" +
                        "_-_ More details are now shown for games in progress before they are loaded"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.CROSSBOW, null), "New Weapons",
                "Three new weapons have been added!\n" +
                        "\n" +
                        "Throwing spears are a basic tier 3 missile weapon, fishing spears have been reduced to tier 2. Tiers 2-5 now each have a basic missile weapon.\n" +
                        "\n" +
                        "The crossbow is a tier 4 melee weapon which enhances darts! You'll do less damage up-front, but thrown darts will pack a punch!\n" +
                        "\n" +
                        "The gauntlet is a tier 5 fast melee weapon, similar to the sai. Excellent for chaining combos or enchantments."))

        changes = ChangeInfo(Messages.get(this.javaClass, "changes"), false, null)
        changes.hardlight(CharSprite.WARNING)
        infos.add(changes)

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.HOLSTER, null), "Inventory changes",
                "Since the ranged weapon improvements in 0.6.3, inventory space has become a bit too congested. Rather than make a small change that only helps the issue for a few more updates, I have decided to make a larger-scale adjustment to available inventory space:\n" +
                        "\n" +
                        "_-_ The wand holster is now the magical holster, and can store missile weapons as well as wands.\n" +
                        "\n" +
                        "_-_ The seed pouch is now the velvet pouch, and can store runestones as well as seeds.\n" +
                        "\n" +
                        "_-_ Every hero now starts the game with an extra container."))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ It is now possible to back up game data using ADB backup on android 4.0+ and android auto-backup on android 6.0+. Runs in progress are not backed up to prevent cheating.\n" +
                        "\n" +
                        "_-_ Firebloom plants will no longer appear in garden rooms. Accidentally running into them is massively more harmful than any other plant.\n" +
                        "\n" +
                        "_-_ Mage and Warrior tutorial functionality has been removed, as more players found it confusing than helpful.\n" +
                        "\n" +
                        "_-_ Added a new visual effect to the loading screen\n" +
                        "\n" +
                        "_-_ Piranha treasure rooms now have a one tile wide buffer\n" +
                        "\n" +
                        "_-_ Bags are now unsellable"))

        changes.addButton(ChangeButton(Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(this.javaClass, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Crashes involving corrupted mimics\n" +
                        "_-_ Various rare crash bugs\n" +
                        "_-_ Various minor visual bugs\n" +
                        "_-_ Skeletons exploding when falling in chasms\n" +
                        "_-_ Thrown weapons lost when used on sheep\n" +
                        "_-_ Warden gaining benefits from rotberry bush"))

        changes.addButton(ChangeButton(Icons.get(Icons.LANGS), Messages.get(this.javaClass, "language"),
                "Updated Translations"))

        changes = ChangeInfo(Messages.get(this.javaClass, "buffs"), false, null)
        changes.hardlight(CharSprite.POSITIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(TimekeepersHourglass(),
                "The timekeeper's hourglass has been adjusted to cap at 10 charges, instead of 20, and to have a bit more power without upgrades:\n" +
                        "\n" +
                        "_-_ Number of charges halved\n" +
                        "_-_ 2x freeze duration per charge\n" +
                        "_-_ 5x stasis duration per charge\n" +
                        "_-_ Overall recharge speed increased at +0, unchanged at +10"))

        changes.addButton(ChangeButton(TalismanOfForesight(),
                "The talisman of foresight now builds power for scrying slightly faster\n" +
                        "\n" +
                        "_-_ Charge speed increased by 20% at +0, scaling to 50% increased at +10\n" +
                        "_-_ Charge gain when discovering traps unchanged"))

        changes = ChangeInfo(Messages.get(this.javaClass, "nerfs"), false, null)
        changes.hardlight(CharSprite.NEGATIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(Image(Assets.BUFFS_LARGE, 64, 0, 16, 16), "Paralysis changes",
                "Paralysis is an extremely powerful debuff, and its ability to completely immobilize the player or an enemy while they are killed needs to be adjusted.\n" +
                        "\n" +
                        "Chance to resist paralysis is now based on all recent damage taken while paralyzed, instead of each specific instance of damage separately.\n" +
                        "\n" +
                        "This means that after taking around half current HP in damage, breaking from paralysis becomes very likely, and immediately re-applying paralysis will not reset this resist chance."))

        changes.addButton(ChangeButton(Image(Assets.TILES_SEWERS, 48, 48, 16, 16), "Chasm changes",
                "Dropping enemies into chasms is a very fun way to deal with enemies, but killing an enemy instantly and getting almost the full reward is simply too strong. This change should keep killing via chasms fun and useful, without it being as strong.\n" +
                        "\n" +
                        "_-_ Enemies killed via chasms now only award 50% exp"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.SEED_SUNGRASS, null), "Seed adjustments",
                "Sungrass is almost as effective as a potion of healing when used properly, which is extremely strong for a seed. I am increasing the time it takes to heal, so that hunger and combat while waiting can add some cost to the otherwise very powerful healing sungrass provides.\n" +
                        "\n" +
                        "_-_ Sungrass now grants healing significantly more slowly, scaling to ~40% speed at high levels\n" +
                        "_-_ Taking damage no longer reduces sungrass healing\n" +
                        "_-_ Sungrass healing no longer dissapears at full HP\n" +
                        "\n" +
                        "Earthroot is also problematic, as its 50% damage resist makes it an extremely potent tool against bosses, yet not so useful against regular enemies. My hope is that this change levels its power out over both situations.\n" +
                        "\n" +
                        "_-_ Earthroot now blocks up to a certain amount of damage, based on depth, rather than 50% damage"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.POTION_CRIMSON, null), PotionOfHealing().trueName(),
                "Heal potion drops have had their RNG bounded in shattered for a long time, but this bound was always fairly lax. This meant that people who wanted to slowly farm for potions could still amass large numbers of them. I have decided to reign this in more harshly.\n" +
                        "\n" +
                        "_-_ Health potion drops now lower in probability more quickly after potions have already been dropped from a given enemy type\n" +
                        "\n" +
                        "This change should not seriously affect the average player, but does make hoarding health potions much less feasible."))

        //**********************
        //       v0.6.3
        //**********************

        changes = ChangeInfo("v0.6.3", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo(Messages.get(this.javaClass, "new"), false, null)
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released February 14th, 2018\n" +
                        "_-_ 113 days after Shattered v0.6.2\n" +
                        "\n" +
                        "Commentary will be added here when this update is older."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.TRIDENT, null), "Ranged Weapons Overhaul!",
                "Ranged weapons have been completely overhauled!\n\n" +
                        "_-_ Quantity of ranged weapons decreased, however most ranged weapons now last for several uses before breaking.\n\n" +
                        "_-_ Ranged weapon effectiveness increased significantly.\n\n" +
                        "_-_ Ranged weapons are now dropped in more situations, and do not replace melee weapons.\n\n" +
                        "_-_ Existing ranged weapons reworked, 5 new ranged weapons added.\n\n" +
                        "_-_ Warrior now starts with throwing stones, rogue starts with throwing knives"))

        changes.addButton(ChangeButton(Image(Assets.HUNTRESS, 0, 15, 12, 15), "Huntress",
                "Huntress adjusted due to ranged weapon changes (note that this is not a full class rework):\n\n" +
                        "_-_ Huntress no longer has a chance to reclaim a single ranged weapon.\n\n" +
                        "_-_ Missile weapons now have 50% greater durability when used by the huntress.\n\n" +
                        "_-_ Boomerang dmg increased to 1-6 from 1-5\n" +
                        "_-_ Boomerang str req reduced to 9 from 10\n" +
                        "_-_ Knuckleduster dmg reduced to 1-5 from 1-6"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.CHILLING_DART, null), "Expanded Alchemy",
                "It is now possible to use alchemy to tip darts!\n\n" +
                        "_-_ Every seed (except blandfruit) can now be combined with two darts to make two tipped darts.\n\n" +
                        "_-_ Tipped dart effects are similar to their potion/seed counterparts.\n\n" +
                        "_-_ Curare darts are now paralytic darts, and paralyze for 5 turns, up from 3\n\n" +
                        "_-_ Alchemy interface now features a recipes button to show you what you can create."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_TOPAZ, null), Messages.get(RingOfSharpshooting::class.java, "name"),
                "Ring of Sharpshooting overhauled\n\n" +
                        "_-_ No longer grants bonus accuracy\n\n" +
                        "_-_ Now increases ranged weapon durability, instead of giving a chance to not consume them\n\n" +
                        "_-_ Now increases ranged weapon damage, scaling based on the weapon's initial damage."))

        changes = ChangeInfo(Messages.get(this.javaClass, "changes"), false, null)
        changes.hardlight(CharSprite.WARNING)
        infos.add(changes)

        changes.addButton(ChangeButton(Image(Assets.BUFFS_LARGE, 32, 0, 16, 16), "Changes to debuffs and resistances",
                "The game's resistance system has been totally overhauled, to allow for more flexibility and consistency.\n\n" +
                        "Previously, if a character was resistant to something, its effect would be reduced by a random amount between 0% and 100%.\n\n" +
                        "Now, resistances are much less random, applying a specific reduction to harmful effects. Currently all resistances are 50%.\n\n" +
                        "Resistances are also now much more consistent between different creatures. e.g. all non-organic enemies are now immune to bleed.\n\n" +
                        "A few things have been adjusted due to this:\n\n" +
                        "_-_ The Rotting Fist is now immune to paralysis.\n" +
                        "_-_ Psionic blast now deals 100% of current HP, instead of 100% of max HP.\n" +
                        "_-_ Damage from fire now scales with max HP, and is slightly lower below 40 max HP."))

        changes.addButton(ChangeButton(WandOfCorrosion(),
                "Wand of venom is now wand of corrosion. This is primarily a visual rework, with only some changes to functionality:\n\n" +
                        "_-_ Wand now shoots bolts of caustic gas, instead of venom gas\n" +
                        "_-_ Venom debuff is now corrosion debuff, functionality unchanged\n\n" +
                        "_-_ Battlemage now inflicts ooze with a staff of corrosion, instead of poison."))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Performance improvements to the fog of war & mind vision.\n\n" +
                        "_-_ Improved the consistency of how ranged traps pick targets.\n\n" +
                        "_-_ Weapons and armor can now be found upgraded and cursed. Overall curse chance unchanged.\n\n" +
                        "_-_ Each shop now always stocks 2 random tipped darts\n\n" +
                        "_-_ Starting weapons can no longer appear in hero's remains\n\n" +
                        "_-_ The ghost hero is no longer unaffected by all buffs, and is also immune to corruption"))

        changes.addButton(ChangeButton(Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(this.javaClass, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Various crash bugs\n" +
                        "_-_ Serious memory leaks on android 8.0+\n" +
                        "_-_ Rankings not retaining challenges completed\n" +
                        "_-_ Scroll of psionic blast debuffing a dead hero\n" +
                        "_-_ Rot lashers not being considered minibosses\n" +
                        "_-_ Wand of corruption ignoring NPCs\n" +
                        "_-_ NPCs being valid targets for assassin\n" +
                        "_-_ Wand of frost battlemage effect not activating as often as it should.\n" +
                        "_-_ Items in the alchemy window rarely being lost\n" +
                        "_-_ Various minor visual bugs"))

        changes.addButton(ChangeButton(Icons.get(Icons.LANGS), Messages.get(this.javaClass, "language"),
                "In English:\n" +
                        "_-_ Fixed inconsistent text when equipping cursed artifacts\n\n" +
                        "Updated Translations"))

        changes = ChangeInfo(Messages.get(this.javaClass, "buffs"), false, null)
        changes.hardlight(CharSprite.POSITIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_EMERALD, null), Messages.get(RingOfElements::class.java, "name"),
                "Thanks to the increased flexibility of the improved resistance system, buffing the ring of elements is now possible!\n\n" +
                        "_-_ Now reduces the duration and damage of harmful effects significantly more at higher levels.\n\n" +
                        "_-_ Rather than granting a chance to resist elemental/magic damage, ring now grants a set percentage resistance to these effects, which increases each level.\n\n" +
                        "_-_ Ring now applies to more elemental/magical effects than before."))

        changes.addButton(ChangeButton(Image(Assets.MAGE, 0, 90, 12, 15), "Warlock",
                "The warlock is underperforming relative to the battlemage at the moment, and so he is getting an adjustment to his ability.\n\n" +
                        "This should hopefully both increase his power, and further encourage investing upgrades in wands.\n\n" +
                        "_-_ Reduced the base soul mark chance by 40%\n" +
                        "_-_ Increased soul mark chance scaling by 100%\n\n" +
                        "Soul mark chance reaches pre-adjustment levels at a +2 wand, and grows from there."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE, null), "Minor Wand buffs",
                "Wand of Corruption:\n" +
                        "_-_ Reduced the corruption resistance of wraiths by ~40%\n" +
                        "_-_ Enemies now drop their loot (including ranged weapons) when corrupted.\n" +
                        "_-_ If an enemy is immune to a particular debuff, corruption will now try to give a different debuff, instead of doing nothing.\n\n" +
                        "Wand of Corrosion:\n" +
                        "_-_ Corrosion damage growth will continue at 1/2 speed when the damage cap is reached, rather than stopping completely."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.FLAIL, null), "Weapon and Glyph buffs",
                "Weapons with non-standard accuracy are generally weak, so they have been buffed across the board:\n\n" +
                        "_-_ Flail accuracy penalty reduced by 10%\n" +
                        "_-_ Handaxe accuracy bonus increased by 9.5%\n" +
                        "_-_ Mace accuracy bonus increased by 8%\n" +
                        "_-_ BattleAxe accuracy bonus increased by 6.5%\n" +
                        "_-_ WarHammer accuracy bonus increased by 5%\n\n" +
                        "Glyph Buffs:\n" +
                        "_-_ Glyph of obfuscation no longer reduces damage blocking, but is also less powerful.\n" +
                        "_-_ Glyph of entanglement now gives more herbal armor, and root duration decreases at higher armor levels."))

        changes = ChangeInfo(Messages.get(this.javaClass, "nerfs"), false, null)
        changes.hardlight(CharSprite.NEGATIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(Image(Assets.WARRIOR, 0, 90, 12, 15), "Berserker",
                "The previous berserker nerf from 0.6.2 had little effect on his overall winrate, so I'm trying again with a different approach, based around having a permanent penalty for each use of berserk.\n\n" +
                        "_-_ Reverted exhaustion nerf from 0.6.2\n\n" +
                        "_-_ Decreased lvls to recover rage to 2 from 3\n" +
                        "_-_ Berserking now reduces max health by 20%"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_ONYX, null), RingOfEvasion().trueName(),
                "The ring of evasion has always been a very powerful ring, but the recent freerunner rework has increased the power of evasiveness in general, making the ring overbearingly strong.\n\n" +
                        "Evasion synergy has been adjusted:\n" +
                        "_-_ Ring of evasion no longer synergizes as strongly with freerunner or armor of swiftness.\n" +
                        "_-_ Previously their affects would multiply together, they now add to eachother instead.\n\n" +
                        "And the ring itself has been nerf/simplified:\n" +
                        "_-_ Ring of evasion no longer grants stealth"))

        //**********************
        //       v0.6.2
        //**********************

        changes = ChangeInfo("v0.6.2", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo(Messages.get(this.javaClass, "new"), false, null)
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released October 24th, 2017\n" +
                        "_-_ 70 days after Shattered v0.6.1\n" +
                        "\n" +
                        "Commentary will be added here when this update is older."))

        changes.addButton(ChangeButton(Icons.get(Icons.DEPTH), "Dungeon Secrets!",
                "The secrets of the dungeon have been totally redesigned!\n\n" +
                        "_-_ Regular rooms can no longer be totally hidden\n\n" +
                        "_-_ 12 new secret rooms added, which are always hidden\n\n" +
                        "_-_ Hidden doors are now harder to find automatically\n\n" +
                        "_-_ Searching now consumes 6 turns of hunger, up from 2.\n\n" +
                        "This is a big adjustment to how secrets work in the dungeon. The goal is to make secrets more interesting, harder to find, and also more optional."))

        changes.addButton(ChangeButton(Image(Assets.ROGUE, 0, 15, 12, 15), "Rogue Rework!",
                "The rogue has been reworked! His abilities have received a number of changes to make his strengths more pronounced and focused.\n\n" +
                        "These abilities have been _removed:_\n" +
                        "_-_ Gains evasion from excess strength on armor\n" +
                        "_-_ Gains hunger 20% more slowly\n" +
                        "_-_ Identifies rings by wearing them\n" +
                        "_-_ Has an increased chance to detect secrets\n\n" +
                        "These abilities have been _added:_\n" +
                        "_-_ Searches in a wider radius than other heroes\n" +
                        "_-_ Is able to find more secrets in the dungeon\n\n" +
                        "Make sure to check out the Cloak of Shadows and Dagger changes as well."))

        changes.addButton(ChangeButton(Image(Assets.ROGUE, 0, 90, 12, 15), "Rogue Subclasses Rework!",
                "Both of the rogue's subclasses has been reworked, with an emphasis on more powerful abilities that need more interaction from the player.\n\n" +
                        "_The Assassin:_\n" +
                        "_-_ No longer gains a free +25% damage on surprise attacks\n" +
                        "_-_ Now prepares for a deadly strike while invisible, the longer he waits the more powerful the strike becomes.\n" +
                        "_-_ Charged attacks have special effects, such as blinking to the target and dealing bonus damage to weakened enemies.\n\n" +
                        "_The Freerunner:_\n" +
                        "_-_ No longer gains movement speed when not hungry or encumbered\n" +
                        "_-_ Now gains 'momentum' as he runs. Momentum increases evasion and movement speed as it builds.\n" +
                        "_-_ Momentum is rapidly lost when standing still.\n" +
                        "_-_ Evasion gained from momentum scales with excess strength on armor."))

        changes.addButton(ChangeButton(Image(Assets.TERRAIN_FEATURES, 16, 0, 16, 16), "Trap Overhaul!",
                "Most of the game's traps have received changes, some have been overhauled entirely!\n\n" +
                        "_-_ Removed Spear and Paralytic Gas Traps\n" +
                        "_-_ Lightning Trap is now Shocking and Storm traps\n" +
                        "_-_ Elemental Traps now all create fields of their element\n" +
                        "_-_ Worn and Poison Trap are now Worn and Poison Dart Trap\n" +
                        "_-_ Dart traps, Rockfall Trap, and Disintegration Trap are now always visible, but attack at a range\n" +
                        "_-_ Warping Trap reworked, no longer sends to previous floors\n" +
                        "_-_ Gripping and Flashing Traps now never deactivate, but are less harmful\n\n" +
                        "_-_ Tengu now uses Gripping Traps\n\n" +
                        "_-_ Significantly reduced instances of items appearing ontop of item-destroying traps"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.LOCKED_CHEST, null), "Chest Adjustments",
                "_-_ Crystal chests are now opened by crystal keys.\n\n" + "_-_ Golden chests now sometimes appear in the dungeon, containing more valuable items."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.IRON_KEY, null), "New Key Display",
                "The key display has been overhauled!\n\n" +
                        "_-_ Each key type now has its own icon, instead of all special keys being shown as golden.\n\n" +
                        "_-_ Can now display up to 6 keys, up from 3. After 3 keys the key icons will become smaller.\n\n" +
                        "_-_ Button background now dims as keys are collected, for added visual clarity."))


        changes = ChangeInfo(Messages.get(this.javaClass, "changes"), false, null)
        changes.hardlight(CharSprite.WARNING)
        infos.add(changes)

        changes.addButton(ChangeButton(WandOfCorruption(),
                "The Wand of Corruption has been reworked!\n\n" +
                        "_-_ Corruption resistance is now based on enemy exp values, not max HP. Low HP and debuffs still reduce enemy corruption resistance.\n\n" +
                        "_-_ Wand now only spends 1 charge per shot, and inflicts debuffs on enemies if it fails to corrupt. Debuffs become stronger the closer the wand got to corrupting.\n\n" +
                        "_-_ Corrupted enemies are now considered by the game to be ally characters.\n\n" +
                        "_-_ Corrupted enemies award exp immediately as they are corrupted.\n\n" +
                        "These changes are aimed at making the wand more powerful, and also less of an all-in wand. Wand of Corruption is now useful even if it doesn't corrupt an enemy."))

        changes.addButton(ChangeButton(Image(Assets.STATUE, 0, 0, 12, 15), "AI and Enemy Changes",
                "_-_ Characters now have an internal alignment and choose enemies based on that. Friendly characters should now never attack eachother.\n\n" +
                        "_-_ Injured characters will now always have a persistent health bar, even if they aren't being targeted.\n\n" +
                        "_-_ Improved enemy emote visuals, they now appear more frequently and there is now one for losing a target.\n\n" +
                        "_-_ Enemies now always lose their target after being teleported."))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Buff icons can now be tinted, several buffs take advantage of this to better display their state.\n\n" +
                        "_-_ Wands that fire magical bolts now push on their detonation area, opening doors and trampling grass.\n\n" +
                        "_-_ Crystal chest rooms will now always have a different item type in each chest.\n\n" +
                        "_-_ Burning and freezing now destroy held items in a more consistent manner.\n\n" +
                        "_-_ Reduced enemies in dark floors to 1.5x, from 2x.\n" +
                        "_-_ Increased the brightness of the fog of war.\n" +
                        "_-_ Various internal code improvements.\n" +
                        "_-_ Added a new interface and graphics for alchemy.\n" +
                        "_-_ Zooming is now less stiff at low resolutions.\n" +
                        "_-_ Improved VFX when items are picked up.\n" +
                        "_-_ Improved older updates in the changes list.\n" +
                        "_-_ Game now mutes during phone calls on android 6.0+"))

        changes.addButton(ChangeButton(Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(this.javaClass, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Various crash bugs\n" +
                        "_-_ Various exploits players could use to determine map shape\n" +
                        "_-_ Artifacts sometimes removed from quickslot when equipped\n" +
                        "_-_ Items removed from quickslots when containers are bought\n" +
                        "_-_ Swapping misc items not working with a full inventory\n" +
                        "_-_ Enemies sometimes not waking from sleep\n" +
                        "_-_ Swarms not duplicating over hazards\n" +
                        "_-_ Black bars on certain modern phones\n" +
                        "_-_ Action button not persisting between floors\n" +
                        "_-_ Various bugs with multiplicity curse\n" +
                        "_-_ Various minor visual bugs\n" +
                        "_-_ Plants not updating terrain correctly\n" +
                        "_-_ Enemies spawning ontop of exit stairs\n" +
                        "_-_ Evil Eyes sometimes skipping beam chargeup\n" +
                        "_-_ Warrior's seal being disabled by armor kit\n" +
                        "_-_ Gladiator being able to combo non-visible enemies\n" +
                        "_-_ Music volume being ignored in certain cases\n" +
                        "_-_ Game music not correctly pausing on android 2.2/2.3\n" +
                        "_-_ Game failing to save in rare cases"))

        changes.addButton(ChangeButton(Icons.get(Icons.LANGS), Messages.get(this.javaClass, "language"),
                "In English:\n" +
                        "_-_ Improved some common game log entries\n" +
                        "_-_ Fixed a typo when enemies die out of view\n" +
                        "_-_ Fixed a typo in the ghost hero's introduction\n" +
                        "_-_ Fixed a typo in dirk description\n" +
                        "_-_ Fixed various text errors with venom\n" +
                        "\n" +
                        "_-_ Translation Updates\n" +
                        "_-_ Various Translation Updates\n" +
                        "_-_ Added new language: _Turkish_\n" +
                        "_-_ New Language: _Czech_"))

        changes = ChangeInfo(Messages.get(this.javaClass, "buffs"), false, null)
        changes.hardlight(CharSprite.POSITIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(CloakOfShadows(),
                "As part of the rogue rework, the cloak of shadows has been significantly buffed:\n\n" +
                        "_-_ Max charges have been halved, however each charge is roughly twice as useful.\n" +
                        "_-_ As there are half as many charges total, charge rate is effectively increased.\n" +
                        "_-_ Cooldown mechanic removed, cloak can now be used multiple times in a row.\n" +
                        "_-_ Cloak levelling progression changed, it is now much more dependant on hero level\n\n" +
                        "These changes should let the rogue go invisible more often, and with more flexibility."))

        changes.addButton(ChangeButton(Dagger(),
                "As part of the rogue rework, sneak attack weapons have been buffed:\n\n" +
                        "_-_ Dagger sneak attack minimum damage increased to 75% from 50%.\n" +
                        "_-_ Dirk sneak attack minimum damage increased to 67% from 50%\n" +
                        "_-_ Assassin's blade sneak attack minimum damage unchanged at 50%\n\n" +
                        "This change should hopefully give the rogue some needed earlygame help, and give him a more clear choice as to what item he should upgrade, if no items were found in the dungeon."))

        changes.addButton(ChangeButton(Flail(),
                "The flail's downsides were too harsh, so one of them has been changed both to make its weaknesses more centralized and to hopefully increase its power.\n\n" + "_-_ Flail no longer attacks at 0.8x speed, instead it has 20% reduced accuracy."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.POTION_GOLDEN, null), "Potion Adjustments",
                "Potion of Purification buffed:\n" +
                        "_-_ Drinking effect now lasts for 20 turns, up from 15.\n" +
                        "_-_ Drinking now provides immunity to all area-bound effects, not just gasses.\n\n" +
                        "Potion of Frost buffed:\n" +
                        "_-_ Now creates a freezing field which lasts for several turns."))

        changes = ChangeInfo(Messages.get(this.javaClass, "nerfs"), false, null)
        changes.hardlight(CharSprite.NEGATIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(Image(Assets.WARRIOR, 0, 90, 12, 15), "Berserker",
                "The Berserker's survivability and power have been reduced to help bring him into line with the other subclasses:\n\n" +
                        "_-_ Bonus damage from low health reduced significantly when below 50% HP. 2x damage while berserking is unchanged.\n\n" +
                        "_-_ Turns of exhaustion after berserking increased to 60 from 40. Damage reduction from exhaustion stays higher for longer."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.REMAINS, null), "Heroes Remains",
                "_-_ Remains can no longer contain progression items, such as potions of strength or scrolls of upgrade.\n\n" +
                        "_-_ All upgradeable items dropped by remains are now capped at +3 (+0 for artifacts)\n\n" +
                        "The intention for remains is so a previously failed run can give a nice surprise and tiny boost to the next one, but these items are both too strong and too easy to abuse.\n\n" +
                        "In compensation, it is now much less likely to receive gold from remains, unless that character died with very few items."))

        //**********************
        //       v0.6.1
        //**********************

        changes = ChangeInfo("v0.6.1", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo(Messages.get(this.javaClass, "new"), false, null)
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released August 15th, 2017\n" +
                        "_-_ 72 days after Shattered v0.6.0\n" +
                        "\n" +
                        "Commentary will be added here when this update is older."))
        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.GUIDE_PAGE, null), "Journal Additions",
                "_-_ Overhauled the Journal window with loads of new functionality\n\n" +
                        "_-_ Added a completely overhauled tutorial experience, which replaces the existing signpost system.\n\n" +
                        "_-_ Massively expanded the items catalog, now contains every identifiable item in the game."))
        changes.addButton(ChangeButton(BadgeBanner.image(Badges.Badge.ALL_ITEMS_IDENTIFIED.image), "Badge Changes",
                "_-_ Added new badges for identifying all weapons, armor, wands, and artifacts.\n\n" +
                        "_-_ All identification-based badges are now tied to the new item list system, and progress for them will persist between runs.\n\n" +
                        "_-_ Removed the Night Hunter badge\n\n" +
                        "_-_ The 'Many Deaths' badge now covers all death related badges, previously it was not covering 2 of them.\n\n" +
                        "_-_ Reduced the numbers of games needed for the 'games played' badges from 10/100/500/2000 to 10/50/250/1000\n\n" +
                        "_-_ Blank badges shown in the badges menu are now accurate to how many badges you have left to unlock."))
        changes.addButton(ChangeButton(Icons.get(Icons.DEPTH), "Dungeon Changes",
                "_-_ Added 5 new regional rooms\n" +
                        "_-_ Added two new uncommon room types\n" +
                        "_-_ Added a new type of tunnel room\n\n" +
                        "_-_ Level layouts now more likely to be dense and interconnected.\n\n" +
                        "_-_ Tunnels will now appear more consistently.\n\n" +
                        "_-_ Ascending stairs, descending stairs, and mining no longer increase hunger."))
        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_TOPAZ, null), RingOfEnergy().trueName(),
                "_-_ Added the ring of energy."))
        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.CHEST, null), "Sprites",
                "New sprites for the following:\n" +
                        "_-_ Chests & Mimics\n" +
                        "_-_ Darts\n" +
                        "_-_ Javelins\n" +
                        "_-_ Tomahawks"))

        changes = ChangeInfo(Messages.get(this.javaClass, "changes"), false, null)
        changes.hardlight(CharSprite.WARNING)
        infos.add(changes)

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_DIAMOND, null), "Ring Mechanics Changes",
                "Rings now handle upgrades and curses more similarly to other items:\n\n" +
                        "_-_ Rings are now found at +0, down from +1, but are more powerful to compensate.\n\n" +
                        "_-_ Curses no longer affect ring upgrades, it is now impossible to find negatively upgraded rings.\n\n" +
                        "_-_ Cursed rings are now always harmful regardless of their level, until the curse is cleansed.\n\n" +
                        "_-_ Scrolls of upgrade have a chance to remove curses on a ring, scrolls of remove curse will always remove the curse."))
        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_AMETHYST, null), RingOfWealth().trueName(),
                "The ring of wealth is getting a change in emphasis, moving away from affecting items generally, and instead affecting item drops more strongly.\n\n" +
                        "_-_ No longer grants any benefit to item spawns when levels are generated.\n\n" +
                        "_-_ Now has a chance to generate extra loot when defeating enemies.\n\n" +
                        "I'm planning to make further tweaks to this item in future updates."))
        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.POTION_CRIMSON, null), PotionOfHealing().trueName(),
                "Health Potions are getting a changeup to make hoarding and chugging them less effective, and to encourage a bit more strategy than to just drink them on the verge of death.\n\n" +
                        "_-_ Health potions now heal in a burst that fades over time, rather than instantly.\n\n" +
                        "_-_ Health potions now heal more than max HP at low levels, and slightly less than max HP at high levels.\n\n" +
                        "Make sure to read the dew vial changes as well."))
        changes.addButton(ChangeButton(DewVial(),
                "The dew vial (and dew) are having their healing abilities enhanced to improve the availability of healing in the sewers, and to help offset the health potion changes.\n\n" +
                        "_-_ Dew drops now heal 5% of max HP\n\n" +
                        "_-_ Dew vial now always spawns on floor 1\n\n" +
                        "_-_ The dew vial is now full at 20 drops, drinking heals 5% per drop and is instantaneous.\n\n" +
                        "_-_ Dew will always be collected into an available vial, even if the hero is below full HP.\n\n" +
                        "_-_ When drinking from the vial, the hero will now only drink as many drops as they need to reach full HP."))
        changes.addButton(ChangeButton(Image(Assets.STATUE, 0, 0, 12, 15), "AI Changes",
                "_-_ Improvements to pathfinding. Characters are now more prone to take efficient paths to their targets, and will prefer to wait instead of taking a very inefficient path.\n\n" + "_-_ Characters will now more consistently decide who to attack based on distance and who they are being attacked by."))
        changes.addButton(ChangeButton(Image(Assets.SPINNER, 144, 0, 16, 16), Messages.get(this.javaClass, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Issues with Android 7.0+ multi-window\n" +
                        "_-_ Rare stability issues on certain devices\n" +
                        "_-_ Numerous rare crash and freeze bugs\n" +
                        "_-_ Chasm death not showing in rankings\n" +
                        "_-_ Resting icon sometimes not appearing\n" +
                        "_-_ Various minor graphical bugs\n" +
                        "_-_ The ambitious imp rarely blocking paths\n" +
                        "_-_ Berserk prematurely ending after loading\n" +
                        "_-_ Mind vision not updating while waiting\n" +
                        "_-_ Troll blacksmith destroying broken seal\n" +
                        "_-_ Wands always being uncursed by upgrades\n" +
                        "_-_ Various bugs with Evil Eyes\n" +
                        "_-_ Thieves being able to escape while visible\n" +
                        "_-_ Enemies not visually dying in rare cases\n" +
                        "_-_ Visuals flickering while zooming on low resolution devices.\n" +
                        "_-_ Various minor bugs with the buff indicator\n" +
                        "_-_ Sleep-immune enemies being affected by magical sleep\n" +
                        "_-_ Sad Ghost being affected by corruption\n" +
                        "_-_ Switching places with the Sad Ghost over chasms causing the hero to fall"))
        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Completely overhauled the changes scene (which you're currently reading!)\n" +
                        "_-_ Item and enemy spawn RNG is now more consistent. Should prevent things like finding 4 crabs on floor 3.\n" +
                        "_-_ The compass marker now points toward entrances after the amulet has been acquired.\n" +
                        "_-_ Improved quickslot behaviour when items are removed by monks or thieves.\n" +
                        "_-_ Allies are now better able to follow you through bosses.\n" +
                        "_-_ Lloyd's Beacon's return function is no longer blocked by none-hostile creatures.\n" +
                        "_-_ Performance tweaks on devices with 2+ cpu cores\n" +
                        "_-_ Stepping on plants now interrupts the hero\n" +
                        "_-_ Improved potion and scroll inventory icons\n" +
                        "_-_ Increased landscape width of some windows\n" +
                        "_-_ Un-IDed artifacts no longer display charge"))
        changes.addButton(ChangeButton(Icons.get(Icons.LANGS), Messages.get(this.javaClass, "language"),
                "Fixed in English:\n" +
                        "_-_ Missing capitalization in Prison Guard text\n" +
                        "_-_ Typo when trying a locked chest with no key\n" +
                        "_-_ Missing period in alarm trap description\n\n" +
                        "_-_ Added new Language: _Catalan_\n\n" +
                        "_-_ Various translation updates"))

        changes = ChangeInfo(Messages.get(this.javaClass, "buffs"), false, null)
        changes.hardlight(CharSprite.POSITIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(UnstableSpellbook(),
                "The Unstable spellbook wasn't really worth upgrading, so it's getting some new effects to make it worth investing in!\n\n" +
                        "_-_ Infusing a scroll into the unstable spellbook will now grant a unique empowered effect whenever that scroll's spell is cast from the book.\n\n" +
                        "To compensate, charge mechanics have been adjusted:\n\n" +
                        "_-_ Max charges reduced from 3-8 to 2-6\n\n" +
                        "_-_ Recharge speed has been reduced slightly"))
        changes.addButton(ChangeButton(DriedRose().upgrade(10),
                "The ghost hero summoned by the rose is now much more capable and is also much less temporary:\n\n" +
                        "_-_ Ghost can now be equipped with a weapon and armor and uses them just like the hero.\n" +
                        "_-_ Ghost no longer takes damage over time as long as the rose remains equipped.\n" +
                        "_-_ Ghost health increased by 10\n" +
                        "_-_ Ghost now has a persistent HP bar\n" +
                        "_-_ Ghost can now follow you between floors\n\n" +
                        "However:\n\n" +
                        "_-_ Ghost no longer gains damage and defense from rose levels, instead gains strength to use better equipment.\n" +
                        "_-_ Rose no longer recharges while ghost is summoned\n" +
                        "_-_ Rose takes 25% longer to fully charge"))
        changes.addButton(ChangeButton(Icons.get(Icons.BACKPACK), "Inventory",
                "_-_ Inventory space increased from 19 slots to 20 slots.\n\n" + "_-_ Gold indicator has been moved to the top-right of the inventory window to make room for the extra slot."))

        changes = ChangeInfo(Messages.get(this.javaClass, "nerfs"), false, null)
        changes.hardlight(CharSprite.NEGATIVE)
        infos.add(changes)

        changes.addButton(ChangeButton(HornOfPlenty(),
                "The Horn of Plenty was providing a bit too much value in the earlygame, especially without upgrades:\n\n" +
                        "_-_ Charge Speed reduced, primarily at lower levels:\n-20% at +0\n-7.5% at +10\n\n" +
                        "_-_ Upgrade rate adjusted, Food now contributes towards upgrades exactly in line with how much hunger it restores. This means smaller food items will contribute more, larger ones will contribute less. Rations still grant exactly 1 upgrade each."))
        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_GARNET, null), RingOfMight().trueName(),
                "The Ring of Might's strength bonus is already extremely valuable, having it also provide an excellent health boost was simply too much:\n\n" +
                        "_-_ Health granted reduced from +5 per upgrade to +3.5% of max hp per upgrade.\n\n" +
                        "This is a massive reduction to its earlygame health boosting power, however as the player levels up this will improve. By hero level 26 it will be as strong as before this change."))
        changes.addButton(ChangeButton(EtherealChains(),
                "The ability for Ethereal Chains to pull you literally anywhere limits design possibilities for future updates, so I've added a limitation.\n\n" +
                        "_-_ Ethereal chains now cannot reach locations the player cannot reach by walking or flying. e.g. the chains can no longer reach into a locked room.\n\n" +
                        "_-_ Ethereal chains can now reach through walls on boss floors, but the above limitation still applies."))

        //**********************
        //       v0.6.0
        //**********************

        changes = ChangeInfo("v0.6.0", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released June 4th, 2017\n" +
                        "_-_ 116 days after Shattered v0.5.0\n" +
                        "\n" +
                        "Commentary will be added here when this update is older."))

        changes.addButton(ChangeButton(Icons.get(Icons.DEPTH), "Levelgen Overhaul!",
                "Level creation algorithm overhauled!\n\n" +
                        "_-_ Levels are now much less box-shaped\n" +
                        "_-_ Sewers are now smaller, caves+ are now larger\n" +
                        "_-_ Some rooms can now be much larger than before\n" +
                        "_-_ Added 8 new standard room types, and loads of new standard room layouts\n\n" +
                        "_-_ Reduced number of traps in later chapters"))

        changes.addButton(ChangeButton(ItemSprite(Torch()), "Light Source Buffs",
                "_-_ Light sources now grant significantly more vision\n" +
                        "_-_ Light from torches now lasts 20% longer\n" +
                        "_-_ Slightly increased visibility on floor 22+\n" +
                        "_-_ Floor 21 shop now sells 3 torches, up from 2"))

        changes.addButton(ChangeButton(ItemSprite(Food()), "Food Buffs",
                "_-_ Meat and small rations are 50% more filling\n" + "_-_ Pasties and blandfruit are 12.5% more filling"))

        changes.addButton(ChangeButton(ItemSprite(Greataxe()), "Tier-5 Weapon Buffs",
                "_-_ Greataxe base damage increased by ~22%\n" + "_-_ Greatshield base damage increased by ~17%"))

        changes.addButton(ChangeButton(ItemSprite(StoneOfEnchantment()), "Enchant and Glyph Balance Changes",
                "_-_ Vampiric enchant lifesteal reduced by 20%\n\n" +
                        "Lucky enchant rebalanced:\n" +
                        "_-_ now deals 2x/0x damage, instead of min/max\n" +
                        "_-_ base chance to deal 2x increased by ~10%\n\n" +
                        "Glyph of Viscosity rebalanced:\n" +
                        "_-_ proc chance reduced by ~25% \n" +
                        "_-_ damage over time reverted from 15% to 10%\n\n" +
                        "_-_ Glyph of Entanglement root time reduced by 40%\n\n" +
                        "Glyph of Potential rebalanced:\n" +
                        "_-_ self-damage no longer scales with max hp\n" +
                        "_-_ grants more charge at higher levels"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Visiting floor 21 before completing the imp quest no longer prevents his shop from spawning\n\n" +
                        "_-_ Floor 2 entry doors are now only hidden for new players\n\n" +
                        "_-_ Falling damage tweaked to be less random\n\n" +
                        "_-_ Resume indicator now appears in more cases"))

        //**********************
        //       v0.5.0
        //**********************

        changes = ChangeInfo("v0.5.0", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released February 8th, 2017\n" +
                        "_-_ 233 days after Shattered v0.4.0\n" +
                        "_-_ 115 days after Shattered v0.4.3\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Icons.get(Icons.DEPTH), "New Dungeon Visual Style!",
                "_-_ Walls and some terrain now have depth\n" +
                        "_-_ Characters & items are raised & cast shadows\n" +
                        "_-_ Added a visible tile grid in the settings menu"))

        changes.addButton(ChangeButton(ItemSprite(Quarterstaff()), "Equipment Balance Changes",
                "_-_ Quarterstaff armor bonus increased from 2 to 3\n\n" +
                        "_-_ Wand of Frost damage against chilled enemies reduced from -7.5% per turn of chill to -10%\n\n" +
                        "_-_ Wand of Transfusion self-damage reduced from 15% max hp to 10% max hp per zap\n\n" +
                        "_-_ Dried Rose charges 20% faster and the ghost hero is stronger, especially at low levels"))

        changes.addButton(ChangeButton(ItemSprite(Stylus()), "Glyph Balance Changes",
                "_-_ Glyph of Entanglement activates less often but grants significantly more herbal armor\n\n" +
                        "_-_ Glyph of Stone armor bonus reduced from 2+level to 0+level\n\n" +
                        "_-_ Glyph of Antimagic magical damage resist reduced from 50% of armor to 33% of armor\n\n" +
                        "_-_ Glyph of Viscosity damage rate increased from 10% of deferred damage to 15%"))

        changes.addButton(ChangeButton(Icons.get(Icons.LANGS), Messages.get(this.javaClass, "language"),
                "_-_ Added new Language: Esperanto\n" + "_-_ Added new Language: Indonesian\n"))

        //**********************
        //       v0.4.X
        //**********************

        changes = ChangeInfo("v0.4.X", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo("v0.4.3", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released October 16th, 2016\n" +
                        "_-_ 37 days after Shattered v0.4.2\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), "Technical Improvements",
                "_-_ Added rankings and hall of heroes sync via Google Play Games, for the Google Play version of Shattered.\n\n" +
                        "_-_ Added Power Saver mode in settings\n" +
                        "_-_ Download size reduced by ~25%\n" +
                        "_-_ Game now supports small screen devices\n" +
                        "_-_ Performance improvements\n" +
                        "_-_ Improved variety of level visuals"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.FLAIL, null), "Balance Changes",
                "_-_ Flail max damage increased by ~15%\n" +
                        "_-_ Wand of Frost damage reduction increased from 5% per turn of chill to 7.5%\n" +
                        "_-_ Ring of Furor speed bonus reduced by ~15% for slow weapons, ~0% for fast weapons\n" +
                        "_-_ Reduced sacrificial curse bleed by ~33%\n" +
                        "_-_ Reworked glyph of brimstone, now grants shielding instead of healing\n" +
                        "_-_ Reworked glyph of stone, now reduces speed in doorways\n" +
                        "_-_ Thrown potions now trigger traps and plants"))

        changes = ChangeInfo("v0.4.2", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released September 9th, 2016\n" +
                        "_-_ 46 days after Shattered v0.4.1\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), "Technical Improvements",
                "_-_ Many general performance improvements\n" +
                        "_-_ Game now uses 2 CPU cores, up from 1\n" +
                        "_-_ Reduced hitching on many devices\n" +
                        "_-_ Framerate improvements for older devices\n" +
                        "_-_ Game size reduced by ~10%"))

        changes.addButton(ChangeButton(ItemSprite(Glaive()), "Balance Changes",
                "_-_ Spear and Glaive damage reduced\n" +
                        "_-_ Runic blade damage reduced\n" +
                        "_-_ Grim enchant now procs more often\n" +
                        "_-_ Glyph of stone adds more weight\n" +
                        "_-_ Glyph of potential procs less often\n" +
                        "_-_ Wand of Fireblast less dangerous to caster\n" +
                        "_-_ Wand of Pris. Light reveal area reduced\n" +
                        "_-_ Ring of Wealth slightly more effective\n" +
                        "_-_ Ring of Sharpshooting gives more accuracy"))

        changes = ChangeInfo("v0.4.1", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released July 25th, 2016\n" +
                        "_-_ 35 days after Shattered v0.4.0\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(ItemSprite(PlateArmor()), "Item Changes pt.1",
                "Armor and Enemy Balance Changes:\n" +
                        "_-_ Armor now has a min damage block value\n" +
                        "_-_ Armor gains more blocking from upgrades\n" +
                        "_-_ Prison+ enemy damage increased\n" +
                        "_-_ Evil Eyes reworked\n" +
                        "_-_ Brimstone glyph healing reduced\n" +
                        "\n" +
                        "Class Balance Changes:\n" +
                        "_-_ Mage's Staff melee damage increased\n" +
                        "_-_ Mage's Staff can now preserve one upgrade\n" +
                        "_-_ Cloak of Shadows buffed at lower levels\n" +
                        "_-_ Some Battlemage effects changed\n" +
                        "\n" +
                        "Wand Balance Changes:\n" +
                        "_-_ All wands damage adjusted/increased\n" +
                        "_-_ Upgraded wands appear slightly less often\n" +
                        "_-_ Wand of Lightning bonus damage reduced\n" +
                        "_-_ Wand of Fireblast uses fewer charges\n" +
                        "_-_ Wand of Venom damage increases over time\n" +
                        "_-_ Wand of Prismatic Light bonus damage reduced\n" +
                        "_-_ Corrupted enemies live longer & no longer attack eachother\n" +
                        "_-_ Wands in the holster now charge faster"))

        changes.addButton(ChangeButton(ItemSprite(RunicBlade()), "Item Changes pt.2",
                "Ring Balance Changes:\n" +
                        "_-_ Ring of Force weaker at 18+ strength, stronger otherwise\n" +
                        "_-_ Ring of Tenacity reduces more damage\n" +
                        "_-_ Ring of Wealth secret rewards adjusted\n" +
                        "_-_ Ring of Evasion now works consistently\n" +
                        "\n" +
                        "Artifact Balance Changes:\n" +
                        "_-_ Dried Rose charges faster, ghost HP up\n" +
                        "_-_ Horn of Plenty now charges on exp gain\n" +
                        "_-_ Master Thieves Armband levels faster\n" +
                        "_-_ Sandals of Nature level faster\n" +
                        "_-_ Hourglass uses fewer charges at a time\n" +
                        "_-_ Horn of Plenty adjusted, now stronger\n" +
                        "\n" +
                        "Weapon Balance Changes:\n" +
                        "_-_ Lucky enchant deals max dmg more often\n" +
                        "_-_ Dazzling enchant now cripples & blinds\n" +
                        "_-_ Flail now can't surprise attack, damage increased\n" +
                        "_-_ Extra reach weapons no longer penetrate\n" +
                        "_-_ Runic blade damage decreased"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Added a new journal button with key display\n" +
                        "_-_ Keys now exist in the journal, not inventory\n" +
                        "_-_ Improved donator menu button visuals\n" +
                        "_-_ Increased the efficiency of data storage\n" +
                        "_-_ Chasms now deal less damage, but bleed\n" +
                        "_-_ Many shop prices adjusted\n" +
                        "_-_ Pirahna rooms no longer give cursed gear"))

        changes = ChangeInfo("v0.4.0", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released June 20th, 2016\n" +
                        "_-_ 391 days after Shattered v0.3.0\n" +
                        "_-_ 50 days after Shattered v0.3.5\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(ItemSprite(Longsword()), "Equipment Overhaul!",
                "_-_ 13 new weapons, 12 rebalanced weapons\n" +
                        "\n" +
                        "Equipment Balance:\n" +
                        "_-_ Tier 2-4 weapons do more base damage\n" +
                        "_-_ All weapons gain more dmg from upgrades\n" +
                        "_-_ Upgrades now remove enchants less often\n" +
                        "_-_ Upgrades reduce str requirements less\n" +
                        "_-_ All armors require 1 more str\n" +
                        "_-_ Encumbered characters can't sneak attack\n" +
                        "\n" +
                        "Droprate Changes:\n" +
                        "_-_ Powerful equipment less common early\n" +
                        "_-_ +3 and +2 equipment less common\n" +
                        "_-_ Equipment curses more common\n" +
                        "_-_ Tier 1 equipment no longer drops\n" +
                        "_-_ Arcane styli slightly more common\n" +
                        "_-_ Better item drops on floors 22-24"))

        changes.addButton(ChangeButton(ItemSprite(Stylus()), "Curse, Enchant, & Glyph Overhaul!",
                "_-_ 3 new enchants, 10 rebalanced enchants\n" +
                        "_-_ 8 new glyphs, 5 rebalanced glyphs\n" +
                        "_-_ 12 new curse effects\n" +
                        "\n" +
                        "Equipment Curses:\n" +
                        "_-_ Curses now give negative effects\n" +
                        "_-_ Curses no longer give negative levels\n" +
                        "_-_ Upgrades now weaken curses\n" +
                        "_-_ Remove curse scrolls now affect 1 item"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "Class Balance:\n" +
                        "_-_ Huntress now starts with knuckleduster\n" +
                        "_-_ Assassin sneak bonus damage reduced\n" +
                        "_-_ Fixed a bug where berserker was immune when enraged\n" +
                        "_-_ Gladiator's clobber now inflicts vertigo, not stun\n" +
                        "\n" +
                        "Enemy Balance:\n" +
                        "_-_ Tengu damage increased\n" +
                        "_-_ Prison Guard health and DR increased\n" +
                        "\n" +
                        "Misc:\n" +
                        "_-_ Scrolls of upgrade no longer burn\n" +
                        "_-_ Potions of strength no longer freeze\n" +
                        "_-_ Translation updates\n" +
                        "_-_ Various bugfixes"))

        //**********************
        //       v0.3.X
        //**********************

        changes = ChangeInfo("v0.3.X", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo("v0.3.5", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released May 1st, 2016\n" +
                        "_-_ 81 days after Shattered v0.3.4\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Image(Assets.WARRIOR, 0, 15, 12, 15), "Warrior Rework!",
                "Warrior Rework:\n" +
                        "_-_ Starting STR down to 10, from 11\n" +
                        "_-_ Short sword dmg down to 1-10, from 1-12\n" +
                        "_-_ Short sword can no longer be reforged\n" +
                        "_-_ Now IDs potions of health, not STR\n" +
                        "_-_ Now starts with a unique seal for armor\n" +
                        "_-_ Seal grants shielding ontop of health\n" +
                        "_-_ Seal allows for one upgrade transfer"))

        changes.addButton(ChangeButton(Image(Assets.WARRIOR, 0, 90, 12, 15), "Warrior Subclass Rework!",
                "Berserker Rework:\n" +
                        "_-_ Bonus damage now scales with lost HP, instead of a flat 50% at 50% hp\n" +
                        "_-_ Berserker can now endure through death for a short time, with caveats\n" +
                        "\n" +
                        "Gladiator Rework:\n" +
                        "_-_ Combo no longer grants bonus damage\n" +
                        "_-_ Combo is now easier to stack\n" +
                        "_-_ Combo now unlocks special finisher moves"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "Balance Tweaks:\n" +
                        "_-_ Spears can now reach enemies 1 tile away\n" +
                        "_-_ Wand of Blast Wave now pushes bosses less\n" +
                        "\n" +
                        "Misc:\n" +
                        "_-_ Can now examine multiple things in one tile\n" +
                        "_-_ Pixelated font now available for cyrillic languages\n" +
                        "_-_ Added Hungarian language"))

        changes = ChangeInfo("v0.3.4", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released February 10th, 2016\n" +
                        "_-_ 54 days after Shattered v0.3.3\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Icons.get(Icons.LANGS), "Translations!",
                "Shattered Pixel Dungeon now supports multiple languages, thanks to a new community translation project!\n\n" +
                        "The Following languages are supported at release:\n" +
                        "_-_ English\n" +
                        "_-_ Russian\n" +
                        "_-_ Korean\n" +
                        "_-_ Chinese\n" +
                        "_-_ Portugese\n" +
                        "_-_ German\n" +
                        "_-_ French\n" +
                        "_-_ Italian\n" +
                        "_-_ Polish\n" +
                        "_-_ Spanish"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "Completely redesigned the text rendering system to support none-english characters\n\n" +
                        "New text system renders using either the default system font, or the original pixelated game font. None-latin languages must use system font.\n\n" +
                        "Balance Changes:\n" +
                        "_-_ Hunger now builds ~10% slower\n" +
                        "_-_ Sad Ghost no longer gives tier 1 loot\n" +
                        "_-_ Sad Ghost gives tier 4/5 loot less often\n" +
                        "_-_ Burning now deals less damage at low HP\n" +
                        "_-_ Weakness no longer discharges wands\n" +
                        "_-_ Rockfall traps rebalanced"))

        changes = ChangeInfo("v0.3.3", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released December 18th, 2015\n" +
                        "_-_ 44 days after Shattered v0.3.2\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), "Google Play Games",
                "Added support for Google Play Games in the Google Play version:\n\n" +
                        "- Badges can now sync across devices\n" +
                        "- Five Google Play Achievements added\n" +
                        "- Rankings sync will come in future\n\n" +
                        "Shattered remains a 100% offline game if Google Play Games is not enabled"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "Gameplay Changes:\n" +
                        "- Tengu's maze is now different each time\n" +
                        "- Items no longer auto-pickup when enemies are near\n" +
                        "\n" +
                        "Fixes:\n" +
                        "- Fixed several bugs with prison enemies\n" +
                        "- Fixed some landscape window size issues\n" +
                        "- Fixed other minor bugs\n" +
                        "\n" +
                        "Misc:\n" +
                        "- Added support for reverse landscape\n" +
                        "- Added a small holiday treat ;)\n" +
                        "- Thieves now disappear when they get away"))

        changes = ChangeInfo("v0.3.2", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released November 4th, 2015\n" +
                        "_-_ 79 days after Shattered v0.3.1\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Image(Assets.TENGU, 0, 0, 14, 16), "Prison Rework",
                "_-_ Tengu boss fight completely redone\n" +
                        "_-_ Corpse dust quest overhauled\n" +
                        "_-_ Rotberry quest overhauled\n" +
                        "_-_ NEW elemental embers quest\n" +
                        "_-_ NEW prison mob: insane prison guards!\n" +
                        "_-_ Thieves can escape with a stolen item\n" +
                        "_-_ Gnoll shaman attack speed increased"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.MASTERY, null), "Balance Changes",
                "_-_ Mastery Book now always at floor 10, even when unlocked\n" +
                        "_-_ Hunger damage now increases with hero level, starts lower\n" +
                        "\n" +
                        "Sewers rebalance: \n" +
                        "_-_ Marsupial rat dmg and evasion reduced\n" +
                        "_-_ Gnoll scout accuracy reduced\n" +
                        "_-_ Sewer crabs less likely to spawn on floor 3, grant more exp\n" +
                        "_-_ Fly swarms rebalanced, moved to the sewers\n" +
                        "_-_ Great Crab HP reduced \n" +
                        "_-_ Goo fight rebalanced \n" +
                        " \n" +
                        "Base Class Changes: \n" +
                        "_-_ Mage's staff base damage increased \n" +
                        "_-_ Huntress now starts with 20 hp \n" +
                        "_-_ Huntress no longer heals more from dew \n" +
                        "_-_ Rogue's cloak of shadows now drains less while invisible\n" +
                        " \n" +
                        "Subclass Changes: \n" +
                        "_-_ Berserker now starts raging at 50% hp (up from 40%) \n" +
                        "_-_ Warden now heals 2 extra HP from dew \n" +
                        "_-_ Warlock completely overhauled"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Visual improvements from 1.9.1 source\n" +
                        "_-_ Improved golden UI for donators\n" +
                        "_-_ Fixed 'white line' graphical artifacts\n" +
                        "_-_ Floor locking now pauses all passive effects, not just hunger\n" +
                        "_-_ Cursed chains now only cripple, do not root\n" +
                        "_-_ Warping trap rebalanced, much less harsh\n" +
                        "_-_ Various other bugfixes"))

        changes = ChangeInfo("v0.3.1", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released August 17th, 2015\n" +
                        "_-_ 83 days after Shattered v0.3.0\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Image(Assets.TERRAIN_FEATURES, 112, 96, 16, 16), "Trap Overhaul",
                "_-_ Over 20 new traps + tweaks to existing ones\n" +
                        "_-_ Trap visuals overhauled\n" +
                        "_-_ Traps now get trickier deeper in the dungeon\n" +
                        "_-_ Trap room reworked to make use of new traps"))

        changes.addButton(ChangeButton(Image(Assets.MENU, 15, 0, 16, 15), "Interface Improvements",
                "_-_ Adjusted display scaling\n" +
                        "_-_ Search and Examine merged into one button (double tap to search)\n" +
                        "_-_ New max of 4 Quickslots!\n" +
                        "_-_ Multiple toolbar modes for large display and landscape users\n" +
                        "_-_ Ability to flip toolbar and indicators (left-handed mode)\n" +
                        "_-_ Better settings menu\n" +
                        "_-_ Graphics settings are now accessible ingame\n" +
                        "_-_ More consistent text rendering\n" +
                        "_-_ Recent changes can now be viewed from the title screen\n" +
                        "_-_ Added a health bar for bosses"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "Balance changes:\n" +
                        "_-_ Ethereal chains now gain less charge the more charges they have\n" +
                        "_-_ Staff of regrowth grants more herbal healing\n" +
                        "_-_ Monks now disarm less randomly, but not less frequently\n" +
                        "_-_ Invisibility potion effect now lasts for 20 turns, up from 15\n\n" +
                        "QOL improvements:\n" +
                        "_-_ Quickslots now autotarget enemies\n" +
                        "_-_ Resting now works while hungry & at max HP\n" +
                        "_-_ Dew drops no longer collect when at full health with no dew vial\n" +
                        "_-_ Items now stay visible in the fog of war\n" +
                        "_-_ Added a visual hint to weak floor rooms\n" +
                        "_-_ Many bugfixes"))

        changes = ChangeInfo("v0.3.0", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released May 26th, 2015\n" +
                        "_-_ 253 days after Shattered v0.2.0\n" +
                        "_-_ 92 days after Shattered v0.2.4\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Image(Assets.MAGE, 0, 15, 12, 15), "Mage Rework!",
                "_-_ No longer starts with knuckledusters or a wand\n" +
                        "_-_ Can no longer equip wands\n" +
                        "_-_ Now starts with a unique mages staff, empowered with magic missile to start.\n\n" +
                        "_-_ Battlemage reworked, staff now deals bonus effects when used as a melee weapon.\n\n" +
                        "_-_ Warlock reworked, gains more health and fullness from gaining exp, but food no longer restores hunger."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.WAND_DISINTEGRATION, null), "Wand Rework!",
                "Removed Wands:\n" +
                        "Flock, Blink, Teleportation, Avalanche\n" +
                        "\n" +
                        "Reworked Wands:\n" +
                        "Magic Missile, Lightning, Disintegration,\n" +
                        "Fireblast (was Firebolt), Venom (was Poison),\n" +
                        "Frost (was Slowing), Corruption (was Amok),\n" +
                        "Blast Wave (was Telekinesis), Regrowth\n" +
                        "\n" +
                        "New Wands:\n" +
                        "Prismatic Light, Transfusion\n" +
                        "\n" +
                        "_-_ Wand types are now known by default.\n" +
                        "_-_ Wands now each have unique sprites.\n" +
                        "_-_ Wands now cap at 10 charges instead of 9\n" +
                        "_-_ Wands now recharge faster the more charges are missing.\n" +
                        "_-_ Self-targeting with wands is no longer possible.\n" +
                        "_-_ Wand recharge effects now give charge over time.\n" +
                        "_-_ Wands can now be cursed!"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "New Artifacts:\n" +
                        "_-_ Ethereal Chains (replaces wand of blink)\n" +
                        "_-_ Lloyd's Beacon (replaces wand of teleportation)\n" +
                        "\n" +
                        "Misc. Balance changes:\n" +
                        "_-_ Blessed Ankhs now revive at 1/4hp, but also grant initiative.\n" +
                        "_-_ Alchemist's Toolkit removed (will be reworked)\n" +
                        "_-_ Chalice of blood nerfed, now regens less hp at high levels.\n" +
                        "_-_ Cape of Thorns buffed, now absorbs all damage, but only deflects adjacent attacks.\n" +
                        "_-_ Sandals of nature adjusted, now give more seeds, less dew.\n" +
                        "_-_ Hunger no longer increases while fighting bosses.\n" +
                        "_-_ Floor 1 now spawns 10 rats, exactly enough to level up.\n" +
                        "_-_ Scrolls of recharging and mirror image now more common.\n" +
                        "_-_ Mimics are now less common, stronger, & give better loot.\n" +
                        "\n" +
                        "UI tweaks:\n" +
                        "_-_ New app icon!\n" +
                        "_-_ Shading added to main game interface\n" +
                        "_-_ Buffs now have descriptions, tap their icons!\n" +
                        "_-_ Visual indicator added for surprising enemies"))

        //**********************
        //       v0.2.X
        //**********************

        changes = ChangeInfo("v0.2.X", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo("v0.2.4", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released February 23rd, 2015\n" +
                        "_-_ 48 days after Shattered v0.2.3\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(ItemSprite(Honeypot()), "Pixel Dungeon v1.7.5",
                "v1.7.3 - v1.7.5 Source Implemented, with exceptions:\n" +
                        "_-_ Degredation not implemented.\n\n" +
                        "_-_ Badge syncing not implemented.\n\n" +
                        "_-_ Scroll of Weapon Upgrade renamed to Magical Infusion, works on armor.\n\n" +
                        "_-_ Scroll of Enchantment not implemented, Arcane stylus has not been removed.\n\n" +
                        "_-_ Honey pots now shatter in a new item: shattered honeypot. A bee will defend its shattered pot to the death against anything that gets near.\n\n" +
                        "_-_ Bombs have been reworked/nerfed: they explode after a delay, no longer stun, deal more damage at the center of the blast, affect the world (destroy items, blow up other bombs)."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.BANDOLIER, null), "New Content",
                "_-_ The huntress has been buffed: starts with Potion of Mind Vision identified, now benefits from strength on melee attacks, and has a chance to reclaim a single used ranged weapon from each defeated enemy.\n\n" +
                        "_-_ A new container: The Potion Bandolier! Potions can now shatter from frost, but the bandolier can protect them.\n\n" +
                        "_-_ Shops now stock a much greater variety of items, some item prices have been rebalanced.\n\n" +
                        "_-_ Added Merchant's Beacon.\n\n" +
                        "_-_ Added initials for IDed scrolls/potions."))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Going down stairs no longer increases hunger, going up still does.\n\n" +
                        "_-_ Many, many bugfixes.\n" +
                        "_-_ Some UI improvements.\n" +
                        "_-_ Ingame audio quality improved.\n" +
                        "_-_ Unstable spellbook buffed.\n" +
                        "_-_ Psionic blasts deal less self-damage.\n" +
                        "_-_ Potions of liquid flame affect a 3x3 grid."))

        changes = ChangeInfo("v0.2.3", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released January 6th, 2015\n" +
                        "_-_ 64 days after Shattered v0.2.2\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(ItemSprite(TimekeepersHourglass()), "Artifact Changes",
                "Added 4 new artifacts:\n" +
                        "_-_ Alchemist's Toolkit\n" +
                        "_-_ Unstable Spellbook\n" +
                        "_-_ Timekeeper's Hourglass\n" +
                        "_-_ Dried Rose\n\n" +
                        "_-_ Artifacts are now unique over each run\n" +
                        "_-_ Artifacts can now be cursed!\n" +
                        "_-_ Cloak of Shadows is now exclusive to the rogue\n" +
                        "_-_ Smaller Balance Changes and QOL improvements to almost every artifact"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.POTION_CRIMSON, null), "Balance Changes",
                "_-_ Health potion farming has been nerfed from all sources\n" +
                        "_-_ Freerunner now moves at very high speeds when invisible\n" +
                        "_-_ Ring of Force buffed significantly\n" +
                        "_-_ Ring of Evasion reworked again\n" +
                        "_-_ Improved the effects of some blandfruit types\n" +
                        "_-_ Using throwing weapons now cancels stealth"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "_-_ Implemented a donation system in the Google Play version of Shattered\n\n" +
                        "_-_ Significantly increased the stability of the save system\n\n" +
                        "_-_ Increased the number of visible rankings to 11 from 6\n\n" +
                        "_-_ Improved how the player is interrupted by harmful events"))

        changes = ChangeInfo("v0.2.2", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released November 3rd, 2014\n" +
                        "_-_ 21 days after Shattered v0.2.1\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.WEIGHT, null), "Pixel Dungeon v1.7.2",
                "Implemented directly from v1.7.2:\n" +
                        "_-_ Synchronous Movement\n" +
                        "_-_ Challenges\n" +
                        "_-_ UI improvements\n" +
                        "_-_ Vertigo debuff\n\n" +
                        "Implement with changes:\n" +
                        "_-_ Weightstone: Increases either speed or damage, at the cost of the other, instead of increasing either speed or accuracy.\n\n" +
                        "Not Implemented:\n" +
                        "_-_ Key ring and unstackable keys\n" +
                        "_-_ Blindweed has not been removed"))

        changes.addButton(ChangeButton(Image(Assets.TERRAIN_FEATURES, 144, 112, 16, 16), "New Plants",
                "Added two new plants:\n" +
                        "_-_ Stormvine, which brews into levitation\n" +
                        "_-_ Dreamfoil, which brews into purity\n\n" +
                        "_-_ Potion of levitation can now be thrown to make a cloud of confusion gas\n\n" +
                        "_-_ Removed gas collision logic, gasses can now stack without limitation."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.REMAINS, null), "Heroes Remains",
                "Heroes remains have been significantly adjusted to prevent strategies that exploit them, but also to increase their average loot.\n\n" +
                        "Remains have additional limitations:\n" +
                        "_-_ Heros will no longer drop remains if they have obtained the amulet of yendor, or die 5 or more floors above the deepest floor they have reached\n" +
                        "_-_ Class exclusive items can no longer appear in remains\n" +
                        "_-_ Items found in remains are now more harshly level-capped\n" +
                        "_-_ Remains are not dropped, and cannot be found, when challenges are enabled.\n\n" +
                        "However:\n" +
                        "_-_ Remains can now contain useful items from the inventory, not just equipped items.\n" +
                        "_-_ Remains are now less likely to contain gold."))

        changes = ChangeInfo("v0.2.1", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released October 13th, 2014\n" +
                        "_-_ 28 days after Shattered v0.2.0\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Image(Assets.GHOST, 0, 0, 14, 15), "New Sewer Quests",
                "_-_ Removed the dried rose quest (the rose will return...)\n\n" +
                        "_-_ Tweaked the mechanics of the fetid rat quest\n\n" +
                        "_-_ Added a gnoll trickster quest\n\n" +
                        "_-_ Added a great crab quest"))

        changes.addButton(ChangeButton(Image(Assets.GOO, 43, 3, 14, 11), "Goo Changes",
                "Goo's animations have been overhauled, including a particle effect for the area of its pumped up attack.\n\n" + "Goo's arena has been updated to give more room to maneuver, and to be more variable."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.GUIDE_PAGE, null), "Story & Signpost Changes",
                "Most text in the sewers has been overhauled, including descriptions, quest dialogues, signposts, and story scrolls"))

        changes = ChangeInfo("v0.2.0", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released September 15th, 2014\n" + "_-_ 31 days after Shattered v0.1.1"))

        changes.addButton(ChangeButton(ItemSprite(HornOfPlenty()), "Artifacts!",
                "Added artifacts to the game!\n\n" +
                        "Artifacts are unique items which offer new gameplay opportunities and grow stronger through unique means.\n\n" +
                        "Removed 7 Rings... And Replaced them with 7 Artifacts!\n" +
                        "_-_ Ring of Shadows becomes Cloak of Shadows\n" +
                        "_-_ Ring of Satiety becomes Horn of Plenty\n" +
                        "_-_ Ring of Mending becomes Chalice of Blood\n" +
                        "_-_ Ring of Thorns becomes Cape of Thorns\n" +
                        "_-_ Ring of Haggler becomes Master Thieves' Armband\n" +
                        "_-_ Ring of Naturalism becomes Sandals of Nature"))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.RING_DIAMOND, null), "New Rings!",
                "To replace the lost rings, 6 new rings have been added:\n" +
                        "_-_ Ring of Force\n" +
                        "_-_ Ring of Furor\n" +
                        "_-_ Ring of Might\n" +
                        "_-_ Ring of Tenacity\n" +
                        "_-_ Ring of Sharpshooting\n" +
                        "_-_ Ring of Wealth\n\n" +
                        "The 4 remaining rings have also been tweaked or reworked entirely:\n" +
                        "_-_ Ring of Accuracy\n" +
                        "_-_ Ring of Elements\n" +
                        "_-_ Ring of Evasion\n" +
                        "_-_ Ring of Haste"))

        changes.addButton(ChangeButton(Icons.get(Icons.PREFS), Messages.get(this.javaClass, "misc"),
                "-Nerfed farming health potions from fly swarms.\n\n" +
                        "-Buffed crazed bandit and his drops.\n\n" +
                        "-Made Blandfruit more common.\n\n" +
                        "-Nerfed Assassin bonus damage slightly, to balance with him having an artifact now.\n\n" +
                        "-Added a welcome page!"))

        changes = ChangeInfo(" ", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        //**********************
        //       v0.1.X
        //**********************

        changes = ChangeInfo("v0.1.X", true, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes = ChangeInfo("v0.1.1", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released August 15th, 2014\n" +
                        "_-_ 10 days after Shattered v0.1.0\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(Blandfruit(),
                "Players who chance upon gardens or who get lucky while trampling grass may come across a new plant: the _Blandfruit._\n\n" + "As the name implies, the fruit from this plant is pretty unexceptional, and will barely do anything for you on its own. Perhaps there is some way to prepare the fruit with another ingredient..."))

        changes.addButton(ChangeButton(ItemSprite(Ankh()), "Revival Item Changes",
                "When the Dew Vial was initially added to Pixel Dungeon, its essentially free revive made ankhs pretty useless by comparison. " +
                        "To fix this, both items have been adjusted to combine to create a more powerful revive.\n\n" +
                        "Dew Vial nerfed:\n" +
                        "_-_ Still grants a full heal at full charge, but grants less healing at partial charge.\n" +
                        "_-_ No longer revives the player if they die.\n\n" +
                        "Ankh buffed:\n" +
                        "_-_ Can now be blessed with a full dew vial, to gain the vial's old revive effect."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN, null), "Misc Item Changes",
                "Sungrass buffed:\n" +
                        "_-_ Heal scaling now scales with max hp.\n\n" +
                        "Scroll of Psionic Blast rebalanced:\n" +
                        "_-_ Now deals less self damage, and damage is more consistent.\n" +
                        "_-_ Duration of self stun/blind effect increased.\n\n" +
                        "Scroll of lullaby reworked:\n" +
                        "_-_ No longer instantly sleeps enemies, now afflicts them with drowsy, which turns into magical sleep.\n" +
                        "_-_ Magically slept enemies will only wake up when attacked.\n" +
                        "_-_ Hero is also affected, and will be healed by magical sleep."))

        changes = ChangeInfo("v0.1.0", false, "")
        changes.hardlight(Window.TITLE_COLOR)
        infos.add(changes)

        changes.addButton(ChangeButton(Icons.get(Icons.SHPX), "Developer Commentary",
                "_-_ Released August 5th, 2014\n" +
                        "_-_ 69 days after Pixel Dungeon v1.7.1\n" +
                        "_-_ 9 days after v1.7.1 source release\n" +
                        "\n" +
                        "More dev commentary will be added here soon."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.SEED_EARTHROOT, null), "Seed Changes",
                "_-_ Blindweed buffed, now cripples as well as blinds.\n\n" +
                        "_-_ Sungrass nerfed, heal scales up over time, total heal reduced by combat.\n\n" +
                        "_-_ Earthroot nerfed, damage absorb down to 50% from 100%, total shield unchanged.\n\n" +
                        "_-_ Icecap buffed, freeze effect is now much stronger in water."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.POTION_SILVER, null), "Potion Changes",
                "_-_ Potion of Purity buffed, immunity duration increased to 10 turns from 5, clear effect radius increased.\n\n" + "_-_ Potion of Frost buffed, freeze effect is now much stronger in water."))

        changes.addButton(ChangeButton(ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN, null), "Scroll Changes",
                "_-_ Scroll of Psionic blast reworked, now rarer and much stronger, but deals damage to the hero.\n\n" + "_-_ Scroll of Challenge renamed to Scroll of Rage, now amoks nearby enemies."))

        val content = list.content()
        content.clear()

        var posY = 0f
        var nextPosY = 0f
        var second = false
        for (info in infos) {
            if (info.major) {
                posY = nextPosY
                second = false
                info.setRect(0f, posY, panel.innerWidth(), 0f)
                content.add(info)
                nextPosY = info.bottom()
                posY = nextPosY
            } else {
                if (!second) {
                    second = true
                    info.setRect(0f, posY, panel.innerWidth() / 2f, 0f)
                    content.add(info)
                    nextPosY = info.bottom()
                } else {
                    second = false
                    info.setRect(panel.innerWidth() / 2f, posY, panel.innerWidth() / 2f, 0f)
                    content.add(info)
                    nextPosY = Math.max(info.bottom(), nextPosY)
                    posY = nextPosY
                }
            }
        }


        content.setSize(panel.innerWidth(), Math.ceil(posY.toDouble()).toInt().toFloat())

        list.setRect(
                panel.x + panel.marginLeft(),
                panel.y + panel.marginTop() - 1,
                panel.innerWidth(),
                panel.innerHeight() + 2)
        list.scrollTo(0f, 0f)

        val archs = Archs()
        archs.setSize(Camera.main!!.width.toFloat(), Camera.main!!.height.toFloat())
        addToBack(archs)

        fadeIn()
    }

    override fun onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene::class.java)
    }

    private class ChangeInfo(title: String, val major: Boolean, text: String?) : Component() {

        protected var line: ColorBlock

        private var title: RenderedText? = null

        private var text: RenderedTextMultiline? = null

        private val buttons = ArrayList<ChangeButton>()

        init {

            if (major) {
                this.title = PixelScene.renderText(title, 9)
                line = ColorBlock(1f, 1f, -0xddddde)
                add(line)
            } else {
                this.title = PixelScene.renderText(title, 6)
                line = ColorBlock(1f, 1f, -0xcccccd)
                add(line)
            }

            add(this.title!!)

            if (text != null && text != "") {
                this.text = PixelScene.renderMultiline(text, 6)
                add(this.text!!)
            }

        }

        fun hardlight(color: Int) {
            title!!.hardlight(color)
        }

        fun addButton(button: ChangeButton) {
            buttons.add(button)
            add(button)

            button.setSize(16f, 16f)
            layout()
        }

        fun onClick(x: Float, y: Float): Boolean {
            for (button in buttons) {
                if (button.inside(x, y)) {
                    button.onClick()
                    return true
                }
            }
            return false
        }

        override fun layout() {
            var posY = this.y + 2
            if (major) posY += 2f

            title!!.x = x + (width - title!!.width()) / 2f
            title!!.y = posY
            PixelScene.align(title!!)
            posY += title!!.baseLine() + 2

            if (text != null) {
                text!!.maxWidth(width().toInt())
                text!!.setPos(x, posY)
                posY += text!!.height()
            }

            var posX = x
            var tallest = 0f
            for (change in buttons) {

                if (posX + change.width() >= right()) {
                    posX = x
                    posY += tallest
                    tallest = 0f
                }

                //centers
                if (posX == x) {
                    var offset = width
                    for (b in buttons) {
                        offset -= b.width()
                        if (offset <= 0) {
                            offset += b.width()
                            break
                        }
                    }
                    posX += offset / 2f
                }

                change.setPos(posX, posY)
                posX += change.width()
                if (tallest < change.height()) {
                    tallest = change.height()
                }
            }
            posY += tallest + 2

            height = posY - this.y

            if (major) {
                line.size(width(), 1f)
                line.x = x
                line.y = y + 2
            } else if (x == 0f) {
                line.size(1f, height())
                line.x = width
                line.y = y
            } else {
                line.size(1f, height())
                line.x = x
                line.y = y
            }
        }
    }

    //not actually a button, but functions as one.
    private class ChangeButton(protected var icon: Image, title: String, protected var message: String) : Component() {
        protected var title: String

        init {
            add(this.icon)

            this.title = Messages.titleCase(title)

            layout()
        }

        constructor(item: Item, message: String) : this(ItemSprite(item), item.name()!!, message) {}

        fun onClick() {
            Game.scene()!!.add(ChangesWindow(Image(icon), title, message))
        }

        override fun layout() {
            super.layout()

            icon.x = x + (width - icon.width) / 2f
            icon.y = y + (height - icon.height) / 2f
            PixelScene.align(icon)
        }
    }

    private class ChangesWindow(icon: Image, title: String, message: String) : WndTitledMessage(icon, title, message) {

        init {

            add(object : TouchArea(chrome) {
                override fun onClick(touch: Touchscreen.Touch) {
                    hide()
                }
            })

        }

    }
}
