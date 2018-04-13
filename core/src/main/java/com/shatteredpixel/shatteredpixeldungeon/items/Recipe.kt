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

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart

import java.util.ArrayList

abstract class Recipe {

    abstract fun testIngredients(ingredients: ArrayList<Item>): Boolean

    //not currently used
    abstract fun cost(ingredients: ArrayList<Item>): Int

    abstract fun brew(ingredients: ArrayList<Item>): Item

    abstract fun sampleOutput(ingredients: ArrayList<Item>): Item

    //subclass for the common situation of a recipe with static inputs and outputs
    abstract class SimpleRecipe : Recipe() {

        //*** These elements must be filled in by subclasses
        protected var inputs: Array<Class<out Item>>? = null
        protected var inQuantity: IntArray? = null

        protected var cost: Int = 0

        protected var output: Class<out Item>? = null
        protected var outQuantity: Int = 0
        //***

        override fun testIngredients(ingredients: ArrayList<Item>): Boolean {
            var found: Boolean
            for (i in inputs!!.indices) {
                found = false
                for (ingredient in ingredients) {
                    if (ingredient.javaClass == inputs!![i] && ingredient.quantity() >= inQuantity!![i]) {
                        found = true
                        break
                    }
                }
                if (!found) {
                    return false
                }
            }
            return true
        }

        override fun cost(ingredients: ArrayList<Item>): Int {
            return cost
        }

        override fun brew(ingredients: ArrayList<Item>): Item? {
            if (!testIngredients(ingredients)) return null

            for (i in inputs!!.indices) {
                for (ingredient in ingredients) {
                    if (ingredient.javaClass == inputs!![i]) {
                        ingredient.quantity(ingredient.quantity() - inQuantity!![i])
                        break
                    }
                }
            }

            //sample output and real output are identical in this case.
            return sampleOutput(null)
        }

        //ingredients are ignored, as output doesn't vary
        override fun sampleOutput(ingredients: ArrayList<Item>?): Item? {
            try {
                val result = output!!.newInstance()
                result.quantity(outQuantity)
                return result
            } catch (e: Exception) {
                ShatteredPixelDungeon.reportException(e)
                return null
            }

        }
    }

    companion object {


        //*******
        // Static members
        //*******

        private val oneIngredientRecipes = arrayOf<Recipe>()

        private val twoIngredientRecipes = arrayOf(Blandfruit.CookFruit(), TippedDart.TipDart())

        private val threeIngredientRecipes = arrayOf<Recipe>(Potion.RandomPotion())

        fun findRecipe(ingredients: ArrayList<Item>): Recipe? {

            if (ingredients.size == 1) {
                for (recipe in oneIngredientRecipes) {
                    if (recipe.testIngredients(ingredients)) {
                        return recipe
                    }
                }

            } else if (ingredients.size == 2) {
                for (recipe in twoIngredientRecipes) {
                    if (recipe.testIngredients(ingredients)) {
                        return recipe
                    }
                }

            } else if (ingredients.size == 3) {
                for (recipe in threeIngredientRecipes) {
                    if (recipe.testIngredients(ingredients)) {
                        return recipe
                    }
                }
            }

            return null
        }
    }

}


