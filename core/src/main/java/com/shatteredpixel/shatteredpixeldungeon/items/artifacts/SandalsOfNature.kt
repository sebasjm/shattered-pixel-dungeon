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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle

import java.util.ArrayList
import java.util.Collections

class SandalsOfNature : Artifact() {

    protected var mode: WndBag.Mode = WndBag.Mode.SEED

    var seeds = ArrayList<Class<*>>()

    protected var itemSelector: WndBag.Listener = WndBag.Listener { item ->
        if (item != null && item is Plant.Seed) {
            if (seeds.contains(item.javaClass)) {
                GLog.w(Messages.get(SandalsOfNature::class.java, "already_fed"))
            } else {
                seeds.add(item.javaClass)

                val hero = Dungeon.hero
                hero!!.sprite!!.operate(hero.pos)
                Sample.INSTANCE.play(Assets.SND_PLANT)
                hero.busy()
                hero.spend(2f)
                if (seeds.size >= 3 + level() * 3) {
                    seeds.clear()
                    upgrade()
                    if (level() >= 1 && level() <= 3) {
                        GLog.p(Messages.get(SandalsOfNature::class.java, "levelup"))
                    }

                } else {
                    GLog.i(Messages.get(SandalsOfNature::class.java, "absorb_seed"))
                }
                item.detach(hero.belongings.backpack)
            }
        }
    }

    init {
        image = ItemSpriteSheet.ARTIFACT_SANDALS

        levelCap = 3

        charge = 0

        defaultAction = AC_ROOT
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && level() < 3 && !cursed)
            actions.add(AC_FEED)
        if (isEquipped(hero) && charge > 0)
            actions.add(AC_ROOT)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {
        super.execute(hero, action)

        if (action == AC_FEED) {

            GameScene.selectItem(itemSelector, mode, Messages.get(this, "prompt"))

        } else if (action == AC_ROOT && level() > 0) {

            if (!isEquipped(hero))
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
            else if (charge == 0)
                GLog.i(Messages.get(this, "no_charge"))
            else {
                Buff.prolong<Roots>(hero, Roots::class.java, 5f)
                Buff.affect<Earthroot.Armor>(hero, Earthroot.Armor::class.java)!!.level(charge)
                CellEmitter.bottom(hero.pos).start(EarthParticle.FACTORY, 0.05f, 8)
                Camera.main.shake(1f, 0.4f)
                charge = 0
                updateQuickslot()
            }
        }
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return Naturalism()
    }

    override fun desc(): String {
        var desc = Messages.get(this, "desc_" + (level() + 1))

        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n"

            if (!cursed)
                desc += Messages.get(this, "desc_hint")
            else
                desc += Messages.get(this, "desc_cursed")

            if (level() > 0)
                desc += "\n\n" + Messages.get(this, "desc_ability")
        }

        if (!seeds.isEmpty()) {
            desc += "\n\n" + Messages.get(this, "desc_seeds", seeds.size)
        }

        return desc
    }

    override fun upgrade(): Item {
        if (level() < 0)
            image = ItemSpriteSheet.ARTIFACT_SANDALS
        else if (level() == 0)
            image = ItemSpriteSheet.ARTIFACT_SHOES
        else if (level() == 1)
            image = ItemSpriteSheet.ARTIFACT_BOOTS
        else if (level() >= 2) image = ItemSpriteSheet.ARTIFACT_GREAVES
        name = Messages.get(this, "name_" + (level() + 1))
        return super.upgrade()
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(SEEDS, seeds.toTypedArray<Class<*>>())
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        if (level() > 0) name = Messages.get(this, "name_" + level())
        if (bundle.contains(SEEDS))
            Collections.addAll<Class>(seeds, *bundle.getClassArray(SEEDS)!!)
        if (level() == 1)
            image = ItemSpriteSheet.ARTIFACT_SHOES
        else if (level() == 2)
            image = ItemSpriteSheet.ARTIFACT_BOOTS
        else if (level() >= 3) image = ItemSpriteSheet.ARTIFACT_GREAVES
    }

    inner class Naturalism : Artifact.ArtifactBuff() {
        fun charge() {
            if (level() > 0 && charge < target.HT) {
                //gain 1+(1*level)% of the difference between current charge and max HP.
                charge += Math.round((target.HT - charge) * (.01 + level() * 0.01)).toInt()
                updateQuickslot()
            }
        }
    }

    companion object {

        val AC_FEED = "FEED"
        val AC_ROOT = "ROOT"


        private val SEEDS = "seeds"
    }

}
