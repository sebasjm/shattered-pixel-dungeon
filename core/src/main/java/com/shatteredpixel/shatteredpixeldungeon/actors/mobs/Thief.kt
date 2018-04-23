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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Gold
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThiefSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random

open class Thief : Mob() {

    var item: Item? = null

    init {
        spriteClass = ThiefSprite::class.java

        HT = 20
        HP = HT
        defenseSkill = 12

        EXP = 5
        maxLvl = 10

        //see createloot
        loot = null
        lootChance = 0.01f

        WANDERING = Wandering()
        FLEEING = Fleeing()

        properties.add(Char.Property.UNDEAD)
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ITEM, item)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        item = bundle.get(ITEM) as Item?
    }

    override fun speed(): Float {
        return if (item != null)
            5 * super.speed() / 6
        else
            super.speed()
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(1, 10)
    }

    override fun attackDelay(): Float {
        return 0.5f
    }

    override fun rollToDropLoot() {
        if (item != null) {
            Dungeon.level!!.drop(item, pos).sprite!!.drop()
            //updates position
            if (item is Honeypot.ShatteredPot) (item as Honeypot.ShatteredPot).setHolder(this)
            item = null
        }
        super.rollToDropLoot()
    }

    override fun createLoot(): Item? {
        if (!Dungeon.LimitedDrops.THIEVES_ARMBAND.dropped()) {
            Dungeon.LimitedDrops.THIEVES_ARMBAND.drop()
            return MasterThievesArmband().identify()
        } else
            return Gold(Random.NormalIntRange(100, 250))
    }

    override fun attackSkill(target: Char?): Int {
        return 12
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 3)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)

        if (item == null && enemy is Hero && steal(enemy)) {
            state = FLEEING
        }

        return damage
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {
        if (state === FLEEING) {
            Dungeon.level!!.drop(Gold(), pos).sprite!!.drop()
        }

        return super.defenseProc(enemy, damage)
    }

    protected open fun steal(hero: Hero): Boolean {

        val item = hero.belongings.randomUnequipped()

        if (item != null && !item.unique && item.level() < 1) {

            GLog.w(Messages.get(Thief::class.java, "stole", item.name()))
            if (!item.stackable || hero.belongings.getSimilar(item) == null) {
                Dungeon.quickslot.convertToPlaceholder(item)
            }
            item.updateQuickslot()

            if (item is Honeypot) {
                this.item = item.shatter(this, this.pos)
                item.detach(hero.belongings.backpack)
            } else {
                this.item = item.detach(hero.belongings.backpack)
                if (item is Honeypot.ShatteredPot)
                    item.setHolder(this)
            }

            return true
        } else {
            return false
        }
    }

    override fun description(): String {
        var desc = super.description()

        if (item != null) {
            desc += Messages.get(this.javaClass, "carries", item!!.name())
        }

        return desc
    }

    private inner class Wandering : Mob.Wandering() {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            super.act(enemyInFOV, justAlerted)

            //if an enemy is just noticed and the thief posses an item, run, don't fight.
            if (state === HUNTING && item != null) {
                state = FLEEING
            }

            return true
        }
    }

    private inner class Fleeing : Mob.Fleeing() {
        override fun nowhereToRun() {
            if (buff<Terror>(Terror::class.java) == null && buff<Corruption>(Corruption::class.java) == null) {
                if (enemySeen) {
                    sprite!!.showStatus(CharSprite.NEGATIVE, Messages.get(Mob::class.java, "rage"))
                    state = HUNTING
                } else if (item != null && !Dungeon.level!!.heroFOV[pos]) {

                    var count = 32
                    var newPos: Int
                    do {
                        newPos = Dungeon.level!!.randomRespawnCell()
                        if (count-- <= 0) {
                            break
                        }
                    } while (newPos == -1 || Dungeon.level!!.heroFOV[newPos] || Dungeon.level!!.distance(newPos, pos) < count / 3)

                    if (newPos != -1) {

                        if (Dungeon.level!!.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6)
                        pos = newPos
                        sprite!!.place(pos)
                        sprite!!.visible = Dungeon.level!!.heroFOV[pos]
                        if (Dungeon.level!!.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6)

                    }

                    if (item != null) GLog.n(Messages.get(Thief::class.java, "escapes", item!!.name()))
                    item = null
                    state = WANDERING
                } else {
                    state = WANDERING
                }
            } else {
                super.nowhereToRun()
            }
        }
    }

    companion object {

        private val ITEM = "item"
    }
}
