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

package com.watabou.noosa.audio

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.AudioManager
import android.media.SoundPool

import com.watabou.noosa.Game

import java.io.IOException
import java.util.HashMap
import java.util.LinkedList

enum class Sample : SoundPool.OnLoadCompleteListener {

    INSTANCE;

    protected var pool: SoundPool? = SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0)

    protected var ids = HashMap<Any, Int>()

    var isEnabled = true
        private set
    private var volume = 1f

    private var loadingQueue = LinkedList<String>()

    fun reset() {

        ids.clear()
        loadingQueue = LinkedList()
        pool!!.release()

        pool = SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0)
        pool!!.setOnLoadCompleteListener(this)

    }

    fun pause() {
        if (pool != null) {
            pool!!.autoPause()
        }
    }

    fun resume() {
        if (pool != null) {
            pool!!.autoResume()
        }
    }

    fun load(vararg assets: String) {

        for (asset in assets) {
            loadingQueue.add(asset)
        }
        loadNext()
    }

    private fun loadNext() {
        val asset = loadingQueue.poll()
        if (asset != null) {
            if (!ids.containsKey(asset)) {
                try {
                    pool!!.setOnLoadCompleteListener { soundPool, sampleId, status -> loadNext() }

                    val manager = Game.instance!!.assets
                    val fd = manager.openFd(asset)
                    val streamID = pool!!.load(fd, 1)
                    ids[asset] = streamID
                    fd.close()
                } catch (e: IOException) {
                    loadNext()
                } catch (e: NullPointerException) {
                    // Do nothing (stop loading sounds)
                }

            } else {
                loadNext()
            }
        }
    }

    fun unload(src: Any) {

        if (ids.containsKey(src)) {

            pool!!.unload(ids[src])
            ids.remove(src)
        }
    }

    @JvmOverloads
    fun play(id: Any, volume: Float = 1f): Int {
        return play(id, volume, volume, 1f)
    }

    fun play(id: Any, leftVolume: Float, rightVolume: Float, rate: Float): Int {
        return if (isEnabled && ids.containsKey(id)) {
            pool!!.play(ids[id], leftVolume * volume, rightVolume * volume, 0, 0, rate)
        } else {
            -1
        }
    }

    fun enable(value: Boolean) {
        isEnabled = value
    }

    fun volume(value: Float) {
        this.volume = value
    }

    override fun onLoadComplete(soundPool: SoundPool, sampleId: Int, status: Int) {}

    companion object {

        val MAX_STREAMS = 8
    }
}