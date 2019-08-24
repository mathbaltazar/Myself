package com.baltazarstudio.regular.util

import android.annotation.SuppressLint
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        private const val STRING_SEPARATOR = "|"
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

        fun unformatCurrency(valor: String): String {
            return valor.replace("R$", "")
                    .replace(",", ".")
                    .trim()
        }

        fun parseList(stringNotas: String?): ArrayList<String> {
            var notas = stringNotas
            val stringList = arrayListOf<String>()
            if (notas != null) {
                while (notas != "") {
                    val separatorIndex = notas!!.indexOf(STRING_SEPARATOR, 0, true)
                    if (separatorIndex > -1) { // SE EXISTE UM SEPARATOR NA STRING
                        stringList.add(notas.substring(0, separatorIndex))
                        notas = notas.substringAfter(STRING_SEPARATOR, notas)
                    } else if (notas.length > 0) {
                        stringList.add(notas)
                        notas = ""
                    }
                }
            }
            return stringList
        }

        fun stringify(notas: ArrayList<String>): String {
            val stringBuilder = StringBuilder()
            notas.forEach {
                if (notas.indexOf(it) != 0) {
                    stringBuilder.append(STRING_SEPARATOR)
                }
                stringBuilder.append(it)
            }
            return stringBuilder.toString()
        }
    }
}