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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BlacksmithRoom
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBlacksmith
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

class Blacksmith : NPC() {

    init {
        spriteClass = BlacksmithSprite::class.java

        properties.add(Char.Property.IMMOVABLE)
    }

    override fun act(): Boolean {
        throwItem()
        return super.act()
    }

    override fun interact(): Boolean {

        sprite!!.turnTo(pos, Dungeon.hero!!.pos)

        if (!Quest.given) {

            GameScene.show(object : WndQuest(this@Blacksmith,
                    if (Quest.alternative) Messages.get(this.javaClass, "blood_1") else Messages.get(this.javaClass, "gold_1")) {

                override fun onBackPressed() {
                    super.onBackPressed()

                    Quest.given = true
                    Quest.completed = false

                    val pick = Pickaxe()
                    if (pick.doPickUp(Dungeon.hero!!)) {
                        GLog.i(Messages.get(Dungeon.hero!!.javaClass, "you_now_have", pick.name()))
                    } else {
                        Dungeon.level!!.drop(pick, Dungeon.hero!!.pos).sprite!!.drop()
                    }
                }
            })

            Notes.add(Notes.Landmark.TROLL)

        } else if (!Quest.completed) {
            if (Quest.alternative) {

                val pick = Dungeon.hero!!.belongings.getItem<Pickaxe>(Pickaxe::class.java)
                if (pick == null) {
                    tell(Messages.get(this.javaClass, "lost_pick"))
                } else if (!pick.bloodStained) {
                    tell(Messages.get(this.javaClass, "blood_2"))
                } else {
                    if (pick.isEquipped(Dungeon.hero!!)) {
                        pick.doUnequip(Dungeon.hero!!, false)
                    }
                    pick.detach(Dungeon.hero!!.belongings.backpack)
                    tell(Messages.get(this.javaClass, "completed"))

                    Quest.completed = true
                    Quest.reforged = false
                }

            } else {

                val pick = Dungeon.hero!!.belongings.getItem<Pickaxe>(Pickaxe::class.java)
                val gold = Dungeon.hero!!.belongings.getItem<DarkGold>(DarkGold::class.java)
                if (pick == null) {
                    tell(Messages.get(this.javaClass, "lost_pick"))
                } else if (gold == null || gold.quantity() < 15) {
                    tell(Messages.get(this.javaClass, "gold_2"))
                } else {
                    if (pick.isEquipped(Dungeon.hero!!)) {
                        pick.doUnequip(Dungeon.hero!!, false)
                    }
                    pick.detach(Dungeon.hero!!.belongings.backpack)
                    gold.detachAll(Dungeon.hero!!.belongings.backpack)
                    tell(Messages.get(this.javaClass, "completed"))

                    Quest.completed = true
                    Quest.reforged = false
                }

            }
        } else if (!Quest.reforged) {

            GameScene.show(WndBlacksmith(this, Dungeon.hero!!))

        } else {

            tell(Messages.get(this.javaClass, "get_lost"))

        }

        return false
    }

    private fun tell(text: String) {
        GameScene.show(WndQuest(this, text))
    }

    override fun defenseSkill(enemy: Char?): Int {
        return 1000
    }

    override fun damage(dmg: Int, src: Any) {}

    override fun add(buff: Buff) {}

    override fun reset(): Boolean {
        return true
    }

    object Quest {

        var spawned: Boolean = false

        var alternative: Boolean = false
        var given: Boolean = false
        var completed: Boolean = false
        var reforged: Boolean = false

        private val NODE = "blacksmith"

        private val SPAWNED = "spawned"
        private val ALTERNATIVE = "alternative"
        private val GIVEN = "given"
        private val COMPLETED = "completed"
        private val REFORGED = "reforged"

        fun reset() {
            spawned = false
            given = false
            completed = false
            reforged = false
        }

        fun storeInBundle(bundle: Bundle) {

            val node = Bundle()

            node.put(SPAWNED, spawned)

            if (spawned) {
                node.put(ALTERNATIVE, alternative)
                node.put(GIVEN, given)
                node.put(COMPLETED, completed)
                node.put(REFORGED, reforged)
            }

            bundle.put(NODE, node)
        }

        fun restoreFromBundle(bundle: Bundle) {

            val node = bundle.getBundle(NODE)

            if (!node.isNull) {
                spawned = node.getBoolean(SPAWNED)
                if (spawned) {
                    alternative = node.getBoolean(ALTERNATIVE)
                    given = node.getBoolean(GIVEN)
                    completed = node.getBoolean(COMPLETED)
                    reforged = node.getBoolean(REFORGED)
                }
            } else {
                reset()
            }
        }

        fun spawn(rooms: ArrayList<Room>): ArrayList<Room> {
            if (!spawned && Dungeon.depth > 11 && Random.Int(15 - Dungeon.depth) == 0) {

                rooms.add(BlacksmithRoom())
                spawned = true
                alternative = Random.Int(2) == 0

                given = false

            }
            return rooms
        }
    }

    companion object {

        fun verify(item1: Item, item2: Item): String? {

            if (item1 === item2) {
                return Messages.get(Blacksmith::class.java, "same_item")
            }

            if (item1.javaClass != item2.javaClass) {
                return Messages.get(Blacksmith::class.java, "diff_type")
            }

            if (!item1.isIdentified || !item2.isIdentified) {
                return Messages.get(Blacksmith::class.java, "un_ided")
            }

            if (item1.cursed || item2.cursed) {
                return Messages.get(Blacksmith::class.java, "cursed")
            }

            if (item1.level() < 0 || item2.level() < 0) {
                return Messages.get(Blacksmith::class.java, "degraded")
            }

            return if (!item1.isUpgradable || !item2.isUpgradable) {
                Messages.get(Blacksmith::class.java, "cant_reforge")
            } else null

        }

        fun upgrade(item1: Item, item2: Item) {

            val first: Item
            val second: Item
            if (item2.level() > item1.level()) {
                first = item2
                second = item1
            } else {
                first = item1
                second = item2
            }

            Sample.INSTANCE.play(Assets.SND_EVOKE)
            ScrollOfUpgrade.upgrade(Dungeon.hero!!)
            Item.evoke(Dungeon.hero!!)

            if (first.isEquipped(Dungeon.hero!!)) {
                (first as EquipableItem).doUnequip(Dungeon.hero!!, true)
            }
            first.level(first.level() + 1) //prevents on-upgrade effects like enchant/glyph removal
            Dungeon.hero!!.spendAndNext(2f)
            Badges.validateItemLevelAquired(first)

            if (second.isEquipped(Dungeon.hero!!)) {
                (second as EquipableItem).doUnequip(Dungeon.hero!!, false)
            }
            second.detachAll(Dungeon.hero!!.belongings.backpack)

            if (second is Armor) {
                val seal = second.checkSeal()
                if (seal != null) {
                    Dungeon.level!!.drop(seal, Dungeon.hero!!.pos)
                }
            }

            Quest.reforged = true

            Notes.remove(Notes.Landmark.TROLL)
        }
    }
}
