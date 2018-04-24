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

import android.util.SparseArray

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

import java.util.HashSet

abstract class Actor : Bundlable {

    private var time: Float = 0.toFloat()

    private var id = 0

    //default priority values for general actor categories
    //note that some specific actors pick more specific values
    //e.g. a buff acting after all normal buffs might have priority BUFF_PRIO + 1
    protected val VFX_PRIO = 100      //visual effects take priority
    protected val HERO_PRIO = 0        //positive priority is before hero, negative after
    protected val BLOB_PRIO = -10      //blobs act after hero, before mobs
    protected val MOB_PRIO = -20      //mobs act between buffs and blobd
    protected val BUFF_PRIO = -30      //buffs act last in a turn
    private val DEFAULT = -100     //if no priority is given, act after all else

    //used to determine what order actors act in if their time is equal. Higher values act earlier.
    protected var actPriority = DEFAULT

    protected abstract fun act(): Boolean

    open fun spend(time: Float) {
        this.time += time
    }

    fun postpone(time: Float) {
        if (this.time < now + time) {
            this.time = now + time
        }
    }

    fun cooldown(): Float {
        return time - now
    }

    protected fun diactivate() {
        time = java.lang.Float.MAX_VALUE
    }

    protected open fun onAdd() {}

    protected open fun onRemove() {}

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(TIME, time)
        bundle.put(ID, id)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        time = bundle.getFloat(TIME)
        id = bundle.getInt(ID)
    }

    fun id(): Int {
        return if (id > 0) {
            id
        } else {
            id = nextID++
            id
        }
    }

    /*protected*/open operator fun next() {
        if (current === this) {
            current = null
        }
    }

    companion object {

        val TICK = 1f

        private val TIME = "time"
        private val ID = "id"

        private var nextID = 1

        // **********************
        // *** Static members ***

        private val all = HashSet<Actor>()
        private val chars = HashSet<Char>()
        @Volatile
        private var current: Actor? = null

        private val ids = SparseArray<Actor>()

        private var now = 0f

        @Synchronized
        fun clear() {

            now = 0f

            all.clear()
            chars.clear()

            ids.clear()
        }

        @Synchronized
        fun fixTime() {

            if (Dungeon.hero != null && all.contains(Dungeon.hero!!)) {
                Statistics.duration += now
            }

            var min = java.lang.Float.MAX_VALUE
            for (a in all) {
                if (a.time < min) {
                    min = a.time
                }
            }
            for (a in all) {
                a.time -= min
            }
            now = 0f
        }

        fun init() {

            add(Dungeon.hero!!)

            for (mob in Dungeon.level!!.mobs) {
                add(mob)
            }

            for (blob in Dungeon.level!!.blobs.values) {
                add(blob)
            }

            current = null
        }

        private val NEXTID = "nextid"

        fun storeNextID(bundle: Bundle) {
            bundle.put(NEXTID, nextID)
        }

        fun restoreNextID(bundle: Bundle) {
            nextID = bundle.getInt(NEXTID)
        }

        fun resetNextID() {
            nextID = 1
        }

        fun processing(): Boolean {
            return current != null
        }

        fun process() {

            var doNext: Boolean
            var interrupted = false

            do {

                current = null
                if (!interrupted) {
                    now = java.lang.Float.MAX_VALUE

                    for (actor in all) {

                        //some actors will always go before others if time is equal.
                        if (actor.time < now || actor.time == now && (current == null || actor.actPriority > current!!.actPriority)) {
                            now = actor.time
                            current = actor
                        }

                    }
                }

                if (current != null) {

                    val acting = current

                    if (acting is Char && acting.sprite != null) {
                        // If it's character's turn to act, but its sprite
                        // is moving, wait till the movement is over
                        try {
                            synchronized(acting.sprite!!) {
                                if (acting.sprite!!.isMoving) {
                                    (acting.sprite!! as java.lang.Object).wait()
                                }
                            }
                        } catch (e: InterruptedException) {
                            interrupted = true
                        }

                    }

                    interrupted = interrupted || Thread.interrupted()

                    if (interrupted) {
                        doNext = false
                        current = null
                    } else {
                        doNext = acting!!.act()
                        if (doNext && (Dungeon.hero == null || !Dungeon.hero!!.isAlive)) {
                            doNext = false
                            current = null
                        }
                    }
                } else {
                    doNext = false
                }

                if (!doNext) {
                    synchronized(Thread.currentThread()) {

                        interrupted = interrupted || Thread.interrupted()

                        if (interrupted) {
                            current = null
                            interrupted = false
                        }

                        synchronized(GameScene::class.java) {
                            //signals to the gamescene that actor processing is finished for now
                            (GameScene::class.java as java.lang.Object).notify()
                        }

                        try {
                            (Thread.currentThread() as java.lang.Object).wait()
                        } catch (e: InterruptedException) {
                            interrupted = true
                        }

                    }
                }

            } while (true)
        }

        fun add(actor: Actor?) {
            add(actor, now)
        }

        fun addDelayed(actor: Actor, delay: Float) {
            add(actor, now + delay)
        }

        @Synchronized
        private fun add(actor: Actor?, time: Float) {

            if (all.contains(actor)) {
                return
            }

            ids.put(actor!!.id(), actor)

            all.add(actor)
            actor.time += time
            actor.onAdd()

            if (actor is Char) {
                chars.add(actor)
                for (buff in actor.buffs()) {
                    all.add(buff)
                    buff.onAdd()
                }
            }
        }

        @Synchronized
        fun remove(actor: Actor?) {

            if (actor != null) {
                all.remove(actor)
                chars.remove(actor)
                actor.onRemove()

                if (actor.id > 0) {
                    ids.remove(actor.id)
                }
            }
        }

        @Synchronized
        fun findChar(pos: Int): Char? {
            for (ch in chars) {
                if (ch.pos == pos)
                    return ch
            }
            return null
        }

        @Synchronized
        fun findById(id: Int): Actor {
            return ids.get(id)
        }

        @Synchronized
        fun all(): HashSet<Actor> {
            return HashSet(all)
        }

        @Synchronized
        fun chars(): HashSet<Char> {
            return HashSet(chars)
        }
    }
}
