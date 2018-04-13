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

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

import com.watabou.noosa.Game

enum class Music {

    INSTANCE;

    private var player: MediaPlayer? = null

    private var lastPlayed: String? = null
    private var looping: Boolean = false

    var isEnabled = true
        private set
    private var volume = 1f

    val isPlaying: Boolean
        get() = player != null && player!!.isPlaying

    fun play(assetName: String?, looping: Boolean) {

        if (isPlaying && lastPlayed != null && lastPlayed == assetName) {
            return
        }

        stop()

        lastPlayed = assetName
        this.looping = looping

        if (!isEnabled || assetName == null) {
            return
        }

        try {

            val afd = Game.instance!!.assets.openFd(assetName)

            val mp = MediaPlayer()
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mp.prepare()
            player = mp
            player!!.start()
            player!!.isLooping = looping
            player!!.setVolume(volume, volume)

        } catch (e: Exception) {

            Game.reportException(e)
            player = null

        }

    }

    fun mute() {
        lastPlayed = null
        stop()
    }

    fun pause() {
        if (player != null) {
            player!!.pause()
        }
    }

    fun resume() {
        if (player != null) {
            player!!.start()
            player!!.isLooping = looping
        }
    }

    fun stop() {
        if (player != null) {
            player!!.stop()
            player!!.release()
            player = null
        }
    }

    fun volume(value: Float) {
        volume = value
        if (player != null) {
            player!!.setVolume(value, value)
        }
    }

    fun enable(value: Boolean) {
        isEnabled = value
        if (isPlaying && !value) {
            stop()
        } else if (!isPlaying && value) {
            play(lastPlayed, looping)
        }
    }

    companion object {

        val callMute: PhoneStateListener = object : PhoneStateListener() {

            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    INSTANCE.pause()

                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if (!Game.instance!!.isPaused) {
                        INSTANCE.resume()
                    }
                }

                super.onCallStateChanged(state, incomingNumber)
            }
        }

        fun setMuteListener() {
            //versions lower than this require READ_PHONE_STATE permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val mgr = Game.instance!!.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                mgr.listen(Music.callMute, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }
}
