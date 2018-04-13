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

package com.shatteredpixel.shatteredpixeldungeon.levels

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker
import com.shatteredpixel.shatteredpixeldungeon.effects.Halo
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.PrisonPainter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Group
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.PointF
import com.watabou.utils.Random

import java.util.ArrayList

class PrisonLevel : RegularLevel() {

    init {
        color1 = 0x6a723d
        color2 = 0x88924c
    }

    override fun initRooms(): ArrayList<Room> {
        return Wandmaker.Quest.spawnRoom(super.initRooms())
    }

    override fun standardRooms(): Int {
        //6 to 8, average 6.66
        return 6 + Random.chances(floatArrayOf(4f, 2f, 2f))
    }

    override fun specialRooms(): Int {
        //1 to 3, average 1.83
        return 1 + Random.chances(floatArrayOf(3f, 4f, 3f))
    }

    override fun painter(): Painter {
        return PrisonPainter()
                .setWater(if (feeling == Level.Feeling.WATER) 0.90f else 0.30f, 4)
                .setGrass(if (feeling == Level.Feeling.GRASS) 0.80f else 0.20f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances())
    }

    override fun tilesTex(): String? {
        return Assets.TILES_PRISON
    }

    override fun waterTex(): String? {
        return Assets.WATER_PRISON
    }

    override fun trapClasses(): Array<Class<*>> {
        return arrayOf(ChillingTrap::class.java, ShockingTrap::class.java, ToxicTrap::class.java, BurningTrap::class.java, PoisonDartTrap::class.java, AlarmTrap::class.java, OozeTrap::class.java, GrippingTrap::class.java, ConfusionTrap::class.java, FlockTrap::class.java, SummoningTrap::class.java, TeleportationTrap::class.java)
    }

    override fun trapChances(): FloatArray {
        return floatArrayOf(8f, 8f, 8f, 8f, 8f, 4f, 4f, 4f, 2f, 2f, 2f, 2f)
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(PrisonLevel::class.java, "water_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.EMPTY_DECO -> return Messages.get(PrisonLevel::class.java, "empty_deco_desc")
            Terrain.BOOKSHELF -> return Messages.get(PrisonLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        addPrisonVisuals(this, visuals)
        return visuals
    }

    class Torch(private val pos: Int) : Emitter() {

        init {

            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 1, p.y + 2, 2f, 0f)

            pour(FlameParticle.FACTORY, 0.15f)

            add(Halo(12f, 0xFFFFCC, 0.4f).point(p.x, p.y + 1))
        }

        override fun update() {
            if (visible = pos < Dungeon.level!!.heroFOV.size && Dungeon.level!!.heroFOV[pos]) {
                super.update()
            }
        }
    }

    companion object {

        fun addPrisonVisuals(level: Level, group: Group?) {
            for (i in 0 until level.length()) {
                if (level.map!![i] == Terrain.WALL_DECO) {
                    group!!.add(Torch(i))
                }
            }
        }
    }
}