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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Callback

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

open class Item : Bundlable {

    var defaultAction: String? = null
    var usesTargeting: Boolean = false

    protected var name = Messages.get(this, "name")
    var image = 0

    var stackable = false
    var quantity = 1

    private var level = 0

    var levelKnown = false

    var cursed: Boolean = false
    var cursedKnown: Boolean = false

    // Unique items persist through revival
    var unique = false

    // whether an item can be included in heroes remains
    var bones = false

    open val isUpgradable: Boolean
        get() = true

    open val isIdentified: Boolean
        get() = levelKnown && cursedKnown

    open fun actions(hero: Hero): ArrayList<String> {
        val actions = ArrayList<String>()
        actions.add(AC_DROP)
        actions.add(AC_THROW)
        return actions
    }

    open fun doPickUp(hero: Hero): Boolean {
        if (collect(hero.belongings.backpack)) {

            GameScene.pickUp(this, hero.pos)
            Sample.INSTANCE.play(Assets.SND_ITEM)
            hero.spendAndNext(TIME_TO_PICK_UP)
            return true

        } else {
            return false
        }
    }

    open fun doDrop(hero: Hero) {
        hero.spendAndNext(TIME_TO_DROP)
        Dungeon.level!!.drop(detachAll(hero.belongings.backpack), hero.pos).sprite!!.drop(hero.pos)
    }

    //resets an item's properties, to ensure consistency between runs
    open fun reset() {
        //resets the name incase the language has changed.
        name = Messages.get(this, "name")
    }

    open fun doThrow(hero: Hero) {
        GameScene.selectCell(thrower)
    }

    open fun execute(hero: Hero, action: String?) {

        curUser = hero
        curItem = this

        val combo = hero.buff<Combo>(Combo::class.java)
        combo?.detach()

        if (action == AC_DROP) {

            doDrop(hero)

        } else if (action == AC_THROW) {

            doThrow(hero)

        }
    }

    fun execute(hero: Hero) {
        execute(hero, defaultAction)
    }

    protected open fun onThrow(cell: Int) {
        val heap = Dungeon.level!!.drop(this, cell)
        if (!heap.isEmpty) {
            heap.sprite!!.drop(cell)
        }
    }

    //takes two items and merges them (if possible)
    open fun merge(other: Item): Item {
        if (isSimilar(other)) {
            quantity += other.quantity
            other.quantity = 0
        }
        return this
    }

    open fun collect(container: Bag): Boolean {

        val items = container.items

        if (items.contains(this)) {
            return true
        }

        for (item in items) {
            if (item is Bag && item.grab(this)) {
                return collect(item)
            }
        }

        if (stackable) {
            for (item in items) {
                if (isSimilar(item)) {
                    item.merge(this)
                    item.updateQuickslot()
                    return true
                }
            }
        }

        if (items.size < container.size) {

            if (Dungeon.hero != null && Dungeon.hero!!.isAlive) {
                Badges.validateItemLevelAquired(this)
            }

            items.add(this)
            Dungeon.quickslot.replacePlaceholder(this)
            updateQuickslot()
            Collections.sort(items, itemComparator)
            return true

        } else {

            GLog.n(Messages.get(Item::class.java, "pack_full", name()))
            return false

        }
    }

    fun collect(): Boolean {
        return collect(Dungeon.hero!!.belongings.backpack)
    }

    //returns a new item if the split was sucessful and there are now 2 items, otherwise null
    open fun split(amount: Int): Item? {
        return if (amount <= 0 || amount >= quantity()) {
            null
        } else {
            try {

                //pssh, who needs copy constructors?
                val split = javaClass.newInstance()
                val copy = Bundle()
                this.storeInBundle(copy)
                split.restoreFromBundle(copy)
                split.quantity(amount)
                quantity -= amount

                split
            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
                null
            }

        }
    }

    fun detach(container: Bag): Item? {

        if (quantity <= 0) {

            return null

        } else if (quantity == 1) {

            if (stackable || this is Boomerang) {
                Dungeon.quickslot.convertToPlaceholder(this)
            }

            return detachAll(container)

        } else {


            val detached = split(1)
            updateQuickslot()
            detached?.onDetach()
            return detached

        }
    }

    fun detachAll(container: Bag): Item {
        Dungeon.quickslot.clearItem(this)
        updateQuickslot()

        for (item in container.items) {
            if (item === this) {
                container.items.remove(this)
                item.onDetach()
                return this
            } else if (item is Bag) {
                val bag = item
                if (bag.contains(this)) {
                    return detachAll(bag)
                }
            }
        }

        return this
    }

    open fun isSimilar(item: Item): Boolean {
        return javaClass == item.javaClass
    }

    protected open fun onDetach() {}

    fun level(): Int {
        return level
    }

    open fun level(value: Int) {
        level = value

        updateQuickslot()
    }

    open fun upgrade(): Item {

        this.level++

        updateQuickslot()

        return this
    }

    fun upgrade(n: Int): Item {
        for (i in 0 until n) {
            upgrade()
        }

        return this
    }

    open fun degrade(): Item {

        this.level--

        return this
    }

    fun degrade(n: Int): Item {
        for (i in 0 until n) {
            degrade()
        }

        return this
    }

    open fun visiblyUpgraded(): Int {
        return if (levelKnown) level else 0
    }

    fun visiblyCursed(): Boolean {
        return cursed && cursedKnown
    }

