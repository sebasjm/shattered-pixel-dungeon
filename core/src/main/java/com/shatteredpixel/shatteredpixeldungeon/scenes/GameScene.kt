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

package com.shatteredpixel.shatteredpixeldungeon.scenes

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText
import com.shatteredpixel.shatteredpixeldungeon.effects.Ripple
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.items.Heap
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.DiscardedItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonWallsTilemap
import com.shatteredpixel.shatteredpixeldungeon.tiles.FogOfWar
import com.shatteredpixel.shatteredpixeldungeon.tiles.GridTileMap
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap
import com.shatteredpixel.shatteredpixeldungeon.tiles.WallBlockingTilemap
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner
import com.shatteredpixel.shatteredpixeldungeon.ui.BusyIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.CharHealthIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog
import com.shatteredpixel.shatteredpixeldungeon.ui.LootIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton
import com.shatteredpixel.shatteredpixeldungeon.ui.ResumeIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag.Mode
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGame
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoPlant
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoTrap
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem
import com.watabou.glwrap.Blending
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.NoosaScriptNoLighting
import com.watabou.noosa.SkinnedBlock
import com.watabou.noosa.Visual
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.Emitter
import com.watabou.utils.GameMath
import com.watabou.utils.Random

import java.io.IOException
import java.util.ArrayList
import java.util.Locale

class GameScene : PixelScene() {

    private var water: SkinnedBlock? = null
    private var tiles: DungeonTerrainTilemap? = null
    private var visualGrid: GridTileMap? = null
    private var terrainFeatures: TerrainFeaturesTilemap? = null
    private var walls: DungeonWallsTilemap? = null
    private var wallBlocking: WallBlockingTilemap? = null
    private var fog: FogOfWar? = null
    private var hero: HeroSprite? = null

    private var pane: StatusPane? = null

    private var log: GameLog? = null

    private var busy: BusyIndicator? = null

    private var terrain: Group? = null
    private var customTiles: Group? = null
    private var levelVisuals: Group? = null
    private var customWalls: Group? = null
    private var ripples: Group? = null
    private val plants: Group? = null
    private val traps: Group? = null
    private var heaps: Group? = null
    private var mobs: Group? = null
    private var emitters: Group? = null
    private var effects: Group? = null
    private var gases: Group? = null
    private var spells: Group? = null
    private var statuses: Group? = null
    private var emoicons: Group? = null
    private var healthIndicators: Group? = null

    private var toolbar: Toolbar? = null
    private var prompt: Toast? = null

    private var attack: AttackIndicator? = null
    private var loot: LootIndicator? = null
    private var action: ActionIndicator? = null
    private var resume: ResumeIndicator? = null

    private var tagAttack = false
    private var tagLoot = false
    private var tagAction = false
    private var tagResume = false

