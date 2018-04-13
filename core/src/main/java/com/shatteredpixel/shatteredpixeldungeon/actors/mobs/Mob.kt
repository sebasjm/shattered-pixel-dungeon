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

import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Challenges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.GameMath
import com.watabou.utils.Random

import java.util.ArrayList
import java.util.HashSet

abstract class Mob : Char() {

    var SLEEPING: AiState = Sleeping()
    var HUNTING: AiState = Hunting()
    var WANDERING: AiState = Wandering()
    var FLEEING: AiState = Fleeing()
    var PASSIVE: AiState = Passive()
    var state = SLEEPING

    var spriteClass: Class<out CharSprite>? = null

    protected var target = -1

    protected var defenseSkill = 0

    var EXP = 1
    var maxLvl = Hero.MAX_LEVEL

    protected var enemy: Char? = null
    protected var enemySeen: Boolean = false
    protected var alerted = false

    protected var loot: Any? = null
    protected var lootChance = 0f

    init {
        name = Messages.get(this, "name")
        actPriority = MOB_PRIO

        alignment = Char.Alignment.ENEMY
    }

    override fun storeInBundle(bundle: Bundle) {

        super.storeInBundle(bundle)

        if (state === SLEEPING) {
            bundle.put(STATE, Sleeping.TAG)
        } else if (state === WANDERING) {
            bundle.put(STATE, Wandering.TAG)
        } else if (state === HUNTING) {
            bundle.put(STATE, Hunting.TAG)
        } else if (state === FLEEING) {
            bundle.put(STATE, Fleeing.TAG)
        } else if (state === PASSIVE) {
            bundle.put(STATE, Passive.TAG)
        }
        bundle.put(SEEN, enemySeen)
        bundle.put(TARGET, target)
    }

    override fun restoreFromBundle(bundle: Bundle) {

        super.restoreFromBundle(bundle)

        val state = bundle.getString(STATE)
        if (state == Sleeping.TAG) {
            this.state = SLEEPING
        } else if (state == Wandering.TAG) {
            this.state = WANDERING
        } else if (state == Hunting.TAG) {
            this.state = HUNTING
        } else if (state == Fleeing.TAG) {
            this.state = FLEEING
        } else if (state == Passive.TAG) {
            this.state = PASSIVE
        }

        enemySeen = bundle.getBoolean(SEEN)

        target = bundle.getInt(TARGET)
    }

    open fun sprite(): CharSprite? {
        var sprite: CharSprite? = null
        try {
            sprite = spriteClass!!.newInstance()
        } catch (e: Exception) {
            ShatteredPixelDungeon.reportException(e)
        }

        return sprite
    }

    override fun act(): Boolean {

        super.act()

        val justAlerted = alerted
        alerted = false

        if (justAlerted) {
            sprite!!.showAlert()
        } else {
            sprite!!.hideAlert()
            sprite!!.hideLost()
        }

        if (paralysed > 0) {
            enemySeen = false
            spend(Actor.TICK)
            return true
        }

        enemy = chooseEnemy()

        val enemyInFOV = enemy != null && enemy!!.isAlive && fieldOfView!![enemy!!.pos] && enemy!!.invisible <= 0

        return state.act(enemyInFOV, justAlerted)
    }

