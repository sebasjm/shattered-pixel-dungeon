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
import com.shatteredpixel.shatteredpixeldungeon.Badges
import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.Statistics
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle

import java.util.ArrayList

class HornOfPlenty : Artifact() {

    private var storedFoodEnergy = 0

    protected var mode: WndBag.Mode = WndBag.Mode.FOOD


    init {
        image = ItemSpriteSheet.ARTIFACT_HORN1

        levelCap = 10

        charge = 0
        partialCharge = 0f
        chargeCap = 10 + level()

        defaultAction = AC_EAT
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isEquipped(hero) && charge > 0)
            actions.add(AC_EAT)
        if (isEquipped(hero) && level() < levelCap && !cursed)
            actions.add(AC_STORE)
        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_EAT) {

            if (!isEquipped(hero))
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
            else if (charge == 0)
                GLog.i(Messages.get(this.javaClass, "no_food"))
            else {
                //consume as many
                var chargesToUse = Math.max(1, hero.buff<Hunger>(Hunger::class.java)!!.hunger() / (Hunger.STARVING / 10).toInt())
                if (chargesToUse > charge) chargesToUse = charge
                hero.buff<Hunger>(Hunger::class.java)!!.satisfy(Hunger.STARVING / 10 * chargesToUse)

                //if you get at least 80 food energy from the horn
                when (hero.heroClass) {
                    HeroClass.WARRIOR -> if (hero.HP < hero.HT) {
                        hero.HP = Math.min(hero.HP + 5, hero.HT)
                        hero.sprite!!.emitter().burst(Speck.factory(Speck.HEALING), 1)
                    }
                    HeroClass.MAGE -> {
                        //1 charge
                        Buff.affect<Recharging>(hero, Recharging::class.java, 4f)
                        ScrollOfRecharging.charge(hero)
                    }
                    HeroClass.ROGUE, HeroClass.HUNTRESS -> {
                    }
                }

                Statistics.foodEaten++

                charge -= chargesToUse

                hero.sprite!!.operate(hero.pos)
                hero.busy()
                SpellSprite.show(hero, SpellSprite.FOOD)
                Sample.INSTANCE.play(Assets.SND_EAT)
                GLog.i(Messages.get(this.javaClass, "eat"))

                hero.spend(Food.TIME_TO_EAT)

                Badges.validateFoodEaten()

                if (charge >= 15)
                    image = ItemSpriteSheet.ARTIFACT_HORN4
                else if (charge >= 10)
                    image = ItemSpriteSheet.ARTIFACT_HORN3
                else if (charge >= 5)
                    image = ItemSpriteSheet.ARTIFACT_HORN2
                else
                    image = ItemSpriteSheet.ARTIFACT_HORN1

                updateQuickslot()
            }

        } else if (action == AC_STORE) {

            GameScene.selectItem(itemSelector, mode, Messages.get(this.javaClass, "prompt"))

        }
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return hornRecharge()
    }

    override fun desc(): String {
        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!)) {
            if (!cursed) {
                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this.javaClass, "desc_hint")
            } else {
                desc += "\n\n" + Messages.get(this.javaClass, "desc_cursed")
            }
        }

        return desc
    }

    override fun level(value: Int) {
        super.level(value)
        chargeCap = 10 + level()
    }

    override fun upgrade(): Item {
        super.upgrade()
        chargeCap = 10 + level()
        return this
    }

    fun gainFoodValue(food: Food) {
        if (level() >= 10) return

        storedFoodEnergy += food.energy.toInt()
        if (storedFoodEnergy >= Hunger.HUNGRY) {
            var upgrades = storedFoodEnergy / Hunger.HUNGRY.toInt()
            upgrades = Math.min(upgrades, 10 - level())
            upgrade(upgrades)
            storedFoodEnergy -= (upgrades * Hunger.HUNGRY).toInt()
            if (level() == 10) {
                storedFoodEnergy = 0
                GLog.p(Messages.get(this.javaClass, "maxlevel"))
            } else {
                GLog.p(Messages.get(this.javaClass, "levelup"))
            }
        } else {
            GLog.i(Messages.get(this.javaClass, "feed"))
        }
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STORED, storedFoodEnergy)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)

        if (bundle.contains(STORED)) {
            storedFoodEnergy = bundle.getInt(STORED)

            //logic for pre-0.6.1 saves
        } else {
            //keep partial levels
            storedFoodEnergy = (level() % 3 * Hunger.HUNGRY / 3).toInt()
            level(level() / 3)
        }

        if (charge >= 15)
            image = ItemSpriteSheet.ARTIFACT_HORN4
        else if (charge >= 10)
            image = ItemSpriteSheet.ARTIFACT_HORN3
        else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2
    }

    inner class hornRecharge : Artifact.ArtifactBuff() {

        fun gainCharge(levelPortion: Float) {
            if (charge < chargeCap) {

                //generates 0.2x max hunger value every hero level, +0.1x max value per horn level
                //to a max of 1.2x max hunger value per hero level
                //This means that a standard ration will be recovered in 6.67 hero levels
                partialCharge += Hunger.STARVING * levelPortion * (0.2f + 0.1f * level())

                //charge is in increments of 1/10 max hunger value.
                while (partialCharge >= Hunger.STARVING / 10) {
                    charge++
                    partialCharge -= Hunger.STARVING / 10

                    if (charge >= 15)
                        image = ItemSpriteSheet.ARTIFACT_HORN4
                    else if (charge >= 10)
                        image = ItemSpriteSheet.ARTIFACT_HORN3
                    else if (charge >= 5)
                        image = ItemSpriteSheet.ARTIFACT_HORN2
                    else
                        image = ItemSpriteSheet.ARTIFACT_HORN1

                    if (charge == chargeCap) {
                        GLog.p(Messages.get(HornOfPlenty::class.java, "full"))
                        partialCharge = 0f
                    }

                    updateQuickslot()
                }
            } else
                partialCharge = 0f
        }

    }

    companion object {

        val AC_EAT = "EAT"
        val AC_STORE = "STORE"

        private val STORED = "stored"

        protected var itemSelector: WndBag.Listener = { item: Item? ->
            if (item != null && item is Food) {
                if (item is Blandfruit && item.potionAttrib == null) {
                    GLog.w(Messages.get(HornOfPlenty::class.java, "reject"))
                } else {
                    val hero = Dungeon.hero!!
                    hero!!.sprite!!.operate(hero.pos)
                    hero.busy()
                    hero.spend(Food.TIME_TO_EAT)

                    (Item.curItem!! as HornOfPlenty).gainFoodValue(item)
                    item.detach(hero.belongings.backpack)
                }

            }
        } as WndBag.Listener
    }
}
