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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random

import java.util.ArrayList

class CavesLevel : RegularLevel() {

    init {
        color1 = 0x534f3e
        color2 = 0xb9d661

        viewDistance = Math.min(6, viewDistance)
    }

    override fun initRooms(): ArrayList<Room> {
        return Blacksmith.Quest.spawn(super.initRooms())
    }

    override fun standardRooms(): Int {
        //6 to 9, average 7.333
        return 6 + Random.chances(floatArrayOf(2f, 3f, 3f, 1f))
    }

    override fun specialRooms(): Int {
        //1 to 3, average 2.2
        return 1 + Random.chances(floatArrayOf(2f, 4f, 4f))
    }

    override fun painter(): Painter {
        return CavesPainter()
                .setWater(if (feeling == Level.Feeling.WATER) 0.85f else 0.30f, 6)
                .setGrass(if (feeling == Level.Feeling.GRASS) 0.65f else 0.15f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances())
    }

    override fun tilesTex(): String? {
        return Assets.TILES_CAVES
    }

    override fun waterTex(): String? {
        return Assets.WATER_CAVES
    }

    override fun trapClasses(): Array<Class<*>> {
        return arrayOf(BurningTrap::class.java, PoisonDartTrap::class.java, FrostTrap::class.java, StormTrap::class.java, CorrosionTrap::class.java, GrippingTrap::class.java, ExplosiveTrap::class.java, RockfallTrap::class.java, GuardianTrap::class.java, ConfusionTrap::class.java, SummoningTrap::class.java, WarpingTrap::class.java, PitfallTrap::class.java)
    }

    override fun trapChances(): FloatArray {
        return floatArrayOf(8f, 8f, 8f, 8f, 8f, 4f, 4f, 4f, 4f, 2f, 2f, 2f, 1f)
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.GRASS -> return Messages.get(CavesLevel::class.java, "grass_name")
            Terrain.HIGH_GRASS -> return Messages.get(CavesLevel::class.java, "high_grass_name")
            Terrain.WATER -> return Messages.get(CavesLevel::class.java, "water_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.ENTRANCE -> return Messages.get(CavesLevel::class.java, "entrance_desc")
            Terrain.EXIT -> return Messages.get(CavesLevel::class.java, "exit_desc")
            Terrain.HIGH_GRASS -> return Messages.get(CavesLevel::class.java, "high_grass_desc")
            Terrain.WALL_DECO -> return Messages.get(CavesLevel::class.java, "wall_deco_desc")
            Terrain.BOOKSHELF -> return Messages.get(CavesLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        addCavesVisuals(this, visuals)
        return visuals
    }

    private class Vein(private val pos: Int) : Group() {

        private var delay: Float = 0.toFloat()

        init {

            delay = Random.Float(2f)
        }

        override fun update() {

            visible = pos < Dungeon.level!!.heroFOV.size && Dungeon.level!!.heroFOV[pos]
            if (visible) {

                super.update()

                delay -= Game.elapsed
                if (delay <= 0) {

                    //pickaxe can remove the ore, should remove the sparkling too.
                    if (Dungeon.level!!.map!![pos] != Terrain.WALL_DECO) {
                        kill()
                        return
                    }

                    delay = Random.Float()

                    val p = DungeonTilemap.tileToWorld(pos)
                    (recycle(Sparkle::class.java) as Sparkle).reset(
                            p.x + Random.Float(DungeonTilemap.SIZE.toFloat()),
                            p.y + Random.Float(DungeonTilemap.SIZE.toFloat()))
                }
            }
        }
    }

    class Sparkle : PixelParticle() {

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            lifespan = 0.5f
            left = lifespan
        }

        override fun update() {
            super.update()

            val p = left / lifespan
            am = if (p < 0.5f) p * 2 else (1 - p) * 2
            size(am * 2)
        }
    }

    companion object {

        fun addCavesVisuals(level: Level, group: Group?) {
            for (i in 0 until level.length()) {
                if (level.map!![i] == Terrain.WALL_DECO) {
                    group!!.add(Vein(i))
                }
            }
        }
    }
}