    override fun create() {

        Music.INSTANCE.play(Assets.TUNE, true)

        SPDSettings.lastClass(Dungeon.hero!!.heroClass.ordinal)

        super.create()
        Camera.main.zoom(GameMath.gate(PixelScene.minZoom, (PixelScene.defaultZoom + SPDSettings.zoom()).toFloat(), PixelScene.maxZoom))

        scene = this

        terrain = Group()
        add(terrain)

        water = object : SkinnedBlock(
                (Dungeon.level!!.width() * DungeonTilemap.SIZE).toFloat(),
                (Dungeon.level!!.height() * DungeonTilemap.SIZE).toFloat(),
                Dungeon.level!!.waterTex()) {

            override fun script(): NoosaScript {
                return NoosaScriptNoLighting.get()
            }

            override fun draw() {
                //water has no alpha component, this improves performance
                Blending.disable()
                super.draw()
                Blending.enable()
            }
        }
        terrain!!.add(water)

        ripples = Group()
        terrain!!.add(ripples)

        DungeonTileSheet.setupVariance(Dungeon.level!!.map!!.size, Dungeon.seedCurDepth())

        tiles = DungeonTerrainTilemap()
        terrain!!.add(tiles)

        customTiles = Group()
        terrain!!.add(customTiles)

        for (visual in Dungeon.level!!.customTiles) {
            addCustomTile(visual)
        }

        visualGrid = GridTileMap()
        terrain!!.add(visualGrid)

        terrainFeatures = TerrainFeaturesTilemap(Dungeon.level!!.plants, Dungeon.level!!.traps)
        terrain!!.add(terrainFeatures)

        levelVisuals = Dungeon.level!!.addVisuals()
        add(levelVisuals)

        heaps = Group()
        add(heaps)

        val size = Dungeon.level!!.heaps.size()
        for (i in 0 until size) {
            addHeapSprite(Dungeon.level!!.heaps.valueAt(i))
        }

        emitters = Group()
        effects = Group()
        healthIndicators = Group()
        emoicons = Group()

        mobs = Group()
        add(mobs)

        for (mob in Dungeon.level!!.mobs) {
            addMobSprite(mob)
            if (Statistics.amuletObtained) {
                mob.beckon(Dungeon.hero!!.pos)
            }
        }

        walls = DungeonWallsTilemap()
        add(walls)

        customWalls = Group()
        add(customWalls)

        for (visual in Dungeon.level!!.customWalls) {
            addCustomWall(visual)
        }

        wallBlocking = WallBlockingTilemap()
        add(wallBlocking)

        add(emitters)
        add(effects)

        gases = Group()
        add(gases)

        for (blob in Dungeon.level!!.blobs.values) {
            blob.emitter = null
            addBlobSprite(blob)
        }


        fog = FogOfWar(Dungeon.level!!.width(), Dungeon.level!!.height())
        add(fog)

        spells = Group()
        add(spells)

        statuses = Group()
        add(statuses)

        add(healthIndicators)
        //always appears ontop of other health indicators
        add(TargetHealthIndicator())

        add(emoicons)

        hero = HeroSprite()
        hero!!.place(Dungeon.hero!!.pos)
        hero!!.updateArmor()
        mobs!!.add(hero)

        add(cellSelector = CellSelector(tiles))

        pane = StatusPane()
        pane!!.camera = PixelScene.uiCamera
        pane!!.setSize(PixelScene.uiCamera.width.toFloat(), 0f)
        add(pane)

        toolbar = Toolbar()
        toolbar!!.camera = PixelScene.uiCamera
        toolbar!!.setRect(0f, PixelScene.uiCamera.height - toolbar!!.height(), PixelScene.uiCamera.width.toFloat(), toolbar!!.height())
        add(toolbar)

        attack = AttackIndicator()
        attack!!.camera = PixelScene.uiCamera
        add(attack)

        loot = LootIndicator()
        loot!!.camera = PixelScene.uiCamera
        add(loot)

        action = ActionIndicator()
        action!!.camera = PixelScene.uiCamera
        add(action)

        resume = ResumeIndicator()
        resume!!.camera = PixelScene.uiCamera
        add(resume)

        log = GameLog()
        log!!.camera = PixelScene.uiCamera
        log!!.newLine()
        add(log)

        layoutTags()

        busy = BusyIndicator()
        busy!!.camera = PixelScene.uiCamera
        busy!!.x = 1f
        busy!!.y = pane!!.bottom() + 1
        add(busy)

        when (InterlevelScene.mode) {
            InterlevelScene.Mode.RESURRECT -> {
                ScrollOfTeleportation.appear(Dungeon.hero!!, Dungeon.level!!.entrance)
                Flare(8, 32f).color(0xFFFF66, true).show(hero, 2f)
            }
            InterlevelScene.Mode.RETURN -> ScrollOfTeleportation.appear(Dungeon.hero!!, Dungeon.hero!!.pos)
            InterlevelScene.Mode.DESCEND -> {
                when (Dungeon.depth) {
                    1 -> WndStory.showChapter(WndStory.ID_SEWERS)
                    6 -> WndStory.showChapter(WndStory.ID_PRISON)
                    11 -> WndStory.showChapter(WndStory.ID_CAVES)
                    16 -> WndStory.showChapter(WndStory.ID_CITY)
                    22 -> WndStory.showChapter(WndStory.ID_HALLS)
                }
                if (Dungeon.hero!!.isAlive && Dungeon.depth != 22) {
                    Badges.validateNoKilling()
                }
            }
        }

        val dropped = Dungeon.droppedItems.get(Dungeon.depth)
        if (dropped != null) {
            for (item in dropped) {
                val pos = Dungeon.level!!.randomRespawnCell()
                (item as? Potion)?.shatter(pos) ?: if (item is Plant.Seed) {
                    Dungeon.level!!.plant(item, pos)
                } else if (item is Honeypot) {
                    Dungeon.level!!.drop(item.shatter(null, pos), pos)
                } else {
                    Dungeon.level!!.drop(item, pos)
                }
            }
            Dungeon.droppedItems.remove(Dungeon.depth)
        }

        Dungeon.hero!!.next()

        Camera.main.target = hero

        if (InterlevelScene.mode != InterlevelScene.Mode.NONE) {
            if (Dungeon.depth == Statistics.deepestFloor && (InterlevelScene.mode == InterlevelScene.Mode.DESCEND || InterlevelScene.mode == InterlevelScene.Mode.FALL)) {
                GLog.h(Messages.get(this, "descend"), Dungeon.depth)
                Sample.INSTANCE.play(Assets.SND_DESCEND)
            } else if (InterlevelScene.mode == InterlevelScene.Mode.RESET) {
                GLog.h(Messages.get(this, "warp"))
            } else {
                GLog.h(Messages.get(this, "return"), Dungeon.depth)
            }

            when (Dungeon.level!!.feeling) {
                Level.Feeling.CHASM -> GLog.w(Messages.get(this, "chasm"))
                Level.Feeling.WATER -> GLog.w(Messages.get(this, "water"))
                Level.Feeling.GRASS -> GLog.w(Messages.get(this, "grass"))
                Level.Feeling.DARK -> GLog.w(Messages.get(this, "dark"))
            }
            if (Dungeon.level is RegularLevel && (Dungeon.level as RegularLevel).secretDoors > Random.IntRange(3, 4)) {
                GLog.w(Messages.get(this, "secrets"))
            }

            InterlevelScene.mode = InterlevelScene.Mode.NONE

            fadeIn()
        }

    }

