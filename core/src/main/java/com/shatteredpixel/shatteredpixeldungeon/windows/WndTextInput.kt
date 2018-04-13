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

package com.shatteredpixel.shatteredpixeldungeon.windows

import android.app.Activity
import android.text.InputFilter
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextMultiline
import com.shatteredpixel.shatteredpixeldungeon.ui.Window
import com.watabou.noosa.Game
import com.watabou.noosa.RenderedText

//This class makes use of the android EditText component to handle text input
class WndTextInput(title: String, initialValue: String, maxLength: Int,
                   multiLine: Boolean, posTxt: String, negTxt: String?) : Window() {

    private var textInput: EditText? = null

    val text: String
        get() = textInput!!.text.toString().trim { it <= ' ' }

    constructor(title: String, initialValue: String, multiLine: Boolean, posTxt: String, negTxt: String) : this(title, initialValue, if (multiLine) MAX_LEN_MULTI else MAX_LEN_SINGLE, multiLine, posTxt, negTxt) {}

    init {

        //need to offset to give space for the soft keyboard
        if (SPDSettings.landscape()) {
            offset(if (multiLine) -45 else -45)
        } else {
            offset(if (multiLine) -60 else -45)
        }

        val width: Int
        if (SPDSettings.landscape() && multiLine) {
            width = W_LAND_MULTI //more editing space for landscape users
        } else {
            width = WIDTH
        }

        ShatteredPixelDungeon.instance!!.runOnUiThread {
            val txtTitle = PixelScene.renderMultiline(title, 9)
            txtTitle.maxWidth(width)
            txtTitle.hardlight(Window.TITLE_COLOR)
            txtTitle.setPos((width - txtTitle.width()) / 2, 0f)
            add(txtTitle)

            var pos = txtTitle.bottom() + MARGIN

            textInput = EditText(ShatteredPixelDungeon.instance)
            textInput!!.setText(initialValue)
            textInput!!.typeface = RenderedText.font
            textInput!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            textInput!!.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

            //this accounts for the game resolution differing from the display resolution in power saver mode
            val scaledZoom: Float
            scaledZoom = camera!!.zoom * (Game.dispWidth / Game.width.toFloat())

            //sets different visual style depending on whether this is a single or multi line input.
            val inputHeight: Float
            if (multiLine) {

                textInput!!.setSingleLine(false)
                //This is equivalent to PixelScene.renderText(6)
                textInput!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, 6 * scaledZoom)
                //8 lines of text (+1 line for padding)
                inputHeight = 9 * textInput!!.lineHeight / scaledZoom

            } else {

                //sets to single line and changes enter key input to be the same as the positive button
                textInput!!.setSingleLine()
                textInput!!.setOnEditorActionListener(object : EditText.OnEditorActionListener {
                    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
                        onSelect(true)
                        hide()
                        return true
                    }
                })

                //doesn't let the keyboard take over the whole UI
                textInput!!.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI

                //centers text
                textInput!!.gravity = Gravity.CENTER

                //This is equivalent to PixelScene.renderText(9)
                textInput!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, 9 * scaledZoom)
                //1 line of text (+1 line for padding)
                inputHeight = 2 * textInput!!.lineHeight / scaledZoom

            }

            //We haven't added the textInput yet, but we can anticipate its height at this point.
            pos += inputHeight + MARGIN

            val positiveBtn = object : RedButton(posTxt) {
                override fun onClick() {
                    onSelect(true)
                    hide()
                }
            }
            if (negTxt != null)
                positiveBtn.setRect(MARGIN.toFloat(), pos, ((width - MARGIN * 3) / 2).toFloat(), BUTTON_HEIGHT.toFloat())
            else
                positiveBtn.setRect(MARGIN.toFloat(), pos, (width - MARGIN * 2).toFloat(), BUTTON_HEIGHT.toFloat())
            add(positiveBtn)

            if (negTxt != null) {
                val negativeBtn = object : RedButton(negTxt) {
                    override fun onClick() {
                        onSelect(false)
                        hide()
                    }
                }
                negativeBtn.setRect(positiveBtn.right() + MARGIN, pos, ((width - MARGIN * 3) / 2).toFloat(), BUTTON_HEIGHT.toFloat())
                add(negativeBtn)
            }

            pos += (BUTTON_HEIGHT + MARGIN).toFloat()

            //The layout of the TextEdit is in display pixel space, not ingame pixel space
            // resize the window first so we can know the screen-space coordinates for the text input.
            resize(width, pos.toInt())
            val inputTop = (camera!!.cameraToScreen(0f, txtTitle.bottom() + MARGIN).y * (Game.dispWidth / Game.width.toFloat())).toInt()

            //The text input exists in a separate view ontop of the normal game view.
            // It visually appears to be a part of the game window but is infact a separate
            // UI element from the game entirely.
            val layout = FrameLayout.LayoutParams(
                    ((width - MARGIN * 2) * scaledZoom).toInt(),
                    (inputHeight * scaledZoom).toInt(),
                    Gravity.CENTER_HORIZONTAL)
            layout.setMargins(0, inputTop, 0, 0)
            ShatteredPixelDungeon.instance!!.addContentView(textInput, layout)
        }
    }

    protected fun onSelect(positive: Boolean) {}

    override fun destroy() {
        super.destroy()
        if (textInput != null) {
            ShatteredPixelDungeon.instance!!.runOnUiThread {
                //make sure we remove the edit text and soft keyboard
                (textInput!!.parent as ViewGroup).removeView(textInput)

                val imm = ShatteredPixelDungeon
                        .instance!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textInput!!.windowToken, 0)

                //Soft keyboard sometimes triggers software buttons, so make sure to reassert immersive
                ShatteredPixelDungeon.updateSystemUI()

                textInput = null
            }
        }
    }

    companion object {

        private val WIDTH = 120
        private val W_LAND_MULTI = 200 //in the specific case of multiline in landscape
        private val MARGIN = 2
        private val BUTTON_HEIGHT = 16

        //default maximum lengths for inputted text
        private val MAX_LEN_SINGLE = 20
        private val MAX_LEN_MULTI = 2000
    }
}
