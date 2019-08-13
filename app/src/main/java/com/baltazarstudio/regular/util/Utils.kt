package com.baltazarstudio.regular.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        private val mLocale = Locale("pt", "BR")

        @SuppressLint("SimpleDateFormat")
        private val sdf = SimpleDateFormat("dd/MM/yyyy")


        fun hideKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        @SuppressLint("SimpleDateFormat")
        fun currentDateFormatted(): String {
            return sdf.format(Date())
        }

        fun formatCurrency(valor: BigDecimal?): String {
            return NumberFormat.getCurrencyInstance(mLocale)
                    .format(valor)
        }
    }
}