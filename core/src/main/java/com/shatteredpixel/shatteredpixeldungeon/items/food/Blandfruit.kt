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

package com.shatteredpixel.shatteredpixeldungeon.items.food

import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EarthImbue
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant.Seed
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Game
import com.watabou.utils.Bundle

import java.util.ArrayList

class Blandfruit : Food() {

    var potionAttrib: Potion? = null
    var potionGlow: ItemSprite.Glowing? = null

    init {
        stackable = true
        image = ItemSpriteSheet.BLANDFRUIT

        //only applies when blandfruit is cooked
        energy = Hunger.STARVING

        bones = true
    }

    override fun isSimilar(item: Item): Boolean {
        if (item is Blandfruit) {
            if (potionAttrib == null) {
                if (item.potionAttrib == null)
                    return true
            } else if (item.potionAttrib != null) {
                if (item.potionAttrib!!.javaClass == potionAttrib!!.javaClass)
                    return true
            }
        }
        return false
    }

    override fun execute(hero: Hero, action: String?) {

        if (action == Food.AC_EAT && potionAttrib == null) {

            GLog.w(Messages.get(this.javaClass, "raw"))
            return

        }

        super.execute(hero, action)

        if (action == Food.AC_EAT && potionAttrib != null) {

            if (potionAttrib is PotionOfFrost) {
                GLog.i(Messages.get(this.javaClass, "ice_msg"))
                FrozenCarpaccio.effect(hero)
            } else if (potionAttrib is PotionOfLiquidFlame) {
                GLog.i(Messages.get(this.javaClass, "fire_msg"))
                Buff.affect<FireImbue>(hero, FireImbue::class.java)!!.set(FireImbue.DURATION)
            } else if (potionAttrib is PotionOfToxicGas) {
                GLog.i(Messages.get(this.javaClass, "toxic_msg"))
                Buff.affect<ToxicImbue>(hero, ToxicImbue::class.java)!!.set(ToxicImbue.DURATION)
            } else if (potionAttrib is PotionOfParalyticGas) {
                GLog.i(Messages.get(this.javaClass, "para_msg"))
                Buff.affect<EarthImbue>(hero, EarthImbue::class.java, EarthImbue.DURATION)
            } else {
                potionAttrib!!.apply(hero)
            }

        }
    }

    override fun desc(): String {
        return if (potionAttrib == null)
            super.desc()
        else
            Messages.get(this.javaClass, "desc_cooked")
    }

    override fun price(): Int {
        return 20 * quantity
    }

    fun cook(seed: Seed): Item? {

        try {
            return imbuePotion(seed.alchemyClass!!.newInstance() as Potion)
        } catch (e: Exception) {
            Game.reportException(e)
            return null
        }

    }

    fun imbuePotion(potion: Potion): Item {

        potionAttrib = potion
        potionAttrib!!.ownedByFruit = true

        potionAttrib!!.image = ItemSpriteSheet.BLANDFRUIT

        if (potionAttrib is PotionOfHealing) {
            name = Messages.get(this.javaClass, "sunfruit")
            potionGlow = ItemSprite.Glowing(0x2EE62E)
        } else if (potionAttrib is PotionOfStrength) {
            name = Messages.get(this.javaClass, "rotfruit")
            potionGlow = ItemSprite.Glowing(0xCC0022)
        } else if (potionAttrib is PotionOfParalyticGas) {
            name = Messages.get(this.javaClass, "earthfruit")
            potionGlow = ItemSprite.Glowing(0x67583D)
        } else if (potionAttrib is PotionOfInvisibility) {
            name = Messages.get(this.javaClass, "blindfruit")
            potionGlow = ItemSprite.Glowing(0xE5D273)
        } else if (potionAttrib is PotionOfLiquidFlame) {
            name = Messages.get(this.javaClass, "firefruit")
            potionGlow = ItemSprite.Glowing(0xFF7F00)
        } else if (potionAttrib is PotionOfFrost) {
            name = Messages.get(this.javaClass, "icefruit")
            potionGlow = ItemSprite.Glowing(0x66B3FF)
        } else if (potionAttrib is PotionOfMindVision) {
            name = Messages.get(this.javaClass, "fadefruit")
            potionGlow = ItemSprite.Glowing(0xB8E6CF)
        } else if (potionAttrib is PotionOfToxicGas) {
            name = Messages.get(this.javaClass, "sorrowfruit")
            potionGlow = ItemSprite.Glowing(0xA15CE5)
        } else if (potionAttrib is PotionOfLevitation) {
            name = Messages.get(this.javaClass, "stormfruit")
            potionGlow = ItemSprite.Glowing(0x1C3A57)
        } else if (potionAttrib is PotionOfPurity) {
            name = Messages.get(this.javaClass, "dreamfruit")
            potionGlow = ItemSprite.Glowing(0x8E2975)
        } else if (potionAttrib is PotionOfExperience) {
            name = Messages.get(this.javaClass, "starfruit")
            potionGlow = ItemSprite.Glowing(0xA79400)
        }

        return this
    }

