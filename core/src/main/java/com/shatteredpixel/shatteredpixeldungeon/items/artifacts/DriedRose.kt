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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor
import com.shatteredpixel.shatteredpixeldungeon.actors.Char
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem
import com.shatteredpixel.shatteredpixeldungeon.items.Item
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang
import com.shatteredpixel.shatteredpixeldungeon.levels.Level
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBlacksmith
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest
import com.watabou.noosa.audio.Sample
import com.watabou.utils.Bundle
import com.watabou.utils.PathFinder
import com.watabou.utils.Random

import java.util.ArrayList

class DriedRose : Artifact() {

    private var talkedTo = false
    private var firstSummon = false

    private var ghost: GhostHero? = null
    private var ghostID = 0

    private var weapon: MeleeWeapon? = null
    private var armor: Armor? = null

    var droppedPetals = 0

    init {
        image = ItemSpriteSheet.ARTIFACT_ROSE1

        levelCap = 10

        charge = 100
        chargeCap = 100

        defaultAction = AC_SUMMON
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (!Ghost.Quest.completed()) {
            actions.remove(EquipableItem.AC_EQUIP)
            return actions
        }
        if (isEquipped(hero) && charge == chargeCap && !cursed) {
            actions.add(AC_SUMMON)
        }
        if (isIdentified && !cursed) {
            actions.add(AC_OUTFIT)
        }

        return actions
    }

    override fun execute(hero: Hero, action: String?) {

        super.execute(hero, action)

        if (action == AC_SUMMON) {

            if (ghost != null)
                GLog.i(Messages.get(this.javaClass, "spawned"))
            else if (!isEquipped(hero))
                GLog.i(Messages.get(Artifact::class.java, "need_to_equip"))
            else if (charge != chargeCap)
                GLog.i(Messages.get(this.javaClass, "no_charge"))
            else if (cursed)
                GLog.i(Messages.get(this.javaClass, "cursed"))
            else {
                val spawnPoints = ArrayList<Int>()
                for (i in PathFinder.NEIGHBOURS8!!.indices) {
                    val p = hero.pos + PathFinder.NEIGHBOURS8!![i]
                    if (Actor.findChar(p) == null && (Dungeon.level!!.passable[p] || Dungeon.level!!.avoid[p])) {
                        spawnPoints.add(p)
                    }
                }

                if (spawnPoints.size > 0) {
                    ghost = GhostHero(this)
                    ghostID = ghost!!.id()
                    ghost!!.pos = Random.element(spawnPoints)!!

                    GameScene.add(ghost!!, 1f)
                    CellEmitter.get(ghost!!.pos).start(ShaftParticle.FACTORY, 0.3f, 4)
                    CellEmitter.get(ghost!!.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)

                    hero.spend(1f)
                    hero.busy()
                    hero.sprite!!.operate(hero.pos)

                    if (!firstSummon) {
                        ghost!!.yell(Messages.get(GhostHero::class.java, "hello", Dungeon.hero!!.givenName()))
                        Sample.INSTANCE.play(Assets.SND_GHOST)
                        firstSummon = true
                    } else
                        ghost!!.saySpawned()

                    charge = 0
                    updateQuickslot()

                } else
                    GLog.i(Messages.get(this.javaClass, "no_space"))
            }

        } else if (action == AC_OUTFIT) {
            GameScene.show(WndGhostHero(this))
        }
    }

    fun ghostStrength(): Int {
        return 13 + level() / 2
    }

    override fun desc(): String {
        if (!Ghost.Quest.completed() && !isIdentified) {
            return Messages.get(this.javaClass, "desc_no_quest")
        }

        var desc = super.desc()

        if (isEquipped(Dungeon.hero!!)) {
            if (!cursed) {

                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this.javaClass, "desc_hint")

            } else
                desc += "\n\n" + Messages.get(this.javaClass, "desc_cursed")
        }

