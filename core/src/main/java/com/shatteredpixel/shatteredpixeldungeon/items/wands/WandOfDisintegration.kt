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

package com.shatteredpixel.shatteredpixeldungeon.items.wands

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap
import com.watabou.utils.Callback
import com.watabou.utils.Random

import java.util.ArrayList

class WandOfDisintegration : DamageWand() {

    init {
        image = ItemSpriteSheet.WAND_DISINTEGRATION

        collisionProperties = Ballistica.WONT_STOP
    }


    override fun min(lvl: Int): Int {
        return 2 + lvl
    }

    override fun max(lvl: Int): Int {
        return 8 + 4 * lvl
    }

    override fun onZap(beam: Ballistica) {

        var terrainAffected = false

        val level = level()

        val maxDistance = Math.min(distance(), beam.dist!!)

        val chars = ArrayList<Char>()

        var terrainPassed = 2
        var terrainBonus = 0
        for (c in beam.subPath(1, maxDistance)) {

            val ch: Char?
            if ((ch = Actor.findChar(c)) != null) {

                //we don't want to count passed terrain after the last enemy hit. That would be a lot of bonus levels.
                //terrainPassed starts at 2, equivalent of rounding up when /3 for integer arithmetic.
                terrainBonus += terrainPassed / 3
                terrainPassed = terrainPassed % 3

                chars.add(ch)
            }

            if (Dungeon.level!!.flamable[c]) {

                Dungeon.level!!.destroy(c)
                GameScene.updateMap(c)
                terrainAffected = true

            }

            if (Dungeon.level!!.solid[c])
                terrainPassed++

            CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2))
        }

        if (terrainAffected) {
            Dungeon.observe()
        }

        val lvl = level + (chars.size - 1) + terrainBonus
        for (ch in chars) {
            processSoulMark(ch, chargesPerCast())
            ch.damage(damageRoll(lvl), this)
            ch.sprite!!.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2))
            ch.sprite!!.flash()
        }
    }

    override fun onHit(staff: MagesStaff, attacker: Char, defender: Char, damage: Int) {
        //no direct effect, see magesStaff.reachfactor
    }

    private fun distance(): Int {
        return level() * 2 + 4
    }

    override fun fx(beam: Ballistica, callback: Callback) {

        val cell = beam.path[Math.min(beam.dist!!, distance())]
        Item.curUser.sprite!!.parent!!.add(Beam.DeathRay(Item.curUser.sprite!!.center(), DungeonTilemap.raisedTileCenterToWorld(cell)))
        callback.call()
    }

    override fun staffFx(particle: MagesStaff.StaffParticle) {
        particle.color(0x220022)
        particle.am = 0.6f
        particle.setLifespan(1f)
        particle.acc.set(10f, -10f)
        particle.setSize(0.5f, 3f)
        particle.shuffleXY(1f)
    }

}