    protected open fun chooseEnemy(): Char? {

        val terror = buff<Terror>(Terror::class.java)
        if (terror != null) {
            val source = Actor.findById(terror.`object`) as Char
            if (source != null) {
                return source
            }
        }

        //find a new enemy if..
        var newEnemy = false
        //we have no enemy, or the current one is dead
        if (enemy == null || !enemy!!.isAlive || state === WANDERING)
            newEnemy = true
        else if (alignment == Char.Alignment.ALLY && enemy!!.alignment == Char.Alignment.ALLY)
            newEnemy = true
        else if (buff<Amok>(Amok::class.java) != null && enemy === Dungeon.hero)
            newEnemy = true//We are amoked and current enemy is the hero
        //We are an ally, and current enemy is another ally.

        if (newEnemy) {

            val enemies = HashSet<Char>()

            //if the mob is amoked...
            if (buff<Amok>(Amok::class.java) != null) {
                //try to find an enemy mob to attack first.
                for (mob in Dungeon.level!!.mobs)
                    if (mob.alignment == Char.Alignment.ENEMY && mob !== this && fieldOfView!![mob.pos])
                        enemies.add(mob)

                if (enemies.isEmpty()) {
                    //try to find ally mobs to attack second.
                    for (mob in Dungeon.level!!.mobs)
                        if (mob.alignment == Char.Alignment.ALLY && mob !== this && fieldOfView!![mob.pos])
                            enemies.add(mob)

                    if (enemies.isEmpty()) {
                        //try to find the hero third
                        if (fieldOfView!![Dungeon.hero!!.pos]) {
                            enemies.add(Dungeon.hero)
                        }
                    }
                }

                //if the mob is an ally...
            } else if (alignment == Char.Alignment.ALLY) {
                //look for hostile mobs that are not passive to attack
                for (mob in Dungeon.level!!.mobs)
                    if (mob.alignment == Char.Alignment.ENEMY
                            && fieldOfView!![mob.pos]
                            && mob.state !== mob.PASSIVE)
                        enemies.add(mob)

                //if the mob is an enemy...
            } else if (alignment == Char.Alignment.ENEMY) {
                //look for ally mobs to attack
                for (mob in Dungeon.level!!.mobs)
                    if (mob.alignment == Char.Alignment.ALLY && fieldOfView!![mob.pos])
                        enemies.add(mob)

                //and look for the hero
                if (fieldOfView!![Dungeon.hero!!.pos]) {
                    enemies.add(Dungeon.hero)
                }

            }

            //neutral character in particular do not choose enemies.
            if (enemies.isEmpty()) {
                return null
            } else {
                //go after the closest potential enemy, preferring the hero if two are equidistant
                var closest: Char? = null
                for (curr in enemies) {
                    if (closest == null
                            || Dungeon.level!!.distance(pos, curr.pos) < Dungeon.level!!.distance(pos, closest.pos)
                            || Dungeon.level!!.distance(pos, curr.pos) == Dungeon.level!!.distance(pos, closest.pos) && curr === Dungeon.hero) {
                        closest = curr
                    }
                }
                return closest
            }

        } else
            return enemy
    }

    protected fun moveSprite(from: Int, to: Int): Boolean {

        if (sprite!!.isVisible && (Dungeon.level!!.heroFOV[from] || Dungeon.level!!.heroFOV[to])) {
            sprite!!.move(from, to)
            return true
        } else {
            sprite!!.turnTo(from, to)
            sprite!!.place(to)
            return true
        }
    }

    override fun add(buff: Buff) {
        super.add(buff)
        if (buff is Amok || buff is Corruption) {
            state = HUNTING
        } else if (buff is Terror) {
            state = FLEEING
        } else if (buff is Sleep) {
            state = SLEEPING
            postpone(Sleep.SWS)
        }
    }

    override fun remove(buff: Buff) {
        super.remove(buff)
        if (buff is Terror) {
            sprite!!.showStatus(CharSprite.NEGATIVE, Messages.get(this, "rage"))
            state = HUNTING
        }
    }

    protected open fun canAttack(enemy: Char?): Boolean {
        return Dungeon.level!!.adjacent(pos, enemy!!.pos)
    }

