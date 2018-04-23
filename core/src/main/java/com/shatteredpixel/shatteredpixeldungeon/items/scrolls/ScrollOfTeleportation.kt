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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.utils.PathFinder

class ScrollOfTeleportation : Scroll() {

    init {
        initials = 9
    }

    override fun doRead() {

        Sample.INSTANCE.play(Assets.SND_READ)
        Invisibility.dispel()

        teleportHero(Item.curUser!!)
        setKnown()

        readAnimation()
    }

    override fun empoweredRead() {

        if (Dungeon.bossLevel()) {
            GLog.w(Messages.get(this.javaClass, "no_tele"))
            return
        }

        GameScene.selectCell(object : CellSelector.Listener {
            override fun onSelect(target: Int?) {
                if (target != null) {
                    //time isn't spent
                    (Item.curUser!!.sprite as HeroSprite).read()
                    teleportToLocation(Item.curUser!!, target)
                }
            }

            override fun prompt(): String {
                return Messages.get(ScrollOfTeleportation::class.java, "prompt")
            }
        })
    }

    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }

    companion object {

        fun teleportToLocation(hero: Hero, pos: Int) {
            PathFinder.buildDistanceMap(pos, BArray.or(Dungeon.level!!.passable, Dungeon.level!!.avoid, null))
            if (PathFinder.distance!![hero.pos] == Integer.MAX_VALUE
                    || !Dungeon.level!!.passable[pos] && !Dungeon.level!!.avoid[pos]
                    || Actor.findChar(pos) != null) {
                GLog.w(Messages.get(ScrollOfTeleportation::class.java, "cant_reach"))
                return
            }

            appear(hero, pos)
            Dungeon.level!!.press(pos, hero)
            Dungeon.observe()
            GameScene.updateFog()

            GLog.i(Messages.get(ScrollOfTeleportation::class.java, "tele"))
        }

        fun teleportHero(hero: Hero?) {

            var count = 10
            var pos: Int
            do {
                pos = Dungeon.level!!.randomRespawnCell()
                if (count-- <= 0) {
                    break
                }
            } while (pos == -1)

            if (pos == -1 || Dungeon.bossLevel()) {

                GLog.w(Messages.get(ScrollOfTeleportation::class.java, "no_tele"))

            } else {

                appear(hero!!, pos)
                Dungeon.level!!.press(pos, hero)
                Dungeon.observe()
                GameScene.updateFog()

                GLog.i(Messages.get(ScrollOfTeleportation::class.java, "tele"))

            }
        }

        fun appear(ch: Char, pos: Int) {

            ch.sprite!!.interruptMotion()

            ch.move(pos)
            ch.sprite!!.place(pos)

            if (ch.invisible == 0) {
                ch.sprite!!.alpha(0f)
                ch.sprite!!.parent!!.add(AlphaTweener(ch.sprite!!, 1f, 0.4f))
            }

            ch.sprite!!.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3)
            Sample.INSTANCE.play(Assets.SND_TELEPORT)
        }
    }
}
