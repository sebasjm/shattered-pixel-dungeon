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

package com.watabou.glwrap

import android.opengl.GLSurfaceView

import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

class ScreenConfigChooser(vals: IntArray, prefs: IntArray, weights: IntArray) : GLSurfaceView.EGLConfigChooser {

    //array of corresponding EGL attributes for each array index
    private val attribEGLconsts = intArrayOf(EGL10.EGL_RED_SIZE, EGL10.EGL_GREEN_SIZE, EGL10.EGL_BLUE_SIZE, EGL10.EGL_ALPHA_SIZE, EGL10.EGL_DEPTH_SIZE, EGL10.EGL_STENCIL_SIZE)

    private var desiredAttribVals = IntArray(attribEGLconsts.size) //desired attribute values
    private var attribPrefs = IntArray(attribEGLconsts.size) //attribute preferences types
    private var prefWeights = IntArray(attribEGLconsts.size) //weights for preferences


    private var egl: EGL10? = null
    private var display: EGLDisplay? = null

    private val eglPrefs = intArrayOf(EGL10.EGL_RENDERABLE_TYPE, 4, //same as EGL_OPENGL_ES2_BIT. config must support GLES 2.0
            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, EGL10.EGL_NONE)

    private val value = IntArray(1)

    @JvmOverloads constructor(depth: Boolean = false) : this(false, depth) {}

    //helper constructor for a basic config with or without depth
    //and whether or not to prefer RGB565 for performance reasons
    //On many devices RGB565 gives slightly better performance for a minimal quality tradeoff.
    constructor(prefRGB565: Boolean, depth: Boolean) : this(
            intArrayOf(5, 6, 5, 0, if (depth) 16 else 0, 0),
            if (prefRGB565)
                intArrayOf(PREF_LOW, PREF_LOW, PREF_LOW, EXACTLY, PREF_LOW, PREF_LOW)
            else
                intArrayOf(PREF_HIGH, PREF_HIGH, PREF_HIGH, EXACTLY, PREF_LOW, PREF_LOW),
            intArrayOf(2, 2, 2, 1, 1, 1)
    ) {
    }

    init {
        if (vals.size != desiredAttribVals.size
                || prefs.size != attribPrefs.size
                || weights.size != prefWeights.size)
            throw IllegalArgumentException("incorrect array lengths!")

        desiredAttribVals = vals
        attribPrefs = prefs
        prefWeights = weights
    }

    override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig {

        this.egl = egl
        this.display = display

        val num = IntArray(1)
        if (!egl.eglChooseConfig(display, eglPrefs, null, 0, num)) {
            throw IllegalArgumentException("eglChooseConfig failed")
        }

        val configs = arrayOfNulls<EGLConfig>(num[0])
        if (!egl.eglChooseConfig(display, eglPrefs, configs, num[0], num)) {
            throw IllegalArgumentException("eglChooseConfig failed")
        }

        val config = chooseConfig(configs) ?: throw IllegalArgumentException("No config chosen")
        return config

    }

    private fun chooseConfig(configs: Array<EGLConfig>): EGLConfig? {
        var bestConfig: EGLConfig? = null
        var bestConfigValue = Integer.MIN_VALUE
        for (curConfig in configs) {

            var curConfigValue = 0

            for (i in attribEGLconsts.indices) {
                val `val` = findConfigAttrib(curConfig, attribEGLconsts[i])

                if (attribPrefs[i] == EXACTLY) {

                    if (desiredAttribVals[i] != `val`) {
                        curConfigValue = Integer.MIN_VALUE
                        break
                    }

                } else if (attribPrefs[i] == PREF_HIGH) {

                    if (desiredAttribVals[i] > `val`) {
                        curConfigValue = Integer.MIN_VALUE
                        break
                    } else {
                        curConfigValue += prefWeights[i] * (`val` - desiredAttribVals[i])
                    }

                } else if (attribPrefs[i] == PREF_LOW) {

                    if (desiredAttribVals[i] > `val`) {
                        curConfigValue = Integer.MIN_VALUE
                        break
                    } else {
                        curConfigValue -= prefWeights[i] * (`val` - desiredAttribVals[i])
                    }

                }
            }

            if (curConfigValue > bestConfigValue) {
                bestConfigValue = curConfigValue
                bestConfig = curConfig
            }

        }
        return bestConfig
    }

    private fun findConfigAttrib(config: EGLConfig, attribute: Int): Int {

        return if (egl!!.eglGetConfigAttrib(display, config, attribute, value)) {
            value[0]
        } else {
            throw IllegalArgumentException("eglGetConfigAttrib failed")
        }
    }

    companion object {

        //attributes with this preference are ignored
        val DONT_CARE = 0

        //attributes with this preference must be present in the config at exactly the given value
        val EXACTLY = 1

        //attributes with this preference must be present in the config with at least the given value
        // In the case of multiple valid configs, chooser will prefer higher values for these attributes
        val PREF_LOW = 2

        //attributes with this preference must be present in the config with at least the given value
        // In the case of multiple valid configs, chooser will prefer lower values for these attributes
        val PREF_HIGH = 3
    }
}
