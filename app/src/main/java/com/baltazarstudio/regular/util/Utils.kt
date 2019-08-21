package com.baltazarstudio.regular.util

import android.annotation.SuppressLint
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        private val mLocale = Locale("pt", "BR")

        @SuppressLint("SimpleDateFormat")
        private val sdf = SimpleDateFormat("dd/MM/yyyy")


        @SuppressLint("SimpleDateFormat")
        fun currentDateFormatted(): String {
            return sdf.format(Calendar.getInstance().time)
        }

        fun formatCurrency(valor: BigDecimal?): String {
            return NumberFormat.getCurrencyInstance(mLocale)
                .format(valor)
                .replace("R$", "R$ ")
        }
    }
}