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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

class Weightstone : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    private val itemSelector = object: WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                GameScene.show(WndBalance(item as Weapon))
            }
        }
    }

    init {
        image = ItemSpriteSheet.WEIGHT

        stackable = true

        bones = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_APPLY)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_APPLY) {

            Item.curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, Messages.get(this.javaClass, "select"))

        }
    }

    private fun apply(weapon: Weapon, forSpeed: Boolean) {

        detach(Item.curUser!!.belongings.backpack)

        if (forSpeed) {
            weapon.imbue = Weapon.Imbue.LIGHT
            GLog.p(Messages.get(this.javaClass, "light"))
        } else {
            weapon.imbue = Weapon.Imbue.HEAVY
            GLog.p(Messages.get(this.javaClass, "heavy"))
        }

        Item.curUser!!.sprite!!.operate(Item.curUser!!.pos)
        Sample.INSTANCE.play(Assets.SND_MISS)

        Item.curUser!!.spend(TIME_TO_APPLY)
        Item.curUser!!.busy()
    }

    override fun price(): Int {
        return 50 * quantity
    }

    inner class WndBalance(weapon: Weapon) : Window() {

        init {

            val titlebar = IconTitle(weapon)
            titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
            add(titlebar)

            val tfMesage = PixelScene.renderMultiline(Messages.get(this.javaClass, "choice"), 8)
            tfMesage.maxWidth(WIDTH - MARGIN * 2)
            tfMesage.setPos(MARGIN.toFloat(), titlebar.bottom() + MARGIN)
            add(tfMesage)

            var pos = tfMesage.top() + tfMesage.height()

            if (weapon.imbue != Weapon.Imbue.LIGHT) {
                val btnSpeed = object : RedButton(Messages.get(this@Weightstone.javaClass, "light")) {
                    override fun onClick() {
                        hide()
                        this@Weightstone.apply(weapon, true)
                    }
                }
                btnSpeed.setRect(MARGIN.toFloat(), pos + MARGIN, BUTTON_WIDTH.toFloat(), BUTTON_HEIGHT.toFloat())
                add(btnSpeed)

                pos = btnSpeed.bottom()
            }

            if (weapon.imbue != Weapon.Imbue.HEAVY) {
                val btnAccuracy = object : RedButton(Messages.get(this@Weightstone.javaClass, "heavy")) {
                    override fun onClick() {
                        hide()
                        this@Weightstone.apply(weapon, false)
                    }
                }
                btnAccuracy.setRect(MARGIN.toFloat(), pos + MARGIN, BUTTON_WIDTH.toFloat(), BUTTON_HEIGHT.toFloat())
                add(btnAccuracy)

                pos = btnAccuracy.bottom()
            }

            val btnCancel = object : RedButton(Messages.get(this@Weightstone.javaClass, "cancel")) {
                override fun onClick() {
                    hide()
                }
            }
            btnCancel.setRect(MARGIN.toFloat(), pos + MARGIN, BUTTON_WIDTH.toFloat(), BUTTON_HEIGHT.toFloat())
            add(btnCancel)

            resize(WIDTH, btnCancel.bottom().toInt() + MARGIN)
        }

        protected fun onSelect(index: Int) {}

    }

    companion object {
        private val WIDTH = 120
        private val MARGIN = 2
        private val BUTTON_WIDTH = WIDTH - MARGIN * 2
        private val BUTTON_HEIGHT = 20

        private val TIME_TO_APPLY = 2f

        private val AC_APPLY = "APPLY"
    }
}