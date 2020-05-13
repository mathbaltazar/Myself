package com.baltazarstudio.regular.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        private val mLocale = Locale("pt", "BR")
        private val sdf = SimpleDateFormat("dd/MM/yyyy", mLocale)

        fun Calendar.formattedDate(): String {
            return sdf.format(this.time)
        }

        fun formatCurrency(valor: Double?): String {
            return NumberFormat.getCurrencyInstance(mLocale)
                .format(valor)
                .replace("R$", "R$ ")
        }

        fun unformatCurrency(valor: String): String {
            return valor.replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
        }

        fun getScreenSize(context: Context): Point {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            wm.defaultDisplay.getSize(size)
            return size
        }
    }
}