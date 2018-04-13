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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FetidRat
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GreatCrab
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

class Ghost : NPC() {

    init {
        spriteClass = GhostSprite::class.java

        flying = true

        state = WANDERING
    }

    init {

        Sample.INSTANCE.load(Assets.SND_GHOST)
    }

    override fun act(): Boolean {
        if (Quest.processed())
            target = Dungeon.hero!!.pos
        return super.act()
    }

    override fun defenseSkill(enemy: Char): Int {
        return 1000
    }

    override fun speed(): Float {
        return if (Quest.processed()) 2f else 0.5f
    }

    override fun chooseEnemy(): Char? {
        return null
    }

    override fun damage(dmg: Int, src: Any) {}

    override fun add(buff: Buff) {}

    override fun reset(): Boolean {
        return true
    }

    override fun interact(): Boolean {
        sprite!!.turnTo(pos, Dungeon.hero!!.pos)

        Sample.INSTANCE.play(Assets.SND_GHOST)

        if (Quest.given) {
            if (Quest.weapon != null) {
                if (Quest.processed) {
                    GameScene.show(WndSadGhost(this, Quest.type))
                } else {
                    when (Quest.type) {
                        1 -> GameScene.show(WndQuest(this, Messages.get(this, "rat_2")))
                        2 -> GameScene.show(WndQuest(this, Messages.get(this, "gnoll_2")))
                        3 -> GameScene.show(WndQuest(this, Messages.get(this, "crab_2")))
                        else -> GameScene.show(WndQuest(this, Messages.get(this, "rat_2")))
                    }

                    var newPos = -1
                    for (i in 0..9) {
                        newPos = Dungeon.level!!.randomRespawnCell()
                        if (newPos != -1) {
                            break
                        }
                    }
                    if (newPos != -1) {

                        CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
                        pos = newPos
                        sprite!!.place(pos)
                        sprite!!.visible = Dungeon.level!!.heroFOV[pos]
                    }
                }
            }
        } else {
            val questBoss: Mob
            val txt_quest: String

            when (Quest.type) {
                1 -> {
                    questBoss = FetidRat()
                    txt_quest = Messages.get(this, "rat_1", Dungeon.hero!!.givenName())
                }
                2 -> {
                    questBoss = GnollTrickster()
                    txt_quest = Messages.get(this, "gnoll_1", Dungeon.hero!!.givenName())
                }
                3 -> {
                    questBoss = GreatCrab()
                    txt_quest = Messages.get(this, "crab_1", Dungeon.hero!!.givenName())
                }
                else -> {
                    questBoss = FetidRat()
                    txt_quest = Messages.get(this, "rat_1", Dungeon.hero!!.givenName())
                }
            }

            questBoss.pos = Dungeon.level!!.randomRespawnCell()

            if (questBoss.pos != -1) {
                GameScene.add(questBoss)
                GameScene.show(WndQuest(this, txt_quest))
                Quest.given = true
                Notes.add(Notes.Landmark.GHOST)
            }

        }

        return false
    }

    init {
        immunities.add(Paralysis::class.java)
        immunities.add(Roots::class.java)
    }

    object Quest {

        private var spawned: Boolean = false

        private var type: Int = 0

        private var given: Boolean = false
        private var processed: Boolean = false

        private var depth: Int = 0

        var weapon: Weapon? = null
        var armor: Armor? = null

        private val NODE = "sadGhost"

        private val SPAWNED = "spawned"
        private val TYPE = "type"
        private val GIVEN = "given"
        private val PROCESSED = "processed"
        private val DEPTH = "depth"
        private val WEAPON = "weapon"
        private val ARMOR = "armor"

        fun reset() {
            spawned = false

            weapon = null
            armor = null
        }

        fun storeInBundle(bundle: Bundle) {

            val node = Bundle()

            node.put(SPAWNED, spawned)

            if (spawned) {

                node.put(TYPE, type)

                node.put(GIVEN, given)
                node.put(DEPTH, depth)
                node.put(PROCESSED, processed)

                node.put(WEAPON, weapon)
                node.put(ARMOR, armor)
            }

            bundle.put(NODE, node)
        }

        fun restoreFromBundle(bundle: Bundle) {

            val node = bundle.getBundle(NODE)

            if (!node.isNull && (spawned = node.getBoolean(SPAWNED))) {

                type = node.getInt(TYPE)
                given = node.getBoolean(GIVEN)
                processed = node.getBoolean(PROCESSED)

                depth = node.getInt(DEPTH)

                weapon = node.get(WEAPON) as Weapon
                armor = node.get(ARMOR) as Armor
            } else {
                reset()
            }
        }

        fun spawn(level: SewerLevel) {
            if (!spawned && Dungeon.depth > 1 && Random.Int(5 - Dungeon.depth) == 0) {

                val ghost = Ghost()
                do {
                    ghost.pos = level.randomRespawnCell()
                } while (ghost.pos == -1)
                level.mobs.add(ghost)

                spawned = true
                //dungeon depth determines type of quest.
                //depth2=fetid rat, 3=gnoll trickster, 4=great crab
                type = Dungeon.depth - 1

                given = false
                processed = false
                depth = Dungeon.depth

                //50%:tier2, 30%:tier3, 15%:tier4, 5%:tier5
                val itemTierRoll = Random.Float()
                val wepTier: Int

                if (itemTierRoll < 0.5f) {
                    wepTier = 2
                    armor = LeatherArmor()
                } else if (itemTierRoll < 0.8f) {
                    wepTier = 3
                    armor = MailArmor()
                } else if (itemTierRoll < 0.95f) {
                    wepTier = 4
                    armor = ScaleArmor()
                } else {
                    wepTier = 5
                    armor = PlateArmor()
                }

                try {
                    do {
                        weapon = Generator.wepTiers[wepTier - 1].classes[Random.chances(Generator.wepTiers[wepTier - 1].probs)].newInstance() as Weapon
                    } while (weapon !is MeleeWeapon)
                } catch (e: Exception) {
                    ShatteredPixelDungeon.reportException(e)
                    weapon = Shortsword()
                }

                //50%:+0, 30%:+1, 15%:+2, 5%:+3
                val itemLevelRoll = Random.Float()
                val itemLevel: Int
                if (itemLevelRoll < 0.5f) {
                    itemLevel = 0
                } else if (itemLevelRoll < 0.8f) {
                    itemLevel = 1
                } else if (itemLevelRoll < 0.95f) {
                    itemLevel = 2
                } else {
                    itemLevel = 3
                }
                weapon!!.upgrade(itemLevel)
                armor!!.upgrade(itemLevel)

                //10% to be enchanted
                if (Random.Int(10) == 0) {
                    weapon!!.enchant()
                    armor!!.inscribe()
                }

            }
        }

        fun process() {
            if (spawned && given && !processed && depth == Dungeon.depth) {
                GLog.n(Messages.get(Ghost::class.java, "find_me"))
                Sample.INSTANCE.play(Assets.SND_GHOST)
                processed = true
            }
        }

        fun complete() {
            weapon = null
            armor = null

            Notes.remove(Notes.Landmark.GHOST)
        }

        fun processed(): Boolean {
            return spawned && processed
        }

        fun completed(): Boolean {
            return processed() && weapon == null && armor == null
        }
    }
}
