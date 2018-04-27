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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Generator
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.watabou.noosa.Camera
import com.watabou.utils.Callback
import com.watabou.utils.Random

class Shaman : Mob(), Callback {

    init {
        spriteClass = ShamanSprite::class.java

        HT = 18
        HP = HT
        defenseSkill = 8

        EXP = 6
        maxLvl = 14

        loot = Generator.Category.SCROLL
        lootChance = 0.33f

        properties.add(Char.Property.ELECTRIC)
    }

    override fun damageRoll(): Int {
        return Random.NormalIntRange(2, 8)
    }

    override fun attackSkill(target: Char?): Int {
        return 11
    }

    override fun drRoll(): Int {
        return Random.NormalIntRange(0, 4)
    }

    override fun canAttack(enemy: Char?): Boolean {
        return Ballistica(pos, enemy!!.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos
    }

    override fun doAttack(enemy: Char?): Boolean {

        if (Dungeon.level!!.distance(pos, enemy!!.pos) <= 1) {

            return super.doAttack(enemy)

        } else {

            val visible = fieldOfView!![pos] || fieldOfView!![enemy.pos]
            if (visible) {
                sprite!!.zap(enemy.pos)
            }

            spend(TIME_TO_ZAP)

            if (Char.hit(this, enemy, true)) {
                var dmg = Random.NormalIntRange(3, 10)
                if (Dungeon.level!!.water[enemy.pos] && !enemy.flying) {
                    dmg = (dmg * 1.5f).toInt()
                }
                enemy.damage(dmg, this)

                enemy.sprite!!.centerEmitter().burst(SparkParticle.FACTORY, 3)
                enemy.sprite!!.flash()

                if (enemy === Dungeon.hero!!) {

                    Camera.main!!.shake(2f, 0.3f)

                    if (!enemy.isAlive) {
                        Dungeon.fail(javaClass)
                        GLog.n(Messages.get(this.javaClass, "zap_kill"))
                    }
                }
            } else {
                enemy.sprite!!.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb())
            }

            return !visible
        }
    }

    override fun call() {
        next()
    }

    companion object {

        private val TIME_TO_ZAP = 1f
    }

}
