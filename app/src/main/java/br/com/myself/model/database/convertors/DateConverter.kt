package br.com.myself.model.database.converter

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    
    @TypeConverter
    fun dateToLong(date: Date): Long = date.time
    
    @TypeConverter
    fun longToDate(timeInMillis: Long): Date = Date(timeInMillis)
    
}