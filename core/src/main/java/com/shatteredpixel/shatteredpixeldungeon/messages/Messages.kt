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

package com.shatteredpixel.shatteredpixeldungeon.messages

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.watabou.utils.DeviceCompat

import java.util.Arrays
import java.util.Enumeration
import java.util.HashMap
import java.util.HashSet
import java.util.Locale
import java.util.ResourceBundle

/*
	Simple wrapper class for java resource bundles.

	The core idea here is that each string resource's key is a combination of the class definition and a local value.
	An object or static method would usually call this with an object/class reference (usually its own) and a local key.
	This means that an object can just ask for "name" rather than, say, "items.weapon.enchantments.death.name"
 */
object Messages {

    /*
		use hashmap for two reasons. Firstly because android 2.2 doesn't support resourcebundle.containskey(),
		secondly so I can read in and combine multiple properties files,
		resulting in a more clean structure for organizing all the strings, instead of one big file.

		..Yes R.string would do this for me, but that's not multiplatform
	 */
    private var strings: HashMap<String, String>? = null
    private var lang: Languages? = null


    /**
     * Setup Methods
     */

    private val prop_files = arrayOf("com.shatteredpixel.shatteredpixeldungeon.messages.actors.actors", "com.shatteredpixel.shatteredpixeldungeon.messages.items.items", "com.shatteredpixel.shatteredpixeldungeon.messages.journal.journal", "com.shatteredpixel.shatteredpixeldungeon.messages.levels.levels", "com.shatteredpixel.shatteredpixeldungeon.messages.plants.plants", "com.shatteredpixel.shatteredpixeldungeon.messages.scenes.scenes", "com.shatteredpixel.shatteredpixeldungeon.messages.ui.ui", "com.shatteredpixel.shatteredpixeldungeon.messages.windows.windows", "com.shatteredpixel.shatteredpixeldungeon.messages.misc.misc")

    //Words which should not be capitalized in title case, mostly prepositions which appear ingame
    //This list is not comprehensive!
    private val noCaps = HashSet(
            Arrays.asList(*arrayOf(
                    //English
                    "a", "an", "and", "of", "by", "to", "the", "x"))
    )

    fun lang(): Languages? {
        return lang
    }

    init {
        setup(SPDSettings.language())
    }

    fun setup(lang: Languages) {
        strings = HashMap()
        Messages.lang = lang
        val locale = Locale(lang.code())

        for (file in prop_files) {
            val bundle = ResourceBundle.getBundle(file, locale)
            val keys = bundle.keys
            while (keys.hasMoreElements()) {
                val key = keys.nextElement()
                var value = bundle.getString(key)

                if (DeviceCompat.usesISO_8859_1()) {
                    try {
                        value = String(value.toByteArray(charset("ISO-8859-1")), "UTF-8")
                    } catch (e: Exception) {
                        ShatteredPixelDungeon.reportException(e)
                    }

                }

                strings!![key] = value
            }
        }
    }


    /**
     * Resource grabbing methods
     */

    operator fun get(key: String, vararg args: Any): String {
        return get(null, key, *args)
    }

    operator fun get(o: Any, k: String, vararg args: Any): String {
        return get(o.javaClass, k, *args)
    }

    operator fun get(c: Class<*>?, k: String, vararg args: Any): String {
        var key: String
        if (c != null) {
            key = c.name.replace("com.shatteredpixel.shatteredpixeldungeon.", "")
            key += ".$k"
        } else
            key = k

        return if (strings!!.containsKey(key.toLowerCase(Locale.ENGLISH))) {
            if (args.size > 0)
                format(strings!![key.toLowerCase(Locale.ENGLISH)], *args)
            else
                strings!![key.toLowerCase(Locale.ENGLISH)]
        } else {
            //this is so child classes can inherit properties from their parents.
            //in cases where text is commonly grabbed as a utility from classes that aren't mean to be instantiated
            //(e.g. flavourbuff.dispTurns()) using .class directly is probably smarter to prevent unnecessary recursive calls.
            if (c != null && c.superclass != null) {
                get(c.superclass, k, *args)
            } else {
                "!!!NO TEXT FOUND!!!"
            }
        }
    }


    /**
     * String Utility Methods
     */

    fun format(format: String, vararg args: Any): String {
        return String.format(Locale.ENGLISH, format, *args)
    }

    fun capitalize(str: String): String {
        return if (str.length == 0)
            str
        else
            Character.toTitleCase(str[0]) + str.substring(1)
    }

    fun titleCase(str: String): String {
        //English capitalizes every word except for a few exceptions
        if (lang == Languages.ENGLISH) {
            var result = ""
            //split by any unicode space character
            for (word in str.split("(?<=\\p{Zs})".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()) {
                if (noCaps.contains(word.trim({ it <= ' ' }).toLowerCase(Locale.ENGLISH).replace(":|[0-9]".toRegex(), ""))) {
                    result += word
                } else {
                    result += capitalize(word)
                }
            }
            //first character is always capitalized.
            return capitalize(result)
        }

        //Otherwise, use sentence case
        return capitalize(str)
    }
}