package com.kbratkovic.investmentgoldportfolio.util

import android.widget.EditText

object  Utils {

    fun editTextSelectAll(editText: EditText) {
        if (editText.hasFocus()) {
            editText.postDelayed({
                editText.selectAll()
            }, 200)
        }
    }
}