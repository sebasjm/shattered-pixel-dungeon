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

package com.shatteredpixel.shatteredpixeldungeon.items.quest

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.Callback
import com.watabou.utils.PathFinder

import java.util.ArrayList

class Pickaxe : Weapon() {

    var bloodStained = false

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    init {
        image = ItemSpriteSheet.PICKAXE

        unique = true
        bones = false

        defaultAction = AC_MINE

    }

    override fun min(lvl: Int): Int {
        return 2   //tier 2
    }

    override fun max(lvl: Int): Int {
        return 15  //tier 2
    }

    override fun STRReq(lvl: Int): Int {
        return 14  //tier 3
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_MINE)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_MINE) {

            if (Dungeon.depth < 11 || Dungeon.depth > 15) {
                GLog.w(Messages.get(this.javaClass, "no_vein"))
                return
            }

            for (i in PathFinder.NEIGHBOURS8!!.indices) {

                val pos = hero.pos + PathFinder.NEIGHBOURS8!![i]
                if (Dungeon.level!!.map!![pos] == Terrain.WALL_DECO) {

                    hero.spend(TIME_TO_MINE)
                    hero.busy()

                    hero.sprite!!.attack(pos, {
                        CellEmitter.center(pos).burst(Speck.factory(Speck.STAR), 7)
                        Sample.INSTANCE.play(Assets.SND_EVOKE)

                        Level.set(pos, Terrain.WALL)
                        GameScene.updateMap(pos)

                        val gold = DarkGold()
                        if (gold.doPickUp(Dungeon.hero!!)) {
                            GLog.i(Messages.get(Dungeon.hero!!.javaClass, "you_now_have", gold.name()))
                        } else {
                            Dungeon.level!!.drop(gold, hero.pos).sprite!!.drop()
                        }

                        hero.onOperateComplete()
                    }as Callback)

                    return
                }
            }

            GLog.w(Messages.get(this.javaClass, "no_vein"))

        }
    }

    override fun proc(attacker: Char, defender: Char, damage: Int): Int {
        if (!bloodStained && defender is Bat && defender.HP <= damage) {
            bloodStained = true
            updateQuickslot()
        }
        return damage
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)

        bundle.put(BLOODSTAINED, bloodStained)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)

        bloodStained = bundle.getBoolean(BLOODSTAINED)
    }

    override fun glowing(): Glowing? {
        return if (bloodStained) BLOODY else null
    }

    companion object {

        val AC_MINE = "MINE"

        val TIME_TO_MINE = 2f

        private val BLOODY = Glowing(0x550000)

        private val BLOODSTAINED = "bloodStained"
    }

}
