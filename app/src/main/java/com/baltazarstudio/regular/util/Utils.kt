package com.baltazarstudio.regular.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        private val mLocale = Locale("pt", "BR")
        private val sdf = SimpleDateFormat("dd/MM/yyyy", mLocale)

        fun Calendar.formattedDate(): String {
            return sdf.format(this.time)
        }

        fun Long.formattedDate(): String {
            return sdf.format(Date(this))
        }

        fun UTCInstanceCalendar(): Calendar {
            return Calendar.getInstance(TimeZone.getTimeZone("UTC"))
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

        fun isDataValida(dateToValidate: String?): Boolean {

            if (dateToValidate.isNullOrBlank() || dateToValidate.length < 10) {
                return false
            }

            //val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            sdf.isLenient = false

            try {
                val date = sdf.parse(dateToValidate)

                if (date.after(Date())) {
                    return false
                }

            } catch (e: ParseException) {
                e.printStackTrace()
                return false
            }

            return true
        }

        fun getScreenSize(context: Context): Point {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            wm.defaultDisplay.getSize(size)
            return size
        }
    }
}