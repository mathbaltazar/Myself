package com.baltazarstudio.regular.util

import android.app.Dialog
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
        
        fun String.parseDate() : Date {
            return  sdf.parse(this)
        }
        
        fun Dialog.setUpDimensions(x: Float, y: Float) {
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(window?.attributes)
            
            if (x > 1 || y > 1)
                throw IllegalArgumentException("Values can't be higher than 1")
    
            if (x.toInt() != WindowManager.LayoutParams.MATCH_PARENT || x.toInt() != WindowManager.LayoutParams.WRAP_CONTENT) {
                lp.width = (getScreenSize(context).y * x).toInt()
            } else {
                lp.width = x.toInt()
            }
    
            if (y.toInt() != WindowManager.LayoutParams.MATCH_PARENT || y.toInt() != WindowManager.LayoutParams.WRAP_CONTENT) {
                lp.height = (getScreenSize(context).y * y).toInt()
            } else {
                lp.height = y.toInt()
            }
            
            window?.attributes = lp
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