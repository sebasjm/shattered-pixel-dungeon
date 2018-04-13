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

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.watabou.utils.Bundle
import com.watabou.utils.FileUtils

import java.io.IOException

object Journal {

    val JOURNAL_FILE = "journal.dat"

    private var loaded = false

    //package-private
    internal var saveNeeded = false

    fun loadGlobal() {
        if (loaded) {
            return
        }

        var bundle: Bundle
        try {
            bundle = FileUtils.bundleFromFile(JOURNAL_FILE)

        } catch (e: IOException) {
            bundle = Bundle()
        }

        Catalog.restore(bundle)
        Document.restore(bundle)

        loaded = true
    }

    fun saveGlobal() {
        if (!saveNeeded) {
            return
        }

        val bundle = Bundle()

        Catalog.store(bundle)
        Document.store(bundle)

        try {
            FileUtils.bundleToFile(JOURNAL_FILE, bundle)
            saveNeeded = false
        } catch (e: IOException) {
            ShatteredPixelDungeon.reportException(e)
        }

    }

}
