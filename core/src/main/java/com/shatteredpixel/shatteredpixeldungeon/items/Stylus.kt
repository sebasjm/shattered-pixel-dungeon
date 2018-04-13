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
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

class Stylus : Item() {

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    private val itemSelector = WndBag.Listener { item ->
        if (item != null) {
            this@Stylus.inscribe(item as Armor)
        }
    }

    init {
        image = ItemSpriteSheet.STYLUS

        stackable = true

        bones = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_INSCRIBE)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_INSCRIBE) {

            Item.curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.ARMOR, Messages.get(this, "prompt"))

        }
    }

    private fun inscribe(armor: Armor) {

        if (!armor.isIdentified) {
            GLog.w(Messages.get(this, "identify"))
            return
        } else if (armor.cursed || armor.hasCurseGlyph()) {
            GLog.w(Messages.get(this, "cursed"))
            return
        }

        detach(Item.curUser.belongings.backpack)

        GLog.w(Messages.get(this, "inscribed"))

        armor.inscribe()

        Item.curUser.sprite!!.operate(Item.curUser.pos)
        Item.curUser.sprite!!.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10)
        Enchanting.show(Item.curUser, armor)
        Sample.INSTANCE.play(Assets.SND_BURNING)

        Item.curUser.spend(TIME_TO_INSCRIBE)
        Item.curUser.busy()
    }

    override fun price(): Int {
        return 30 * quantity
    }

    companion object {

        private val TIME_TO_INSCRIBE = 2f

        private val AC_INSCRIBE = "INSCRIBE"
    }
}
