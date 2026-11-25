package com.shantokofficial.mkkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import kotlin.math.abs

class MKKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {
    private var keyboardView: SwipeKeyboardView? = null
    private var banglaKeyboard: Keyboard? = null
    private var englishKeyboard: Keyboard? = null
    private var english = false
    private var shifted = false

    override fun onCreateInputView(): View {
        banglaKeyboard = Keyboard(this, R.xml.bangla_qwerty)
        englishKeyboard = Keyboard(this, R.xml.english_qwerty)
        val view = SwipeKeyboardView(this)
        keyboardView = view
        view.isPreviewEnabled = false
        view.setOnKeyboardActionListener(this)
        showCurrentKeyboard()
        return view
    }

    private fun showCurrentKeyboard() {
        val view = keyboardView ?: return
        val keyboard = if (english) englishKeyboard else banglaKeyboard
        view.keyboard = keyboard
        view.invalidateAllKeys()
    }

    private fun toggleLanguage() {
        english = !english
        shifted = false
        showCurrentKeyboard()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        showCurrentKeyboard()
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic: InputConnection = currentInputConnection ?: return
        when (primaryCode) {
            -101 -> toggleLanguage()
            -1 -> {
                shifted = !shifted
                val k = if (english) englishKeyboard else banglaKeyboard
                k?.isShifted = shifted
                keyboardView?.invalidateAllKeys()
            }
            -5 -> ic.deleteSurroundingText(1, 0)
            -4 -> ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            -2 -> { /* Symbol layer will be added after the base layouts. */ }
            32 -> ic.commitText(" ", 1)
            else -> {
                var value = String(Character.toChars(primaryCode))
                if (english && shifted) value = value.uppercase()
                ic.commitText(value, 1)
            }
        }
    }

    override fun onText(text: CharSequence?) { if (text != null) currentInputConnection?.commitText(text, 1) }
    override fun swipeLeft() { }
    override fun swipeRight() { }
    override fun swipeDown() { }
    override fun swipeUp() { }
    override fun onPress(primaryCode: Int) { }
    override fun onRelease(primaryCode: Int) { }

    inner class SwipeKeyboardView(context: android.content.Context) : KeyboardView(context, null) {
        private var downX = 0f
        private var downY = 0f
        private var languageSwipe = false

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    languageSwipe = downY > height * 0.72f &&
                        downX > width * 0.18f && downX < width * 0.82f
                    if (languageSwipe) return true
                }
                MotionEvent.ACTION_MOVE -> if (languageSwipe) return true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (languageSwipe) {
                        val dx = event.x - downX
                        if (abs(dx) > dp(36)) toggleLanguage()
                        languageSwipe = false
                        return true
                    }
                }
            }
            return super.onTouchEvent(event)
        }

        private fun dp(value: Int): Float =
            value * resources.displayMetrics.density
    }
}
