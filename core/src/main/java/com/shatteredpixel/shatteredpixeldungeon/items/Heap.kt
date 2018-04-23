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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat
import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Collections
import java.util.LinkedList

class Heap : Bundlable {
    var type = Type.HEAP

    var pos = 0

    var sprite: ItemSprite? = null
    var seen = false

    var items: LinkedList<Item>? = LinkedList()

    val isEmpty: Boolean
        get() = items == null || items!!.size == 0

    enum class Type {
        HEAP,
        FOR_SALE,
        CHEST,
        LOCKED_CHEST,
        CRYSTAL_CHEST,
        TOMB,
        SKELETON,
        REMAINS,
        MIMIC
    }

    fun image(): Int {
        when (type) {
            Heap.Type.HEAP, Heap.Type.FOR_SALE -> return if (size() > 0) items!!.peek().image() else 0
            Heap.Type.CHEST, Heap.Type.MIMIC -> return ItemSpriteSheet.CHEST
            Heap.Type.LOCKED_CHEST -> return ItemSpriteSheet.LOCKED_CHEST
            Heap.Type.CRYSTAL_CHEST -> return ItemSpriteSheet.CRYSTAL_CHEST
            Heap.Type.TOMB -> return ItemSpriteSheet.TOMB
            Heap.Type.SKELETON -> return ItemSpriteSheet.BONES
            Heap.Type.REMAINS -> return ItemSpriteSheet.REMAINS
            else -> return 0
        }
    }

    fun glowing(): ItemSprite.Glowing? {
        return if ((type == Type.HEAP || type == Type.FOR_SALE) && items!!.size > 0) items!!.peek().glowing() else null
    }

    fun open(hero: Hero) {
        when (type) {
            Heap.Type.MIMIC -> if (Mimic.spawnAt(pos, items!!) != null) {
                destroy()
            } else {
                type = Type.CHEST
            }
            Heap.Type.TOMB -> Wraith.spawnAround(hero.pos)
            Heap.Type.REMAINS, Heap.Type.SKELETON -> {
                CellEmitter.center(pos).start(Speck.factory(Speck.RATTLE), 0.1f, 3)
                for (item in items!!) {
                    if (item.cursed) {
                        if (Wraith.spawnAt(pos) == null) {
                            hero.sprite!!.emitter().burst(ShadowParticle.CURSE, 6)
                            hero.damage(hero.HP / 2, this)
                        }
                        Sample.INSTANCE.play(Assets.SND_CURSED)
                        break
                    }
                }
            }
        }

        if (type != Type.MIMIC) {
            type = Type.HEAP
            val bonus = RingOfWealth.tryRareDrop(hero, 1)
            if (bonus != null) {
                items!!.addAll(0, bonus)
                Flare(8, 32f).color(0xFFFF00, true).show(sprite!!, 2f)
            }
            sprite!!.link()
            sprite!!.drop()
        }
    }

    fun size(): Int {
        return items!!.size
    }

    fun pickUp(): Item {

        val item = items!!.removeFirst()
        if (items!!.isEmpty()) {
            destroy()
        } else if (sprite != null) {
            sprite!!.view(image(), glowing())
            sprite!!.place(pos)
        }

        return item
    }

    fun peek(): Item {
        return items!!.peek()
    }

    fun drop(item: Item) {
        var item = item

        if (item.stackable && type != Type.FOR_SALE) {

            for (i in items!!) {
                if (i.isSimilar(item)) {
                    item = i.merge(item)
                    break
                }
            }
            items!!.remove(item)

        }

        if ((item is Dewdrop || item is DriedRose.Petal) && type != Type.FOR_SALE) {
            items!!.add(item)
        } else {
            items!!.addFirst(item)
        }

        if (sprite != null) {
            if (type == Type.HEAP || type == Type.FOR_SALE)
                sprite!!.view(items!!.peek())
            else
                sprite!!.view(image(), glowing())
            sprite!!.place(pos)
        }
    }

    fun replace(a: Item, b: Item) {
        val index = items!!.indexOf(a)
        if (index != -1) {
            items!!.removeAt(index)
            items!!.add(index, b)
        }
    }

    fun burn() {

        if (type == Type.MIMIC) {
            val m = Mimic.spawnAt(pos, items!!)
            if (m != null) {
                Buff.affect<Burning>(m, Burning::class.java)!!.reignite(m)
                m.sprite!!.emitter().burst(FlameParticle.FACTORY, 5)
                destroy()
            }
        }

        if (type != Type.HEAP) {
            return
        }

        var burnt = false
        var evaporated = false

        for (item in items!!.toTypedArray<Item>()) {
            if (item is Scroll && !(item is ScrollOfUpgrade || item is ScrollOfMagicalInfusion)) {
                items!!.remove(item)
                burnt = true
            } else if (item is Dewdrop) {
                items!!.remove(item)
                evaporated = true
            } else if (item is MysteryMeat) {
                replace(item, ChargrilledMeat.cook(item as MysteryMeat))
                burnt = true
            } else if (item is Bomb) {
                items!!.remove(item)
                (item as Bomb).explode(pos)
                //stop processing the burning, it will be replaced by the explosion.
                return
            }
        }

        if (burnt || evaporated) {

            if (Dungeon.level!!.heroFOV[pos]) {
                if (burnt) {
                    burnFX(pos)
                } else {
                    evaporateFX(pos)
                }
            }

            if (isEmpty) {
                destroy()
            } else if (sprite != null) {
                sprite!!.view(items!!.peek())
            }

        }
    }

