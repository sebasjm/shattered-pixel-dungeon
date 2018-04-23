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

package com.shatteredpixel.shatteredpixeldungeon.levels.features

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Random

object Chasm {

    var jumpConfirmed = false

    fun heroJump(hero: Hero) {
        GameScene.show(
                object : WndOptions(Messages.get(Chasm::class.java, "chasm"),
                        Messages.get(Chasm::class.java, "jump"),
                        Messages.get(Chasm::class.java, "yes"),
                        Messages.get(Chasm::class.java, "no")) {
                    override fun onSelect(index: Int) {
                        if (index == 0) {
                            jumpConfirmed = true
                            hero.resume()
                        }
                    }
                }
        )
    }

    fun heroFall(pos: Int) {

        jumpConfirmed = false

        Sample.INSTANCE.play(Assets.SND_FALLING)

        val buff = Dungeon.hero!!.buff<TimekeepersHourglass.timeFreeze>(TimekeepersHourglass.timeFreeze::class.java)
        buff?.detach()

        if (Dungeon.hero!!.isAlive) {
            Dungeon.hero!!.interrupt()
            Buff.affect<Falling>(Dungeon.hero!!, Falling::class.java)
            InterlevelScene.mode = InterlevelScene.Mode.FALL
            if (Dungeon.level is RegularLevel) {
                val room = (Dungeon.level as RegularLevel).room(pos)
                InterlevelScene.fallIntoPit = room != null && room is WeakFloorRoom
            } else {
                InterlevelScene.fallIntoPit = false
            }
            Game.switchScene(InterlevelScene::class.java)
        } else {
            Dungeon.hero!!.sprite!!.visible = false
        }
    }

    fun heroLand() {

        val hero = Dungeon.hero!!

        hero!!.sprite!!.burst(hero.sprite!!.blood(), 10)
        Camera.main!!.shake(4f, 0.2f)

        Dungeon.level!!.press(hero.pos, hero, true)
        Buff.prolong<Cripple>(hero, Cripple::class.java, Cripple.DURATION)

        //The lower the hero's HP, the more bleed and the less upfront damage.
        //Hero has a 50% chance to bleed out at 66% HP, and begins to risk instant-death at 25%
        Buff.affect<Bleeding>(hero, Bleeding::class.java)!!.set(Math.round(hero.HT / (6f + 6f * (hero.HP / hero.HT.toFloat()))))
        hero.damage(Math.max(hero.HP / 2, Random.NormalIntRange(hero.HP / 2, hero.HT / 4)), object: Hero.Doom {
            override fun onDeath() {
                Badges.validateDeathFromFalling()

                Dungeon.fail(Chasm::class.java)
                GLog.n(Messages.get(Chasm::class.java, "ondeath"))
            }
        } )
    }

    fun mobFall(mob: Mob) {
        mob.die(Chasm::class.java)

        (mob.sprite as MobSprite).fall()
    }

    class Falling : Buff() {

        init {
            actPriority = VFX_PRIO
        }

        override fun act(): Boolean {
            heroLand()
            detach()
            return true
        }
    }
}
