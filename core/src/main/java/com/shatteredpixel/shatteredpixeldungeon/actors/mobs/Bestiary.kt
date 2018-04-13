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

import com.watabou.utils.Random

import java.util.ArrayList
import java.util.Arrays

object Bestiary {

    fun getMobRotation(depth: Int): ArrayList<Class<out Mob>> {
        val mobs = standardMobRotation(depth)
        addRareMobs(depth, mobs)
        swapMobAlts(mobs)
        Random.shuffle(mobs)
        return mobs
    }

    //returns a rotation of standard mobs, unshuffled.
    private fun standardMobRotation(depth: Int): ArrayList<Class<out Mob>> {
        when (depth) {

        // Sewers
            1 ->
                //10x rat
                return ArrayList(Arrays.asList<Class<Rat>>(
                        Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java,
                        Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java))
            2 ->
                //3x rat, 3x gnoll
                return ArrayList(Arrays.asList(Rat::class.java, Rat::class.java, Rat::class.java,
                        Gnoll::class.java, Gnoll::class.java, Gnoll::class.java))
            3 ->
                //2x rat, 4x gnoll, 1x crab, 1x swarm
                return ArrayList(Arrays.asList(Rat::class.java, Rat::class.java,
                        Gnoll::class.java, Gnoll::class.java, Gnoll::class.java, Gnoll::class.java,
                        Crab::class.java, Swarm::class.java))
            4 ->
                //1x rat, 2x gnoll, 3x crab, 1x swarm
                return ArrayList(Arrays.asList(Rat::class.java,
                        Gnoll::class.java, Gnoll::class.java,
                        Crab::class.java, Crab::class.java, Crab::class.java,
                        Swarm::class.java))

        // Prison
            6 ->
                //3x skeleton, 1x thief, 1x swarm
                return ArrayList(Arrays.asList(Skeleton::class.java, Skeleton::class.java, Skeleton::class.java,
                        Thief::class.java,
                        Swarm::class.java))
            7 ->
                //3x skeleton, 1x thief, 1x shaman, 1x guard
                return ArrayList(Arrays.asList(Skeleton::class.java, Skeleton::class.java, Skeleton::class.java,
                        Thief::class.java,
                        Shaman::class.java,
                        Guard::class.java))
            8 ->
                //3x skeleton, 1x thief, 2x shaman, 2x guard
                return ArrayList(Arrays.asList(Skeleton::class.java, Skeleton::class.java, Skeleton::class.java,
                        Thief::class.java,
                        Shaman::class.java, Shaman::class.java,
                        Guard::class.java, Guard::class.java))
            9 ->
                //3x skeleton, 1x thief, 2x shaman, 3x guard
                return ArrayList(Arrays.asList(Skeleton::class.java, Skeleton::class.java, Skeleton::class.java,
                        Thief::class.java,
                        Shaman::class.java, Shaman::class.java,
                        Guard::class.java, Guard::class.java, Guard::class.java))

        // Caves
            11 ->
                //5x bat, 1x brute
                return ArrayList(Arrays.asList(
                        Bat::class.java, Bat::class.java, Bat::class.java, Bat::class.java, Bat::class.java,
                        Brute::class.java))
            12 ->
                //5x bat, 5x brute, 1x spinner
                return ArrayList(Arrays.asList(
                        Bat::class.java, Bat::class.java, Bat::class.java, Bat::class.java, Bat::class.java,
                        Brute::class.java, Brute::class.java, Brute::class.java, Brute::class.java, Brute::class.java,
                        Spinner::class.java))
            13 ->
                //1x bat, 3x brute, 1x shaman, 1x spinner
                return ArrayList(Arrays.asList(
                        Bat::class.java,
                        Brute::class.java, Brute::class.java, Brute::class.java,
                        Shaman::class.java,
                        Spinner::class.java))
            14 ->
                //1x bat, 3x brute, 1x shaman, 4x spinner
                return ArrayList(Arrays.asList(
                        Bat::class.java,
                        Brute::class.java, Brute::class.java, Brute::class.java,
                        Shaman::class.java,
                        Spinner::class.java, Spinner::class.java, Spinner::class.java, Spinner::class.java))

        // City
            16 ->
                //5x elemental, 5x warlock, 1x monk
                return ArrayList(Arrays.asList(
                        Elemental::class.java, Elemental::class.java, Elemental::class.java, Elemental::class.java, Elemental::class.java,
                        Warlock::class.java, Warlock::class.java, Warlock::class.java, Warlock::class.java, Warlock::class.java,
                        Monk::class.java))
            17 ->
                //2x elemental, 2x warlock, 2x monk
                return ArrayList(Arrays.asList(
                        Elemental::class.java, Elemental::class.java,
                        Warlock::class.java, Warlock::class.java,
                        Monk::class.java, Monk::class.java))
            18 ->
                //1x elemental, 1x warlock, 2x monk, 1x golem
                return ArrayList(Arrays.asList(
                        Elemental::class.java,
                        Warlock::class.java,
                        Monk::class.java, Monk::class.java,
                        Golem::class.java))
            19 ->
                //1x elemental, 1x warlock, 2x monk, 3x golem
                return ArrayList(Arrays.asList(
                        Elemental::class.java,
                        Warlock::class.java,
                        Monk::class.java, Monk::class.java,
                        Golem::class.java, Golem::class.java, Golem::class.java))

        // Halls
            22 ->
                //3x succubus, 3x evil eye
                return ArrayList(Arrays.asList(
                        Succubus::class.java, Succubus::class.java, Succubus::class.java,
                        Eye::class.java, Eye::class.java, Eye::class.java))
            23 ->
                //2x succubus, 4x evil eye, 2x scorpio
                return ArrayList(Arrays.asList(
                        Succubus::class.java, Succubus::class.java,
                        Eye::class.java, Eye::class.java, Eye::class.java, Eye::class.java,
                        Scorpio::class.java, Scorpio::class.java))
            24 ->
                //1x succubus, 2x evil eye, 3x scorpio
                return ArrayList(Arrays.asList(
                        Succubus::class.java,
                        Eye::class.java, Eye::class.java,
                        Scorpio::class.java, Scorpio::class.java, Scorpio::class.java))
            else -> return ArrayList(Arrays.asList<Class<Rat>>(Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java, Rat::class.java))
        }

    }

