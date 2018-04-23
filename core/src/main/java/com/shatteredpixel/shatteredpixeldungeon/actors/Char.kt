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

package com.shatteredpixel.shatteredpixeldungeon.actors

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EarthImbue
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Speed
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ShockingDart
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.GameMath
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.Arrays
import java.util.HashSet

abstract class Char : Actor() {

    var pos = 0

    var sprite: CharSprite? = null

    var name = "mob"

    var HT: Int = 0
    var HP: Int = 0
    var SHLD: Int = 0

    protected var baseSpeed = 1f
    protected var path: PathFinder.Path? = null

    var paralysed = 0
    var rooted = false
    var flying = false
    var invisible = 0
    var alignment: Alignment? = null

    var viewDistance = 8

    protected var fieldOfView: BooleanArray? = null

    private val buffs = HashSet<Buff>()

    open val isAlive: Boolean
        get() = HP > 0

    protected val resistances = HashSet<Class<*>>()

    protected val immunities = HashSet<Class<*>>()

    protected var properties = HashSet<Property>()

    //these are relative to the hero
    enum class Alignment {
        ENEMY,
        NEUTRAL,
        ALLY
    }

    override fun act(): Boolean {
        if (fieldOfView == null || fieldOfView!!.size != Dungeon.level!!.length()) {
            fieldOfView = BooleanArray(Dungeon.level!!.length())
        }
        Dungeon.level!!.updateFieldOfView(this, fieldOfView!!)
        return false
    }

    override fun storeInBundle(bundle: Bundle) {

        super.storeInBundle(bundle)

        bundle.put(POS, pos)
        bundle.put(TAG_HP, HP)
        bundle.put(TAG_HT, HT)
        bundle.put(TAG_SHLD, SHLD)
        bundle.put(BUFFS, buffs)
    }

    override fun restoreFromBundle(bundle: Bundle) {

        super.restoreFromBundle(bundle)

        pos = bundle.getInt(POS)
        HP = bundle.getInt(TAG_HP)
        HT = bundle.getInt(TAG_HT)
        SHLD = bundle.getInt(TAG_SHLD)

        for (b in bundle.getCollection(BUFFS)) {
            if (b != null) {
                (b as Buff).attachTo(this)
            }
        }
    }

    open fun attack(enemy: Char?): Boolean {

        if (enemy == null || !enemy.isAlive) return false

        val visibleFight = Dungeon.level!!.heroFOV[pos] || Dungeon.level!!.heroFOV[enemy.pos]

        if (hit(this, enemy, false)) {

            var dr = enemy.drRoll()

            if (this is Hero) {
                val h = this
                if (h.belongings.weapon is MissileWeapon && h.subClass == HeroSubClass.SNIPER) {
                    dr = 0
                }
            }

            val dmg: Int
            val prep = buff<Preparation>(Preparation::class.java)
            if (prep != null) {
                dmg = prep.damageRoll(this, enemy)
            } else {
                dmg = damageRoll()
            }
            var effectiveDamage = Math.max(dmg - dr, 0)

            effectiveDamage = attackProc(enemy, effectiveDamage)
            effectiveDamage = enemy.defenseProc(this, effectiveDamage)

            if (visibleFight) {
                Sample.INSTANCE.play(Assets.SND_HIT, 1f, 1f, Random.Float(0.8f, 1.25f))
            }

            // If the enemy is already dead, interrupt the attack.
            // This matters as defence procs can sometimes inflict self-damage, such as armor glyphs.
            if (!enemy.isAlive) {
                return true
            }

            //TODO: consider revisiting this and shaking in more cases.
            var shake = 0f
            if (enemy === Dungeon.hero!!)
                shake = (effectiveDamage / (enemy.HT / 4)).toFloat()

            if (shake > 1f)
                Camera.main!!.shake(GameMath.gate(1f, shake, 5f), 0.3f)

            enemy.damage(effectiveDamage, this)

            if (buff<FireImbue>(FireImbue::class.java) != null)
                buff<FireImbue>(FireImbue::class.java)!!.proc(enemy)
            if (buff<EarthImbue>(EarthImbue::class.java) != null)
                buff<EarthImbue>(EarthImbue::class.java)!!.proc(enemy)

            enemy.sprite!!.bloodBurstA(sprite!!.center(), effectiveDamage)
            enemy.sprite!!.flash()

            if (!enemy.isAlive && visibleFight) {
                if (enemy === Dungeon.hero!!) {

                    Dungeon.fail(javaClass)
                    GLog.n(Messages.capitalize(Messages.get(Char::class.java, "kill", name)))

                } else if (this === Dungeon.hero!!) {
                    GLog.i(Messages.capitalize(Messages.get(Char::class.java, "defeat", enemy.name)))
                }
            }

            return true

        } else {

            if (visibleFight) {
                val defense = enemy.defenseVerb()
                enemy.sprite!!.showStatus(CharSprite.NEUTRAL, defense)

                Sample.INSTANCE.play(Assets.SND_MISS)
            }

            return false

        }
    }

    open fun attackSkill(target: Char?): Int {
        return 0
    }

