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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

import java.util.ArrayList

class TalismanOfForesight : Artifact() {

    init {
        image = ItemSpriteSheet.ARTIFACT_TALISMAN

        exp = 0
        levelCap = 10

        charge = 0
        partialCharge = 0f
        chargeCap = 100

        defaultAction = AC_SCRY
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && charge == chargeCap && !cursed)
            actions.add(AC_SCRY)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {
        super.execute(hero, action)

        if (action == AC_SCRY) {

            if (!isEquipped(hero))
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
            else if (charge != chargeCap)
                GLog.i(Messages.get(this.javaClass, "no_charge"))
            else {
                hero.sprite!!.operate(hero.pos)
                hero.busy()
                Sample.INSTANCE.play(Assets.SND_BEACON)
                charge = 0
                for (i in 0 until Dungeon.level!!.length()) {

                    val terr = Dungeon.level!!.map!![i]
                    if (Terrain.flags[terr] and Terrain.SECRET != 0) {

                        GameScene.updateMap(i)

                        if (Dungeon.level!!.heroFOV[i]) {
                            GameScene.discoverTile(i, terr)
                        }
                    }
                }

                GLog.p(Messages.get(this.javaClass, "scry"))

                updateQuickslot()

                Buff.affect<Awareness>(hero, Awareness::class.java, Awareness.DURATION)
                Dungeon.observe()
            }
        }
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return Foresight()
    }

    override fun desc(): String {
        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!)) {
            if (!cursed) {
                desc += "\n\n" + Messages.get(this.javaClass, "desc_worn")

            } else {
                desc += "\n\n" + Messages.get(this.javaClass, "desc_cursed")
            }
        }

        return desc
    }

    inner class Foresight : Artifact.ArtifactBuff() {
        private var warn = 0

        override fun act(): Boolean {
            spend(Actor.TICK)

            var smthFound = false

            val distance = 3

            val cx = target!!.pos % Dungeon.level!!.width()
            val cy = target!!.pos / Dungeon.level!!.width()
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

            for (y in ay..by) {
                var x = ax
                var p = ax + y * Dungeon.level!!.width()
                while (x <= bx) {

                    if (Dungeon.level!!.heroFOV[p]
                            && Dungeon.level!!.secret[p]
                            && Dungeon.level!!.map!![p] != Terrain.SECRET_DOOR)
                        smthFound = true
                    x++
                    p++
                }
            }

            if (smthFound && !cursed) {
                if (warn == 0) {
                    GLog.w(Messages.get(this.javaClass, "uneasy"))
                    if (target!! is Hero) {
                        (target!! as Hero).interrupt()
                    }
                }
                warn = 3
            } else {
                if (warn > 0) {
                    warn--
                }
            }
            BuffIndicator.refreshHero()

            //fully charges in 2000 turns at lvl=0, scaling to 667 turns at lvl = 10.
            val lock = target!!.buff<LockedFloor>(LockedFloor::class.java)
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                partialCharge += (0.05 + level() * 0.01).toFloat()

                if (partialCharge > 1 && charge < chargeCap) {
                    partialCharge--
                    charge++
                } else if (charge >= chargeCap) {
                    partialCharge = 0f
                    GLog.p(Messages.get(this.javaClass, "full_charge"))
                }
            }

            return true
        }

        fun charge() {
            charge = Math.min(charge + (2 + level() / 3), chargeCap)
            exp++
            if (exp >= 4 && level() < levelCap) {
                upgrade()
                GLog.p(Messages.get(this.javaClass, "levelup"))
                exp -= 4
            }
        }

        override fun toString(): String {
            return Messages.get(this.javaClass, "name")
        }

        override fun desc(): String {
            return Messages.get(this.javaClass, "desc")
        }

        override fun icon(): Int {
            return if (warn == 0)
                BuffIndicator.NONE
            else
                BuffIndicator.FORESIGHT
        }
    }

    companion object {

        val AC_SCRY = "SCRY"
    }
}
