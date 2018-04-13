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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.watabou.noosa.Image

enum class Icons {

    SKULL,
    BUSY,
    COMPASS,
    INFO,
    PREFS,
    WARNING,
    TARGET,
    MASTERY,
    WATA,
    SHPX,
    WARRIOR,
    MAGE,
    ROGUE,
    HUNTRESS,
    CLOSE,
    DEPTH,
    SLEEP,
    ALERT,
    LOST,
    BACKPACK,
    SEED_POUCH,
    SCROLL_HOLDER,
    POTION_BANDOLIER,
    WAND_HOLSTER,
    CHECKED,
    UNCHECKED,
    EXIT,
    NOTES,
    LANGS,
    CHALLENGE_OFF,
    CHALLENGE_ON,
    RESUME;

    fun get(): Image {
        return get(this)
    }

    companion object {

        operator fun get(type: Icons): Image {
            val icon = Image(Assets.ICONS)
            when (type) {
                SKULL -> icon.frame(icon.texture!!.uvRect(0f, 0f, 8f, 8f))
                BUSY -> icon.frame(icon.texture!!.uvRect(8f, 0f, 16f, 8f))
                COMPASS -> icon.frame(icon.texture!!.uvRect(0f, 8f, 7f, 13f))
                INFO -> icon.frame(icon.texture!!.uvRect(16f, 0f, 30f, 14f))
                PREFS -> icon.frame(icon.texture!!.uvRect(30f, 0f, 46f, 16f))
                WARNING -> icon.frame(icon.texture!!.uvRect(46f, 0f, 58f, 12f))
                TARGET -> icon.frame(icon.texture!!.uvRect(0f, 13f, 16f, 29f))
                MASTERY -> icon.frame(icon.texture!!.uvRect(16f, 14f, 30f, 28f))
                WATA -> icon.frame(icon.texture!!.uvRect(30f, 16f, 45f, 26f))
                SHPX -> icon.frame(icon.texture!!.uvRect(64f, 44f, 80f, 60f))
                WARRIOR -> icon.frame(icon.texture!!.uvRect(0f, 29f, 16f, 45f))
                MAGE -> icon.frame(icon.texture!!.uvRect(16f, 29f, 32f, 45f))
                ROGUE -> icon.frame(icon.texture!!.uvRect(32f, 29f, 48f, 45f))
                HUNTRESS -> icon.frame(icon.texture!!.uvRect(48f, 29f, 64f, 45f))
                CLOSE -> icon.frame(icon.texture!!.uvRect(0f, 45f, 13f, 58f))
                DEPTH -> icon.frame(icon.texture!!.uvRect(38f, 46f, 54f, 62f))
                SLEEP -> icon.frame(icon.texture!!.uvRect(13f, 45f, 22f, 53f))
                ALERT -> icon.frame(icon.texture!!.uvRect(22f, 45f, 30f, 53f))
                LOST -> icon.frame(icon.texture!!.uvRect(30f, 45f, 38f, 53f))
                BACKPACK -> icon.frame(icon.texture!!.uvRect(58f, 0f, 68f, 10f))
                SCROLL_HOLDER -> icon.frame(icon.texture!!.uvRect(68f, 0f, 78f, 10f))
                SEED_POUCH -> icon.frame(icon.texture!!.uvRect(78f, 0f, 88f, 10f))
                WAND_HOLSTER -> icon.frame(icon.texture!!.uvRect(88f, 0f, 98f, 10f))
                POTION_BANDOLIER -> icon.frame(icon.texture!!.uvRect(98f, 0f, 108f, 10f))
                CHECKED -> icon.frame(icon.texture!!.uvRect(54f, 12f, 66f, 24f))
                UNCHECKED -> icon.frame(icon.texture!!.uvRect(66f, 12f, 78f, 24f))
                EXIT -> icon.frame(icon.texture!!.uvRect(108f, 0f, 124f, 16f))
                NOTES -> icon.frame(icon.texture!!.uvRect(79f, 40f, 94f, 56f))
                LANGS -> icon.frame(icon.texture!!.uvRect(95f, 42f, 107f, 51f))
                CHALLENGE_OFF -> icon.frame(icon.texture!!.uvRect(78f, 12f, 92f, 24f))
                CHALLENGE_ON -> icon.frame(icon.texture!!.uvRect(92f, 12f, 108f, 24f))
                RESUME -> icon.frame(icon.texture!!.uvRect(13f, 53f, 24f, 64f))
            }
            return icon
        }

        operator fun get(cl: HeroClass): Image? {
            when (cl) {
                HeroClass.WARRIOR -> return get(WARRIOR)
                HeroClass.MAGE -> return get(MAGE)
                HeroClass.ROGUE -> return get(ROGUE)
                HeroClass.HUNTRESS -> return get(HUNTRESS)
                else -> return null
            }
        }
    }
}