    open fun isEquipped(hero: Hero): Boolean {
        return false
    }

    open fun identify(): Item {

        levelKnown = true
        cursedKnown = true

        if (Dungeon.hero != null && Dungeon.hero!!.isAlive) {
            Catalog.setSeen(javaClass)
        }

        return this
    }

    override fun toString(): String {

        var name = name()

        if (visiblyUpgraded() != 0)
            name = Messages.format(TXT_TO_STRING_LVL, name, visiblyUpgraded())

        if (quantity > 1)
            name = Messages.format(TXT_TO_STRING_X, name, quantity)

        return name

    }

    open fun name(): String {
        return name
    }

    fun trueName(): String {
        return name
    }

    open fun image(): Int {
        return image
    }

    open fun glowing(): ItemSprite.Glowing? {
        return null
    }

    open fun emitter(): Emitter? {
        return null
    }

    open fun info(): String {
        return desc()
    }

    open fun desc(): String {
        return Messages.get(this, "desc")
    }

    fun quantity(): Int {
        return quantity
    }

    open fun quantity(value: Int): Item {
        quantity = value
        return this
    }

    open fun price(): Int {
        return 0
    }

    open fun random(): Item {
        return this
    }

    open fun status(): String? {
        return if (quantity != 1) Integer.toString(quantity) else null
    }

    fun updateQuickslot() {
        QuickSlotButton.refresh()
    }

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(QUANTITY, quantity)
        bundle.put(LEVEL, level)
        bundle.put(LEVEL_KNOWN, levelKnown)
        bundle.put(CURSED, cursed)
        bundle.put(CURSED_KNOWN, cursedKnown)
        if (Dungeon.quickslot.contains(this)) {
            bundle.put(QUICKSLOT, Dungeon.quickslot.getSlot(this))
        }
    }

    override fun restoreFromBundle(bundle: Bundle) {
        quantity = bundle.getInt(QUANTITY)
        levelKnown = bundle.getBoolean(LEVEL_KNOWN)
        cursedKnown = bundle.getBoolean(CURSED_KNOWN)

        val level = bundle.getInt(LEVEL)
        if (level > 0) {
            upgrade(level)
        } else if (level < 0) {
            degrade(-level)
        }

        cursed = bundle.getBoolean(CURSED)

        //only want to populate slot on first load.
        if (Dungeon.hero == null) {
            if (bundle.contains(QUICKSLOT)) {
                Dungeon.quickslot.setSlot(bundle.getInt(QUICKSLOT), this)
            }
        }
    }

    open fun throwPos(user: Hero?, dst: Int): Int {
        return Ballistica(user!!.pos, dst, Ballistica.PROJECTILE).collisionPos!!
    }

    open fun cast(user: Hero?, dst: Int) {

        val cell = throwPos(user, dst)
        user!!.sprite!!.zap(cell)
        user.busy()

        Sample.INSTANCE.play(Assets.SND_MISS, 0.6f, 0.6f, 1.5f)

        val enemy = Actor.findChar(cell)
        QuickSlotButton.target(enemy)

        val delay = castDelay(user, dst)

        if (enemy != null) {
            (user.sprite!!.parent!!.recycle(MissileSprite::class.java) as MissileSprite).reset(user.sprite,
                    enemy.sprite,
                    this
            ) {
                this@Item.detach(user.belongings.backpack)!!.onThrow(cell)
                user.spendAndNext(delay)
            }
        } else {
            (user.sprite!!.parent!!.recycle(MissileSprite::class.java) as MissileSprite).reset(user.sprite,
                    cell,
                    this
            ) {
                this@Item.detach(user.belongings.backpack)!!.onThrow(cell)
                user.spendAndNext(delay)
            }
        }
    }

    open fun castDelay(user: Char, dst: Int): Float {
        return TIME_TO_THROW
    }

    companion object {

        protected val TXT_TO_STRING_LVL = "%s %+d"
        protected val TXT_TO_STRING_X = "%s x%d"

        protected val TIME_TO_THROW = 1.0f
        protected val TIME_TO_PICK_UP = 1.0f
        protected val TIME_TO_DROP = 0.5f

        val AC_DROP = "DROP"
        val AC_THROW = "THROW"

        private val itemComparator = Comparator<Item> { lhs, rhs -> Generator.Category.order(lhs) - Generator.Category.order(rhs) }

        fun evoke(hero: Hero) {
            hero.sprite!!.emitter().burst(Speck.factory(Speck.EVOKE), 5)
        }

        fun virtual(cl: Class<out Item>): Item? {
            try {

                val item = cl.newInstance() as Item
                item.quantity = 0
                return item

            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
                return null
            }

        }

        private val QUANTITY = "quantity"
        private val LEVEL = "level"
        private val LEVEL_KNOWN = "levelKnown"
        private val CURSED = "cursed"
        private val CURSED_KNOWN = "cursedKnown"
        private val QUICKSLOT = "quickslotpos"

        protected var curUser: Hero? = null
        protected var curItem: Item? = null
        protected var thrower: CellSelector.Listener = object : CellSelector.Listener {
            override fun onSelect(target: Int?) {
                if (target != null) {
                    curItem!!.cast(curUser, target)
                }
            }

            override fun prompt(): String {
                return Messages.get(Item::class.java, "prompt")
            }
        }
    }
}
