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

package com.watabou.utils

import com.watabou.noosa.Game

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PushbackInputStream
import java.lang.reflect.Modifier
import java.util.ArrayList
import java.util.HashMap
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class Bundle private constructor(private val data: JSONObject?) {

    val isNull: Boolean
        get() = data == null

    constructor() : this(JSONObject()) {}

    override fun toString(): String {
        return data!!.toString()
    }

    operator fun contains(key: String): Boolean {
        return !data!!.isNull(key)
    }

    fun getBoolean(key: String): Boolean {
        return data!!.optBoolean(key)
    }

    fun getInt(key: String): Int {
        return data!!.optInt(key)
    }

    fun getLong(key: String): Long {
        return data!!.optLong(key)
    }

    fun getFloat(key: String): Float {
        return data!!.optDouble(key, 0.0).toFloat()
    }

    fun getString(key: String): String {
        return data!!.optString(key)
    }

    fun getClass(key: String): Class<*>? {
        var clName: String? = getString(key).replace("class ", "")
        if (clName != null) {
            if (aliases.containsKey(clName)) {
                clName = aliases[clName]
            }
            try {
                val cl = Class.forName(clName)
                return cl
            } catch (e: ClassNotFoundException) {
                Game.reportException(e)
                return null
            }

        }
        return null
    }

    fun getBundle(key: String): Bundle {
        return Bundle(data!!.optJSONObject(key))
    }

    private fun get(): Bundlable? {
        if (data == null) return null
        try {
            var clName = getString(CLASS_NAME)
            if (aliases.containsKey(clName)) {
                clName = aliases[clName]!!
            }

            val cl = Class.forName(clName)
            if (cl != null && (!cl.isMemberClass || Modifier.isStatic(cl.modifiers))) {
                val `object` = cl.newInstance() as Bundlable
                `object`.restoreFromBundle(this)
                return `object`
            } else {
                return null
            }
        } catch (e: ClassNotFoundException) {
            Game.reportException(e)
            return null
        } catch (e: InstantiationException) {
            Game.reportException(e)
            return null
        } catch (e: IllegalAccessException) {
            Game.reportException(e)
            return null
        }

    }

    operator fun get(key: String): Bundlable? {
        return getBundle(key).get()
    }

    fun <E : Enum<E>> getEnum(key: String, enumClass: Class<E>): E {
        try {
            return java.lang.Enum.valueOf(enumClass, data!!.getString(key))
        } catch (e: JSONException) {
            Game.reportException(e)
            return enumClass.enumConstants[0]
        }

    }

    fun getIntArray(key: String): IntArray? {
        try {
            val array = data!!.getJSONArray(key)
            val length = array.length()
            val result = IntArray(length)
            for (i in 0 until length) {
                result[i] = array.getInt(i)
            }
            return result
        } catch (e: JSONException) {
            Game.reportException(e)
            return null
        }

    }

    fun getFloatArray(key: String): FloatArray? {
        try {
            val array = data!!.getJSONArray(key)
            val length = array.length()
            val result = FloatArray(length)
            for (i in 0 until length) {
                result[i] = array.optDouble(i, 0.0).toFloat()
            }
            return result
        } catch (e: JSONException) {
            Game.reportException(e)
            return null
        }

    }

    fun getBooleanArray(key: String): BooleanArray? {
        try {
            val array = data!!.getJSONArray(key)
            val length = array.length()
            val result = BooleanArray(length)
            for (i in 0 until length) {
                result[i] = array.getBoolean(i)
            }
            return result
        } catch (e: JSONException) {
            Game.reportException(e)
            return null
        }

    }

    fun getStringArray(key: String): Array<String>? {
        try {
            val array = data!!.getJSONArray(key)
            val length = array.length()
            val result = arrayOfNulls<String>(length)
            for (i in 0 until length) {
                result[i] = array.getString(i)
            }
            return result.filterNotNull().toTypedArray()
        } catch (e: JSONException) {
            Game.reportException(e)
            return null
        }

    }

    fun getClassArray(key: String): Array<Class<*>>? {
        try {
            val array = data!!.getJSONArray(key)
            val length = array.length()
            val result = arrayOfNulls<Class<*>>(length)
            for (i in 0 until length) {
                var clName = array.getString(i).replace("class ", "")
                if (aliases.containsKey(clName)) {
                    clName = aliases[clName]!!
                }
                try {
                    val cl = Class.forName(clName)
                    result[i] = cl
                } catch (e: ClassNotFoundException) {
                    Game.reportException(e)
                    result[i] = null
                }

            }
            return result.filterNotNull().toTypedArray()
        } catch (e: JSONException) {
            Game.reportException(e)
            return null
        }

    }

    fun getCollection(key: String): Collection<Bundlable> {

        val list = ArrayList<Bundlable>()

        try {
            val array = data!!.getJSONArray(key)
            for (i in 0 until array.length()) {
                val O = Bundle(array.getJSONObject(i)).get()
                if (O != null) list.add(O)
            }
        } catch (e: JSONException) {
            Game.reportException(e)
        }

        return list
    }

    fun put(key: String, value: Boolean) {
        try {
            data!!.put(key, value)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, value: Int) {
        try {
            data!!.put(key, value)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, value: Long) {
        try {
            data!!.put(key, value)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, value: Float) {
        try {
            data!!.put(key, value.toDouble())
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, value: String) {
        try {
            data!!.put(key, value)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, value: Class<*>) {
        try {
            data!!.put(key, value)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, bundle: Bundle) {
        try {
            data!!.put(key, bundle.data)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, `object`: Bundlable?) {
        if (`object` != null) {
            try {
                val bundle = Bundle()
                bundle.put(CLASS_NAME, `object`.javaClass.getName())
                `object`.storeInBundle(bundle)
                data!!.put(key, bundle.data)
            } catch (e: JSONException) {
                Game.reportException(e)
            }

        }
    }

    fun put(key: String, value: Enum<*>?) {
        if (value != null) {
            try {
                data!!.put(key, value.name)
            } catch (e: JSONException) {
                Game.reportException(e)
            }

        }
    }

    fun put(key: String, array: IntArray) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i])
            }
            data!!.put(key, jsonArray)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, array: FloatArray) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i].toDouble())
            }
            data!!.put(key, jsonArray)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, array: BooleanArray) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i])
            }
            data!!.put(key, jsonArray)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, array: Array<String>) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i])
            }
            data!!.put(key, jsonArray)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, array: Array<Class<*>>) {
        try {
            val jsonArray = JSONArray()
            for (i in array.indices) {
                jsonArray.put(i, array[i])
            }
            data!!.put(key, jsonArray)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    fun put(key: String, collection: Collection<Bundlable?>) {
        val array = JSONArray()
        for (`object` in collection) {
            //Skip none-static inner classes as they can't be instantiated through bundle restoring
            //Classes which make use of none-static inner classes must manage instantiation manually
            if (`object` != null) {
                val cl = `object`.javaClass
                if (!cl.isMemberClass() || Modifier.isStatic(cl.getModifiers())) {
                    val bundle = Bundle()
                    bundle.put(CLASS_NAME, cl.getName())
                    `object`.storeInBundle(bundle)
                    array.put(bundle.data)
                }
            }
        }
        try {
            data!!.put(key, array)
        } catch (e: JSONException) {
            Game.reportException(e)
        }

    }

    companion object {

        private val CLASS_NAME = "__className"

        private val aliases = HashMap<String, String>()

        //useful to turn this off for save data debugging.
        private val compressByDefault = true

        private val GZIP_BUFFER = 1024 * 4 //4 kb

        @Throws(IOException::class)
        fun read(stream: InputStream): Bundle {

            try {
                val reader: BufferedReader

                //determines if we're reading a regular, or compressed file
                val pb = PushbackInputStream(stream, 2)
                val header = ByteArray(2)
                pb.unread(header, 0, pb.read(header))
                //GZIP header is 0x1f8b
                if (header[0] == 0x1f.toByte() && header[1] == 0x8b.toByte())
                    reader = BufferedReader(InputStreamReader(GZIPInputStream(pb, GZIP_BUFFER)))
                else
                    reader = BufferedReader(InputStreamReader(pb))

                val json = JSONTokener(reader.readLine()).nextValue() as JSONObject
                reader.close()

                return Bundle(json)
            } catch (e: Exception) {
                Game.reportException(e)
                throw IOException()
            }

        }

        @JvmOverloads
        fun write(bundle: Bundle, stream: OutputStream, compressed: Boolean = compressByDefault): Boolean {
            try {
                val writer: BufferedWriter
                if (compressed)
                    writer = BufferedWriter(OutputStreamWriter(GZIPOutputStream(stream, GZIP_BUFFER)))
                else
                    writer = BufferedWriter(OutputStreamWriter(stream))

                writer.write(bundle.data!!.toString())
                writer.close()

                return true
            } catch (e: IOException) {
                Game.reportException(e)
                return false
            }

        }

        fun addAlias(cl: Class<*>, alias: String) {
            aliases[alias] = cl.name
        }
    }

}
