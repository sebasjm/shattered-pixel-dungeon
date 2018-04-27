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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.Enchantment
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random

open class Statue : Mob() {

    protected var weapon: Weapon

    init {
        spriteClass = StatueSprite::class.java

        EXP = 0
        state = PASSIVE

        properties.add(Char.Property.INORGANIC)
    }

    init {

        do {
            weapon = Generator.random(Generator.Category.WEAPON) as MeleeWeapon
        } while (weapon.cursed)

        weapon.enchant(Enchantment.random())

        HT = 15 + Dungeon.depth * 5
        HP = HT
        defenseSkill = 4 + Dungeon.depth
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(WEAPON, weapon)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        weapon = bundle.get(WEAPON) as Weapon
    }

    override fun act(): Boolean {
        if (Dungeon.level!!.heroFOV[pos]) {
            Notes.add(Notes.Landmark.STATUE)
        }
        return super.act()
    }

    override fun damageRoll(): Int {
        return weapon.damageRoll(this)
    }

    override fun attackSkill(target: Char?): Int {
        return ((9 + Dungeon.depth) * weapon.accuracyFactor(this)).toInt()
    }

    override fun attackDelay(): Float {
        return weapon.speedFactor(this)
    }

    override fun canAttack(enemy: Char?): Boolean {
        return Dungeon.level!!.distance(pos, enemy!!.pos) <= weapon.reachFactor(this)
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, Dungeon.depth + weapon.defenseFactor(this))
    }

    override fun damage(dmg: Int, src: Any) {

        if (state === PASSIVE) {
            state = HUNTING
        }

        super.damage(dmg, src)
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        return weapon.proc(this, enemy, damage)
    }

    override fun beckon(cell: Int) {
        // Do nothing
    }

    override fun die(cause: Any?) {
        weapon.identify()
        Dungeon.level!!.drop(weapon, pos).sprite!!.drop()
        super.die(cause)
    }

    override fun destroy() {
        Notes.remove(Notes.Landmark.STATUE)
        super.destroy()
    }

    override fun reset(): Boolean {
        state = PASSIVE
        return true
    }

    override fun description(): String {
        return Messages.get(this.javaClass, "desc", weapon.name())
    }

    init {
        resistances.add(Grim::class.java)
    }

    companion object {

        private val WEAPON = "weapon"
    }

}
