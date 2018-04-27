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

package com.shatteredpixel.shatteredpixeldungeon.effects

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.utils.Random

open class EmoIcon(protected var owner: CharSprite) : Image() {

    protected var maxSize = 2f
    protected var timeScale = 1f

    protected var growing = true

    init {
        GameScene.add(this)
    }

    override fun update() {
        super.update()

        if (visible) {
            if (growing) {
                scale.set(Math.min(scale.x + Game.elapsed * timeScale, maxSize))
                if (scale.x >= maxSize) {
                    growing = false
                }
            } else {
                scale.set(Math.max(scale.x - Game.elapsed * timeScale, 1f))
                if (scale.x <= 1) {
                    growing = true
                }
            }

            x = owner.x + owner.width - width / 2
            y = owner.y - height
        }
    }

    class Sleep(owner: CharSprite) : EmoIcon(owner) {

        init {

            copy(Icons.get(Icons.SLEEP))

            maxSize = 1.2f
            timeScale = 0.5f

            origin.set(width / 2, height / 2)
            scale.set(Random.Float(1f, maxSize))

            x = owner.x + owner.width - width / 2
            y = owner.y - height
        }
    }

    class Alert(owner: CharSprite) : EmoIcon(owner) {

        init {

            copy(Icons.get(Icons.ALERT))

            maxSize = 1.3f
            timeScale = 2f

            origin.set(2.5f, height - 2.5f)
            scale.set(Random.Float(1f, maxSize))

            x = owner.x + owner.width - width / 2
            y = owner.y - height
        }
    }

    class Lost(owner: CharSprite) : EmoIcon(owner) {

        init {

            copy(Icons.get(Icons.LOST))

            maxSize = 1.25f
            timeScale = 1f

            origin.set(2.5f, height - 2.5f)
            scale.set(Random.Float(1f, maxSize))

            x = owner.x + owner.width - width / 2
            y = owner.y - height
        }

    }

}
