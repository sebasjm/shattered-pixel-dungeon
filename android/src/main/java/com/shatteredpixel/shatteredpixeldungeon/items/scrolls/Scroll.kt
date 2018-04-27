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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

abstract class Scroll : Item() {

    protected var initials: Int = 0

    private var rune: String? = null

    var ownedByBook = false

    val isKnown: Boolean
        get() = handler!!.isKnown(this)

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = isKnown

    init {
        stackable = true
        defaultAction = AC_READ
    }

    init {
        reset()
    }

    override fun reset() {
        super.reset()
        if (handler != null) {
            image = handler!!.image(this)
            rune = handler!!.label(this)
        }
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_READ)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_READ) {

            if (hero.buff<Blindness>(Blindness::class.java) != null) {
                GLog.w(Messages.get(this.javaClass, "blinded"))
            } else if (hero.buff<UnstableSpellbook.bookRecharge>(UnstableSpellbook.bookRecharge::class.java) != null
                    && hero.buff<UnstableSpellbook.bookRecharge>(UnstableSpellbook.bookRecharge::class.java)!!.isCursed
                    && this !is ScrollOfRemoveCurse) {
                GLog.n(Messages.get(this.javaClass, "cursed"))
            } else {
                Item.curUser = hero
                Item.curItem = detach(hero.belongings.backpack)
                doRead()
            }

        }
    }

    abstract fun doRead()

    //currently only used in scrolls owned by the unstable spellbook
    abstract fun empoweredRead()

    protected fun readAnimation() {
        Item.curUser!!.spend(TIME_TO_READ)
        Item.curUser!!.busy()
        (Item.curUser!!.sprite as HeroSprite).read()
    }

    fun setKnown() {
        if (!ownedByBook) {
            if (!isKnown) {
                handler!!.know(this)
                updateQuickslot()
            }

            if (Dungeon.hero!!.isAlive) {
                Catalog.setSeen(javaClass)
            }
        }
    }

    override fun identify(): Item {
        setKnown()
        return super.identify()
    }

    override fun name(): String {
        return if (isKnown) name!! else Messages.get(Scroll::class.java, rune!!)
    }

    override fun info(): String {
        return if (isKnown)
            desc()
        else
            Messages.get(this.javaClass, "unknown_desc")
    }

    fun initials(): Int? {
        return if (isKnown) initials else null
    }

    override fun price(): Int {
        return 30 * quantity
    }

    companion object {

        val AC_READ = "READ"

        @JvmStatic protected val TIME_TO_READ = 1f

        private val scrolls = arrayOf<Class<*>>(ScrollOfIdentify::class.java, ScrollOfMagicMapping::class.java, ScrollOfRecharging::class.java, ScrollOfRemoveCurse::class.java, ScrollOfTeleportation::class.java, ScrollOfUpgrade::class.java, ScrollOfRage::class.java, ScrollOfTerror::class.java, ScrollOfLullaby::class.java, ScrollOfMagicalInfusion::class.java, ScrollOfPsionicBlast::class.java, ScrollOfMirrorImage::class.java)

        private val runes = object : HashMap<String, Int>() {
            init {
                put("KAUNAN", ItemSpriteSheet.SCROLL_KAUNAN)
                put("SOWILO", ItemSpriteSheet.SCROLL_SOWILO)
                put("LAGUZ", ItemSpriteSheet.SCROLL_LAGUZ)
                put("YNGVI", ItemSpriteSheet.SCROLL_YNGVI)
                put("GYFU", ItemSpriteSheet.SCROLL_GYFU)
                put("RAIDO", ItemSpriteSheet.SCROLL_RAIDO)
                put("ISAZ", ItemSpriteSheet.SCROLL_ISAZ)
                put("MANNAZ", ItemSpriteSheet.SCROLL_MANNAZ)
                put("NAUDIZ", ItemSpriteSheet.SCROLL_NAUDIZ)
                put("BERKANAN", ItemSpriteSheet.SCROLL_BERKANAN)
                put("ODAL", ItemSpriteSheet.SCROLL_ODAL)
                put("TIWAZ", ItemSpriteSheet.SCROLL_TIWAZ)
            }
        }

        private var handler: ItemStatusHandler<Scroll>? = null

        fun initLabels() {
            handler = ItemStatusHandler(scrolls as Array<Class<out Scroll>>, runes)
        }

        fun save(bundle: Bundle) {
            handler!!.save(bundle)
        }

        fun saveSelectively(bundle: Bundle, items: ArrayList<Item>) {
            handler!!.saveSelectively(bundle, items)
        }

        fun restore(bundle: Bundle) {
            handler = ItemStatusHandler(scrolls as Array<Class<out Scroll>>, runes, bundle)
        }

        val known: HashSet<Class<out Scroll>>?
            get() = handler!!.known()

        val unknown: HashSet<Class<out Scroll>>
            get() = handler!!.unknown()

        fun allKnown(): Boolean {
            return handler!!.known()!!.size == scrolls.size
        }
    }
}
