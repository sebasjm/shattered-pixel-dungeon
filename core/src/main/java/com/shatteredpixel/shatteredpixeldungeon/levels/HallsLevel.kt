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
import com.shatteredpixel.shatteredpixeldungeon.items.Torch
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.HallsPainter
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.glwrap.Blending
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random

class HallsLevel : RegularLevel() {

    init {

        viewDistance = Math.min(26 - Dungeon.depth, viewDistance)

        color1 = 0x801500
        color2 = 0xa68521
    }

    override fun standardRooms(): Int {
        //8 to 10, average 8.67
        return 8 + Random.chances(floatArrayOf(3f, 2f, 1f))
    }

    override fun specialRooms(): Int {
        //2 to 3, average 2.5
        return 2 + Random.chances(floatArrayOf(1f, 1f))
    }

    override fun painter(): Painter {
        return HallsPainter()
                .setWater(if (feeling == Level.Feeling.WATER) 0.70f else 0.15f, 6)
                .setGrass(if (feeling == Level.Feeling.GRASS) 0.65f else 0.10f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances())
    }

    override fun create() {
        addItemToSpawn(Torch())
        super.create()
    }

    override fun tilesTex(): String? {
        return Assets.TILES_HALLS
    }

    override fun waterTex(): String? {
        return Assets.WATER_HALLS
    }

    override fun trapClasses(): Array<Class<*>> {
        return arrayOf(FrostTrap::class.java, StormTrap::class.java, CorrosionTrap::class.java, BlazingTrap::class.java, DisintegrationTrap::class.java, ExplosiveTrap::class.java, RockfallTrap::class.java, FlashingTrap::class.java, GuardianTrap::class.java, WeakeningTrap::class.java, SummoningTrap::class.java, WarpingTrap::class.java, CursingTrap::class.java, GrimTrap::class.java, PitfallTrap::class.java, DisarmingTrap::class.java, DistortionTrap::class.java)
    }

    override fun trapChances(): FloatArray {
        return floatArrayOf(8f, 8f, 8f, 8f, 8f, 4f, 4f, 4f, 4f, 4f, 2f, 2f, 2f, 2f, 1f, 1f, 1f)
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(HallsLevel::class.java, "water_name")
            Terrain.GRASS -> return Messages.get(HallsLevel::class.java, "grass_name")
            Terrain.HIGH_GRASS -> return Messages.get(HallsLevel::class.java, "high_grass_name")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(HallsLevel::class.java, "statue_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(HallsLevel::class.java, "water_desc")
            Terrain.STATUE, Terrain.STATUE_SP -> return Messages.get(HallsLevel::class.java, "statue_desc")
            Terrain.BOOKSHELF -> return Messages.get(HallsLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        addHallsVisuals(this, visuals)
        return visuals
    }

    private class Stream(private val pos: Int) : Group() {

        private var delay: Float = 0.toFloat()

        init {

            delay = Random.Float(2f)
        }

        override fun update() {

            if (visible = pos < Dungeon.level!!.heroFOV.size && Dungeon.level!!.heroFOV[pos]) {

                super.update()

                if ((delay -= Game.elapsed) <= 0) {

                    delay = Random.Float(2f)

                    val p = DungeonTilemap.tileToWorld(pos)
                    (recycle(FireParticle::class.java) as FireParticle).reset(
                            p.x + Random.Float(DungeonTilemap.SIZE.toFloat()),
                            p.y + Random.Float(DungeonTilemap.SIZE.toFloat()))
                }
            }
        }

        override fun draw() {
            Blending.setLightMode()
            super.draw()
            Blending.setNormalMode()
        }
    }

    class FireParticle : PixelParticle.Shrinking() {
        init {

            color(0xEE7722)
            lifespan = 1f

            acc.set(0f, +80f)
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            left = lifespan

            speed.set(0f, -40f)
            size = 4f
        }

        override fun update() {
            super.update()
            val p = left / lifespan
            am = if (p > 0.8f) (1 - p) * 5 else 1
        }
    }

    companion object {

        fun addHallsVisuals(level: Level, group: Group?) {
            for (i in 0 until level.length()) {
                if (level.map!![i] == Terrain.WATER) {
                    group!!.add(Stream(i))
                }
            }
        }
    }
}
