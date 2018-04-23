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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MassGraveRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RotGardenRoom
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RitualSiteRoom
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest
import com.shatteredpixel.shatteredpixeldungeon.windows.WndWandmaker
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class Wandmaker : NPC() {

    init {
        spriteClass = WandmakerSprite::class.java

        properties.add(Char.Property.IMMOVABLE)
    }

    override fun act(): Boolean {
        throwItem()
        return super.act()
    }

    override fun defenseSkill(enemy: Char?): Int {
        return 1000
    }

    override fun damage(dmg: Int, src: Any) {}

    override fun add(buff: Buff) {}

    override fun reset(): Boolean {
        return true
    }

    override fun interact(): Boolean {

        sprite!!.turnTo(pos, Dungeon.hero!!.pos)
        if (Quest.given) {

            val item: Item?
            when (Quest.type) {
                1 -> item = Dungeon.hero!!.belongings.getItem<CorpseDust>(CorpseDust::class.java)
                2 -> item = Dungeon.hero!!.belongings.getItem<Embers>(Embers::class.java)
                3 -> item = Dungeon.hero!!.belongings.getItem<Rotberry.Seed>(Rotberry.Seed::class.java)
                else -> item = Dungeon.hero!!.belongings.getItem<CorpseDust>(CorpseDust::class.java)
            }

            if (item != null) {
                GameScene.show(WndWandmaker(this, item))
            } else {
                var msg = ""
                when (Quest.type) {
                    1 -> msg = Messages.get(this.javaClass, "reminder_dust", Dungeon.hero!!.givenName())
                    2 -> msg = Messages.get(this.javaClass, "reminder_ember", Dungeon.hero!!.givenName())
                    3 -> msg = Messages.get(this.javaClass, "reminder_berry", Dungeon.hero!!.givenName())
                }
                GameScene.show(WndQuest(this, msg))
            }

        } else {

            var msg1 = ""
            var msg2 = ""
            when (Dungeon.hero!!.heroClass) {
                HeroClass.WARRIOR -> msg1 += Messages.get(this.javaClass, "intro_warrior")
                HeroClass.ROGUE -> msg1 += Messages.get(this.javaClass, "intro_rogue")
                HeroClass.MAGE -> msg1 += Messages.get(this.javaClass, "intro_mage", Dungeon.hero!!.givenName())
                HeroClass.HUNTRESS -> msg1 += Messages.get(this.javaClass, "intro_huntress")
            }

            msg1 += Messages.get(this.javaClass, "intro_1")

            when (Quest.type) {
                1 -> msg2 += Messages.get(this.javaClass, "intro_dust")
                2 -> msg2 += Messages.get(this.javaClass, "intro_ember")
                3 -> msg2 += Messages.get(this.javaClass, "intro_berry")
            }

            msg2 += Messages.get(this.javaClass, "intro_2")
            val msg2final = msg2
            val wandmaker = this

            GameScene.show(object : WndQuest(wandmaker, msg1) {
                override fun hide() {
                    super.hide()
                    GameScene.show(WndQuest(wandmaker, msg2final))
                }
            })

            Notes.add(Notes.Landmark.WANDMAKER)
            Quest.given = true
        }

        return false
    }

    object Quest {

        var type: Int = 0
        // 1 = corpse dust quest
        // 2 = elemental embers quest
        // 3 = rotberry quest

        var spawned: Boolean = false

        var given: Boolean = false

        var wand1: Wand? = null
        var wand2: Wand? = null

        private val NODE = "wandmaker"

        private val SPAWNED = "spawned"
        private val TYPE = "type"
        private val GIVEN = "given"
        private val WAND1 = "wand1"
        private val WAND2 = "wand2"

        private val RITUALPOS = "ritualpos"

        private var questRoomSpawned: Boolean = false

        fun reset() {
            spawned = false
            type = 0

            wand1 = null
            wand2 = null
        }

        fun storeInBundle(bundle: Bundle) {

            val node = Bundle()

            node.put(SPAWNED, spawned)

            if (spawned) {

                node.put(TYPE, type)

                node.put(GIVEN, given)

                node.put(WAND1, wand1)
                node.put(WAND2, wand2)

                if (type == 2) {
                    node.put(RITUALPOS, CeremonialCandle.ritualPos)
                }

            }

            bundle.put(NODE, node)
        }

        fun restoreFromBundle(bundle: Bundle) {

            val node = bundle.getBundle(NODE)

            if (!node.isNull) {
                spawned = node.getBoolean(SPAWNED)
                if (spawned) {
                    type = node.getInt(TYPE)

                    given = node.getBoolean(GIVEN)

                    wand1 = node.get(WAND1) as Wand?
                    wand2 = node.get(WAND2) as Wand?

                    if (type == 2) {
                        CeremonialCandle.ritualPos = node.getInt(RITUALPOS)
                    }

                } else {
                    reset()
                }
            }
        }

        fun spawnWandmaker(level: Level, room: Room) {
            if (questRoomSpawned) {

                questRoomSpawned = false

                val npc = Wandmaker()
                do {
                    npc.pos = level.pointToCell(room.random())
                } while (npc.pos == level.entrance)
                level.mobs.add(npc)

                spawned = true

                given = false
                wand1 = Generator.random(Generator.Category.WAND) as Wand
                wand1!!.cursed = false
                wand1!!.upgrade()

                do {
                    wand2 = Generator.random(Generator.Category.WAND) as Wand
                } while (wand2!!.javaClass == wand1!!.javaClass)
                wand2!!.cursed = false
                wand2!!.upgrade()

            }
        }

        fun spawnRoom(rooms: ArrayList<Room>): ArrayList<Room> {
            questRoomSpawned = false
            if (!spawned && (type != 0 || Dungeon.depth > 6 && Random.Int(10 - Dungeon.depth) == 0)) {

                // decide between 1,2, or 3 for quest type.
                if (type == 0) type = Random.Int(3) + 1

                when (type) {
                    1 -> rooms.add(MassGraveRoom())
                    2 -> rooms.add(RitualSiteRoom())
                    3 -> rooms.add(RotGardenRoom())
                    else -> rooms.add(MassGraveRoom())
                }

                questRoomSpawned = true

            }
            return rooms
        }

        fun complete() {
            wand1 = null
            wand2 = null

            Notes.remove(Notes.Landmark.WANDMAKER)
        }
    }
}
