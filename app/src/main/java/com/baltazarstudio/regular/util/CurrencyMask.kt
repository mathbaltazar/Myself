package com.baltazarstudio.regular.util

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText

class CurrencyMask(private var editText: EditText) : TextWatcher {
    
    init {
        if (editText.inputType != InputType.TYPE_CLASS_NUMBER) {
            throw NumberFormatException("The EditText input type must be NUMBER")
        }
    }
    
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        var campo = text.toString()
        
        try {
            
            if (count == 1 && start == 0) { // Campo vazio, primeiro caractere
                campo = Utils.formatCurrency("0.0$campo".toDouble())
            } else {
                campo = Utils.unformatCurrency(campo)
                campo = campo.replace(".", "")
                campo = StringBuilder(campo).insert(campo.lastIndex - 1, ".").toString()
                campo = Utils.formatCurrency(campo.toDouble())
            }
            
        } catch (ex: Exception) {
            campo = Utils.formatCurrency(0.0)
        }
        
        editText.removeTextChangedListener(this)
        editText.setText(campo)
        editText.addTextChangedListener(this)
        
        editText.setSelection(campo.length)
    }
}