    //Note: should not be called to initiate an explosion, but rather by an explosion that is happening.
    fun explode() {

        //breaks open most standard containers, mimics die.
        if (type == Type.MIMIC || type == Type.CHEST || type == Type.SKELETON) {
            type = Type.HEAP
            sprite!!.link()
            sprite!!.drop()
            return
        }

        if (type != Type.HEAP) {

            return

        } else {

            for (item in items!!.toTypedArray<Item>()) {

                if (item is Potion) {
                    items!!.remove(item)
                    (item as Potion).shatter(pos)

                } else if (item is Bomb) {
                    items!!.remove(item)
                    (item as Bomb).explode(pos)
                    //stop processing current explosion, it will be replaced by the new one.
                    return

                    //unique and upgraded items can endure the blast
                } else if (!(item.level() > 0 || item.unique))
                    items!!.remove(item)

            }

            if (isEmpty) {
                destroy()
            } else if (sprite != null) {
                sprite!!.view(items!!.peek())
            }
        }
    }

    fun freeze() {

        if (type == Type.MIMIC) {
            val m = Mimic.spawnAt(pos, items!!)
            if (m != null) {
                Buff.prolong<Frost>(m, Frost::class.java, Frost.duration(m) * Random.Float(1.0f, 1.5f))
                destroy()
            }
        }

        if (type != Type.HEAP) {
            return
        }

        var frozen = false
        for (item in items!!.toTypedArray<Item>()) {
            if (item is MysteryMeat) {
                replace(item, FrozenCarpaccio.cook(item as MysteryMeat))
                frozen = true
            } else if (item is Potion && !(item is PotionOfStrength || item is PotionOfMight)) {
                items!!.remove(item)
                (item as Potion).shatter(pos)
                frozen = true
            } else if (item is Bomb) {
                (item as Bomb).fuse = null
                frozen = true
            }
        }

        if (frozen) {
            if (isEmpty) {
                destroy()
            } else if (sprite != null) {
                sprite!!.view(items!!.peek())
            }
        }
    }

    fun destroy() {
        Dungeon.level!!.heaps.remove(this.pos)
        if (sprite != null) {
            sprite!!.kill()
        }
        items!!.clear()
    }

    override fun toString(): String {
        when (type) {
            Heap.Type.CHEST, Heap.Type.MIMIC -> return Messages.get(this.javaClass, "chest")
            Heap.Type.LOCKED_CHEST -> return Messages.get(this.javaClass, "locked_chest")
            Heap.Type.CRYSTAL_CHEST -> return Messages.get(this.javaClass, "crystal_chest")
            Heap.Type.TOMB -> return Messages.get(this.javaClass, "tomb")
            Heap.Type.SKELETON -> return Messages.get(this.javaClass, "skeleton")
            Heap.Type.REMAINS -> return Messages.get(this.javaClass, "remains")
            else -> return peek().toString()
        }
    }

    fun info(): String {
        when (type) {
            Heap.Type.CHEST, Heap.Type.MIMIC -> return Messages.get(this.javaClass, "chest_desc")
            Heap.Type.LOCKED_CHEST -> return Messages.get(this.javaClass, "locked_chest_desc")
            Heap.Type.CRYSTAL_CHEST -> return if (peek() is Artifact)
                Messages.get(this.javaClass, "crystal_chest_desc", Messages.get(this.javaClass, "artifact"))
            else if (peek() is Wand)
                Messages.get(this.javaClass, "crystal_chest_desc", Messages.get(this.javaClass, "wand"))
            else
                Messages.get(this.javaClass, "crystal_chest_desc", Messages.get(this.javaClass, "ring"))
            Heap.Type.TOMB -> return Messages.get(this.javaClass, "tomb_desc")
            Heap.Type.SKELETON -> return Messages.get(this.javaClass, "skeleton_desc")
            Heap.Type.REMAINS -> return Messages.get(this.javaClass, "remains_desc")
            else -> return peek().info()
        }
    }

    override fun restoreFromBundle(bundle: Bundle) {
        pos = bundle.getInt(POS)
        seen = bundle.getBoolean(SEEN)
        type = Type.valueOf(bundle.getString(TYPE))

        items = LinkedList(bundle.getCollection(ITEMS) as Collection<*> as Collection<Item>)
        items!!.removeAll(setOf<Any?>(null))

        //remove any document pages that either don't exist anymore or that the player already has
        for (item in items!!.toTypedArray<Item>()) {
            if (item is DocumentPage && (!(item as DocumentPage).document().pages().contains((item as DocumentPage).page()) || (item as DocumentPage).document().hasPage((item as DocumentPage).page()!!))) {
                items!!.remove(item)
            }
        }

    }

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(POS, pos)
        bundle.put(SEEN, seen)
        bundle.put(TYPE, type.toString())
        bundle.put(ITEMS, items!!)
    }

    companion object {

        private val SEEDS_TO_POTION = 3

        fun burnFX(pos: Int) {
            CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6)
            Sample.INSTANCE.play(Assets.SND_BURNING)
        }

        fun evaporateFX(pos: Int) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 5)
        }

        private val POS = "pos"
        private val SEEN = "seen"
        private val TYPE = "type"
        private val ITEMS = "items"
    }

}