    override fun destroy() {

        //tell the actor thread to finish, then wait for it to complete any actions it may be doing.
        if (actorThread.isAlive) {
            synchronized(GameScene::class.java) {
                synchronized(actorThread) {
                    actorThread.interrupt()
                }
                try {
                    GameScene::class.java!!.wait(5000)
                } catch (e: InterruptedException) {
                    ShatteredPixelDungeon.reportException(e)
                }

                synchronized(actorThread) {
                    if (Actor.processing()) {
                        val t = Throwable()
                        t.stackTrace = actorThread.stackTrace
                        throw RuntimeException("timeout waiting for actor thread! ", t)
                    }
                }
            }
        }

        Group.freezeEmitters = false

        scene = null
        Badges.saveGlobal()
        Journal.saveGlobal()

        super.destroy()
    }

    @Synchronized
    override fun onPause() {
        try {
            Dungeon.saveAll()
            Badges.saveGlobal()
            Journal.saveGlobal()
        } catch (e: IOException) {
            ShatteredPixelDungeon.reportException(e)
        }

    }

    @Synchronized
    override fun update() {
        if (Dungeon.hero == null || scene == null) {
            return
        }

        super.update()

        if (!Group.freezeEmitters) water!!.offset(0f, -5 * Game.elapsed)

        if (!Actor.processing() && Dungeon.hero!!.isAlive) {
            if (!actorThread.isAlive) {
                //if cpu cores are limited, game should prefer drawing the current frame
                if (Runtime.getRuntime().availableProcessors() == 1) {
                    actorThread.priority = Thread.NORM_PRIORITY - 1
                }
                actorThread.start()
            } else {
                synchronized(actorThread) {
                    actorThread.notify()
                }
            }
        }

        if (Dungeon.hero!!.ready && Dungeon.hero!!.paralysed == 0) {
            log!!.newLine()
        }

        if (tagAttack != attack!!.active ||
                tagLoot != loot!!.visible ||
                tagAction != action!!.visible ||
                tagResume != resume!!.visible) {

            //we only want to change the layout when new tags pop in, not when existing ones leave.
            val tagAppearing = attack!!.active && !tagAttack ||
                    loot!!.visible && !tagLoot ||
                    action!!.visible && !tagAction ||
                    resume!!.visible && !tagResume

            tagAttack = attack!!.active
            tagLoot = loot!!.visible
            tagAction = action!!.visible
            tagResume = resume!!.visible

            if (tagAppearing) layoutTags()
        }

        cellSelector!!.enable(Dungeon.hero!!.ready)
    }

