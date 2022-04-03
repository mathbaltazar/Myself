package br.com.myself.model.database.convertors

import androidx.room.TypeConverter
import br.com.myself.util.Utils
import java.util.*
import java.util.Calendar.*

class DateConverter {
    
    @TypeConverter
    fun dateToString(date: Calendar): String {
        return if (date[MONTH] < 10) "${date[YEAR]}-0${date[MONTH]}-${date[DAY_OF_MONTH]}"
        else "${date[YEAR]}-${date[MONTH]}-${date[DAY_OF_MONTH]}"
    }
    
    @TypeConverter
    fun stringToDate(date: String): Calendar {
        return Utils.getCalendar().apply {
            set(date.substring(0, 4).toInt(), // ANO
                date.substring(5, 7).toInt(), // MES (0-based)
                date.substring(8).toInt()) // DIA
        }
    }
    
}