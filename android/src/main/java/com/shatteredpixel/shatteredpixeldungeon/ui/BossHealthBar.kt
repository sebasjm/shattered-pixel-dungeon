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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle
import com.watabou.noosa.Image
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.ui.Component

class BossHealthBar internal constructor() : Component() {

    private var bar: Image? = null
    private var hp: Image? = null

    private var skull: Image? = null
    private var blood: Emitter? = null

    init {
        active = boss != null
        visible = active
        instance = this
    }

    override fun createChildren() {
        bar = Image(asset, 0, 0, 64, 16)
        add(bar!!)

        width = bar!!.width
        height = bar!!.height

        hp = Image(asset, 15, 19, 47, 4)
        add(hp!!)

        skull = Image(asset, 5, 18, 6, 6)
        add(skull!!)

        blood = Emitter()
        blood!!.pos(skull!!)
        blood!!.pour(BloodParticle.FACTORY, 0.3f)
        blood!!.autoKill = false
        blood!!.on = false
        add(blood!!)
    }

    override fun layout() {
        bar!!.x = x
        bar!!.y = y

        hp!!.x = bar!!.x + 15
        hp!!.y = bar!!.y + 6

        skull!!.x = bar!!.x + 5
        skull!!.y = bar!!.y + 5
    }

    override fun update() {
        super.update()
        if (boss != null) {
            if (!boss!!.isAlive || !Dungeon.level!!.mobs.contains(boss!!)) {
                boss = null
                active = false
                visible = active
            } else {
                hp!!.scale.x = boss!!.HP.toFloat() / boss!!.HT
                if (hp!!.scale.x < 0.25f) bleed(true)

                if (bleeding != blood!!.on) {
                    if (bleeding)
                        skull!!.tint(0xcc0000, 0.6f)
                    else
                        skull!!.resetColor()
                    blood!!.on = bleeding
                }
            }
        }
    }

    companion object {

        private var boss: Mob? = null

        private val asset = Assets.BOSSHP

        private var instance: BossHealthBar? = null
        private var bleeding: Boolean = false

        fun assignBoss(boss: Mob) {
            BossHealthBar.boss = boss
            bleed(false)
            if (instance != null) {
                instance!!.active = true
                instance!!.visible = instance!!.active
            }
        }

        fun bleed(value: Boolean) {
            bleeding = value
        }
    }

}
