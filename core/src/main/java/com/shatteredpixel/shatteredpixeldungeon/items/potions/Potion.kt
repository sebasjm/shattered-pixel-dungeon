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

package com.shatteredpixel.shatteredpixeldungeon.items.potions

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

open class Potion : Item() {

    protected var initials: Int? = null

    private var color: String? = null

    var ownedByFruit = false

    val isKnown: Boolean
        get() = handler!!.isKnown(this)

    override val isIdentified: Boolean
        get() = isKnown

    override val isUpgradable: Boolean
        get() = false

    init {
        stackable = true
        defaultAction = AC_DRINK
    }

    init {
        reset()
    }

    override fun reset() {
        super.reset()
        if (handler != null) {
            image = handler!!.image(this)
            color = handler!!.label(this)
        }
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_DRINK)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_DRINK) {

            if (isKnown && (this is PotionOfLiquidFlame ||
                            this is PotionOfToxicGas ||
                            this is PotionOfParalyticGas)) {

                GameScene.show(
                        object : WndOptions(Messages.get(Potion::class.java, "harmful"),
                                Messages.get(Potion::class.java, "sure_drink"),
                                Messages.get(Potion::class.java, "yes"), Messages.get(Potion::class.java, "no")) {
                            override fun onSelect(index: Int) {
                                if (index == 0) {
                                    drink(hero)
                                }
                            }
                        }
                )

            } else {
                drink(hero)
            }

        }
    }

    override fun doThrow(hero: Hero) {

        if (isKnown && (this is PotionOfExperience ||
                        this is PotionOfHealing ||
                        this is PotionOfMindVision ||
                        this is PotionOfStrength ||
                        this is PotionOfInvisibility ||
                        this is PotionOfMight)) {

            GameScene.show(
                    object : WndOptions(Messages.get(Potion::class.java, "beneficial"),
                            Messages.get(Potion::class.java, "sure_throw"),
                            Messages.get(Potion::class.java, "yes"), Messages.get(Potion::class.java, "no")) {
                        override fun onSelect(index: Int) {
                            if (index == 0) {
                                super@Potion.doThrow(hero)
                            }
                        }
                    }
            )

        } else {
            super.doThrow(hero)
        }
    }

    protected fun drink(hero: Hero) {

        detach(hero.belongings.backpack)

        hero.spend(TIME_TO_DRINK)
        hero.busy()
        apply(hero)

        Sample.INSTANCE.play(Assets.SND_DRINK)

        hero.sprite!!.operate(hero.pos)
    }

    override fun onThrow(cell: Int) {
        if (Dungeon.level!!.map!![cell] == Terrain.WELL || Dungeon.level!!.pit[cell]) {

            super.onThrow(cell)

        } else {

            Dungeon.level!!.press(cell, null, true)
            shatter(cell)

        }
    }

    open fun apply(hero: Hero) {
        shatter(hero.pos)
    }

    open fun shatter(cell: Int) {
        if (Dungeon.level!!.heroFOV[cell]) {
            GLog.i(Messages.get(Potion::class.java, "shatter"))
            Sample.INSTANCE.play(Assets.SND_SHATTER)
            splash(cell)
        }
    }

    override fun cast(user: Hero?, dst: Int) {
        super.cast(user, dst)
    }

    fun setKnown() {
        if (!ownedByFruit) {
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
        return if (isKnown) super.name()!! else Messages.get(Potion::class.java, color!!)
    }

    override fun info(): String {
        return if (isKnown)
            desc()
        else
            Messages.get(Potion::class.java, "unknown_desc")
    }

    fun initials(): Int? {
        return if (isKnown) initials else null
    }

    protected fun splash(cell: Int) {

        val fire = Dungeon.level!!.blobs[Fire::class.java] as Fire
        fire?.clear(cell)

        val color = ItemSprite.pick(image, 8, 10)

        val ch = Actor.findChar(cell)
        if (ch != null) {
            Buff.detach(ch, Burning::class.java)
            Buff.detach(ch, Ooze::class.java)
            Splash.at(ch.sprite!!.center(), color, 5)
        } else {
            Splash.at(cell, color, 5)
        }
    }

    override fun price(): Int {
        return 30 * quantity
    }


    class RandomPotion : Recipe() {

        override fun testIngredients(ingredients: ArrayList<Item>): Boolean {
            if (ingredients.size != 3) {
                return false
            }

            for (ingredient in ingredients) {
                if (!(ingredient is Plant.Seed && ingredient.quantity() >= 1)) {
                    return false
                }
            }
            return true
        }

        override fun cost(ingredients: ArrayList<Item>): Int {
            return 1
        }

        override fun brew(ingredients: ArrayList<Item>): Item? {
            if (!testIngredients(ingredients)) return null

            for (ingredient in ingredients) {
                ingredient.quantity(ingredient.quantity() - 1)
            }

            var result: Item?

            if (Random.Int(3) == 0) {

                result = Generator.random(Generator.Category.POTION)

            } else {

                val itemClass = (Random.element(ingredients) as Plant.Seed).alchemyClass
                try {
                    result = itemClass!!.newInstance()
                } catch (e: Exception) {
                    Game.reportException(e)
                    result = Generator.random(Generator.Category.POTION)
                }

            }

            while (result is PotionOfHealing && (Dungeon.isChallenged(Challenges.NO_HEALING) || Random.Int(10) < Dungeon.LimitedDrops.COOKING_HP.count)) {
                result = Generator.random(Generator.Category.POTION)
            }

            if (result is PotionOfHealing) {
                Dungeon.LimitedDrops.COOKING_HP.count++
            }

            Statistics.potionsCooked++
            Badges.validatePotionsCooked()

            return result
        }

        override fun sampleOutput(ingredients: ArrayList<Item>?): Item? {
            return object : WndBag.Placeholder(ItemSpriteSheet.POTION_HOLDER) {
                init {
                    name = Messages.get(RandomPotion::class.java, "name")
                }

                override fun info(): String {
                    return ""
                }
            }
        }
    }

    companion object {

        val AC_DRINK = "DRINK"

        private val TIME_TO_DRINK = 1f

        private val potions = arrayOf<Class<*>>(PotionOfHealing::class.java, PotionOfExperience::class.java, PotionOfToxicGas::class.java, PotionOfLiquidFlame::class.java, PotionOfStrength::class.java, PotionOfParalyticGas::class.java, PotionOfLevitation::class.java, PotionOfMindVision::class.java, PotionOfPurity::class.java, PotionOfInvisibility::class.java, PotionOfMight::class.java, PotionOfFrost::class.java)

        private val colors = object : HashMap<String, Int>() {
            init {
                put("crimson", ItemSpriteSheet.POTION_CRIMSON)
                put("amber", ItemSpriteSheet.POTION_AMBER)
                put("golden", ItemSpriteSheet.POTION_GOLDEN)
                put("jade", ItemSpriteSheet.POTION_JADE)
                put("turquoise", ItemSpriteSheet.POTION_TURQUOISE)
                put("azure", ItemSpriteSheet.POTION_AZURE)
                put("indigo", ItemSpriteSheet.POTION_INDIGO)
                put("magenta", ItemSpriteSheet.POTION_MAGENTA)
                put("bistre", ItemSpriteSheet.POTION_BISTRE)
                put("charcoal", ItemSpriteSheet.POTION_CHARCOAL)
                put("silver", ItemSpriteSheet.POTION_SILVER)
                put("ivory", ItemSpriteSheet.POTION_IVORY)
            }
        }

        private var handler: ItemStatusHandler<Potion>? = null

        fun initColors() {
            handler = ItemStatusHandler(potions as Array<Class<out Potion>>, colors)
        }

        fun save(bundle: Bundle) {
            handler!!.save(bundle)
        }

        fun saveSelectively(bundle: Bundle, items: ArrayList<Item>) {
            handler!!.saveSelectively(bundle, items)
        }

        fun restore(bundle: Bundle) {
            handler = ItemStatusHandler(potions as Array<Class<out Potion>>, colors, bundle)
        }

        val known: HashSet<Class<out Potion>>?
            get() = handler!!.known()

        val unknown: HashSet<Class<out Potion>>
            get() = handler!!.unknown()

        fun allKnown(): Boolean {
            return handler!!.known()!!.size == potions.size
        }
    }
}
