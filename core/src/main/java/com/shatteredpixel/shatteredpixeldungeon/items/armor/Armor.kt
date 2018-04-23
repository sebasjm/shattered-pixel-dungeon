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

package com.shatteredpixel.shatteredpixeldungeon.items.armor

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.AntiEntropy
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Corrosion
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Displacement
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Metabolism
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Multiplicity
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Stench
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Affection
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Entanglement
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Repulsion
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Thorns
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Game
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random

import java.util.ArrayList

open class Armor(var tier: Int) : EquipableItem() {

    private var hitsToKnow = HITS_TO_KNOW

    var glyph: Glyph? = null
    private var seal: BrokenSeal? = null

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, hitsToKnow)
        bundle.put(GLYPH, glyph)
        bundle.put(SEAL, seal)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        hitsToKnow = bundle.getInt(UNFAMILIRIARITY)
        if (hitsToKnow == 0) {
            hitsToKnow = HITS_TO_KNOW
        }
        inscribe(bundle.get(GLYPH) as Glyph)
        seal = bundle.get(SEAL) as BrokenSeal
    }

    override fun reset() {
        super.reset()
        //armor can be kept in bones between runs, the seal cannot.
        seal = null
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (seal != null) actions.add(AC_DETACH)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_DETACH && seal != null) {
            val sealBuff = hero.buff<BrokenSeal.WarriorShield>(BrokenSeal.WarriorShield::class.java)
            sealBuff?.setArmor(null)

            if (seal!!.level() > 0) {
                degrade()
            }
            GLog.i(Messages.get(Armor::class.java, "detach_seal"))
            hero.sprite!!.operate(hero.pos)
            if (!seal!!.collect()) {
                Dungeon.level!!.drop(seal, hero.pos)
            }
            seal = null
        }
    }

    override fun doEquip(hero: Hero): Boolean {

        detach(hero.belongings.backpack)

        if (hero.belongings.armor == null || hero.belongings.armor!!.doUnequip(hero, true, false)) {

            hero.belongings.armor = this

            cursedKnown = true
            if (cursed) {
                EquipableItem.equipCursed(hero)
                GLog.n(Messages.get(Armor::class.java, "equip_cursed"))
            }

            (hero.sprite as HeroSprite).updateArmor()
            activate(hero)

            hero.spendAndNext(time2equip(hero))
            return true

        } else {

            collect(hero.belongings.backpack)
            return false

        }
    }

    override fun activate(ch: Char) {
        if (seal != null) Buff.affect<BrokenSeal.WarriorShield>(ch, BrokenSeal.WarriorShield::class.java)!!.setArmor(this)
    }

    fun affixSeal(seal: BrokenSeal) {
        this.seal = seal
        if (seal.level() > 0) {
            //doesn't trigger upgrading logic such as affecting curses/glyphs
            level(level() + 1)
            Badges.validateItemLevelAquired(this)
        }
        if (isEquipped(Dungeon.hero!!)) {
            Buff.affect<BrokenSeal.WarriorShield>(Dungeon.hero!!, BrokenSeal.WarriorShield::class.java)!!.setArmor(this)
        }
    }

    fun checkSeal(): BrokenSeal? {
        return seal
    }

    override fun time2equip(hero: Hero?): Float {
        return 2 / hero!!.speed()
    }

    override fun doUnequip(hero: Hero?, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {

            hero!!.belongings.armor = null
            (hero.sprite as HeroSprite).updateArmor()

            val sealBuff = hero.buff<BrokenSeal.WarriorShield>(BrokenSeal.WarriorShield::class.java)
            sealBuff?.setArmor(null)

            return true

        } else {

            return false

        }
    }

    override fun isEquipped(hero: Hero): Boolean {
        return hero!!.belongings.armor === this
    }

    fun DRMax(): Int {
        return DRMax(level())
    }

    open fun DRMax(lvl: Int): Int {
        var effectiveTier = tier
        if (glyph != null) effectiveTier += glyph!!.tierDRAdjust()
        effectiveTier = Math.max(0, effectiveTier)

        return Math.max(DRMin(lvl), effectiveTier * (2 + lvl))
    }

    @JvmOverloads
    fun DRMin(lvl: Int = level()): Int {
        return if (glyph != null && glyph is Stone)
            2 * lvl
        else
            lvl
    }

    override fun upgrade(): Item {
        return upgrade(false)
    }

    fun upgrade(inscribe: Boolean): Item {

        if (inscribe && (glyph == null || glyph!!.curse())) {
            inscribe(Glyph.random())
        } else if (!inscribe && Random.Float() > Math.pow(0.9, level().toDouble())) {
            inscribe(null)
        }

        cursed = false

        if (seal != null && seal!!.level() == 0)
            seal!!.upgrade()

        return super.upgrade()
    }

    fun proc(attacker: Char, defender: Char, damage: Int): Int {
        var damage = damage

        if (glyph != null) {
            damage = glyph!!.proc(this, attacker, defender, damage)
        }

        if (!levelKnown) {
            if (--hitsToKnow <= 0) {
                identify()
                GLog.w(Messages.get(Armor::class.java, "identify"))
                Badges.validateItemLevelAquired(this)
            }
        }

        return damage
    }


    override fun name(): String {
        return if (glyph != null && (cursedKnown || !glyph!!.curse())) glyph!!.name(super.name()) else super.name()
    }

    override fun info(): String {
        var info = desc()

        if (levelKnown) {
            info += "\n\n" + Messages.get(Armor::class.java, "curr_absorb", DRMin(), DRMax(), STRReq())

            if (STRReq() > Dungeon.hero!!.STR()) {
                info += " " + Messages.get(Armor::class.java, "too_heavy")
            }
        } else {
            info += "\n\n" + Messages.get(Armor::class.java, "avg_absorb", DRMin(0), DRMax(0), STRReq(0))

            if (STRReq(0) > Dungeon.hero!!.STR()) {
                info += " " + Messages.get(Armor::class.java, "probably_too_heavy")
            }
        }

        if (glyph != null && (cursedKnown || !glyph!!.curse())) {
            info += "\n\n" + Messages.get(Armor::class.java, "inscribed", glyph!!.name())
            info += " " + glyph!!.desc()
        }

        if (cursed && isEquipped(Dungeon.hero!!)) {
            info += "\n\n" + Messages.get(Armor::class.java, "cursed_worn")
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Armor::class.java, "cursed")
        } else if (seal != null) {
            info += "\n\n" + Messages.get(Armor::class.java, "seal_attached")
        }

        return info
    }

    override fun emitter(): Emitter? {
        if (seal == null) return super.emitter()
        val emitter = Emitter()
        emitter.pos(ItemSpriteSheet.film.width(image) / 2f + 2f, ItemSpriteSheet.film.height(image) / 3f)
        emitter.fillTarget = false
        emitter.pour(Speck.factory(Speck.RED_LIGHT), 0.6f)
        return emitter
    }

    override fun random(): Item {
        //+0: 75% (3/4)
        //+1: 20% (4/20)
        //+2: 5%  (1/20)
        var n = 0
        if (Random.Int(4) == 0) {
            n++
            if (Random.Int(5) == 0) {
                n++
            }
        }
        level(n)

        //30% chance to be cursed
        //15% chance to be inscribed
        val effectRoll = Random.Float()
        if (effectRoll < 0.3f) {
            inscribe(Glyph.randomCurse())
            cursed = true
        } else if (effectRoll >= 0.85f) {
            inscribe()
        }

        return this
    }

    fun STRReq(): Int {
        return STRReq(level())
    }

    open fun STRReq(lvl: Int): Int {
        var lvl = lvl
        lvl = Math.max(0, lvl)
        var effectiveTier = tier.toFloat()
        if (glyph != null) effectiveTier += glyph!!.tierSTRAdjust()
        effectiveTier = Math.max(0f, effectiveTier)

        //strength req decreases at +1,+3,+6,+10,etc.
        return 8 + Math.round(effectiveTier * 2) - (Math.sqrt((8 * lvl + 1).toDouble()) - 1).toInt() / 2
    }

    override fun price(): Int {
        if (seal != null) return 0

        var price: Double = 20.0 * tier
        if (hasGoodGlyph()) {
            price *= 1.5
        }
        if (cursedKnown && (cursed || hasCurseGlyph())) {
            price /= 2
        }
        if (levelKnown && level() > 0) {
            price *= level() + 1
        }
        if (price < 1) {
            price = 1.0
        }
        return price.toInt()
    }

    fun inscribe(glyph: Glyph?): Armor {
        this.glyph = glyph

        return this
    }

    fun inscribe(): Armor {

        val oldGlyphClass = if (glyph != null) glyph!!.javaClass else null
        var gl = Glyph.random()
        while (gl!!.javaClass == oldGlyphClass) {
            gl = Armor.Glyph.random()
        }

        return inscribe(gl)
    }

    fun hasGlyph(type: Class<out Glyph>): Boolean {
        return glyph != null && glyph!!.javaClass == type
    }

    fun hasGoodGlyph(): Boolean {
        return glyph != null && !glyph!!.curse()
    }

    fun hasCurseGlyph(): Boolean {
        return glyph != null && glyph!!.curse()
    }

    override fun glowing(): ItemSprite.Glowing? {
        return if (glyph != null && (cursedKnown || !glyph!!.curse())) glyph!!.glowing() else null
    }

    abstract class Glyph : Bundlable {

        abstract fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int

        fun name(): String {
            return if (!curse())
                name(Messages.get(this.javaClass, "glyph"))
            else
                name(Messages.get(Item::class.java, "curse"))
        }

        fun name(armorName: String): String {
            return Messages.get(this.javaClass, "name", armorName)
        }

        fun desc(): String {
            return Messages.get(this.javaClass, "desc")
        }

        open fun curse(): Boolean {
            return false
        }

        override fun restoreFromBundle(bundle: Bundle) {}

        override fun storeInBundle(bundle: Bundle) {}

        abstract fun glowing(): ItemSprite.Glowing

        open fun tierDRAdjust(): Int {
            return 0
        }

        open fun tierSTRAdjust(): Float {
            return 0f
        }

        fun checkOwner(owner: Char): Boolean {
            if (!owner.isAlive && owner is Hero) {

                Dungeon.fail(javaClass)
                GLog.n(Messages.get(this.javaClass, "killed", name()))

                Badges.validateDeathFromGlyph()
                return true

            } else {
                return false
            }
        }

        companion object {

            private val glyphs = arrayOf<Class<*>>(Obfuscation::class.java, Swiftness::class.java, Stone::class.java, Potential::class.java, Brimstone::class.java, Viscosity::class.java, Entanglement::class.java, Repulsion::class.java, Camouflage::class.java, Flow::class.java, Affection::class.java, AntiMagic::class.java, Thorns::class.java)
            private val chances = floatArrayOf(10f, 10f, 10f, 10f, 5f, 5f, 5f, 5f, 5f, 5f, 2f, 2f, 2f)

            private val curses = arrayOf<Class<*>>(AntiEntropy::class.java, Corrosion::class.java, Displacement::class.java, Metabolism::class.java, Multiplicity::class.java, Stench::class.java)

            fun random(): Glyph? {
                try {
                    return (glyphs[Random.chances(chances)] as Class<Glyph>).newInstance()
                } catch (e: Exception) {
                    Game.reportException(e)
                    return null
                }

            }

            fun randomCurse(): Glyph? {
                try {
                    return (Random.oneOf(*curses) as Class<Glyph>).newInstance()
                } catch (e: Exception) {
                    Game.reportException(e)
                    return null
                }

            }
        }

    }

    companion object {

        private val HITS_TO_KNOW = 10

        protected val AC_DETACH = "DETACH"

        private val UNFAMILIRIARITY = "unfamiliarity"
        private val GLYPH = "glyph"
        private val SEAL = "seal"
    }
}