    open fun defenseSkill(enemy: Char?): Int {
        return 0
    }

    fun defenseVerb(): String {
        return Messages.get(this.javaClass, "def_verb")
    }

    open fun drRoll(): Int {
        return 0
    }

    open fun damageRoll(): Int {
        return 1
    }

    open fun attackProc(enemy: Char, damage: Int): Int {
        return damage
    }

    open fun defenseProc(enemy: Char, damage: Int): Int {
        return damage
    }

    open fun speed(): Float {
        return if (buff<Cripple>(Cripple::class.java) == null) baseSpeed else baseSpeed * 0.5f
    }

    open fun damage(dmg: Int, src: Any) {
        var dmg = dmg

        if (!isAlive || dmg < 0) {
            return
        }
        if (this.buff<Frost>(Frost::class.java) != null) {
            Buff.detach(this, Frost::class.java)
        }
        if (this.buff<MagicalSleep>(MagicalSleep::class.java) != null) {
            Buff.detach(this, MagicalSleep::class.java)
        }
        if (this.buff<Doom>(Doom::class.java) != null) {
            dmg *= 2
        }

        val srcClass = src.javaClass
        if (isImmune(srcClass)) {
            dmg = 0
        } else {
            dmg = Math.round(dmg * resist(srcClass))
        }

        if (buff<Paralysis>(Paralysis::class.java) != null) {
            buff<Paralysis>(Paralysis::class.java)!!.processDamage(dmg)
        }

        //FIXME: when I add proper damage properties, should add an IGNORES_SHIELDS property to use here.
        if (src is Hunger || SHLD == 0) {
            HP -= dmg
        } else if (SHLD >= dmg) {
            SHLD -= dmg
        } else if (SHLD > 0) {
            HP -= dmg - SHLD
            SHLD = 0
        }

        sprite!!.showStatus(if (HP > HT / 2)
            CharSprite.WARNING
        else
            CharSprite.NEGATIVE,
                Integer.toString(dmg))

        if (HP < 0) HP = 0

        if (!isAlive) {
            die(src)
        }
    }

    open fun destroy() {
        HP = 0
        Actor.remove(this)
    }

    open fun die(src: Any?) {
        destroy()
        if (src !== Chasm::class.java) sprite!!.die()
    }

    override fun spend(time: Float) {

        var timeScale = 1f
        if (buff<Slow>(Slow::class.java) != null) {
            timeScale *= 0.5f
            //slowed and chilled do not stack
        } else if (buff<Chill>(Chill::class.java) != null) {
            timeScale *= buff<Chill>(Chill::class.java)!!.speedFactor()
        }
        if (buff<Speed>(Speed::class.java) != null) {
            timeScale *= 2.0f
        }

        super.spend(time / timeScale)
    }

    @Synchronized
    fun buffs(): HashSet<Buff> {
        return HashSet(buffs)
    }

    @Synchronized
    fun <T : Buff> buffs(c: Class<T>): HashSet<T> {
        val filtered = HashSet<T>()
        for (b in buffs) {
            if (c.isInstance(b)) {
                filtered.add(b as T)
            }
        }
        return filtered
    }

    @Synchronized
    fun <T : Buff> buff(c: Class<T>): T? {
        for (b in buffs) {
            if (c.isInstance(b)) {
                return b as T
            }
        }
        return null
    }

    @Synchronized
    fun isCharmedBy(ch: Char): Boolean {
        val chID = ch.id()
        for (b in buffs) {
            if (b is Charm && b.`object` == chID) {
                return true
            }
        }
        return false
    }

    @Synchronized
    open fun add(buff: Buff) {

        buffs.add(buff)
        Actor.add(buff)

        if (sprite != null)
            when (buff.type) {
                Buff.buffType.POSITIVE -> sprite!!.showStatus(CharSprite.POSITIVE, buff.toString())
                Buff.buffType.NEGATIVE -> sprite!!.showStatus(CharSprite.NEGATIVE, buff.toString())
                Buff.buffType.NEUTRAL -> sprite!!.showStatus(CharSprite.NEUTRAL, buff.toString())
                Buff.buffType.SILENT -> {
                }
                else -> {
                }
            }//show nothing

    }

    @Synchronized
    open fun remove(buff: Buff) {

        buffs.remove(buff)
        Actor.remove(buff)

    }

    @Synchronized
    fun remove(buffClass: Class<out Buff>) {
        for (buff in buffs(buffClass)) {
            remove(buff)
        }
    }

    @Synchronized
    override fun onRemove() {
        for (buff in buffs.toTypedArray<Buff>()) {
            buff.detach()
        }
    }

    @Synchronized
    open fun updateSpriteState() {
        for (buff in buffs) {
            buff.fx(true)
        }
    }

    open fun stealth(): Int {
        return 0
    }

