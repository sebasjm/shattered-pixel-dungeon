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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.utils.Random

class CapeOfThorns : Artifact() {

    init {
        image = ItemSpriteSheet.ARTIFACT_CAPE

        levelCap = 10

        charge = 0
        chargeCap = 100
        cooldown = 0

        defaultAction = "NONE" //so it can be quickslotted
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return Thorns()
    }

    override fun desc(): String {
        var desc = Messages.get(this, "desc")
        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n"
            if (cooldown == 0)
                desc += Messages.get(this, "desc_inactive")
            else
                desc += Messages.get(this, "desc_active")
        }

        return desc
    }

    inner class Thorns : Artifact.ArtifactBuff() {

        override fun act(): Boolean {
            if (cooldown > 0) {
                cooldown--
                if (cooldown == 0) {
                    BuffIndicator.refreshHero()
                    GLog.w(Messages.get(this, "inert"))
                }
                updateQuickslot()
            }
            spend(Actor.TICK)
            return true
        }

        fun proc(damage: Int, attacker: Char?, defender: Char): Int {
            var damage = damage
            if (cooldown == 0) {
                charge += (damage * (0.5 + level() * 0.05)).toInt()
                if (charge >= chargeCap) {
                    charge = 0
                    cooldown = 10 + level()
                    GLog.p(Messages.get(this, "radiating"))
                    BuffIndicator.refreshHero()
                }
            }

            if (cooldown != 0) {
                val deflected = Random.NormalIntRange(0, damage)
                damage -= deflected

                if (attacker != null && Dungeon.level!!.adjacent(attacker.pos, defender.pos)) {
                    attacker.damage(deflected, this)
                }

                exp += deflected

                if (exp >= (level() + 1) * 5 && level() < levelCap) {
                    exp -= (level() + 1) * 5
                    upgrade()
                    GLog.p(Messages.get(this, "levelup"))
                }

            }
            updateQuickslot()
            return damage
        }

        override fun toString(): String {
            return Messages.get(this, "name")
        }

        override fun desc(): String {
            return Messages.get(this, "desc", dispTurns(cooldown.toFloat()))
        }

        override fun icon(): Int {
            return if (cooldown == 0)
                BuffIndicator.NONE
            else
                BuffIndicator.THORNS
        }

        override fun detach() {
            cooldown = 0
            charge = 0
            super.detach()
        }

    }


}
