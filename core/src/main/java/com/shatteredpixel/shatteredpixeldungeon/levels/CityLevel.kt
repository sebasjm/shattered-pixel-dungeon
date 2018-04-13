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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Group
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random

class CityLevel : RegularLevel() {

    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }

    override fun standardRooms(): Int {
        //7 to 10, average 7.9
        return 7 + Random.chances(floatArrayOf(4f, 3f, 2f, 1f))
    }

    override fun specialRooms(): Int {
        //2 to 3, average 2.33
        return 2 + Random.chances(floatArrayOf(2f, 1f))
    }

    override fun tilesTex(): String? {
        return Assets.TILES_CITY
    }

    override fun waterTex(): String? {
        return Assets.WATER_CITY
    }

    override fun painter(): Painter {
        return CityPainter()
                .setWater(if (feeling == Level.Feeling.WATER) 0.90f else 0.30f, 4)
                .setGrass(if (feeling == Level.Feeling.GRASS) 0.80f else 0.20f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances())
    }

    override fun trapClasses(): Array<Class<*>> {
        return arrayOf(FrostTrap::class.java, StormTrap::class.java, CorrosionTrap::class.java, BlazingTrap::class.java, DisintegrationTrap::class.java, ExplosiveTrap::class.java, RockfallTrap::class.java, FlashingTrap::class.java, GuardianTrap::class.java, WeakeningTrap::class.java, SummoningTrap::class.java, WarpingTrap::class.java, CursingTrap::class.java, PitfallTrap::class.java, DisarmingTrap::class.java)
    }

    override fun trapChances(): FloatArray {
        return floatArrayOf(8f, 8f, 8f, 8f, 8f, 4f, 4f, 4f, 4f, 4f, 2f, 2f, 2f, 1f, 1f)
    }

    override fun createItems() {
        super.createItems()

        Imp.Quest.spawn(this)
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(CityLevel::class.java, "water_name")
            Terrain.HIGH_GRASS -> return Messages.get(CityLevel::class.java, "high_grass_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.ENTRANCE -> return Messages.get(CityLevel::class.java, "entrance_desc")
            Terrain.EXIT -> return Messages.get(CityLevel::class.java, "exit_desc")
            Terrain.WALL_DECO, Terrain.EMPTY_DECO -> return Messages.get(CityLevel::class.java, "deco_desc")
            Terrain.EMPTY_SP -> return Messages.get(CityLevel::class.java, "sp_desc")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(CityLevel::class.java, "statue_desc")
            Terrain.BOOKSHELF -> return Messages.get(CityLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        addCityVisuals(this, visuals)
        return visuals
    }

    private class Smoke(private val pos: Int) : Emitter() {

        init {

            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 6, p.y - 4, 12f, 12f)

            pour(factory, 0.2f)
        }

        override fun update() {
            if (visible = pos < Dungeon.level!!.heroFOV.size && Dungeon.level!!.heroFOV[pos]) {
                super.update()
            }
        }

        companion object {

            private val factory = object : Emitter.Factory() {

                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    val p = emitter.recycle(SmokeParticle::class.java) as SmokeParticle
                    p.reset(x, y)
                }
            }
        }
    }

    class SmokeParticle : PixelParticle() {
        init {

            color(0x000000)
            speed.set(Random.Float(-2f, 4f), -Random.Float(3f, 6f))
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            lifespan = 2f
            left = lifespan
        }

        override fun update() {
            super.update()
            val p = left / lifespan
            am = if (p > 0.8f) 1 - p else p * 0.25f
            size(6 - p * 3)
        }
    }

    companion object {

        fun addCityVisuals(level: Level, group: Group?) {
            for (i in 0 until level.length()) {
                if (level.map!![i] == Terrain.WALL_DECO) {
                    group!!.add(Smoke(i))
                }
            }
        }
    }
}