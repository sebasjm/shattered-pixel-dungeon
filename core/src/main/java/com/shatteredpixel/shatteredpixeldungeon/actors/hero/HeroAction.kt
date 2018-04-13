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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero

import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC

open class HeroAction {

    var dst: Int = 0

    class Move(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }

    class PickUp(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }

    class OpenChest(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }

    class Buy(dst: Int) : HeroAction() {
        init {
            this.dst = dst
        }
    }

    class Interact(var npc: NPC) : HeroAction()

    class Unlock(door: Int) : HeroAction() {
        init {
            this.dst = door
        }
    }

    class Descend(stairs: Int) : HeroAction() {
        init {
            this.dst = stairs
        }
    }

    class Ascend(stairs: Int) : HeroAction() {
        init {
            this.dst = stairs
        }
    }

    class Alchemy(pot: Int) : HeroAction() {
        init {
            this.dst = pot
        }
    }

    class Attack(var target: Char) : HeroAction()
}
