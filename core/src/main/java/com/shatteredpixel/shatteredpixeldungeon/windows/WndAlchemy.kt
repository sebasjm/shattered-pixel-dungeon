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

package com.shatteredpixel.shatteredpixeldungeon.windows

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

import java.util.ArrayList

class WndAlchemy : Window() {
    private val output: ItemSlot

    private val smokeEmitter: Emitter
    private val bubbleEmitter: Emitter

    private val btnCombine: RedButton

    protected var itemSelector: WndBag.Listener = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            synchronized(inputs) {
                if (item != null && inputs[0] != null) {
                    for (i in inputs.indices) {
                        if (inputs[i]!!.item == null) {
                            if (item is Dart) {
                                inputs[i]!!.item(item.detachAll(Dungeon.hero!!.belongings.backpack))
                            } else {
                                inputs[i]!!.item(item.detach(Dungeon.hero!!.belongings.backpack))
                            }
                            break
                        }
                    }
                    updateState()
                }
            }
        }
    }

    init {

        val w = WIDTH_P

        var h = 0

        val titlebar = IconTitle()
        titlebar.icon(DungeonTerrainTilemap.tile(0, Terrain.ALCHEMY))
        titlebar.label(Messages.get(this.javaClass, "title"))
        titlebar.setRect(0f, 0f, w.toFloat(), 0f)
        add(titlebar)

        h += (titlebar.height() + 2).toInt()

        val desc = PixelScene.renderMultiline(6)
        desc.text(Messages.get(this.javaClass, "text"))
        desc.setPos(0f, h.toFloat())
        desc.maxWidth(w)
        add(desc)

        h += (desc.height() + 6).toInt()

        synchronized(inputs) {
            for (i in inputs.indices) {
                inputs[i] = object : WndBlacksmith.ItemButton() {
                    override fun onClick() {
                        super.onClick()
                        if (item != null) {
                            if (!item!!.collect()) {
                                Dungeon.level!!.drop(item, Dungeon.hero!!.pos)
                            }
                            item = null
                            slot!!.item(WndBag.Placeholder(ItemSpriteSheet.SOMETHING))
                        }
                        GameScene.selectItem(itemSelector, WndBag.Mode.ALCHEMY, Messages.get(WndAlchemy::class.java, "select"))
                    }
                }
                inputs[i]!!.setRect(10f, h.toFloat(), BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
                add(inputs[i])
                h += BTN_SIZE + 2
            }
        }

        btnCombine = object : RedButton("") {
            internal var arrow: Image? = null

            override fun createChildren() {
                super.createChildren()

                arrow = Icons.get(Icons.RESUME)
                add(arrow)
            }

            override fun layout() {
                super.layout()
                arrow!!.x = x + (width - arrow!!.width) / 2f
                arrow!!.y = y + (height - arrow!!.height) / 2f
                PixelScene.align(arrow!!)
            }

            override fun enable(value: Boolean) {
                super.enable(value)
                if (value) {
                    arrow!!.tint(1f, 1f, 0f, 1f)
                    arrow!!.alpha(1f)
                    bg!!.alpha(1f)
                } else {
                    arrow!!.color(0f, 0f, 0f)
                    arrow!!.alpha(0.6f)
                    bg!!.alpha(0.6f)
                }
            }

            override fun onClick() {
                super.onClick()
                combine()
            }
        }
        btnCombine.enable(false)
        btnCombine.setRect((w - 30) / 2f, inputs[1]!!.top() + 5, 30f, inputs[1]!!.height() - 10)
        add(btnCombine)

        output = object : ItemSlot() {
            override fun onClick() {
                super.onClick()
                if (visible && item!!.trueName() != null) {
                    GameScene.show(WndInfoItem(item!!))
                }
            }
        }
        output.setRect((w - BTN_SIZE - 10).toFloat(), inputs[1]!!.top(), BTN_SIZE.toFloat(), BTN_SIZE.toFloat())

        val outputBG = ColorBlock(output.width(), output.height(), -0x666e6c74)
        outputBG.x = output.left()
        outputBG.y = output.top()
        add(outputBG)

        add(output)
        output.visible = false

        bubbleEmitter = Emitter()
        smokeEmitter = Emitter()
        bubbleEmitter.pos(outputBG.x + (BTN_SIZE - 16) / 2f, outputBG.y + (BTN_SIZE - 16) / 2f, 16f, 16f)
        smokeEmitter.pos(bubbleEmitter.x, bubbleEmitter.y, bubbleEmitter.width, bubbleEmitter.height)
        bubbleEmitter.autoKill = false
        smokeEmitter.autoKill = false
        add(bubbleEmitter)
        add(smokeEmitter)

        h += 4

        val btnWidth = (w - 14) / 2f

        val btnRecipes = object : RedButton(Messages.get(this.javaClass, "recipes_title")) {
            override fun onClick() {
                super.onClick()
                Game.scene()!!.addToFront(WndMessage(Messages.get(WndAlchemy::class.java, "recipes_text")))
            }
        }
        btnRecipes.setRect(5f, h.toFloat(), btnWidth, 18f)
        PixelScene.align(btnRecipes)
        add(btnRecipes)

        val btnClose = object : RedButton(Messages.get(this.javaClass, "close")) {
            override fun onClick() {
                super.onClick()
                onBackPressed()
            }
        }
        btnClose.setRect(w.toFloat() - 5f - btnWidth, h.toFloat(), btnWidth, 18f)
        PixelScene.align(btnClose)
        add(btnClose)

        h += btnClose.height().toInt()

        resize(w, h)
    }

    private fun <T : Item> filterInput(itemClass: Class<out T>): ArrayList<T> {
        val filtered = ArrayList<T>()
        for (i in inputs.indices) {
            val item = inputs[i]!!.item
            if (item != null && itemClass.isInstance(item)) {
                filtered.add(item as T)
            }
        }
        return filtered
    }

    private fun updateState() {

        val ingredients = filterInput<Item>(Item::class.java)
        val recipe = Recipe.findRecipe(ingredients)

        if (recipe != null) {
            output.item(recipe.sampleOutput(ingredients))
            output.visible = true
            btnCombine.enable(true)

        } else {
            btnCombine.enable(false)
            output.visible = false
        }

    }

    private fun combine() {

        val ingredients = filterInput<Item>(Item::class.java)
        val recipe = Recipe.findRecipe(ingredients)

        var result: Item? = null

        if (recipe != null) {
            result = recipe.brew(ingredients)
        }

        if (result != null) {
            bubbleEmitter.start(Speck.factory(Speck.BUBBLE), 0.2f, 10)
            smokeEmitter.burst(Speck.factory(Speck.WOOL), 10)
            Sample.INSTANCE.play(Assets.SND_PUFF)

            output.item(result)
            if (!result.collect()) {
                Dungeon.level!!.drop(result, Dungeon.hero!!.pos)
            }

            synchronized(inputs) {
                for (i in inputs.indices) {
                    if (inputs[i] != null && inputs[i]!!.item != null) {
                        if (inputs[i]!!.item!!.quantity() <= 0) {
                            inputs[i]!!.slot!!.item(WndBag.Placeholder(ItemSpriteSheet.SOMETHING))
                            inputs[i]!!.item = null
                        } else {
                            inputs[i]!!.slot!!.item(inputs[i]!!.item)
                        }
                    }
                }
            }

            btnCombine.enable(false)
        }

    }

    override fun destroy() {
        synchronized(inputs) {
            for (i in inputs.indices) {
                if (inputs[i] != null && inputs[i]!!.item != null) {
                    if (!inputs[i]!!.item!!.collect()) {
                        Dungeon.level!!.drop(inputs[i]!!.item, Dungeon.hero!!.pos)
                    }
                }
                inputs[i] = null
            }
        }
        super.destroy()
    }

    companion object {

        private val inputs = arrayOfNulls<WndBlacksmith.ItemButton>(3)

        private val WIDTH_P = 116
        private val WIDTH_L = 160

        private val BTN_SIZE = 28

        private val ALCHEMY_INPUTS = "alchemy_inputs"

        fun storeInBundle(b: Bundle) {
            synchronized(inputs) {
                val items = ArrayList<Item>()
                for (i in inputs) {
                    if (i != null && i.item != null) {
                        items.add(i!!.item!!)
                    }
                }
                if (!items.isEmpty()) {
                    b.put(ALCHEMY_INPUTS, items)
                }
            }
        }

        fun restoreFromBundle(b: Bundle, h: Hero) {

            if (b.contains(ALCHEMY_INPUTS)) {
                for (item in b.getCollection(ALCHEMY_INPUTS)) {

                    //try to add normally, force-add otherwise.
                    if (!(item as Item).collect(h.belongings.backpack)) {
                        h.belongings.backpack.items.add(item)
                    }
                }
            }

        }
    }
}
