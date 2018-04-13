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

import com.shatteredpixel.shatteredpixeldungeon.messages.Languages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.utils.GameSettings

import java.util.Locale

class SPDSettings : GameSettings() {
    companion object {

        //Version info

        val KEY_VERSION = "version"

        fun version(value: Int) {
            GameSettings.put(KEY_VERSION, value)
        }

        fun version(): Int {
            return GameSettings.getInt(KEY_VERSION, 0)
        }

        //Graphics

        val KEY_FULLSCREEN = "fullscreen"
        val KEY_LANDSCAPE = "landscape"
        val KEY_POWER_SAVER = "power_saver"
        val KEY_SCALE = "scale"
        val KEY_ZOOM = "zoom"
        val KEY_BRIGHTNESS = "brightness"
        val KEY_GRID = "visual_grid"

        fun fullscreen(value: Boolean) {
            GameSettings.put(KEY_FULLSCREEN, value)

            ShatteredPixelDungeon.instance!!.runOnUiThread { ShatteredPixelDungeon.updateSystemUI() }
        }

        fun fullscreen(): Boolean {
            return GameSettings.getBoolean(KEY_FULLSCREEN, false)
        }

        fun landscape(value: Boolean) {
            GameSettings.put(KEY_LANDSCAPE, value)
            (ShatteredPixelDungeon.instance as ShatteredPixelDungeon).updateDisplaySize()
        }

        fun landscape(): Boolean {
            return GameSettings.getBoolean(KEY_LANDSCAPE, Game.dispWidth > Game.dispHeight)
        }

        fun powerSaver(value: Boolean) {
            GameSettings.put(KEY_POWER_SAVER, value)
            (ShatteredPixelDungeon.instance as ShatteredPixelDungeon).updateDisplaySize()
        }

        fun powerSaver(): Boolean {
            return GameSettings.getBoolean(KEY_POWER_SAVER, false)
        }

        fun scale(value: Int) {
            GameSettings.put(KEY_SCALE, value)
        }

        fun scale(): Int {
            return GameSettings.getInt(KEY_SCALE, 0)
        }

        fun zoom(value: Int) {
            GameSettings.put(KEY_ZOOM, value)
        }

        fun zoom(): Int {
            return GameSettings.getInt(KEY_ZOOM, 0)
        }

        fun brightness(value: Int) {
            GameSettings.put(KEY_BRIGHTNESS, value)
            GameScene.updateFog()
        }

        fun brightness(): Int {
            return GameSettings.getInt(KEY_BRIGHTNESS, 0, -2, 2)
        }

        fun visualGrid(value: Int) {
            GameSettings.put(KEY_GRID, value)
            GameScene.updateMap()
        }

        fun visualGrid(): Int {
            return GameSettings.getInt(KEY_GRID, 0, -1, 3)
        }

        //Interface

        val KEY_QUICKSLOTS = "quickslots"
        val KEY_FLIPTOOLBAR = "flipped_ui"
        val KEY_FLIPTAGS = "flip_tags"
        val KEY_BARMODE = "toolbar_mode"

        fun quickSlots(value: Int) {
            GameSettings.put(KEY_QUICKSLOTS, value)
        }

        fun quickSlots(): Int {
            return GameSettings.getInt(KEY_QUICKSLOTS, 4, 0, 4)
        }

        fun flipToolbar(value: Boolean) {
            GameSettings.put(KEY_FLIPTOOLBAR, value)
        }

        fun flipToolbar(): Boolean {
            return GameSettings.getBoolean(KEY_FLIPTOOLBAR, false)
        }

        fun flipTags(value: Boolean) {
            GameSettings.put(KEY_FLIPTAGS, value)
        }

        fun flipTags(): Boolean {
            return GameSettings.getBoolean(KEY_FLIPTAGS, false)
        }

        fun toolbarMode(value: String) {
            GameSettings.put(KEY_BARMODE, value)
        }

        fun toolbarMode(): String? {
            return GameSettings.getString(KEY_BARMODE, if (!SPDSettings.landscape()) "SPLIT" else "GROUP")
        }

        //Game State

        val KEY_LAST_CLASS = "last_class"
        val KEY_CHALLENGES = "challenges"
        val KEY_INTRO = "intro"

        fun intro(value: Boolean) {
            GameSettings.put(KEY_INTRO, value)
        }

        fun intro(): Boolean {
            return GameSettings.getBoolean(KEY_INTRO, true)
        }

        fun lastClass(value: Int) {
            GameSettings.put(KEY_LAST_CLASS, value)
        }

        fun lastClass(): Int {
            return GameSettings.getInt(KEY_LAST_CLASS, 0, 0, 3)
        }

        fun challenges(value: Int) {
            GameSettings.put(KEY_CHALLENGES, value)
        }

        fun challenges(): Int {
            return GameSettings.getInt(KEY_CHALLENGES, 0, 0, Challenges.MAX_VALUE)
        }

        //Audio

        val KEY_MUSIC = "music"
        val KEY_MUSIC_VOL = "music_vol"
        val KEY_SOUND_FX = "soundfx"
        val KEY_SFX_VOL = "sfx_vol"

        fun music(value: Boolean) {
            Music.INSTANCE.enable(value)
            GameSettings.put(KEY_MUSIC, value)
        }

        fun music(): Boolean {
            return GameSettings.getBoolean(KEY_MUSIC, true)
        }

        fun musicVol(value: Int) {
            Music.INSTANCE.volume(value / 10f)
            GameSettings.put(KEY_MUSIC_VOL, value)
        }

        fun musicVol(): Int {
            return GameSettings.getInt(KEY_MUSIC_VOL, 10, 0, 10)
        }

        fun soundFx(value: Boolean) {
            Sample.INSTANCE.enable(value)
            GameSettings.put(KEY_SOUND_FX, value)
        }

        fun soundFx(): Boolean {
            return GameSettings.getBoolean(KEY_SOUND_FX, true)
        }

        fun SFXVol(value: Int) {
            Sample.INSTANCE.volume(value / 10f)
            GameSettings.put(KEY_SFX_VOL, value)
        }

        fun SFXVol(): Int {
            return GameSettings.getInt(KEY_SFX_VOL, 10, 0, 10)
        }

        //Languages and Font

        val KEY_LANG = "language"
        val KEY_SYSTEMFONT = "system_font"

        fun language(lang: Languages) {
            GameSettings.put(KEY_LANG, lang.code())
        }

        fun language(): Languages {
            val code = GameSettings.getString(KEY_LANG, null)
            return if (code == null) {
                Languages.matchLocale(Locale.getDefault())
            } else {
                Languages.matchCode(code)
            }
        }

        fun systemFont(value: Boolean) {
            GameSettings.put(KEY_SYSTEMFONT, value)
            if (!value) {
                RenderedText.setFont("pixelfont.ttf")
            } else {
                RenderedText.setFont(null)
            }
        }

        fun systemFont(): Boolean {
            return GameSettings.getBoolean(KEY_SYSTEMFONT,
                    language() == Languages.KOREAN || language() == Languages.CHINESE)
        }
    }

}
