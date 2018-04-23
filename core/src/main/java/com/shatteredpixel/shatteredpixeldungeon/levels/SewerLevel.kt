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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost
import com.shatteredpixel.shatteredpixeldungeon.effects.Ripple
import com.shatteredpixel.shatteredpixeldungeon.items.DewVial
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.SewerPainter
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.PointF
import com.watabou.utils.Random

open class SewerLevel : RegularLevel() {

    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }

    override fun standardRooms(): Int {
        //5 to 7, average 5.57
        return 5 + Random.chances(floatArrayOf(4f, 2f, 1f))
    }

    override fun specialRooms(): Int {
        //1 to 3, average 1.67
        return 1 + Random.chances(floatArrayOf(4f, 4f, 2f))
    }

    override fun painter(): Painter {
        return SewerPainter()
                .setWater(if (feeling == Level.Feeling.WATER) 0.85f else 0.30f, 5)
                .setGrass(if (feeling == Level.Feeling.GRASS) 0.80f else 0.20f, 4)
                .setTraps(nTraps(), trapClasses(), trapChances())
    }

    override fun tilesTex(): String? {
        return Assets.TILES_SEWERS
    }

    override fun waterTex(): String? {
        return Assets.WATER_SEWERS
    }

    override fun trapClasses(): Array<Class<*>> {
        return if (Dungeon.depth == 1)
            arrayOf(WornDartTrap::class.java)
        else
            arrayOf(ChillingTrap::class.java, ShockingTrap::class.java, ToxicTrap::class.java, WornDartTrap::class.java, AlarmTrap::class.java, OozeTrap::class.java, ConfusionTrap::class.java, FlockTrap::class.java, SummoningTrap::class.java, TeleportationTrap::class.java)
    }

    override fun trapChances(): FloatArray {
        return if (Dungeon.depth == 1)
            floatArrayOf(1f)
        else
            floatArrayOf(8f, 8f, 8f, 8f, 4f, 4f, 2f, 2f, 2f, 2f)
    }

    override fun createItems() {
        if (!Dungeon.LimitedDrops.DEW_VIAL.dropped()) {
            addItemToSpawn(DewVial())
            Dungeon.LimitedDrops.DEW_VIAL.drop()
        }

        Ghost.Quest.spawn(this)

        super.createItems()
    }

    override fun addVisuals(): Group? {
        super.addVisuals()
        addSewerVisuals(this, visuals)
        return visuals
    }

    override fun tileName(tile: Int): String {
        when (tile) {
            Terrain.WATER -> return Messages.get(SewerLevel::class.java, "water_name")
            else -> return super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        when (tile) {
            Terrain.EMPTY_DECO -> return Messages.get(SewerLevel::class.java, "empty_deco_desc")
            Terrain.BOOKSHELF -> return Messages.get(SewerLevel::class.java, "bookshelf_desc")
            else -> return super.tileDesc(tile)
        }
    }

    private class Sink(private val pos: Int) : Emitter() {
        private var rippleDelay = 0f

        init {

            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 2, p.y + 3, 4f, 0f)

            pour(factory!!, 0.1f)
        }

        override fun update() {
            visible = pos < Dungeon.level!!.heroFOV.size && Dungeon.level!!.heroFOV[pos]
            if (visible) {

                super.update()

                rippleDelay -= Game.elapsed
                if (rippleDelay <= 0) {
                    val ripple = GameScene.ripple(pos + Dungeon.level!!.width())
                    if (ripple != null) {
                        ripple.y -= (DungeonTilemap.SIZE / 2).toFloat()
                        rippleDelay = Random.Float(0.4f, 0.6f)
                    }
                }
            }
        }

        companion object {

            private val factory = object : Emitter.Factory() {

                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    val p = emitter.recycle(WaterParticle::class.java) as WaterParticle
                    p.reset(x, y)
                }
            }
        }
    }

    class WaterParticle : PixelParticle() {
        init {

            acc.y = 50f
            am = 0.5f

            color(ColorMath.random(0xb6ccc2, 0x3b6653))
            size(2f)
        }

        fun reset(x: Float, y: Float) {
            revive()

            this.x = x
            this.y = y

            speed.set(Random.Float(-2f, +2f), 0f)

            lifespan = 0.4f
            left = lifespan
        }
    }

    companion object {

        fun addSewerVisuals(level: Level, group: Group?) {
            for (i in 0 until level.length()) {
                if (level.map!![i] == Terrain.WALL_DECO) {
                    group!!.add(Sink(i))
                }
            }
        }
    }
}
