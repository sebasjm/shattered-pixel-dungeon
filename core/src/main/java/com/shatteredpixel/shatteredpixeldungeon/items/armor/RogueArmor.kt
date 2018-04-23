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

package com.shatteredpixel.shatteredpixeldungeon.items.armor

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class RogueArmor : ClassArmor() {

    init {
        image = ItemSpriteSheet.ARMOR_ROGUE
    }

    override fun doSpecial() {
        GameScene.selectCell(teleporter)
    }

    companion object {

        protected var teleporter: CellSelector.Listener = object : CellSelector.Listener {

            override fun onSelect(target: Int?) {
                if (target != null) {

                    if (!Dungeon.level!!.heroFOV[target] ||
                            !(Dungeon.level!!.passable[target] || Dungeon.level!!.avoid[target]) ||
                            Actor.findChar(target) != null) {

                        GLog.w(Messages.get(RogueArmor::class.java, "fov"))
                        return
                    }

                    Item.curUser!!.HP -= Item.curUser!!.HP / 3

                    for (mob in Dungeon.level!!.mobs.toTypedArray<Mob>()) {
                        if (Dungeon.level!!.heroFOV[mob.pos]) {
                            Buff.prolong<Blindness>(mob, Blindness::class.java, 2f)
                            if (mob.state === mob.HUNTING) mob.state = mob.WANDERING
                            mob.sprite!!.emitter().burst(Speck.factory(Speck.LIGHT), 4)
                        }
                    }

                    ScrollOfTeleportation.appear(Item.curUser!!, target)
                    CellEmitter.get(target).burst(Speck.factory(Speck.WOOL), 10)
                    Sample.INSTANCE.play(Assets.SND_PUFF)
                    Dungeon.level!!.press(target, Item.curUser!!)
                    Dungeon.observe()
                    GameScene.updateFog()

                    Item.curUser!!.spendAndNext(Actor.TICK)
                }
            }

            override fun prompt(): String {
                return Messages.get(RogueArmor::class.java, "prompt")
            }
        }
    }
}