package com.baltazarstudio.regular.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import com.google.android.material.textfield.TextInputEditText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateInputText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {
    
    
    private var focusChangeListener: OnFocusChangeListener? = null
    private var clickListener: OnClickListener? = null
    
    private val mask = DateMask(this)
    
    init {
        addTextChangedListener(mask)
        setTextIsSelectable(false)
        isClickable = true
        
        super.setOnFocusChangeListener { view, hasFocus ->
            post {
                if (hasFocus) {
                    adaptSelection()
                }
            }
            
            focusChangeListener?.onFocusChange(view, hasFocus)
        }
        super.setOnClickListener {
            adaptSelection()
            
            clickListener?.onClick(it)
        }
    }
    
    private fun adaptSelection() {
        when {
            selectionStart <= 2 -> setSelection(0, 2)
            selectionStart <= 5 -> setSelection(3, 5)
            else -> setSelection(6, length())
        }
    }
    
    override fun setOnClickListener(l: OnClickListener?) {
        this.clickListener = l
    }
    
    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        this.focusChangeListener = l
    }
    
    //fun setDate(date: Date) {}
    
    fun setDate(calendar: Calendar) {
        removeTextChangedListener(mask)
        setText(calendar.formattedDate())
        mask.sync()
        addTextChangedListener(mask)
    }
    
    fun setDate(time: Long) {
        removeTextChangedListener(mask)
        setText(time.formattedDate())
        mask.sync()
        addTextChangedListener(mask)
    }
    
    fun getTime(): Long {
        var time = 0L
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", textLocale)
            time = sdf.parse(text.toString())!!.time
        } catch (e: ParseException) {
            return time
        }
        
        return time
    }
    
    private class DateMask(private var editText: TextInputEditText) : TextWatcher {
        private var old = ""
        
        private var isDaySelection = false
        private var isMonthSelection = false
        private var isYearSelection = false
        
        private var day = "00"
        private var month = "00"
        private var year = "0000"
        
        init {
            sync()
        }
        
        fun sync() {
            if (editText.length() == 10) {
                day = editText.text.toString().substring(0, 2)
                month = editText.text.toString().substring(3, 5)
                year = editText.text.toString().substring(6)
            }
        }
        
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isDaySelection = editText.selectionStart <= 2
            isMonthSelection = editText.selectionStart in 3..5
            isYearSelection = editText.selectionStart > 5
            
            old = s.toString()
        }
        
        override fun onTextChanged(c: CharSequence?, start: Int, before: Int, count: Int) {
            val str = unmask(c.toString())
            //Log.i("Selection ON CHANGED", str)
            
            if (isDaySelection) {
                val input = str.substring(0, 1)
                
                if (!validInputString(input)) {
                    aplicarMascara(old)
                    return
                }
                
                if (day.length < 2) {
                    day += input
                } else {
                    day = input
                }
            }
            
            if (isMonthSelection) {
                val input = str.substring(2, 3)
                
                if (!validInputString(input)) {
                    aplicarMascara(old)
                    return
                }
                
                if (month.length < 2) {
                    month += input
                } else {
                    month = input
                }
            }
            
            if (isYearSelection) {
                val input = str.substring(str.length - 1)
                
                if (!validInputString(input)) {
                    aplicarMascara(old)
                    return
                }
                
                if (year.length == 4) {
                    year = input
                } else {
                    year += input
                }
            }
            
            var mascara = ""
            if (day.length == 1) mascara += "0"
            mascara += day
            
            mascara += "/"
            
            if (month.length == 1) mascara += "0"
            mascara += month
            
            mascara += "/"
            
            repeat(4 - year.length) {
                mascara += "0"
            }
            mascara += year
            
            
            aplicarMascara(mascara)
            
            
            when {
                isDaySelection -> editText.setSelection(0, 2)
                isMonthSelection -> editText.setSelection(3, 5)
                isYearSelection -> editText.setSelection(6, editText.length())
            }
        }
        
        private fun aplicarMascara(mascara: String) {
            editText.removeTextChangedListener(this)
            editText.setText(mascara)
            editText.addTextChangedListener(this)
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
    
}