    open fun move(step: Int) {
        var step = step

        if (Dungeon.level!!.adjacent(step, pos) && buff<Vertigo>(Vertigo::class.java) != null) {
            sprite!!.interruptMotion()
            val newPos = pos + PathFinder.NEIGHBOURS8!![Random.Int(8)]
            if (!(Dungeon.level!!.passable[newPos] || Dungeon.level!!.avoid[newPos]) || Actor.findChar(newPos) != null)
                return
            else {
                sprite!!.move(pos, newPos)
                step = newPos
            }
        }

        if (Dungeon.level!!.map!![pos] == Terrain.OPEN_DOOR) {
            Door.leave(pos)
        }

        pos = step

        if (flying && Dungeon.level!!.map!![pos] == Terrain.DOOR) {
            Door.enter(pos)
        }

        if (this !== Dungeon.hero!!) {
            sprite!!.visible = Dungeon.level!!.heroFOV[pos]
        }

        if (!flying) {
            Dungeon.level!!.press(pos, this)
        }
    }

    fun distance(other: Char): Int {
        return Dungeon.level!!.distance(pos, other.pos)
    }

    fun onMotionComplete() {
        //Does nothing by default
        //The main actor thread already accounts for motion,
        // so calling next() here isn't necessary (see Actor.process)
    }

    open fun onAttackComplete() {
        next()
    }

    open fun onOperateComplete() {
        next()
    }

    //returns percent effectiveness after resistances
    //TODO currently resistances reduce effectiveness by a static 50%, and do not stack.
    fun resist(effect: Class<*>): Float {
        val resists = HashSet<Class<*>>(resistances)
        for (p in properties()) {
            resists.addAll(p.resistances())
        }
        for (b in buffs()) {
            resists.addAll(b.resistances())
        }

        var result = 1f
        for (c in resists) {
            if (c.isAssignableFrom(effect)) {
                result *= 0.5f
            }
        }
        return result * RingOfElements.resist(this, effect)
    }

    fun isImmune(effect: Class<*>): Boolean {
        val immunes = HashSet<Class<*>>(immunities)
        for (p in properties()) {
            immunes.addAll(p.immunities())
        }
        for (b in buffs()) {
            immunes.addAll(b.immunities())
        }

        for (c in immunes) {
            if (c.isAssignableFrom(effect)) {
                return true
            }
        }
        return false
    }

    fun properties(): HashSet<Property> {
        return HashSet(properties)
    }

    enum class Property private constructor(private val resistances: HashSet<Class<*>> = HashSet(), private val immunities: HashSet<Class<*>> = HashSet()) {
        BOSS(HashSet<Class<*>>(Arrays.asList<Class<out Bundlable>>(Grim::class.java, ScrollOfPsionicBlast::class.java)),
                HashSet<Class<*>>(Arrays.asList<Class<Corruption>>(Corruption::class.java!!))),
        MINIBOSS(HashSet<Class<*>>(),
                HashSet<Class<*>>(Arrays.asList<Class<Corruption>>(Corruption::class.java!!))),
        UNDEAD,
        DEMONIC,
        INORGANIC(HashSet<Class<*>>(),
                HashSet<Class<*>>(Arrays.asList<Class<out Actor>>(Bleeding::class.java, ToxicGas::class.java, Poison::class.java))),
        BLOB_IMMUNE(HashSet<Class<*>>(),
                HashSet<Class<*>>(Arrays.asList<Class<Blob>>(Blob::class.java!!))),
        FIERY(HashSet<Class<*>>(Arrays.asList<Class<WandOfFireblast>>(WandOfFireblast::class.java!!)),
                HashSet<Class<*>>(Arrays.asList<Class<out Bundlable>>(Burning::class.java, Blazing::class.java))),
        ACIDIC(HashSet<Class<*>>(Arrays.asList<Class<out Actor>>(ToxicGas::class.java, Corrosion::class.java)),
                HashSet<Class<*>>(Arrays.asList<Class<Ooze>>(Ooze::class.java!!))),
        ELECTRIC(HashSet<Class<*>>(Arrays.asList<Class<out Bundlable>>(WandOfLightning::class.java, Shocking::class.java, Potential::class.java, Electricity::class.java, ShockingDart::class.java)),
                HashSet<Class<*>>()),
        IMMOVABLE;

        fun resistances(): HashSet<Class<*>> {
            return HashSet<Class<*>>(resistances)
        }

        fun immunities(): HashSet<Class<*>> {
            return HashSet<Class<*>>(immunities)
        }
    }

    companion object {

        @JvmStatic protected val POS = "pos"
        @JvmStatic protected val TAG_HP = "HP"
        @JvmStatic protected val TAG_HT = "HT"
        @JvmStatic protected val TAG_SHLD = "SHLD"
        @JvmStatic protected val BUFFS = "buffs"

        fun hit(attacker: Char, defender: Char, magic: Boolean): Boolean {
            var acuRoll = Random.Float(attacker.attackSkill(defender).toFloat())
            var defRoll = Random.Float(defender.defenseSkill(attacker).toFloat())
            if (attacker.buff<Bless>(Bless::class.java) != null) acuRoll *= 1.20f
            if (defender.buff<Bless>(Bless::class.java) != null) defRoll *= 1.20f
            return (if (magic) acuRoll * 2 else acuRoll) >= defRoll
        }
    }
}
