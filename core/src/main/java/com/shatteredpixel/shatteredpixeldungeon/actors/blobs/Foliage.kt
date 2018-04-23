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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene

class Foliage : Blob() {

    override fun evolve() {

        val map = Dungeon.level!!.map

        var visible = false

        var cell: Int
        for (i in area.left until area.right) {
            for (j in area.top until area.bottom) {
                cell = i + j * Dungeon.level!!.width()
                if (cur!![cell] > 0) {

                    off!![cell] = cur!![cell]
                    volume += off!![cell]

                    if (map!![cell] == Terrain.EMBERS) {
                        map[cell] = Terrain.GRASS
                        GameScene.updateMap(cell)
                    }

                    visible = visible || Dungeon.level!!.heroFOV[cell]

                } else {
                    off!![cell] = 0
                }
            }
        }

        val hero = Dungeon.hero!!
        if (hero!!.isAlive && hero.visibleEnemies() == 0 && cur!![hero.pos] > 0) {
            Buff.affect<Shadows>(hero, Shadows::class.java)!!.prolong()
        }

        if (visible) {
            Notes.add(Notes.Landmark.GARDEN)
        }
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(ShaftParticle.FACTORY, 0.9f, 0)
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }
}