    protected open fun getCloser(target: Int): Boolean {

        if (rooted || target == pos) {
            return false
        }

        var step = -1

        if (Dungeon.level!!.adjacent(pos, target)) {

            path = null

            if (Actor.findChar(target) == null && Dungeon.level!!.passable[target]) {
                step = target
            }

        } else {

            var newPath = false
            //scrap the current path if it's empty, no longer connects to the current location
            //or if it's extremely inefficient and checking again may result in a much better path
            if (path == null || path!!.isEmpty()
                    || !Dungeon.level!!.adjacent(pos, path!!.first)
                    || path!!.size > 2 * Dungeon.level!!.distance(pos, target))
                newPath = true
            else if (path!!.last != target) {
                //if the new target is adjacent to the end of the path, adjust for that
                //rather than scrapping the whole path.
                if (Dungeon.level!!.adjacent(target, path!!.last)) {
                    val last = path!!.removeLast()

                    if (path!!.isEmpty()) {

                        //shorten for a closer one
                        if (Dungeon.level!!.adjacent(target, pos)) {
                            path!!.add(target)
                            //extend the path for a further target
                        } else {
                            path!!.add(last)
                            path!!.add(target)
                        }

                    } else if (!path!!.isEmpty()) {
                        //if the new target is simply 1 earlier in the path shorten the path
                        if (path!!.last == target) {

                            //if the new target is closer/same, need to modify end of path
                        } else if (Dungeon.level!!.adjacent(target, path!!.last)) {
                            path!!.add(target)

                            //if the new target is further away, need to extend the path
                        } else {
                            path!!.add(last)
                            path!!.add(target)
                        }
                    }

                } else {
                    newPath = true
                }

            }


            if (!newPath) {
                //looks ahead for path validity, up to length-1 or 4, but always at least 1.
                val lookAhead = GameMath.gate(1f, (path!!.size - 1).toFloat(), 4f).toInt()
                for (i in 0 until lookAhead) {
                    val cell = path!![i]
                    if (!Dungeon.level!!.passable[cell] || fieldOfView!![cell] && Actor.findChar(cell) != null) {
                        newPath = true
                        break
                    }
                }
            }

            if (newPath) {
                path = Dungeon.findPath(this, pos, target,
                        Dungeon.level!!.passable,
                        fieldOfView)
            }

            //if hunting something, don't follow a path that is extremely inefficient
            //FIXME this is fairly brittle, primarily it assumes that hunting mobs can't see through
            // permanent terrain, such that if their path is inefficient it's always because
            // of a temporary blockage, and therefore waiting for it to clear is the best option.
            if (path == null || state === HUNTING && path!!.size > Math.max(9, 2 * Dungeon.level!!.distance(pos, target))) {
                return false
            }

            step = path!!.removeFirst()
        }
        if (step != -1) {
            move(step)
            return true
        } else {
            return false
        }
    }

    protected open fun getFurther(target: Int): Boolean {
        val step = Dungeon.flee(this, pos, target,
                Dungeon.level!!.passable,
                fieldOfView)
        if (step != -1) {
            move(step)
            return true
        } else {
            return false
        }
    }

