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

package com.shatteredpixel.shatteredpixeldungeon.ui

import com.shatteredpixel.shatteredpixeldungeon.Chrome
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.watabou.input.Touchscreen
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.NinePatch
import com.watabou.noosa.RenderedText
import com.watabou.noosa.TouchArea
import com.watabou.noosa.ui.Component
import com.watabou.utils.GameMath
import com.watabou.utils.PointF

abstract class OptionSlider(title: String, minTxt: String, maxTxt: String, minVal: Int, private val maxVal: Int) : Component() {

    private var touchArea: TouchArea? = null

    private var title: RenderedText? = null
    private var minTxt: RenderedText? = null
    private var maxTxt: RenderedText? = null

    //values are expressed internally as ints, but they can easily be interpreted as something else externally.
    private val minVal: Int
    private var selectedVal: Int = 0

    private var sliderNode: NinePatch? = null
    private var BG: NinePatch? = null
    private var sliderBG: ColorBlock? = null
    private val sliderTicks: Array<ColorBlock>
    private var tickDist: Float = 0.toFloat()

    var selectedValue: Int
        get() = selectedVal
        set(`val`) {
            this.selectedVal = `val`
            sliderNode!!.x = (x + tickDist * (selectedVal - minVal)).toInt().toFloat()
            sliderNode!!.y = sliderBG!!.y - 4
        }


    init {
        var minVal = minVal

        //shouldn't function if this happens.
        if (minVal > maxVal) {
            minVal = maxVal
            active = false
        }

        this.title!!.text(title)
        this.minTxt!!.text(minTxt)
        this.maxTxt!!.text(maxTxt)

        this.minVal = minVal

        sliderTicks = arrayOfNulls(maxVal - minVal + 1)
        for (i in sliderTicks.indices) {
            add(sliderTicks[i] = ColorBlock(1f, 11f, -0xddddde))
        }
        add(sliderNode)
    }

    protected abstract fun onChange()

    override fun createChildren() {
        super.createChildren()

        add(BG = Chrome.get(Chrome.Type.BUTTON))
        BG!!.alpha(0.5f)

        add(title = PixelScene.renderText(9))
        add(this.minTxt = PixelScene.renderText(6))
        add(this.maxTxt = PixelScene.renderText(6))

        add(sliderBG = ColorBlock(1f, 1f, -0xddddde))
        sliderNode = Chrome.get(Chrome.Type.BUTTON)
        sliderNode!!.size(5f, 9f)

        touchArea = object : TouchArea(0f, 0f, 0f, 0f) {
            internal var pressed = false

            override fun onTouchDown(touch: Touchscreen.Touch) {
                pressed = true
                val p = camera()!!.screenToCamera(touch.current.x.toInt(), touch.current.y.toInt())
                sliderNode!!.x = GameMath.gate(sliderBG!!.x - 2, p.x, sliderBG!!.x + sliderBG!!.width() - 2)
                sliderNode!!.brightness(1.5f)
            }

            override fun onDrag(touch: Touchscreen.Touch) {
                if (pressed) {
                    val p = camera()!!.screenToCamera(touch.current.x.toInt(), touch.current.y.toInt())
                    sliderNode!!.x = GameMath.gate(sliderBG!!.x - 2, p.x, sliderBG!!.x + sliderBG!!.width() - 2)
                }
            }

            override fun onTouchUp(touch: Touchscreen.Touch) {
                if (pressed) {
                    val p = camera()!!.screenToCamera(touch.current.x.toInt(), touch.current.y.toInt())
                    sliderNode!!.x = GameMath.gate(sliderBG!!.x - 2, p.x, sliderBG!!.x + sliderBG!!.width() - 2)
                    sliderNode!!.resetColor()

                    //sets the selected value
                    selectedVal = minVal + Math.round(sliderNode!!.x / tickDist)
                    sliderNode!!.x = (x + tickDist * (selectedVal - minVal)).toInt().toFloat()
                    onChange()
                    pressed = false
                }
            }
        }
        add(touchArea)

    }

    override fun layout() {
        title!!.x = x + (width - title!!.width()) / 2
        title!!.y = y + 2
        PixelScene.align(title!!)
        sliderBG!!.y = y + height() - 8
        sliderBG!!.x = x + 2
        sliderBG!!.size(width - 5, 1f)
        tickDist = sliderBG!!.width() / (maxVal - minVal)
        for (i in sliderTicks.indices) {
            sliderTicks[i].y = sliderBG!!.y - 5
            sliderTicks[i].x = (x + 2f + tickDist * i).toInt().toFloat()
        }

        maxTxt!!.y = sliderBG!!.y - 6f - minTxt!!.baseLine()
        minTxt!!.y = maxTxt!!.y
        minTxt!!.x = x + 1
        maxTxt!!.x = x + width() - maxTxt!!.width() - 1f


        sliderNode!!.x = (x + tickDist * (selectedVal - minVal)).toInt().toFloat()
        sliderNode!!.y = sliderBG!!.y - 4

        touchArea!!.x = x
        touchArea!!.y = y
        touchArea!!.width = width()
        touchArea!!.height = height()

        BG!!.size(width(), height())
        BG!!.x = x
        BG!!.y = y

    }
}
