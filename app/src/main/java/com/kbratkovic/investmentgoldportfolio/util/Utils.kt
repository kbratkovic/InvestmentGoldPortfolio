package com.kbratkovic.investmentgoldportfolio.util

import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.util.*

object  Utils {

    fun editTextSelectAll(editText: EditText) {
        if (editText.hasFocus()) {
            editText.postDelayed({
                editText.selectAll()
            }, 200)
        }
    }


    fun showSnackBar(view: View, message: String, anchorView: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(anchorView)
            .show()
    }


    fun convertEpochTimeToDate(currentTimeMillis: Long, locale: Locale): String? {
        return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(currentTimeMillis)
    }

}