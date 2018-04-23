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

package com.shatteredpixel.shatteredpixeldungeon.journal

import com.shatteredpixel.shatteredpixeldungeon.Dungeon
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

import java.util.ArrayList
import java.util.Collections

object Notes {

    private var records: ArrayList<Record>? = null

    private val RECORDS = "records"

    abstract class Record : Comparable<Record>, Bundlable {

        protected var depth: Int = 0

        open fun depth(): Int {
            return depth
        }

        abstract fun desc(): String

        abstract override fun equals(obj: Any?): Boolean

        override fun compareTo(another: Record): Int {
            return another.depth() - depth()
        }

        override fun restoreFromBundle(bundle: Bundle) {
            depth = bundle.getInt(DEPTH)
        }

        override fun storeInBundle(bundle: Bundle) {
            bundle.put(DEPTH, depth)
        }

        companion object {

            private val DEPTH = "depth"
        }
    }

    enum class Landmark {
        WELL_OF_HEALTH,
        WELL_OF_AWARENESS,
        WELL_OF_TRANSMUTATION,
        ALCHEMY,
        GARDEN,
        STATUE,

        GHOST,
        WANDMAKER,
        TROLL,
        IMP;

        fun desc(): String {
            return Messages.get(this.javaClass, name)
        }
    }

    class LandmarkRecord : Record {

        protected var landmark: Landmark

        constructor(landmark: Landmark, depth: Int) {
            this.landmark = landmark
            this.depth = depth
        }

        override fun desc(): String {
            return landmark.desc()
        }

        override fun equals(obj: Any?): Boolean {
            return (obj is LandmarkRecord
                    && landmark == obj.landmark
                    && depth() == obj.depth())
        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            landmark = Landmark.valueOf(bundle.getString(LANDMARK))
        }

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(LANDMARK, landmark.toString())
        }

        companion object {

            private val LANDMARK = "landmark"
        }
    }

    class KeyRecord : Record {

        protected var key: Key

        constructor(key: Key) {
            this.key = key
        }

        override fun depth(): Int {
            return key.depth
        }

        override fun desc(): String {
            return key.toString()
        }

        fun type(): Class<out Key> {
            return key.javaClass
        }

        fun quantity(): Int {
            return key.quantity()
        }

        fun quantity(num: Int) {
            key.quantity(num)
        }

        override fun equals(obj: Any?): Boolean {
            return obj is KeyRecord && key.isSimilar(obj.key)
        }

        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            key = bundle.get(KEY) as Key
        }

        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(KEY, key)
        }

        companion object {

            private val KEY = "key"
        }
    }

    fun reset() {
        records = ArrayList()
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(RECORDS, records!!)
    }

    fun restoreFromBundle(bundle: Bundle) {
        records = ArrayList()
        for (rec in bundle.getCollection(RECORDS)) {
            records!!.add(rec as Record)
        }
    }

    fun add(landmark: Landmark) {
        val l = LandmarkRecord(landmark, Dungeon.depth)
        if (!records!!.contains(l)) {
            records!!.add(LandmarkRecord(landmark, Dungeon.depth))
            Collections.sort(records!!)
        }
    }

    fun remove(landmark: Landmark) {
        records!!.remove(LandmarkRecord(landmark, Dungeon.depth))
    }

    fun add(key: Key) {
        var k = KeyRecord(key)
        if (!records!!.contains(k)) {
            records!!.add(k)
            Collections.sort(records!!)
        } else {
            k = records!![records!!.indexOf(k)] as KeyRecord
            k.quantity(k.quantity() + key.quantity())
        }
    }

    fun remove(key: Key) {
        var k = KeyRecord(key)
        if (records!!.contains(k)) {
            k = records!![records!!.indexOf(k)] as KeyRecord
            k.quantity(k.quantity() - key.quantity())
            if (k.quantity() <= 0) {
                records!!.remove(k)
            }
        }
    }

    fun keyCount(key: Key): Int {
        var k = KeyRecord(key)
        if (records!!.contains(k)) {
            k = records!![records!!.indexOf(k)] as KeyRecord
            return k.quantity()
        } else {
            return 0
        }
    }

    fun getRecords(): ArrayList<Record> {
        return getRecords(Record::class.java)
    }

    fun <T : Notes.Record> getRecords(recordType: Class<T>): ArrayList<T> {
        val filtered = ArrayList<T>()
        for (rec in records!!) {
            if (recordType.isInstance(rec)) {
                filtered.add(rec as T)
            }
        }
        return filtered
    }

    fun remove(rec: Record) {
        records!!.remove(rec)
    }

}