    override fun onBackPressed() {
        if (!cancel()) {
            add(WndGame())
        }
    }

    override fun onMenuPressed() {
        if (Dungeon.hero!!.ready) {
            selectItem(null, WndBag.Mode.ALL, null)
        }
    }

    fun addCustomTile(visual: CustomTiledVisual) {
        customTiles!!.add(visual.create())
    }

    fun addCustomWall(visual: CustomTiledVisual) {
        customWalls!!.add(visual.create())
    }

    private fun addHeapSprite(heap: Heap) {
        heap.sprite = heaps!!.recycle(ItemSprite::class.java) as ItemSprite
        val sprite = heap.sprite
        sprite.revive()
        sprite.link(heap)
        heaps!!.add(sprite)
    }

    private fun addDiscardedSprite(heap: Heap) {
        heap.sprite = heaps!!.recycle(DiscardedItemSprite::class.java) as DiscardedItemSprite
        heap.sprite!!.revive()
        heap.sprite!!.link(heap)
        heaps!!.add(heap.sprite)
    }

    private fun addPlantSprite(plant: Plant) {

    }

    private fun addTrapSprite(trap: Trap) {

    }

    private fun addBlobSprite(gas: Blob) {
        if (gas.emitter == null) {
            gases!!.add(BlobEmitter(gas))
        }
    }

    private fun addMobSprite(mob: Mob) {
        val sprite = mob.sprite()
        sprite!!.visible = Dungeon.level!!.heroFOV[mob.pos]
        mobs!!.add(sprite)
        sprite.link(mob)
    }

    @Synchronized
    private fun prompt(text: String?) {

        if (prompt != null) {
            prompt!!.killAndErase()
            prompt!!.destroy()
            prompt = null
        }

        if (text != null) {
            prompt = object : Toast(text) {
                override fun onClose() {
                    cancel()
                }
            }
            prompt!!.camera = PixelScene.uiCamera
            prompt!!.setPos((PixelScene.uiCamera.width - prompt!!.width()) / 2, (PixelScene.uiCamera.height - 60).toFloat())
            add(prompt)
        }
    }

    private fun showBanner(banner: Banner) {
        banner.camera = PixelScene.uiCamera
        banner.x = PixelScene.align(PixelScene.uiCamera, (PixelScene.uiCamera.width - banner.width) / 2)
        banner.y = PixelScene.align(PixelScene.uiCamera, (PixelScene.uiCamera.height - banner.height) / 3)
        addToFront(banner)
    }

