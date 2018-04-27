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

package com.shatteredpixel.shatteredpixeldungeon.windows

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.RenderedText
import com.watabou.noosa.ui.Component

class WndInfoMob(mob: Mob) : WndTitledMessage(MobTitle(mob), mob.description()) {

    private class MobTitle(mob: Mob) : Component() {

        private val image: CharSprite?
        private val name: RenderedText
        private val health: HealthBar
        private val buffs: BuffIndicator

        init {

            name = PixelScene.renderText(Messages.titleCase(mob.name), 9)
            name.hardlight(Window.TITLE_COLOR)
            add(name)

            image = mob.sprite()
            add(image)

            health = HealthBar()
            health.level(mob)
            add(health)

            buffs = BuffIndicator(mob)
            add(buffs)
        }

        override fun layout() {

            image!!.x = 0f
            image.y = Math.max(0f, name.height() + health.height() - image.height)

            name.x = image.width + GAP
            name.y = Math.max(0f, image.height - health.height() - name.height())

            val w = width - image.width - GAP.toFloat()

            health.setRect(image.width + GAP, name.y + name.height(), w, health.height())

            buffs.setPos(
                    name.x + name.width() + GAP.toFloat() - 1,
                    name.y + name.baseLine() - BuffIndicator.SIZE.toFloat() - 2f)

            height = health.bottom()
        }

        companion object {

            private val GAP = 2
        }
    }
}
