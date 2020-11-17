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
    
        fun getUTCCalendar(): Calendar {
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
            
            val ano = dateToValidate.substring(dateToValidate.length - 4)
            if (ano.toInt() < 1970) {
                return false
            }
    
            //val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            sdf.isLenient = false

            try {
                val date = sdf.parse(dateToValidate)

                if (date!!.after(Date())) {
                    return false
                }

            } catch (e: ParseException) {
                e.printStackTrace()
                return false
            }

            return true
        }
    
        fun getMesString(mes: Int, ano: Int): String {
            val mesExtense = when (mes) {
                1 -> "JANEIRO"
                2 -> "FEVEREIRO"
                3 -> "MARÇO"
                4 -> "ABRIL"
                5 -> "MAIO"
                6 -> "JUNHO"
                7 -> "JULHO"
                8 -> "AGOSTO"
                9 -> "SETEMBRO"
                10 -> "OUTUBRO"
                11 -> "NOVEMBRO"
                12 -> "DEZEMBRO"
                else -> ""
            }
            
            return "$mesExtense/$ano"
        }
        
        fun getScreenSize(context: Context): Point {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val size = Point()
            wm.defaultDisplay.getSize(size)
            return size
        }
    }
}