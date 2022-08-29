package br.com.myself.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

const val DEFAULT_REQUEST_LOAD_SIZE = 5

class Utils {
    companion object {
    
        @SuppressLint("ConstantLocale")
        private val mLocale = Locale.getDefault()
        private const val mPattern = "dd/MM/yyyy"
        
        private val sdf = SimpleDateFormat(mPattern, mLocale)
        
        val MESES_STRING = arrayOf(
            "JANEIRO",
            "FEVEREIRO",
            "MARÇO",
            "ABRIL",
            "MAIO",
            "JUNHO",
            "JULHO",
            "AGOSTO",
            "SETEMBRO",
            "OUTUBRO",
            "NOVEMBRO",
            "DEZEMBRO"
        )

        fun Calendar.formattedDate(pattern: String = mPattern): String {
            sdf.applyPattern(pattern)
            return sdf.format(this.time).also { sdf.applyPattern(mPattern) }
        }
        
        fun getCalendar(): Calendar {
            return GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), mLocale)
        }

        fun formatCurrency(valor: Double?): String {
            return NumberFormat.getCurrencyInstance(mLocale)
                .format(valor ?: 0)
                .replace("R$", "R$ ")
                .trim()
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
            
            /* TODO AVALIAR MELHOR POSSIBILIDADE
            
            val x = wm.currentWindowMetrics.bounds.width()
            val y = wm.currentWindowMetrics.bounds.height()
    
            Log.i("SCREEN SIZE INFORMATION","Utils.getScreenSize() - currentWindowMetrics | X: $x - Y: $y")*/
            
            return size
        }
    
        /**
         * @param widthPercent Width percentage accordingly with device screen.
         * If not set, WindowManager.LayoutParams.MATCH_PARENT as default.
         * @param height Exact size of the height. Set WindowManager.LayoutParams.WRAP_CONTENT as default.
         */
        fun Dialog.setUpDimensions(
            widthPercent: Int = WindowManager.LayoutParams.MATCH_PARENT,
            height: Int = WindowManager.LayoutParams.WRAP_CONTENT
        ) {
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(window?.attributes)
    
            lp.width = if (widthPercent < 0) widthPercent
            else (getScreenSize(context).x * widthPercent / 100)
            
            lp.height = height
    
            window?.attributes = lp
        }
    
    }
}