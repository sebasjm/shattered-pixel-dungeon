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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.watabou.noosa.Game
import com.watabou.noosa.Image

import java.text.DecimalFormat
import java.util.HashSet

open class Buff : Actor() {

    var target: Char? = null
    var type = buffType.SILENT

    protected var resistances = HashSet<Class<*>>()

    protected var immunities = HashSet<Class<*>>()

    init {
        actPriority = BUFF_PRIO //low priority, towards the end of a turn
    }

    //determines how the buff is announced when it is shown.
    //buffs that work behind the scenes, or have other visual indicators can usually be silent.
    enum class buffType {
        POSITIVE, NEGATIVE, NEUTRAL, SILENT
    }

    fun resistances(): HashSet<Class<*>> {
        return HashSet(resistances)
    }

    fun immunities(): HashSet<Class<*>> {
        return HashSet(immunities)
    }

    open fun attachTo(target: Char): Boolean {

        if (target.isImmune(javaClass)) {
            return false
        }

        this.target = target
        target.add(this)

        if (target.buffs().contains(this)) {
            if (target.sprite != null) fx(true)
            return true
        } else
            return false
    }

    open fun detach() {
        if (target!!.sprite != null) fx(false)
        target!!.remove(this)
    }

    public override fun act(): Boolean {
        diactivate()
        return true
    }

    open fun icon(): Int {
        return BuffIndicator.NONE
    }

    open fun tintIcon(icon: Image) {
        //do nothing by default
    }

    open fun fx(on: Boolean) {
        //do nothing by default
    }

    open fun heroMessage(): String? {
        return null
    }

    open fun desc(): String {
        return ""
    }

    //to handle the common case of showing how many turns are remaining in a buff description.
    protected fun dispTurns(input: Float): String {
        return DecimalFormat("#.##").format(input.toDouble())
    }

    companion object {

        //creates a fresh instance of the buff and attaches that, this allows duplication.
        fun <T : Buff> append(target: Char, buffClass: Class<T>): T? {
            try {
                val buff = buffClass.newInstance()
                buff.attachTo(target)
                return buff
            } catch (e: Exception) {
                Game.reportException(e)
                return null
            }

        }

        fun <T : FlavourBuff> append(target: Char, buffClass: Class<T>, duration: Float): T {
            val buff = append(target, buffClass)
            buff!!.spend(duration * target.resist(buffClass))
            return buff
        }

        //same as append, but prevents duplication.
        fun <T : Buff> affect(target: Char, buffClass: Class<T>): T? {
            val buff = target.buff(buffClass)
            return buff ?: append(target, buffClass)
        }

        fun <T : FlavourBuff> affect(target: Char, buffClass: Class<T>, duration: Float): T {
            val buff = affect(target, buffClass)
            buff!!.spend(duration * target.resist(buffClass))
            return buff
        }

        //postpones an already active buff, or creates & attaches a new buff and delays that.
        fun <T : FlavourBuff> prolong(target: Char, buffClass: Class<T>, duration: Float): T {
            val buff = affect(target, buffClass)
            buff!!.postpone(duration * target.resist(buffClass))
            return buff
        }

        fun detach(buff: Buff?) {
            buff?.detach()
        }

        fun detach(target: Char, cl: Class<out Buff>) {
            detach(target.buff(cl as Class<Buff>))
        }
    }
}
