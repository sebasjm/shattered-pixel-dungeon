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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite

import java.util.ArrayList

class Boomerang : MissileWeapon() {

    override val isUpgradable: Boolean
        get() = true

    override val isIdentified: Boolean
        get() = levelKnown && cursedKnown

    private var throwEquiped: Boolean = false

    init {
        image = ItemSpriteSheet.BOOMERANG

        stackable = false

        unique = true
        bones = false

    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (!isEquipped(hero)) actions.add(EquipableItem.AC_EQUIP)
        return actions
    }

    override fun min(lvl: Int): Int {
        return 1 + lvl
    }

    override fun max(lvl: Int): Int {
        return 6 +     //half the base damage of a tier-1 weapon
                2 * lvl//scales the same as a tier 1 weapon
    }

    override fun STRReq(lvl: Int): Int {
        var lvl = lvl
        lvl = Math.max(0, lvl)
        //strength req decreases at +1,+3,+6,+10,etc.
        return 9 - (Math.sqrt((8 * lvl + 1).toDouble()) - 1).toInt() / 2
    }

    override fun upgrade(enchant: Boolean): Item {
        super.upgrade(enchant)

        updateQuickslot()

        return this
    }

    override fun durabilityPerUse(): Float {
        return 0f
    }

    public override fun rangedHit(enemy: Char, cell: Int) {
        circleBack(cell, Item.curUser)
    }

    override fun rangedMiss(cell: Int) {
        circleBack(cell, Item.curUser)
    }

    private fun circleBack(from: Int, owner: Hero) {

        (Item.curUser.sprite!!.parent!!.recycle(MissileSprite::class.java) as MissileSprite).reset(from, owner.sprite, Item.curItem, null)

        if (throwEquiped) {
            owner.belongings.weapon = this
            owner.spend(-KindOfWeapon.TIME_TO_EQUIP)
            Dungeon.quickslot.replacePlaceholder(this)
            updateQuickslot()
        } else if (!collect(Item.curUser.belongings.backpack)) {
            Dungeon.level!!.drop(this, owner.pos).sprite!!.drop()
        }
    }

    override fun cast(user: Hero?, dst: Int) {
        throwEquiped = isEquipped(user) && !cursed
        if (throwEquiped) Dungeon.quickslot.convertToPlaceholder(this)
        super.cast(user, dst)
    }

    override fun desc(): String {
        var info = super.desc()
        when (imbue) {
            Weapon.Imbue.LIGHT -> info += "\n\n" + Messages.get(Weapon::class.java, "lighter")
            Weapon.Imbue.HEAVY -> info += "\n\n" + Messages.get(Weapon::class.java, "heavier")
        }

        return info
    }
}