    override fun updateSpriteState() {
        super.updateSpriteState()
        if (Dungeon.hero!!.buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java) != null)
            sprite!!.add(CharSprite.State.PARALYSED)
    }

    protected open fun attackDelay(): Float {
        return 1f
    }

    protected open fun doAttack(enemy: Char?): Boolean {

        val visible = Dungeon.level!!.heroFOV[pos]

        if (visible) {
            sprite!!.attack(enemy!!.pos)
        } else {
            attack(enemy)
        }

        spend(attackDelay())

        return !visible
    }

    override fun onAttackComplete() {
        attack(enemy)
        super.onAttackComplete()
    }

    override fun attackProc(enemy: Char, damage: Int): Int {
        var damage = damage
        damage = super.attackProc(enemy, damage)
        if (buff<Weakness>(Weakness::class.java) != null) {
            damage *= 0.67f
        }
        return damage
    }

    override fun defenseSkill(enemy: Char): Int {
        val seen = enemySeen || enemy === Dungeon.hero && !Dungeon.hero!!.canSurpriseAttack()
        if (seen
                && paralysed == 0
                && !(alignment == Char.Alignment.ALLY && enemy === Dungeon.hero)) {
            var defenseSkill = this.defenseSkill
            defenseSkill *= RingOfAccuracy.enemyEvasionMultiplier(enemy).toInt()
            return defenseSkill
        } else {
            return 0
        }
    }

    override fun defenseProc(enemy: Char, damage: Int): Int {
        if (!enemySeen && enemy === Dungeon.hero && Dungeon.hero!!.canSurpriseAttack()) {
            if (enemy.buff<Preparation>(Preparation::class.java) != null) {
                Wound.hit(this)
            } else {
                Surprise.hit(this)
            }
        }

        //if attacked by something else than current target, and that thing is closer, switch targets
        if (this.enemy == null || enemy !== this.enemy && Dungeon.level!!.distance(pos, enemy.pos) < Dungeon.level!!.distance(pos, this.enemy!!.pos)) {
            aggro(enemy)
            target = enemy.pos
        }

        if (buff<SoulMark>(SoulMark::class.java) != null) {
            val restoration = Math.min(damage, HP)
            Dungeon.hero!!.buff<Hunger>(Hunger::class.java)!!.satisfy(restoration * 0.5f)
            Dungeon.hero!!.HP = Math.ceil(Math.min(Dungeon.hero!!.HT.toFloat(), Dungeon.hero!!.HP + restoration * 0.25f).toDouble()).toInt()
            Dungeon.hero!!.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)
        }

        return damage
    }

    fun surprisedBy(enemy: Char): Boolean {
        return !enemySeen && enemy === Dungeon.hero
    }

    open fun aggro(ch: Char) {
        enemy = ch
        if (state !== PASSIVE) {
            state = HUNTING
        }
    }

    override fun damage(dmg: Int, src: Any) {

        Terror.recover(this)

        if (state === SLEEPING) {
            state = WANDERING
        }
        if (state !== HUNTING) {
            alerted = true
        }

        super.damage(dmg, src)
    }


    override fun destroy() {

        super.destroy()

        Dungeon.level!!.mobs.remove(this)

        if (Dungeon.hero!!.isAlive) {

            if (alignment == Char.Alignment.ENEMY) {
                Statistics.enemiesSlain++
                Badges.validateMonstersSlain()
                Statistics.qualifiedForNoKilling = false

                val exp = if (Dungeon.hero!!.lvl <= maxLvl) EXP else 0
                if (exp > 0) {
                    Dungeon.hero!!.sprite!!.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", exp))
                    Dungeon.hero!!.earnExp(exp)
                }
            }
        }
    }

    override fun die(cause: Any) {

        if (cause === Chasm::class.java) {
            //50% chance to round up, 50% to round down
            if (EXP % 2 == 1) EXP += Random.Int(2)
            EXP /= 2
        }

        super.die(cause)

        if (alignment == Char.Alignment.ENEMY) {
            rollToDropLoot()
        }

        if (Dungeon.hero!!.isAlive && !Dungeon.level!!.heroFOV[pos]) {
            GLog.i(Messages.get(this, "died"))
        }
    }

    open fun rollToDropLoot() {
        if (Dungeon.hero!!.lvl > maxLvl + 2) return

        var lootChance = this.lootChance
        lootChance *= RingOfWealth.dropChanceMultiplier(Dungeon.hero)

        if (Random.Float() < lootChance) {
            val loot = createLoot()
            if (loot != null) {
                Dungeon.level!!.drop(loot, pos).sprite!!.drop()
            }
        }

        //ring of wealth logic
        if (Ring.getBonus(Dungeon.hero!!, RingOfWealth.Wealth::class.java) > 0) {
            var rolls = 1
            if (properties.contains(Char.Property.BOSS))
                rolls = 15
            else if (properties.contains(Char.Property.MINIBOSS)) rolls = 5
            val bonus = RingOfWealth.tryRareDrop(Dungeon.hero, rolls)
            if (bonus != null) {
                for (b in bonus) Dungeon.level!!.drop(b, pos).sprite!!.drop()
                Flare(8, 32f).color(0xFFFF00, true).show(sprite, 2f)
            }
        }
    }

    protected open fun createLoot(): Item? {
        val item: Item?
        if (loot is Generator.Category) {

            item = Generator.random(loot as Generator.Category?)

        } else if (loot is Class<*>) {

            item = Generator.random(loot as Class<out Item>?)

        } else {

            item = loot as Item?

        }
        return item
    }

    open fun reset(): Boolean {
        return false
    }

    open fun beckon(cell: Int) {

        notice()

        if (state !== HUNTING) {
            state = WANDERING
        }
        target = cell
    }

    open fun description(): String {
        return Messages.get(this, "desc")
    }

    open fun notice() {
        sprite!!.showAlert()
    }

    fun yell(str: String) {
        GLog.n("%s: \"%s\" ", name, str)
    }

    //returns true when a mob sees the hero, and is currently targeting them.
    fun focusingHero(): Boolean {
        return enemySeen && target == Dungeon.hero!!.pos
    }

    interface AiState {
        fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean
    }

    protected inner class Sleeping : AiState {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            if (enemyInFOV && Random.Int(distance(enemy) + enemy!!.stealth() + if (enemy!!.flying) 2 else 0) == 0) {

                enemySeen = true

                notice()
                state = HUNTING
                target = enemy!!.pos

                if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                    for (mob in Dungeon.level!!.mobs) {
                        if (Dungeon.level!!.distance(pos, mob.pos) <= 8 && mob.state !== mob.HUNTING) {
                            mob.beckon(target)
                        }
                    }
                }

                spend(TIME_TO_WAKE_UP)

            } else {

                enemySeen = false

                spend(Actor.TICK)

            }
            return true
        }

        companion object {

            val TAG = "SLEEPING"
        }
    }

    protected open inner class Wandering : AiState {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            if (enemyInFOV && (justAlerted || Random.Int(distance(enemy) / 2 + enemy!!.stealth()) == 0)) {

                enemySeen = true

                notice()
                alerted = true
                state = HUNTING
                target = enemy!!.pos

                if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                    for (mob in Dungeon.level!!.mobs) {
                        if (Dungeon.level!!.distance(pos, mob.pos) <= 8 && mob.state !== mob.HUNTING) {
                            mob.beckon(target)
                        }
                    }
                }

            } else {

                enemySeen = false

                val oldPos = pos
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed())
                    return moveSprite(oldPos, pos)
                } else {
                    target = Dungeon.level!!.randomDestination()
                    spend(Actor.TICK)
                }

            }
            return true
        }

        companion object {

            val TAG = "WANDERING"
        }
    }

    protected open inner class Hunting : AiState {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV
            if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

                return doAttack(enemy)

            } else {

                if (enemyInFOV) {
                    target = enemy!!.pos
                } else if (enemy == null) {
                    state = WANDERING
                    target = Dungeon.level!!.randomDestination()
                    return true
                }

                val oldPos = pos
                if (target != -1 && getCloser(target)) {

                    spend(1 / speed())
                    return moveSprite(oldPos, pos)

                } else {
                    spend(Actor.TICK)
                    if (!enemyInFOV) {
                        sprite!!.showLost()
                        state = WANDERING
                        target = Dungeon.level!!.randomDestination()
                    }
                    return true
                }
            }
        }

        companion object {

            val TAG = "HUNTING"
        }
    }

    protected open inner class Fleeing : AiState {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV
            //loses target when 0-dist rolls a 6 or greater.
            if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level!!.distance(pos, target)) >= 6) {
                target = -1

                //if enemy isn't in FOV, keep running from their previous position.
            } else if (enemyInFOV) {
                target = enemy!!.pos
            }

            val oldPos = pos
            if (target != -1 && getFurther(target)) {

                spend(1 / speed())
                return moveSprite(oldPos, pos)

            } else {

                spend(Actor.TICK)
                nowhereToRun()

                return true
            }
        }

        protected open fun nowhereToRun() {}

        companion object {

            val TAG = "FLEEING"
        }
    }

    protected inner class Passive : AiState {

        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = false
            spend(Actor.TICK)
            return true
        }

        companion object {

            val TAG = "PASSIVE"
        }
    }

    companion object {

        private val TXT_DIED = "You hear something died in the distance"

        protected val TXT_NOTICE1 = "?!"
        protected val TXT_RAGE = "#$%^"
        protected val TXT_EXP = "%+dEXP"

        protected val TIME_TO_WAKE_UP = 1f

        private val STATE = "state"
        private val SEEN = "seen"
        private val TARGET = "target"
    }
}