    //has a chance to add a rarely spawned mobs to the rotation
    fun addRareMobs(depth: Int, rotation: ArrayList<Class<out Mob>>) {

        when (depth) {
            4 -> {
                if (Random.Float() < 0.01f) rotation.add(Skeleton::class.java)
                if (Random.Float() < 0.01f) rotation.add(Thief::class.java)
                return
            }

        // Prison
            6 -> {
                if (Random.Float() < 0.2f) rotation.add(Shaman::class.java)
                return
            }
            8 -> {
                if (Random.Float() < 0.02f) rotation.add(Bat::class.java)
                return
            }
            9 -> {
                if (Random.Float() < 0.02f) rotation.add(Bat::class.java)
                if (Random.Float() < 0.01f) rotation.add(Brute::class.java)
                return
            }

        // Caves
            13 -> {
                if (Random.Float() < 0.02f) rotation.add(Elemental::class.java)
                return
            }
            14 -> {
                if (Random.Float() < 0.02f) rotation.add(Elemental::class.java)
                if (Random.Float() < 0.01f) rotation.add(Monk::class.java)
                return
            }

        // City
            19 -> {
                if (Random.Float() < 0.02f) rotation.add(Succubus::class.java)
                return
            }

        // Sewers
            else -> return
        }
    }

    //switches out regular mobs for their alt versions when appropriate
    private fun swapMobAlts(rotation: ArrayList<Class<out Mob>>) {
        for (i in rotation.indices) {
            if (Random.Int(50) == 0) {
                var cl = rotation[i]
                if (cl == Rat::class.java) {
                    cl = Albino::class.java
                } else if (cl == Thief::class.java) {
                    cl = Bandit::class.java
                } else if (cl == Brute::class.java) {
                    cl = Shielded::class.java
                } else if (cl == Monk::class.java) {
                    cl = Senior::class.java
                } else if (cl == Scorpio::class.java) {
                    cl = Acidic::class.java
                }
                rotation[i] = cl
            }
        }
    }
}
