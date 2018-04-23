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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Bones
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Fury
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.SurfaceScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndAlchemy
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.GameMath
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Collections

class Hero : Char() {

    var heroClass = HeroClass.ROGUE
    var subClass: HeroSubClass? = HeroSubClass.NONE

    private var attackSkill = 10
    private var defenseSkill = 5

    var ready = false
    private var damageInterrupt = true
    var curAction: HeroAction? = null
    var lastAction: HeroAction? = null

    private var enemy: Char? = null

    var resting = false

    var belongings: Belongings

    var STR: Int = 0

    var awareness: Float = 0.toFloat()

    var lvl = 1
    var exp = 0

    var HTBoost = 0

    private var visibleEnemies: ArrayList<Mob>? = null

    //This list is maintained so that some logic checks can be skipped
    // for enemies we know we aren't seeing normally, resultign in better performance
    var mindVisionEnemies = ArrayList<Mob>()

    //this variable is only needed because of the boomerang, remove if/when it is no longer equippable
    internal var rangedAttack = false

    //FIXME this is a fairly crude way to track this, really it would be nice to have a short
    //history of hero actions
    var justMoved = false

    val isStarving: Boolean
        get() = buff<Hunger>(Hunger::class.java) != null && (buff<Hunger>(Hunger::class.java) as Hunger).isStarving

    //effectively cache this buff to prevent having to call buff(Berserk.class) a bunch.
    //This is relevant because we call isAlive during drawing, which has both performance
    //and concurrent modification implications if that method calls buff(Berserk.class)
    private var berserk: Berserk? = null

    override val isAlive: Boolean
        get() = if (subClass == HeroSubClass.BERSERKER
                && berserk != null
                && berserk!!.berserking()
                && SHLD > 0) {
            true
        } else super.isAlive

    init {
        actPriority = HERO_PRIO

        alignment = Char.Alignment.ALLY
    }

    init {
        name = Messages.get(this.javaClass, "name")

        HT = 20
        HP = HT
        STR = STARTING_STR

        belongings = Belongings(this)

        visibleEnemies = ArrayList()
    }

    fun updateHT(boostHP: Boolean) {
        val curHT = HT

        HT = 20 + 5 * (lvl - 1) + HTBoost
        val multiplier = RingOfMight.HTMultiplier(this)
        HT = Math.round(multiplier * HT)

        if (boostHP) {
            HP += Math.max(HT - curHT, 0)
        }
        HP = Math.min(HP, HT)
    }

    fun STR(): Int {
        var STR = this.STR

        STR += RingOfMight.strengthBonus(this)

        return if (buff<Weakness>(Weakness::class.java) != null) STR - 2 else STR
    }

    override fun storeInBundle(bundle: Bundle) {

        super.storeInBundle(bundle)

        heroClass.storeInBundle(bundle)
        subClass!!.storeInBundle(bundle)

        bundle.put(ATTACK, attackSkill)
        bundle.put(DEFENSE, defenseSkill)

        bundle.put(STRENGTH, STR)

        bundle.put(LEVEL, lvl)
        bundle.put(EXPERIENCE, exp)

        bundle.put(HTBOOST, HTBoost)

        belongings.storeInBundle(bundle)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)

        heroClass = HeroClass.restoreInBundle(bundle)
        subClass = HeroSubClass.restoreInBundle(bundle)

        berserk = if (subClass == HeroSubClass.BERSERKER) buff(Berserk::class.java) else null

        attackSkill = bundle.getInt(ATTACK)
        defenseSkill = bundle.getInt(DEFENSE)

        STR = bundle.getInt(STRENGTH)

        lvl = bundle.getInt(LEVEL)
        exp = bundle.getInt(EXPERIENCE)

        HTBoost = bundle.getInt(HTBOOST)