    companion object {

        internal var scene: GameScene? = null

        private var cellSelector: CellSelector? = null

        private val actorThread = object : Thread() {
            override fun run() {
                Actor.process()
            }
        }

        fun layoutTags() {

            if (scene == null) return

            val tagLeft = if (SPDSettings.flipTags()) 0 else PixelScene.uiCamera.width - scene!!.attack!!.width()

            if (SPDSettings.flipTags()) {
                scene!!.log!!.setRect(scene!!.attack!!.width(), scene!!.toolbar!!.top(), PixelScene.uiCamera.width - scene!!.attack!!.width(), 0f)
            } else {
                scene!!.log!!.setRect(0f, scene!!.toolbar!!.top(), PixelScene.uiCamera.width - scene!!.attack!!.width(), 0f)
            }

            var pos = scene!!.toolbar!!.top()

            if (scene!!.tagAttack) {
                scene!!.attack!!.setPos(tagLeft, pos - scene!!.attack!!.height())
                scene!!.attack!!.flip(tagLeft == 0f)
                pos = scene!!.attack!!.top()
            }

            if (scene!!.tagLoot) {
                scene!!.loot!!.setPos(tagLeft, pos - scene!!.loot!!.height())
                scene!!.loot!!.flip(tagLeft == 0f)
                pos = scene!!.loot!!.top()
            }

            if (scene!!.tagAction) {
                scene!!.action!!.setPos(tagLeft, pos - scene!!.action!!.height())
                scene!!.action!!.flip(tagLeft == 0f)
                pos = scene!!.action!!.top()
            }

            if (scene!!.tagResume) {
                scene!!.resume!!.setPos(tagLeft, pos - scene!!.resume!!.height())
                scene!!.resume!!.flip(tagLeft == 0f)
            }
        }

        // -------------------------------------------------------

        fun add(plant: Plant) {
            if (scene != null) {
                scene!!.addPlantSprite(plant)
            }
        }

        fun add(trap: Trap) {
            if (scene != null) {
                scene!!.addTrapSprite(trap)
            }
        }

        fun add(gas: Blob) {
            Actor.add(gas)
            if (scene != null) {
                scene!!.addBlobSprite(gas)
            }
        }

        fun add(heap: Heap) {
            if (scene != null) {
                scene!!.addHeapSprite(heap)
            }
        }

        fun discard(heap: Heap) {
            if (scene != null) {
                scene!!.addDiscardedSprite(heap)
            }
        }

        fun add(mob: Mob) {
            Dungeon.level!!.mobs.add(mob)
            Actor.add(mob)
            scene!!.addMobSprite(mob)
        }

        fun add(mob: Mob, delay: Float) {
            Dungeon.level!!.mobs.add(mob)
            Actor.addDelayed(mob, delay)
            scene!!.addMobSprite(mob)
        }

        fun add(icon: EmoIcon) {
            scene!!.emoicons!!.add(icon)
        }

        fun add(indicator: CharHealthIndicator) {
            if (scene != null) scene!!.healthIndicators!!.add(indicator)
        }

        fun effect(effect: Visual) {
            scene!!.effects!!.add(effect)
        }

        fun ripple(pos: Int): Ripple? {
            if (scene != null) {
                val ripple = scene!!.ripples!!.recycle(Ripple::class.java) as Ripple
                ripple.reset(pos)
                return ripple
            } else {
                return null
            }
        }

        fun spellSprite(): SpellSprite {
            return scene!!.spells!!.recycle(SpellSprite::class.java) as SpellSprite
        }

        fun emitter(): Emitter? {
            if (scene != null) {
                val emitter = scene!!.emitters!!.recycle(Emitter::class.java) as Emitter
                emitter.revive()
                return emitter
            } else {
                return null
            }
        }

        fun status(): FloatingText? {
            return if (scene != null) scene!!.statuses!!.recycle(FloatingText::class.java) as FloatingText else null
        }

        fun pickUp(item: Item, pos: Int) {
            if (scene != null) scene!!.toolbar!!.pickup(item, pos)
        }

        fun pickUpJournal(item: Item, pos: Int) {
            if (scene != null) scene!!.pane!!.pickup(item, pos)
        }

        fun flashJournal() {
            if (scene != null) scene!!.pane!!.flash()
        }

        fun updateKeyDisplay() {
            if (scene != null) scene!!.pane!!.updateKeys()
        }

        fun resetMap() {
            if (scene != null) {
                scene!!.tiles!!.map(Dungeon.level!!.map, Dungeon.level!!.width())
                scene!!.visualGrid!!.map(Dungeon.level!!.map, Dungeon.level!!.width())
                scene!!.terrainFeatures!!.map(Dungeon.level!!.map, Dungeon.level!!.width())
                scene!!.walls!!.map(Dungeon.level!!.map, Dungeon.level!!.width())
            }
            updateFog()
        }

        //updates the whole map
        fun updateMap() {
            if (scene != null) {
                scene!!.tiles!!.updateMap()
                scene!!.visualGrid!!.updateMap()
                scene!!.terrainFeatures!!.updateMap()
                scene!!.walls!!.updateMap()
                updateFog()
            }
        }

        fun updateMap(cell: Int) {
            if (scene != null) {
                scene!!.tiles!!.updateMapCell(cell)
                scene!!.visualGrid!!.updateMapCell(cell)
                scene!!.terrainFeatures!!.updateMapCell(cell)
                scene!!.walls!!.updateMapCell(cell)
                //update adjacent cells too
                updateFog(cell, 1)
            }
        }

        fun plantSeed(cell: Int) {
            if (scene != null) {
                scene!!.terrainFeatures!!.growPlant(cell)
            }
        }

        //todo this doesn't account for walls right now
        fun discoverTile(pos: Int, oldValue: Int) {
            if (scene != null) {
                scene!!.tiles!!.discover(pos, oldValue)
            }
        }

        fun show(wnd: Window) {
            if (scene != null) {
                cancelCellSelector()
                scene!!.addToFront(wnd)
            }
        }

        fun updateFog() {
            if (scene != null) {
                scene!!.fog!!.updateFog()
                scene!!.wallBlocking!!.updateMap()
            }
        }

        fun updateFog(x: Int, y: Int, w: Int, h: Int) {
            if (scene != null) {
                scene!!.fog!!.updateFogArea(x, y, w, h)
                scene!!.wallBlocking!!.updateArea(x, y, w, h)
            }
        }

        fun updateFog(cell: Int, radius: Int) {
            if (scene != null) {
                scene!!.fog!!.updateFog(cell, radius)
                scene!!.wallBlocking!!.updateArea(cell, radius)
            }
        }

        fun afterObserve() {
            if (scene != null) {
                for (mob in Dungeon.level!!.mobs) {
                    if (mob.sprite != null)
                        mob.sprite!!.visible = Dungeon.level!!.heroFOV[mob.pos]
                }
            }
        }

        fun flash(color: Int) {
            scene!!.fadeIn(-0x1000000 or color, true)
        }

        fun gameOver() {
            val gameOver = Banner(BannerSprites.get(BannerSprites.Type.GAME_OVER))
            gameOver.show(0x000000, 1f)
            scene!!.showBanner(gameOver)

            Sample.INSTANCE.play(Assets.SND_DEATH)
        }

        fun bossSlain() {
            if (Dungeon.hero!!.isAlive) {
                val bossSlain = Banner(BannerSprites.get(BannerSprites.Type.BOSS_SLAIN))
                bossSlain.show(0xFFFFFF, 0.3f, 5f)
                scene!!.showBanner(bossSlain)

                Sample.INSTANCE.play(Assets.SND_BOSS)
            }
        }

        fun handleCell(cell: Int) {
            cellSelector!!.select(cell)
        }

        fun selectCell(listener: CellSelector.Listener) {
            cellSelector!!.listener = listener
            if (scene != null)
                scene!!.prompt(listener.prompt())
        }

        private fun cancelCellSelector(): Boolean {
            if (cellSelector!!.listener != null && cellSelector!!.listener !== defaultCellListener) {
                cellSelector!!.cancel()
                return true
            } else {
                return false
            }
        }

        fun selectItem(listener: WndBag.Listener?, mode: WndBag.Mode, title: String?): WndBag {
            cancelCellSelector()

            val wnd = if (mode == Mode.SEED)
                WndBag.getBag(VelvetPouch::class.java, listener, mode, title)
            else if (mode == Mode.SCROLL)
                WndBag.getBag(ScrollHolder::class.java, listener, mode, title)
            else if (mode == Mode.POTION)
                WndBag.getBag(PotionBandolier::class.java, listener, mode, title)
            else if (mode == Mode.WAND)
                WndBag.getBag(MagicalHolster::class.java, listener, mode, title)
            else
                WndBag.lastBag(listener, mode, title)

            if (scene != null) scene!!.addToFront(wnd)

            return wnd
        }

        internal fun cancel(): Boolean {
            if (Dungeon.hero != null && (Dungeon.hero!!.curAction != null || Dungeon.hero!!.resting)) {

                Dungeon.hero!!.curAction = null
                Dungeon.hero!!.resting = false
                return true

            } else {

                return cancelCellSelector()

            }
        }

        fun ready() {
            selectCell(defaultCellListener)
            QuickSlotButton.cancel()
            if (scene != null && scene!!.toolbar != null) scene!!.toolbar!!.examining = false
        }

        fun examineCell(cell: Int?) {
            if (cell == null
                    || cell < 0
                    || cell > Dungeon.level!!.length()
                    || !Dungeon.level!!.visited!![cell] && !Dungeon.level!!.mapped!![cell]) {
                return
            }

            val names = ArrayList<String>()
            val objects = ArrayList<Any>()

            if (cell == Dungeon.hero!!.pos) {
                objects.add(Dungeon.hero)
                names.add(Dungeon.hero!!.className().toUpperCase(Locale.ENGLISH))
            } else {
                if (Dungeon.level!!.heroFOV[cell]) {
                    val mob = Actor.findChar(cell) as Mob
                    if (mob != null) {
                        objects.add(mob)
                        names.add(Messages.titleCase(mob.name))
                    }
                }
            }

            val heap = Dungeon.level!!.heaps.get(cell)
            if (heap != null && heap.seen) {
                objects.add(heap)
                names.add(Messages.titleCase(heap.toString()))
            }

            val plant = Dungeon.level!!.plants.get(cell)
            if (plant != null) {
                objects.add(plant)
                names.add(Messages.titleCase(plant.plantName))
            }

            val trap = Dungeon.level!!.traps.get(cell)
            if (trap != null && trap.visible) {
                objects.add(trap)
                names.add(Messages.titleCase(trap.name))
            }

            if (objects.isEmpty()) {
                GameScene.show(WndInfoCell(cell))
            } else if (objects.size == 1) {
                examineObject(objects[0])
            } else {
                GameScene.show(object : WndOptions(Messages.get(GameScene::class.java, "choose_examine"),
                        Messages.get(GameScene::class.java, "multiple_examine"), *names.toTypedArray<String>()) {
                    override fun onSelect(index: Int) {
                        examineObject(objects[index])
                    }
                })

            }
        }

        fun examineObject(o: Any) {
            if (o === Dungeon.hero) {
                GameScene.show(WndHero())
            } else if (o is Mob) {
                GameScene.show(WndInfoMob(o))
            } else if (o is Heap) {
                val heap = o
                if (heap.type == Heap.Type.FOR_SALE && heap.size() == 1 && heap.peek().price() > 0) {
                    GameScene.show(WndTradeItem(heap, false))
                } else {
                    GameScene.show(WndInfoItem(heap))
                }
            } else if (o is Plant) {
                GameScene.show(WndInfoPlant(o))
            } else if (o is Trap) {
                GameScene.show(WndInfoTrap(o))
            } else {
                GameScene.show(WndMessage(Messages.get(GameScene::class.java, "dont_know")))
            }
        }


        private val defaultCellListener = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (Dungeon.hero!!.handle(cell!!)) {
                    Dungeon.hero!!.next()
                }
            }

            override fun prompt(): String? {
                return null
            }
        }
    }
}
