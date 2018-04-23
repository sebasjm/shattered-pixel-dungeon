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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts

import com.shatteredpixel.shatteredpixeldungeon.Assets
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle

import java.util.ArrayList

class ChaliceOfBlood : Artifact() {

    init {
        image = ItemSpriteSheet.ARTIFACT_CHALICE1

        levelCap = 10
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && level() < levelCap && !cursed)
            actions.add(AC_PRICK)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {
        super.execute(hero, action)

        if (action == AC_PRICK) {

            val damage = 3 * (level() * level())

            if (damage > hero.HP * 0.75) {

                GameScene.show(
                        object : WndOptions(Messages.get(this.javaClass, "name"),
                                Messages.get(this.javaClass, "prick_warn"),
                                Messages.get(this.javaClass, "yes"),
                                Messages.get(this.javaClass, "no")) {
                            override fun onSelect(index: Int) {
                                if (index == 0)
                                    prick(Dungeon.hero!!)
                            }
                        }
                )

            } else {
                prick(hero)
            }
        }
    }

    private fun prick(hero: Hero) {
        var damage = 3 * (level() * level())

        val armor = hero.buff<Earthroot.Armor>(Earthroot.Armor::class.java)
        if (armor != null) {
            damage = armor.absorb(damage)
        }

        damage -= hero.drRoll()

        hero.sprite!!.operate(hero.pos)
        hero.busy()
        hero.spend(3f)
        GLog.w(Messages.get(this.javaClass, "onprick"))
        if (damage <= 0) {
            damage = 1
        } else {
            Sample.INSTANCE.play(Assets.SND_CURSED)
            hero.sprite!!.emitter().burst(ShadowParticle.CURSE, 4 + damage / 10)
        }

        hero.damage(damage, this)

        if (!hero.isAlive) {
            Dungeon.fail(javaClass)
            GLog.n(Messages.get(this.javaClass, "ondeath"))
        } else {
            upgrade()
        }
    }

    override fun upgrade(): Item {
        if (level() >= 6)
            image = ItemSpriteSheet.ARTIFACT_CHALICE3
        else if (level() >= 2)
            image = ItemSpriteSheet.ARTIFACT_CHALICE2
        return super.upgrade()
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (level() >= 7)
            image = ItemSpriteSheet.ARTIFACT_CHALICE3
        else if (level() >= 3) image = ItemSpriteSheet.ARTIFACT_CHALICE2
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return chaliceRegen()
    }

    override fun desc(): String {
        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!)) {
            desc += "\n\n"
            if (cursed)
                desc += Messages.get(this.javaClass, "desc_cursed")
            else if (level() == 0)
                desc += Messages.get(this.javaClass, "desc_1")
            else if (level() < levelCap)
                desc += Messages.get(this.javaClass, "desc_2")
            else
                desc += Messages.get(this.javaClass, "desc_3")
        }

        return desc
    }

    inner class chaliceRegen : Artifact.ArtifactBuff()

    companion object {

        val AC_PRICK = "PRICK"
    }

}
