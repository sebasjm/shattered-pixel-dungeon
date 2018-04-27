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

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes.Landmark
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.audio.Sample

class WaterOfAwareness : WellWater() {

    override fun affectHero(hero: Hero?): Boolean {

        Sample.INSTANCE.play(Assets.SND_DRINK)
        emitter!!.parent!!.add(Identification(hero!!.sprite!!.center()))

        hero.belongings.observe()

        for (i in 0 until Dungeon.level!!.length()) {

            val terr = Dungeon.level!!.map!![i]
            if (Terrain.flags[terr] and Terrain.SECRET != 0) {

                Dungeon.level!!.discover(i)

                if (Dungeon.level!!.heroFOV[i]) {
                    GameScene.discoverTile(i, terr)
                }
            }
        }

        Buff.affect<Awareness>(hero, Awareness::class.java, Awareness.DURATION)
        Dungeon.observe()

        Dungeon.hero!!.interrupt()

        GLog.p(Messages.get(this.javaClass, "procced"))

        return true
    }

    override fun affectItem(item: Item): Item? {
        if (item.isIdentified) {
            return null
        } else {
            item.identify()
            Badges.validateItemLevelAquired(item)

            emitter!!.parent!!.add(Identification(DungeonTilemap.tileCenterToWorld(pos)))

            return item
        }
    }

    override fun record(): Landmark {
        return Landmark.WELL_OF_AWARENESS
    }

    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.QUESTION), 0.3f)
    }

    override fun tileDesc(): String? {
        return Messages.get(this.javaClass, "desc")
    }
}
