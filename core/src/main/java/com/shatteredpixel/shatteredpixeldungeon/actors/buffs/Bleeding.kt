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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.PointF
import com.watabou.utils.Random

class Bleeding : Buff() {

    protected var level: Int = 0

    init {
        type = Buff.buffType.NEGATIVE
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)

    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getInt(LEVEL)
    }

    fun set(level: Int) {
        this.level = Math.max(this.level, level)
    }

    override fun icon(): Int {
        return BuffIndicator.BLEEDING
    }

    override fun toString(): String {
        return Messages.get(this, "name")
    }

    override fun act(): Boolean {
        if (target.isAlive) {

            if ((level = Random.NormalIntRange(level / 2, level)) > 0) {

                target.damage(level, this)
                if (target.sprite!!.visible) {
                    Splash.at(target.sprite!!.center(), -PointF.PI / 2, PointF.PI / 6,
                            target.sprite!!.blood(), Math.min(10 * level / target.HT, 10))
                }

                if (target === Dungeon.hero && !target.isAlive) {
                    Dungeon.fail(javaClass)
                    GLog.n(Messages.get(this, "ondeath"))
                }

                spend(Actor.TICK)
            } else {
                detach()
            }

        } else {

            detach()

        }

        return true
    }

    override fun heroMessage(): String? {
        return Messages.get(this, "heromsg")
    }

    override fun desc(): String {
        return Messages.get(this, "desc", level)
    }

    companion object {

        private val LEVEL = "level"
    }
}