        return desc
    }

    override fun passiveBuff(): Artifact.ArtifactBuff? {
        return roseRecharge()
    }

    override fun upgrade(): Item {
        if (level() >= 9)
            image = ItemSpriteSheet.ARTIFACT_ROSE3
        else if (level() >= 4)
            image = ItemSpriteSheet.ARTIFACT_ROSE2

        //For upgrade transferring via well of transmutation
        droppedPetals = Math.max(level(), droppedPetals)

        if (ghost != null) {
            ghost!!.updateRose()
        }

        return super.upgrade()
    }

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)

        bundle.put(TALKEDTO, talkedTo)
        bundle.put(FIRSTSUMMON, firstSummon)
        bundle.put(GHOSTID, ghostID)
        bundle.put(PETALS, droppedPetals)

        if (weapon != null) bundle.put(WEAPON, weapon)
        if (armor != null) bundle.put(ARMOR, armor)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)

        talkedTo = bundle.getBoolean(TALKEDTO)
        firstSummon = bundle.getBoolean(FIRSTSUMMON)
        ghostID = bundle.getInt(GHOSTID)
        droppedPetals = bundle.getInt(PETALS)

        if (bundle.contains(WEAPON)) weapon = bundle.get(WEAPON) as MeleeWeapon
        if (bundle.contains(ARMOR)) armor = bundle.get(ARMOR) as Armor
    }

    inner class roseRecharge : Artifact.ArtifactBuff() {

        override fun act(): Boolean {

            spend(Actor.TICK)

            if (ghost == null && ghostID != 0) {
                val a = Actor.findById(ghostID)
                if (a != null) {
                    ghost = a as GhostHero
                } else {
                    ghostID = 0
                }
            }

            //rose does not charge while ghost hero is alive
            if (ghost != null) {
                return true
            }

            val lock = target!!.buff<LockedFloor>(LockedFloor::class.java)
            if (charge < chargeCap && !cursed && (lock == null || lock.regenOn())) {
                partialCharge += 1 / 5f //500 turns to a full charge
                if (partialCharge > 1) {
                    charge++
                    partialCharge--
                    if (charge == chargeCap) {
                        partialCharge = 0f
                        GLog.p(Messages.get(DriedRose::class.java, "charged"))
                    }
                }
            } else if (cursed && Random.Int(100) == 0) {

                val spawnPoints = ArrayList<Int>()

                for (i in PathFinder.NEIGHBOURS8!!.indices) {
                    val p = target!!.pos + PathFinder.NEIGHBOURS8!![i]
                    if (Actor.findChar(p) == null && (Dungeon.level!!.passable[p] || Dungeon.level!!.avoid[p])) {
                        spawnPoints.add(p)
                    }
                }

                if (spawnPoints.size > 0) {
                    Wraith.spawnAt(Random.element(spawnPoints)!!)
                    Sample.INSTANCE.play(Assets.SND_CURSED)
                }

            }

            updateQuickslot()

            return true
        }
    }

    class Petal : Item() {

        init {
            stackable = true
            image = ItemSpriteSheet.PETAL
        }

        override fun doPickUp(hero: Hero): Boolean {
            val rose = hero.belongings.getItem<DriedRose>(DriedRose::class.java)

            if (rose == null) {
                GLog.w(Messages.get(this.javaClass, "no_rose"))
                return false
            }
            if (rose.level() >= rose.levelCap) {
                GLog.i(Messages.get(this.javaClass, "no_room"))
                hero.spendAndNext(Item.TIME_TO_PICK_UP)
                return true
            } else {

                rose.upgrade()
                if (rose.level() == rose.levelCap) {
                    GLog.p(Messages.get(this.javaClass, "maxlevel"))
                } else
                    GLog.i(Messages.get(this.javaClass, "levelup"))

                Sample.INSTANCE.play(Assets.SND_DEWDROP)
                hero.spendAndNext(Item.TIME_TO_PICK_UP)
                return true

            }
        }

    }

    class GhostHero : NPC {

        private var rose: DriedRose? = null

        init {
            spriteClass = GhostSprite::class.java

            flying = true

            alignment = Char.Alignment.ALLY

            WANDERING = Wandering()

            state = HUNTING

            //before other mobs
            actPriority = MOB_PRIO + 1

            properties.add(Char.Property.UNDEAD)
        }

        constructor() : super() {}

        constructor(rose: DriedRose) : super() {
            this.rose = rose
            updateRose()
            HP = HT
        }

        fun updateRose() {
            if (rose == null) {
                rose = Dungeon.hero!!.belongings.getItem(DriedRose::class.java)
            }

            defenseSkill = (Dungeon.hero!!.lvl + 4) * 2
            if (rose == null) return
            HT = 20 + 4 * rose!!.level()
        }

        fun saySpawned() {
            if (Messages.lang() != Languages.ENGLISH) return  //don't say anything if not on english
            val i = (Dungeon.depth - 1) / 5
            fieldOfView = BooleanArray(Dungeon.level!!.length())
            Dungeon.level!!.updateFieldOfView(this, fieldOfView!!)
            if (chooseEnemy() == null)
                yell(Random.element(VOICE_AMBIENT[i]))
            else
                yell(Random.element(VOICE_ENEMIES[i][if (Dungeon.bossLevel()) 1 else 0]))
            Sample.INSTANCE.play(Assets.SND_GHOST)
        }

        fun sayAnhk() {
            yell(Random.element(VOICE_BLESSEDANKH))
            Sample.INSTANCE.play(Assets.SND_GHOST)
        }

        fun sayDefeated() {
            if (Messages.lang() != Languages.ENGLISH) return  //don't say anything if not on english
            yell(Random.element(VOICE_DEFEATED[if (Dungeon.bossLevel()) 1 else 0]))
            Sample.INSTANCE.play(Assets.SND_GHOST)
        }

        fun sayHeroKilled() {
            if (Messages.lang() != Languages.ENGLISH) return  //don't say anything if not on english
            yell(Random.element(VOICE_HEROKILLED))
            Sample.INSTANCE.play(Assets.SND_GHOST)
        }

        fun sayBossBeaten() {
            yell(Random.element(VOICE_BOSSBEATEN[if (Dungeon.depth == 25) 1 else 0]))
            Sample.INSTANCE.play(Assets.SND_GHOST)
        }

        override fun act(): Boolean {
            updateRose()
            if (rose == null || !rose!!.isEquipped(Dungeon.hero!!)) {
                damage(1, this)
            }

            if (!isAlive)
                return true
            if (!Dungeon.hero!!.isAlive) {
                sayHeroKilled()
                sprite!!.die()
                destroy()
                return true
            }
            return super.act()
        }

        override fun chooseEnemy(): Char? {
            val enemy = super.chooseEnemy()

            //will never attack something far from the player
            return if (enemy != null && Dungeon.level!!.distance(enemy.pos, Dungeon.hero!!.pos) <= 8) {
                enemy
            } else {
                null
            }
        }

        override fun attackSkill(target: Char?): Int {
            //same accuracy as the hero.
            var acc = Dungeon.hero!!.lvl + 9

            if (rose != null && rose!!.weapon != null) {
                acc *= rose!!.weapon!!.accuracyFactor(this).toInt()
            }

            return acc
        }

        override fun attackDelay(): Float {
            return if (rose != null && rose!!.weapon != null) {
                rose!!.weapon!!.speedFactor(this)
            } else {
                super.attackDelay()
            }
        }

        override fun canAttack(enemy: Char?): Boolean {
            return if (rose != null && rose!!.weapon != null) {
                Dungeon.level!!.distance(pos, enemy!!.pos) <= rose!!.weapon!!.reachFactor(this)
            } else {
                super.canAttack(enemy)
            }
        }

        override fun damageRoll(): Int {
            var dmg = 0
            if (rose != null && rose!!.weapon != null) {
                dmg += rose!!.weapon!!.damageRoll(this)
            } else {
                dmg += Random.NormalIntRange(0, 5)
            }

            return dmg
        }

        override fun attackProc(enemy: Char, damage: Int): Int {
            var damage = damage
            damage = super.attackProc(enemy, damage)
            if (rose != null && rose!!.weapon != null) {
                damage = rose!!.weapon!!.proc(this, enemy, damage)
            }
            return damage
        }

        override fun defenseProc(enemy: Char, damage: Int): Int {
            return if (rose != null && rose!!.armor != null) {
                rose!!.armor!!.proc(enemy, this, damage)
            } else {
                super.defenseProc(enemy, damage)
            }
        }

        override fun damage(dmg: Int, src: Any) {
            var dmg = dmg
            //TODO improve this when I have proper damage source logic
            if (rose != null && rose!!.armor != null && rose!!.armor!!.hasGlyph(AntiMagic::class.java)
                    && RingOfElements.RESISTS.contains(src.javaClass)) {
                dmg -= Random.NormalIntRange(rose!!.armor!!.DRMin(), rose!!.armor!!.DRMax()) / 3
            }

            super.damage(dmg, src)
        }

        override fun speed(): Float {
            var speed = super.speed()

            if (rose != null && rose!!.armor != null) {
                if (rose!!.armor!!.hasGlyph(Swiftness::class.java)) {
                    speed *= 1.1f + 0.01f * rose!!.armor!!.level()
                } else if (rose!!.armor!!.hasGlyph(Flow::class.java) && Dungeon.level!!.water[pos]) {
                    speed *= 1.5f + 0.05f * rose!!.armor!!.level()
                }
            }

            return speed
        }

        override fun defenseSkill(enemy: Char?): Int {
            var defense = super.defenseSkill(enemy)

            if (defense != 0 && rose != null && rose!!.armor != null && rose!!.armor!!.hasGlyph(Swiftness::class.java)) {
                defense += (5 + rose!!.armor!!.level() * 1.5f).toInt()
            }

            return defense
        }

        override fun stealth(): Int {
            var stealth = super.stealth()

            if (rose != null && rose!!.armor != null && rose!!.armor!!.hasGlyph(Obfuscation::class.java)) {
                stealth += 1 + rose!!.armor!!.level() / 3
            }

            return stealth
        }

        override fun drRoll(): Int {
            var block = 0
            if (rose != null && rose!!.armor != null) {
                block += Random.NormalIntRange(rose!!.armor!!.DRMin(), rose!!.armor!!.DRMax())
            }
            if (rose != null && rose!!.weapon != null) {
                block += Random.NormalIntRange(0, rose!!.weapon!!.defenseFactor(this))
            }
            return block
        }

        override fun interact(): Boolean {
            updateRose()
            if (rose != null && !rose!!.talkedTo) {
                rose!!.talkedTo = true
                GameScene.show(WndQuest(this, Messages.get(this.javaClass, "introduce")))
                return false
            } else if (Dungeon.level!!.passable[pos] || Dungeon.hero!!.flying) {
                val curPos = pos

                moveSprite(pos, Dungeon.hero!!.pos)
                move(Dungeon.hero!!.pos)

                Dungeon.hero!!.sprite!!.move(Dungeon.hero!!.pos, curPos)
                Dungeon.hero!!.move(curPos)

                Dungeon.hero!!.spend(1 / Dungeon.hero!!.speed())
                Dungeon.hero!!.busy()
                return true
            } else {
                return false
            }
        }

        override fun die(cause: Any?) {
            sayDefeated()
            super.die(cause)
        }

        override fun destroy() {
            updateRose()
            if (rose != null) {
                rose!!.ghost = null
                rose!!.ghostID = -1
            }
            super.destroy()
        }

        init {
            immunities.add(ToxicGas::class.java)
            immunities.add(CorrosiveGas::class.java)
            immunities.add(Burning::class.java)
            immunities.add(ScrollOfPsionicBlast::class.java)
            immunities.add(Corruption::class.java)
        }

        private inner class Wandering : Mob.Wandering() {

            override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
                if (enemyInFOV) {

                    enemySeen = true

                    notice()
                    alerted = true
                    state = HUNTING
                    target = enemy!!.pos

                } else {

                    enemySeen = false

                    val oldPos = pos
                    //always move towards the hero when wandering
                    if (getCloser(target = Dungeon.hero!!.pos)) {
                        //moves 2 tiles at a time when returning to the hero from a distance
                        if (!Dungeon.level!!.adjacent(Dungeon.hero!!.pos, pos)) {
                            getCloser(target = Dungeon.hero!!.pos)
                        }
                        spend(1 / speed())
                        return moveSprite(oldPos, pos)
                    } else {
                        spend(Actor.TICK)
                    }

                }
                return true
            }

        }

        companion object {

            //************************************************************************************
            //This is a bunch strings & string arrays, used in all of the sad ghost's voice lines.
            //************************************************************************************

            private val VOICE_INTRODUCE = "My spirit is bound to this rose, it was very precious to me, a " +
                    "gift from my love whom I left on the surface.\n\nI cannot return to him, but thanks to you I have a " +
                    "second chance to complete my journey. When I am able I will respond to your call and fight with you.\n\n" +
                    "hopefully you may succeed where I failed..."

            //1st index - depth type, 2nd index - specific line.
            val VOICE_AMBIENT = arrayOf(arrayOf("These sewers were once safe, some even lived here in the winter...", "I wonder what happened to the guard patrols, did they give up?...", "I had family on the surface, I hope they are safe..."), arrayOf("I've heard stories about this place, nothing good...", "This place was always more of a dungeon than a prison...", "I can't imagine what went on when this place was abandoned..."), arrayOf("No human or dwarf has been here for a very long time...", "Something must have gone very wrong, for the dwarves to abandon a gold mine...", "I feel great evil lurking below..."), arrayOf("The dwarves were industrious, but greedy...", "I hope the surface never ends up like this place...", "So the dwarvern metropolis really has fallen..."), arrayOf("What is this place?...", "So the stories are true, we have to fight a demon god...", "I feel a great evil in this place..."), arrayOf("... I don't like this place... We should leave as soon as possible..."))

            //1st index - depth type, 2nd index - boss or not, 3rd index - specific line.
            val VOICE_ENEMIES = arrayOf(arrayOf(arrayOf("Let's make the sewers safe again...", "If the guards couldn't defeat them, perhaps we can...", "These crabs are extremely annoying..."), arrayOf("Beware Goo!...", "Many of my friends died to this thing, time for vengeance...", "Such an abomination cannot be allowed to live...")), arrayOf(arrayOf("What dark magic happened here?...", "To think the captives of this place are now its guardians...", "They were criminals before, now they are monsters..."), arrayOf("If only he would see reason, he doesn't seem insane...", "He assumes we are hostile, if only he would stop to talk...", "The one prisoner left sane is a deadly assassin. Of course...")), arrayOf(arrayOf("The creatures here are twisted, just like the sewers... ", "more gnolls, I hate gnolls...", "Even the bats are bloodthirsty here..."), arrayOf("Only dwarves would build a mining machine that kills looters...", "That thing is huge...", "How has it survived here for so long?...")), arrayOf(arrayOf("Dwarves aren't supposed to look that pale...", "I don't know what's worse, the dwarves, or their creations...", "They all obey their master without question, even now..."), arrayOf("When people say power corrupts, this is what they mean...", "He's more a Lich than a King now...", "Looks like he's more demon than dwarf now...")), arrayOf(arrayOf("What the heck is that thing?...", "This place is terrifying...", "What were the dwarves thinking, toying with power like this?..."), arrayOf("Oh.... this doesn't look good...", "So that's what a god looks like?...", "This is going to hurt...")), arrayOf(arrayOf("I don't like this place... we should leave as soon as we can..."), arrayOf("Hello source viewer, I'm writing this here as this line should never trigger. Have a nice day!")))

            //1st index - Yog or not, 2nd index - specific line.
            val VOICE_BOSSBEATEN = arrayOf(arrayOf("Yes!", "Victory!"), arrayOf("It's over... we won...", "I can't believe it... We just killed a god..."))

            //1st index - boss or not, 2nd index - specific line.
            val VOICE_DEFEATED = arrayOf(arrayOf("Good luck...", "I will return...", "Tired... for now..."), arrayOf("No... I can't....", "I'm sorry.. good luck..", "Finish it off... without me..."))

            val VOICE_HEROKILLED = arrayOf("nooo...", "no...", "I couldn't help them...")

            val VOICE_BLESSEDANKH = arrayOf("Incredible!...", "Wish I had one of those...", "How did you survive that?...")
        }
    }

    private class WndGhostHero internal constructor(rose: DriedRose) : Window() {

        private val btnWeapon: WndBlacksmith.ItemButton
        private val btnArmor: WndBlacksmith.ItemButton

        init {

            val titlebar = IconTitle()
            titlebar.icon(ItemSprite(rose))
            titlebar.label(Messages.get(this.javaClass, "title"))
            titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
            add(titlebar)

            val message = PixelScene.renderMultiline(Messages.get(this.javaClass, "desc", rose.ghostStrength()), 6)
            message.maxWidth(WIDTH)
            message.setPos(0f, titlebar.bottom() + GAP)
            add(message)

            btnWeapon = object : WndBlacksmith.ItemButton() {
                override fun onClick() {
                    if (rose.weapon != null) {
                        item(WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER))
                        if (!rose.weapon!!.doPickUp(Dungeon.hero!!)) {
                            Dungeon.level!!.drop(rose.weapon, Dungeon.hero!!.pos)
                        }
                        rose.weapon = null
                    } else {
                        GameScene.selectItem({ item: Item? ->
                            if (!(item is MeleeWeapon || item is Boomerang)) {
                                //do nothing, should only happen when window is cancelled
                            } else if (item.unique || item is Boomerang) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_unique"))
                                hide()
                            } else if (!item.isIdentified) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_unidentified"))
                                hide()
                            } else if (item.cursed) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_cursed"))
                                hide()
                            } else if ((item as MeleeWeapon).STRReq() > rose.ghostStrength()) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_strength"))
                                hide()
                            } else {
                                if (item.isEquipped(Dungeon.hero!!)) {
                                    item.doUnequip(Dungeon.hero!!, false, false)
                                } else {
                                    item.detach(Dungeon.hero!!.belongings.backpack)
                                }
                                rose.weapon = item
                                item(rose.weapon)
                            }
                        } as WndBag.Listener, WndBag.Mode.WEAPON, Messages.get(WndGhostHero::class.java, "weapon_prompt"))
                    }
                }
            }
            btnWeapon.setRect((WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + GAP, BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
            if (rose.weapon != null) {
                btnWeapon.item(rose.weapon)
            } else {
                btnWeapon.item(WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER))
            }
            add(btnWeapon)

            btnArmor = object : WndBlacksmith.ItemButton() {
                override fun onClick() {
                    if (rose.armor != null) {
                        item(WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER))
                        if (!rose.armor!!.doPickUp(Dungeon.hero!!)) {
                            Dungeon.level!!.drop(rose.armor, Dungeon.hero!!.pos)
                        }
                        rose.armor = null
                    } else {
                        GameScene.selectItem({ item: Item? ->
                            if (item !is Armor) {
                                //do nothing, should only happen when window is cancelled
                            } else if (item.unique || item.checkSeal() != null) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_unique"))
                                hide()
                            } else if (!item.isIdentified) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_unidentified"))
                                hide()
                            } else if (item.cursed) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_cursed"))
                                hide()
                            } else if (item.STRReq() > rose.ghostStrength()) {
                                GLog.w(Messages.get(WndGhostHero::class.java, "cant_strength"))
                                hide()
                            } else {
                                if (item.isEquipped(Dungeon.hero!!)) {
                                    item.doUnequip(Dungeon.hero!!, false, false)
                                } else {
                                    item.detach(Dungeon.hero!!.belongings.backpack)
                                }
                                rose.armor = item
                                item(rose.armor)
                            }
                        } as WndBag.Listener, WndBag.Mode.ARMOR, Messages.get(WndGhostHero::class.java, "armor_prompt"))
                    }
                }
            }
            btnArmor.setRect(btnWeapon.right() + BTN_GAP, btnWeapon.top(), BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
            if (rose.armor != null) {
                btnArmor.item(rose.armor)
            } else {
                btnArmor.item(WndBag.Placeholder(ItemSpriteSheet.ARMOR_HOLDER))
            }
            add(btnArmor)

            resize(WIDTH, (btnArmor.bottom() + GAP).toInt())
        }

        companion object {

            private val BTN_SIZE = 32
            private val GAP = 2f
            private val BTN_GAP = 12f
            private val WIDTH = 116
        }

    }

    companion object {

        val AC_SUMMON = "SUMMON"
        val AC_OUTFIT = "OUTFIT"

        private val TALKEDTO = "talkedto"
        private val FIRSTSUMMON = "firstsummon"
        private val GHOSTID = "ghostID"
        private val PETALS = "petals"

        private val WEAPON = "weapon"
        private val ARMOR = "armor"

        // *** static methods for transferring a ghost hero between floors ***

        private var heldGhost: GhostHero? = null

        fun holdGhostHero(level: Level) {
            for (mob in level.mobs.toTypedArray<Mob>()) {
                if (mob is DriedRose.GhostHero) {
                    level.mobs.remove(mob)
                    heldGhost = mob
                    break
                }
            }
        }

        fun restoreGhostHero(level: Level, pos: Int) {
            if (heldGhost != null) {
                level.mobs.add(heldGhost!!)

                var ghostPos: Int
                do {
                    ghostPos = pos + PathFinder.NEIGHBOURS8!![Random.Int(8)]
                } while (Dungeon.level!!.solid[ghostPos] || level.findMob(ghostPos) != null)

                heldGhost!!.pos = ghostPos
                heldGhost = null
            }
        }

        fun clearHeldGhostHero() {
            heldGhost = null
        }
    }
}
