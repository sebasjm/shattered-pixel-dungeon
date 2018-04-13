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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class GuardianTrap : Trap() {

    init {
        color = Trap.RED
        shape = Trap.STARS
    }

    override fun activate() {

        for (mob in Dungeon.level!!.mobs) {
            mob.beckon(pos)
        }

        if (Dungeon.level!!.heroFOV[pos]) {
            GLog.w(Messages.get(this, "alarm"))
            CellEmitter.center(pos).start(Speck.factory(Speck.SCREAM), 0.3f, 3)
        }

        Sample.INSTANCE.play(Assets.SND_ALERT)

        for (i in 0 until (Dungeon.depth - 5) / 5) {
            val guardian = Guardian()
            guardian.state = guardian.WANDERING
            guardian.pos = Dungeon.level!!.randomRespawnCell()
            GameScene.add(guardian)
            guardian.beckon(Dungeon.hero!!.pos)
        }

    }

    class Guardian : Statue() {

        init {
            spriteClass = GuardianSprite::class.java

            EXP = 0
            state = WANDERING
        }

        init {

            weapon.enchant(null)
            weapon.degrade(weapon.level())
        }

        override fun beckon(cell: Int) {
            //Beckon works on these ones, unlike their superclass.
            notice()

            if (state !== HUNTING) {
                state = WANDERING
            }
            target = cell
        }

    }

    class GuardianSprite : StatueSprite() {
        init {
            tint(0f, 0f, 1f, 0.2f)
        }

        override fun resetColor() {
            super.resetColor()
            tint(0f, 0f, 1f, 0.2f)
        }
    }
}
