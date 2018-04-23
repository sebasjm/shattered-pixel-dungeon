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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Collections

class UnstableSpellbook : Artifact() {

    private val scrolls = ArrayList<Class<*>>()

    protected var mode: WndBag.Mode = WndBag.Mode.SCROLL

    protected var itemSelector: WndBag.Listener = object : WndBag.Listener  {
        override fun onSelect(item: Item?) {
            if (item != null && item is Scroll && item.isIdentified) {
                val hero = Dungeon.hero!!
                var i = 0
                while (i <= 1 && i < scrolls.size) {
                    if (scrolls[i] == item.javaClass) {
                        hero!!.sprite!!.operate(hero.pos)
                        hero.busy()
                        hero.spend(2f)
                        Sample.INSTANCE.play(Assets.SND_BURNING)
                        hero.sprite!!.emitter().burst(ElmoParticle.FACTORY, 12)

                        scrolls.removeAt(i)
                        item.detach(hero.belongings.backpack)

                        upgrade()
                        GLog.i(Messages.get(UnstableSpellbook::class.java, "infuse_scroll"))
                        return
                    }
                    i++
                }
                GLog.w(Messages.get(UnstableSpellbook::class.java, "unable_scroll"))
            } else if (item is Scroll && !item.isIdentified)
                GLog.w(Messages.get(UnstableSpellbook::class.java, "unknown_scroll"))
        }
    }

    init {
        image = ItemSpriteSheet.ARTIFACT_SPELLBOOK

        levelCap = 10

        charge = (level() * 0.4f).toInt() + 2
        partialCharge = 0f
        chargeCap = (level() * 0.4f).toInt() + 2

        defaultAction = AC_READ
    }

    init {

        val scrollClasses = Generator.Category.SCROLL.classes
        val probs = Generator.Category.SCROLL.probs!!.clone() //array of primitives, clone gives deep copy.
        var i = Random.chances(probs)

        while (i != -1) {
            scrolls.add(scrollClasses!![i])
            probs[i] = 0f

            i = Random.chances(probs)
        }
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && charge > 0 && !cursed)
            actions.add(AC_READ)
        if (isEquipped(hero) && level() < levelCap && !cursed)
            actions.add(AC_ADD)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_READ) {

            if (hero.buff<Blindness>(Blindness::class.java) != null)
                GLog.w(Messages.get(this.javaClass, "blinded"))
            else if (!isEquipped(hero))
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
            else if (charge == 0)
                GLog.i(Messages.get(this.javaClass, "no_charge"))
            else if (cursed)
                GLog.i(Messages.get(this.javaClass, "cursed"))
            else {
                charge--

                var scroll: Scroll?
                do {
                    scroll = Generator.random(Generator.Category.SCROLL) as Scroll
                } while (scroll == null
                        //reduce the frequency of these scrolls by half
                        || (scroll is ScrollOfIdentify ||
                                scroll is ScrollOfRemoveCurse ||
                                scroll is ScrollOfMagicMapping) && Random.Int(2) == 0
                        //don't roll teleportation scrolls on boss floors
                        || scroll is ScrollOfTeleportation && Dungeon.bossLevel())

                scroll.ownedByBook = true
                Item.curItem = scroll
                Item.curUser = hero

                //if this scroll hasn't been given to the book
                if (scrolls.contains(scroll.javaClass)) {
                    scroll.doRead()
                } else {
                    scroll.empoweredRead()
                }
                updateQuickslot()
            }

        } else if (action == AC_ADD) {
            GameScene.selectItem(itemSelector, mode, Messages.get(this.javaClass, "prompt"))
        }
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return bookRecharge()
    }

    override fun upgrade(): Item {
        chargeCap = ((level() + 1) * 0.4f).toInt() + 2

        //for artifact transmutation.
        while (scrolls.size > levelCap - 1 - level())
            scrolls.removeAt(0)

        return super.upgrade()
    }

    override fun desc(): String {
        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!)) {
            if (cursed) {
                desc += "\n\n" + Messages.get(this.javaClass, "desc_cursed")
            }

            if (level() < levelCap && scrolls.size > 0) {
                desc += "\n\n" + Messages.get(this.javaClass, "desc_index")
                desc += "\n" + "_" + Messages.get(scrolls[0], "name") + "_"
                if (scrolls.size > 1)
                    desc += "\n" + "_" + Messages.get(scrolls[1], "name") + "_"
            }
        }

        if (level() > 0) {
            desc += "\n\n" + Messages.get(this.javaClass, "desc_empowered")
        }

        return desc
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(SCROLLS, scrolls.toTypedArray<Class<*>>())
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        scrolls.clear()
        Collections.addAll<Class<*>>(scrolls, *bundle.getClassArray(SCROLLS)!!)
    }

    inner class bookRecharge : Artifact.ArtifactBuff() {
        override fun act(): Boolean {
            val lock = target!!.buff<LockedFloor>(LockedFloor::class.java)
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (160f - (chargeCap - charge) * 15f)

                if (partialCharge >= 1) {
                    partialCharge--
                    charge++

                    if (charge == chargeCap) {
                        partialCharge = 0f
                    }
                }
            }

            updateQuickslot()

            spend(Actor.TICK)

            return true
        }
    }

    companion object {

        val AC_READ = "READ"
        val AC_ADD = "ADD"

        private val SCROLLS = "scrolls"
    }
}
