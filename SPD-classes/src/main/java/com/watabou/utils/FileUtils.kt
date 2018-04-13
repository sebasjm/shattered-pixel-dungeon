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

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

    // Files

    fun fileExists(name: String): Boolean {
        val file = File(Game.instance!!.filesDir, name)
        return file.exists() && !file.isDirectory
    }

    fun getFile(name: String): File? {
        return getFile(Game.instance!!.filesDir, name)
    }

    fun getFile(base: File, name: String): File? {
        val file = File(base, name)
        return if (!file.exists() || !file.isDirectory) {
            file
        } else null
    }

    fun deleteFile(name: String): Boolean {
        return Game.instance!!.deleteFile(name)
    }

    fun deleteFile(file: File): Boolean {
        return !file.isDirectory && file.delete()
    }


    // Directories

    fun dirExists(name: String): Boolean {
        val dir = File(Game.instance!!.filesDir, name)
        return dir.exists() && dir.isDirectory
    }

    //base directory
    fun getDir(name: String): File? {
        return getDir(Game.instance!!.filesDir, name)
    }

    fun getDir(base: File, name: String): File? {
        val dir = File(base, name)
        if (!dir.exists() && dir.mkdirs()) {
            return dir
        } else if (dir.isDirectory) {
            return dir
        }
        return null
    }

    fun deleteDir(name: String): Boolean {
        return deleteDir(getDir(name))
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir == null || !dir.isDirectory) {
            return false
        }

        for (f in dir.listFiles()) {
            if (f.isDirectory) {
                if (!deleteDir(f)) return false
            } else {
                if (!deleteFile(f)) return false
            }
        }

        return dir.delete()
    }

    // bundle reading

    //only works for base path
    @Throws(IOException::class)
    fun bundleFromFile(fileName: String): Bundle {
        return bundleFromStream(Game.instance!!.openFileInput(fileName))
    }

    @Throws(IOException::class)
    fun bundleFromFile(file: File): Bundle {
        return bundleFromStream(FileInputStream(file))
    }

    @Throws(IOException::class)
    private fun bundleFromStream(input: InputStream): Bundle {
        val bundle = Bundle.read(input)
        input.close()
        return bundle
    }

    // bundle writing

    //only works for base path
    @Throws(IOException::class)
    fun bundleToFile(fileName: String, bundle: Bundle) {
        bundleToStream(Game.instance!!.openFileOutput(fileName, Game.MODE_PRIVATE), bundle)
    }

    @Throws(IOException::class)
    fun bundleToFile(file: File, bundle: Bundle) {
        bundleToStream(FileOutputStream(file), bundle)
    }

    @Throws(IOException::class)
    private fun bundleToStream(output: OutputStream, bundle: Bundle) {
        Bundle.write(bundle, output)
        output.close()
    }

}
