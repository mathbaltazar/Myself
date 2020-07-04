package com.baltazarstudio.regular.util

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class DateMask(private var edittext: TextInputEditText) : TextWatcher {
    private var old = ""

    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {    }
    override fun onTextChanged(c: CharSequence?, start: Int, before: Int, count: Int) {
        val str = unmask(c.toString())
        var mascara = str

        if (count != 0) {
            if (validInputString(str)) {
                if (str.length > 2 && str.length <= 4) {
                    mascara = "${str.substring(0, 2)}/${str.substring(2)}"
                } else if (str.length > 4) {
                    mascara = "${str.substring(0, 2)}/${str.substring(2, 4)}/${str.substring(4)}"
                }
            } else {
                mascara = old
            }
        }

        edittext.removeTextChangedListener(this)
        edittext.setText(mascara)
        edittext.addTextChangedListener(this)
        edittext.setSelection(mascara.length)

        old = mascara
    }

    private fun unmask(mascara: String): String {
        return mascara.replace("/", "")
    }

    private fun validInputString(s: String): Boolean {
        for (char in s.toCharArray()) {
            if (!"0123456789".contains(char)) {
                return false
            }
        }
        return true
    }
}
