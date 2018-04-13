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

package com.shatteredpixel.shatteredpixeldungeon

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.WelcomeScene
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.utils.DeviceCompat

import javax.microedition.khronos.opengles.GL10

class ShatteredPixelDungeon : Game(WelcomeScene::class.java) {
    init {

        //v0.6.0
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MassGraveRoom.Bones::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.painters.MassGravePainter\$Bones")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RitualSiteRoom.RitualMarker::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.painters.RitualSitePainter\$RitualMarker")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom.HiddenWell::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.painters.WeakFloorPainter\$HiddenWell")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.Room")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.NewShortsword")

        //v0.6.0a
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.food.OverpricedRation")

        //v0.6.2
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.RatKingRoom::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RatKingRoom")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PlantsRoom::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.GardenRoom")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.GardenRoom::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.FoliageRoom")

        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornTrap")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonTrap")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.traps.ParalyticTrap")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.traps.LightningTrap")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.traps.SpearTrap")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.traps.FireTrap")

        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GasesImmunity")

        //v0.6.3
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tamahawk")

        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Dart")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.IncendiaryDart::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.IncendiaryDart")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.CurareDart")

        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfVenom")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.actors.blobs.VenomGas")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Venom")
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.levels.traps.VenomTrap")

        //v0.6.4
        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.bags.SeedPouch")

        com.watabou.utils.Bundle.addAlias(
                com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster::class.java!!,
                "com.shatteredpixel.shatteredpixeldungeon.items.bags.WandHolster")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateSystemUI()
        SPDSettings.landscape(SPDSettings.landscape())

        Music.INSTANCE.enable(SPDSettings.music())
        Music.INSTANCE.volume(SPDSettings.musicVol() / 10f)
        Sample.INSTANCE.enable(SPDSettings.soundFx())
        Sample.INSTANCE.volume(SPDSettings.SFXVol() / 10f)

        Music.setMuteListener()

        Sample.INSTANCE.load(
                Assets.SND_CLICK,
                Assets.SND_BADGE,
                Assets.SND_GOLD,

                Assets.SND_STEP,
                Assets.SND_WATER,
                Assets.SND_OPEN,
                Assets.SND_UNLOCK,
                Assets.SND_ITEM,
                Assets.SND_DEWDROP,
                Assets.SND_HIT,
                Assets.SND_MISS,

                Assets.SND_DESCEND,
                Assets.SND_EAT,
                Assets.SND_READ,
                Assets.SND_LULLABY,
                Assets.SND_DRINK,
                Assets.SND_SHATTER,
                Assets.SND_ZAP,
                Assets.SND_LIGHTNING,
                Assets.SND_LEVELUP,
                Assets.SND_DEATH,
                Assets.SND_CHALLENGE,
                Assets.SND_CURSED,
                Assets.SND_EVOKE,
                Assets.SND_TRAP,
                Assets.SND_TOMB,
                Assets.SND_ALERT,
                Assets.SND_MELD,
                Assets.SND_BOSS,
                Assets.SND_BLAST,
                Assets.SND_PLANT,
                Assets.SND_RAY,
                Assets.SND_BEACON,
                Assets.SND_TELEPORT,
                Assets.SND_CHARMS,
                Assets.SND_MASTERY,
                Assets.SND_PUFF,
                Assets.SND_ROCKS,
                Assets.SND_BURNING,
                Assets.SND_FALLING,
                Assets.SND_GHOST,
                Assets.SND_SECRET,
                Assets.SND_BONES,
                Assets.SND_BEE,
                Assets.SND_DEGRADE,
                Assets.SND_MIMIC)

        if (!SPDSettings.systemFont()) {
            RenderedText.setFont("pixelfont.ttf")
        } else {
            RenderedText.setFont(null)
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) updateSystemUI()
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        updateSystemUI()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

        super.onSurfaceChanged(gl, width, height)

        updateDisplaySize()

    }

    fun updateDisplaySize() {
        val landscape = SPDSettings.landscape()

        if (landscape != Game.width > Game.height) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                Game.instance.requestedOrientation = if (landscape)
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                else
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                Game.instance.requestedOrientation = if (landscape)
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                else
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        if (view.measuredWidth == 0 || view.measuredHeight == 0)
            return

        Game.dispWidth = view.measuredWidth
        Game.dispHeight = view.measuredHeight

        val dispRatio = Game.dispWidth / Game.dispHeight.toFloat()

        var renderWidth = if (dispRatio > 1) PixelScene.MIN_WIDTH_L else PixelScene.MIN_WIDTH_P
        var renderHeight = if (dispRatio > 1) PixelScene.MIN_HEIGHT_L else PixelScene.MIN_HEIGHT_P

        //force power saver in this case as all devices must run at at least 2x scale.
        if (Game.dispWidth < renderWidth * 2 || Game.dispHeight < renderHeight * 2)
            SPDSettings.put(SPDSettings.KEY_POWER_SAVER, true)

        if (SPDSettings.powerSaver()) {

            val maxZoom = Math.min(Game.dispWidth / renderWidth, Game.dispHeight / renderHeight).toInt()

            renderWidth *= Math.max(2, Math.round(1f + maxZoom * 0.4f)).toFloat()
            renderHeight *= Math.max(2, Math.round(1f + maxZoom * 0.4f)).toFloat()

            if (dispRatio > renderWidth / renderHeight) {
                renderWidth = renderHeight * dispRatio
            } else {
                renderHeight = renderWidth / dispRatio
            }

            val finalW = Math.round(renderWidth)
            val finalH = Math.round(renderHeight)
            if (finalW != Game.width || finalH != Game.height) {

                runOnUiThread { view.holder.setFixedSize(finalW, finalH) }

            }
        } else {
            runOnUiThread { view.holder.setSizeFromLayout() }
        }
    }

    companion object {

        //variable constants for specific older versions of shattered, used for data conversion
        //versions older than v0.5.0b are no longer supported, and data from them is ignored
        val v0_5_0b = 159

        val v0_6_0b = 185

        val v0_6_1b = 209

        val v0_6_2e = 229

        val v0_6_3c = 245

        val v0_6_4 = 251

        @JvmOverloads
        fun switchNoFade(c: Class<out PixelScene>, callback: Game.SceneChangeCallback? = null) {
            PixelScene.noFade = true
            Game.switchScene(c, callback)
        }

        fun updateSystemUI() {

            val fullscreen = Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !Game.instance.isInMultiWindowMode

            if (fullscreen) {
                Game.instance.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            } else {
                Game.instance.window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            }

            if (DeviceCompat.supportsFullScreen()) {
                if (fullscreen && SPDSettings.fullscreen()) {
                    Game.instance.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                } else {
                    Game.instance.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }

        }
    }

}