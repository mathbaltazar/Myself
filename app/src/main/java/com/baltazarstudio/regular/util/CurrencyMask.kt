package com.baltazarstudio.regular.util

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import java.math.BigDecimal

class CurrencyMask(private var editText: EditText) : TextWatcher {
    private var formatado: Boolean = false
    private var ultimoFormatado = ""

    init {
        if (editText.inputType != InputType.TYPE_CLASS_NUMBER) {
            throw NumberFormatException("The EditText input type must be NUMBER")
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        formatado = false
        editText.setSelection(editText.length())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        var campo = text.toString()

        if (!formatado) {
            if (editText.selectionStart == campo.length) {
                if (count == 1 && start == 0) { // Campo vazio, primeiro caractere
                    campo = Utils.formatCurrency(BigDecimal("0.0$campo"))
                } else {
                    campo = Utils.unformatCurrency(campo)
                    campo = campo.replace(".", "")
                    campo = StringBuilder(campo).insert(campo.lastIndex - 1, ".").toString()
                    campo = Utils.formatCurrency(campo.toBigDecimal())
                }

                ultimoFormatado = campo
            }

            formatado = true
            editText.text = Editable.Factory.getInstance().newEditable(ultimoFormatado)
        }
    }
}