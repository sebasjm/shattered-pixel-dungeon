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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages
import com.watabou.utils.Bundle

import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap

enum class Document {

    ADVENTURERS_GUIDE;

    private val pages = LinkedHashMap<String, Boolean>()

    fun pages(): Collection<String> {
        return pages.keys
    }

    fun addPage(page: String): Boolean {
        if (pages.containsKey(page) && !pages[page]) {
            pages[page] = true
            Journal.saveNeeded = true
            return true
        }
        return false
    }

    fun hasPage(page: String): Boolean {
        return pages.containsKey(page) && pages[page]
    }

    fun title(): String {
        return Messages.get(this, "$name.title")
    }

    fun pageTitle(page: String): String {
        return Messages.get(this, "$name.$page.title")
    }

    fun pageBody(page: String): String {
        return Messages.get(this, "$name.$page.body")
    }

    companion object {

        val GUIDE_INTRO_PAGE = "Intro"
        val GUIDE_SEARCH_PAGE = "Examining_and_Searching"

        init {
            ADVENTURERS_GUIDE.pages[GUIDE_INTRO_PAGE] = false
            ADVENTURERS_GUIDE.pages["Identifying"] = false
            ADVENTURERS_GUIDE.pages[GUIDE_SEARCH_PAGE] = false
            ADVENTURERS_GUIDE.pages["Strength"] = false
            ADVENTURERS_GUIDE.pages["Food"] = false
            ADVENTURERS_GUIDE.pages["Levelling"] = false
            ADVENTURERS_GUIDE.pages["Surprise_Attacks"] = false
            ADVENTURERS_GUIDE.pages["Dieing"] = false
            ADVENTURERS_GUIDE.pages["Looting"] = false
            ADVENTURERS_GUIDE.pages["Magic"] = false
        }

        private val DOCUMENTS = "documents"

        fun store(bundle: Bundle) {

            val docBundle = Bundle()

            for (doc in values()) {
                val pages = ArrayList<String>()
                for (page in doc.pages()) {
                    if (doc.pages[page]) {
                        pages.add(page)
                    }
                }
                if (!pages.isEmpty()) {
                    docBundle.put(doc.name, pages.toTypedArray<String>())
                }
            }

            bundle.put(DOCUMENTS, docBundle)

        }

        fun restore(bundle: Bundle) {

            if (!bundle.contains(DOCUMENTS)) {
                return
            }

            val docBundle = bundle.getBundle(DOCUMENTS)

            for (doc in values()) {
                if (docBundle.contains(doc.name)) {
                    val pages = Arrays.asList(*docBundle.getStringArray(doc.name)!!)
                    for (page in pages) {
                        if (doc.pages.containsKey(page)) {
                            doc.pages[page] = true
                        }
                    }
                }
            }
        }
    }

}