    override fun onThrow(cell: Int) {
        if (Dungeon.level!!.map!![cell] == Terrain.WELL || Dungeon.level!!.pit[cell]) {
            super.onThrow(cell)

        } else if (potionAttrib is PotionOfLiquidFlame ||
                potionAttrib is PotionOfToxicGas ||
                potionAttrib is PotionOfParalyticGas ||
                potionAttrib is PotionOfFrost ||
                potionAttrib is PotionOfLevitation ||
                potionAttrib is PotionOfPurity) {

            Dungeon.level!!.press(cell, null, true)
            potionAttrib!!.shatter(cell)

        } else {
            super.onThrow(cell)
        }
    }

    override fun reset() {
        if (potionAttrib != null)
            imbuePotion(potionAttrib!!)
        else
            super.reset()
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(POTIONATTRIB, potionAttrib)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (bundle.contains(POTIONATTRIB)) {
            imbuePotion(bundle.get(POTIONATTRIB) as Potion)
        }
    }

    override fun glowing(): ItemSprite.Glowing? {
        return potionGlow
    }

    class CookFruit : Recipe() {

        override//also sorts ingredients if it can
        fun testIngredients(ingredients: ArrayList<Item>): Boolean {
            if (ingredients.size != 2) return false

            if (ingredients[0] is Blandfruit) {
                if (ingredients[1] !is Seed) {
                    return false
                }
            } else if (ingredients[0] is Seed) {
                if (ingredients[1] is Blandfruit) {
                    val temp = ingredients[0]
                    ingredients[0] = ingredients[1]
                    ingredients[1] = temp
                } else {
                    return false
                }
            } else {
                return false
            }

            val fruit = ingredients[0] as Blandfruit
            val seed = ingredients[1] as Seed

            return if (fruit.quantity() >= 1 && fruit.potionAttrib == null
                    && seed.quantity() >= 1) {

                if (Dungeon.isChallenged(Challenges.NO_HEALING) && seed is Sungrass.Seed) {
                    false
                } else true

            } else false

        }

        override fun cost(ingredients: ArrayList<Item>): Int {
            return 2
        }

        override fun brew(ingredients: ArrayList<Item>): Item? {
            if (!testIngredients(ingredients)) return null

            ingredients[0].quantity(ingredients[0].quantity() - 1)
            ingredients[1].quantity(ingredients[1].quantity() - 1)


            return Blandfruit().cook(ingredients[1] as Seed)
        }

        override fun sampleOutput(ingredients: ArrayList<Item>?): Item? {
            return if (!testIngredients(ingredients!!)) null else Blandfruit().cook(ingredients[1] as Seed)

        }
    }

    companion object {

        val POTIONATTRIB = "potionattrib"
    }

}