        belongings.restoreFromBundle(bundle)
    }

    fun className(): String {
        return if (subClass == null || subClass == HeroSubClass.NONE) heroClass.title() else subClass!!.title()
    }

    fun givenName(): String {
        return if (name == Messages.get(this.javaClass, "name")) className() else name
    }

    fun live() {
        Buff.affect<Regeneration>(this, Regeneration::class.java)
        Buff.affect<Hunger>(this, Hunger::class.java)
    }

    fun tier(): Int {
        return if (belongings.armor == null) 0 else belongings.armor!!.tier
    }

    fun shoot(enemy: Char, wep: MissileWeapon): Boolean {

        //temporarily set the hero's weapon to the missile weapon being used
        val equipped = belongings.weapon
        belongings.weapon = wep
        rangedAttack = true
        val result = attack(enemy)
        Invisibility.dispel()
        belongings.weapon = equipped
        rangedAttack = false

        return result
    }

    override fun attackSkill(target: Char?): Int {
        val wep = belongings.weapon

        var accuracy = 1f
        if (wep is MissileWeapon && rangedAttack
                && Dungeon.level!!.distance(pos, target!!.pos) == 1) {
            accuracy *= 0.5f
        }

        return if (wep != null) {
            (attackSkill.toFloat() * accuracy * wep.accuracyFactor(this)).toInt()
        } else {
            (attackSkill * accuracy).toInt()
        }
    }

    override fun defenseSkill(enemy: Char?): Int {

        var multiplier = 1f * RingOfEvasion.evasionMultiplier(this)

        if (paralysed > 0) {
            multiplier /= 2f
        }

        val aEnc = if (belongings.armor != null) belongings.armor!!.STRReq() - STR() else 10 - STR()

        if (aEnc > 0) {
            multiplier /= Math.pow(1.5, aEnc.toDouble()).toFloat()
        }
        var bonus = 0

        if (belongings.armor != null && belongings.armor!!.hasGlyph(Swiftness::class.java))
            bonus += (5 + belongings.armor!!.level() * 1.5f).toInt()

        val momentum = buff<Momentum>(Momentum::class.java)
        if (momentum != null) {
            bonus += momentum.evasionBonus(Math.max(0, -aEnc))
        }

        return Math.round(defenseSkill * multiplier + bonus)
    }

    override fun drRoll(): Int {
        var dr = 0
        val bark = buff<Barkskin>(Barkskin::class.java)

        if (belongings.armor != null) {
            dr += Random.NormalIntRange(belongings.armor!!.DRMin(), belongings.armor!!.DRMax())
            if (STR() < belongings.armor!!.STRReq()) {
                dr -= 2 * (belongings.armor!!.STRReq() - STR())
                dr = Math.max(dr, 0)
            }
        }
        if (belongings.weapon != null) dr += Random.NormalIntRange(0, belongings.weapon!!.defenseFactor(this))
        if (bark != null) dr += Random.NormalIntRange(0, bark.level())

        return dr
    }

    override fun damageRoll(): Int {
        val wep = belongings.weapon
        var dmg: Int

        if (wep != null) {
            dmg = wep.damageRoll(this) + RingOfForce.armedDamageBonus(this)
        } else {
            dmg = RingOfForce.damageRoll(this)
        }
        if (dmg < 0) dmg = 0
        if (subClass == HeroSubClass.BERSERKER) {
            berserk = Buff.affect(this, Berserk::class.java)
            dmg = berserk!!.damageFactor(dmg)
        }
        return if (buff<Fury>(Fury::class.java) != null) (dmg * 1.5f).toInt() else dmg
    }

    override fun speed(): Float {

        var speed = super.speed()

        speed *= RingOfHaste.speedMultiplier(this)

        val armor = belongings.armor

        if (armor != null) {

            if (armor.hasGlyph(Swiftness::class.java)) {
                speed *= 1.1f + 0.01f * belongings.armor!!.level()
            } else if (armor.hasGlyph(Flow::class.java) && Dungeon.level!!.water[pos]) {
                speed *= 1.5f + 0.05f * belongings.armor!!.level()
            }
        }

        val aEnc = if (armor != null) armor.STRReq() - STR() else 0
        if (aEnc > 0) speed /= Math.pow(1.2, aEnc.toDouble()).toFloat()

        val momentum = buff<Momentum>(Momentum::class.java)
        if (momentum != null) {
            (sprite as HeroSprite).sprint(1f + 0.05f * momentum.stacks())
            speed *= momentum.speedMultiplier()
        }

        return speed

    }

    fun canSurpriseAttack(): Boolean {
        if (belongings.weapon == null || belongings.weapon !is Weapon) return true
        if (STR() < (belongings.weapon as Weapon).STRReq()) return false
        return if (belongings.weapon is Flail) false else true

    }

    fun canAttack(enemy: Char?): Boolean {
        if (enemy == null || pos == enemy.pos)
            return false

        //can always attack adjacent enemies
        if (Dungeon.level!!.adjacent(pos, enemy.pos))
            return true

        val wep = Dungeon.hero!!.belongings.weapon

        if (wep != null && Dungeon.level!!.distance(pos, enemy.pos) <= wep.reachFactor(this)) {

            val passable = BArray.not(Dungeon.level!!.solid, null)
            for (m in Dungeon.level!!.mobs)
                passable[m.pos] = false

            PathFinder.buildDistanceMap(enemy.pos, passable, wep.reachFactor(this))

            return PathFinder.distance!![pos] <= wep.reachFactor(this)

        } else {
            return false
        }
    }

    fun attackDelay(): Float {
        return if (belongings.weapon != null) {

            belongings.weapon!!.speedFactor(this)

        } else {
            //Normally putting furor speed on unarmed attacks would be unnecessary
            //But there's going to be that one guy who gets a furor+force ring combo
            //This is for that one guy, you shall get your fists of fury!
            RingOfFuror.modifyAttackDelay(1f, this)
        }
    }

    public override fun spend(time: Float) {
        justMoved = false
        val buff = buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)
        if (buff != null) {
            buff.processTime(time)
        } else {
            super.spend(time)
        }
    }

    fun spendAndNext(time: Float) {
        busy()
        spend(time)
        next()
    }

    public override fun act(): Boolean {

        //calls to dungeon.observe will also update hero's local FOV.
        fieldOfView = Dungeon.level!!.heroFOV


        if (!ready) {
            //do a full observe (including fog update) if not resting.
            if (!resting || buff<MindVision>(MindVision::class.java) != null || buff<Awareness>(Awareness::class.java) != null) {
                Dungeon.observe()
            } else {
                //otherwise just directly re-calculate FOV
                Dungeon.level!!.updateFieldOfView(this, fieldOfView!!)
            }
        }

        checkVisibleMobs()
        if (buff<FlavourBuff>(FlavourBuff::class.java) != null) {
            BuffIndicator.refreshHero()
        }

        if (paralysed > 0) {

            curAction = null

            spendAndNext(Actor.TICK)
            return false
        }

        if (curAction == null) {

            if (resting) {
                spend(TIME_TO_REST)
                next()
                return false
            }

            ready()
            return false

        } else {

            resting = false

            ready = false

            if (curAction is HeroAction.Move) {

                return actMove((curAction as HeroAction.Move?)!!)

            } else if (curAction is HeroAction.Interact) {

                return actInteract((curAction as HeroAction.Interact?)!!)

            } else if (curAction is HeroAction.Buy) {

                return actBuy((curAction as HeroAction.Buy?)!!)

            } else if (curAction is HeroAction.PickUp) {

                return actPickUp((curAction as HeroAction.PickUp?)!!)

            } else if (curAction is HeroAction.OpenChest) {

                return actOpenChest((curAction as HeroAction.OpenChest?)!!)

            } else if (curAction is HeroAction.Unlock) {

                return actUnlock((curAction as HeroAction.Unlock?)!!)

            } else if (curAction is HeroAction.Descend) {

                return actDescend((curAction as HeroAction.Descend?)!!)

            } else if (curAction is HeroAction.Ascend) {

                return actAscend((curAction as HeroAction.Ascend?)!!)

            } else if (curAction is HeroAction.Attack) {

                return actAttack((curAction as HeroAction.Attack?)!!)

            } else if (curAction is HeroAction.Alchemy) {

                return actAlchemy((curAction as HeroAction.Alchemy?)!!)

            }
        }

        return false
    }

    fun busy() {
        ready = false
    }

    private fun ready() {
        if (sprite!!.looping()) sprite!!.idle()
        curAction = null
        damageInterrupt = true
        ready = true

        AttackIndicator.updateState()

        GameScene.ready()
    }

    fun interrupt() {
        if (isAlive && curAction != null &&
                (curAction is HeroAction.Move && curAction!!.dst != pos || curAction is HeroAction.Ascend || curAction is HeroAction.Descend)) {
            lastAction = curAction
        }
        curAction = null
    }

    fun resume() {
        curAction = lastAction
        lastAction = null
        damageInterrupt = false
        next()
    }

    private fun actMove(action: HeroAction.Move): Boolean {

        if (getCloser(action.dst)) {
            justMoved = true
            return true

        } else {
            ready()
            return false
        }
    }

    private fun actInteract(action: HeroAction.Interact): Boolean {

        val npc = action.npc

        if (Dungeon.level!!.adjacent(pos, npc.pos)) {

            ready()
            sprite!!.turnTo(pos, npc.pos)
            return npc.interact()

        } else {

            if (fieldOfView!![npc.pos] && getCloser(npc.pos)) {

                return true

            } else {
                ready()
                return false
            }

        }
    }

    private fun actBuy(action: HeroAction.Buy): Boolean {
        val dst = action.dst
        if (pos == dst || Dungeon.level!!.adjacent(pos, dst)) {

            ready()

            val heap = Dungeon.level!!.heaps.get(dst)
            if (heap != null && heap.type == Type.FOR_SALE && heap.size() == 1) {
                GameScene.show(WndTradeItem(heap, true))
            }

            return false

        } else if (getCloser(dst)) {

            return true

        } else {
            ready()
            return false
        }
    }

    private fun actAlchemy(action: HeroAction.Alchemy): Boolean {
        val dst = action.dst
        if (Dungeon.level!!.distance(dst, pos) <= 1) {

            ready()
            GameScene.show(WndAlchemy())
            return false

        } else if (getCloser(dst)) {

            return true

        } else {
            ready()
            return false
        }
    }

    private fun actPickUp(action: HeroAction.PickUp): Boolean {
        val dst = action.dst
        if (pos == dst) {

            val heap = Dungeon.level!!.heaps.get(pos)
            if (heap != null) {
                val item = heap.peek()
                if (item.doPickUp(this)) {
                    heap.pickUp()

                    if (item is Dewdrop
                            || item is TimekeepersHourglass.sandBag
                            || item is DriedRose.Petal
                            || item is Key) {
                        //Do Nothing
                    } else {

                        val important = (item is ScrollOfUpgrade || item is ScrollOfMagicalInfusion) && (item as Scroll).isKnown || (item is PotionOfStrength || item is PotionOfMight) && (item as Potion).isKnown
                        if (important) {
                            GLog.p(Messages.get(this.javaClass, "you_now_have", item.name()))
                        } else {
                            GLog.i(Messages.get(this.javaClass, "you_now_have", item.name()))
                        }
                    }

                    curAction = null
                } else {
                    heap.sprite!!.drop()
                    ready()
                }
            } else {
                ready()
            }

            return false

        } else if (getCloser(dst)) {

            return true

        } else {
            ready()
            return false
        }
    }

    private fun actOpenChest(action: HeroAction.OpenChest): Boolean {
        val dst = action.dst
        if (Dungeon.level!!.adjacent(pos, dst) || pos == dst) {

            val heap = Dungeon.level!!.heaps.get(dst)
            if (heap != null && heap.type != Type.HEAP && heap.type != Type.FOR_SALE) {

                if (heap.type == Type.LOCKED_CHEST && Notes.keyCount(GoldenKey(Dungeon.depth)) < 1 || heap.type == Type.CRYSTAL_CHEST && Notes.keyCount(CrystalKey(Dungeon.depth)) < 1) {

                    GLog.w(Messages.get(this.javaClass, "locked_chest"))
                    ready()
                    return false

                }

                when (heap.type) {
                    Heap.Type.TOMB -> {
                        Sample.INSTANCE.play(Assets.SND_TOMB)
                        Camera.main!!.shake(1f, 0.5f)
                    }
                    Heap.Type.SKELETON, Heap.Type.REMAINS -> {
                    }
                    else -> Sample.INSTANCE.play(Assets.SND_UNLOCK)
                }

                spend(Key.TIME_TO_UNLOCK)
                sprite!!.operate(dst)

            } else {
                ready()
            }

            return false

        } else if (getCloser(dst)) {

            return true

        } else {
            ready()
            return false
        }
    }

    private fun actUnlock(action: HeroAction.Unlock): Boolean {
        val doorCell = action.dst
        if (Dungeon.level!!.adjacent(pos, doorCell)) {

            var hasKey = false
            val door = Dungeon.level!!.map!![doorCell]

            if (door == Terrain.LOCKED_DOOR && Notes.keyCount(IronKey(Dungeon.depth)) > 0) {

                hasKey = true

            } else if (door == Terrain.LOCKED_EXIT && Notes.keyCount(SkeletonKey(Dungeon.depth)) > 0) {

                hasKey = true

            }

            if (hasKey) {

                spend(Key.TIME_TO_UNLOCK)
                sprite!!.operate(doorCell)

                Sample.INSTANCE.play(Assets.SND_UNLOCK)

            } else {
                GLog.w(Messages.get(this.javaClass, "locked_door"))
                ready()
            }

            return false

        } else if (getCloser(doorCell)) {

            return true

        } else {
            ready()
            return false
        }
    }

    private fun actDescend(action: HeroAction.Descend): Boolean {
        val stairs = action.dst
        if (pos == stairs && pos == Dungeon.level!!.exit) {

            curAction = null

            val buff = buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)
            buff?.detach()

            InterlevelScene.mode = InterlevelScene.Mode.DESCEND
            Game.switchScene(InterlevelScene::class.java)

            return false

        } else if (getCloser(stairs)) {

            return true

        } else {
            ready()
            return false
        }
    }

    private fun actAscend(action: HeroAction.Ascend): Boolean {
        val stairs = action.dst
        if (pos == stairs && pos == Dungeon.level!!.entrance) {

            if (Dungeon.depth == 1) {

                if (belongings.getItem<Amulet>(Amulet::class.java) == null) {
                    GameScene.show(WndMessage(Messages.get(this.javaClass, "leave")))
                    ready()
                } else {
                    Dungeon.win(Amulet::class.java)
                    Dungeon.deleteGame(GamesInProgress.curSlot, true)
                    Game.switchScene(SurfaceScene::class.java)
                }

            } else {

                curAction = null

                val buff = buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)
                buff?.detach()

                InterlevelScene.mode = InterlevelScene.Mode.ASCEND
                Game.switchScene(InterlevelScene::class.java)
            }

            return false

        } else if (getCloser(stairs)) {

            return true

        } else {
            ready()
            return false
        }
    }

    private fun actAttack(action: HeroAction.Attack): Boolean {

        enemy = action.target

        if (enemy!!.isAlive && canAttack(enemy) && !isCharmedBy(enemy!!)) {

            Invisibility.dispel()
            spend(attackDelay())
            sprite!!.attack(enemy!!.pos)

            return false

        } else {

            if (fieldOfView!![enemy!!.pos] && getCloser(enemy!!.pos)) {

                return true

            } else {
                ready()
                return false
            }

        }
    }

    fun enemy(): Char? {
        return enemy
    }

    fun rest(fullRest: Boolean) {
        spendAndNext(TIME_TO_REST)
        if (!fullRest) {
            sprite!!.showStatus(CharSprite.DEFAULT, Messages.get(this.javaClass, "wait"))
        }
        resting = fullRest
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        val wep = belongings.weapon

        if (wep != null) damage = wep.proc(this, enemy, damage)

        when (subClass) {
            HeroSubClass.SNIPER -> if (wep is MissileWeapon && rangedAttack) {
                Buff.prolong<SnipersMark>(this, SnipersMark::class.java, attackDelay()).`object` = enemy.id()
            }
        }


        return damage
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {
        var damage = damage

        val armor = buff<Earthroot.Armor>(Earthroot.Armor::class.java)
        if (armor != null) {
            damage = armor.absorb(damage)
        }

        if (belongings.armor != null) {
            damage = belongings.armor!!.proc(enemy, this, damage)
        }

        return damage
    }

    override fun damage(dmg: Int, src: Any) {
        var dmg = dmg
        if (buff<TimekeepersHourglass.timeStasis>(TimekeepersHourglass.timeStasis::class.java) != null)
            return

        if (!(src is Hunger || src is Viscosity.DeferedDamage) && damageInterrupt) {
            interrupt()
            resting = false
        }

        if (this.buff<Drowsy>(Drowsy::class.java) != null) {
            Buff.detach(this, Drowsy::class.java)
            GLog.w(Messages.get(this.javaClass, "pain_resist"))
        }

        val thorns = buff<CapeOfThorns.Thorns>(CapeOfThorns.Thorns::class.java)
        if (thorns != null) {
            dmg = thorns.proc(dmg, src as? Char, this)
        }

        dmg = Math.ceil((dmg * RingOfTenacity.damageMultiplier(this)).toDouble()).toInt()

        //TODO improve this when I have proper damage source logic
        if (belongings.armor != null && belongings.armor!!.hasGlyph(AntiMagic::class.java)
                && RingOfElements.RESISTS.contains(src.javaClass)) {
            dmg -= Random.NormalIntRange(belongings.armor!!.DRMin(), belongings.armor!!.DRMax()) / 3
        }

        if (subClass == HeroSubClass.BERSERKER && berserk == null) {
            berserk = Buff.affect(this, Berserk::class.java)
        }

        super.damage(dmg, src)
    }

    fun checkVisibleMobs() {
        val visible = ArrayList<Mob>()

        var newMob = false

        var target: Mob? = null
        for (m in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
            if (fieldOfView!![m.pos] && m.alignment == Char.Alignment.ENEMY) {
                visible.add(m)
                if (!visibleEnemies!!.contains(m)) {
                    newMob = true
                }

                if (!mindVisionEnemies.contains(m) && QuickSlotButton.autoAim(m) != -1) {
                    if (target == null) {
                        target = m
                    } else if (distance(target) > distance(m)) {
                        target = m
                    }
                }
            }
        }

        if (target != null && (QuickSlotButton.lastTarget == null ||
                        !QuickSlotButton.lastTarget!!.isAlive ||
                        !fieldOfView!![QuickSlotButton.lastTarget!!.pos])) {
            QuickSlotButton.target(target)
        }

        if (newMob) {
            interrupt()
            resting = false
        }

        visibleEnemies = visible
    }

    fun visibleEnemies(): Int {
        return visibleEnemies!!.size
    }

    fun visibleEnemy(index: Int): Mob {
        return visibleEnemies!![index % visibleEnemies!!.size]
    }

    private fun getCloser(target: Int): Boolean {

        if (target == pos)
            return false

        if (rooted) {
            Camera.main!!.shake(1f, 1f)
            return false
        }

        var step = -1

        if (Dungeon.level!!.adjacent(pos, target)) {

            path = null

            if (Actor.findChar(target) == null) {
                if (Dungeon.level!!.pit[target] && !flying && !Dungeon.level!!.solid[target]) {
                    if (!Chasm.jumpConfirmed) {
                        Chasm.heroJump(this)
                        interrupt()
                    } else {
                        Chasm.heroFall(target)
                    }
                    return false
                }
                if (Dungeon.level!!.passable[target] || Dungeon.level!!.avoid[target]) {
                    step = target
                }
            }

        } else {

            var newPath = false
            if (path == null || path!!.isEmpty() || !Dungeon.level!!.adjacent(pos, path!!.first))
                newPath = true
            else if (path!!.last != target)
                newPath = true
            else {
                //looks ahead for path validity, up to length-1 or 2.
                //Note that this is shorter than for mobs, so that mobs usually yield to the hero
                val lookAhead = GameMath.gate(0f, (path!!.size - 1).toFloat(), 2f).toInt()
                for (i in 0 until lookAhead) {
                    val cell = path!![i]
                    if (!Dungeon.level!!.passable[cell] || fieldOfView!![cell] && Actor.findChar(cell) != null) {
                        newPath = true
                        break
                    }
                }
            }

            if (newPath) {

                val len = Dungeon.level!!.length()
                val p = Dungeon.level!!.passable
                val v = Dungeon.level!!.visited
                val m = Dungeon.level!!.mapped
                val passable = BooleanArray(len)
                for (i in 0 until len) {
                    passable[i] = p[i] && (v!![i] || m!![i])
                }

                path = Dungeon.findPath(this, pos, target, passable, fieldOfView!!)
            }

            if (path == null) return false
            step = path!!.removeFirst()

        }

        if (step != -1) {

            var moveTime = 1
            if (belongings.armor != null && belongings.armor!!.hasGlyph(Stone::class.java) &&
                    (Dungeon.level!!.map!![pos] == Terrain.DOOR
                            || Dungeon.level!!.map!![pos] == Terrain.OPEN_DOOR
                            || Dungeon.level!!.map!![step] == Terrain.DOOR
                            || Dungeon.level!!.map!![step] == Terrain.OPEN_DOOR)) {
                moveTime *= 2
            }
            sprite!!.move(pos, step)
            move(step)

            spend(moveTime / speed())

            search(false)

            if (subClass == HeroSubClass.FREERUNNER) {
                Buff.affect<Momentum>(this, Momentum::class.java)!!.gainStack()
            }

            //FIXME this is a fairly sloppy fix for a crash involving pitfall traps.
            //really there should be a way for traps to specify whether action should continue or
            //not when they are pressed.
            return InterlevelScene.mode != InterlevelScene.Mode.FALL

        } else {

            return false

        }

    }

    fun handle(cell: Int): Boolean {

        if (cell == -1) {
            return false
        }

        val ch: Char?
        val heap: Heap?

        if (Dungeon.level!!.map!![cell] == Terrain.ALCHEMY && cell != pos) {

            curAction = HeroAction.Alchemy(cell)

        } else {
            ch = Actor.findChar(cell)
            if (fieldOfView!![cell] && ch is Mob) {

                if (ch is NPC) {
                    curAction = HeroAction.Interact(ch)
                } else {
                    curAction = HeroAction.Attack(ch)
                }

            } else {
                heap = Dungeon.level!!.heaps.get(cell)
                if (heap != null
                        //moving to an item doesn't auto-pickup when enemies are near...
                        && (visibleEnemies!!.size == 0 || cell == pos ||
                        //...but only for standard heaps, chests and similar open as normal.
                        heap.type != Type.HEAP && heap.type != Type.FOR_SALE)) {

                    when (heap.type) {
                        Heap.Type.HEAP -> curAction = HeroAction.PickUp(cell)
                        Heap.Type.FOR_SALE -> curAction = if (heap.size() == 1 && heap.peek().price() > 0)
                            HeroAction.Buy(cell)
                        else
                            HeroAction.PickUp(cell)
                        else -> curAction = HeroAction.OpenChest(cell)
                    }

                } else if (Dungeon.level!!.map!![cell] == Terrain.LOCKED_DOOR || Dungeon.level!!.map!![cell] == Terrain.LOCKED_EXIT) {

                    curAction = HeroAction.Unlock(cell)

                } else if (cell == Dungeon.level!!.exit && Dungeon.depth < 26) {

                    curAction = HeroAction.Descend(cell)

                } else if (cell == Dungeon.level!!.entrance) {

                    curAction = HeroAction.Ascend(cell)

                } else {

                    curAction = HeroAction.Move(cell)
                    lastAction = null

                }
            }
        }

        return true
    }

    fun earnExp(exp: Int) {

        this.exp += exp
        val percent = exp / maxExp().toFloat()

        val chains = buff<EtherealChains.chainsRecharge>(EtherealChains.chainsRecharge::class.java)
        chains?.gainExp(percent)

        val horn = buff<HornOfPlenty.hornRecharge>(HornOfPlenty.hornRecharge::class.java)
        horn?.gainCharge(percent)

        if (subClass == HeroSubClass.BERSERKER) {
            berserk = Buff.affect(this, Berserk::class.java)
            berserk!!.recover(percent)
        }

        var levelUp = false
        while (this.exp >= maxExp()) {
            this.exp -= maxExp()
            if (lvl < MAX_LEVEL) {
                lvl++
                levelUp = true

                updateHT(true)
                attackSkill++
                defenseSkill++

            } else {
                Buff.prolong<Bless>(this, Bless::class.java, 30f)
                this.exp = 0

                GLog.p(Messages.get(this.javaClass, "level_cap"))
                Sample.INSTANCE.play(Assets.SND_LEVELUP)
            }

        }

        if (levelUp) {

            GLog.p(Messages.get(this.javaClass, "new_level"), lvl)
            sprite!!.showStatus(CharSprite.POSITIVE, Messages.get(Hero::class.java, "level_up"))
            Sample.INSTANCE.play(Assets.SND_LEVELUP)

            Badges.validateLevelReached()
        }
    }

    fun maxExp(): Int {
        return maxExp(lvl)
    }

    override fun add(buff: Buff) {

        if (buff<TimekeepersHourglass.timeStasis>(TimekeepersHourglass.timeStasis::class.java) != null)
            return

        super.add(buff)

        if (sprite != null) {
            val msg = buff.heroMessage()
            if (msg != null) {
                GLog.w(msg)
            }

            if (buff is Paralysis || buff is Vertigo) {
                interrupt()
            }

        }

        BuffIndicator.refreshHero()
    }

    override fun remove(buff: Buff) {
        super.remove(buff)

        BuffIndicator.refreshHero()
    }

    override fun stealth(): Int {
        var stealth = super.stealth()

        if (belongings.armor != null && belongings.armor!!.hasGlyph(Obfuscation::class.java)) {
            stealth += 1 + belongings.armor!!.level() / 3
        }
        return stealth
    }

    override fun die(cause: Any?) {

        curAction = null

        var ankh: Ankh? = null

        //look for ankhs in player inventory, prioritize ones which are blessed.
        for (item in belongings) {
            if (item is Ankh) {
                if (ankh == null || item.isBlessed!!) {
                    ankh = item
                }
            }
        }

        if (ankh != null && ankh.isBlessed!!) {
            this.HP = HT / 4

            //ensures that you'll get to act first in almost any case, to prevent reviving and then instantly dieing again.
            Buff.detach(this, Paralysis::class.java)
            spend(-cooldown())

            Flare(8, 32f).color(0xFFFF66, true).show(sprite!!, 2f)
            CellEmitter.get(this.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)

            ankh.detach(belongings.backpack)

            Sample.INSTANCE.play(Assets.SND_TELEPORT)
            GLog.w(Messages.get(this.javaClass, "revive"))
            Statistics.ankhsUsed++

            return
        }

        Actor.fixTime()
        super.die(cause)

        if (ankh == null) {

            reallyDie(cause!!)

        } else {

            Dungeon.deleteGame(GamesInProgress.curSlot, false)
            GameScene.show(WndResurrect(ankh, cause!!))

        }
    }

    override fun move(step: Int) {
        super.move(step)

        if (!flying) {
            if (Dungeon.level!!.water[pos]) {
                Sample.INSTANCE.play(Assets.SND_WATER, 1f, 1f, Random.Float(0.8f, 1.25f))
            } else {
                Sample.INSTANCE.play(Assets.SND_STEP)
            }
        }
    }

    override fun onAttackComplete() {

        AttackIndicator.target(enemy!!)

        val hit = attack(enemy)

        if (subClass == HeroSubClass.GLADIATOR) {
            if (hit) {
                Buff.affect<Combo>(this, Combo::class.java)!!.hit()
            } else {
                val combo = buff<Combo>(Combo::class.java)
                combo?.miss()
            }
        }

        curAction = null

        super.onAttackComplete()
    }

    override fun onOperateComplete() {

        if (curAction is HeroAction.Unlock) {

            val doorCell = (curAction as HeroAction.Unlock).dst
            val door = Dungeon.level!!.map!![doorCell]

            if (door == Terrain.LOCKED_DOOR) {
                Notes.remove(IronKey(Dungeon.depth))
                Level.set(doorCell, Terrain.DOOR)
            } else {
                Notes.remove(SkeletonKey(Dungeon.depth))
                Level.set(doorCell, Terrain.UNLOCKED_EXIT)
            }
            GameScene.updateKeyDisplay()

            Level.set(doorCell, if (door == Terrain.LOCKED_DOOR) Terrain.DOOR else Terrain.UNLOCKED_EXIT)
            GameScene.updateMap(doorCell)

        } else if (curAction is HeroAction.OpenChest) {

            val heap = Dungeon.level!!.heaps.get((curAction as HeroAction.OpenChest).dst)!!
            if (heap.type == Type.SKELETON || heap.type == Type.REMAINS) {
                Sample.INSTANCE.play(Assets.SND_BONES)
            } else if (heap.type == Type.LOCKED_CHEST) {
                Notes.remove(GoldenKey(Dungeon.depth))
            } else if (heap.type == Type.CRYSTAL_CHEST) {
                Notes.remove(CrystalKey(Dungeon.depth))
            }
            GameScene.updateKeyDisplay()
            heap.open(this)
        }
        curAction = null

        super.onOperateComplete()
    }

    fun search(intentional: Boolean): Boolean {

        if (!isAlive) return false

        var smthFound = false

        val distance = if (heroClass == HeroClass.ROGUE) 2 else 1

        val cx = pos % Dungeon.level!!.width()
        val cy = pos / Dungeon.level!!.width()
        var ax = cx - distance
        if (ax < 0) {
            ax = 0
        }
        var bx = cx + distance
        if (bx >= Dungeon.level!!.width()) {
            bx = Dungeon.level!!.width() - 1
        }
        var ay = cy - distance
        if (ay < 0) {
            ay = 0
        }
        var by = cy + distance
        if (by >= Dungeon.level!!.height()) {
            by = Dungeon.level!!.height() - 1
        }

        val foresight = buff<TalismanOfForesight.Foresight>(TalismanOfForesight.Foresight::class.java)
        val cursed = foresight != null && foresight.isCursed

        for (y in ay..by) {
            var x = ax
            var p = ax + y * Dungeon.level!!.width()
            while (x <= bx) {

                if (fieldOfView!![p] && p != pos) {

                    if (intentional) {
                        sprite!!.parent!!.addToBack(CheckedCell(p))
                    }

                    if (Dungeon.level!!.secret[p]) {

                        val chance: Float
                        //intentional searches always succeed
                        if (intentional) {
                            chance = 1f

                            //unintentional searches always fail with a cursed talisman
                        } else if (cursed) {
                            chance = 0f

                            //unintentional trap detection scales from 40% at floor 0 to 30% at floor 25
                        } else if (Dungeon.level!!.map!![p] == Terrain.SECRET_TRAP) {
                            chance = 0.4f - Dungeon.depth / 250f

                            //unintentional door detection scales from 20% at floor 0 to 0% at floor 20
                        } else {
                            chance = 0.2f - Dungeon.depth / 100f
                        }

                        if (Random.Float() < chance) {

                            val oldValue = Dungeon.level!!.map!![p]

                            GameScene.discoverTile(p, oldValue)

                            Dungeon.level!!.discover(p)

                            ScrollOfMagicMapping.discover(p)

                            smthFound = true

                            if (foresight != null && !foresight.isCursed)
                                foresight.charge()
                        }
                    }
                }
                x++
                p++
            }
        }


        if (intentional) {
            sprite!!.showStatus(CharSprite.DEFAULT, Messages.get(this.javaClass, "search"))
            sprite!!.operate(pos)
            if (cursed) {
                GLog.n(Messages.get(this.javaClass, "search_distracted"))
                buff<Hunger>(Hunger::class.java)!!.reduceHunger(TIME_TO_SEARCH - 2 * HUNGER_FOR_SEARCH)
            } else {
                buff<Hunger>(Hunger::class.java)!!.reduceHunger(TIME_TO_SEARCH - HUNGER_FOR_SEARCH)
            }
            spendAndNext(TIME_TO_SEARCH)

        }

        if (smthFound) {
            GLog.w(Messages.get(this.javaClass, "noticed_smth"))
            Sample.INSTANCE.play(Assets.SND_SECRET)
            interrupt()
        }

        return smthFound
    }

    fun resurrect(resetLevel: Int) {

        HP = HT
        Dungeon.gold = 0
        exp = 0

        belongings.resurrect(resetLevel)

        live()
    }

    override fun next() {
        if (isAlive)
            super.next()
    }

    interface Doom {
        fun onDeath()
    }

    companion object {

        val MAX_LEVEL = 30

        val STARTING_STR = 10

        private val TIME_TO_REST = 1f
        private val TIME_TO_SEARCH = 2f
        private val HUNGER_FOR_SEARCH = 6f

        private val ATTACK = "attackSkill"
        private val DEFENSE = "defenseSkill"
        private val STRENGTH = "STR"
        private val LEVEL = "lvl"
        private val EXPERIENCE = "exp"
        private val HTBOOST = "htboost"

        fun preview(info: GamesInProgress.Info, bundle: Bundle) {
            info.level = bundle.getInt(LEVEL)
            info.str = bundle.getInt(STRENGTH)
            info.exp = bundle.getInt(EXPERIENCE)
            info.hp = bundle.getInt(com.shatteredpixel.shatteredpixeldungeon.actors.Char.TAG_HP)
            info.ht = bundle.getInt(com.shatteredpixel.shatteredpixeldungeon.actors.Char.TAG_HT)
            info.shld = bundle.getInt(com.shatteredpixel.shatteredpixeldungeon.actors.Char.TAG_SHLD)
            info.heroClass = HeroClass.restoreInBundle(bundle)
            info.subClass = HeroSubClass.restoreInBundle(bundle)
            Belongings.preview(info, bundle)
        }

        fun maxExp(lvl: Int): Int {
            return 5 + lvl * 5
        }

        fun reallyDie(cause: Any) {

            val length = Dungeon.level!!.length()
            val map = Dungeon.level!!.map
            val visited = Dungeon.level!!.visited
            val discoverable = Dungeon.level!!.discoverable

            for (i in 0 until length) {

                val terr = map!![i]

                if (discoverable[i]) {

                    visited!![i] = true
                    if (Terrain.flags[terr] and Terrain.SECRET != 0) {
                        Dungeon.level!!.discover(i)
                    }
                }
            }

            Bones.leave()

            Dungeon.observe()
            GameScene.updateFog()

            Dungeon.hero!!.belongings.identify()

            val pos = Dungeon.hero!!.pos

            val passable = ArrayList<Int>()
            for (ofs in PathFinder.NEIGHBOURS8!!) {
                val cell = pos + ofs
                if ((Dungeon.level!!.passable[cell] || Dungeon.level!!.avoid[cell]) && Dungeon.level!!.heaps.get(cell) == null) {
                    passable.add(cell)
                }
            }
            Collections.shuffle(passable)

            val items = ArrayList(Dungeon.hero!!.belongings.backpack.items)
            for (cell in passable) {
                if (items.isEmpty()) {
                    break
                }

                val item = Random.element(items)
                Dungeon.level!!.drop(item, cell).sprite!!.drop(pos)
                items.remove(item)
            }

            GameScene.gameOver()

            if (cause is Hero.Doom) {
                cause.onDeath()
            }

            Dungeon.deleteGame(GamesInProgress.curSlot, true)
        }
    }
}
